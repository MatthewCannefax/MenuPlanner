package com.matthewcannefax.menuplanner.arrayAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.matthewcannefax.menuplanner.R;

import java.util.List;

//this custom array adapter is to act as a placeholder so a button can be added as a footer view in an empty listview
public class ButtonArrayAdapter extends ArrayAdapter {
    private List<Object> mObjects;

    private LayoutInflater mInflater;

    public ButtonArrayAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, R.layout.fake_btn_layout, objects);
        mObjects = objects;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.fake_btn_layout, parent, false);
        }

        return convertView;
    }
}
