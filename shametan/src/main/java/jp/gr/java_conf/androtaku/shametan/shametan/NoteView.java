package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by takuma on 2014/05/30.
 */
public class NoteView extends View{
    Paint[] paintLine;
    Paint[] hPaintLine;

    private float[] x1;
    private float[] y1;
    private float[] x2;
    private float[] y2;

    private float[] hx1;
    private float[] hy1;
    private float[] hx2;
    private float[] hy2;

    private int[] lineWidth;
    private int[] hLineWidth;

    private int[] lineColors;

    private boolean[] hide;

    int numLines;

    private int red = Color.argb(50,255,0,0);
    private int green = Color.argb(50,0,255,0);
    private int blue = Color.argb(50,0,0,255);

    Context context;

    String filePath;

    private int orientation = 1;
    private int stOrientation = 1;
    private static final int ORIEN_VERTICAL = 1;
    private static final int ORIEN_HORIZON = 2;
    private float dispWidth;
    private float dispHeight;
    private float actionBarHeight;

    public NoteView(Context context, String filePath,int width,int height,int actionBarHeight){
        super(context);
        this.filePath = filePath;

        dispWidth = width;
        dispHeight = height;
        this.actionBarHeight = actionBarHeight;

        importFile(filePath);
        this.context = context;

        hide = new boolean[numLines];
        for(int i = 0;i < numLines;++i){
            hide[i] = true;
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        for(int i = 0;i < numLines;++i) {
            //draw line
            if((stOrientation == ORIEN_VERTICAL && orientation == ORIEN_VERTICAL)
                    || (stOrientation == ORIEN_HORIZON && orientation == ORIEN_HORIZON)) {
                canvas.drawLine(x1[i], y1[i], x2[i], y2[i], paintLine[i]);
            }
            else {
                canvas.drawLine(hx1[i], hy1[i], hx2[i], hy2[i], hPaintLine[i]);
            }

        }
    }

    public void importFile(String filePath){
        try {
            FileInputStream fis = new FileInputStream(filePath);
            byte[] readBytes = new byte[fis.available()];
            fis.read(readBytes);
            String readString = new String(readBytes);
            analyzeData(readString);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void analyzeData(String data){
        int index;
        index = data.indexOf(",");
        stOrientation = Integer.valueOf(data.substring(0,index));
        index = data.indexOf(";");
        numLines = Integer.valueOf(data.substring(2,index));
        Log.i("numlines",String.valueOf(numLines));
        String restString = data.substring(index + 1);

        x1 = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            x1[i] = Float.valueOf(temp.substring(0,index));
            Log.i("x1",String.valueOf(x1[i]));
            restString = temp.substring(index + 1);
        }

        x2 = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            x2[i] = Float.valueOf(temp.substring(0,index));
            Log.i("x2",String.valueOf(x2[i]));
            restString = temp.substring(index + 1);
        }

        y1 = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            y1[i] = Float.valueOf(temp.substring(0,index));
            Log.i("y1",String.valueOf(y1[i]));
            restString = temp.substring(index + 1);
        }

        y2 = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            y2[i] = Float.valueOf(temp.substring(0,index));
            Log.i("y2",String.valueOf(y2[i]));
            restString = temp.substring(index + 1);
        }

        lineWidth = new int[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            lineWidth[i] = Integer.valueOf(temp.substring(0,index));
            Log.i("linewidth",String.valueOf(lineWidth[i]));
            restString = temp.substring(index + 1);
        }

        lineColors = new int[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            lineColors[i] = Integer.valueOf(temp.substring(0,index));
            Log.i("lineColors",String.valueOf(lineColors[i]));
            restString = temp.substring(index + 1);
        }

        paintLine = new Paint[numLines];
        for(int i = 0;i < numLines;++i){
            paintLine[i] = new Paint();
            paintLine[i].setColor(lineColors[i]);
            paintLine[i].setStyle(Paint.Style.STROKE);
            paintLine[i].setAntiAlias(true);
            paintLine[i].setStrokeWidth(lineWidth[i]);
        }

        hx1 = new float[numLines];
        hy1 = new float[numLines];
        hx2 = new float[numLines];
        hy2 = new float[numLines];
        hLineWidth = new int[numLines];
        hPaintLine = new Paint[numLines];

