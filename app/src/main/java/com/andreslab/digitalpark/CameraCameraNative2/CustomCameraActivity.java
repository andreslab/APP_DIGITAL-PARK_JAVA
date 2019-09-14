package com.andreslab.digitalpark.CameraCameraNative2;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.andreslab.digitalpark.R;

public class CustomCameraActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;

    ShowCamera showCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        frameLayout = (FrameLayout)findViewById(R.id.custom_camera);

        //open Camera
        camera = Camera.open();
        showCamera = new ShowCamera(this, camera);

        frameLayout.addView(showCamera);

    }
}
