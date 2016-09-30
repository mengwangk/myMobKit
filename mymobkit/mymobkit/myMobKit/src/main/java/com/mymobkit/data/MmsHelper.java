package com.mymobkit.data;

import android.content.ContentValues;
import android.content.Context;

import com.mymobkit.common.ValidationUtils;
import com.mymobkit.model.Mms;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * MMS database helper class
 */
public class MmsHelper {

    private static final String TAG = makeLogTag(MmsHelper.class);

    private static MmsHelper mmsHelper = null;

    /**
     * This constructor ensures that the database is setup correctly
     *
     * @param context
     */
    private MmsHelper(Context context) {
        new MmsTable(context);
    }

    public static MmsHelper getMmsHelper(Context ctx) {
        if (mmsHelper == null) {
            mmsHelper = new MmsHelper(ctx);
        }
        return mmsHelper;
    }

    public boolean addMms(Mms mms) {
        String id = mms.getId();
        ContentValues values = new ContentValues();
        values.put("mmsID", id);
        values.put("_to", mms.getTo());
        values.put("cc", ValidationUtils.getString(mms.getCc()));
        values.put("bcc", ValidationUtils.getString(mms.getBcc()));
        values.put("subject", ValidationUtils.getString(mms.getSubject()));
        values.put("body", ValidationUtils.getString(mms.getBody()));
        values.put("delivery", mms.isDeliveryReport() ? 1 : 0);
        values.put("read", mms.isReadReport() ? 1 : 0);
        values.put("delivered", mms.isDelivered() ? 1 : 0);
        values.put("isRead", mms.isRead() ? 1 : 0);
        values.put("date", mms.getDate().getTime());
        return addOrUpdate(values, id);
    }

    public boolean deleteMms(String id) {
        if (MmsTable.containsMms(id)) {
            return MmsTable.deleteMms(id);
        } else {
            return false;
        }
    }

    public Mms getMms(String id) {
        return MmsTable.getMms(id);
    }

    public void deleteOldMms(final int days) {
        MmsTable.deleteOldMms(days);
    }

    public void deleteOldMmsByNumber(final int toKeep) {
        MmsTable.deleteOldMmsByNumber(toKeep);
    }

    private boolean addOrUpdate(ContentValues values, String id) {
        if (MmsTable.containsMms(id)) {
            return MmsTable.updateMms(values, id);
        } else {
            return MmsTable.addMms(values);
        }
    }
}
