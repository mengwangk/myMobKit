package com.mymobkit.camera;

import java.util.Comparator;

/**
 * Camera size.
 */
public final class CameraSize implements Comparable<CameraSize> {
    public int width;
    public int height;

    public CameraSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int compareTo(CameraSize cameraSize) {
        if (getWidth() > cameraSize.getWidth())
            return 1;
        else if (getWidth() < cameraSize.getWidth())
            return -1;
        else
            return 0;
    }
}
