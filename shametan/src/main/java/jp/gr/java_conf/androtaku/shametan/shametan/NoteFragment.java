package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by takuma on 2014/05/30.
 */
public class NoteFragment extends Fragment {

    ImageView background;
    FrameLayout frameLayout;
    NoteView noteView;

    String filePath,backgroundPath;

    private int orientation;
    private static final int ORIEN_VERTICAL = 1;
    private static final int ORIEN_HORIZON = 2;

    public NoteFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.note_layout,container,false);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.note));

        backgroundPath = getArguments().getString("file_path");
        int index = backgroundPath.lastIndexOf(".");
        filePath = backgroundPath.substring(0,index) + ".st";

        init(rootView);

        NotebookActivity.menuType = NotebookActivity.MENU_NOTE;
        //getFragmentManager().invalidOptionsMenu();
        setHasOptionsMenu(true);

        return rootView;
    }

    public void init(View v){
        background = (ImageView)v.findViewById(R.id.noteBackground);
        Bitmap backgroundBmp = BitmapFactory.decodeFile(backgroundPath);
        background.setImageBitmap(backgroundBmp);
        noteView = new NoteView(getActivity().getApplicationContext(), filePath);
        frameLayout = (FrameLayout)v.findViewById(R.id.note_framelayout);

        frameLayout.addView(noteView);

        SharedPreferences prefs = getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE);
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
        noteView.putDispWidth((int)dispWidth);
        noteView.putDispHeight((int)dispHeight);

        float bmpWidth = backgroundBmp.getWidth();
        float bmpHeight = backgroundBmp.getHeight();
        if(bmpWidth / bmpHeight > dispWidth / dispHeight){
            float ratio = dispWidth / bmpWidth;
            noteView.putImageWidth((int)dispWidth);
            Log.i("imagewidth",""+dispWidth);
            noteView.putImageHeight((int)(bmpHeight * ratio));
            Log.i("imageHeight",""+(int)(bmpHeight * ratio));
            noteView.init();
        }
        else{
            float ratio = dispHeight / bmpHeight;
            noteView.putImageWidth((int)(bmpWidth * ratio));
            Log.i("imagewidth",""+dispWidth * ratio);
            noteView.putImageHeight((int)dispHeight);
            Log.i("imageHeight",""+(int)dispHeight);
            noteView.init();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.edit:
                toDrawLine();
                break;

            case android.R.id.home:
                getParentFragment().getFragmentManager().popBackStack();
                break;
            default:
        }
        return super.onOptionsItemSelected(menuItem);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    public void toDrawLine(){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("trimed_image_path",backgroundPath);
        DrawLineFragment fragment = new DrawLineFragment();
        bundle.putString("cst_path",getArguments().getString("cst_path"));
        fragment.setArguments(bundle);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("note");
        transaction.commit();
    }
}
