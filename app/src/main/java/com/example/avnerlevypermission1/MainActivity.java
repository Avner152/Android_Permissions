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
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity<x> extends AppCompatActivity {

    public final int PERMISSION_FOR_SEND_SMS = 0;
    public final int PERMISSION_FOR_CALL_PHONE = 1;
    public final int PERMISSION_FOR_CAMERA = 2;
    AlertDialog.Builder builder;

    private int counter = 0, x = 1, y = 1, z = 1;
    private float battery = 0;
    private int hasFlash = -1;
    private Camera camera;
    private CameraManager cameraManager;
    private Switch flashFlicker;

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
           battery = level * 100 / (float) scale;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        listeners();
        parameters();
    }

    private void parameters() {
        //       Get Battery Percentage:
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //        Get Flash Data
        hasFlash = checkFlashMode();
        Log.i("TAG", "init: "+ hasFlash);
    }

    private void init() {
        logInBtn = findViewById(R.id.logIn_btn);
        callBtn = findViewById(R.id.call_btn);
        smsBtn = findViewById(R.id.sms_btn);
        camBtn = findViewById(R.id.camera_btn);
        resetBtn = findViewById(R.id.resetBtn);
        flashFlicker = findViewById(R.id.switch_flash_main);
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        builder = new AlertDialog.Builder(this);
    }

    private void permissions(int permissionNumber) {
        if (!hasPermission(permissionNumber, this, permissionsList))
            ActivityCompat.requestPermissions(this, permissionsList, permissionNumber);
        else {
            ActivityCompat.requestPermissions(this, permissionsList, permissionNumber);
        }
    }

    public int checkFlashMode(){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            flashFlicker.setEnabled(true);
                } else{
                Toast.makeText(MainActivity.this, "Your Device Doesn't Support Flash.", Toast.LENGTH_SHORT).show();
                return -1;
            }
        } else {
            Toast.makeText(MainActivity.this, "Your Device Doesn't Support Camera.", Toast.LENGTH_SHORT).show();
            return -1;
        }
        flashFlicker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String[] camID = {};
                try {
                    camID = cameraManager.getCameraIdList();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

                if(isChecked){
                    try {
                        cameraManager.setTorchMode(camID[0],false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    flashFlicker.setText("Flash ON");
                } else{
                    try {
                        cameraManager.setTorchMode(camID[1],true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    flashFlicker.setText("Flash OFF");
                }
            }
        });
        return 0;
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
            if(counter < 3)
                Toast.makeText(getApplicationContext(), "You need to grant all three permissions in order to go on ",
                        Toast.LENGTH_SHORT).show();
            else if(battery < 50)
                Toast.makeText(getApplicationContext(), "You don't Have enough battery to go on. please Plug-in or chage your phone ",
                        Toast.LENGTH_SHORT).show();
            else{
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
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
                    updateUI(x, callBtn);
                    x = 0;
                    grantedToast("Call-Phone");
                } else
                    deniedToast("Call-Phone", callBtn);

                break;

            case 1:
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    updateUI(y, smsBtn);
                    y = 0;
                    grantedToast("SMS");
                } else
                    deniedToast("SMS", smsBtn);
                break;

            case 2:
                if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    updateUI(z, camBtn);
                    z = 0;
                    grantedToast("Camera");
                } else
                    deniedToast("Camera", camBtn);
                break;
            default:
                Log.i("TAG", "Default!");
                break;
        }
    }

    private void updateUI(int variable, Button btn) {
        if(counter + variable > counter)
            btn.setBackgroundColor(this.getApplicationContext().getResources().getColor(R.color.codereBackground));
        counter += variable;
    }

    @SuppressLint("ResourceAsColor")
    private void grantedToast(String message) {
        Toast.makeText(this, "Great! The " + message + " Permission is granted", Toast.LENGTH_LONG).show();
    }

    private void deniedToast(String message, Button btn) {
        btn.setBackgroundColor(this.getApplicationContext().getResources().getColor(R.color.error));
        Toast.makeText(this, "Sorry! The " + message + " Permission is denied", Toast.LENGTH_LONG).show();
    }
}
