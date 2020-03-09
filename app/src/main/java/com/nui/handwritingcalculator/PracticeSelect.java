package com.nui.handwritingcalculator;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class PracticeSelect extends AppCompatActivity {

    CheckBox multiplyCheckbox;
    CheckBox divideCheckbox;
    CheckBox addCheckbox;
    CheckBox subCheckbox;
    Button submitButton;

    Boolean multi;
    Boolean divide;
    Boolean add;
    Boolean sub;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.practice_select);

      //  Toolbar toolbar = findViewById(R.id.toolbar);
       // toolbar.setTitleTextColor(getResources().getColor(R.color.design_default_color_on_secondary));

      //  setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.design_default_color_on_secondary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.custom_background));

        String text = getString(R.string.practice_select);
        getSupportActionBar().setTitle(Html.fromHtml(text));

        multi=false;
        divide=false;
        add=false;
        sub=false;

        //CheckBox variable added
        multiplyCheckbox = findViewById(R.id.multiply_checkbox);
        divideCheckbox = findViewById(R.id.divide_checkbox);
        addCheckbox = findViewById(R.id.add_checkbox);
        subCheckbox = findViewById(R.id.sub_checkbox);
        submitButton =findViewById(R.id.practiceSelectButton);


        multiplyCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiplyCheckbox.isChecked()){
                    multi=true;
//                    Toast.makeText(getApplicationContext(),"Multi Checked",Toast.LENGTH_SHORT).show();
                }
                else multi=false;
            }
        });

        divideCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divideCheckbox.isChecked()){
                    divide=true;
//                    Toast.makeText(getApplicationContext(),"divide Checked",Toast.LENGTH_SHORT).show();
                }
                else divide=false;
            }
        });

        addCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addCheckbox.isChecked()){
                    add=true;
//                    Toast.makeText(getApplicationContext(),"add Checked",Toast.LENGTH_SHORT).show();
                }
                else add=false;
            }
        });

        subCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCheckbox.isChecked()){
                    sub=true;
//                    Toast.makeText(getApplicationContext(),"Subtract Checked",Toast.LENGTH_SHORT).show();
                }
                else sub=false;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PracticeSelect.this, PracticeActivity.class);
                i.putExtra("multiplyBox",multi);
                i.putExtra("divideBox",divide);
                i.putExtra("addBox",add);
                i.putExtra("subBox",sub);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        boolean rtn = true;

        switch(item.getItemId()) {
            case R.id.action_help:
                Intent i = new Intent(PracticeSelect.this, prac_select_help.class);
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
