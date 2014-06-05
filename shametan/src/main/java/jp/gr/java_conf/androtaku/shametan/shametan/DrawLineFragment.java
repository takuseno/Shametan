package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");
    ImageView background;
    DrawLineView drawLineView;
    FrameLayout frameLayout;

    String fileName;

    public DrawLineFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.drawline_layout,container,false);

        if(getActivity().getClass() == GetImageFromCameraActivity.class) {
            GetImageFromCameraActivity.menuType = GetImageFromCameraActivity.MENU_DRAWLINE;
        }

        if(getActivity().getClass() == GetImageFromGalleryActivity.class) {
            GetImageFromGalleryActivity.menuType = GetImageFromGalleryActivity.MENU_DRAWLINE;
        }

        if(getActivity().getClass() == NotebookActivity.class){
            NotebookActivity.menuType = NotebookActivity.MENU_DRAWLINE;
        }

        getFragmentManager().invalidateOptionsMenu();
        init(rootView);
        setHasOptionsMenu(true);

        return rootView;
    }

    public void init(View v){
        background = (ImageView)v.findViewById(R.id.drawline_background);
        fileName = getArguments().getString("trimed_image_path");
        Bitmap bmp = BitmapFactory.decodeFile(fileName);
        background.setImageBitmap(bmp);

        frameLayout = (FrameLayout)v.findViewById(R.id.drawline_frameLayout);

        drawLineView = new DrawLineView(getActivity(),fileName);
        frameLayout.addView(drawLineView);
    }

    @Override
    public void onDestroyView(){
        if(getActivity().getClass() == GetImageFromCameraActivity.class) {
            GetImageFromCameraActivity.menuType = GetImageFromCameraActivity.MENU_MAIN;
        }
        if(getActivity().getClass() == GetImageFromGalleryActivity.class) {
            GetImageFromGalleryActivity.menuType = GetImageFromGalleryActivity.MENU_MAIN;
        }
        if(getActivity().getClass() == NotebookActivity.class){
            NotebookActivity.menuType = NotebookActivity.MENU_NOTE;
        }
        getFragmentManager().invalidateOptionsMenu();
        super.onDestroyView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.save_line:
                drawLineView.exportData();
                CSTFileController cstFileController = new CSTFileController(getArguments().getString("cst_file"));
                cstFileController.saveCSTFile(new File(fileName));
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
