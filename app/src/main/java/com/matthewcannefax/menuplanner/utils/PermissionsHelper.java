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
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static List<String> permissions;

    public static int getMyPermissionsRequestCamera() {
        return MY_PERMISSIONS_REQUEST_CAMERA;
    }

    public static void checkPermissions(Activity activity, Context context){
        permissions = new ArrayList<>();
        checkStoragePermmisions(activity, context);
        checkCameraPermissions(activity, context);
        if(permissions != null && permissions.size() != 0){

            String[] permissionsArray = new String[permissions.size()];
            permissions.toArray(permissionsArray);
            ActivityCompat.requestPermissions(activity, permissionsArray, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private static void checkStoragePermmisions(Activity activity, Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private static void checkCameraPermissions(Activity activity, Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.CAMERA);
        }
    }
}
