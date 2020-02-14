package com.nui.handwritingcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class PracticeSelect extends AppCompatActivity {

    CheckBox multiplyCheckbox;
    CheckBox divideCheckbox;
    CheckBox addCheckbox;
    CheckBox subCheckbox;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_select);

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
                    Toast.makeText(getApplicationContext(),"Multi Checked",Toast.LENGTH_SHORT).show();
                }
            }
        });

        divideCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(divideCheckbox.isChecked()){
                    Toast.makeText(getApplicationContext(),"divide Checked",Toast.LENGTH_SHORT).show();
                }
            }
        });

        addCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addCheckbox.isChecked()){
                    Toast.makeText(getApplicationContext(),"add Checked",Toast.LENGTH_SHORT).show();
                }
            }
        });

        subCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCheckbox.isChecked()){
                    Toast.makeText(getApplicationContext(),"Subtract Checked",Toast.LENGTH_SHORT).show();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
