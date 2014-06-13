package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * Created by takuma on 2014/05/09.
 */
public class DrawLineOptionDialog extends DialogFragment{
    //declare views
    ImageButton redButton,greenButton,blueButton;
    SeekBar lineSizeSeek;
    Button deleteButton,okButton,cancelButton;

    private int lineColor;
    private int lineSize;

    private int sourceColor;
    private int sourceSize;

    private boolean changeFragColor = false;
    private boolean changeFragSize = false;
    private boolean deleteFrag = false;

    private int red = Color.argb(100, 255, 0, 0);
    private int green = Color.argb(100,0,255,0);
    private int blue = Color.argb(100,0,0,255);

    DrawLineView drawLineView;

    public void setValue(int lineSize,int lineColor,DrawLineView view){
        drawLineView = view;

        sourceSize = lineSize;
        sourceColor = lineColor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View content = inflater.inflate(R.layout.drawline_option_dialog,null);
        init(content);

        return content;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = new Dialog(getActivity(),R.style.MyDialogTheme);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(wmlp);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public void init(View v){
        lineSizeSeek = (SeekBar)v.findViewById(R.id.line_size_seek);
        lineSizeSeek.setProgress(sourceSize);
        lineSizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lineSize = lineSizeSeek.getProgress();
                changeFragSize = true;
                drawLineView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        redButton = (ImageButton)v.findViewById(R.id.option_red_button);
        redButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lineColor = red;
                changeFragColor = true;
                drawLineView.invalidate();
                redButton.setBackgroundResource(R.drawable.red_clicked);
                greenButton.setBackgroundResource(R.drawable.green_option);
                blueButton.setBackgroundResource(R.drawable.blue_option);
                return true;
            }
        });

        greenButton = (ImageButton)v.findViewById(R.id.option_green_button);
        greenButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lineColor = green;
                changeFragColor = true;
                drawLineView.invalidate();
                redButton.setBackgroundResource(R.drawable.red_option);
                greenButton.setBackgroundResource(R.drawable.green_clicked);
                blueButton.setBackgroundResource(R.drawable.blue_option);
                return true;
            }
        });

        blueButton = (ImageButton)v.findViewById(R.id.option_blue_button);
        blueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lineColor = blue;
                changeFragColor = true;
                redButton.setBackgroundResource(R.drawable.red_option);
                greenButton.setBackgroundResource(R.drawable.green_option);
                blueButton.setBackgroundResource(R.drawable.blue_clicked);
                drawLineView.invalidate();
                return true;
            }
        });

        deleteButton = (Button)v.findViewById(R.id.deleteLineButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFrag = true;
                drawLineView.invalidate();
            }
        });

        okButton = (Button)v.findViewById(R.id.dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        cancelButton = (Button)v.findViewById(R.id.dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragSize = true;
                lineSize = sourceSize;
                changeFragColor = true;
                lineColor = sourceColor;
                drawLineView.invalidate();
                getDialog().dismiss();
            }
        });
    }

    public boolean isChangeFragColor(){
        return changeFragColor;
    }

    public void changeFragColorReset(){
        changeFragColor = false;
    }

    public int getChangeColor(){
        return lineColor;
    }

    public boolean isChangeFragSize(){
        return changeFragSize;
    }

    public void changeFragSizeReset(){
        changeFragSize = false;
    }

    public int getChangeSize(){
        return lineSize;
    }

    public boolean isDeleteFrag(){
        return deleteFrag;
    }

    public void changeFragDeleteReset(){
        deleteFrag = false;
    }
}
