package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DisplayReport extends AppCompatActivity {

    EditText etReporter;
    EditText etRepType;
    EditText etCarType;
    EditText etCarColor;
    EditText etAddress;
    EditText etDate;
    EditText etTime;
    EditText etFine;
    EditText etPlateNum;
    ImageView ivDisplay;

    Report rep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_report);
        AsyncCaller service = new AsyncCaller();
        service.execute();
    }

    public void getScreenFields() {
        etReporter = (EditText) findViewById(R.id.etReporterID);
        etRepType = (EditText) findViewById(R.id.etRepType);
        etCarType = (EditText) findViewById(R.id.etCarType);
        etCarColor = (EditText) findViewById(R.id.etCarColor);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        ivDisplay = (ImageView) findViewById(R.id.imgView);
        etPlateNum = (EditText) findViewById(R.id.etPlateNum);
        etFine = (EditText) findViewById(R.id.etFineAmount);
    }

    public void setScreenFields(Report rep) {
        etReporter.setText(String.valueOf(rep.getReporterID()));
        etRepType.setText(rep.getReportType());
        etCarType.setText(rep.getCarType());
        etCarColor.setText(rep.getCarColor());
        etAddress.setText(MyLocation.getAddress(rep.getLocation(), this));
        etDate.setText(rep.getDate());
        etTime.setText(rep.getTime());
        etFine.setText(String.valueOf(rep.getFineAmount()));
        etPlateNum.setText(rep.getCarNum());
    }

    public void getImage(final Context mContext, String dir) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://appakah-c217c.appspot.com");
        StorageReference imagesRef = storageRef.child(dir);
        /*imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri).into(ivDisplay);
            }
        });*/
    }

    public class AsyncCaller extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            //Get the chosen report for display
            rep =  (Report) getIntent().getSerializableExtra("chosenRep");
            getScreenFields();
            String dir = CreateReport.DIR + rep.getKey() + "/rep_img" + Constants.JPG;
            getImage(getApplicationContext(), dir);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(1000);
                setScreenFields(rep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
