package com.nui.handwritingcalculator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewOverlay;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.TextViewCompat;

import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Gesture;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.gesture.GestureStore.ORIENTATION_INVARIANT;
import static android.gesture.GestureStore.ORIENTATION_SENSITIVE;
import static android.gesture.GestureStore.SEQUENCE_INVARIANT;

public class CalculatorActivity extends AppCompatActivity implements OnGesturePerformedListener {

    private GestureLibrary gLibrary;
    private GestureOverlayView gOverlay;
    private String mathExpressionString;
    private boolean solutionDisplayed=false;

 //   HandwritingView hwView;
    TextView drawingMode;
    TextView solutionView;   //text view where we show what the user wrote and the solution
    ArrayList<Character> formula = new ArrayList<>();
    ArrayList<Gesture> gestureList = new ArrayList<>();
    Gesture currentGesture = new Gesture();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
        //gLibrary.setOrientationStyle(ORIENTATION_SENSITIVE);
        gLibrary.setSequenceType(SEQUENCE_INVARIANT);
        if (!gLibrary.load()) {
            finish();
        }

        gOverlay = (GestureOverlayView) findViewById(R.id.handwriting);
        gOverlay.setGestureStrokeAngleThreshold(90.0f);
        gOverlay.addOnGesturePerformedListener(this);


        mathExpressionString = "";
        solutionView = findViewById(R.id.solution);

        Button calculateButton = findViewById(R.id.calculate_btn);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // TextView solutionText = findViewById(R.id.solution);
                calculate();
            }
        });

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.design_default_color_on_secondary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

   //     hwView =  findViewById(R.id.handwriting);

    }

    CountDownTimer currentCountDownTimer;

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        int gestureListSize = gestureList.size();
        if (gestureListSize == 0) {
            gestureList.add(gesture);
            keepGestureOnScreen(gesture);
            currentCountDownTimer = createTimeout(gesture);
            return;
        }
        else {
            Gesture lastGesture = gestureList.get(gestureListSize-1);
            if (doGesturesOverlap(gesture, lastGesture) && lastGesture.getStrokesCount() == 1) {
                System.out.println("Do overlap");
                lastGesture.addStroke(gesture.getStrokes().get(0));
                keepGestureOnScreen(lastGesture);
            }
            else {
                gestureList.add(gesture);
                keepGestureOnScreen(gesture);
                currentCountDownTimer = createTimeout(gesture);
            }
        }
    }

    private CountDownTimer createTimeout(final Gesture gesture) {
        //After 1000 milliseconds (1.0 seconds), recognize the gesture as-is, i.e. stop waiting for multi-stroke
        CountDownTimer countDownTimer = new CountDownTimer(500, 1000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                recognizeGesture(gesture);
            }
        }.start();
        return countDownTimer;
    }

    private boolean doGesturesOverlap(Gesture gesture, Gesture lastGesture) {
        if (gesture.getBoundingBox().intersect(lastGesture.getBoundingBox())) {
            return true;
        }
        else {
            return false;
        }
    }

    private void recognizeGesture(Gesture gesture) {
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
                if (gLibrary.getGestures(temp_action).get(0).getStrokesCount() == gesture.getStrokesCount()) {
                    action = temp_action;
                    break;
                }
            }

            Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
            if (!action.equals("invalid")) {
                keepGestureOnScreen(gesture);
                if (solutionDisplayed) {
                    clearSolution();
                }
                mathExpressionString += action;
                solutionView.setText(mathExpressionString);

                //                TextView recognizedText = new TextView(this);
                //                recognizedText.setText(action);
                //                recognizedText.setTextColor(Color.WHITE);
                //                float x = gesture.getBoundingBox().left;
                //                float y = gesture.getBoundingBox().top;
                //                float height = gesture.getBoundingBox().height();
                //                float width = gesture.getBoundingBox().width();
                //                recognizedText.setX(x - 10);
                //                recognizedText.setY(y - 90);
                //                if (width > height) {
                //                    recognizedText.setTextSize(width / 2);
                //                } else {
                //                    recognizedText.setTextSize(height / 2);
                //                }
                //                gOverlay.addView(recognizedText);
                //                mathExpressionString += action;
            }
        }
    }

    private void keepGestureOnScreen(Gesture gesture) {
        GestureView newGestureView = new GestureView(this, gesture);
        gOverlay.addView(newGestureView);
    }

    private void clearSolution() {
        solutionView.setText("");
        mathExpressionString = "";
        solutionDisplayed = false;
    }
    private void clear() {
        gestureList.clear();
    }

    private void calculate() {
        //send formula for processing then write string to solutionView

        solutionView.setText("Solution to " + mathExpressionString + " goes here");
        solutionDisplayed = true;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calculator_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        boolean rtn = true;

        drawingMode = findViewById(R.id.mode);

        switch (item.getItemId()) {
            case R.id.action_draw:
                // User chose "draw" - only necessary if erase was previously selected
               drawingMode.setText(getString(R.string.draw_mode));
                Toast.makeText(getApplicationContext(),"draw selected",Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_erase:
                // User chose "erase"
                drawingMode.setText(getString(R.string.erase_mode));
                Toast.makeText(getApplicationContext(),"erase selected",Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_clear:
                // User chose "clear" - clear canvas and clear formula area
                Toast.makeText(getApplicationContext(),"clear selected",Toast.LENGTH_SHORT).show();
                gOverlay.removeAllViews();
                clear();
                break;

            case R.id.action_help:
                // User chose "help" - display help information
                Toast.makeText(getApplicationContext(),"help selected",Toast.LENGTH_SHORT).show();
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                rtn =  super.onOptionsItemSelected(item);
                break;

        }
        return rtn;
    }

}
