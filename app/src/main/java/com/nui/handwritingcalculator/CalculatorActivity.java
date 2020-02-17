package com.nui.handwritingcalculator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;

import androidx.appcompat.widget.Toolbar;

public class CalculatorActivity extends AppCompatActivity {

    HandwritingView hwView;
    TextView drawingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hwView =  findViewById(R.id.handwriting);
        drawingMode = findViewById(R.id.mode);

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
                hwView.clearScreen();

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
