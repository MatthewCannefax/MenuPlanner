package com.matthewcannefax.menuplanner.utils;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

//this class helps with loading/requesting/taking photos for use with the app
@SuppressWarnings("SameReturnValue")
public class ImageHelper {

    //Getters for gallery and capture request ids
    private static int getRequestImageCapture() {
        return REQUEST_IMAGE_CAPTURE;
    }
    private static int getRequestImageGallery() {return REQUEST_IMAGE_GALLERY; }

    //constant fields
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int DEFAULT_IMAGE = R.drawable.ic_photo_camera_black_24dp;


    //File field for new file creation
    private static File photoFile;


    //this is used to set the imageview with photos that are part of the app in the drawable directory
    public static void setImageViewDrawable(String imagePath, Context context, ImageView imageView){
        if(imagePath.equals(context.getString(R.string.no_img_selected))) {
//            imageView.setImageBitmap(loadSampledResource(context, getResId(context, imagePath)));
            imageView.setImageResource(DEFAULT_IMAGE);
        }else if(loadSampledIMGFile(imagePath) != null){
            imageView.setImageBitmap(loadSampledIMGFile(imagePath));
        }else {
            imageView.setImageResource(DEFAULT_IMAGE);
        }
    }

    //this method sets the clicklistener for the imageviews that are part of the add and edit recipe activities
    public static void setImageViewClickListener(final Context context, ImageView imageView, final Activity activity){
        //constant vars using string values from resources
        final String title = context.getString(R.string.new_photo_title);
        final String message = context.getString(R.string.new_photo_message);
        final String takePhoto = context.getString(R.string.take_new_photo);
        final String existing = context.getString(R.string.existing);
        final String cancel = context.getString(R.string.cancel);

        //this onclicklistener asks the user if they would like to select a photo from gallery or take a new photo
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (PermissionsHelper.checkPermissionsForImage(context)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(title);
                    builder.setMessage(message);

                    //neutral button is a simple null cancel
                    builder.setNeutralButton(cancel, null);

                    //take a new photo with the positive button
                    builder.setPositiveButton(takePhoto, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            takePhoto(activity, context);
                        }
                    });

