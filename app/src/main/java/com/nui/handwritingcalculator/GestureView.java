package com.nui.handwritingcalculator;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


public class GestureView extends View {

    private static final int GESTURE_STROKE_WIDTH = 12;
    private static final boolean GESTURE_RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;
    public static final int DEFAULT_GESTURE_COLOR = Color.WHITE;

    Gesture gesture;
    Paint gesturePaint;

    public GestureView(Context context, Gesture gesture) {
        super(context);
        this.gesture = gesture;
        gesturePaint = new Paint();
        gesturePaint.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
        gesturePaint.setColor(DEFAULT_GESTURE_COLOR);
        gesturePaint.setStyle(Paint.Style.STROKE);
        gesturePaint.setStrokeJoin(Paint.Join.ROUND);
        gesturePaint.setStrokeCap(Paint.Cap.ROUND);
        gesturePaint.setStrokeWidth(GESTURE_STROKE_WIDTH);
        gesturePaint.setDither(DITHER_FLAG);
    }

    public void onDraw(Canvas canvas) {

        canvas.drawPath(gesture.toPath(), gesturePaint);
    }

}