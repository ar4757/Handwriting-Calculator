package com.nui.handwritingcalculator;

import android.content.Intent;
import android.gesture.Prediction;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Gesture;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Stack;
import com.nui.handwritingcalculator.UIConstants;

import static android.gesture.GestureStore.ORIENTATION_INVARIANT;
import static android.gesture.GestureStore.ORIENTATION_SENSITIVE;
import static android.gesture.GestureStore.SEQUENCE_INVARIANT;
import org.mariuszgromada.math.mxparser.*;
import static com.nui.handwritingcalculator.UIConstants.MAX_STROKE_WIDTH;
import static com.nui.handwritingcalculator.UIConstants.MIN_STROKE_WIDTH;

public class CalculatorActivity extends AppCompatActivity {

    private HandwritingView hwView;

    private boolean resultDisplayed=false;

    TextView solutionView;   //text view where we show what the user wrote and the solution


    //*--------------------
    //* onCreate
    //*--------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Toast.makeText(this, "Calculator", Toast.LENGTH_SHORT).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button calculateButton = findViewById(R.id.calculate_btn);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TextView solutionText = findViewById(R.id.solution);
                calculate();
            }
        });

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.design_default_color_on_secondary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        solutionView = findViewById(R.id.solution);

        hwView =  findViewById(R.id.handwriting);
        TextView tv = findViewById(R.id.solution);
        hwView.writeGestureStringToTextArea (tv);
        if (!hwView.libraryLoaded) {
            Toast.makeText(this, "Gesture library did not load", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            GestureOverlayView gov =  (GestureOverlayView) findViewById(R.id.overlay);
            hwView.setOverlayView(gov);

        }
    }



    private void undo() {
            hwView.undo();
            solutionView.setText(hwView.getTextString());
    } //undo

    //--------------------*/
    // clearSolution      */
    //--------------------*/
    private void clearSolution() {
        solutionView.setText("");
        hwView.clearText();
    } //clearSolution


    //--------------------*/
    // clear              */
    //--------------------*/
    private void clear() {
        clearSolution();
        hwView.clear();
    } //clear

    //--------------------*/
    // calculate          */
    //--------------------*/
    private void calculate() {
        //send formula for processing then write string to solutionView
        String solution = hwView.getTextString();
        if (solution != "") {

            solution = solution.replace('x', '*');
            clear();
            //create an expression
            Expression e = new Expression(solution);
            Double value = e.calculate();
            solution = value.toString();

        }
        else solution = " ";
        solutionView.setText("Solution is: " + solution);
        hwView.setText(solution);
        hwView.onGestureResetText();

    }
    //---------------------*/
    // onCreateOptionsMenu */
    //---------------------*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calculator_menu, menu);
        return true;
    }


    //-----------------------*/
    // onOptionsItemSelected */
    //-----------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        boolean rtn = true;
        float width = hwView.getStrokeWidth();

        switch (item.getItemId()) {

            case R.id.action_undo:
                // User chose "undo" - undo the last gesture
                undo();
                break;

            case R.id.action_clear:
                // User chose "clear" - clear canvas and clear formula area
                clear();
                break;

            case R.id.action_inc:
                // User chose "help" - display help information
                if (width == UIConstants.MAX_STROKE_WIDTH)
                    Toast.makeText(getApplicationContext(),"Stroke width is at max value",Toast.LENGTH_SHORT).show();
                else {
                    hwView.setStrokeWidth(width+1);
                }

                break;
            case R.id.action_dec:
                // User chose "help" - display help information
                if (width == UIConstants.MIN_STROKE_WIDTH)
                    Toast.makeText(getApplicationContext(),"Stroke width is at min value",Toast.LENGTH_SHORT).show();
                else {
                    hwView.setStrokeWidth(width-1);
                }
                break;

            case R.id.action_help:
                Intent i = new Intent(CalculatorActivity.this, help_calculator.class);
                startActivity(i);
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
