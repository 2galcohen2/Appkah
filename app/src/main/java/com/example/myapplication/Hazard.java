package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Hazard class
 */

public class Hazard {
    private static final String TAG = "Hazard";
    private static final String DIR = "Hazards";

    private String key;
    private int reporter_id;
    private String time;
    private String date;
    private LatLng location;
    private int repType;
    private String description;
    private int photoCounter;

    public Hazard(Context context, int reporter_id, int repType){
        this.reporter_id = reporter_id;
        this.location = MyLocation.getCurrentLocation(context);
        this.repType = repType;
        setCurrentTimeAndDate();
        photoCounter = 0;

        //Create hazard key
        this.key = UUID.randomUUID().toString();
    }

    private void setCurrentTimeAndDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.time = sdf.format(new Date());
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        this.date = sdf.format(new Date());
    }

    public static String returnDir(){
        return DIR;
    }

    public String getKey(){
        return key;
    }

    public int getReporterID(){
        return this.reporter_id;
    }

    public String getDate(){
        return this.date;
    }

    public String getDescription(){
        return this.description;
    }

    public String getTime(){
        return this.time;
    }

    public int checkPhotoCounter(){
        return this.photoCounter;
    }

    public LatLng getLocation(){
        return this.location;
    }

    public boolean addPicture(Activity activity){
        if (photoCounter < Constants.MAX_HAZARD_PHOTOS){
            photoCounter++;
            CameraHandler.dispatchTakePictureIntent(activity);
            return true;
        }else{
            return false;
        }
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void writeHazard() {
        //Get reports count to add id for new rep
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("hazardReports");
        mDatabase.child(this.key).setValue(this);
    }
}
