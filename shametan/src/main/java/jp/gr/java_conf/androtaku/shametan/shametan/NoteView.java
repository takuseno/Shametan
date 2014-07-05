package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by takuma on 2014/05/30.
 */
public class NoteView extends View{
    private Paint[] paintLine;
    private float[] x1;
    private float[] y1;
    private float[] x2;
    private float[] y2;
    private float[] lineWidth;
    private int[] lineColors;
    private boolean[] hide;
    int numLines;

    private int red = Color.argb(50,255,0,0);
    private int green = Color.argb(50,0,255,0);
    private int blue = Color.argb(50,0,0,255);

    private Context context;

    private String filePath;

    private int stOrientation = 1;
    private float dispWidth,dispHeight;
    private float imageWidth,imageHeight;
    private float extraX,extraY;

    public NoteView(Context context, String filePath){
        super(context);
        this.filePath = filePath;
        this.context = context;
    }

    public void init(){
        extraX = (dispWidth - imageWidth) / 2;
        extraY = (dispHeight - imageHeight) / 2;

        importFile(filePath);

        hide = new boolean[numLines];
        for(int i = 0;i < numLines;++i){
            hide[i] = true;
        }

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        for(int i = 0;i < numLines;++i) {
            //draw line
            canvas.drawLine(x1[i], y1[i], x2[i], y2[i], paintLine[i]);
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
        index = data.indexOf(";");
        numLines = Integer.valueOf(data.substring(0,index));
        Log.i("numlines",String.valueOf(numLines));
        String restString = data.substring(index + 1);

        x1 = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            x1[i] = (Float.valueOf(temp.substring(0,index)) * imageWidth) + extraX;
            Log.i("x1",String.valueOf(x1[i]));
            restString = temp.substring(index + 1);
        }

        x2 = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            x2[i] = (Float.valueOf(temp.substring(0,index)) * imageWidth) + extraX;
            Log.i("x2",String.valueOf(x2[i]));
            restString = temp.substring(index + 1);
        }

        y1 = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            y1[i] = (Float.valueOf(temp.substring(0,index)) * imageHeight) + extraY;
            Log.i("y1",String.valueOf(y1[i]));
            restString = temp.substring(index + 1);
        }

        y2 = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            y2[i] = (Float.valueOf(temp.substring(0,index)) * imageHeight) + extraY;
            Log.i("y2",String.valueOf(y2[i]));
            restString = temp.substring(index + 1);
        }

        lineWidth = new float[numLines];
        for(int i = 0;i < numLines;++i){
            String temp = restString;
            index = temp.indexOf(",");
            lineWidth[i] = Float.valueOf(temp.substring(0,index)) * imageWidth;
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
        int tempId = -1;
        float preDistant = 10000;
        for (int i = 0; i < numLines; ++i) {
            float tilt = (y2[i] - y1[i]) / (x2[i] - x1[i]);
            float distant = Math.abs((tilt * x) - y - (tilt * x1[i]) + y1[i]) / (float)Math.sqrt((tilt * tilt) + 1);
            if (distant < lineWidth[i] && distant < preDistant) {
                if(tilt < 1) {
                    if (x1[i] < x2[i] && x > x1[i] && x < x2[i]) {
                        preDistant = distant;
                        tempId = i;
                    } else if (x1[i] > x2[i] && x < x1[i] && x > x2[i]) {
                        preDistant = distant;
                        tempId = i;
                    }
                }
                else{
                    if (y1[i] < y2[i] && y > y1[i] && y < y2[i]) {
                        preDistant = distant;
                        tempId = i;
                    } else if (y1[i] > y2[i] && y < y1[i] && y > y2[i]) {
                        preDistant = distant;
                        tempId = i;
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
        if (lineColors[id] == Color.RED)
            paintLine[id].setColor(red);

        if (lineColors[id] == Color.GREEN)
            paintLine[id].setColor(green);

        if (lineColors[id] == Color.BLUE)
            paintLine[id].setColor(blue);
    }

    public void changeColorSolid(int id){
        paintLine[id].setColor(lineColors[id]);
    }

    public int getOrientation(){
        return stOrientation;
    }

    public void putDispWidth(int dispWidth){
        this.dispWidth = dispWidth;
    }

    public void putDispHeight(int dispHeight){
        this.dispHeight = dispHeight;
    }

    public void putImageWidth(int imageWidth){
        this.imageWidth = imageWidth;
    }

    public void putImageHeight(int imageHeight){
        this.imageHeight = imageHeight;
    }
}
