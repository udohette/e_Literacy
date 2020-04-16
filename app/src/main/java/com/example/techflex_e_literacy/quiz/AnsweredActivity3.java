package com.example.techflex_e_literacy.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AnsweredActivity3 extends AppCompatActivity {
    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";
    private InterstitialAd mInterstitialAd;
    String query;
    String answered;
    String totalq;
    String score;
    String timeLeft;
    private TextView mQuestionView,textView7,score_count,course_code,timeFinish;
    private Button mButtonChoice1;
    private Button mButtonChoice2;
    private Button mButtonChoice3, mButtonChoice4,next,back,quit;
    int total =0;
    long total_question_number = 0;
    int currentQuestion = 0;
    ArrayList questionno = new ArrayList();
    Map<String,String> map;
    String q;

    DatabaseReference databaseReference;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answered3);

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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("courseRegDb");
        mQuestionView = findViewById(R.id.question);
        mButtonChoice1 = findViewById(R.id.choice1);
        mButtonChoice2 = findViewById(R.id.choice2);
        mButtonChoice3 = findViewById(R.id.choice3);
        mButtonChoice4 = findViewById(R.id.choice4);
        textView7 = findViewById(R.id.question_count);
        score_count = findViewById(R.id.score);
        timeFinish = findViewById(R.id.textview_count_down);
        course_code = findViewById(R.id.course_code);
        //total_q = findViewById(R.id.question_count);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        quit = findViewById(R.id.quit);
        Intent i = getIntent();
        query = i.getStringExtra("query");
        timeLeft = i.getStringExtra("timeLeft");
        answered = i.getStringExtra("answered");
        score = i.getStringExtra("score");
        totalq = i.getStringExtra("total_question");
        String questions = i.getStringExtra("Total");
        String total_question = i.getStringExtra("total_question");

        textView7.setText("Total Answered: "+questions+"/"+total_question+"");
        score_count.setText("Correctly answered: "+score);
        timeFinish.setText(timeLeft);


        Log.i("yo_answerwd",query+"  "+answered);
        answered = answered.substring(1, answered.length()-1);           //remove curly brackets
        String[] keyValuePairs = answered.split(",");           //split the string to creat key-value pairs
        map= new HashMap<>();
        try {
            for (String pair : keyValuePairs)                        //iterate over the pairs
            {
                String[] entry = pair.split("=");                   //split the pairs to get key and value
                map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
                questionno.add(entry[0].trim());
            }
            Set k = map.keySet();
            Log.i("yo_answerwd",k.toString()+" "+questionno.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
//        Log.i("yo_answerwd",query+"  "+answered+"  "+map.keySet());
        total_question_number = map.size();
        updateQuestions(query,true);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                updateQuestions(query,true);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                updateQuestions(query,false);
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else {
                    //Do something else
                    finish();
                    startActivity(new Intent(AnsweredActivity3.this, UserActivity.class));
                }

            }
        });

    }

    void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AnsweredActivity3.this);
        builder.setTitle("Attention");
        builder.setMessage("Check spellings\nCheck internet connection if first-time use\nContact admin");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }

        });
        builder.show();


    }

    private void updateQuestions(final String query1, final boolean flag) {
        q = query1;
        Log.i("teating",query1);
        mButtonChoice1.setEnabled(true);
        mButtonChoice2.setEnabled(true);
        mButtonChoice3.setEnabled(true);
        mButtonChoice4.setEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/currentpq/" + query1.trim().toLowerCase());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    showPopUp();
                    return;
                }

                //if (dataSnapshot.exists()) {
                findViewById(R.id.quiz_layout).setVisibility(View.VISIBLE);
                //}

                Log.i("testing",total+"  "+total_question_number);

                if (flag)
                    total++;
                else
                    total--;

                if (total == 1){
                    back.setEnabled(false);
                }else
                    back.setEnabled(true);
                if (total > total_question_number) {
                    total--;
                    // open result activity
                    finish();
                    Intent i = new Intent(AnsweredActivity3.this, UserActivity.class);
                    startActivity(i);
                    mButtonChoice1.setEnabled(false);
                    mButtonChoice2.setEnabled(false);
                    mButtonChoice3.setEnabled(false);
                    mButtonChoice4.setEnabled(false);
                } else {
                    Log.i("yo_answerwd",total-1+"");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/currentpq/" + query1.trim().toLowerCase()).child(String.valueOf(questionno.get(total-1)));
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final QuestionLibrary questionLibrary = dataSnapshot.getValue(QuestionLibrary.class);
                                course_code.setText(query1.trim().toUpperCase());
                                mQuestionView.setText(questionLibrary.getQuestion());
                                mButtonChoice1.setText(questionLibrary.getOption1());
                                mButtonChoice2.setText(questionLibrary.getOption2());
                                mButtonChoice3.setText(questionLibrary.getOption3());
                                mButtonChoice4.setText(questionLibrary.getOption4());
                                currentQuestion++;

                                switch (Objects.requireNonNull(map.get(questionno.get(total-1)))){
                                    case "0":
                                        mButtonChoice1.setBackgroundColor(Color.RED);
                                        break;
                                    case "1":
                                        mButtonChoice2.setBackgroundColor(Color.RED);
                                        break;
                                    case "2":
                                        mButtonChoice3.setBackgroundColor(Color.RED);
                                        break;
                                    case "3":
                                        mButtonChoice4.setBackgroundColor(Color.RED);
                                        break;
                                }

                                if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())){
                                    mButtonChoice1.setBackgroundColor(Color.GREEN);
                                    Toast.makeText(AnsweredActivity3.this,questionLibrary.getAnswer().toUpperCase()+" was the right answer",Toast.LENGTH_LONG).show();
                                }else if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())){
                                    mButtonChoice2.setBackgroundColor(Color.GREEN);
                                    Toast.makeText(AnsweredActivity3.this,questionLibrary.getAnswer().toUpperCase()+" was the right answer",Toast.LENGTH_LONG).show();
                                }else if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())){
                                    mButtonChoice3.setBackgroundColor(Color.GREEN);
                                    Toast.makeText(AnsweredActivity3.this,questionLibrary.getAnswer().toUpperCase()+" was the right answer",Toast.LENGTH_LONG).show();
                                }else if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())){
                                    mButtonChoice4.setBackgroundColor(Color.GREEN);
                                    Toast.makeText(AnsweredActivity3.this,questionLibrary.getAnswer().toUpperCase()+" was the right answer",Toast.LENGTH_LONG).show();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(AnsweredActivity3.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AnsweredActivity3.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AnsweredActivity3.this, UserActivity.class));
        /*if (mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }else {
            //Do something else
            super.onBackPressed();
            startActivity(new Intent(AnsweredActivity3.this, UserActivity.class));
        }*/


    }

}

