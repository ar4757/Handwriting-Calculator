package com.nui.handwritingcalculator;

import android.gesture.Gesture;

 class CustomGesture {

        float width;
        Gesture gesture;


         CustomGesture(Gesture g, float strokeWidth) {
            this.gesture = g;
            this.width = strokeWidth;

        }



}
