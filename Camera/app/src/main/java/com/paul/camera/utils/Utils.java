package com.paul.camera.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Surface;

import com.paul.camera.BaseActivity;

import java.io.IOException;

/**
 * Created by yang on 2017/6/12.
 * paulyung@outlook.com
 */

public class Utils {
    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    //根据前置 或者 后置摄像头，获取预览应该旋转的角度
    public static int getDisplayOrientation(int cameraId, Activity activity) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    //打印摄像头信息
    public static void printCameraInfo() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        Log.d(BaseActivity.TAG, "printCameraInfo: 摄像头数量为 ：" + cameraNum);
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                Log.d(BaseActivity.TAG, "printCameraInfo: 前置摄像头，默认预览角度为：" + info.orientation);
            else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                Log.d(BaseActivity.TAG, "printCameraInfo: 后置摄像头，默认预览角度为：" + info.orientation);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
