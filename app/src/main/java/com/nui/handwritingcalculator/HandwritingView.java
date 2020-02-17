package com.nui.handwritingcalculator;

import android.content.Context;
import android.gesture.GestureOverlayView;
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


public class HandwritingView extends GestureOverlayView {


    
    public HandwritingView(Context context) {
        this(context, null);
    }

    public HandwritingView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }



}