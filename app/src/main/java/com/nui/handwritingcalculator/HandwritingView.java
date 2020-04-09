package com.nui.handwritingcalculator;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
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
    ArrayList<CustomGesture> gestureList = new ArrayList<>();  //recognized gestures in a string
    Stack<CustomGesture> gestureStack = new Stack<CustomGesture>();
    CountDownTimer currentCountDownTimer;
    public boolean isCurrentCountDownTimerRunning = false;
    public Boolean libraryLoaded = false;
    private Boolean gestureResetsText=false;
    private int lastInsertedIndex = 0;

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

        //Since GestureOverlayView does not allow you to input a single tap as a drawable gesture, we can do so manually (for dotting the 'i')
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                System.out.println("dot the i");
                Gesture dotGesture = new Gesture();
                ArrayList<GesturePoint> dotPoints = new ArrayList();
                dotPoints.add(new GesturePoint(e.getX(), e.getY(), 0));
                dotPoints.add(new GesturePoint(e.getX() + 10, e.getY() + 10, 10));
                GestureStroke dotStroke = new GestureStroke(dotPoints);
                dotGesture.addStroke(dotStroke);
                onGesturePerformed(gOverlay, dotGesture);
                return true;
            }
        });
        gOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);
            }
        });

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

        for (CustomGesture g : gestureList) {
          switch (g.action) {
              case "x":
              case "X":
              case "-":
              case "/":
              case "+": {
                  tstr += " " + g.action + " ";
                  break;
              }
              default: {
                  tstr += g.action;
              }
          }
          //Note that for mxparser, square root requires parentheses
          tstr = tstr.replaceAll("^\\((\\d*)\\)|([^√])\\((\\d*)\\)", "$1$2$3");
          tstr = tstr.replaceAll("null", "");
          tstr = tstr.replaceAll("\\(\\)", "");
        }
        System.out.println("tstr: " + tstr);
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
                lastGesture.doesIntersect = true;
                //replace last two "gestures" with new combined gesture
                gestureStack.pop();
                gestureStack.pop();
                gestureStack.push(lastGesture);

