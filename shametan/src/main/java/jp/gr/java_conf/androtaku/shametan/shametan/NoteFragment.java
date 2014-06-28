package jp.gr.java_conf.androtaku.shametan.shametan;

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
import android.view.View;
import android.view.ViewGroup;
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
        final View rootView = inflater.inflate(R.layout.note_layout,container,false);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.note));

        backgroundPath = getArguments().getString("file_path");
        int index = backgroundPath.lastIndexOf(".");
        filePath = backgroundPath.substring(0,index) + ".st";

        rootView.post(new Runnable() {
            @Override
            public void run() {
                noteView.putDispWidth(rootView.getWidth());
                noteView.putDispHeight(rootView.getHeight());
                background.post(new Runnable() {
                    @Override
                    public void run() {
                        noteView.putImageWidth(background.getWidth());
                        Log.i("imagewidth",""+background.getWidth());
                        noteView.putImageHeight(background.getHeight());
                        Log.i("imageHeight",""+background.getHeight());
                        noteView.init();
                    }
                });
            }
        });

        init(rootView);

        NotebookActivity.menuType = NotebookActivity.MENU_NOTE;
        //getFragmentManager().invalidOptionsMenu();
        setHasOptionsMenu(true);

        return rootView;
    }

    public void init(View v){
        background = (ImageView)v.findViewById(R.id.noteBackground);
        background.setImageBitmap(BitmapFactory.decodeFile(backgroundPath));
        noteView = new NoteView(getActivity().getApplicationContext(), filePath);
        frameLayout = (FrameLayout)v.findViewById(R.id.note_framelayout);
        frameLayout.addView(noteView);
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
