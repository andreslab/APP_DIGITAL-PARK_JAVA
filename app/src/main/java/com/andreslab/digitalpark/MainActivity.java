package com.andreslab.digitalpark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    Button btn_mamifero;
    Button btn_ave;
    Button btn_reptil;
    ImageButton btn_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_mamifero = findViewById(R.id.btnMamifero);
        btn_ave = findViewById(R.id.btnAves);
        btn_reptil = findViewById(R.id.btnReptiles);
        btn_camera = findViewById(R.id.btnCamera);

        btn_mamifero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                i.putExtra("category", 0);
                startActivity(i);
            }
        });

        btn_ave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                i.putExtra("category", 1);
                startActivity(i);
            }
        });

        btn_reptil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                i.putExtra("category", 2);
                startActivity(i);
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CameraARCoreActivity.class);
                startActivity(i);
            }
        });
    }
}
