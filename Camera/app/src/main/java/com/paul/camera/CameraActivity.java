package com.paul.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

/**
 * Created by yang on 2017/6/16.
 * paulyung@outlook.com
 */

public class CameraActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
