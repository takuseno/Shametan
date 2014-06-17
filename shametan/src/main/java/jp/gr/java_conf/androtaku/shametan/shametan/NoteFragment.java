package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

    public NoteFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.note_layout,container,false);

        filePath = getArguments().getString("file_path");
        int index = filePath.lastIndexOf(".");
        backgroundPath = filePath.substring(0,index) + ".jpg";

        init(rootView);

        NotebookActivity.menuType = NotebookActivity.MENU_NOTE;
        getFragmentManager().invalidateOptionsMenu();
        setHasOptionsMenu(true);

        return rootView;
    }

    public void init(View v){
        background = (ImageView)v.findViewById(R.id.noteBackground);
        Bitmap bmp = BitmapFactory.decodeFile(backgroundPath);
        background.setImageBitmap(bmp);

        int actionBarHeight = getActivity().getActionBar().getHeight();

        WindowManager wm =
                (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        if(size.x < size.y)
            noteView = new NoteView(getActivity().getApplicationContext(),filePath,size.x,size.y,actionBarHeight);
        else
            noteView = new NoteView(getActivity().getApplicationContext(),filePath,size.y,size.x,actionBarHeight);

        frameLayout = (FrameLayout)v.findViewById(R.id.note_framelayout);
        frameLayout.addView(noteView);
    }

    @Override
    public void onResume(){
        super.onResume();
        noteView.refresh();
    }

    @Override
    public void onDestroy(){
        NotebookActivity.menuType = NotebookActivity.MENU_SELECT_NOTE;
        getFragmentManager().invalidateOptionsMenu();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.edit:
                toDrawLine();
                break;

            default:
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void toDrawLine(){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("trimed_image_path",backgroundPath);
        DrawLineFragment fragment = new DrawLineFragment();
        bundle.putString("cst_file",getArguments().getString("cst_file"));
        bundle.putInt("orientation",noteView.getOrientation());
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"drawline_fragment");
        transaction.addToBackStack("note");
        transaction.commit();
    }
}
