package com.nui.handwritingcalculator;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class PracticeSelect extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_select);

        Toolbar toolbar = findViewById(R.id.practiceToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Practise");
    }
}