                    //choose a photo from gallery with the negative button
                    builder.setNegativeButton(existing, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQUEST_IMAGE_GALLERY);
                        }
                    });

                    builder.show();
                } else {
                    PermissionsHelper.checkPermissions(activity, context);
                    Snackbar.make(activity.findViewById(android.R.id.content), "Permissions must be granted to add photos", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    //this mehtod handle the photo taking of the app
    private static void takePhoto(Activity activity, Context context){

        //check if the app has permission to use the camera
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            //create a photo intent
            Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //grant URI permission to the photo intent
            photoIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            if(photoIntent.resolveActivity(activity.getPackageManager()) != null){
                photoFile = null;
                try {
                    //create an image file
                    photoFile = createImageFile(context);
                } catch (IOException e) {
                    Toast.makeText(activity.getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
                //check if the photofile is null or not
                if (photoFile != null){

                    //use the fileprovider class to create a photoURI
                    Uri photoUri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider", photoFile);

                    //grant uri permission to the photo intent
                    photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    //add the photo URI to the photo intent extras
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                    //start the photo activity
                    activity.startActivityForResult(photoIntent, REQUEST_TAKE_PHOTO);
                }

            }
        }
        //if the app does not have permission, ask for it and recur the method
        else {

            //request camera permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PermissionsHelper.getMyPermissionsRequestCamera());

            //recur this method
            takePhoto(activity, context);
        }
    }

    //this method is used in the onActivityResult method in the Add and Edit Recipe
    public static String getPhotoTaken(Context context, int requestCode, int resultCode, Intent data, ImageView imageView){

        //null the photoPath
        String photoPath = null;

        //if the photo came from a photo taken
        if(requestCode == ImageHelper.getRequestImageCapture() && resultCode == RESULT_OK){

            //the target width and height to use for resizing the image
            int targetW = 100;
            int targetH = 100;

            //bitmap factory options to create the new photo file
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);

            //get the current width and height of the photo
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            //set the bitmap with the absolute path of the new file
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);

            //make sure that the image in oriented correctly
            bitmap = getBitmapExif(photoFile, bitmap);

            //set the image of the imageview with the newly resized bitmap image
            imageView.setImageBitmap(bitmap);

            //set the photoPath with the absolute path of the photo file
            photoPath = photoFile.getAbsolutePath();
        }
        //if the photo came from gallery
        else if(requestCode == ImageHelper.getRequestImageGallery() && resultCode == RESULT_OK){
            //create a uri with the data from the activity result
            Uri selectedIMG = data.getData();

            //create a new bitmap object
            Bitmap bitmap;
            try{
                //use the select image object to create a new bitmap
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedIMG);

                //make sure that the orientation of the image is correct
                bitmap = getBitmapOrientationCursor(selectedIMG, context, bitmap);

                //set the image of the imageview with the newly selected bitmap
                imageView.setImageBitmap(bitmap);

                //create a new File with the createImageFile method
                File galleryFile = createImageFile(context);

                //output stream to save the image to local storage of the app
                OutputStream outputStream = new FileOutputStream(galleryFile);

                //compress the image
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                //close the stream
                outputStream.flush();
                outputStream.close();

                //set the photopath to the absoloute path of the gallery file
                photoPath = galleryFile.getAbsolutePath();

            }catch (IOException e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        //return the absolute path of the gallery or new photo file
        return photoPath;
    }

    //this method uses orientation cursor to make sure that the image displays in the correct orientation
    private static Bitmap getBitmapOrientationCursor(Uri uri, Context context, Bitmap bitmap){
        int orientation;
        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if(cursor != null){
            if(cursor.getCount() != 1){
                cursor.close();
                //noinspection UnusedAssignment
                orientation = -1;
            }

            cursor.moveToFirst();
            orientation = cursor.getInt(0);
            cursor.close();
            //noinspection UnusedAssignment
            cursor = null;
        }else{
            orientation = -1;
        }

        Matrix m = new Matrix();
        m.postRotate(orientation);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        float scale = Math.min(100/ (float)bitmapWidth, 100/(float)bitmapHeight);

        int scaledWidth = (int)(bitmapWidth * scale);
        int scaledHeight = (int)(bitmapHeight * scale);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);

        return Bitmap.createBitmap(scaledBitmap, 0,0, scaledWidth, scaledHeight, m, true);


    }

    //this method reduces the size of a bitmap image
    private static Bitmap loadSampledResource(Context context, int imageID){
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(context.getResources(), imageID, options);

        final int originalHeight = options.outHeight;
        final int originalWidth = options.outWidth;
        int inSampleSize = 1;

        while((originalHeight / (inSampleSize * 2)) > 100 && (originalWidth / (inSampleSize * 2)) > 100){
            inSampleSize *= 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(context.getResources(), imageID, options);
    }

    //this method reduce the size of an image file
    private static Bitmap loadSampledIMGFile(String imagePath){
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);

        final int originalHeight = options.outHeight;
        final int originalWidth = options.outWidth;
        int inSampleSize = 1;

        while((originalHeight / (inSampleSize * 2)) > 100 && (originalWidth / (inSampleSize * 2)) > 100){
            inSampleSize *= 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        File file = new File(imagePath);

        return getBitmapExif(file, BitmapFactory.decodeFile(imagePath, options));
    }

    //this method is used to the the resource id of a drawable image
    private static int getResId(Context context, String imgPath){
        return context.getResources().getIdentifier(imgPath, "drawable", context.getPackageName());

    }

    //this method creates an image file
    @SuppressWarnings("unused")
    private static File createImageFile(Context context) throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName = "RECIPE_PHOTO_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imgFileName,
                ".jpg",
                storageDir
        );
        String mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //this method uses the Exif Interface to make sure the image is displayed in the correct orientation
    private static Bitmap getBitmapExif(File file, Bitmap bitmap){

        ExifInterface exif;
        int orientation = -1;
        try{
            exif = new ExifInterface(file.getAbsolutePath());
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        }catch (IOException e){
            e.printStackTrace();
        }

        Matrix m = new Matrix();

        if(orientation == ExifInterface.ORIENTATION_ROTATE_180){
            m.postRotate(180);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true );
        }else if (orientation <= ExifInterface.ORIENTATION_ROTATE_90 && orientation > 1){
            m.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true );
        }else if (orientation == ExifInterface.ORIENTATION_ROTATE_270){
            m.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true );
        }

        return bitmap;
    }

    public static void resetRecipeImage(Context context, Recipe recipe){
        recipe.setImagePath(context.getString(R.string.no_img_selected));
    }

}
