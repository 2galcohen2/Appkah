package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HazardActivity extends AppCompatActivity {
    Hazard newHazard;
    LinearLayout photosLayout;
    Defect chosenDefect;

    EditText etAddress;
    EditText etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazard);

        chosenDefect =  (Defect) getIntent().getSerializableExtra("chosenDefect");

        photosLayout = (LinearLayout) findViewById(R.id.linLayPictures);
        newHazard = new Hazard(this, User.getUserID(this), chosenDefect.getDefectType());
        ImageView imgDefect = (ImageView) findViewById(R.id.imHazardType);
        imgDefect.setImageResource(chosenDefect.GetImageResource());

        //Set current address
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etAddress.setText(MyLocation.getAddress(newHazard.getLocation(), this));

        //Set title
        TextView txtTitle = (TextView) findViewById(R.id.tvPageTitle);
        txtTitle.setText(Constants.CREATE_HAZARD_TITLE);
    }

    public void onAddPhoto(View view){
        if (!newHazard.addPicture(this)){
            Toast.makeText(this, Constants.CANT_UPLOAD, Toast.LENGTH_LONG).show();
        }
    }

    //Add hazard photos and report to DB
    public void onHazardReport(View view) {
        //Pass all imageview and save the photos
        int count = photosLayout.getChildCount();

        for (int i = 0; i < count; i++) {
            View currObject = photosLayout.getChildAt(i);

            if (currObject instanceof ImageView) {
                ImageView currImg = (ImageView) currObject;
                Bitmap imgBitmap = ((BitmapDrawable) currImg.getDrawable()).getBitmap();
                String dir = Hazard.returnDir() + "/" + newHazard.getKey() + "/" +
                        newHazard.checkPhotoCounter() + Constants.JPG;
                CameraHandler.uploadImage(imgBitmap, dir);
            }
        }

        newHazard.setDescription(etDescription.getText().toString());

        //Save hazard
        newHazard.writeHazard();

        //Return menu
        Intent menuIntent = new Intent(this, MainMenuActivity.class);
        startActivity(menuIntent);
    }

    @Override
    //Add the taken picture to the photos shown in report page
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(180,160));
            image.setMaxHeight(100);
            image.setMaxWidth(100);
            image.setImageBitmap(mImageBitmap);
            photosLayout.addView(image);
        }
    }
}