//                System.out.println("onGesturePerformed: gestures overlap, recognize gesture");

                recognizeGesture(lastGesture);
                lastGesture = null; //start again
            }
            //Case where user is drawing an 'i'
            else if (Math.abs(newGesture.gesture.getBoundingBox().centerX() - lastGesture.gesture.getBoundingBox().centerX()) < 10 && newGesture.width < 10 && lastGesture.width < 10) {
                System.out.println("i");
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
        //After GESTURE_TIMEOUT, currently 500 milliseconds (0.5 seconds), recognize the gesture as-is, i.e. stop waiting for multi-stroke
        CountDownTimer countDownTimer = new CountDownTimer(UIConstants.GESTURE_TIMEOUT, 1000) {

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

        for (int i = 0; i < predictions.size(); i++) {
            System.out.println("Prediction: " + predictions.get(i).name + " with score of " + predictions.get(i).score);
        }
        System.out.println();
        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {

            //Find the first (best scoring) gesture with the matching stroke count
            String action = "invalid";
            for (int i = 0; i < predictions.size(); i++) {
                String temp_action = predictions.get(i).name;
                //Skip 'i' if the gesture definitely has an intersection
                if (cg.doesIntersect == true && temp_action.equals("i")) {
                    continue;
                }
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
                insertGestureBasedOnPosition(gesture, action);
                cleanupMisrecognizedGestures();
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

    private void cleanupMisrecognizedGestures() {
        for (int i = 0; i < gestureList.size(); i++) {
            CustomGesture previous = null;
            CustomGesture current = null;
            CustomGesture next = null;
            if (i > 0) {
                previous = gestureList.get(i - 1);
            }
            current = gestureList.get(i);
            if (i < gestureList.size() - 1) {
                next = gestureList.get(i + 1);
            }
            if (previous != null && next != null && previous.action.equals("t") && next.action.equals("n")) {
                current.action = "a";
            }
            if (previous != null && next != null && previous.action.equals("s") && next.action.equals("n")) {
                current.action = "i";
            }
            if (previous != null && next != null && previous.action.equals("c") && next.action.equals("s")) {
                current.action = "o";
            }
            if (previous != null && next != null && previous.action.equals("c") && current.action.equals("o")) {
                next.action = "s";
            }
            if (previous != null && next != null && current.action.equals("i") && next.action.equals("n")) {
                previous.action = "s";
            }
            if (current != null && current.action.equals("s") && ((previous != null && !previous.action.equals("o")) || ((previous != null && !previous.action.equals("o") || (previous == null)) && next != null && !next.action.equals("i")))) {
                current.action = "5";
            }
            if (previous != null && next != null && current.action.equals("o") && (!previous.action.equals("c") || !next.action.equals("s"))) {
                current.action = "0";
            }
            if (previous != null && next != null && current.action.equals("a") && next.action.equals("n")) {
                previous.action = "t";
            }
        }
    }

    boolean alwaysContinueOnNextLine = false;
    boolean addBackRightParentheses = true;

    private void insertGestureBasedOnPosition(Gesture gesture, String action) {
        float xLeftVal = gesture.getBoundingBox().left;
        float xRightVal = gesture.getBoundingBox().right;
        float yTopVal = gesture.getBoundingBox().top;
        float yBottomVal = gesture.getBoundingBox().bottom;
        if (gestureList.isEmpty()) {
            CustomGesture customGesture = new CustomGesture(gesture, action);
            gestureList.add(customGesture);
            lastInsertedIndex = gestureList.size()-1;

            if (action.equals("√")) {
                CustomGesture leftParentheses = new CustomGesture(gesture, "(");
                gestureList.add(leftParentheses);
                CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                gestureList.add(rightParentheses);
            }
        }
        else {
            //Find the leftmost position (x coordinate) at which to insert the gesture
            //If there is a division bar, locate it. Can't compare xCoordinates of elements above vs below the division bar
            Gesture divisionBar = null;
            int rightParenthesesRemoved = 0;
            int index = 0;
            for (int i = 0; i < gestureList.size(); i++) {
                if (gestureList.get(i).action == "/") {
                    divisionBar = gestureList.get(i).gesture;
                    index = i;
                    break;
                }
            }
            //If number is above division bar, start from index = 0. If it's below division bar, start from index = divisionbar's location
            //if (divisionBar != null && yTopVal < divisionBar.getBoundingBox().bottom) {
            //    index = 0;
            //}
            while (gestureList.get(gestureList.size()-1).action == ")") {
                gestureList.remove(gestureList.size()-1);
                rightParenthesesRemoved++;
            }
            if (!action.equals("-")) {
                if (alwaysContinueOnNextLine) {
                    index = gestureList.size() - 1;
                } else {
                    boolean continuedOnNextLine = true;
                    for (int i = 0; i < gestureList.size(); i++) {
                        if (yTopVal < gestureList.get(i).gesture.getBoundingBox().bottom) {
                            continuedOnNextLine = false;
                            break;
                        }
                    }
                    if (continuedOnNextLine) {
                        index = gestureList.size() - 1;
                        alwaysContinueOnNextLine = true;
                    }
                }
            }
            for (int i = index; i < gestureList.size(); i++) {
                float currentXLeftVal = 0;
                float currentYBottomVal = 0;
                if (gestureList.get(i).gesture != null) {
                    currentXLeftVal = gestureList.get(i).gesture.getBoundingBox().left;
                    currentYBottomVal = gestureList.get(i).gesture.getBoundingBox().bottom;
                }
                if (i == gestureList.size() - 1) {
                    //Exponent check
                    Gesture previousGesture = gestureList.get(i).gesture;
                    boolean isInteger = false;
                    try {
                        Integer.parseInt(action);
                        isInteger = true;
                    }
                    catch (Exception e) {
                        isInteger = false;
                    }
                    if (isInteger && gesture.getBoundingBox().height() < previousGesture.getBoundingBox().height()/2 && gesture.getBoundingBox().intersect(previousGesture.getBoundingBox().right - 20, previousGesture.getBoundingBox().top - 60, previousGesture.getBoundingBox().right + 60, previousGesture.getBoundingBox().bottom - previousGesture.getBoundingBox().height()/2)) {
                        action = "^" + action;
                    }
                    //Division with a horizontal bar check
                    if (action.equals("-") && gesture.getBoundingBox().top > previousGesture.getBoundingBox().bottom) {
                        action = "/";
                        if (xLeftVal < currentXLeftVal) {
                            CustomGesture leftParentheses = new CustomGesture(gesture, "(");
                            gestureList.add(i, leftParentheses);
                            for (int j = i+1; j < gestureList.size(); j++) {
                                float currentXRightVal = gestureList.get(j).gesture.getBoundingBox().right;
                                if (xRightVal < currentXRightVal) {
                                    CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                                    gestureList.add(j, rightParentheses);
                                    CustomGesture customGesture = new CustomGesture(gesture, action);
                                    gestureList.add(j, customGesture);
                                    lastInsertedIndex = j;
                                    if (action.equals("√")) {
                                        gestureList.add(j, leftParentheses);
                                        gestureList.add(j, rightParentheses);
                                    }
                                    leftParentheses = new CustomGesture(gesture, "(");
                                    gestureList.add(j, leftParentheses);
                                    rightParentheses = new CustomGesture(gesture, ")");
                                    gestureList.add(j, rightParentheses);
                                    break;
                                }
                            }
                            CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                            gestureList.add(rightParentheses);
                            CustomGesture customGesture = new CustomGesture(gesture, action);
                            gestureList.add(customGesture);
                            lastInsertedIndex = gestureList.size()-1;
                            if (action.equals("√")) {
                                gestureList.add(leftParentheses);
                                gestureList.add(rightParentheses);
                            }
                            leftParentheses = new CustomGesture(gesture, "(");
                            gestureList.add(leftParentheses);
                            rightParentheses = new CustomGesture(gesture, ")");
                            gestureList.add(rightParentheses);
                            break;
                        }
                    }
                    CustomGesture customGesture = new CustomGesture(gesture, action);
                    if (divisionBar != null && xLeftVal > divisionBar.getBoundingBox().right) {
                        if (fractionIsEnclosed()) {
                            gestureList.add(customGesture);
                            lastInsertedIndex = gestureList.size() - 1;
                        }
                        else {
                            CustomGesture leftParentheses = new CustomGesture(gesture, "(");
                            gestureList.add(0, leftParentheses);
                            for (int j = 0; j < rightParenthesesRemoved; j++) {
                                CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                                gestureList.add(rightParentheses);
                            }
                            CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                            gestureList.add(rightParentheses);
                            gestureList.add(customGesture);
                            lastInsertedIndex = gestureList.size() - 1;
                            addBackRightParentheses = false;
                        }
                    }
                    else if (divisionBar != null && yTopVal < divisionBar.getBoundingBox().bottom) {
                        gestureList.add(index-1, customGesture);
                        lastInsertedIndex = index-1;
                        if (action.equals("√")) {
                            CustomGesture leftParentheses = new CustomGesture(gesture, "(");
                            gestureList.add(index-1, leftParentheses);
                            CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                            gestureList.add(index-1, rightParentheses);
                        }
                    }
                    else {
                        if (xLeftVal < currentXLeftVal) {
                            gestureList.add(i, customGesture);
                            lastInsertedIndex = 0;
                        }
                        else {
                            gestureList.add(customGesture);
                            lastInsertedIndex = gestureList.size()-1;
                        }
                        if (action.equals("√")) {
                            CustomGesture leftParentheses = new CustomGesture(gesture, "(");
                            gestureList.add(leftParentheses);
                            CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                            gestureList.add(rightParentheses);
                        }
                    }
                    break;
                }
                else if (xLeftVal < currentXLeftVal) {
                    //Exponent check
                    Gesture previousGesture = gestureList.get(i).gesture;
                    boolean isInteger = false;
                    try {
                        Integer.parseInt(action);
                        isInteger = true;
                    }
                    catch (Exception e) {
                        isInteger = false;
                    }
                    if (isInteger && gesture.getBoundingBox().height() < previousGesture.getBoundingBox().height()/2 && gesture.getBoundingBox().intersect(previousGesture.getBoundingBox().right - 20, previousGesture.getBoundingBox().top - 60, previousGesture.getBoundingBox().right + 60, previousGesture.getBoundingBox().bottom - previousGesture.getBoundingBox().height()/2)) {
                        action = "^" + action;
                    }
                    //Division with a horizontal bar check
                    if (action.equals("-") && gesture.getBoundingBox().top > previousGesture.getBoundingBox().bottom) {
                        action = "/";
                        if (xLeftVal < currentXLeftVal) {
                            CustomGesture leftParentheses = new CustomGesture(gesture, "(");
                            gestureList.add(i, leftParentheses);
                            for (int j = i+1; j < gestureList.size(); j++) {
                                float currentXRightVal = gestureList.get(j).gesture.getBoundingBox().right;
                                if (xRightVal < currentXRightVal) {
                                    CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                                    gestureList.add(j, rightParentheses);
                                    CustomGesture customGesture = new CustomGesture(gesture, action);
                                    gestureList.add(j, customGesture);
                                    lastInsertedIndex = j;
                                    if (action.equals("√")) {
                                        gestureList.add(j, leftParentheses);
                                        gestureList.add(j, rightParentheses);
                                    }
                                    leftParentheses = new CustomGesture(gesture, "(");
                                    gestureList.add(j, leftParentheses);
                                    rightParentheses = new CustomGesture(gesture, ")");
                                    gestureList.add(j, rightParentheses);
                                    break;
                                }
                            }
                            CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                            gestureList.add(rightParentheses);
                            CustomGesture customGesture = new CustomGesture(gesture, action);
                            gestureList.add(customGesture);
                            lastInsertedIndex = gestureList.size()-1;
                            if (action.equals("√")) {
                                gestureList.add(leftParentheses);
                                gestureList.add(rightParentheses);
                            }
                            leftParentheses = new CustomGesture(gesture, "(");
                            gestureList.add(leftParentheses);
                            rightParentheses = new CustomGesture(gesture, ")");
                            gestureList.add(rightParentheses);
                            break;
                        }
                    }
                    CustomGesture customGesture = new CustomGesture(gesture, action);
                    gestureList.add(i, customGesture);
                    lastInsertedIndex = i;
                    if (action.equals("√")) {
                        CustomGesture leftParentheses = new CustomGesture(gesture, "(");
                        gestureList.add(i, leftParentheses);
                        CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                        gestureList.add(i, rightParentheses);
                    }
                    break;
                }
            }
            if (addBackRightParentheses == true) {
                for (int i = 0; i < rightParenthesesRemoved; i++) {
                    CustomGesture rightParentheses = new CustomGesture(gesture, ")");
                    gestureList.add(rightParentheses);
                }
            }
            addBackRightParentheses = true;
        }
    }
    public void finalizeGesture() {
        if (isCurrentCountDownTimerRunning == true) {
            currentCountDownTimer.onFinish();
            currentCountDownTimer.cancel();
        }
    }

    private boolean fractionIsEnclosed() {
        boolean left = false;
        boolean right = false;
        for (int i = 0; i < gestureList.size(); i++) {
            if (gestureList.get(i).action == "(") {
                if (i < gestureList.size()-1 && gestureList.get(i+1).action == "(") {
                    left = true;
                }
            }
            if (gestureList.get(i).action == ")") {
                if (i < gestureList.size()-1 && gestureList.get(i+1).action == ")") {
                    right = true;
                    break;
                }
            }
        }
        return left && right;
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
        CustomGesture temp = new CustomGesture(s);
        gestureList.add(temp);
    } //clearText

    //--------------------*/
    // clearText          */
    //--------------------*/
    public void clearText() {
        gestureList.clear();
        lastInsertedIndex = 0;
    } //clearText

    //--------------------*/
    // clear              */
    //--------------------*/
    public void clear() {
        gestureStack.clear();
        lastGesture = null;
        if (isCurrentCountDownTimerRunning) currentCountDownTimer.cancel();
        clearText();  //created method in case we need to do other stuff
        refresh();
    } //clear


    public void undo() {
        //undo the last gesture

        //If there is at least one gesture on the screen, pop the last gesture from the gesture list
        //and remove it from the math expression. Erase that gesture from the screen.

            if (gestureStack.empty() == false) {
                gestureStack.pop();
                refresh();
            }
            if (lastInsertedIndex < 0) {
                lastInsertedIndex = 0;
            }
            if (lastInsertedIndex > 0 && lastInsertedIndex < gestureList.size()-1 && gestureList.get(lastInsertedIndex-1).action == "(" && gestureList.get(lastInsertedIndex+1).action == ")") {
                System.out.println("Swag");
                gestureList.remove(lastInsertedIndex+1);
                gestureList.remove(lastInsertedIndex);
                gestureList.remove(lastInsertedIndex-1);
                lastInsertedIndex-=2;
            }
            else if (lastInsertedIndex == gestureList.size()-1 && gestureList.get(0).action == "(" && gestureList.get(lastInsertedIndex-1).action == ")") {
                System.out.println("Money");
                gestureList.remove(lastInsertedIndex);
                gestureList.remove(lastInsertedIndex-1);
                gestureList.remove(0);
                lastInsertedIndex-=4;
            }
            else {
                if (gestureList.isEmpty() == false && lastInsertedIndex >= 0 && lastInsertedIndex < gestureList.size()) {
                    gestureList.remove(lastInsertedIndex);
                    if (lastInsertedIndex > 0) {
                        lastInsertedIndex--;
                        if (lastInsertedIndex - 2 >= 0 && gestureList.get(lastInsertedIndex).action == ")" && gestureList.get(lastInsertedIndex - 2).action == "(") {
                            lastInsertedIndex--;
                        }
                    }
                }
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