package com.example.techflex_e_literacy.quiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.techflex_e_literacy.R;

public class Result_Activity extends AppCompatActivity {
    TextView textView4,textView5,textView6;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        textView4  = findViewById(R.id.textView4);
        textView5  = findViewById(R.id.textView5);
        textView6  = findViewById(R.id.textView6);

        Intent i = getIntent();
        String questions  = i.getStringExtra("Total");
        String correct  = i.getStringExtra("Correct");
        String wrong  = i.getStringExtra("Incorrect");

        textView4.setText(questions);
        textView5.setText(correct);
        textView6.setText(wrong);

        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
