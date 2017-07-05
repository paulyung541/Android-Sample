package com.paul.camera.custom;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.paul.camera.CameraActivity;
import com.paul.camera.R;
import com.paul.camera.utils.LogUtil;
import com.paul.camera.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomCamera1Activity extends CameraActivity {
    TextView textView;
    SurfaceView surfaceView;

    //----
    SurfaceHolder mHolder;
    Camera mCamera;

    //---
    int mCurrentCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();
        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        }, 10);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    //安全打开相机
    private boolean safeOpenCamera(int id) {
        releaseCamera();
        try {
            mCamera = Camera.open(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera != null;
    }

    //释放相机
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void initCamera(int cameraId) {
        mCurrentCameraId = cameraId;
        if (!safeOpenCamera(cameraId)) {//打开摄像头
            Log.d(TAG, "initCamera: 打开相机失败");
            return;
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            //设置相机参数
            Camera.Parameters parameter = mCamera.getParameters();
            int rotation = 90;
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
                rotation = 270;
            parameter.setRotation(rotation);//前置摄像头就270， 后置的就90
            mCamera.setParameters(parameter);
            //设置预览方向
            mCamera.setDisplayOrientation(Utils.getDisplayOrientation(cameraId, this));
            mCamera.startPreview();//开始预览
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                LogUtil.d("ControlCameraActivity#onPictureTaken() : data size = " + data.length);
                String formatString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + File.separator + formatString + ".jpg");
                FileOutputStream out;
                try {
                    file.createNewFile();
                    out = new FileOutputStream(file);
                    out.write(data);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CustomCamera1Activity.this, "出错了", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(CustomCamera1Activity.this, "照片已存储至：" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                mCamera.startPreview();
            }
        });
    }

    //切换前后摄像头
    public void change(View view) {
        if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
            initCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        else
            initCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }
}
