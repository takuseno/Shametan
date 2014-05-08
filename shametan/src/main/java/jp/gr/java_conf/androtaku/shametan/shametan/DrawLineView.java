package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by takuma on 2014/05/06.
 */
public class DrawLineView extends View implements View.OnTouchListener{

    private Paint paintLine,paintPoint;
    private float[] x1;
    private float[] y1;
    private float[] x2;
    private float[] y2;

    int numLines = 1;

    private static int selected = 0;
    private static final int SELECTED_START = 1;
    private static final int SELECTED_END = 2;
    private static final int SELECTED_NONE = 0;

    public DrawLineView(Context context){
        super(context);
        setOnTouchListener(this);

        x1 = new float[10];
        x2 = new float[10];
        y1 = new float[10];
        y2 = new float[10];

        paintLine = new Paint();
        paintLine.setColor(Color.RED);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(20);

        paintPoint = new Paint();
        paintPoint.setColor(Color.GREEN);
        paintPoint.setAntiAlias(true);
        paintPoint.setStrokeWidth(30);


        WindowManager wm =
                (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        x1[0] = (size.x/2) - 50;
        x2[0] = (size.x/2) + 50;
        y1[0] = (size.y/2);
        y2[0] = (size.y/2);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        for(int i = 0;i < numLines;++i) {
            canvas.drawLine(x1[i], y1[i], x2[i], y2[i], paintLine);
            canvas.drawPoint(x1[i], y1[i], paintPoint);
            canvas.drawPoint(x2[i], y2[i], paintPoint);
        }
    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX() < (x1[0] + 30) && event.getX() > (x1[0] - 30)
                        && event.getY() < (y1[0] + 30) && event.getY() > (y1[0] -30)){
                    selected = SELECTED_START;
                }
                if(event.getX() < (x2[0] + 30) && event.getX() > (x2[0] - 30)
                        && event.getY() < (y2[0] + 30) && event.getY() > (y2[0] -30)){
                    selected = SELECTED_END;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(selected == SELECTED_START) {
                    x1[0] = event.getX();
                    y1[0] = event.getY();
                }
                if(selected == SELECTED_END){
                    x2[0] = event.getX();
                    y2[0] = event.getY();
                }
                break;

            case MotionEvent.ACTION_UP:
                selected = SELECTED_NONE;
                break;
            default:
        }
        invalidate();
        return true;
    }
}
