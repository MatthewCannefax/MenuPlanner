package com.matthewcannefax.menuplanner.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class ImageHelper {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;

    private static Uri imageUri;

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

    public static void setImageViewClickListener(final Context context, ImageView imageView, final Activity activity){
        final String title = context.getString(R.string.new_photo_title);
        final String message = context.getString(R.string.new_photo_message);
        final String takePhoto = context.getString(R.string.take_new_photo);
        final String existing = context.getString(R.string.existing);
        final String cancel = context.getString(R.string.cancel);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setNeutralButton(cancel, null);
                builder.setPositiveButton(takePhoto, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        takePhoto(activity);
                    }
                });
                builder.setNegativeButton(existing, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Exisition", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });
    }

    private static void takePhoto(Activity activity){
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "recipeIMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        activity.startActivityForResult(photoIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }



}
