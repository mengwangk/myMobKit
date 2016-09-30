package com.mymobkit.model;

import java.io.Serializable;

/**
 * Created by MEKOH on 2/19/2016.
 */
public final class MmsAttachment implements Serializable{

    private byte[] data;
    private String contentType;

    public MmsAttachment(String contentType, byte[] data) {
        this.data = data;
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }
}
