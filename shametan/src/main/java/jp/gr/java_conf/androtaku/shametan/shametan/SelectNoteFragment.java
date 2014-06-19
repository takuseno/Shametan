package jp.gr.java_conf.androtaku.shametan.shametan;

/**
 * Created by takuma on 2014/05/31.
 */

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;

import java.io.File;

/**
 * Created by takuma on 2014/05/30.
 */
public class SelectNoteFragment extends Fragment {
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");
    GridView gridNoteView;
    TextView noNoteText;
    NoteGridAdapter noteAdapter;
    CSTFileController rootCSTFileController;
    File[] noteFiles;

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
        rootCSTFileController = new CSTFileController(basePath + "/root.cst");
        rootCSTFileController.importCSTFile();
        noteFiles = rootCSTFileController.getNoteFiles();
        gridNoteView = (GridView)v.findViewById(R.id.noteList);
        gridNoteView.setNumColumns(3);
        noteAdapter = new NoteGridAdapter(getActivity(),R.layout.select_note_item,noteFiles);
        gridNoteView.setAdapter(noteAdapter);
        gridNoteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toPage(noteFiles[position].getPath());
            }
        });
        gridNoteView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(noteFiles[position]);
                return true;
            }
        });

        noNoteText = (TextView)v.findViewById(R.id.no_notes_text);
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshNoteAdapter();
    }

    public void refreshNoteAdapter(){
        rootCSTFileController.importCSTFile();
        noteFiles = rootCSTFileController.getNoteFiles();
        noteAdapter.refreshData(noteFiles);
        noteAdapter.notifyDataSetChanged();

        if(noteFiles.length == 0){
            noNoteText.setVisibility(View.VISIBLE);
            gridNoteView.setVisibility(View.INVISIBLE);
        }
        else{
            noNoteText.setVisibility(View.INVISIBLE);
            gridNoteView.setVisibility(View.VISIBLE);
        }
    }

    public void showDeleteDialog(File file){
        final File deleteItem = file;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("削除")
                .setMessage("選択したものを削除しますか？")
                .setPositiveButton("削除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rootCSTFileController.deleteItem(deleteItem);
                        refreshNoteAdapter();
                    }
                })
                .setNegativeButton("キャンセル", null);

        AlertDialog dialog = builder.create();
        dialog.show();
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
                                rootCSTFileController.makeCST(newDirectory);
                                rootCSTFileController.saveCSTFile(newDirectory);
                                refreshNoteAdapter();
                            }
                        })
                        .setNegativeButton("キャンセル", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            default:
        }
        return true;

    }

    public void toPage(String filePath){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("cst_path",filePath);
        SelectPageFragment fragment = new SelectPageFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack("notelist");
        transaction.commit();
    }
}
