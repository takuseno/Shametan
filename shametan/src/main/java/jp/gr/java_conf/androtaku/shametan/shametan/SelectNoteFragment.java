package jp.gr.java_conf.androtaku.shametan.shametan;

/**
 * Created by takuma on 2014/05/31.
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import java.io.File;

/**
 * Created by takuma on 2014/05/30.
 */
public class SelectNoteFragment extends Fragment {
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");
    GridView gridNoteView,gridPageView;
    NoteGridAdapter noteAdapter;
    PageGridAdapter pageAdapter;
    CSTFileController cstFileController;
    File[] noteFiles;
    File[] pageFiles;

    public static SelectNoteFragment newInstance(){
        SelectNoteFragment fragment = new SelectNoteFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.note_select_layout,container,false);
        init(rootView);
        setHasOptionsMenu(true);
        return rootView;
    }

    public void init(View v){
        cstFileController = new CSTFileController(basePath + getArguments().getString("cst_file"));
        cstFileController.importCSTFile();

        noteFiles = cstFileController.getNoteFiles();
        gridNoteView = (GridView)v.findViewById(R.id.noteList);
        gridNoteView.setNumColumns(4);
        noteAdapter = new NoteGridAdapter(getActivity(),R.layout.select_note_item,noteFiles);
        gridNoteView.setAdapter(noteAdapter);
        gridNoteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toSelectNote("/" + noteFiles[position].getName());
            }
        });

        pageFiles = cstFileController.getPageFiles();
        gridPageView = (GridView)v.findViewById(R.id.pageList);
        gridPageView.setNumColumns(2);
        pageAdapter = new PageGridAdapter(getActivity(),R.layout.grid_items,pageFiles);
        gridPageView.setAdapter(pageAdapter);
        gridPageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = pageFiles[position].getPath().lastIndexOf(".");
                String dataPath = pageFiles[position].getPath().substring(0, index) + ".st";
                toNote(dataPath);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //refreshNoteAdapter();
    }

    public void refreshNoteAdapter(){
        cstFileController.importCSTFile();

        noteFiles = new File[cstFileController.getNoteFiles().length];
        noteFiles = cstFileController.getNoteFiles();
        noteAdapter = new NoteGridAdapter(getActivity(),R.layout.select_note_item,noteFiles);
        gridNoteView.setAdapter(noteAdapter);

        pageFiles = new File[cstFileController.getPageFiles().length];
        pageFiles = cstFileController.getPageFiles();
        pageAdapter = new PageGridAdapter(getActivity(),R.layout.grid_items,pageFiles);
        gridPageView.setAdapter(pageAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.add_directory:
                final EditText inputName = new EditText(getActivity());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("ノートの追加")
                        .setView(inputName)
                        .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newPath = basePath + "/" + inputName.getText() + ".cst";
                                File newDirectory = new File(newPath);
                                cstFileController.makeCST(newDirectory);
                                cstFileController.saveCSTFile(newDirectory);
                                //refreshNoteAdapter();
                            }
                        })
                        .setNegativeButton("キャンセル", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.add_from_camera:
                Intent intent = new Intent(getActivity().getApplicationContext(),GetImageFromCameraActivity.class);
                intent.putExtra("cst_file",basePath + getArguments().getString("cst_file"));
                getActivity().startActivity(intent);
                break;

            case R.id.add_from_gallery:

                break;

            default:
        }
        return true;

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

    public void toSelectNote(String fileName){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("cst_file",fileName);
        SelectNoteFragment fragment = new SelectNoteFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack("noteselect");
        transaction.commit();
    }
}
