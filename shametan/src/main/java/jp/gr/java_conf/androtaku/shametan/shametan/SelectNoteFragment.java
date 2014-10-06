package jp.gr.java_conf.androtaku.shametan.shametan;

/**
 * Created by takuma on 2014/05/31.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;

/**
 * Created by takuma on 2014/05/30.
 */
public class SelectNoteFragment extends Fragment {
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");
    GridView gridNoteView;
    TextView noNoteText;
    Fab addNoteButton;
    NoteGridAdapter noteAdapter;
    CSTFileController rootCSTFileController;
    File[] noteFiles;
    SelectNoteFragment noteFragment;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    public static SelectNoteFragment newInstance(){
        SelectNoteFragment fragment = new SelectNoteFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.note_select_layout,container,false);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        noteFragment = this;
        actionBar.setTitle(getString(R.string.notes));
        init(rootView);
        setHasOptionsMenu(true);
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isRatingDialog",true) && prefs.getInt("activationCounter",10) == 0) {
            showRatingDialog();
        }
        else{
            int counter = prefs.getInt("activationCounter",10) - 1;
            editor.putInt("activationCounter",counter);
            editor.commit();
        }
        return rootView;
    }

    public void init(View v){
        rootCSTFileController = new CSTFileController(basePath + "/root.cst");
        rootCSTFileController.importCSTFile();
        noteFiles = rootCSTFileController.getNoteFiles();
        gridNoteView = (GridView)v.findViewById(R.id.noteList);
        gridNoteView.setNumColumns(2);
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
                longPressedDialog(noteFiles[position]);
                return true;
            }
        });

        addNoteButton = (Fab)v.findViewById(R.id.addNoteButton);
        addNoteButton.setFabDrawable(getResources().getDrawable(R.drawable.ic_action_new));
        addNoteButton.setFabColor(Color.rgb(63,81,181));
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteCreateDialog dialog = new NoteCreateDialog();
                dialog.setFragment(noteFragment);
                dialog.show(getActivity().getFragmentManager(), "createDialog");
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
        int index = file.getName().indexOf(".");
        String name = file.getName().substring(0,index);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete))
                .setMessage(name + getString(R.string.isdelete))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rootCSTFileController.deleteItem(deleteItem);
                        refreshNoteAdapter();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showRatingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.thank_you))
                .setMessage(getString(R.string.please_review))
                .setPositiveButton(getString(R.string.review), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("isRatingDialog",false);
                        editor.commit();
                        Uri uri = Uri.parse("market://details?id=jp.gr.java_conf.androtaku.shametan.shametan");
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent);
                    }
                })
                .setNeutralButton(getString(R.string.later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putInt("activationCounter",10);
                        editor.commit();
                    }
                })
                .setNegativeButton(getString(R.string.no_more),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("isRatingDialog",false);
                        editor.commit();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.add_directory:
                final EditText inputName = new EditText(getActivity());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.add_note))
                        .setView(inputName)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newPath = basePath + "/" + inputName.getText() + ".cst";
                                File newDirectory = new File(newPath);
                                rootCSTFileController.makeCST(newDirectory);
                                rootCSTFileController.saveCSTFile(newDirectory);
                                refreshNoteAdapter();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null);

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            default:
        }
        return true;

    }

    public void longPressedDialog(final File file){
        final File selectedFile = file;
        final CharSequence[] items = {getString(R.string.delete),getString(R.string.edit)};
        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.editing_note))
                .setItems(items,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                showDeleteDialog(selectedFile);
                                dialog.dismiss();
                                break;
                            case 1:
                                NoteCreateDialog createDialog = new NoteCreateDialog();
                                createDialog.setFragment(noteFragment);
                                int index = file.getName().indexOf(".");
                                createDialog.setEditText(file.getName().substring(0,index));
                                createDialog.setColor(new NoteColorManagement().getColor(file.getName().substring(0,index) + ".ns"));
                                createDialog.setCSTFile(selectedFile.getPath());
                                createDialog.setMode(2);
                                createDialog.show(getActivity().getFragmentManager(), "createDialog");
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        dialog = builder.create();
        dialog.show();
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
