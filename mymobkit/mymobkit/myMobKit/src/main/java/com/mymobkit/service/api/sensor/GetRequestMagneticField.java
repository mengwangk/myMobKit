package com.mymobkit.service.api.sensor;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

/**
 * Magnetic field sensor data.
 *
 */
public class GetRequestMagneticField extends GetRequest {

    @Expose
    private float x;

    @Expose
    private float y;

    @Expose
    private float z;


    public GetRequestMagneticField(){
        super();
        this.x = this.y = this.z = 0;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
