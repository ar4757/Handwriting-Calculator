package com.nui.handwritingcalculator;

import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;

public class PracticeActivity extends AppCompatActivity {

    private HandwritingView hwView;
    Random rand = new Random();
    int countproblems = 0;
    int scores = 0;
    boolean arr[];

    Double answer = 0d;
    TextView practiceProblemView;   //text view where we show the practice problems
    TextView inputResultView;
    TextView scorecard;
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
        inputResultView = findViewById(R.id.GestureFeedback);
        scorecard = findViewById(R.id.scores);

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

        hwView.writeGestureStringToTextArea (inputResultView);

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
        return rand.nextInt(10)+1;
    }

    private int Doublenumbergeneator() {
        return rand.nextInt(100)+1;
    }

    private int triplenumbergeneator() {
        return rand.nextInt(1000)+1;
    }

    private void generateProblem() {
        int prob = chooseproblem();
        if(scores!=0){
            double stemp = (scores * 100)/countproblems;
            String result = String.format("%.1f", stemp);
            scorecard.setText("current scores "+scores +"/"+countproblems+"("+result+")%");
        }
        if (arr[0] || arr[1] || arr[2] || arr[3]) {
            while (!arr[prob]) {
                prob = chooseproblem();
            }
        }
        int num1 = 0, num2 = 1;
        if (countproblems < 8) {
            num1 = singlenumbergeneator();
            num2 = singlenumbergeneator();
        } else if (countproblems < 16) {
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
        double answer = getAnswer();
        String answerString = Double.toString(answer);

        String userString = hwView.getTextString();
        userString = userString.replaceAll(" ","");
        double userAnswer = 0.0;
        boolean validNumber = true;
        String b1Title = getString(R.string.retry); //"TRY AGAIN";
        String b2Title = getString(R.string.next); //"NEXT PROBLEM";
        String title;
        String message;

        if (userString != "") {
            try {
                userAnswer = Double.valueOf(userString);
            }
            catch (Exception e) {
                validNumber = false;
            }
        }
        else {
            validNumber = false;
            userString = " ";
        }
        //Show popup here

        if (validNumber) {
            if (userAnswer == answer) {
                title = "CORRECT!";
                message = "Your answer of " + userString + " is correct!";
                b1Title = "";
                scores++;
            }
            else {
                title = "WRONG!";
                message = "Your answer of " + userString + " is not correct!";
            }
        }
        else {
            title = "ERROR!";
            message = "Your answer of " + userString + " contains invalid symbols.\n\nPlease enter numbers only.";
        }
        customPopUp(title, message, b1Title, b2Title);
        clear();
        inputResultView.setText("");
    }


    private void customPopUp(String title, String message, String b1Title, String b2Title) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_popup_check_answer, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        TextView header = (TextView) dialogView.findViewById(R.id.text_heading);
        header.setText(title);
        TextView description = (TextView) dialogView.findViewById(R.id.text_description);
        description.setText(message);

        Button btn1 = (Button) dialogView.findViewById(R.id.btn1);
        Button btn2 = (Button) dialogView.findViewById(R.id.btn2);
        btn1.setText(b1Title);
        btn2.setText(b2Title);

         btn2.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 alertDialog.dismiss();
                 generateProblem();
             }
         });

        RelativeLayout btnLayout = (RelativeLayout) dialogView.findViewById(R.id.b1Layout);
        LinearLayout btnView = (LinearLayout) btnLayout.getParent();

        if (b1Title == "") {
           btnView.removeView(btnLayout);
        }
        else {
            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //exit the practice mode
                    alertDialog.dismiss();
//                finish();
                }
            });
            btn1.setVisibility(View.VISIBLE);

        }


//        switch (title){
//            case "CORRECT!":
//                btn1.setText("NEXT PROBLEM");
//                btn1.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                        generateProblem();
//                    }
//                });
//                btn2.setVisibility(View.GONE);
//                break;
//
//            case "WRONG!":
//                btn1.setText("RETRY");
//                btn1.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                    }
//                });
//                btn2.setVisibility(View.VISIBLE);
//                btn2.setText("NEXT PROBLEM");
//                btn2.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                        generateProblem();
//                    }
//                });
//                break;
//            case "ANSWER":
//                btn1.setText("TRY PROBLEM");
//                btn1.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                    }
//                });                break;
//            default:
//                btn1.setText("RETRY");
////                btn1.setText("DISMISS ERROR");
//                btn1.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                    }
//                });
//
//        }



        alertDialog.show();
    }

    //--------------------*/
    // showAnswer        */
    //--------------------*/
    private void showAnswer() {
        double value = getAnswer();

        String title = "ANSWER";
        String message = "The correct answer = " + value;
        customPopUp(title,message,"", getString(R.string.next));
        inputResultView.setText("");
        clear();
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
                inputResultView.setText(hwView.getTextString());
                break;

            case R.id.action_clear:
                // User chose "clear" - clear canvas and clear formula area
                clear();
                inputResultView.setText("");
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
                Intent i = new Intent(PracticeActivity.this, help_practice.class);
               startActivity(i);
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
