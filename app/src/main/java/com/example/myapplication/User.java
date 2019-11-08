package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

/**
 * Represent reporter in this system
 */

public class User {
    private String phone_num;
    private int id;
    private String name;

    public User(String phone_num, int id, String name) {
        this.phone_num = phone_num;
        this.id = id;
        this.name = name;
    }

    public User() {
    }

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.id;
    }

    public static String getUserName(Context mContext) {
        SharedPreferences sharedPref =
                mContext.getSharedPreferences(mContext.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = sharedPref.getString("currUser", "");
        User currUser = gson.fromJson(json, User.class);

        if (currUser != null) {
            return currUser.getName();
        } else {
            return "";
        }
    }

    public static int getUserID(Context mContext) {
        SharedPreferences sharedPref =
                mContext.getSharedPreferences(mContext.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = sharedPref.getString("currUser", "");
        User currUser = gson.fromJson(json, User.class);
        return currUser.getID();
    }

}
