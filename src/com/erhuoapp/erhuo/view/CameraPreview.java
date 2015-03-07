package com.erhuoapp.erhuo.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * A basic Camera preview class
 * guide/topics/media/camera.html#custom-camera
 *
 * @author hujiawei
 * @datetime 2015/1/14
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private final String TAG = "CameraPreview";

    private Camera mCamera;
    private SurfaceHolder mHolder;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            //hujiawei 默认是横着的，给个90度让它转过来
            //A camera preview does not have to be in landscape mode.
            //Starting in Android 2.2 (API Level 8), you can use the setDisplayOrientation() method
            //to set the rotation of the preview image.

            mCamera.setDisplayOrientation(90);
            //NOTE: 这个必须要有，否则预览图像会旋转90度，但是拍照结果还是旋转了90度的

            Camera.Parameters params = mCamera.getParameters();
            params.setPictureFormat(PixelFormat.JPEG);//图片格式
            params.setPreviewSize(640, 480);//图片大小
            mCamera.setParameters(params);//将参数设置到我的camera

            //Once you obtain access to a camera, you can get further information about its capabilities using
            //the Camera.getParameters() method and checking the returned Camera.Parameters object for supported capabilities.
            //When using API Level 9 or higher, use the Camera.getCameraInfo() to determine if a camera is on the front or back of the device,
            //and the orientation of the image.
            //Camera.CameraInfo.CAMERA_FACING_BACK

            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}