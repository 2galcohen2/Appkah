package com.example.myapplication;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "MainMenu";

    PopupWindow mPopupWindow;
    DatabaseReference mDatabase;
    Defect[] defectsArr;
    GridView gvBoard;
    TextView tvHelloMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncCaller service = new AsyncCaller();
        service.execute();
    }

    //Create new report button clicked
    public void onCreateReport(View v) {
        Intent ReportActivityIntent = new Intent(this, CreateReport.class);
        startActivity(ReportActivityIntent);
    }

    //Create report hazard button
    public void onReportHazard(View v) {
        //Get the parent main relative view
        RelativeLayout rlMain = (RelativeLayout) findViewById(R.id.main_rel_view);

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.report_popup, null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView, 1000, 1000
//                LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT
        );

        //WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        //lp.dimAmount = 0.1f;
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        gvBoard = (GridView) customView.findViewById(R.id.gvBoard);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        BuildDefectBoard();
        mPopupWindow.showAtLocation(rlMain, Gravity.CENTER, 0, 0);
    }

    //Show all reports on map
    public void onShowReports(View v) {

        Intent MapsActivityIntent = new Intent(this, MapsActivity.class);
        startActivity(MapsActivityIntent);
    }

    //Call tow truck
    public void onCallTowTruck(View v) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + Constants.TOW_TRUCK_PHONE));

            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Call phone permission granted");
                startActivity(callIntent);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE,
                                Manifest.permission.CALL_PHONE},
                        Constants.PERMISSION_REQUEST_CODE);
            }
        } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
        }
    }

    //Get permissions for location
    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSION_REQUEST_CODE);
        }
    }

    private void enableStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.PERMISSION_REQUEST_CODE);
        }
    }

    //Build the defect type choose screen - there is a perminent number of defect types in the DB
    public void BuildDefectBoard() {
        defectsArr = new Defect[Constants.DEFECTS_SIZE_ARR];
        loadDefectTypes();
    }

    private void loadDefectTypes() {
        // database handler
        mDatabase = FirebaseDatabase.getInstance().getReference("defects");

        //Called only once - to initialize spinner data
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataChangeDefect(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void onDataChangeDefect(DataSnapshot dataSnapshot) {
        int index = 0;

        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
            int defType = areaSnapshot.child("defectType").getValue(int.class);
            int imageRes = this.getResources().getIdentifier("def" + String.valueOf(defType), "drawable", this.getPackageName());

            //Build the cards col manualy - later by DB
            defectsArr[index++] = new Defect(defType, imageRes, this);
        }

        final DefectAdapter defectAdapter = new DefectAdapter(this, defectsArr);
        gvBoard.setAdapter(defectAdapter);

        gvBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Defect chosenDefect = defectsArr[position];
                Intent HazardActivityIntent = new Intent(getApplicationContext(), HazardActivity.class);
                HazardActivityIntent.putExtra("chosenDefect", chosenDefect);
                startActivity(HazardActivityIntent);
            }
        });
    }

    private void loginCurrentUser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Read phone state permission is granted");
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();

            //Select user from DB
            if (mPhoneNumber != "") {
                selectUserFromDB(mPhoneNumber);
            } else {
                Log.v(TAG, "No user phone number");
            }

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_PHONE_STATE},
                    Constants.PERMISSION_REQUEST_CODE);
            loginCurrentUser();
        }
    }

    public void selectUserFromDB(String mPhoneNumber) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.orderByChild("phone").equalTo(mPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User curr_user = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    curr_user = snapshot.getValue(User.class);
                }

                if (curr_user == null) {
                    curr_user = new User("0504443550", 313260278, "משתמש מזוייף");
                }

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(curr_user);
                editor.putString("currUser", json);
                editor.commit();
                tvHelloMsg.setText("היי " + User.getUserName(getApplicationContext()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    public class AsyncCaller extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            enableMyLocationIfPermitted();
            enableStoragePermission();
            loginCurrentUser();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(1200);
                setContentView(R.layout.activity_main_menu);
                tvHelloMsg = (TextView) findViewById(R.id.tvHello);
                tvHelloMsg.setText("היי " + User.getUserName(getApplicationContext()));

                //Set title
                TextView txtTitle = (TextView) findViewById(R.id.tvPageTitle);
                txtTitle.setText("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}