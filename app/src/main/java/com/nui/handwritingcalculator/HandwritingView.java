package com.nui.handwritingcalculator;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

import static android.gesture.GestureStore.SEQUENCE_INVARIANT;
import static java.sql.DriverManager.println;


public class HandwritingView extends View implements GestureOverlayView.OnGesturePerformedListener {

    //gesture variables
    private ArrayList<Path> strokeList = new ArrayList<>();
    private Paint gesturePaint;
    //canvas variables
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);

    //Gesture Recognition Variables
    private GestureLibrary gLibrary;
    private GestureOverlayView gOverlay;
    private HandwritingView hwView;
    private CustomGesture lastGesture = null;
    ArrayList<String> gString = new ArrayList<>();  //recognized gestures in a string
    Stack<CustomGesture> gestureStack = new Stack<CustomGesture>();
    CountDownTimer currentCountDownTimer;
    public boolean isCurrentCountDownTimerRunning = false;
    public Boolean libraryLoaded = false;
    private Boolean gestureResetsText=false;

    //If we want to write the math expression to a text area,
    //call writeExpressionToTextArea
    private Boolean showText = false;
    TextView textOutputView;

    private ArrayList<String>  mathExpression = new ArrayList<>();


    public HandwritingView(Context context) {
        this(context, null);
    }

    public  HandwritingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Set up gesture library
        gLibrary = GestureLibraries.fromRawResource(context, R.raw.gesture);
        //gLibrary.setOrientationStyle(ORIENTATION_SENSITIVE);
        gLibrary.setSequenceType(SEQUENCE_INVARIANT);

        if (gLibrary.load()) {
            libraryLoaded = true;

            //Set up the gesture paint values
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

    } //constructor

    public void setOverlayView (GestureOverlayView gov) {
        gOverlay = gov;
        gOverlay.setGestureStrokeWidth(UIConstants.GESTURE_STROKE_WIDTH);
        gOverlay.setGestureColor(UIConstants.DEFAULT_GESTURE_COLOR);
        gOverlay.setUncertainGestureColor((UIConstants.UNRECOGNIZED_GESTURE_COLOR));
        gOverlay.setGestureStrokeAngleThreshold(90.0f);
        gOverlay.addOnGesturePerformedListener(this);
    }
    public void writeGestureStringToTextArea (TextView tv) {
        showText = true;
        textOutputView = tv;
    }

    //--------------------
    // getTextString
    //--------------------

    public String getTextString() {
        String tstr = "";

        for (String s : gString) {
            switch (s) {
                case "x":
                case "X":
                case "-":
                case "/":
                case "+": {
                    tstr += " " + s + " ";
                    break;
                }
                default: {
                    tstr += s;
                }
            }

        }
        return tstr;
    }

    public void onGestureResetText() {
        gestureResetsText = true;
    }
    // --------------------
    // onGesturePerformed
    //--------------------
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {


        CustomGesture newGesture =   new CustomGesture(gesture, gOverlay.getGestureStrokeWidth());
        gestureStack.push(newGesture);
        refresh();

        if (lastGesture == null) {
//            System.out.println("onGesturePerformed: no previous gesture");

            lastGesture = newGesture;
            currentCountDownTimer = createTimeout(newGesture);
        }
        else {
            //If gestures overlap, add current gesture to previous gesture and recognize it
            //new (overlapping) gesture will only be added as part of the prev gesture which it overlaps

            currentCountDownTimer.cancel();
            isCurrentCountDownTimerRunning = false;

            if (doGesturesOverlap(newGesture.gesture, lastGesture.gesture)) {
//                System.out.println("Do overlap");
                lastGesture.gesture.addStroke(newGesture.gesture.getStrokes().get(0));
                //replace last two "gestures" with new combined gesture
                gestureStack.pop();
                gestureStack.pop();
                gestureStack.push(lastGesture);

//                System.out.println("onGesturePerformed: gestures overlap, recognize gesture");

                recognizeGesture(lastGesture);
                lastGesture = null; //start again
            }
            else {
                //recognize the last gesture and start timer on new gesture
//                System.out.println("onGesturePerformed: gestures don't overlap, recognizing last gesture");

                recognizeGesture(lastGesture);
                lastGesture = newGesture;
                currentCountDownTimer = createTimeout(newGesture);
            }

        }


    }


    //--------------------
    // CountDownTimer
    //--------------------
    private CountDownTimer createTimeout(final CustomGesture gesture) {
        //After 500 milliseconds (0.5 seconds), recognize the gesture as-is, i.e. stop waiting for multi-stroke
        CountDownTimer countDownTimer = new CountDownTimer(500, 1000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
//                System.out.println("time expired: recognize gesture");
                recognizeGesture(gesture);
                lastGesture = null;
                isCurrentCountDownTimerRunning = false;
            }
        }.start();
