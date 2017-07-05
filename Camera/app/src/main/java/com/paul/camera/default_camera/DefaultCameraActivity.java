package com.paul.camera.default_camera;

import android.os.Bundle;
import android.view.View;

import com.paul.camera.BaseActivity;
import com.paul.camera.R;

public class DefaultCameraActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_camera);
    }

    //拍照返回缩略图
    public void onClick(View view) {
        go(Default1Activity.class);
    }

    //拍照并存储在SD卡中
    public void onClick1(View view) {
        go(Default2Activity.class);
    }
}
