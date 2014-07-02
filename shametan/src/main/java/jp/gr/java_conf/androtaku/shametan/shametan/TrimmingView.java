package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by takuma on 2014/06/20.
 */
public class TrimmingView extends View {
/*
     a0   a1

     a3   a2
 */
    private float centerX,centerY;
    private float recWidth,recHeight;

    private float startX,startY;

    private Paint rectanglePaint,pointPaint;

    private int dispWidth,dispHeight,imageWidth,imageHeight;

    private Context context;

    private final static int TOUCH_LESS = 0;
    private final static int TOUCH_INSIDE = 1;
    private final static int TOUCH_POINT_TOP = 2;
    private final static int TOUCH_POINT_RIGHT = 3;
    private final static int TOUCH_POINT_BOTTOM = 4;
    private final static int TOUCH_POINT_LEFT = 5;
    private int touchState = TOUCH_LESS;

    private static final int ORIEN_VERTICAL = 1;
    private static final int ORIEN_HORIZON = 2;
    private int orientation = ORIEN_VERTICAL;

    private boolean initalized = false;

    public TrimmingView(Context context){
        super(context);

        this.context = context;

        rectanglePaint = new Paint();
        rectanglePaint.setColor(Color.WHITE);
        rectanglePaint.setStrokeWidth(5);
        rectanglePaint.setStyle(Paint.Style.STROKE);
        rectanglePaint.setAntiAlias(true);

        pointPaint = new Paint();
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStrokeWidth(30);
        pointPaint.setStyle(Paint.Style.STROKE);
    }

    public void init(int width,int height){
        dispWidth = width;
        dispHeight = height;

        recWidth = dispWidth / 2;
        centerX = dispWidth / 2;
        recHeight = dispHeight / 2;
        centerY = dispHeight / 2;

        Log.i("centerx",String.valueOf(centerX));

        initalized = true;

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

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(initalized) {
            canvas.drawRect(centerX - (recWidth / 2), centerY - (recHeight / 2),
                    centerX + (recWidth / 2), centerY + (recHeight / 2), rectanglePaint);

            canvas.drawPoint(centerX, centerY - (recHeight / 2), pointPaint);
            canvas.drawPoint(centerX + (recWidth / 2), centerY, pointPaint);
            canvas.drawPoint(centerX, centerY + (recHeight / 2), pointPaint);
            canvas.drawPoint(centerX - (recWidth / 2), centerY, pointPaint);
        }
    }

    public void putWidth(int width){
        imageWidth = width;
    }

    public void putHeight(int height){
        imageHeight = height;
    }

    public float[] getPointsX(){
        float[] pointsX = new float[2];
        int extraSpace = (dispWidth - imageWidth) / 2;
        Log.i("extraspace",String.valueOf(extraSpace));
        pointsX[0] = centerX - (recWidth / 2) - extraSpace;
        pointsX[1] = centerX + (recWidth / 2) - extraSpace;
        return pointsX;
    }

    public float[] getPointsY(){
        float[] pointsY = new float[2];
        int extraSpace = (dispHeight - imageHeight) / 2;
        pointsY[0] = centerY - (recHeight / 2) - extraSpace;
        pointsY[1] = centerY + (recHeight / 2) - extraSpace;
        return pointsY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchInside(event.getX(),event.getY());
                touchPoint(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if(touchState == TOUCH_INSIDE) {
                    moveRectangle(event.getX(), event.getY());
                }
                scaleRectangle(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_UP:
                   touchState = TOUCH_LESS;
                break;
        }
        invalidate();
        return true;
    }

    public void touchInside(float x,float y){
        if(x > centerX - (recWidth / 2) && x < centerX + (recWidth / 2)){
            if(y > centerY - (recHeight / 2) && y < centerY + (recHeight / 2)){
                startX = x;
                startY = y;
                touchState = TOUCH_INSIDE;
                Log.i("touch","inside");
            }
        }
    }

    public void touchPoint(float x,float y){
        if(x < centerX + 30 && x > centerX - 30){
            if(y < centerY - (recHeight / 2) + 30 && y > centerY - (recHeight / 2) - 30){
                startX = x;
                startY = y;
                touchState = TOUCH_POINT_TOP;
            }
            else if(y < centerY + (recHeight / 2) + 30 && y > centerY + (recHeight / 2) - 30){
                startX = x;
                startY = y;
                touchState = TOUCH_POINT_BOTTOM;
            }
        }

        else if(y < centerY + 30 && y > centerY - 30){
            if(x < centerX - (recWidth / 2) + 30 && x > centerX - (recWidth / 2) - 30){
                startX = x;
                startY = y;
                touchState = TOUCH_POINT_LEFT;
            }
            else if(x < centerX + (recWidth / 2) + 30 && x > centerX + (recWidth / 2) - 30){
                startX = x;
                startY = y;
                touchState = TOUCH_POINT_RIGHT;
            }
        }
    }

    public void moveRectangle(float x,float y){
        centerX -= startX - x;
        centerY -= startY - y;
        if(centerX - (recWidth / 2) < 0){
            centerX = recWidth / 2;
        }
        else if(centerX + (recWidth / 2) > dispWidth){
            centerX = dispWidth - (recWidth / 2);
        }
        if(centerY - (recHeight / 2) < 0){
            centerY = recHeight / 2;
        }
        else if(centerY + (recHeight / 2) > dispHeight){
            centerY = dispHeight - (recHeight / 2);
        }
        startX = x;
        startY = y;
    }

    public void scaleRectangle(float x,float y){
        switch(touchState){
            case TOUCH_POINT_TOP:
                recHeight += startY - y;
                centerY -= (startY - y) / 2;
                break;

            case TOUCH_POINT_RIGHT:
                recWidth -= startX - x;
                centerX -= (startX - x) / 2;
                break;

            case TOUCH_POINT_BOTTOM:
                recHeight -= startY - y;
                centerY -= (startY - y) / 2;
                break;

            case TOUCH_POINT_LEFT:
                recWidth += startX - x;
                centerX -= (startX - x) /2;
                break;
        }

        startX = x;
        startY = y;
    }
}
