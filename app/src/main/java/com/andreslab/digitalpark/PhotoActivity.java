package com.andreslab.digitalpark;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class PhotoActivity extends AppCompatActivity {

    ImageButton btnSave;
    ImageButton btnTake;
    ImageButton btnExit;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ImageView img = (ImageView) findViewById(R.id.img_capture);

        btnSave = (ImageButton) findViewById(R.id.btn_photo_save);
        btnTake = (ImageButton) findViewById(R.id.btn_photo_take);
        btnExit = (ImageButton) findViewById(R.id.btn_photo_exit);

        imageView = (ImageView) findViewById(R.id.img_capture);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            byte[] photo = extras.getByteArray("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            // Set the Bitmap data to the ImageView
            imageView.setImageBitmap(bmp);
        }else{
            Toast.makeText(getApplicationContext(), "A ocurrido un problema con la imagen", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] photo = extras.getByteArray("image");
                Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);

                saveImageToExternalStorage(bmp);

                Toast.makeText(PhotoActivity.this, "Foto guardada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });



    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images_digital_park");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

    }
}
