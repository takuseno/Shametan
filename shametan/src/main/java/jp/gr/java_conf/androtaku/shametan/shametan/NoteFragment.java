package jp.gr.java_conf.androtaku.shametan.shametan;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
        setHasOptionsMenu(true);

        return rootView;
    }

    public void init(View v){
        background = (ImageView)v.findViewById(R.id.noteBackground);
        Bitmap bmp = BitmapFactory.decodeFile(backgroundPath);
        background.setImageBitmap(bmp);

        int actionBarHeight = ((ActionBarActivity)getActivity()).getActionBar().getHeight();
        Rect rect = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        WindowManager wm =
                (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT < 13){
            if (disp.getWidth() < disp.getHeight())
                noteView = new NoteView(getActivity().getApplicationContext(), filePath,
                        disp.getWidth(),disp.getHeight(), actionBarHeight, statusBarHeight);
            else
                noteView = new NoteView(getActivity().getApplicationContext(), filePath,
                        disp.getHeight(),disp.getWidth(), actionBarHeight, statusBarHeight);
        }else {
            Point size = new Point();
            disp.getSize(size);
            if (size.x < size.y)
                noteView = new NoteView(getActivity().getApplicationContext(), filePath,
                        size.x, size.y, actionBarHeight, statusBarHeight);
            else
                noteView = new NoteView(getActivity().getApplicationContext(), filePath,
                        size.y, size.x, actionBarHeight, statusBarHeight);
        }

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
        bundle.putInt("orientation", noteView.getOrientation());
        fragment.setArguments(bundle);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("note");
        transaction.commit();
    }
}
