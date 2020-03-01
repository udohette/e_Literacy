package com.example.techflex_e_literacy.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.UserActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Result_Activity3 extends AppCompatActivity implements View.OnClickListener {
    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";
    private InterstitialAd mInterstitialAd;
    TextView textView4,textView5,textView6,textView7,count_down;
    Toolbar toolbar;
    Button retake,btnWrongQstns;

    String query;
    String answered;
    String answer;

    String total_question;
    String questions;
    String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_3);
        //displaying adds
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("313C6DAD9D1C192244C9AB5CCC279361")
                .build();
        //creating interstitialAd
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                // Load the next interstitial.
                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                onBackPressed();
                onSupportNavigateUp();
            }
        });

        btnWrongQstns = findViewById(R.id.btnWrongQstns);

        textView4 = findViewById(R.id.txtCorrectAns);
        textView5 = findViewById(R.id.txtWrongAns);
        textView6 = findViewById(R.id.tvPerc);
        retake = findViewById(R.id.retake_quiz);
        textView7 = findViewById(R.id.total_question_answered);
        count_down = findViewById(R.id.textview_count_down);

        retake.setOnClickListener(this);
        btnWrongQstns.setOnClickListener(this);

        toolbar = findViewById(R.id.toolbar);
        (getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        questions = i.getStringExtra("Total");
        String correct = i.getStringExtra("Correct");
        String wrong = i.getStringExtra("Incorrect");
        String points = i.getStringExtra("points");
        score = i.getStringExtra("score");
        total_question = i.getStringExtra("total_question");
        String total_question = i.getStringExtra("total_question");
        query = i.getStringExtra("query");
        answer = i.getStringExtra("answer");
        answered = i.getStringExtra("answered");
        Log.i("yo_result",query+"  "+answered);


        textView7.setText("Total Questions Answered: "+questions+"/"+total_question+"");
        textView5.setText("Wrong Question Answered: "+wrong);
        textView4.setText("Correct Question Answered: "+correct);
        textView6.setText(""+points+"%");

    }

    @Override
    public void onClick(View view) {
        if (view == retake) {
            if (mInterstitialAd.isLoaded()){
                mInterstitialAd.show();
            }else {
                //Do something else
                onBackPressed();
                finish();
            }
        }else if (view.getId() == R.id.btnWrongQstns){
            if (answered.length()>2) {
                Intent i = new Intent(Result_Activity3.this, AnsweredActivity3.class);
                i.putExtra("query", query);
                i.putExtra("answered", answered);
                i.putExtra("total_question",total_question);
                i.putExtra("Total",questions);
                i.putExtra("score",score);

                startActivity(i);
            }else {
                Toast.makeText(this,"No questions answered",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean onSupportNavigateUp() {
        if (mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }else {
            onBackPressed();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }else {
            //Do something else
            super.onBackPressed();
            startActivity(new Intent(Result_Activity3.this, UserActivity.class));
            //no  back tracking after click  on  back  button
            finish();

        }

    }
}
