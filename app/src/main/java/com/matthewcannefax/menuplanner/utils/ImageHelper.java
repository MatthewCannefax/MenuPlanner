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
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.getDrawable;
import static android.support.v4.content.ContextCompat.getExternalFilesDirs;

public class ImageHelper {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;

    public static int getRequestImageCapture() {
        return REQUEST_IMAGE_CAPTURE;
    }
    public static int getRequestImageGallery() {return REQUEST_IMAGE_GALLERY; }

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static File photoFile;
    private static String mCurrentPhotoPath;

    private static Uri imageUri;

    public static void setImageViewDrawable(String imagePath, Context context, ImageView imageView){
        if(imagePath == context.getString(R.string.no_img_selected)) {
            imageView.setImageBitmap(loadSampledResource(context, getResId(context, imagePath), 100, 100));
        }else{
            imageView.setImageBitmap(loadSampledIMGFile(imagePath, 100, 100));
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
                        takePhoto(activity, context);
                    }
                });
                builder.setNegativeButton(existing, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQUEST_IMAGE_GALLERY);
                    }
                });

                builder.show();
            }
        });
    }

    private static void takePhoto(Activity activity, Context context){
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if(photoIntent.resolveActivity(activity.getPackageManager()) != null){
            photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException e) {
                Toast.makeText(activity.getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider", photoFile);
                photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(photoIntent, REQUEST_TAKE_PHOTO);

            }

        }
    }

    public static String getPhotoTaken(Context context, int requestCode, int resultCode, Intent data, ImageView imageView){
        String photoPath = null;
        if(requestCode == ImageHelper.getRequestImageCapture() && resultCode == RESULT_OK){
            int targetW = 100;
            int targetH = 100;

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
            imageView.setImageBitmap(bitmap);

            photoPath = photoFile.getAbsolutePath();
        }else if(requestCode == ImageHelper.getRequestImageGallery() && resultCode == RESULT_OK){
            Uri selectedIMG = data.getData();
            Bitmap bitmap = null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedIMG);
                imageView.setImageBitmap(bitmap);

                File galleryFile = createImageFile(context);
                OutputStream outputStream = new FileOutputStream(galleryFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                photoPath = galleryFile.getAbsolutePath();

            }catch (IOException e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return photoPath;
    }

    private static Bitmap loadSampledResource(Context context, int imageID, int targetHeight, int targetWidth){
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(context.getResources(), imageID, options);

        final int originalHeight = options.outHeight;
        final int originalWidth = options.outWidth;
        int inSampleSize = 1;

        while((originalHeight / (inSampleSize * 2)) > targetHeight && (originalWidth / (inSampleSize * 2)) > targetWidth){
            inSampleSize *= 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageID, options);
        return bitmap;
    }

    private static Bitmap loadSampledIMGFile(String imagePath, int targetHeight, int targetWidth){
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);

        final int originalHeight = options.outHeight;
        final int originalWidth = options.outWidth;
        int inSampleSize = 1;

        while((originalHeight / (inSampleSize * 2)) > targetHeight && (originalWidth / (inSampleSize * 2)) > targetWidth){
            inSampleSize *= 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }

    private static int getResId(Context context, String imgPath){
        return context.getResources().getIdentifier(imgPath, "drawable", context.getPackageName());

    }

    private static File createImageFile(Context context) throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName = "RECIPE_PHOTO_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imgFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
