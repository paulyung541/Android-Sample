package com.paul.camera.application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.paul.camera.BaseActivity;
import com.paul.camera.R;
import com.paul.camera.utils.Utils;
import com.paul.qrlib.activity.CaptureActivity;
import com.paul.qrlib.encoding.EncodingUtils;

/**
 * Camera应用之
 * 利用 自定义 View 实现二维码扫描
 */
public class AppCamera1Activity extends BaseActivity {

    TextView mTvResult;
    ImageView mImgCreate;
    EditText mEtInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_camera1);

        mTvResult = (TextView) findViewById(R.id.tv_scan_result);
        mImgCreate = (ImageView) findViewById(R.id.img_create);
        mEtInput = (EditText) findViewById(R.id.editText);
    }

    //前往扫描
    public void scan(View view) {
        goResult(CaptureActivity.class, 0x01);
    }

    //生成二维码
    public void create(View view) {
        int width = Utils.dip2px(this, 200);
        Bitmap bitmap = EncodingUtils.createQRCode(mEtInput.getText().toString(), width, width, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        mImgCreate.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0x01) {
            mTvResult.setText(data.getExtras().getString("result"));
        }
    }
}
