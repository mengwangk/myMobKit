package com.mymobkit.model;

/**
 * Created by MEKOH on 2/14/2016.
 */
public class SimInfo {

    private int id;
    private String displayName;
    private String iccId;
    private int slot;

    public SimInfo(int id, String displayName, String iccId, int slot) {
        this.id = id;
        this.displayName = displayName;
        this.iccId = iccId;
        this.slot = slot;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIccId() {
        return iccId;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return "SimInfo{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", iccId='" + iccId + '\'' +
                ", slot=" + slot +
                '}';
    }
}