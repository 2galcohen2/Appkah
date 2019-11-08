package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateReport extends AppCompatActivity {
    private static final String TAG = "CreateReport";

    static final int REQUEST_LICENSE_PLATE = 100;
    static final String DIR = "All_report_images/";
    static final String SHARED_IMG = "rep_img";

    DatabaseReference mDatabase;
    ImageView ivDisplay;
    Report newRep;

    private FirebaseAnalytics mFirebaseAnalytics;
    private static File destination;
    private String ANDROID_DATA_DIR;

    EditText etAddress;
    EditText etDate;
    EditText etTime;
    EditText etPlateNum;
    EditText etFineAmount;
    Spinner spnRepType;
    Spinner spnColor;
    Spinner spnCarType;

    Bitmap mImageBitmap;
    SharedPreferences mySharedPreferences;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        AsyncCaller service = new AsyncCaller();
        service.execute();

        ANDROID_DATA_DIR = this.getApplicationInfo().dataDir;

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Set title
        TextView txtTitle = (TextView) findViewById(R.id.tvPageTitle);
        txtTitle.setText(Constants.CREATE_REP_TITLE);
    }

    public class AsyncCaller extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            Context mContext = getApplicationContext();
            mySharedPreferences = getSharedPreferences("App_settings", MODE_PRIVATE);
            getScreenFields();

            //Check if report was created
            newRep = (Report) getIntent().getSerializableExtra("newRep");

            if (newRep == null) {
                newRep = new Report(mContext, User.getUserID(mContext));

                //Set timer for report cancel
                Timer timer = new Timer(10000, 1000);
                timer.start();
            } else {
                //Take information from intent
                etPlateNum.setText(newRep.getCarNum());

                //Get image
                String photo = mySharedPreferences.getString(SHARED_IMG, "photo");

                if (!photo.equals("photo")) {
                    mImageBitmap = CameraHandler.decodeBase64(photo);
                    ivDisplay.setImageBitmap(mImageBitmap);
                }
            }

            // Loading spinner data from database
            loadSpinnerData();
        }

        @Override
        protected void onPostExecute(String result) {
            //Set the current location as default
            newRep.setLocation(MyLocation.getCurrentLocation(getApplicationContext()));
            //Set information in fields
            setScreenFields();
        }
    }

    public void getScreenFields() {
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        etFineAmount = (EditText) findViewById(R.id.etFineAmount);
        ivDisplay = (ImageView) findViewById(R.id.imgView);
        etPlateNum = (EditText) findViewById(R.id.etPlateNum);
        spnCarType = (Spinner) findViewById(R.id.spCarType);
        spnRepType = (Spinner) findViewById(R.id.spReportType);
        spnColor = (Spinner) findViewById(R.id.spCarColor);
    }

    public void setScreenFields() {
        etAddress.setText(MyLocation.getAddress(newRep.getLocation(), this));
        etDate.setText(newRep.getDate());
        etTime.setText(newRep.getTime());
    }

    //Get spinner value by choise
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    //Recognize plate button clicked
    public void onPlateRecognition(View v) {
        takePicture();
    }

    public void onTakePhoto(View view) {
        CameraHandler.dispatchTakePictureIntent(this);
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());

        return df.format(date);
    }

    public void takePicture() {
        Intent intent = new Intent(getApplicationContext(), CreateReport.class);
        intent.putExtra("newRep", newRep);
        startActivityForResult(intent, REQUEST_LICENSE_PLATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Take the image and display it
            Bundle extras = data.getExtras();
            mImageBitmap = (Bitmap) extras.get("data");
            ivDisplay.setImageBitmap(mImageBitmap);

            //Save also on shared pref
            SharedPreferences mySharedPreferences = getSharedPreferences("App_settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString(SHARED_IMG, CameraHandler.encodeTobase64(mImageBitmap));
            editor.commit();
        }
    }

    private void onDataChangeRepTypes(DataSnapshot dataSnapshot) {
        final List<String> repTypes = new ArrayList<>();
        final List<Integer> fines = new ArrayList<>();

        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
            String repType = areaSnapshot.child("repType").getValue(String.class);
            fines.add(areaSnapshot.child("fine").getValue(Integer.class));
            repTypes.add(repType);
        }

        ArrayAdapter<String> repTypesAdapter = new ArrayAdapter<String>(CreateReport.this,
                android.R.layout.simple_spinner_item, repTypes);
        repTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRepType.setAdapter(repTypesAdapter);

        spnRepType.setSelection(getIndex(spnRepType, newRep.getReportType()));

        spnRepType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int selected, long arg3) {
                EditText etFine = (EditText) findViewById(R.id.etFineAmount);

                //If "other" chosen allow insert fine amount
                if (selected == Constants.OTHER_REP_TYPE) {
                    etFine.setEnabled(true);
                } else {
                    etFine.setEnabled(false);
                }

                int fine = fines.get(selected);
                etFine.setText(String.valueOf(fine));
                newRep.setFineAmount(fine);
                newRep.setReportType(repTypes.get(selected));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void onDataChangeCarColors(DataSnapshot dataSnapshot) {
        final List<String> colors = new ArrayList<>();

        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
            String color = areaSnapshot.child("color").getValue(String.class);
            colors.add(color);
        }

        ArrayAdapter<String> colorsAdapter = new ArrayAdapter<String>(CreateReport.this,
                android.R.layout.simple_spinner_item, colors);
        colorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnColor.setAdapter(colorsAdapter);

        spnColor.setSelection(getIndex(spnColor, newRep.getCarColor()));

        spnColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int selected, long arg3) {
                newRep.setCarColor(colors.get(selected));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void onDataChangeCarTypes(DataSnapshot dataSnapshot) {
        final List<String> carTypes = new ArrayList<>();

        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
            String carType = areaSnapshot.child("carType").getValue(String.class);
            carTypes.add(carType);
        }

        ArrayAdapter<String> carTypesAdapter = new ArrayAdapter<String>(CreateReport.this,
                android.R.layout.simple_spinner_item, carTypes);
        carTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCarType.setAdapter(carTypesAdapter);

        spnCarType.setSelection(getIndex(spnCarType, newRep.getCarType()));

        spnCarType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int selected, long arg3) {
                newRep.setCarType(carTypes.get(selected));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void loadRepTypesSpinner() {
        // database handler
        mDatabase = FirebaseDatabase.getInstance().getReference("repTypes");

        //Called only once - to initialize spinner data
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataChangeRepTypes(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadColorsSpinner() {
        // database handler
        mDatabase = FirebaseDatabase.getInstance().getReference("colors");

        //Called only once - to initialize spinner data
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataChangeCarColors(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadCarTypesSpinner() {
        // database handler
        mDatabase = FirebaseDatabase.getInstance().getReference("carTypes");

        //Called only once - to initialize spinner data
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataChangeCarTypes(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadSpinnerData() {
        loadRepTypesSpinner();
        loadColorsSpinner();
        loadCarTypesSpinner();
    }

    //Create new report button clicked
    public void onCreateReport(View view) {
        newRep.setFineAmount(Integer.parseInt(etFineAmount.getText().toString()));
        newRep.writeNewReport();
        String dir = DIR + newRep.getKey() + "/rep_img" + Constants.JPG;
        CameraHandler.uploadImage(mImageBitmap, dir);

        //Return menu
        Intent menuIntent = new Intent(this, MainMenuActivity.class);
        startActivity(menuIntent);
    }

    @Override
    //Handle canceling report and return to menu
    public void onBackPressed() {
        //Check if can cancel - after 10 sec can't
        if (newRep.checkCanBeCanceld()) {
            finish();
            return;
        } else {
            Toast.makeText(this, Constants.NO_CANCEL, Toast.LENGTH_LONG).show();
        }
    }

    public class Timer extends CountDownTimer {

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            newRep.disableCanceled();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }
}

