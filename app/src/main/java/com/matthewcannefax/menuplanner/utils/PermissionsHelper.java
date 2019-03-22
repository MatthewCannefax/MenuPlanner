package com.matthewcannefax.menuplanner.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionsHelper {

    //constant int field to identify the permission type
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    //string list of permissions
    private static List<String> permissions;

    //this boolean will be switched to true or false to show whether the menulist activity has been opened before in the current session.
    private static boolean menuFirstInstance = true;

    public static boolean isMenuFirstInstance() {
        return menuFirstInstance;
    }

    public static void setMenuFirstInstance(boolean menuFirstInstance) {
        PermissionsHelper.menuFirstInstance = menuFirstInstance;
    }

    //get the permission id for the camera
    static int getMyPermissionsRequestCamera() {
        return MY_PERMISSIONS_REQUEST_CAMERA;
    }

    //check the permissions
    public static void checkPermissions(Activity activity, Context context){
        //instantiate the permissions list
        permissions = new ArrayList<>();

        //use the checkPermissions methods to check the requested permissions
        checkStoragePermmisions(context);
        checkCameraPermissions(context);

        //if the list has items request the permission(s) in the list
        if(permissions != null && permissions.size() != 0){

            //create a new array from the size of the permissions list
            String[] permissionsArray = new String[permissions.size()];

            //fill the array with the items of the permissions list
            permissions.toArray(permissionsArray);

            //request permissions
            ActivityCompat.requestPermissions(activity, permissionsArray, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public static boolean checkPermissionsForImage(Context context){
        boolean permmission = false;

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            permmission = true;
        }

        return permmission;
    }

    //check for permissions to use External storage
    private static void checkStoragePermmisions(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    //check for permissions to use the camera
    private static void checkCameraPermissions(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.CAMERA);
        }
    }
}
