package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by takuma on 2014/05/06.
 */
public class DrawLineView extends View{

    Paint[] paintLine;
    Paint paintPoint;
    Paint paintOpt;

    private float[] x1;
    private float[] y1;
    private float[] x2;
    private float[] y2;

    private float startX,startY;

    private float[] lineWidth;
    private float lastModifiedWidth = 80;

    private int numLines = 0;
    private static final int MAX_LINES = 40;

    private int selected = 0;
    private static final int SELECTED_START = 1;
    private static final int SELECTED_END = 2;
    private static final int SELECTED_OPTION = 3;
    private static final int SELECTED_WAITING_ADDITION = 4;
    private static final int SELECTED_NONE = 0;

    private int selectedNum = 0;

    private int red = Color.argb(100,255,0,0);
    private int green = Color.argb(100,0,255,0);
    private int blue = Color.argb(100,0,0,255);
    private int[] lineColors;
    private int lastModifiedColor = red;

    float dispWidth,dispHeight;
    float imageWidth,imageHeight;
    float extraX,extraY;

    Context context;

    DrawLineOptionDialog optionDialog = new DrawLineOptionDialog();

    String backgroundFile;
    String dataPath;

    float touchX,touchY;
    private byte[] ssBytes = null;

    private boolean initialized = false;

    public DrawLineView(Context context,String fileName){
        super(context);
        this.context = context;
        backgroundFile = fileName;

    }

