package com.example.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;

/**
 * Static class for location services
 */

public class MyLocation {

    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5;
    private static final String TAG = "MyLocation";
    public static LocationManager mLocationManager;

    //Get the user current location according to internet or gps
    public static LatLng getCurrentLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        Location location = null;
        LatLng latLng = null;

        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            }
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled&&location==null) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        latLng = new LatLng(location.getLatitude(), location.getLongitude() );
        return latLng;
    }

    //Location listener impl - update location
    private static final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mLocationManager.removeUpdates(mLocationListener);
            } else {
                Log.d(TAG, "location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    public static String getAddress(LatLng repLocation, Context context) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    repLocation.getLatitude(),
                    repLocation.getLongitude(),
                    1);
        } catch (IOException ioException) {
            Log.e(TAG, "error", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e(TAG, "error" + ". " +
                    "Latitude = " + repLocation.getLatitude() +
                    ", Longitude = " +
                    repLocation.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            Log.e(TAG, "No address found");
            return "Not found";
        } else {
            Address address = addresses.get(0);
            return address.getThoroughfare() + " " + address.getFeatureName() + ",  " + address.getLocality();
        }
    }

}
