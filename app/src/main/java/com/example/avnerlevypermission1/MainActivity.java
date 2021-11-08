package com.example.avnerlevypermission1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity<x> extends AppCompatActivity {

    public final int PERMISSION_FOR_SEND_SMS = 0;
    public final int PERMISSION_FOR_CALL_PHONE = 1;
    public final int PERMISSION_FOR_CAMERA = 2;
    AlertDialog.Builder builder;

    private int counter = 0, x = 1, y = 1, z = 1;
    private String[] permissionsList = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CAMERA
    };
    private Button logInBtn, callBtn, smsBtn, camBtn, resetBtn;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;
            Log.i("TAG", "onReceive: " + batteryPct);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        listeners();
    }

    private void init() {
        logInBtn = findViewById(R.id.logIn_btn);
        callBtn = findViewById(R.id.call_btn);
        smsBtn = findViewById(R.id.sms_btn);
        camBtn = findViewById(R.id.camera_btn);
        resetBtn = findViewById(R.id.resetBtn);
        builder = new AlertDialog.Builder(this);

    }

    private void

    permissions(int permissionNumber) {
        if (!hasPermission(permissionNumber, this, permissionsList))
            ActivityCompat.requestPermissions(this, permissionsList, permissionNumber);
        else {
            ActivityCompat.requestPermissions(this, permissionsList, permissionNumber);
        }
    }

    private void listeners() {

        callBtn.setOnClickListener(v -> {
            permissions(0);
            if (counter == 3)
                logInBtn.setBackgroundColor(this.getApplicationContext().getResources().getColor(R.color.codereGreen));
            Log.i("TAG", "counter: " + counter);

        });
        smsBtn.setOnClickListener(v -> {
            permissions(1);
            if (counter == 3)
                logInBtn.setBackgroundColor(this.getApplicationContext().getResources().getColor(R.color.codereGreen));
            Log.i("TAG", "counter: " + counter);

        });
        camBtn.setOnClickListener(v -> {
            permissions(2);
            if (counter == 3)
                logInBtn.setBackgroundColor(this.getApplicationContext().getResources().getColor(R.color.codereGreen));
            Log.i("TAG", "counter: " + counter);

        });
        logInBtn.setOnClickListener(v -> {

            Log.i("TAG", "listeners: " + counter);
        });

        resetBtn.setOnClickListener(v -> {
            //Setting message manually and performing action on button click
            builder.setMessage(R.string.dialog_message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            killMe();
                            Toast.makeText(getApplicationContext(), "you choose yes action for alertbox",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "you choose no action for alertbox",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle(R.string.dialog_title);
            alert.show();

        });
    }

    private void killMe() {
        ((ActivityManager) this.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
    }

    private boolean hasPermission(int index, Context context, String... permissionsList) {
        if (context != null && permissionsList != null) {
            for (String permission : permissionsList) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    counter += x;
                    x = 0;
                    grantedToast("Call-Phone", callBtn);
                } else
                    deniedToast("Call-Phone", callBtn);

                break;

            case 1:
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    counter += y;
                    y = 0;
                    grantedToast("SMS", smsBtn);
                } else
                    deniedToast("SMS", smsBtn);
                break;

            case 2:
                if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    counter += z;
                    z = 0;
                    grantedToast("Camera", camBtn);
                } else
                    deniedToast("Camera", camBtn);
                break;
            default:
                Log.i("TAG", "Default!");
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void grantedToast(String message, Button btn) {
        btn.setBackgroundColor(this.getApplicationContext().getResources().getColor(R.color.codereBackground));
        Toast.makeText(this, "Great! The " + message + " Permission is granted", Toast.LENGTH_LONG).show();

    }

    private void deniedToast(String message, Button btn) {
        btn.setBackgroundColor(this.getApplicationContext().getResources().getColor(R.color.error));
        Toast.makeText(this, "Sorry! The " + message + " Permission is denied", Toast.LENGTH_LONG).show();
    }
}
