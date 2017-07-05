package com.paul.camera.widget;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.paul.camera.utils.LogUtil;

import java.io.IOException;
import java.util.List;

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
 * to the surface. We need to center the SurfaceView because not all devices have cameras that
 * support preview sizes at the same aspect ratio as the device's display.
 * <p>
 * 这是一个容器，将 SurfaceView 作为子 View，能够自动根据此容器大小，自动调节 SurfaceView 的预览大小，使其不会拉伸变形
 */
public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";
    public static final int SCALE_1_1 = 0;//  1/1  h/w
    public static final int SCALE_4_3 = 1;//  4/3
    public static final int SCALE_16_9 = 2;//

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    private int screenWidth;
    private int SCALE = SCALE_16_9;

    CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceView = new SurfaceView(getContext());
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);

        screenWidth = getScreenWidth();
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }

    //设置比例
    public void setScale(int scaleFlag) {
        SCALE = scaleFlag;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
        setCamera(camera);
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //目标宽高，期望是 4:3 的比例，以宽度为准去算高度，但这个高度可能跟 Camera 支持的宽高有点小误差
        //为了做到 CameraPreview 和 SurfaceView 有一样的高度，则先获取 Camera 支持的高度，然后将最接近目标的高度
        //重新赋值给 CameraPreview 高度
        int height = 0;
        int width = screenWidth;
        switch (SCALE) {
            case SCALE_1_1:
                height = screenWidth;
                break;
            case SCALE_4_3:
                height = (int) (screenWidth * 1.333);
                break;
            case SCALE_16_9:
                height = (int) (screenWidth * 1.777);
        }

        //获取支持的宽高
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            LogUtil.d("CameraPreview#onMeasure() : w = " + mPreviewSize.width + "  h = " + mPreviewSize.height);
        }

        //因为刚刚计算的高度的比例是不太精确的，所以这里重新赋值
        if (SCALE != SCALE_1_1)
            height = mPreviewSize.width;

        LayoutParams params = getLayoutParams();
        if (params.width == LayoutParams.MATCH_PARENT && params.height == LayoutParams.MATCH_PARENT) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            //CameraPreview的宽和高
            final int width = r - l;
            final int height = b - t;

            child.layout(0, 0, width, height);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    //获取建议的预览宽高：注意 Size 的宽高是以横屏为准的，如果是竖屏应用，则 Size 的 width 对应
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        //ratio = Size.sw / Size.sh 得到宽高比
        //如果 目标宽高比 - ratio > ASPECT_TOLERANCE 则放弃此 Size，可以看出 ASPECT_TOLERANCE 是一个调节精度的系数，越小精度越高
        //目标宽高也和设备支持的宽高越接近

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            //           LogUtil.df("CameraPreview#getOptimalPreviewSize() : width = %d, height = %d", size.width, size.height);
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.width - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.width - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            LogUtil.d("CameraPreview#getOptimalPreviewSize() : optimalSize = null");
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.width - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.width - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            requestLayout();

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }

}