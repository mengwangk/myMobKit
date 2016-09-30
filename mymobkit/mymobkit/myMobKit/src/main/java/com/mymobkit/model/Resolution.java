package com.mymobkit.model;

import android.text.TextUtils;

import java.io.Serializable;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGE;

public final class Resolution implements Serializable {

    private static final String TAG = makeLogTag(Resolution.class);
    /**
     *
     */
    private static final long serialVersionUID = 4649401958252571368L;

    private int width;
    private int height;

    public Resolution() {

    }

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resolution that = (Resolution) o;

        if (width != that.width) return false;
        return height == that.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }

    public static Resolution parse(final String resString) {
        Resolution resolution = new Resolution(640, 480);
        try {
            if (!TextUtils.isEmpty(resString)) {
                String[] resValues = resString.split("x");
                if (resValues.length == 2) {
                    resolution.setWidth(Integer.parseInt(resValues[0]));
                    resolution.setHeight(Integer.parseInt(resValues[1]));
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[parse] Unable to get resolution", ex);
        }
        return resolution;
    }
}
