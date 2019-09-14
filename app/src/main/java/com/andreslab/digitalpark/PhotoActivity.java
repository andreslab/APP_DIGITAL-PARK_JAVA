package com.andreslab.digitalpark;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
import java.io.IOException;
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


            //rotate
            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);



            String animalName = extras.getString("animal");

            int idAnimal = this.getResources().getIdentifier("img_"+animalName+"_min", "drawable", this.getPackageName());
            Bitmap animal = BitmapFactory.decodeResource(this.getResources(), idAnimal);

            Bitmap new_bmp = combineImages(rotatedBitmap,animal);
            imageView.setImageBitmap(new_bmp);
        }else{
            Toast.makeText(getApplicationContext(), "A ocurrido un problema con la imagen", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] photo = extras.getByteArray("image");
                Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);


                saveToInternalStorage(bmp);


                //saveImageToExternalStorage(bmp);
                /*File picture_file = getOutputMediaFile();
                if (picture_file == null){
                    return;
                }else{
                    try{
                        FileOutputStream fos = new FileOutputStream(picture_file);
                        fos.write(photo);
                        fos.close();

                        Toast.makeText(PhotoActivity.this, "Foto guardada", Toast.LENGTH_SHORT).show();

                    }catch(IOException e){
                        e.printStackTrace();
                        Toast.makeText(PhotoActivity.this, "Error al guardar foto", Toast.LENGTH_SHORT).show();

                    }
                }*/

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

    //guardar foto en memoria interna
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        if (!directory.exists()){
            directory.mkdir();
        }
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    //guardar foto en memoria externa MICRO SD
    private File getOutputMediaFile(){
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }else{
            //File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "GUI");
            File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "GUI");
            if (!folder_gui.exists()){
                folder_gui.mkdir();
            }

            File outputFile = new File(folder_gui, "temp.jpg");
            return outputFile;
        }
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


    public Bitmap combineImages(Bitmap photo, Bitmap animal)
    {
        Bitmap cs = null;

        int width, height = 0;

        /*if(photo.getWidth() > animal.getWidth()) {
            width = photo.getWidth() + animal.getWidth();
            height = photo.getHeight();
        } else {
            width = animal.getWidth() + animal.getWidth();
            height = photo.getHeight();
        }*/
        width = photo.getWidth();
        height = photo.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(photo, 0f, 0f, null);
        comboImage.drawBitmap(animal, width / 2, height / 2, null);

        return cs;
    }
}
