package com.example.geolocateapp;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionHintDialog extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int message = R.string.request_permission_setting;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            message = R.string.request_permission_setting_android_s;
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            message = R.string.request_permission_setting_android_q;
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isFinishing()) {
                    finish();
                }
            }
        }).create().show();
    }
}