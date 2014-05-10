package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

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
    private float[] optX;
    private float[] optY;

    private int[] lineWidth;

    int numLines = 1;
    private static final int MAX_LINES = 20;

    int selected = 0;
    private static final int SELECTED_START = 1;
    private static final int SELECTED_END = 2;
    private static final int SELECTED_OPTION = 3;
    private static final int SELECTED_NONE = 0;
    private int selectedNum = 0;

    private int red = Color.argb(100,255,0,0);
    private int green = Color.argb(100,0,255,0);
    private int blue = Color.argb(100,0,0,255);
    private int[] lineColors;

    float dispWidth,dispHeight;

    Context context;

    DrawLineOptionDialog optionDialog = new DrawLineOptionDialog();

    public DrawLineView(Context context){
        super(context);
        setOnTouchListener(this);

        this.context = context;

        x1 = new float[MAX_LINES];
        x2 = new float[MAX_LINES];
        y1 = new float[MAX_LINES];
        y2 = new float[MAX_LINES];
        optX = new float[MAX_LINES];
        optY = new float[MAX_LINES];
        for(int i = 0;i < MAX_LINES;++i){
            x1[i] = 0;
            x2[i] = 0;
            y1[i] = 0;
            y2[i] = 0;
            optX[i] = 0;
            optY[i] = 0;
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
        paintLine[0] = new Paint();
        paintLine[0].setColor(red);
        paintLine[0].setStyle(Paint.Style.STROKE);
        paintLine[0].setAntiAlias(true);
        paintLine[0].setStrokeWidth(40);

        paintPoint = new Paint[20];
        paintPoint[0] = new Paint();
        paintPoint[0].setColor(Color.GREEN);
        paintPoint[0].setStyle(Paint.Style.STROKE);
        paintPoint[0].setAntiAlias(true);
        paintPoint[0].setStrokeWidth(10);

        paintOpt = new Paint();
        paintOpt.setColor(Color.BLUE);
        paintOpt.setAntiAlias(true);
        paintOpt.setStrokeWidth(30);


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

        optX[0] = x2[0] + 50;
        optY[0] = y2[0];
    }

    public void importData(float[] x1,float[] x2,float[] y1,float[] y2,
                           float[] optX,float[] optY,
                           Paint[] paintLine,Paint[] paintPoint){
        numLines = x1.length;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.optX = optX;
        this.optY = optY;
        this.paintLine = paintLine;
        this.paintPoint = paintPoint;
    }

    public void addLine(){
        x1[numLines] = (dispWidth/2) - 100;
        x2[numLines] = (dispWidth/2) + 100;
        y1[numLines] = (dispHeight/2);
        y2[numLines] = (dispHeight/2);

        optX[numLines] = x2[numLines] + 50;
        optY[numLines] = y2[numLines];

        paintLine[numLines] = new Paint();
        paintLine[numLines].setColor(red);
        paintLine[numLines].setStyle(Paint.Style.STROKE);
        paintLine[numLines].setAntiAlias(true);
        paintLine[numLines].setStrokeWidth(40);

        paintPoint[numLines] = new Paint();
        paintPoint[numLines].setColor(Color.GREEN);
        paintPoint[numLines].setStyle(Paint.Style.STROKE);
        paintPoint[numLines].setAntiAlias(true);
        paintPoint[numLines].setStrokeWidth(10);

        ++numLines;
        invalidate();
    }

    public void deleteLine(int num){
        if(num != (numLines - 1)) {
            x1[num] = x1[numLines - 1];
            x2[num] = x2[numLines - 1];
            y1[num] = y1[numLines - 1];
            y2[num] = y2[numLines - 1];
            optX[num] = optX[numLines - 1];
            optY[num] = optY[numLines - 1];
            lineColors[num] = lineColors[numLines - 1];
            lineWidth[num] = lineWidth[numLines - 1];
            paintLine[num].setStrokeWidth(lineWidth[num]);
            paintLine[num].setColor(lineColors[num]);
        }
        x1[numLines - 1] = 0;
        x2[numLines - 1] = 0;
        y1[numLines - 1] = 0;
        y2[numLines - 1] = 0;
        optX[numLines - 1] = 0;
        optY[numLines - 1] = 0;
        lineColors[numLines - 1] = red;
        lineWidth[numLines - 1] = 40;

        --numLines;
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(optionDialog.isChangeFragColor()){
            paintLine[selectedNum].setColor(optionDialog.getChangeColor());
            lineColors[selectedNum] = optionDialog.getChangeColor();
            optionDialog.changeFragColorReset();
        }

        if(optionDialog.isChangeFragSize()){
            lineWidth[selectedNum] = optionDialog.getChangeSize();
            paintLine[selectedNum].setStrokeWidth(lineWidth[selectedNum]);
            optionDialog.changeFragSizeReset();
        }

        if(optionDialog.isDeleteFrag()){
            deleteLine(selectedNum);
            optionDialog.changeFragDeleteReset();
            optionDialog.dismiss();
        }

        for(int i = 0;i < numLines;++i) {
            canvas.drawLine(x1[i], y1[i], x2[i], y2[i], paintLine[i]);
            canvas.drawCircle(x1[i], y1[i],lineWidth[i]/2, paintPoint[i]);
            canvas.drawCircle(x2[i], y2[i],lineWidth[i]/2, paintPoint[i]);
            canvas.drawPoint(optX[i],optY[i],paintOpt);
        }
    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                judgeTouchPoint(event.getX(),event.getY());
                judgeTouchOption(event.getX(),event.getY());
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

    public void judgeTouchPoint(float x,float y){
        for(int i = 0;i < numLines;++i) {
            if (x < (x1[i] + 50) && x > (x1[i] - 50)
                    && y < (y1[i] + 50) && y > (y1[i] - 50)) {
                selected = SELECTED_START;
                selectedNum = i;
            }
            if (x < (x2[i] + 50) && x > (x2[i] - 50)
                    && y < (y2[i] + 50) && y > (y2[i] - 50)) {
                selected = SELECTED_END;
                selectedNum = i;
            }
        }
    }

    public void judgeTouchOption(float x,float y){
        for(int i = 0;i < numLines;++i) {
            if (x < (optX[i] + 30) && x > (optX[i] - 30)
                    && y < (optY[i] + 30) && y > (optY[i] - 30)) {
                selected = SELECTED_OPTION;
                selectedNum = i;
            }
        }
    }

    public void movePoint(float x,float y){
        if(selected == SELECTED_START) {
            x1[selectedNum] = x;
            y1[selectedNum] = y;
        }
        if(selected == SELECTED_END){
            x2[selectedNum] = x;
            y2[selectedNum] = y;

            if((x + 50) > dispWidth) {
                optX[selectedNum] = x;
                optY[selectedNum] = y - 50;
            }
            else {
                optX[selectedNum] = x + 50;
                optY[selectedNum] = y;
            }
        }
    }
}