//        System.out.println("start Timer");
        isCurrentCountDownTimerRunning = true;
        return countDownTimer;
    }

    //--------------------
    // doGesturesOverlap
    //--------------------
    private boolean doGesturesOverlap(Gesture gesture, Gesture lastGesture) {
        if (gesture.getBoundingBox().intersect(lastGesture.getBoundingBox())) {
            return true;
        }
        else {
            return false;
        }
    }

    //--------------------
    // recognizeGesture
    //--------------------
    private void recognizeGesture(CustomGesture cg) {
        Gesture gesture = cg.gesture;
        ArrayList<Prediction> predictions = gLibrary.recognize(gesture);

//        for (int i = 0; i < predictions.size(); i++) {
//            System.out.println("Prediction: " + predictions.get(i).name + " with score of " + predictions.get(i).score);
//        }
//        System.out.println();
        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {

            //Find the first (best scoring) gesture with the matching stroke count
            String action = "invalid";
            for (int i = 0; i < predictions.size(); i++) {
                String temp_action = predictions.get(i).name;
                if (gLibrary.getGestures(temp_action).get(0).getStrokesCount() == gesture.getStrokesCount()) {
                    action = temp_action;
                    break;
                }
            }

//            Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
            if (!action.equals("invalid")) {
                //Valid gesture add to math expression
                if (gestureResetsText) {
                    gestureResetsText = false;
                    clearText();
                }

//                System.out.println("Prediction: valid Gesture");
//
//                System.out.println("Prediction: adding expression");
                gString.add(action);
                if (showText)
                     textOutputView.setText(getTextString());

            }
            else {
//                System.out.println("Prediction: invalid Gesture");
                //remove unrecognized gesture from screen

                gestureStack.remove(cg);
                refresh();
                //Undo invlaid gesture
                //keepGestureOnScreen(gesture,true);
            }
        }
    }

    public void finalizeGesture() {
        if (isCurrentCountDownTimerRunning == true) {
            currentCountDownTimer.onFinish();
            currentCountDownTimer.cancel();
        }
    }

    public float getStrokeWidth() {
        return gOverlay.getGestureStrokeWidth();
    }

    public void setStrokeWidth(float w) {
        gOverlay.setGestureStrokeWidth(w);
    }
    //--------------------*/
    // setText            */
    //--------------------*/
    public void setText(String s) {
        gString.add(s);
    } //clearText

    //--------------------*/
    // clearText          */
    //--------------------*/
    public void clearText() {
        gString.clear();
    } //clearText

    //--------------------*/
    // clear              */
    //--------------------*/
    public void clear() {
        gestureStack.clear();
        lastGesture = null;
        clearText();  //created method in case we need to do other stuff
        refresh();
    } //clear


    public void undo() {
        //undo the last gesture
        String result = null;
        int i = gestureStack.size();

        //If there is at least one gesture on the screen, pop the last gesture from the gesture list
        //and remove it from the math expression. Erase that gesture from the screen.

        if (i > 0) {
            i--;
            gestureStack.pop();
            refresh();
            gString.remove(i);
        }
        //need to remove last gesture from screen

    } //undo

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


    public void refresh() {
        Log.d("hw:refresh"," - calling invalidate: GESTURE COUNT = "+gestureStack.size());

        invalidate();
    }



}