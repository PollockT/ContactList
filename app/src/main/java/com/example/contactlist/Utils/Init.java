package com.example.contactlist.Utils;

import android.Manifest;

public class Init {
    public Init(){

    }

    public static final String[] PHONE_PERMISSIONS =  {Manifest.permission.CALL_PHONE};
    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

}
