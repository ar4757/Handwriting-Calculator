package com.nui.handwritingcalculator;

import android.gesture.GestureOverlayView;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.mariuszgromada.math.mxparser.Expression;

public class PracticeActivity extends AppCompatActivity {

    private HandwritingView hwView;
    TextView practiceProblemView;   //text view where we show the practice problems

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button checkAnswerButton = findViewById(R.id.checkBtn);
        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        Button showAnswerButton = findViewById(R.id.showBtn);
        showAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
            }
        });



        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.design_default_color_on_secondary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        practiceProblemView = findViewById(R.id.practiceProblemsId);

        hwView =  findViewById(R.id.handwriting);
//        TextView tv = findViewById(R.id.solution);
//        hwView.setTextArea(tv);
        if (!hwView.libraryLoaded) {
            Toast.makeText(this, "Gesture library did not load", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            GestureOverlayView gov =  (GestureOverlayView) findViewById(R.id.overlay);
            hwView.setOverlayView(gov);

        }
    } //constructor



    private void undo() {
        hwView.undo();
//        solutionView.setText(hwView.getTextString());
    } //undo

    //--------------------*/
    // clearSolution      */
    //--------------------*/
    private void clearSolution() {
//        solutionView.setText("");
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
    // getAnswer          */
    //--------------------*/
    private double getAnswer() {
        CharSequence txt = practiceProblemView.getText();
        String equation = txt.toString();

        //now convert string to a proper math expression, get the answer and return it
        //temporarily returning answer to 4 x 3

        return 12;

    }
    //--------------------*/
    // checkAnswer        */
    //--------------------*/
    private void checkAnswer() {
        //send formula for processing then write string to solutionView
        Double answer = getAnswer();
        String answerString = answer.toString();

        String userString = hwView.getTextString();
        Double userAnswer = (Double)0.0;
        if (userString != "") {
            userAnswer = Double.valueOf(userString);
        }
        //Should probably show some kind of popup here

        if (userAnswer == answer) {
            Toast.makeText(this, "Your answer of " + userString+ " is correct!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Your answer of " + userString+ " is not correct!", Toast.LENGTH_SHORT).show();

        }
        clear();

    }

    //--------------------*/
    // showAnswer        */
    //--------------------*/
    private void showAnswer() {
        Double value = getAnswer();

        Toast.makeText(this, "The correct answer = "+value.toString(), Toast.LENGTH_SHORT).show();

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
