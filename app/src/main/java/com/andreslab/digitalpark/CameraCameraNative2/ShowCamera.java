package com.andreslab.digitalpark.CameraCameraNative2;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceHolder holder;

    public ShowCamera(Context context, Camera camera){
        super(context);

        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size msize = null;

        for (Camera.Size size : sizes){
            msize = size;
        }


        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            params.set("orientation","landscape");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
        }else{
            params.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);

        }

        params.setPictureSize(msize.width, msize.height);
        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }
}
