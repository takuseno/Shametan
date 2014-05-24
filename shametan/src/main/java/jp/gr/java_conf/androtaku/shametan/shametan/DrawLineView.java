package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by takuma on 2014/05/06.
 */
public class DrawLineView extends View implements View.OnTouchListener{

    Paint[] paintLine;
    Paint[] paintPoint;
    Paint paintOpt;

    private float[] x1;
    private float[] y1;
    private float[] x2;
    private float[] y2;

    private int[] lineWidth;
    private int lastModifiedWidth = 40;

    int numLines = 1;
    private static final int MAX_LINES = 20;

    int selected = 0;
    private static final int SELECTED_START = 1;
    private static final int SELECTED_END = 2;
    private static final int SELECTED_OPTION = 3;
    private static final int SELECTED_WAITING_ADDITION = 4;
    private static final int SELECTED_NONE = 0;

    private int selectedNum = 0;

    private int timeCounter = 0;

    private int red = Color.argb(100,255,0,0);
    private int green = Color.argb(100,0,255,0);
    private int blue = Color.argb(100,0,0,255);
    private int[] lineColors;
    private int lastModifiedColor = red;

    float dispWidth,dispHeight;

    Context context;

    DrawLineOptionDialog optionDialog = new DrawLineOptionDialog();

    String backgroundFile;

    float touchX,touchY;

