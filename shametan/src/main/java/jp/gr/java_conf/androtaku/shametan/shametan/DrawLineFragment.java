package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

/**
 * Created by takuma on 2014/05/06.
 */
public class DrawLineFragment extends Fragment {
    //declare views
    private ImageView background;
    private DrawLineView drawLineView;
    private FrameLayout frameLayout;

    //declare String of file path
    private String filePath;

    private int orientation;
    private static final int ORIEN_VERTICAL = 1;
    private static final int ORIEN_HORIZON = 2;

    public DrawLineFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.drawline_layout,container,false);

        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //set menu as activity
        if(getActivity().getClass() == GetImageFromCameraActivity.class) {
            GetImageFromCameraActivity.menuType = GetImageFromCameraActivity.MENU_DRAWLINE;
        }
        else if(getActivity().getClass() == GetImageFromGalleryActivity.class) {
            GetImageFromGalleryActivity.menuType = GetImageFromGalleryActivity.MENU_DRAWLINE;
        }
        else if(getActivity().getClass() == NotebookActivity.class){
            NotebookActivity.menuType = NotebookActivity.MENU_DRAWLINE;
        }
        //getFragmentManager().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        //initialize views
        init(rootView);

        return rootView;
    }

    public void init(View v){
        background = (ImageView)v.findViewById(R.id.drawline_background);
        //get trimed image path
        filePath = getArguments().getString("trimed_image_path");
        Bitmap backgroundBmp = BitmapFactory.decodeFile(filePath);
        background.setImageBitmap(backgroundBmp);
        background.post(new Runnable() {
            @Override
            public void run() {
                frameLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        frameLayout.setDrawingCacheEnabled(true);
                        drawLineView.putBackgroundCash(frameLayout.getDrawingCache());
                        frameLayout.addView(drawLineView);
                    }
                });
            }
        });

        frameLayout = (FrameLayout)v.findViewById(R.id.drawline_frameLayout);

        drawLineView = new DrawLineView(getActivity(),filePath);

        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        WindowManager windowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_90:
                orientation = ORIEN_HORIZON;
                break;
            case Surface.ROTATION_180:
                orientation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_270:
                orientation = ORIEN_HORIZON;
                break;
        }
        float dispWidth,dispHeight;
        if(orientation == ORIEN_VERTICAL){
            dispWidth = prefs.getInt("vDispWidth",0);
            dispHeight = prefs.getInt("vDispHeight",0);
        }
        else{
            dispWidth = prefs.getInt("lDispWidth",0);
            dispHeight = prefs.getInt("lDispHeight",0);
        }
        drawLineView.putDispWidth((int)dispWidth);
        drawLineView.putDispHeight((int)dispHeight);

        float bmpWidth = backgroundBmp.getWidth();
        float bmpHeight = backgroundBmp.getHeight();
        if(bmpWidth / bmpHeight > dispWidth / dispHeight){
            float ratio = dispWidth / bmpWidth;
            drawLineView.putImageWidth((int)dispWidth);
            Log.i("imagewidth",""+dispWidth);
            drawLineView.putImageHeight((int)(bmpHeight * ratio));
            Log.i("imageHeight",""+(int)(bmpHeight * ratio));
            drawLineView.init();
        }
        else{
            float ratio = dispHeight / bmpHeight;
            drawLineView.putImageWidth((int)(bmpWidth * ratio));
            Log.i("imagewidth",""+dispWidth * ratio);
            drawLineView.putImageHeight((int)dispHeight);
            Log.i("imageHeight",""+(int)dispHeight);
            drawLineView.init();
        }

        if(getActivity().getClass() == GetImageFromGalleryActivity.class){
            if(orientation == ORIEN_VERTICAL){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else{
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    @Override
    public void onDestroyView(){
        if(getActivity().getClass() == GetImageFromCameraActivity.class) {
            GetImageFromCameraActivity.menuType = GetImageFromCameraActivity.MENU_MAIN;
        }
        else if(getActivity().getClass() == GetImageFromGalleryActivity.class) {
            GetImageFromGalleryActivity.menuType = GetImageFromGalleryActivity.MENU_MAIN;
        }
        else if(getActivity().getClass() == NotebookActivity.class){
            NotebookActivity.menuType = NotebookActivity.MENU_NOTE;
        }

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onDestroyView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.save_line:
                drawLineView.exportData();
                CSTFileController cstFileController = new CSTFileController(getArguments().getString("cst_path"));
                cstFileController.saveCSTFile(new File(filePath));
                if(getActivity().getClass() == GetImageFromCameraActivity.class
                        || getActivity().getClass() == GetImageFromGalleryActivity.class){
                    getActivity().finish();
                }
                else{
                    getFragmentManager().popBackStack();
                }
                break;

            case android.R.id.home:
                getFragmentManager().popBackStack();
                break;

            default:
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
}
