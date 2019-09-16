package com.andreslab.digitalpark;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.andreslab.digitalpark.CameraCameraNative2.CustomCameraActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class PhotoActivity extends AppCompatActivity {

    ImageButton btnSave;
    ImageButton btnTake;
    ImageButton btnExit;

    ImageView imageView;

    Bitmap photoWithAnimal;

    String animalName;

    private static final int PERMISSION_REQUEST_CODE = 100;

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

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth() * 10, bmp.getHeight() * 10, true);

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


            Log.i("PHOTO","width: " + bmp.getWidth() + " height: " + bmp.getHeight());



            animalName = extras.getString("animal");

            int idAnimal = this.getResources().getIdentifier(animalName+"_gift", "drawable", this.getPackageName());
            Bitmap animal = BitmapFactory.decodeResource(this.getResources(), idAnimal);

            photoWithAnimal = combineImages(rotatedBitmap,animal);
            imageView.setImageBitmap(photoWithAnimal);
        }else{
            Toast.makeText(getApplicationContext(), "A ocurrido un problema con la imagen", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] photo = extras.getByteArray("image");
                Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);

                //saveToInternalStorage(bmp);
                //saveImageToExternalStorage(bmp);


                if  (photoWithAnimal != null) {
                    savePhotoInGallery(photoWithAnimal);
                    finish();
                }


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

                //finish();
            }
        });

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CustomCameraActivity.class);
                i.putExtra("animal_name",animalName);
                startActivity(i);
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
        //String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        //File myDir = new File(root + "/saved_images_digital_park");
        File myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
            Toast.makeText(this, "Se guardó correctamente", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ha ocurrido un error al guardar la foto: " + e.toString(), Toast.LENGTH_SHORT).show();
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

    private void savePhotoInGallery(Bitmap bitmap){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    String path = Environment.getExternalStorageDirectory().toString() + "/DCIM/PARQUE_DIGITAL";
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fname = "Image-ParqueDigital_" + n +".jpg";
                    File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(path, fname);
                    if (!file.exists()) {
                        Log.d("path", file.toString());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    requestPermission(); // Code for permission
                }
            } else {
                String path = Environment.getExternalStorageDirectory().toString();
                File file = new File(path, "UniqueFileName"+".jpg");
                if (!file.exists()) {
                    Log.d("path", file.toString());
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void savePrivateImage(Bitmap b){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        //String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        //File directory = new File(root + "/saved_images_digital_park");

        if (!directory.exists()){
            directory.mkdir();
        }
        File file = new File(directory, "UniqueFileName" + ".jpg");
        if (!file.exists()) {
            Log.d("path", file.toString());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(this, "Se guardó correctamente", Toast.LENGTH_SHORT).show();
            } catch (java.io.IOException e) {
                Toast.makeText(this, "Hubo un error al guardar la foto" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    //PERMISSION
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(PhotoActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PhotoActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(PhotoActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
            break;
        }
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
        int widthNewPhotoAnimal;
        int heightNewPhotoAnimal;
        if (animal.getWidth() < photo.getWidth() / 3){
            widthNewPhotoAnimal = animal.getWidth();
            heightNewPhotoAnimal = animal.getHeight();
        }else{
            widthNewPhotoAnimal = animal.getWidth() /4;
            heightNewPhotoAnimal = (widthNewPhotoAnimal * animal.getHeight()) / animal.getWidth();
        }
        Bitmap resizeAnimal = Bitmap.createScaledBitmap(animal, widthNewPhotoAnimal, heightNewPhotoAnimal, false);
        comboImage.drawBitmap(resizeAnimal, (width / 2 ) - (resizeAnimal.getWidth() / 2) - 20 , height / 2 - (resizeAnimal.getHeight() / 2), null);

        return cs;
    }
}
