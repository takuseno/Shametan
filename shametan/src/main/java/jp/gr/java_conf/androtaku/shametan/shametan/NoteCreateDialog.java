package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;

/**
 * Created by takuma on 2014/06/27.
 */
public class NoteCreateDialog extends DialogFragment{
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");

    ImageButton blueButton,greenButton,pinkButton
            ,yellowButton,violetButton,orangeButton;
    EditText noteTitleEdit;
    Button okButton,cancelButton;

    SelectNoteFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View content = inflater.inflate(R.layout.note_create_dialog,null);
        init(content);
        return content;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("ノートの作成");
        return dialog;
    }

    public void init(View v){
        blueButton = (ImageButton)v.findViewById(R.id.note_blue_button);
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        greenButton = (ImageButton)v.findViewById(R.id.note_green_button);
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        pinkButton = (ImageButton)v.findViewById(R.id.note_pink_button);
        pinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        yellowButton = (ImageButton)v.findViewById(R.id.note_yellow_button);
        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        violetButton = (ImageButton)v.findViewById(R.id.note_violet_button);
        violetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        orangeButton = (ImageButton)v.findViewById(R.id.note_green_button);
        orangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        noteTitleEdit = (EditText)v.findViewById(R.id.note_title_edit);

        okButton = (Button)v.findViewById(R.id.note_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPath = basePath + "/" + noteTitleEdit.getText() + ".cst";
                File newDirectory = new File(newPath);
                CSTFileController rootCSTFileController = new CSTFileController(basePath + "/root.cst");
                rootCSTFileController.makeCST(newDirectory);
                rootCSTFileController.saveCSTFile(newDirectory);
                fragment.refreshNoteAdapter();
                dismiss();
            }
        });

        cancelButton = (Button)v.findViewById(R.id.note_dialog_cancel);
        cancelButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setFragment(SelectNoteFragment fragment){
        this.fragment = fragment;
    }
}
