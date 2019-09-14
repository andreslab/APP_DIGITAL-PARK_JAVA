package com.andreslab.digitalpark;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreslab.digitalpark.CameraCameraKit.SimpleCameraActivity;
import com.andreslab.digitalpark.CameraCameraNative2.CustomCameraActivity;

public class PopUpActivity extends Activity {

    private static final String TAG = "PopUp";
    String animal_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        TextView title = (TextView)findViewById(R.id.title_detail_animal);
        TextView detail = (TextView)findViewById(R.id.text_detail_animal);
        ImageView img_animal = (ImageView)findViewById(R.id.img_detail_animal);
        ImageButton btnClose = (ImageButton)findViewById(R.id.btn_close);
        ImageButton btnCamera = (ImageButton)findViewById(R.id.btn_camera);


        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            animal_name = extras.getString("name");
        }else{
            Toast.makeText(getApplicationContext(), "A ocurrido un problema", Toast.LENGTH_SHORT).show();
        }


        if (animal_name != "") {

            title.setText(animal_name);

            int idImg = getResources().getIdentifier("img_"+animal_name+"_min", "drawable", getPackageName());
            Log.i(TAG, "imagen: "+"img_"+animal_name+"  codigo:  "+idImg);
            if (idImg != 0) {
                img_animal.setImageResource(idImg);
            }


            int idDescription = getResources().getIdentifier("description_"+animal_name, "string", getPackageName());
            Log.i(TAG, "Descripcion: "+"description_"+animal_name+"  codigo:  "+idDescription);
            if(idDescription != 0){
                String textDetail = getResources().getString(idDescription);
                detail.setText(textDetail);
            }
        }


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                //Intent i = new Intent(getApplicationContext(), SimpleCameraActivity.class);
                Intent i = new Intent(getApplicationContext(), CustomCameraActivity.class);
                i.putExtra("animal_name", animal_name);
                startActivity(i);
                //finish();
            }
        });


        //config style

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int heigth = dm.heightPixels;
        int width = dm.widthPixels;

        getWindow().setLayout((int) (width * .8), (int) (heigth* .7));

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }
}
