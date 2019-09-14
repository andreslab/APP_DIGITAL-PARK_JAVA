package com.andreslab.digitalpark.CameraCameraKit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.andreslab.digitalpark.PhotoActivity;
import com.andreslab.digitalpark.R;
import com.andreslab.digitalpark.ShowGifView;
import com.camerakit.CameraKitView;
import com.jpegkit.Jpeg;
import com.jpegkit.JpegImageView;

public class SimpleCameraActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    Button btnTakePicture;
    private JpegImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_camera);
        cameraKitView = findViewById(R.id.camera);
        btnTakePicture = findViewById(R.id.simple_camera_take_picture);

        btnTakePicture.setOnClickListener(photoOnClickListener);

        imageView = new JpegImageView(getApplicationContext());




        ShowGifView showGifView = new ShowGifView(getApplicationContext());

        LinearLayout linearLayout = findViewById(R.id.layoutAnimal);
        linearLayout.addView(showGifView);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        showGifView.setLayoutParams(layoutParams);

        showGifView.setGifImageDrawableId(R.drawable.bird_2);
        showGifView.drawGif();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("CAMERA", "BEFORE TAKE PICTURE");
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView view, final byte[] photo) {
                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Jpeg jpeg = new Jpeg(photo);
                            imageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setJpeg(jpeg);
                                }
                            });
                        }
                    }).start();*/

                    Log.i("CAMERA"," CAPTURE FINISH");

                    Intent i = new Intent(getApplicationContext(), PhotoActivity.class);
                    i.putExtra("image", photo);
                    startActivity(i);
                }
            });
        }
    };
}
