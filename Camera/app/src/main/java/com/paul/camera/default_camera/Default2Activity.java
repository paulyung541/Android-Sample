package com.paul.camera.default_camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.paul.camera.BaseActivity;
import com.paul.camera.BuildConfig;
import com.paul.camera.R;
import com.paul.camera.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 拍摄照片存储，并展示照片
 */
public class Default2Activity extends BaseActivity {
    File imgFile;
    ImageView imageView;
    View loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default2);

        imageView = (ImageView) findViewById(R.id.imageView);
        loading = findViewById(R.id.loading);
    }

    public void onClick(View view) {
        createNewFile();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriCompat());
        startActivityForResult(intent, 0x01);
    }

    private void createNewFile() {
        String tmp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + tmp + ".jpg";
        imgFile = new File(fileName);
        try {
            imgFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Uri getUriCompat() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            return Uri.fromFile(imgFile);
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", imgFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0x01) {
            loading.setVisibility(View.VISIBLE);
            loading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));

            new Thread() {
                @Override
                public void run() {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    int degree = Utils.readPictureDegree(imgFile.getAbsolutePath());
                    final Bitmap bmp = Utils.rotaingImageView(degree, bitmap);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.clearAnimation();
                            loading.setVisibility(View.GONE);
                            imageView.setBackgroundResource(0);
                            imageView.setImageBitmap(bmp);
                        }
                    });
                }
            }.start();
        }
    }
}
