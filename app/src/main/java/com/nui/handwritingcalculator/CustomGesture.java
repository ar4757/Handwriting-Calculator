package com.nui.handwritingcalculator;

import android.gesture.Gesture;

 class CustomGesture {

        float width;
        Gesture gesture;
        String action;
        boolean doesIntersect;


         CustomGesture(Gesture g, float strokeWidth) {
            this.gesture = g;
            this.width = strokeWidth;
            this.action = "";
            this.doesIntersect = false;
        }

        CustomGesture(String action) {
             this.action = action;
        }

        CustomGesture(Gesture g, String action) {
             this.gesture = g;
             this.action = action;
             this.doesIntersect = false;
        }



}
