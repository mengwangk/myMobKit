package com.mymobkit.mms.job;

import android.content.Context;
import android.text.TextUtils;

import com.mymobkit.common.SmilUtils;
import com.mymobkit.common.TelephonyUtils;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.data.MmsHelper;
import com.mymobkit.job.ContextJob;
import com.mymobkit.mms.CompatMmsConnection;
import com.mymobkit.mms.MmsSendResult;
import com.mymobkit.mms.transport.UndeliverableMessageException;
import com.mymobkit.mms.utils.Utils;
import com.mymobkit.model.Mms;
import com.mymobkit.model.MmsAttachment;

import org.whispersystems.jobqueue.JobParameters;
import org.whispersystems.jobqueue.requirements.NetworkRequirement;

import java.io.IOException;
import java.util.Arrays;

import ws.com.google.android.mms.ContentType;
import ws.com.google.android.mms.InvalidHeaderValueException;
import ws.com.google.android.mms.pdu.CharacterSets;
import ws.com.google.android.mms.pdu.EncodedStringValue;
import ws.com.google.android.mms.pdu.PduBody;
import ws.com.google.android.mms.pdu.PduComposer;
import ws.com.google.android.mms.pdu.PduHeaders;
import ws.com.google.android.mms.pdu.PduPart;
import ws.com.google.android.mms.pdu.SendConf;
import ws.com.google.android.mms.pdu.SendReq;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Job to send MMS.
 */
public class MmsSendJob extends ContextJob {

    private static final long serialVersionUID = 0L;

    private static final String TAG = makeLogTag(MmsSendJob.class);

    private final Mms message;

    public MmsSendJob(Context context, Mms message) {
        super(context, JobParameters.newBuilder()
                .withGroupId("mms-operation")
                .withRequirement(new NetworkRequirement(context))
                        //.withPersistence()
                .create());
        this.message = message;
    }

    @Override
    public void onAdded() {
        updateMmsToDatabase();
    }

    @Override
    public void onRun() throws Exception {
        send();
    }

    @Override
    public boolean onShouldRetry(Exception exception) {
        LOGE(TAG, "[onShouldRetry] Error sending MMS", exception);
        return false;
    }

    @Override
    public void onCanceled() {
        LOGE(TAG, "[onCanceled] Job is canceled");

        // Mark as failure
        message.setIsDelivered(false);
        updateMmsToDatabase();
    }

    public void send() throws IOException, UndeliverableMessageException, InvalidHeaderValueException {
        final SendReq pdu = constructSendPdu(message);
        validateDestinations(message, pdu);
        final byte[] pduBytes = getPduBytes(pdu);
        // TODO: for now default the subscription id to -1 to use the default subscription
        final SendConf sendConf = new CompatMmsConnection(context).send(pduBytes, -1);
        final MmsSendResult result = getSendResult(sendConf, pdu);

        // Mark as sent
        message.setIsDelivered(true);
        updateMmsToDatabase();
    }


    private byte[] getPduBytes(SendReq message) throws IOException, UndeliverableMessageException {
        final String number = TelephonyUtils.getManager(context).getLine1Number();

        message.setBody(SmilUtils.getSmilBody(message.getBody()));

        if (!TextUtils.isEmpty(number)) {
            message.setFrom(new EncodedStringValue(number));
        }

        byte[] pduBytes = new PduComposer(context, message).make();

        if (pduBytes == null) {
            throw new UndeliverableMessageException("PDU composition failed, null payload");
        }

        return pduBytes;
    }

    private MmsSendResult getSendResult(SendConf conf, SendReq message) throws UndeliverableMessageException {
        if (conf == null) {
            throw new UndeliverableMessageException("No M-Send.conf received in response to send.");
        } else if (conf.getResponseStatus() != PduHeaders.RESPONSE_STATUS_OK) {
            throw new UndeliverableMessageException("Got bad response: " + conf.getResponseStatus());
        } else if (isInconsistentResponse(message, conf)) {
            throw new UndeliverableMessageException("Mismatched response!");
        } else {
            return new MmsSendResult(conf.getMessageId(), conf.getResponseStatus(), false, false);
        }
    }

