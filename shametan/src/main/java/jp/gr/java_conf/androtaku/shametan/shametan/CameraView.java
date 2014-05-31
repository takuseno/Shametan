package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by takuma on 2014/05/03.
 */
public class CameraView extends SurfaceView implements Callback,Camera.AutoFocusCallback,Camera.PictureCallback{
    private Camera camera;
    Context context;
    FragmentManager manager;

    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");

    public CameraView(Context context,FragmentManager manager){
        super(context);
        this.context = context;
        this.manager = manager;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        Camera.Parameters parameters = camera.getParameters();
        parameters.setRotation(90);
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
        //set preview size
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        //set parameter
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
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

    public void myAutoFocus(){
        camera.autoFocus(this);
    }

    @Override
    public void onAutoFocus(boolean success,Camera camera){
        camera.takePicture(null,null,this);
    }

    @Override
    public void onPictureTaken(byte[] data,Camera camera){
        if(!basePath.exists()){
            if(!basePath.mkdir()){
                Toast.makeText(context,"failed to make a root folder",Toast.LENGTH_SHORT).show();
            }
            File noMedia = new File(basePath + "/.nomedia");
            try {
                if(!noMedia.createNewFile()){
                    Toast.makeText(context,"failed to make .nomedia",Toast.LENGTH_SHORT).show();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        String imagePath = basePath.getPath() + "/test.jpg";
        saveImage(data,new File(imagePath));

        toTrim(imagePath);
    }

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

    public void toTrim(String imagePath){
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("image_path",imagePath);
        bundle.putString("from","camera");
        TrimFragment fragment = new TrimFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"trim_fragment");
        transaction.addToBackStack("camera");
        transaction.commit();
    }
}
