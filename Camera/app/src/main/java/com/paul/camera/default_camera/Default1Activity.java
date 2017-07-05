package com.paul.camera.default_camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.paul.camera.BaseActivity;
import com.paul.camera.R;
/**
 * 摄像头拍照，并返回缩略图
 * */
public class Default1Activity extends BaseActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default1);

        imageView = (ImageView) findViewById(R.id.imageView);
    }

    /**
     * 注意在调用startActivityForResult()方法之前，先调用resolveActivity()，
     * 这个方法会返回能处理该Intent的第一个Activity（译注：即检查有没有能处理这个Intent的Activity）。
     * 执行这个检查非常重要，因为如果在调用startActivityForResult()时，
     * 没有应用能处理你的Intent，应用将会崩溃。所以只要返回结果不为null，使用该Intent就是安全的。
     */
    public void onClick(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 0x01);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x01 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(imageBitmap);
            Log.i(TAG, "width: " + imageBitmap.getWidth() + " --- height: " + imageBitmap.getHeight());
            /**
             * 打印结果：
             * width: 195 --- height: 260
             * 很明显，这只是一张缩略图
             * */
        }
    }
}
