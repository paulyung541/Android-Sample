package com.paul.http;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.paul.http.get.GETActivity;
import com.paul.http.post.BasePostActivity;

/**
 * 服务器地址：http://39.108.177.76/ --> 阿里云
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0x01);
        }
    }

    public void onClick(View view) {
        go(GETActivity.class);
    }

    public void onClick2(View view) {
        go(BasePostActivity.class);
    }
}
