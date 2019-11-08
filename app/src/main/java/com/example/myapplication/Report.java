package com.example.myapplication;

import android.content.Context;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Report class, includes all the report details
 */

public class Report implements Serializable {
    private static final String TAG = "Report";

    private String key;
    private int reporter_id;
    private String repType;
    private String time;
    private String date;
    private LatLng location;
    private String carNum;
    private String carType;
    private int fineAmount;
    private String carColor;
    private boolean bCanBeCanceld;

    public Report(Context mContext, int reporter_id) {
        this.reporter_id = reporter_id;

        //Get current time
        setCurrentTimeAndDate();
        this.carNum = "";
        this.fineAmount = 0;
        this.bCanBeCanceld = true;
        this.carColor = Constants.OTHER;
        this.carType = Constants.OTHER;
        this.repType = Constants.OTHER;

        //Create key to connect between report to it's photos
        this.key = UUID.randomUUID().toString();
    }

    public Report(){
    }


    public String getKey(){
        return this.key;
    }

    private void setCurrentTimeAndDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.time = sdf.format(new Date());
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        this.date = sdf.format(new Date());
    }

    public LatLng getLocation(){
        return this.location;
    }

    public String getDate(){
        return  this.date;
    }

    public String getTime(){
        return  this.time;
    }

    public String getCarColor(){
        return this.carColor;
    }

    public String getCarType(){
        return this.carType;
    }

    public String getReportType(){
        return this.repType;
    }

    public void setCarType(String carType){
        this.carType = carType;
    }

    public void setCarColor(String color){
        this.carColor = color;
    }

    public void setLocation(LatLng location){
        this.location = location;
    }

    public void setFineAmount(int fine){
        this.fineAmount = fine;
    }

    public void setReportType(String rep_type){
        this.repType = rep_type;
    }

    public String getCarNum(){
        return this.carNum;
    }

    public int getFineAmount(){
        return this.fineAmount;
    }

    public int getReporterID(){
        return this.reporter_id;
    }

    public boolean checkCanBeCanceld(){
        return  this.bCanBeCanceld;
    }

    public void writeNewReport() {
        //Get reports count to add id for new rep
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("reports");
        mDatabase.child(this.key).setValue(this);
    }

    public void disableCanceled(){
        this.bCanBeCanceld = false;
    }

    public void setCarNum(String carNum){
        this.carNum = carNum;
    }
}
