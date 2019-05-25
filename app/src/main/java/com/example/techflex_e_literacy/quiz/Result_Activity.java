package com.example.techflex_e_literacy.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.UserActivity;

public class Result_Activity extends AppCompatActivity implements View.OnClickListener {
    TextView textView4,textView5,textView6;
    Toolbar toolbar;
    Button retake, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        textView4  = findViewById(R.id.textView4);
        textView5  = findViewById(R.id.textView5);
        textView6  = findViewById(R.id.textView6);
        retake = findViewById(R.id.retake_button);
        end = findViewById(R.id.end_button);

        end.setOnClickListener(this);
        retake.setOnClickListener(this);

        Intent i = getIntent();
        String questions  = i.getStringExtra("Total");
        String correct  = i.getStringExtra("Correct");
        String wrong  = i.getStringExtra("Incorrect");

        textView4.setText(questions);
        textView5.setText(correct);
        textView6.setText(wrong);

        toolbar = findViewById(R.id.toolbar);
        (getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public void onClick(View view) {
        if (view == end) {
            startActivity(new Intent(Result_Activity.this, UserActivity.class));
        }
        if (view == retake) {
            startActivity(new Intent(Result_Activity.this, QuizActivity.class));
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