    public void init(){
        extraX = (dispWidth - imageWidth) / 2;
        extraY = (dispHeight - imageHeight) / 2;

        x1 = new float[MAX_LINES];
        x2 = new float[MAX_LINES];
        y1 = new float[MAX_LINES];
        y2 = new float[MAX_LINES];

        lineWidth = new float[MAX_LINES];
        for(int i = 0;i < MAX_LINES;++i){
            lineWidth[i] = 80;
        }

        lineColors = new int[MAX_LINES];
        for(int i = 0;i < MAX_LINES;++i){
            lineColors[i] = red;
        }

        paintLine = new Paint[MAX_LINES];
        for(int i = 0;i < MAX_LINES;++i) {
            paintLine[i] = new Paint();
            paintLine[i].setColor(red);
            paintLine[i].setStyle(Paint.Style.STROKE);
            paintLine[i].setAntiAlias(true);
            paintLine[i].setStrokeWidth(40);
        }

        paintPoint = new Paint();
        paintPoint.setColor(Color.GREEN);
        paintPoint.setStyle(Paint.Style.STROKE);
        paintPoint.setAntiAlias(true);
        paintPoint.setStrokeWidth(5);

        paintOpt = new Paint();
        paintOpt.setColor(Color.BLUE);
        paintOpt.setAntiAlias(true);
        paintOpt.setStrokeWidth(30);

        addLine();

        int index = backgroundFile.lastIndexOf(".");
        dataPath = backgroundFile.substring(0,index) + ".st";
        File data = new File(dataPath);
        if(data.exists()){
            importFile(dataPath);
        }

        initialized = true;
        invalidate();
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
        index = data.indexOf(";");
        numLines = Integer.valueOf(data.substring(0,index));
        Log.i("numlines", String.valueOf(numLines));
        String restString = data.substring(index + 1);

        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            x1[i] = (Float.valueOf(temp.substring(0,index)) * imageWidth) + extraX;
            Log.i("x1",String.valueOf(x1[i]));
            restString = temp.substring(index + 1);
        }

        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            x2[i] = (Float.valueOf(temp.substring(0,index)) * imageWidth) + extraX;
            Log.i("x2",String.valueOf(x2[i]));
            restString = temp.substring(index + 1);
        }

        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            y1[i] = (Float.valueOf(temp.substring(0,index)) * imageHeight) + extraY;
            Log.i("y1",String.valueOf(y1[i]));
            restString = temp.substring(index + 1);
        }

        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            y2[i] = (Float.valueOf(temp.substring(0,index)) * imageHeight) + extraY;
            Log.i("y2",String.valueOf(y2[i]));
            restString = temp.substring(index + 1);
        }

        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            lineWidth[i] = (Float.valueOf(temp.substring(0,index)) * imageWidth);
            Log.i("linewidth",String.valueOf(lineWidth[i]));
            restString = temp.substring(index + 1);
        }

        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            lineColors[i] = Integer.valueOf(temp.substring(0,index));
            if(lineColors[i] == Color.RED){
                lineColors[i] = red;
            }
            else if(lineColors[i] == Color.GREEN){
                lineColors[i] = green;
            }
            else if(lineColors[i] == Color.BLUE){
                lineColors[i] = blue;
            }
            Log.i("lineColors",String.valueOf(lineColors[i]));
            restString = temp.substring(index + 1);
        }

        for(int i = 0;i < numLines;++i){
            paintLine[i] = new Paint();
            paintLine[i].setColor(lineColors[i]);
            paintLine[i].setStyle(Paint.Style.STROKE);
            paintLine[i].setAntiAlias(true);
            paintLine[i].setStrokeWidth(lineWidth[i]);
        }
    }

    public void exportData(){
        /*
        header orientation
        subheader numlines
        value01 x1
        value02 x2
        value03 y1
        value04 y2
        value05 linewidth
        value06 linecolors
        each component separated by ,
        each section separated by ;
         */
        int index = backgroundFile.lastIndexOf(".");
        String dataPath = backgroundFile.substring(0,index) + ".st";
        File data = new File(dataPath);
        if(data.exists()){
            data.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(data);
            String output = "" + String.valueOf(numLines) + ";";
            for(int i = 0;i <numLines;++i){
                output += String.valueOf((x1[i] - extraX) / imageWidth) + ",";
            }

            for(int i = 0;i <numLines;++i){
                output += String.valueOf((x2[i] - extraX) / imageWidth) + ",";
            }

            for(int i = 0;i <numLines;++i){
                output += String.valueOf((y1[i] - extraY) / imageHeight) + ",";
            }

            for(int i = 0;i <numLines;++i){
                output += String.valueOf((y2[i] - extraY) / imageHeight) + ",";
            }

            for(int i = 0;i <numLines;++i){
                output += String.valueOf(lineWidth[i] / imageWidth) + ",";
            }

            for(int i = 0;i <numLines;++i){
                if(lineColors[i] == red) {
                    output += String.valueOf(Color.RED) + ",";
                }
                if(lineColors[i] == green){
                    output += String.valueOf(Color.GREEN) + ",";
                }
                if(lineColors[i] == blue){
                    output += String.valueOf(Color.BLUE) + ",";
                }
            }

            fos.write(output.getBytes());
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void addLine(){
        x1[numLines] = (dispWidth/2) - 150;
        x2[numLines] = (dispWidth/2) + 150;
        y1[numLines] = (dispHeight/2);
        y2[numLines] = (dispHeight/2);

        lineWidth[numLines] = lastModifiedWidth;
        lineColors[numLines] = lastModifiedColor;

        ++numLines;
        refreshPaints();
    }

    public void deleteLine(int num){
        if(num != (numLines - 1)) {
            x1[num] = x1[numLines - 1];
            x2[num] = x2[numLines - 1];
            y1[num] = y1[numLines - 1];
            y2[num] = y2[numLines - 1];
            lineColors[num] = lineColors[numLines - 1];
            lineWidth[num] = lineWidth[numLines - 1];
        }
        x1[numLines - 1] = 0;
        x2[numLines - 1] = 0;
        y1[numLines - 1] = 0;
        y2[numLines - 1] = 0;
        lineColors[numLines - 1] = red;
        lineWidth[numLines - 1] = 40;

        refreshPaints();

        --numLines;

        invalidate();
    }

    public void refreshPaints(){
        for(int i = 0;i < numLines;++i){
            paintLine[i].setColor(lineColors[i]);
            paintLine[i].setStyle(Paint.Style.STROKE);
            paintLine[i].setAntiAlias(true);
            paintLine[i].setStrokeWidth(lineWidth[i]);
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(initialized) {
            if (optionDialog.isChangeFragColor()) {
                paintLine[selectedNum].setColor(optionDialog.getChangeColor());
                lineColors[selectedNum] = optionDialog.getChangeColor();
                lastModifiedColor = optionDialog.getChangeColor();
                optionDialog.changeFragColorReset();
            }

            if (optionDialog.isChangeFragSize()) {
                lineWidth[selectedNum] = optionDialog.getChangeSize();
                paintLine[selectedNum].setStrokeWidth(lineWidth[selectedNum]);
                lastModifiedWidth = optionDialog.getChangeSize();
                optionDialog.changeFragSizeReset();
            }

            for (int i = 0; i < numLines; ++i) {
                //draw line
                canvas.drawLine(x1[i], y1[i], x2[i], y2[i], paintLine[i]);

                //draw start point
                canvas.save();
                if (y1[i] > y2[i]) {
                    canvas.rotate(-(float) Math.toDegrees(Math.acos((x2[i] - x1[i]) /
                            Math.sqrt((double) ((x2[i] - x1[i]) * (x2[i] - x1[i])) + (double) ((y2[i] - y1[i]) * (y2[i] - y1[i])))))
                            , x1[i], y1[i]);
                } else {
                    canvas.rotate((float) Math.toDegrees(Math.acos((x2[i] - x1[i]) /
                            Math.sqrt((double) ((x2[i] - x1[i]) * (x2[i] - x1[i])) + (double) ((y2[i] - y1[i]) * (y2[i] - y1[i])))))
                            , x1[i], y1[i]);
                }
                canvas.drawRect(x1[i] - lineWidth[i], y1[i] - (lineWidth[i] / 2), x1[i], y1[i] + (lineWidth[i] / 2), paintPoint);
                canvas.restore();

                //draw end point
                canvas.save();
                if (y1[i] > y2[i]) {
                    canvas.rotate(-(float) Math.toDegrees(Math.acos((x2[i] - x1[i]) /
                            Math.sqrt((double) ((x2[i] - x1[i]) * (x2[i] - x1[i])) + (double) ((y2[i] - y1[i]) * (y2[i] - y1[i])))))
                            , x2[i], y2[i]);
                } else {
                    canvas.rotate((float) Math.toDegrees(Math.acos((x2[i] - x1[i]) /
                            Math.sqrt((double) ((x2[i] - x1[i]) * (x2[i] - x1[i])) + (double) ((y2[i] - y1[i]) * (y2[i] - y1[i])))))
                            , x2[i], y2[i]);
                }
                canvas.drawRect(x2[i], y2[i] - (lineWidth[i] / 2), x2[i] + lineWidth[i], y2[i] + lineWidth[i] / 2, paintPoint);
                canvas.restore();

                //draw option point
                canvas.drawPoint((x1[i] + x2[i]) / 2, (y1[i] + y2[i]) / 2, paintOpt);
            }

            if (selected == SELECTED_START || selected == SELECTED_END) {
                trimTouchPoint(canvas);
            }
        }
    }

    public void trimTouchPoint(Canvas canvas){
        try {
            float recX,recY,scopeX,scopeY,fingerPointY;
            if(touchX + 100 > dispWidth){
                recX = dispWidth - 200;
                scopeX = recX + 100;
            }
            else if(touchX - 100 < 0){
                recX = 0;
                scopeX = recX + 100;
            }
            else{
                recX = touchX - 100;
                scopeX = touchX;
            }

            if(touchY - 300 < 0){
                recY = touchY + 100;
            }
            else{
                recY = touchY - 300;
            }

            if(touchY - 100 < 0){
                scopeY = 100;
                fingerPointY = recY + touchY;
            }
            else if(touchY + 100 > dispHeight){
                scopeY = dispHeight - 100;
                fingerPointY = recY + 200 - (dispHeight - touchY);
            }
            else{
                scopeY = touchY;
                fingerPointY = recY + 100;
            }

            BitmapRegionDecoder regionDecoder;
            regionDecoder = BitmapRegionDecoder.newInstance(ssBytes,0,ssBytes.length,false);
            Rect rect = new Rect((int)(scopeX - 100),(int)(scopeY - 100),(int)(scopeX + 100),(int)(scopeY + 100));

            canvas.drawBitmap(regionDecoder.decodeRegion(rect,null),recX,recY,null);
            canvas.drawPoint(touchX,fingerPointY,paintOpt);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                judgeTouchPoint(event.getX(),event.getY());
                judgeTouchOption(event.getX(),event.getY());
                if(selected == SELECTED_NONE){
                    selected = SELECTED_WAITING_ADDITION;
                    startX = event.getX();
                    startY = event.getY();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                movePoint(event.getX(),event.getY());
                if(selected == SELECTED_WAITING_ADDITION){
                    if(Math.abs(startX - event.getX()) > 30
                            || Math.abs(startY - event.getY()) > 30) {
                        addLine();
                        selected = SELECTED_END;
                        selectedNum = numLines - 1;
                        x1[selectedNum] = startX;
                        x2[selectedNum] = event.getX();
                        y1[selectedNum] = startY;
                        y2[selectedNum] = event.getY();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if(selected == SELECTED_OPTION){
                    optionDialog = new DrawLineOptionDialog();
                    optionDialog.setValue(lineWidth[selectedNum],lineColors[selectedNum],this,selectedNum);
                    optionDialog.show(((ActionBarActivity)context).getSupportFragmentManager(),"optiondialog");
                }
                selected = SELECTED_NONE;
                break;
            default:
        }
        invalidate();
        return true;
    }

    public boolean judgeTouchPoint(float x,float y){
        for(int i = 0;i < numLines;++i) {
            if (x < (x1[i] + lineWidth[i]) && x > (x1[i] - lineWidth[i])
                    && y < (y1[i] + lineWidth[i]) && y > (y1[i] - lineWidth[i])) {
                selected = SELECTED_START;
                touchX = x;
                touchY = y;
                selectedNum = i;
                return true;
            }
            if (x < (x2[i] + 50) && x > (x2[i] - 50)
                    && y < (y2[i] + 50) && y > (y2[i] - 50)) {
                selected = SELECTED_END;
                touchX = x;
                touchY = y;
                selectedNum = i;
                return true;
            }
        }

        return false;
    }

    public boolean judgeTouchOption(float x,float y){
        for(int i = 0;i < numLines;++i) {
            if (x < ((x1[i] + x2[i]) / 2 + 30) && x > ((x1[i] + x2[i]) / 2 - 30)
                    && y < ((y1[i] + y2[i]) / 2 + 30) && y > ((y1[i] + y2[i]) / 2 - 30)) {
                selected = SELECTED_OPTION;
                selectedNum = i;
                return true;
            }
        }

        return false;
    }

    public void movePoint(float x,float y){
        if(selected == SELECTED_START) {
            x1[selectedNum] = x;
            y1[selectedNum] = y;
        }
        if(selected == SELECTED_END){
            x2[selectedNum] = x;
            y2[selectedNum] = y;
        }

        touchX = x;
        touchY = y;
    }

    public void putDispWidth(int width){
        dispWidth = width;
    }
    public void putDispHeight(int height){
        dispHeight = height;
    }

    public void putImageWidth(int width){
        imageWidth = width;
    }

    public void putImageHeight(int height){
        imageHeight = height;
    }

    public void putBackgroundCash(Bitmap bmp){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            ssBytes = baos.toByteArray();
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
}
