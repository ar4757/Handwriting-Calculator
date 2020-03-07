package com.nui.handwritingcalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class help extends AppCompatActivity {

    private ScrollView scrollView;
    private TextView calc_help;
    private TextView prac_help;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.design_default_color_on_secondary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        final Button calcHelpButton = findViewById(R.id.calc_help_button);
        final Button pracHelpButton = findViewById(R.id.practice_help_button);

        scrollView = findViewById(R.id.scrollView_help);
        calc_help = findViewById(R.id.Calculator_Help);
        prac_help = findViewById(R.id.Practice_Help);


        calcHelpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                focusOnView(calc_help);
                pracHelpButton.setPaintFlags(pracHelpButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                calcHelpButton.setPaintFlags(calcHelpButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        });

        pracHelpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                focusOnView(prac_help);
                calcHelpButton.setPaintFlags(calcHelpButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                pracHelpButton.setPaintFlags(pracHelpButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        });

        TextView view1 = (TextView)findViewById(R.id.calc_help_text);

        String text1 = getString(R.string.html_calc_help);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view1.setText(Html.fromHtml(text1,Html.FROM_HTML_MODE_LEGACY));
        } else {
            view1.setText(Html.fromHtml(text1));
        }

        TextView view2 = (TextView)findViewById(R.id.prac_help_text);

        String text2 = getString(R.string.html_practice_help);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view2.setText(Html.fromHtml(text2,Html.FROM_HTML_MODE_LEGACY));
        } else {
            view2.setText(Html.fromHtml(text2));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void focusOnView(final TextView t){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, t.getTop());
            }
        });
    }



}
