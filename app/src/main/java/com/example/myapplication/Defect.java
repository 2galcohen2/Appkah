package com.example.myapplication;

import android.content.Context;
import java.io.Serializable;

/**
 * Defect class for reporting
 */

public class Defect implements Serializable {
    private int defect_type;
    private transient LatLng location = null;
    private final int imageRes;

    public Defect(int def_type, int imageRes, Context context){
        this.defect_type = def_type;
        this.imageRes = imageRes;
        this.location = MyLocation.getCurrentLocation(context);
    }

    public int GetImageResource()
    {
        return (this.imageRes);
    }

    public int getDefectType(){
        return this.defect_type;
    }
}
