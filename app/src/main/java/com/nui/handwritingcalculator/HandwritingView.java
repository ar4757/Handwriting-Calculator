package com.nui.handwritingcalculator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class HandwritingView extends View {

    public static final int DEFAULT_COLOR = Color.WHITE;
    public static final int DEFAULT_BG_COLOR = Color.BLACK;
    public static final int DEFAULT_WIDTH = 5;
    //gesture variables
    private Path stroke;
    private ArrayList<Path> strokeList = new ArrayList<>();
    private Paint strokePaint;
    //canvas variables
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
    
    private float curX, curY; //current location on screen

    
    public HandwritingView(Context context) {
        this(context, null);
    }

    public  HandwritingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        //Set up stroke paint values
        
        strokePaint = new Paint();
        strokePaint.setColor(DEFAULT_COLOR);
        strokePaint.setAntiAlias(true);
        strokePaint.setDither(true);
        strokePaint.setXfermode(null);
        strokePaint.setMaskFilter(null);
        strokePaint.setAlpha(0xff);
        strokePaint.setStrokeWidth(DEFAULT_WIDTH);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onSizeChanged(int w, int h , int oldw, int oldh) {
        //Redraw the canvas
        
        super.onSizeChanged(w,h,oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

    }


    @Override
    protected void onDraw(Canvas canvas) {
       // canvas.save();  //This doesn't seem to be necessary
        
        canvas.drawColor(DEFAULT_BG_COLOR);

        //draw the strokes on the canvas
        for (Path fp : strokeList) {
            canvas.drawPath(fp, strokePaint);
        }

        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
       //canvas.restore();  //This doesn't seem to be necessary
    }

    private void strokeBegin(float x, float y) {
        stroke = new Path();
        strokeList.add(stroke);

        stroke.reset();
        stroke.moveTo(x, y);
        curX = x;
        curY = y;
    }

    private void strokeMove(float x, float y) {

        stroke.quadTo(curX, curY, (x + curX) / 2, (y + curY) / 2);
        curX = x;
        curY = y;
    }

    private void strokeEnd() {
        stroke.lineTo(curX, curY);
    }

    public void clearScreen() {
        //Clear the display in handwriting area
        //Should we also clear the formula area?

        strokeList.clear();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                strokeBegin(x, y);
                break;
            case MotionEvent.ACTION_MOVE :
                strokeMove(x, y);
                break;
            case MotionEvent.ACTION_UP :
                strokeEnd();
                break;
        }
        invalidate();
        return true;
    }


}