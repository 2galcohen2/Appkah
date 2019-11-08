package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private LatLng currLocation;
    private Report chosenRep;

    private Intent DisplayReportIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        currLocation = MyLocation.getCurrentLocation(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Check map service works fine
        if (isServicesOK()) {
            mapFragment.getMapAsync(this);
        }

        DisplayReportIntent = new Intent(this, DisplayReport.class);

        //Set title
        TextView txtTitle = (TextView) findViewById(R.id.tvPageTitle);
        txtTitle.setText(Constants.SHOW_REP_MAP);
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, 9001);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        markLocation(currLocation);
    }

    //Mark current location when location button clicked
    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    markLocation(currLocation);
                    return false;
                }
            };

    //Mark location and add it'd details to the marker
    public void markLocation(LatLng location) {
        mMap.setMinZoomPreference(15);
        getAllReportsLocationForToday();

        if (location != null) {
            com.google.android.gms.maps.model.LatLng mapsLatLng =
                    new com.google.android.gms.maps.model.LatLng(location.getLatitude(),
                            location.getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(mapsLatLng)
                    .icon(getMarkerIcon("#ff2299"))
                    .title("מיקום נוכחי"));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapsLatLng, 12.0f));
        } else {
            Log.d(TAG, "markLocation: location is nul");
        }
    }

    // Create marker color
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void getReportByKey(String key){
        // database handler
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("reports");

        //Called only once - to initialize spinner data
        mDatabase.orderByChild("key").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //There is gonna be only one match to a specific key - display the report
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    chosenRep = areaSnapshot.getValue(Report.class);
                    double longitude = areaSnapshot.child("location").child("longitude").getValue(Double.class);
                    double latitude = areaSnapshot.child("location").child("latitude").getValue(Double.class);
                    chosenRep.setLocation(new LatLng(latitude, longitude));
                }

                if (chosenRep != null){
                    //Display the clicked report
                    DisplayReportIntent.putExtra("chosenRep", chosenRep);
                    startActivity(DisplayReportIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAllReportsLocationForToday(){
        // database handler
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("reports");

        //Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currDate = sdf.format(new Date());

        //Called only once - to initialize spinner data
        mDatabase.orderByChild("date").equalTo(currDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String reportKey = areaSnapshot.child("key").getValue(String.class);
                    //Long reporterID = areaSnapshot.child("reporterID").getValue(String.class);
                    String reporterID = String.valueOf(areaSnapshot.child("reporterID").getValue(Long.class));
                    String reportType = areaSnapshot.child("reportType").getValue(String.class);
                    double longitude = areaSnapshot.child("location").child("longitude").getValue(Double.class);
                    double latitude = areaSnapshot.child("location").child("latitude").getValue(Double.class);

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new com.google.android.gms.maps.model.LatLng(latitude,
                                    longitude))
                            .snippet(reporterID)
                            .title(reportType));
                    marker.setTag(reportKey);


                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            String key = (String)marker.getTag();
                            getReportByKey(key);
                            return true;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
