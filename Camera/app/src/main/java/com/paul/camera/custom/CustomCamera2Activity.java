package com.paul.camera.custom;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.paul.camera.CameraActivity;
import com.paul.camera.R;
import com.paul.camera.utils.Utils;
import com.paul.camera.widget.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 使用 CameraPreview 作为 SurfaceView 的父控件解决了拉伸问题
 * 使 SurfaceView 的大小适配 Camera 支持的大小
 */
public class CustomCamera2Activity extends CameraActivity {
    protected CameraPreview cameraPreview;
    protected Camera mCamera;

    //---
    protected int mCurrentCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera2);

        cameraPreview = (CameraPreview) findViewById(R.id.camera_view);
        cameraPreview.setScale(CameraPreview.SCALE_1_1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        cameraPreview.setCamera(mCamera);
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

        //设置相机参数
        Camera.Parameters parameter = mCamera.getParameters();
        int rotation = 90;
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
            rotation = 270;
        parameter.setRotation(rotation);//前置摄像头就270， 后置的就90
        mCamera.setParameters(parameter);
        //设置预览方向
        mCamera.setDisplayOrientation(Utils.getDisplayOrientation(cameraId, this));
    }

    private void changeCamera(int cameraId) {
        initCamera(cameraId);
        cameraPreview.switchCamera(mCamera);
        mCamera.startPreview();
    }

    public void onClick(View view) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
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
                    Toast.makeText(CustomCamera2Activity.this, "出错了", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(CustomCamera2Activity.this, "照片已存储至：" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                mCamera.startPreview();
            }
        });
    }

    public void change(View view) {
        if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
            changeCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        else
            changeCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }
}
