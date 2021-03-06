package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by takuma on 2014/05/03.
 */
public class CameraView extends SurfaceView implements Callback,Camera.PictureCallback,Camera.AutoFocusCallback{

    //declare camera
    private Camera camera;
    //declare fragmentmanager
    FragmentManager manager;
    Context context;
    //declare String of cst file path
    String cstPath;

    private static int orientation = 1;
    private final static int ORIEN_VERTICAL = 1;
    private final static int ORIEN_HORIZON = 2;

    //declare File of base directory path
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");

    private boolean isTaking = false;
    private boolean isFocused = false;

    //constructor
    public CameraView(Context context,FragmentManager manager,String cstPath){
        super(context);
        this.context = context;
        this.manager = manager;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        this.cstPath = cstPath;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        Camera.Parameters parameters = camera.getParameters();

        List<Camera.Size> supportedSize = parameters.getSupportedPictureSizes();
        int id = 0;
        int product = 0;
        for(int i = 0;i < supportedSize.size();++i){
            Camera.Size size = supportedSize.get(i);
            if(product > size.width * size.height){
                product = size.width * size.height;
                id = i;
            }
        }
        Camera.Size size = supportedSize.get(id);
        parameters.setPictureSize(size.width,size.height);

        //rotate camera preview because of vertical handed as default
        parameters.setRotation(90);
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        //get max size
        Camera.Size previewSize = previewSizes.get(1);
        //set preview size
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        //set parameter
        camera.setParameters(parameters);
        setCameraDisplayOrientation(context,0,camera);
        adjustPreview(size.width,size.height,width,height);
        //start preview
        camera.startPreview();
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

    public static void setCameraDisplayOrientation(Context context,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                orientation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                orientation = ORIEN_HORIZON;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                orientation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                orientation = ORIEN_HORIZON;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    public void myAutoFocus(boolean isTaking){
        this.isTaking = isTaking;
        if(isTaking && isFocused){
            camera.takePicture(mShutterListener,null,this);
        }
        else {
            camera.autoFocus(this);
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        isFocused = success;
        if(isTaking) {
            camera.takePicture(mShutterListener, null, this);
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //temporary image file path
        String imagePath = basePath.getPath() + "/temp.jpg";
        //save image
        saveImage(data,new File(imagePath));
        //fragment transition to TrimFragment
        toTrim(imagePath);
    }

    private Camera.ShutterCallback mShutterListener = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    //function of saving image
    public void saveImage(byte[] data,File path){
        FileOutputStream fileOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(path);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        try{
            fileOutputStream.write(data);
        }catch(IOException e){
            e.printStackTrace();
        }
        try {
            fileOutputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void adjustPreview(int width,int height,int dispWidth,int dispHeight){
        float scale = 1f;

        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {// 縦表示なら
            scale = (float) dispWidth / (float) height;
            layoutParams.height = (int) (width * scale);
            layoutParams.width = (int) (height * scale);
        } else { // 横なら
            scale = (float) dispHeight / (float) height;
            layoutParams.height = (int) (height * scale);
            layoutParams.width = (int) (width * scale);
        }

        this.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            myAutoFocus(false);
        }
        return true;
    }

    //function of fragment transition to TrimFragment
    public void toTrim(String imagePath){
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        //put temporary image path
        bundle.putString("image_path",imagePath);
        //put whether from camera or not
        bundle.putString("from","camera");
        //put loaded cst file path
        bundle.putString("cst_path",cstPath);
        bundle.putInt("orientation",orientation);
        TrimFragment fragment = new TrimFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("camera");
        transaction.commit();
    }
}

