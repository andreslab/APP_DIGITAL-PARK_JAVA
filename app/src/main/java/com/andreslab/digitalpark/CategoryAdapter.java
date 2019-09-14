package com.andreslab.digitalpark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.InputEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<GroupAnimals> animals;
    private final ArrayList<String> animalNameNearst;

    // 1
    public CategoryAdapter(Context context, ArrayList<GroupAnimals> animals, ArrayList<String> animalNameNearst) {
        this.mContext = context;
        this.animals = animals;
        this.animalNameNearst = animalNameNearst;
    }

    // 2
    @Override
    public int getCount() {
        return animals.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(String.valueOf(position));
        return dummyTextView;
    }*/

    private static final String TAG = "Category list";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        final GroupAnimals animal = animals.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_animal, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.animal_img);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.animal_name);
        // 4

        nameTextView.setText(animal.getName().toUpperCase());

        if (animalNameNearst.contains(animal.getName())) {
            if (animal.getName().contains(" ")){
                animal.setName(animal.getName().replace(" ", "_"));
            }
            animal.setName(eliminarAcentos(animal.getName()));

            int idImg = mContext.getResources().getIdentifier("img_"+animal.getName()+"_min", "drawable", mContext.getPackageName());
            Log.i(TAG, "imagen: "+"img_"+animal.getName()+"  codigo:  0");
            if (idImg != 0) {
                imageView.setImageResource(idImg);
            }else{
                imageView.setImageResource(R.drawable.not);
            }
        }else {
            if (animal.getName().contains(" ")){
                animal.setName(animal.getName().replace(" ", "_"));
            }
            animal.setName(eliminarAcentos(animal.getName()));

            int idImg = mContext.getResources().getIdentifier("img_"+animal.getName()+"_min", "drawable", mContext.getPackageName());
            Log.i(TAG, "imagen: "+"img_"+animal.getName()+"  codigo:  "+idImg);
            if (idImg != 0) {
                imageView.setImageBitmap(grayScaleImage(BitmapFactory.decodeResource( mContext.getResources(),idImg)));
            }else{
                imageView.setImageBitmap(grayScaleImage(BitmapFactory.decodeResource( mContext.getResources(), R.drawable.not)));
            }
        }




        return convertView;
    }

    public static String eliminarAcentos(String str) {

        final String ORIGINAL = "ÁáÉéÍíÓóÚúÑñÜü";
        final String REEMPLAZO = "AaEeIiOoUuNnUu";

        if (str == null) {
            return null;
        }
        char[] array = str.toCharArray();
        for (int indice = 0; indice < array.length; indice++) {
            int pos = ORIGINAL.indexOf(array[indice]);
            if (pos > -1) {
                array[indice] = REEMPLAZO.charAt(pos);
            }
        }
        return new String(array);
    }

    //GRAYSCALE
    public static Bitmap grayScaleImage(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

}
