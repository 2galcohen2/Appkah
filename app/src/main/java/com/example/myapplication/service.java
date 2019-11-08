package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service
 */

public class service extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
