package jp.gr.java_conf.androtaku.shametan.shametan;

import android.support.v4.app.Fragment;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by takuma on 2014/05/06.
 */
public class DrawLineFragment extends Fragment {
    //declare views
    ImageView background;
    DrawLineView drawLineView;
    FrameLayout frameLayout;

    //declare String of file path
    String filePath;

    public DrawLineFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        if(getArguments().getInt("orientation") == 2){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if(getArguments().getInt("orientation") == 1){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        View rootView = inflater.inflate(R.layout.drawline_layout,container,false);

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
        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        background.setImageBitmap(bmp);

        frameLayout = (FrameLayout)v.findViewById(R.id.drawline_frameLayout);

        drawLineView = new DrawLineView(getActivity(),filePath,getArguments().getInt("orientation"));
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
