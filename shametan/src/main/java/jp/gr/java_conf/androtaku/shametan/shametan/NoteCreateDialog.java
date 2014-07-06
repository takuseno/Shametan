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

    private static final int BLUE = 1;
    private static final int GREEN = 2;
    private static final int PINK = 3;
    private static final int YELLOW = 4;
    private static final int VIOLET = 5;
    private static final int ORANGE = 6;
    int selectedColorId = BLUE;

    private static int NEW = 1;
    private static int EDIT = 2;
    int mode = NEW;

    ImageButton blueButton,greenButton,pinkButton
            ,yellowButton,violetButton,orangeButton;
    EditText noteTitleEdit;
    Button okButton,cancelButton;

    SelectNoteFragment fragment;

    ImageButton selectedButton;

    String cstPath;
    String presetString;

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
        dialog.setTitle(getString(R.string.making_note));
        return dialog;
    }

    public void init(View v){
        blueButton = (ImageButton)v.findViewById(R.id.note_blue_button);
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorId = BLUE;
                resetCircle(selectedButton);
                selectedButton = blueButton;
                blueButton.setBackgroundResource(R.drawable.note_circle_blue_clicked);
            }
        });

        greenButton = (ImageButton)v.findViewById(R.id.note_green_button);
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorId = GREEN;
                resetCircle(selectedButton);
                selectedButton = greenButton;
                greenButton.setBackgroundResource(R.drawable.note_circle_green_clicked);
            }
        });

        pinkButton = (ImageButton)v.findViewById(R.id.note_pink_button);
        pinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorId = PINK;
                resetCircle(selectedButton);
                selectedButton = pinkButton;
                pinkButton.setBackgroundResource(R.drawable.note_circle_pink_clicked);
            }
        });

        yellowButton = (ImageButton)v.findViewById(R.id.note_yellow_button);
        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorId = YELLOW;
                resetCircle(selectedButton);
                selectedButton = yellowButton;
                yellowButton.setBackgroundResource(R.drawable.note_circle_yellow_clicked);
            }
        });

        violetButton = (ImageButton)v.findViewById(R.id.note_violet_button);
        violetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorId = VIOLET;
                resetCircle(selectedButton);
                selectedButton = violetButton;
                violetButton.setBackgroundResource(R.drawable.note_circle_violet_clicked);
            }
        });

        orangeButton = (ImageButton)v.findViewById(R.id.note_orange_button);
        orangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorId = ORANGE;
                resetCircle(selectedButton);
                selectedButton = orangeButton;
                orangeButton.setBackgroundResource(R.drawable.note_circle_orange_clicked);
            }
        });

        noteTitleEdit = (EditText)v.findViewById(R.id.note_title_edit);
        if(mode == EDIT){
            noteTitleEdit.setText(presetString);
        }

        okButton = (Button)v.findViewById(R.id.note_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode == NEW) {
                    String newPath = basePath + "/" + noteTitleEdit.getText().toString() + ".cst";
                    File newDirectory = new File(newPath);
                    CSTFileController rootCSTFileController = new CSTFileController(basePath + "/root.cst");
                    rootCSTFileController.makeCST(newDirectory);
                    rootCSTFileController.saveCSTFile(newDirectory);
                    NoteColorManagement noteColorManagement = new NoteColorManagement();
                    noteColorManagement.makeNCFile(noteTitleEdit.getText().toString(), selectedColorId);
                    fragment.refreshNoteAdapter();
                }
                else{
                    String newPath = basePath + "/" + noteTitleEdit.getText().toString() + ".cst";
                    CSTFileController rootCSTFileController = new CSTFileController(basePath + "/root.cst");
                    rootCSTFileController.reviseCSTFile(new File(cstPath),newPath);
                    NoteColorManagement noteColorManagement = new NoteColorManagement();
                    noteColorManagement.makeNCFile(noteTitleEdit.getText().toString(), selectedColorId);
                    fragment.refreshNoteAdapter();
                }
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

        if(mode == NEW) {
            selectedButton = blueButton;
        }
        else{
            setSelectedButton(selectedColorId);
        }
    }

    public void resetCircle(ImageButton button){
        if(button == blueButton){
            blueButton.setBackgroundResource(R.drawable.note_blue_button);
        }
        else if(button == greenButton){
            greenButton.setBackgroundResource(R.drawable.note_green_button);
        }
        else if(button == pinkButton){
            pinkButton.setBackgroundResource(R.drawable.note_pink_button);
        }
        else if(button == yellowButton){
            yellowButton.setBackgroundResource(R.drawable.note_yellow_button);
        }
        else if(button == violetButton){
            violetButton.setBackgroundResource(R.drawable.note_violet_button);
        }
        else if(button == orangeButton){
            orangeButton.setBackgroundResource(R.drawable.note_orange_button);
        }
    }

    public void setFragment(SelectNoteFragment fragment){
        this.fragment = fragment;
    }

    public void setEditText(String string){
        presetString = string;
    }

    public void setSelectedButton(int colorId){
        switch (colorId){
            case BLUE:
                selectedButton = blueButton;
                blueButton.setBackgroundResource(R.drawable.note_circle_blue_clicked);
                break;
            case GREEN:
                selectedButton = greenButton;
                greenButton.setBackgroundResource(R.drawable.note_circle_green_clicked);
                break;
            case PINK:
                selectedButton = pinkButton;
                pinkButton.setBackgroundResource(R.drawable.note_circle_pink_clicked);
                break;
            case YELLOW:
                selectedButton = yellowButton;
                yellowButton.setBackgroundResource(R.drawable.note_circle_yellow_clicked);
                break;
            case VIOLET:
                selectedButton = violetButton;
                violetButton.setBackgroundResource(R.drawable.note_circle_violet_clicked);
                break;
            case ORANGE:
                selectedButton = orangeButton;
                orangeButton.setBackgroundResource(R.drawable.note_circle_orange_clicked);
                break;
        }
    }

    public void setColor(int color){
        selectedColorId = color;
    }

    public void setMode(int mode){
        this.mode = mode;
    }

    public void setCSTFile(String path){
        cstPath = path;
    }
}