    public DrawLineView(Context context,String fileName){
        super(context);
        setOnTouchListener(this);

        this.context = context;

        init();

        WindowManager wm =
                (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        dispWidth = size.x;
        dispHeight = size.y;
        x1[0] = (dispWidth/2) - 100;
        x2[0] = (dispWidth/2) + 100;
        y1[0] = (dispHeight/2);
        y2[0] = (dispHeight/2);

        backgroundFile = fileName;
    }

    public void init(){
        x1 = new float[MAX_LINES];
        x2 = new float[MAX_LINES];
        y1 = new float[MAX_LINES];
        y2 = new float[MAX_LINES];
        for(int i = 0;i < MAX_LINES;++i){
            x1[i] = 0;
            x2[i] = 0;
            y1[i] = 0;
            y2[i] = 0;
        }

        lineWidth = new int[MAX_LINES];
        for(int i = 0;i < MAX_LINES;++i){
            lineWidth[i] = 40;
        }

        lineColors = new int[MAX_LINES];
        for(int i = 0;i < MAX_LINES;++i){
            lineColors[i] = red;
        }

        paintLine = new Paint[20];
        for(int i = 0;i < MAX_LINES;++i) {
            paintLine[i] = new Paint();
            paintLine[i].setColor(red);
            paintLine[i].setStyle(Paint.Style.STROKE);
            paintLine[i].setAntiAlias(true);
            paintLine[i].setStrokeWidth(40);
        }

        paintPoint = new Paint[20];
        for(int i = 0;i < MAX_LINES;++i) {
            paintPoint[i] = new Paint();
            paintPoint[i].setColor(Color.GREEN);
            paintPoint[i].setStyle(Paint.Style.STROKE);
            paintPoint[i].setAntiAlias(true);
            paintPoint[i].setStrokeWidth(5);
        }

        paintOpt = new Paint();
        paintOpt.setColor(Color.BLUE);
        paintOpt.setAntiAlias(true);
        paintOpt.setStrokeWidth(30);
    }

    public void importData(float[] x1,float[] x2,float[] y1,float[] y2,
                           int[] lineWidth,int[] lineColors){
        numLines = x1.length;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.lineWidth = lineWidth;
        this.lineColors = lineColors;
    }

    public void addLine(){
        x1[numLines] = (dispWidth/2) - 100;
        x2[numLines] = (dispWidth/2) + 100;
        y1[numLines] = (dispHeight/2);
        y2[numLines] = (dispHeight/2);

        lineWidth[numLines] = lastModifiedWidth;
        lineColors[numLines] = lastModifiedColor;

        ++numLines;
        refreshPaints();
        invalidate();
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
    }

    public void refreshPaints(){
        for(int i = 0;i < numLines;++i){
            paintLine[i].setColor(lineColors[i]);
            paintLine[i].setStyle(Paint.Style.STROKE);
            paintLine[i].setAntiAlias(true);
            paintLine[i].setStrokeWidth(lineWidth[i]);

            paintPoint[i].setColor(Color.GREEN);
            paintPoint[i].setStyle(Paint.Style.STROKE);
            paintPoint[i].setAntiAlias(true);
            paintPoint[i].setStrokeWidth(5);
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(optionDialog.isChangeFragColor()){
            paintLine[selectedNum].setColor(optionDialog.getChangeColor());
            lineColors[selectedNum] = optionDialog.getChangeColor();
            lastModifiedColor = optionDialog.getChangeColor();
            optionDialog.changeFragColorReset();
        }

        if(optionDialog.isChangeFragSize()){
            lineWidth[selectedNum] = optionDialog.getChangeSize();
            paintLine[selectedNum].setStrokeWidth(lineWidth[selectedNum]);
            lastModifiedWidth = optionDialog.getChangeSize();
            optionDialog.changeFragSizeReset();
        }

        if(optionDialog.isDeleteFrag()){
            deleteLine(selectedNum);
            optionDialog.changeFragDeleteReset();
            optionDialog.dismiss();
        }

        for(int i = 0;i < numLines;++i) {
            //draw line
            canvas.drawLine(x1[i], y1[i], x2[i], y2[i], paintLine[i]);

            //draw start point
            canvas.save();
            if(y1[i] > y2[i]) {
                canvas.rotate(-(float) Math.toDegrees(Math.acos((x2[i] - x1[i]) /
                        Math.sqrt((double) ((x2[i] - x1[i]) * (x2[i] - x1[i])) + (double) ((y2[i] - y1[i]) * (y2[i] - y1[i])))))
                        , x1[i], y1[i]);
            }
            else{
                canvas.rotate((float) Math.toDegrees(Math.acos((x2[i] - x1[i]) /
                        Math.sqrt((double) ((x2[i] - x1[i]) * (x2[i] - x1[i])) + (double) ((y2[i] - y1[i]) * (y2[i] - y1[i])))))
                        , x1[i], y1[i]);
            }
            canvas.drawRect(x1[i] - lineWidth[i],y1[i] - (lineWidth[i]/2),x1[i], y1[i] + (lineWidth[i]/2), paintPoint[i]);
            canvas.restore();

            //draw end point
            canvas.save();
            if(y1[i] > y2[i]) {
                canvas.rotate(-(float) Math.toDegrees(Math.acos((x2[i] - x1[i]) /
                        Math.sqrt((double) ((x2[i] - x1[i]) * (x2[i] - x1[i])) + (double) ((y2[i] - y1[i]) * (y2[i] - y1[i])))))
                        , x2[i], y2[i]);
            }
            else{
                canvas.rotate((float) Math.toDegrees(Math.acos((x2[i] - x1[i]) /
                        Math.sqrt((double) ((x2[i] - x1[i]) * (x2[i] - x1[i])) + (double) ((y2[i] - y1[i]) * (y2[i] - y1[i])))))
                        , x2[i], y2[i]);
            }
            canvas.drawRect(x2[i], y2[i] - (lineWidth[i]/2),x2[i] + lineWidth[i],y2[i] + lineWidth[i]/2, paintPoint[i]);
            canvas.restore();

            //draw option point
            canvas.drawPoint((x1[i] + x2[i]) / 2,(y1[i] + y2[i]) / 2,paintOpt);


        }

        if(selected == SELECTED_START || selected == SELECTED_END){
            canvas.drawBitmap(trimTouchPoint(touchX,touchY),touchX - 100,touchY - 300,null);
        }
    }

    public Bitmap trimTouchPoint(float x,float y){
        try {
            BitmapRegionDecoder regionDecoder;
            regionDecoder = BitmapRegionDecoder.newInstance(backgroundFile, false);
            Rect rect = new Rect((int)(x - 100),(int)(y - 100),(int)(x + 100),(int)(y + 100));
            Bitmap bitmap = regionDecoder.decodeRegion(rect,null);
            return bitmap;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                judgeTouchPoint(event.getX(),event.getY());
                judgeTouchOption(event.getX(),event.getY());
                if(selected == SELECTED_NONE){
                    selected = SELECTED_WAITING_ADDITION;
                    addtionTimer(event.getX(),event.getY());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                movePoint(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_UP:
                if(selected == SELECTED_OPTION){
                    optionDialog.setValue(selectedNum,lineWidth[selectedNum],lineColors[selectedNum],this);
                    optionDialog.show(((Activity)context).getFragmentManager(),"optiondialog");
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
            if (x < (x1[i] + 50) && x > (x1[i] - 50)
                    && y < (y1[i] + 50) && y > (y1[i] - 50)) {
                selected = SELECTED_START;
                selectedNum = i;
                return true;
            }
            if (x < (x2[i] + 50) && x > (x2[i] - 50)
                    && y < (y2[i] + 50) && y > (y2[i] - 50)) {
                selected = SELECTED_END;
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

    public void addtionTimer(float x,float y){
        final float position_x = x;
        final float position_y = y;
        Timer timer = new Timer(true);
        final android.os.Handler handler = new android.os.Handler();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        handler.post(
                                new Runnable(){
                                    public void run(){
                                        if(timeCounter == 2 && selected == SELECTED_WAITING_ADDITION){
                                            addLine();
                                            selected = SELECTED_END;
                                            selectedNum = numLines - 1;
                                            x1[selectedNum] = position_x;
                                            x2[selectedNum] = position_x;
                                            y1[selectedNum] = position_y;
                                            y2[selectedNum] = position_y;
                                            timeCounter = 0;
                                            cancel();
                                        }

                                        if(timeCounter == 2 && selected != SELECTED_WAITING_ADDITION){
                                            timeCounter = 0;
                                            cancel();
                                        }

                                        ++timeCounter;
                                    }
                                });
                    }
                }
                , 0, 1000
        );
    }
}
