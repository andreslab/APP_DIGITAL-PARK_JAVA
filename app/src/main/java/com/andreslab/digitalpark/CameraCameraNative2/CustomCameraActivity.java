package com.andreslab.digitalpark.CameraCameraNative2;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.andreslab.digitalpark.PhotoActivity;
import com.andreslab.digitalpark.R;
import com.andreslab.digitalpark.ShowGifView;

import java.io.File;

public class CustomCameraActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;

    ShowCamera showCamera;

    Button btnTakePicure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        frameLayout = (FrameLayout)findViewById(R.id.custom_camera);

        //open Camera
        camera = Camera.open();
        showCamera = new ShowCamera(this, camera);

        frameLayout.addView(showCamera);

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
        ShowGifView showGifView = new ShowGifView(getApplicationContext());

        LinearLayout linearLayout = findViewById(R.id.layoutAnimal);
        linearLayout.addView(showGifView);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        showGifView.setLayoutParams(layoutParams);

        showGifView.setGifImageDrawableId(R.drawable.bird_2);
        showGifView.drawGif();

    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Intent i = new Intent(getApplicationContext(), PhotoActivity.class);
            i.putExtra("image", data);
            startActivity(i);
            finish();
        }
    };
}
