package com.paul.camera.custom;

import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.paul.camera.R;
import com.paul.camera.utils.LogUtil;
import com.paul.camera.widget.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 自定义拍照并存储：解决了角度问题
 * 所有的照片其实都是按照传感器设置进行存储的，比如后置摄像头，以竖屏为例，就是以偏转90°来存储的，只不过相册类应用可以读取
 * ExifInterface 这个类，来进行偏转，这个类的信息是设置 Camera 的 Parameters 来进行设置的。
 * 比如设置 parameter.setRotation(90)，则 ExifInterface 的 TAG_ORIENTATION 属性就会设置成 90
 * 到时候相册app可以读取此值进行旋转操作，使图片能够以正常角度预览
 * 根据传感器，判断此时的方向，设置 parameter.setRotation，或者利用算法旋转存储的数据
 * <p>
 * 去网上找个根据传感器数据设置 parameter.setRotation 的算法，因为自己测的有点不准确，且麻烦
 */
public class CustomCamera3Activity extends CustomCamera2Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor acc_sensor;
    private Sensor mag_sensor;

    //加速度传感器数据
    float accValues[] = new float[3];
    //地磁传感器数据
    float magValues[] = new float[3];
    //旋转矩阵，用来保存磁场和加速度的数据
    float r[] = new float[9];
    //模拟方向传感器的数据（原始数据为弧度）
    float values[] = new float[3];

    float x, y, z;

    private TextView mPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera3);

        mPrint = (TextView) findViewById(R.id.chuanganqi);

        cameraPreview.setScale(CameraPreview.SCALE_4_3);
        ///

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //给传感器注册监听：
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mag_sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        }
        SensorManager.getRotationMatrix(r, null, accValues, magValues);
        SensorManager.getOrientation(r, values);

        float tmpx = (float) Math.toDegrees(values[0]);
        float tmpy = (float) Math.toDegrees(values[1]);
        float tmpz = (float) Math.toDegrees(values[2]);

        if (Math.abs(tmpx - x) > 2) {
            x = tmpx;
            LogUtil.df("CustomCamera3Activity#onSensorChanged() : x = %f , y = %f , z = %f", x, y, z);
            mPrint.setText(String.format("x = %.1f , y = %.1f , z = %.1f", x, y, z));
        }
        if (Math.abs(tmpy - y) > 2) {
            y = tmpy;
            LogUtil.df("CustomCamera3Activity#onSensorChanged() : x = %f , y = %f , z = %f", x, y, z);
            mPrint.setText(String.format("x = %.1f , y = %.1f , z = %.1f", x, y, z));
        }
        if (Math.abs(tmpz - z) > 2) {
            z = tmpz;
            LogUtil.df("CustomCamera3Activity#onSensorChanged() : x = %f , y = %f , z = %f", x, y, z);
            mPrint.setText(String.format("x = %.1f , y = %.1f , z = %.1f", x, y, z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View view) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //设置相机参数
                Camera.Parameters parameter = mCamera.getParameters();
                int rotation = 90;
                if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    rotation = 270;

                //// TODO: 2017/6/15 添加偏移量，使照片角度参数设置正确

                parameter.setRotation(rotation);//前置摄像头就270， 后置的就90

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
                    Toast.makeText(CustomCamera3Activity.this, "出错了", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(CustomCamera3Activity.this, "照片已存储至：" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                mCamera.startPreview();
            }
        });
    }
}
