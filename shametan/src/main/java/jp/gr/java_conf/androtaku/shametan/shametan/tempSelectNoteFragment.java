package jp.gr.java_conf.androtaku.shametan.shametan;

/**
 * Created by takuma on 2014/05/31.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.io.File;

/**
 * Created by takuma on 2014/05/30.
 */
public class TempSelectNoteFragment extends Fragment {
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");
    GridView gridView;
    FileSearch fileSearch = new FileSearch();

    File[] imageFiles;

    public static TempSelectNoteFragment newInstance(){
        TempSelectNoteFragment fragment = new TempSelectNoteFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.tempnoteselect,container,false);
        init(rootView);
        return rootView;
    }

    public void init(View v){
        gridView = (GridView)v.findViewById(R.id.noteList);
        gridView.setNumColumns(2);
        //fileSearch.searchFolder(basePath,".jpg",".JPG",".png",".PNG");
        CSTFileController cstFileController = new CSTFileController(basePath + "/root.cst");
        cstFileController.importCSTFile();
        //ArrangeImages arrangeImages = new ArrangeImages(fileSearch.getFileList());
        imageFiles = cstFileController.getFiles();
        final NoteGridAdapter adapter = new NoteGridAdapter(getActivity(),R.layout.grid_items,imageFiles);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = imageFiles[position].getPath().lastIndexOf(".");
                String dataPath = imageFiles[position].getPath().substring(0, index) + ".st";
                toNote(dataPath);
            }
        });
    }

    public void toNote(String filePath){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("file_path",filePath);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"note_fragment");
        transaction.addToBackStack("notelist");
        transaction.commit();
    }
}
