package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * Created by takuma on 2014/05/09.
 */
public class DrawLineOptionDialog extends DialogFragment{
    ImageButton redButton,greenButton,blueButton;
    SeekBar lineSizeSeek;
    Button deleteButton;

    int selected;
    int changeColor;
    int lineSize;

    int initColor;
    int initSize;

    boolean changeFragColor = false;
    boolean changeFragSize = false;
    boolean deleteFrag;

    private int red = Color.argb(100, 255, 0, 0);
    private int green = Color.argb(100,0,255,0);
    private int blue = Color.argb(100,0,0,255);

    DrawLineView drawLineView;

    public void setValue(int selected,int lineSize,int lineColor,DrawLineView view){
        this.selected = selected;
        drawLineView = view;
        this.lineSize = lineSize;

        initSize = lineSize;
        initColor = lineColor;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater)getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.drawline_option_dialog,null);

        builder.setView(content);

        init(content);

        builder.setMessage("調整")
                .setPositiveButton("決定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("キャンセル",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int id){
                        changeFragSize = true;
                        lineSize = initSize;
                        changeFragColor = true;
                        changeColor = initColor;
                        drawLineView.invalidate();
                    }
                });

        return builder.create();
    }

    public void init(View v){
        lineSizeSeek = (SeekBar)v.findViewById(R.id.line_size_seek);
        lineSizeSeek.setProgress(lineSize);
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
                changeColor = red;
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
                changeColor = green;
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
                changeColor = blue;
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
    }

    public boolean isChangeFragColor(){
        return changeFragColor;
    }

    public void changeFragColorReset(){
        changeFragColor = false;
    }

    public int getChangeColor(){
        return changeColor;
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
