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
    CSTFileController rootCSTFileController,cstFileController;
    File[] noteFiles;
    File[] pageFiles;
    File selectCST;

    final static int NOTE = 1;
    final static int PAGE = 2;

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

        cstFileController = new CSTFileController(basePath + getArguments().getString("cst_file"));
        cstFileController.importCSTFile();

        noteFiles = rootCSTFileController.getNoteFiles();
        gridNoteView = (GridView)v.findViewById(R.id.noteList);
        gridNoteView.setNumColumns(3);
        noteAdapter = new NoteGridAdapter(getActivity(),R.layout.select_note_item,noteFiles);
        gridNoteView.setAdapter(noteAdapter);
        gridNoteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cstFileController = new CSTFileController(noteFiles[position].getPath());
                refreshPageAdapter();
                selectCST = noteFiles[position];
                noteAdapter.setSelectPosition(position);
                noteAdapter.getView(position,view,parent);
            }
        });
        gridNoteView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(noteFiles[position],NOTE);
                return true;
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
        gridPageView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(pageFiles[position],PAGE);
                return true;
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshNoteAdapter();
        refreshPageAdapter();
    }

    public void refreshNoteAdapter(){
        rootCSTFileController.importCSTFile();
        noteFiles = rootCSTFileController.getNoteFiles();
        noteAdapter.refreshData(noteFiles);
        noteAdapter.notifyDataSetChanged();
    }

    public void refreshPageAdapter(){
        cstFileController.importCSTFile();
        pageFiles = cstFileController.getPageFiles();
        pageAdapter.refreshData(pageFiles);
        pageAdapter.notifyDataSetChanged();
    }

    public void showDeleteDialog(File file, final int pageOrnote){
        final File deleteItem = file;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("削除")
                .setMessage("選択したものを削除しますか？")
                .setPositiveButton("削除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pageOrnote == NOTE) {
                            rootCSTFileController.deleteItem(deleteItem);
                            refreshPageAdapter();
                            refreshNoteAdapter();
                        } else if (pageOrnote == PAGE) {
                            cstFileController.deleteItem(deleteItem);
                            refreshPageAdapter();
                        }
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

            case R.id.add_from_camera:
                Intent intent = new Intent(getActivity().getApplicationContext(),GetImageFromCameraActivity.class);
                intent.putExtra("cst_file",selectCST.getPath());
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
}
