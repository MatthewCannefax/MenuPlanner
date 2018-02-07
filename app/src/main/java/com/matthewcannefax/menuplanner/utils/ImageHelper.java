package com.matthewcannefax.menuplanner.utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

public class ImageHelper {

    public static void setImageViewDrawable(String imagePath, Context context, ImageView imageView){

        InputStream  inputStream = null;

        try {
            inputStream = context.getAssets().open(imagePath);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





}
