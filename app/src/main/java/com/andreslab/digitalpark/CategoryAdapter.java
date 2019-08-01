package com.andreslab.digitalpark;

import android.content.Context;
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

    // 1
    public CategoryAdapter(Context context, ArrayList<GroupAnimals> animals) {
        this.mContext = context;
        this.animals = animals;
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
        //imageView.setImageResource(book.getImageResource());
        nameTextView.setText(animal.getName());


        return convertView;
    }

}
