package jp.gr.java_conf.androtaku.shametan.shametan;

import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

    private float PIXEL_LIMIT;

    public NoteFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.note_layout,container,false);

        filePath = getArguments().getString("file_path");
        int index = filePath.lastIndexOf(".");
        backgroundPath = filePath.substring(0,index) + ".jpg";

        rootView.post(new Runnable() {
            @Override
            public void run() {
                noteView.putDispWidth(rootView.getWidth());
                noteView.putDispHeight(rootView.getHeight());
                PIXEL_LIMIT = rootView.getWidth() * rootView.getHeight() * 3;
                background.setImageBitmap(fitImage(backgroundPath));
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
        setHasOptionsMenu(true);

        return rootView;
    }

    public void init(View v){
        background = (ImageView)v.findViewById(R.id.noteBackground);
        noteView = new NoteView(getActivity().getApplicationContext(), filePath);
        frameLayout = (FrameLayout)v.findViewById(R.id.note_framelayout);
        frameLayout.addView(noteView);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        NotebookActivity.menuType = NotebookActivity.MENU_SELECT_NOTE;
        super.onDestroy();
    }

    public Bitmap fitImage(String imageName){
        Bitmap bmpSrc;
        bmpSrc = BitmapFactory.decodeFile(imageName);
        float srcWidth = bmpSrc.getWidth();
        float srcHeight = bmpSrc.getHeight();

        float rsz_ratio = (float)Math.sqrt(PIXEL_LIMIT / (srcWidth * srcHeight));
        Log.i("ratio",String.valueOf(rsz_ratio));
        Matrix matrix = new Matrix();

        matrix.postScale(rsz_ratio,rsz_ratio);

        Bitmap bmpRsz = Bitmap.createBitmap(bmpSrc,0,0,bmpSrc.getWidth(),
                bmpSrc.getHeight(),matrix,true);
        return bmpRsz;
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
        fragment.setArguments(bundle);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("note");
        transaction.commit();
    }
}
