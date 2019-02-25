package com.deepakbajpay.capture.utils;

import android.Manifest;

public interface Constants {
    int PIC_PHOTO = 1;
    int PIC_DOCUMENT = 2;
    String[] GALLERY_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
}
