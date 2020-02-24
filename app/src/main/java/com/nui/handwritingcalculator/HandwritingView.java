package com.nui.handwritingcalculator;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

import static java.sql.DriverManager.println;


public class HandwritingView extends View {

    //gesture variables
    private ArrayList<Path> strokeList = new ArrayList<>();
    private Paint gesturePaint;
    //canvas variables
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
    private Stack<CustomGesture> gestureStack = new Stack<>();

    public HandwritingView(Context context) {
        this(context, null);
    }

    public  HandwritingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gesturePaint = new Paint();
        gesturePaint.setAntiAlias(UIConstants.GESTURE_RENDERING_ANTIALIAS);
        gesturePaint.setColor(UIConstants.DEFAULT_GESTURE_COLOR);
        gesturePaint.setAntiAlias(true);
        gesturePaint.setStyle(Paint.Style.STROKE);
        gesturePaint.setStrokeJoin(Paint.Join.ROUND);
        gesturePaint.setStrokeCap(Paint.Cap.ROUND);
        gesturePaint.setStrokeWidth(UIConstants.GESTURE_STROKE_WIDTH);
        gesturePaint.setDither(UIConstants.DITHER_FLAG);
        gesturePaint.setXfermode(null);
        gesturePaint.setMaskFilter(null);
        gesturePaint.setAlpha(0xff);


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

        canvas.drawColor(UIConstants.BG_COLOR);
Log.d("hw:onDraw ","- canvas.drawColor"+ " STACK SIZE = "+gestureStack.size() );

        //draw the strokes on the canvas
        for (CustomGesture g : gestureStack) {
            gesturePaint.setStrokeWidth(g.width);
            canvas.drawPath(g.gesture.toPath(), gesturePaint);
Log.d("hw:onDraw ","- canvas.drawPath");
        }

        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
Log.d("hw:onDraw ","- canvas.drawBitmap");

        //canvas.restore();  //This doesn't seem to be necessary
    }


    public void refresh(Stack g) {
        gestureStack = g;
        Log.d("hw:refresh"," - calling invalidate: GESTURE COUNT = "+gestureStack.size());

        invalidate();
    }



}