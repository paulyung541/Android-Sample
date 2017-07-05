package com.paul.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.paul.camera.application.AppCamera1Activity;
import com.paul.camera.custom.CustomCamera1Activity;
import com.paul.camera.custom.CustomCamera2Activity;
import com.paul.camera.custom.CustomCamera3Activity;
import com.paul.camera.default_camera.DefaultCameraActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
    }

    @TargetApi(23)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            showDialog();
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setMessage("需要相机权限 和 外部存储权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.VIBRATE}, 0x01);
                    }
                })
                .create()
                .show();
    }

    public void onClick1(View view) {
        go(DefaultCameraActivity.class);
    }

    public void onClick2(View view) {
        go(CustomCamera1Activity.class);
    }

    public void onClick3(View view) {
        go(CustomCamera2Activity.class);
    }

    public void onClick4(View view) {
        go(CustomCamera3Activity.class);
    }

    public void onClick5(View view) {
        go(AppCamera1Activity.class);
    }
}