        if(stOrientation == ORIEN_VERTICAL) {
            dispWidth -= actionBarHeight;
            for (int i = 0; i < numLines; ++i) {
                hx1[i] = x1[i] * (dispWidth / dispHeight) + ((dispHeight - (dispWidth * dispWidth / dispHeight)) / 2);
                Log.i("hx1", String.valueOf(hx1[i]));
                hy1[i] = y1[i] * (dispWidth / dispHeight);
                Log.i("hy1", String.valueOf(hy1[i]));
                hx2[i] = x2[i] * (dispWidth / dispHeight) + ((dispHeight - (dispWidth * dispWidth / dispHeight)) / 2);
                Log.i("hx2", String.valueOf(hx2[i]));
                hy2[i] = y2[i] * (dispWidth / dispHeight);
                Log.i("hy2", String.valueOf(hy2[i]));

                hLineWidth[i] = (int) ((float) (lineWidth[i]) * (dispWidth / dispHeight));
                Log.i("hlinewidth", String.valueOf(lineWidth[i] * (dispWidth / dispHeight)));

                hPaintLine[i] = new Paint();
                hPaintLine[i].setColor(lineColors[i]);
                hPaintLine[i].setStyle(Paint.Style.STROKE);
                hPaintLine[i].setAntiAlias(true);
                hPaintLine[i].setStrokeWidth(hLineWidth[i]);
            }
        }
        else{
            dispHeight -= actionBarHeight;
            for (int i = 0; i < numLines; ++i) {
                hx1[i] = x1[i] * (dispWidth / dispHeight);
                Log.i("hx1", String.valueOf(hx1[i]));
                hy1[i] = y1[i] * (dispWidth / dispHeight) + ((dispHeight - (dispWidth * dispWidth / dispHeight)) / 2);
                Log.i("hy1", String.valueOf(hy1[i]));
                hx2[i] = x2[i] * (dispWidth / dispHeight);
                Log.i("hx2", String.valueOf(hx2[i]));
                hy2[i] = y2[i] * (dispWidth / dispHeight) + ((dispHeight - (dispWidth * dispWidth / dispHeight)) / 2);
                Log.i("hy2", String.valueOf(hy2[i]));

                hLineWidth[i] = (int) ((float) (lineWidth[i]) * (dispWidth / dispHeight));
                Log.i("hlinewidth", String.valueOf(lineWidth[i] * (dispWidth / dispHeight)));

                hPaintLine[i] = new Paint();
                hPaintLine[i].setColor(lineColors[i]);
                hPaintLine[i].setStyle(Paint.Style.STROKE);
                hPaintLine[i].setAntiAlias(true);
                hPaintLine[i].setStrokeWidth(hLineWidth[i]);
            }
        }
    }

    public void refresh(){
        //importFile(filePath);
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_90:
                orientation = ORIEN_HORIZON;
                break;
            case Surface.ROTATION_180:
                orientation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_270:
                orientation = ORIEN_HORIZON;
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                judgeTouched(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:

                break;
            default:
        }
        invalidate();
        return true;
    }

    public void judgeTouched(float x,float y){
        float distant = 10000;
        int tempId = -1;
        if((stOrientation == ORIEN_VERTICAL && orientation == ORIEN_VERTICAL)
                || (stOrientation == ORIEN_HORIZON && orientation == ORIEN_HORIZON)) {
            for (int i = 0; i < numLines; ++i) {
                float tilt = (y2[i] - y1[i]) / (x2[i] - x1[i]);
                float lineY = tilt * (x - x1[i]) + y1[i];
                if (y < lineY + lineWidth[i] && y > lineY - lineWidth[i]) {
                    if (x1[i] < x2[i]) {
                        if (x > x1[i] && x < x2[i]) {
                            if (distant > Math.abs(lineY - y)) {
                                distant = Math.abs(lineY - y);
                                tempId = i;
                            }
                        }
                    } else if (x1[i] > x2[i]) {
                        if (x < x1[i] && x > x2[i]) {
                            if (distant > Math.abs(lineY - y)) {
                                distant = Math.abs(lineY - y);
                                tempId = i;
                            }
                        }
                    }
                }
            }
        }
        else{
            for (int i = 0; i < numLines; ++i) {
                float tilt = (hy2[i] - hy1[i]) / (hx2[i] - hx1[i]);
                float lineY = tilt * (x - hx1[i]) + hy1[i];
                if (y < lineY + hLineWidth[i] && y > lineY - hLineWidth[i]) {
                    if (hx1[i] < hx2[i]) {
                        if (x > hx1[i] && x < hx2[i]) {
                            if (distant > Math.abs(lineY - y)) {
                                distant = Math.abs(lineY - y);
                                tempId = i;
                            }
                        }
                    } else if (hx1[i] > hx2[i]) {
                        if (x < hx1[i] && x > hx2[i]) {
                            if (distant > Math.abs(lineY - y)) {
                                distant = Math.abs(lineY - y);
                                tempId = i;
                            }
                        }
                    }
                }
            }
        }


        if(tempId != -1){
            if(hide[tempId]) {
                changeColorTransparent(tempId);
                hide[tempId] = false;
            }
            else if(!hide[tempId]){
                changeColorSolid(tempId);
                hide[tempId] = true;
            }
        }
    }

    public void changeColorTransparent(int id){
        if((stOrientation == ORIEN_VERTICAL && orientation == ORIEN_VERTICAL)
                || (stOrientation == ORIEN_HORIZON && orientation == ORIEN_HORIZON)) {
            if (lineColors[id] == Color.RED)
                paintLine[id].setColor(red);

            if (lineColors[id] == Color.GREEN)
                paintLine[id].setColor(green);

            if (lineColors[id] == Color.BLUE)
                paintLine[id].setColor(blue);
        }
        else{
            if (lineColors[id] == Color.RED)
                hPaintLine[id].setColor(red);

            if (lineColors[id] == Color.GREEN)
                hPaintLine[id].setColor(green);

            if (lineColors[id] == Color.BLUE)
                hPaintLine[id].setColor(blue);
        }
    }

    public void changeColorSolid(int id){
        if((stOrientation == ORIEN_VERTICAL && orientation == ORIEN_VERTICAL)
                || (stOrientation == ORIEN_HORIZON && orientation == ORIEN_HORIZON))
            paintLine[id].setColor(lineColors[id]);

        else
            hPaintLine[id].setColor(lineColors[id]);
    }

    public int getOrientation(){
        return stOrientation;
    }
}
