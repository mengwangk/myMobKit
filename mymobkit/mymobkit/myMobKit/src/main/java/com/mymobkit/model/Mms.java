package com.mymobkit.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MMS message.
 */
public final class Mms implements Serializable {

    public static class DistributionTypes {
        public static final int DEFAULT = 2;
        public static final int BROADCAST = 1;
        public static final int CONVERSATION = 2;
        public static final int ARCHIVE = 3;
    }

    @Expose
    private String to;

    @Expose
    private String cc;

    @Expose
    private String bcc;

    @Expose
    private String subject;

    @Expose
    private String body;

    private List<MmsAttachment> attachments;

    private int distributionType;

    @Expose
    private Date date;

    @Expose
    private boolean deliveryReport;

    @Expose
    private boolean readReport;

    @Expose
    private String id;

    @Expose
    private boolean isDelivered;

    @Expose
    private boolean isRead;


    public Mms(String id, String to, String subject) {
        this.id = id;
        this.to = to;
        this.subject = subject;
        this.distributionType = DistributionTypes.DEFAULT;
        this.date = new Date();
        this.attachments = new ArrayList<MmsAttachment>();

        this.deliveryReport = false;
        this.readReport = false;
        this.isDelivered = false;
        this.isRead = false;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(int distributionType) {
        this.distributionType = distributionType;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public List<MmsAttachment> getAttachments() {
        return attachments;
    }

    public void addAttachment(final MmsAttachment attachment) {
        this.attachments.add(attachment);
    }

    public Date getDate() {
        return date;
    }

    public boolean isDeliveryReport() {
        return deliveryReport;
    }

    public void setDeliveryReport(boolean deliveryReport) {
        this.deliveryReport = deliveryReport;
    }

    public boolean isReadReport() {
        return readReport;
    }

    public void setReadReport(boolean readReport) {
        this.readReport = readReport;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