    private boolean isInconsistentResponse(SendReq message, SendConf response) {
        return !Arrays.equals(message.getTransactionId(), response.getTransactionId());
    }

    private void validateDestinations(EncodedStringValue[] destinations) throws UndeliverableMessageException {
        if (destinations == null) return;

        for (EncodedStringValue destination : destinations) {
            if (destination == null || !ValidationUtils.isValidSmsOrEmail(destination.getString())) {
                throw new UndeliverableMessageException("Invalid destination: " + (destination == null ? null : destination.getString()));
            }
        }
    }

    private void validateDestinations(Mms media, SendReq message) throws UndeliverableMessageException {
        validateDestinations(message.getTo());
        validateDestinations(message.getCc());
        validateDestinations(message.getBcc());

        if (message.getTo() == null && message.getCc() == null && message.getBcc() == null) {
            throw new UndeliverableMessageException("No to, cc, or bcc specified!");
        }
    }

    private SendReq constructSendPdu(final Mms message) throws InvalidHeaderValueException {
        final SendReq sendReq = new SendReq();
        PduBody body = new PduBody();
        if (message.getDistributionType() == Mms.DistributionTypes.CONVERSATION) {
            sendReq.addTo(new EncodedStringValue(Utils.toIsoBytes(message.getTo())));
        } else {
            sendReq.addBcc(new EncodedStringValue(Utils.toIsoBytes(message.getTo())));
        }

        if (!TextUtils.isEmpty(message.getCc())) {
            sendReq.addCc(new EncodedStringValue(Utils.toIsoBytes(message.getCc())));
        }

        if (!TextUtils.isEmpty(message.getBcc())) {
            sendReq.addCc(new EncodedStringValue(Utils.toIsoBytes(message.getBcc())));
        }

        if (!TextUtils.isEmpty(message.getSubject())) {
            sendReq.setSubject(new EncodedStringValue(Utils.toUtf8Bytes(message.getSubject())));
        }

        if (message.isDeliveryReport()) {
            sendReq.setDeliveryReport(PduHeaders.VALUE_YES);
        } else {
            sendReq.setDeliveryReport(PduHeaders.VALUE_NO);
        }

        if (message.isReadReport()) {
            sendReq.setReadReport(PduHeaders.VALUE_YES);
        } else {
            sendReq.setReadReport(PduHeaders.VALUE_NO);
        }

        sendReq.setDate(message.getDate().getTime() / 1000L);

        if (!TextUtils.isEmpty(message.getBody())) {
            PduPart part = new PduPart();
            part.setData(Utils.toUtf8Bytes(message.getBody()));
            part.setCharset(CharacterSets.UTF_8);
            part.setContentType(ContentType.TEXT_PLAIN.getBytes());
            part.setContentId((System.currentTimeMillis() + "").getBytes());
            part.setName(("Text" + System.currentTimeMillis()).getBytes());
            body.addPart(part);
        }
        for (MmsAttachment attachment : message.getAttachments()) {
            PduPart part = new PduPart();
            part.setData(attachment.getData());
            part.setContentType(Utils.toIsoBytes(attachment.getContentType()));
            part.setContentId((System.currentTimeMillis() + "").getBytes());
            part.setName((System.currentTimeMillis() + "").getBytes());
            body.addPart(part);
        }
        sendReq.setBody(body);
        return sendReq;
    }


    private void updateMmsToDatabase(){
        try {
            LOGD(TAG, "[updateMmsToDatabase] Update this MMS to database");
            MmsHelper.getMmsHelper(context).addMms(message);
        } catch (Exception ex) {
            LOGD(TAG, "[updateMmsToDatabase] Unable to update this MMS to database", ex);
        }
    }

}
