package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by takuma on 2014/05/03.
 */
public class CameraView extends SurfaceView implements Callback{
    private Camera camera;
    private Camera.Parameters parameters;
    private SurfaceHolder v_holder;

    public CameraView(Context context){
        super(context);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
        //set preview size
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setRotation(90);
        //set parameter
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        //start preview
        camera.startPreview();

        v_holder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //open main camera
        camera = Camera.open(0);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
    }
}
