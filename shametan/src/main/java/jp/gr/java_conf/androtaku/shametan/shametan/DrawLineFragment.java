package jp.gr.java_conf.androtaku.shametan.shametan;

import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    private float PIXEL_LIMIT;

    public DrawLineFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.drawline_layout,container,false);

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

        rootView.post(new Runnable() {
            @Override
            public void run() {
                drawLineView.putDispWidth(rootView.getWidth());
                drawLineView.putDispHeight(rootView.getHeight());
                PIXEL_LIMIT = rootView.getWidth() * rootView.getHeight() * 3;
                background.setImageBitmap(fitImage(filePath));
                background.post(new Runnable() {
                    @Override
                    public void run() {
                        drawLineView.putImageWidth(background.getWidth());
                        drawLineView.putImageHeight(background.getHeight());
                        drawLineView.init();
                        frameLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                frameLayout.setDrawingCacheEnabled(true);
                                drawLineView.putBackgroundCash(frameLayout.getDrawingCache());
                            }
                        });
                    }
                });
            }
        });

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

        frameLayout = (FrameLayout)v.findViewById(R.id.drawline_frameLayout);

        drawLineView = new DrawLineView(getActivity(),filePath);
        frameLayout.addView(drawLineView);
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

    public Bitmap fitImage(String imageName){
        Bitmap bmpSrc;
        bmpSrc = BitmapFactory.decodeFile(imageName);
        float srcWidth = bmpSrc.getWidth();
        float srcHeight = bmpSrc.getHeight();

        float rsz_ratio = (float)Math.sqrt(PIXEL_LIMIT / (srcWidth * srcHeight));
        Log.i("ratio", String.valueOf(rsz_ratio));
        Matrix matrix = new Matrix();

        matrix.postScale(rsz_ratio,rsz_ratio);

        Bitmap bmpRsz = Bitmap.createBitmap(bmpSrc,0,0,bmpSrc.getWidth(),
                bmpSrc.getHeight(),matrix,true);
        return bmpRsz;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.save_line:
                drawLineView.exportData();
                CSTFileController cstFileController = new CSTFileController(getArguments().getString("cst_file"));
                cstFileController.saveCSTFile(new File(filePath));
                if(getActivity().getClass() == GetImageFromCameraActivity.class
                        || getActivity().getClass() == GetImageFromGalleryActivity.class){
                    getActivity().finish();
                }
                else{
                    getFragmentManager().popBackStack();
                }
                break;

            default:
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
