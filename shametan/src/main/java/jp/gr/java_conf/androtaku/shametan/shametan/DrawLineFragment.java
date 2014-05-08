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

    public DrawLineFragment(){

    }

    public static DrawLineFragment newInstance(){
        DrawLineFragment fragment = new DrawLineFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.drawline_layout,container,false);

        MainActivity.menuType = MainActivity.MENU_DRAWLINE;
        getFragmentManager().invalidateOptionsMenu();
        init(rootView);
        setHasOptionsMenu(true);
        return rootView;
    }

    public void init(View v){
        background = (ImageView)v.findViewById(R.id.drawline_background);
        Bitmap bmp = BitmapFactory.decodeFile(getArguments().getString("trimed_image_path"));
        background.setImageBitmap(bmp);

        frameLayout = (FrameLayout)v.findViewById(R.id.drawline_frameLayout);

        drawLineView = new DrawLineView(getActivity());
        frameLayout.addView(drawLineView);
    }

    @Override
    public void onDestroyView(){
        MainActivity.menuType = MainActivity.MENU_MAIN;
        getFragmentManager().invalidateOptionsMenu();
        super.onDestroyView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.add_line:

                break;

            default:
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
