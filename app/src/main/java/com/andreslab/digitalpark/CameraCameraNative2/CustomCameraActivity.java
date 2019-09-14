package com.andreslab.digitalpark.CameraCameraNative2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andreslab.digitalpark.PhotoActivity;
import com.andreslab.digitalpark.R;
import com.andreslab.digitalpark.ShowGifView;

import java.io.File;

public class CustomCameraActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;

    ShowCamera showCamera;

    Button btnTakePicure;

    String animalName = "";

    ImageView imageViewAnimal;
    ShowGifView showGifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        frameLayout = (FrameLayout)findViewById(R.id.custom_camera);

        imageViewAnimal = (ImageView) findViewById(R.id.imageViewAnimal);


        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            animalName = extras.getString("animal_name");
        }else{
            Toast.makeText(getApplicationContext(), "A ocurrido un problema", Toast.LENGTH_SHORT).show();
        }

        checkCameraPermission();


        btnTakePicure = (Button)findViewById(R.id.btn_custom_camera_take_picture);


        btnTakePicure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null){
                    camera.takePicture(null,null,mPictureCallback);
                }
            }
        });


        //load gift
        showGifView = new ShowGifView(getApplicationContext());

        LinearLayout linearLayout = findViewById(R.id.layoutAnimal);
        linearLayout.addView(showGifView);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        showGifView.setLayoutParams(layoutParams);

        /*showGifView.setGifImageDrawableId(R.drawable.bird_2);
        showGifView.drawGif();*/

    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Intent i = new Intent(getApplicationContext(), PhotoActivity.class);
            i.putExtra("image", data);
            i.putExtra("animal", animalName);
            startActivity(i);
            finish();
        }
    };



    private final String TAG = "Debug_MainActivity";


    public void activateCamera(){
        //open Camera
        camera = Camera.open();
        showCamera = new ShowCamera(this, camera);

        frameLayout.addView(showCamera);

        if (animalName == "paloma"){
            imageViewAnimal.setVisibility(View.INVISIBLE);
            showGifView.setGifImageDrawableId(R.drawable.bird_2);
            showGifView.drawGif();
        }else{
            int idAnimal = this.getResources().getIdentifier(animalName+"_gift", "drawable", this.getPackageName());
            imageViewAnimal.setImageResource(idAnimal);
        }

    }



    private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0x00AF;
    private void checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permission not available requesting permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
        } else {
            Log.d(TAG,"Permission has already granted");
            activateCamera();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_USE_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"permission was granted! Do your stuff");
                    activateCamera();
                } else {
                    Log.d(TAG,"permission denied! Disable the function related with permission.");
                }
                return;
            }
        }
    }



}
