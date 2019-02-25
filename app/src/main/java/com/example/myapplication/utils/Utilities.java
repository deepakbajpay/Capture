package com.example.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

public class Utilities {

    public static boolean shouldShowRequestPermissionRationale(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= 23)
            if (!((Activity) context).shouldShowRequestPermissionRationale(permission)) {
                showAlertDialog(context, "Permission Required", "App needs permissions to continue.", "Open settings", "Don't grant");
                return true;
            }
        return false;
    }

    private static void showAlertDialog(final Context context, String title, String message, String positiveText, String negativeText) {
        AlertDialog builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                }).setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setCancelable(false)
                .show();

    }
}
