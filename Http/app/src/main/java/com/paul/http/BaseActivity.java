package com.paul.http;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yang on 2017/6/12.
 * paulyung@outlook.com
 */

public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "debug_code";

    protected Handler mHandler = new Handler();

    protected void go(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void goResult(Class clazz, int requestCode) {
        startActivityForResult(new Intent(this, clazz), requestCode);
    }
}
