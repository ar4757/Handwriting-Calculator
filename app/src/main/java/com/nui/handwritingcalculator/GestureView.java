package com.nui.handwritingcalculator;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;;



public class GestureView extends View {

    Gesture gesture;
    Paint gesturePaint;

    public GestureView(Context context, Gesture gesture, Boolean undo) {
        super(context);
        this.gesture = gesture;
        gesturePaint = new Paint();
        gesturePaint.setAntiAlias(UIConstants.GESTURE_RENDERING_ANTIALIAS);
        gesturePaint.setColor(UIConstants.DEFAULT_GESTURE_COLOR);
        gesturePaint.setStyle(Paint.Style.STROKE);
        gesturePaint.setStrokeJoin(Paint.Join.ROUND);
        gesturePaint.setStrokeCap(Paint.Cap.ROUND);
        gesturePaint.setStrokeWidth(UIConstants.GESTURE_STROKE_WIDTH);
        gesturePaint.setDither(UIConstants.DITHER_FLAG);
        if (undo)
            gesturePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else
            gesturePaint.setXfermode(null);
    }

    public void onDraw(Canvas canvas) {

        canvas.drawPath(gesture.toPath(), gesturePaint);
    }


}