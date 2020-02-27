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

import java.util.Random;

public class PracticeActivity extends AppCompatActivity {

    private HandwritingView hwView;
    Random rand = new Random();
    int countproblems = 0;
    boolean arr[];

    Double answer = 0d;
    TextView practiceProblemView;   //text view where we show the practice problems

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        arr = new boolean[4];

        arr[0] = this.getIntent().getBooleanExtra("multiplyBox", false);
        arr[1] = this.getIntent().getBooleanExtra("divideBox", false);
        arr[2] = this.getIntent().getBooleanExtra("addBox", false);
        arr[3] = this.getIntent().getBooleanExtra("subBox", false);

        practiceProblemView = findViewById(R.id.practiceProblemsId);

        generateProblem();

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


        hwView = findViewById(R.id.handwriting);
//        TextView tv = findViewById(R.id.solution);
//        hwView.setTextArea(tv);
        if (!hwView.libraryLoaded) {
            Toast.makeText(this, "Gesture library did not load", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            GestureOverlayView gov = (GestureOverlayView) findViewById(R.id.overlay);
            hwView.setOverlayView(gov);

        }
    } //constructor

    private int chooseproblem() {
        return rand.nextInt(4);
    }

    private int singlenumbergeneator() {
        return rand.nextInt(10);
    }

    private int Doublenumbergeneator() {
        return rand.nextInt(100);
    }

    private int triplenumbergeneator() {
        return rand.nextInt(1000);
    }

    private void generateProblem() {
        int prob = chooseproblem();
        if (arr[0] || arr[1] || arr[2] || arr[3]) {
            while (!arr[prob]) {
                prob = chooseproblem();
            }
        }
        int num1 = 0, num2 = 1;
        if (countproblems < 15) {
            num1 = singlenumbergeneator();
            num2 = singlenumbergeneator();
        } else if (countproblems < 30) {
            num1 = Doublenumbergeneator();
            num2 = Doublenumbergeneator();
        } else {
            num1 = triplenumbergeneator();
            num2 = triplenumbergeneator();
        }

        switch (prob) {
            case 0:
                practiceProblemView.setText(num1+" X "+num2);
                answer=(double)num1*num2;
                break;
            case 1:
                if(num2==0){
                    num2=3;
                }
                practiceProblemView.setText(num1+" / "+num2);
                answer=(double)num1/num2;
                break;
            case 2:
                practiceProblemView.setText(num1+" + "+num2);
                answer=(double)num1+num2;
                break;
            case 3:
                practiceProblemView.setText(num1+" - "+num2);
                answer=(double)num1-num2;
                break;
        }
        countproblems++;
    }

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
        return answer;

    }

    //--------------------*/
    // checkAnswer        */
    //--------------------*/
    private void checkAnswer() {
        //Finalizes the last gesture instantly
        hwView.finalizeGesture();

        //send formula for processing then write string to solutionView
        Double answer = getAnswer();
        String answerString = answer.toString();

        String userString = hwView.getTextString();
        Double userAnswer = (Double) 0.0;
        boolean validNumber = true;
        if (userString != "") {
            try {
                userAnswer = Double.valueOf(userString);
            }
            catch (Exception e) {
                validNumber = false;
            }
        }
        //Should probably show some kind of popup here

        if (validNumber && userAnswer == answer) {
            Toast.makeText(this, "Your answer of " + userString + " is correct!", Toast.LENGTH_SHORT).show();
        }
        else if (validNumber && userAnswer != answer) {
            Toast.makeText(this, "Your answer of " + userString + " is not correct!", Toast.LENGTH_SHORT).show();
        }
        else if (validNumber == false) {
            Toast.makeText(this, "Your answer of " + userString + " contains invalid symbols (numbers only)!", Toast.LENGTH_SHORT).show();
        }
        clear();

    }

    //--------------------*/
    // showAnswer        */
    //--------------------*/
    private void showAnswer() {
        Double value = getAnswer();

        Toast.makeText(this, "The correct answer = " + value.toString(), Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getApplicationContext(), "Stroke width is at max value", Toast.LENGTH_SHORT).show();
                else {
                    hwView.setStrokeWidth(width + 1);
                }

                break;
            case R.id.action_dec:
                // User chose "help" - display help information
                if (width == UIConstants.MIN_STROKE_WIDTH)
                    Toast.makeText(getApplicationContext(), "Stroke width is at min value", Toast.LENGTH_SHORT).show();
                else {
                    hwView.setStrokeWidth(width - 1);
                }
                break;

            case R.id.action_help:
                // User chose "help" - display help information
                Toast.makeText(getApplicationContext(), "help selected", Toast.LENGTH_SHORT).show();
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                rtn = super.onOptionsItemSelected(item);
                break;

        }
        return rtn;
    }


}
