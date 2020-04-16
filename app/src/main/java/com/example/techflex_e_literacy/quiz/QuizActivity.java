package com.example.techflex_e_literacy.quiz;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.UserActivity;
import com.example.techflex_e_literacy.model.Courses;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private ConstraintLayout mConstraintLayout;

    private EditText mEditTextInput;
    private TextView mScoreView;
    private Context context;
    private TextView mQuestionView, count_down, total_question, course_code, loadingCousreText;
    private Button mButtonChoice1;
    private Button mButtonChoice2;
    private Button mButtonChoice3, mButtonChoice4, quit,pause,reset,set;
    ProgressBar searchCourseProBar;
    Toolbar toolbar;
    private int mScore = 0;
    int total = 0;
    int points = 0;
    long total_question_number = 0;
    int currentQuestion = 0;
    int correct = 0;
    int wrong = 0;
    String query;
    String q;
    HashMap<Integer, Integer>  answered = new HashMap<>();
    DatabaseReference databaseReference;
    DatabaseReference mDatabaseReference;

    private CountDownTimer mCountDownTimer;
    private boolean mTimeRunning;
    private long mStartTimeInMillis;
    private long mEndTime;
    private long mTimeLeftInMillis;
    String timeLeftFormatted;

    //creating adBanners Variables;
    private AdView adView;
    private FrameLayout frameLayout;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd mAd;


    List<SubscriptionValidation> mValidationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);
        MobileAds.initialize(getApplicationContext(),getString(R.string.mobileAPiD));
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideo();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
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
               quitResult();
            }
        });

        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listView = findViewById(R.id.listview_sub);
        mValidationList = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("courseRegDb");


        mScoreView = findViewById(R.id.score);
        mQuestionView = findViewById(R.id.question);
        mButtonChoice1 = findViewById(R.id.choice1);
        mButtonChoice2 = findViewById(R.id.choice2);
        mButtonChoice3 = findViewById(R.id.choice3);
        mButtonChoice4 = findViewById(R.id.choice4);
        pause = findViewById(R.id.pause);
        reset = findViewById(R.id.button_reset);
        mEditTextInput = findViewById(R.id.edit_text_input);
        set = findViewById(R.id.button_set);
        count_down = findViewById(R.id.textview_count_down);
        searchCourseProBar = findViewById(R.id.courseSearch);
        total_question = findViewById(R.id.question_count);
        loadingCousreText = findViewById(R.id.loadingCourse);
        mConstraintLayout = findViewById(R.id.timerLayout);
        course_code = findViewById(R.id.course_code);
        quit = findViewById(R.id.quit);



        handleIntent(getIntent());
        //num = FirebaseDatabase.getInstance().getReference("NumberOfQuestion");
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp3();

            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0){
                    Toast.makeText(QuizActivity.this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0){
                    Toast.makeText(QuizActivity.this, "Please enter a Positive Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                mEditTextInput.setText("");
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeRunning){
                    pauseTimer();
                }else{
                    startTimer();
                    mButtonChoice1.setEnabled(true);
                    mButtonChoice2.setEnabled(true);
                    mButtonChoice3.setEnabled(true);
                    mButtonChoice4.setEnabled(true);
                }

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();

            }
        });

    }
    public void loadRewardedVideo(){
        if (!mAd.isLoaded()){
            mAd.loadAd(getString(R.string.rewardedVideo_ad_unit),new AdRequest.Builder().build());
        }
    }
    public void setTime(long milliseconds){
        mStartTimeInMillis = milliseconds;
        resetTimer();
        stopkeyboard();

    }
    public void startTimer(){
        mEndTime = System.currentTimeMillis()+mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                upDateCountDownText();
            }

            @Override
            public void onFinish() {
               mTimeRunning = false;
                mButtonChoice1.setEnabled(false);
                mButtonChoice2.setEnabled(false);
                mButtonChoice3.setEnabled(false);
                mButtonChoice4.setEnabled(false);
                count_down.setText("Time Up!");
                count_down.setTextColor(Color.WHITE);
                reset.setText("Reset Timer");
                pause.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.VISIBLE);
                mConstraintLayout.setVisibility(View.VISIBLE);
                quit.setText("View Result");
              //updateWatchInterface();
                quit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("yo",answered.toString());
                        Intent intent = new Intent(QuizActivity.this, Result_Activity.class);
                        intent.putExtra("Total", String.valueOf(total));
                        intent.putExtra("Correct", String.valueOf(correct));
                        intent.putExtra("Incorrect", String.valueOf(wrong));
                        intent.putExtra("points", String.valueOf(points));
                        intent.putExtra("total_question", String.valueOf(total_question_number));
                        intent.putExtra("query",q);
                        intent.putExtra("answered",answered.toString());
                        intent.putExtra("score", String.valueOf(mScore));
                        startActivity(intent);
                    }
                });


            }

        }.start();
        mTimeRunning = true;
        updateWatchInterface();
    }
    public void pauseTimer(){
        if (mAd.isLoaded()){
            mAd.show();
        }else {
            mButtonChoice1.setEnabled(false);
            mButtonChoice2.setEnabled(false);
            mButtonChoice3.setEnabled(false);
            mButtonChoice4.setEnabled(false);
            mCountDownTimer.cancel();
            mTimeRunning = false;
            updateWatchInterface();
        }
        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideo();
                mButtonChoice1.setEnabled(false);
                mButtonChoice2.setEnabled(false);
                mButtonChoice3.setEnabled(false);
                mButtonChoice4.setEnabled(false);
                mCountDownTimer.cancel();
                mTimeRunning = false;
                updateWatchInterface();

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Toast.makeText(context, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                        rewardItem.getAmount(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {
                loadRewardedVideo();
                mButtonChoice1.setEnabled(false);
                mButtonChoice2.setEnabled(false);
                mButtonChoice3.setEnabled(false);
                mButtonChoice4.setEnabled(false);
                mCountDownTimer.cancel();
                mTimeRunning = false;
                updateWatchInterface();

            }
        });
    }
    public void resetTimer(){
        mButtonChoice1.setEnabled(false);
        mButtonChoice2.setEnabled(false);
        mButtonChoice3.setEnabled(false);
        mButtonChoice4.setEnabled(false);
        mTimeLeftInMillis = mStartTimeInMillis;
        upDateCountDownText();
        updateWatchInterface();

    }
    public void upDateCountDownText(){
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) /60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        if (hours > 0){
            timeLeftFormatted = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,seconds);
        }else {
            timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        }

        count_down.setText(timeLeftFormatted);
        if (mTimeLeftInMillis < 6000) {
            count_down.setTextColor(Color.RED);
        } else {
            count_down.setTextColor(Color.WHITE);
        }
    }
    public void updateWatchInterface(){
        if (mTimeRunning){
            quit.setText("Stop Quiz");
            mConstraintLayout.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
            pause.setText("Pause");
        }else {
            mConstraintLayout.setVisibility(View.VISIBLE);
            pause.setText("Start");
            quit.setText("View Result");
            if (mTimeLeftInMillis < 1000){
                pause.setVisibility(View.INVISIBLE);
                quit.setText("Stop Quiz");
            }else {
                pause.setVisibility(View.VISIBLE);
                quit.setText("Stop Quiz");
            }
            if (mTimeLeftInMillis < mStartTimeInMillis){
                reset.setVisibility(View.VISIBLE);
                quit.setText("Stop Quiz");

            }else {
                reset.setVisibility(View.INVISIBLE);
                quit.setText("Stop  Quiz");
            }
        }
    }
    private void stopkeyboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeMillis", mStartTimeInMillis);
       editor.putLong("millisLeft",mTimeLeftInMillis);
        editor.putBoolean("timerRunning",mTimeRunning);
        editor.putLong("endTime", mEndTime);
       editor.apply();
        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
    }

   @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong("startTimeMillis",3000000);
       mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
       mTimeRunning  = prefs.getBoolean("timerRunning",false);
        mEndTime = prefs.getLong("endTime",0);
      if (mTimeLeftInMillis < 0){
           mTimeLeftInMillis = 0;
           mTimeRunning = false;
           upDateCountDownText();
           updateWatchInterface();
       }else {
           startTimer();
           mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
       }

    }

    public void quitResult(){
        Log.i("yo",answered.toString());
        Intent intent = new Intent(QuizActivity.this, Result_Activity.class);
        intent.putExtra("Total", String.valueOf(total));
        intent.putExtra("Correct", String.valueOf(correct));
        intent.putExtra("Incorrect", String.valueOf(wrong));
        intent.putExtra("points", String.valueOf(points));
        intent.putExtra("total_question", String.valueOf(total_question_number));
        intent.putExtra("query",q);
        intent.putExtra("answered",answered.toString());
        intent.putExtra("score", String.valueOf(mScore));
        startActivity(intent);
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            mDatabaseReference.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        try {
                            Courses c = d.getValue(Courses.class);
                            Log.i("testing_courses",c.getEndDate());
                            if (c.getCourseReg().contains(query)){
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
                                Date strDate = dateFormat.parse(c.getEndDate());
                                if (!new Date().after(strDate)) {
                                    //startQuiz();
                                    return;
                                }else {
                                   // Toast.makeText(QuizActivity.this,"Expired",Toast.LENGTH_LONG).show();
                                    expiredSubscriptionPopUp();
                                }

                            }else {
                                Toast.makeText(QuizActivity.this,"You didn't Subscribe for this course",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(QuizActivity.this, QuizActivity.class);
                                startActivity(intent);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            startQuiz();
        }

    }
    void startQuiz() {
        new AlertDialog.Builder(QuizActivity.this)
                .setTitle("!Attention")
                .setMessage("Are You ready To  Start Quiz?")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //startTimer();
                       updateQuestions(query);

                    }

                }).setNegativeButton("Not ready", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setCancelable(false).show();
    }

    void showPopUp3() {
         new AlertDialog.Builder(QuizActivity.this)
        .setTitle("Attention!")
        .setMessage("are you  sure you  want to  quit?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }else {
                    //Do something else
                    quitResult();
                }

            }

        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setCancelable(false).show();

    }

    void showPopUp() {
        new AlertDialog.Builder(QuizActivity.this)
        .setTitle("Attention")
        .setMessage("Check spellings\nCheck internet connection if first-time use\nContact admin")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }

        }).setCancelable(false).show();


    }

    void expiredSubscriptionPopUp() {
         new AlertDialog.Builder(QuizActivity.this)
        .setTitle("Attention!")
        .setMessage("Your Subscription has Expired, Kindly renew")
        .setPositiveButton("Renew", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(QuizActivity.this, Bill.class);
                startActivity(intent);
            }

        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(QuizActivity.this, UserActivity.class);
                startActivity(intent);
            }
        }).setCancelable(false).show();

    }

    private void updateQuestions(final String query1) {
        q = query1;
        Log.i("teating",query1);
        mButtonChoice1.setEnabled(true);
        mButtonChoice2.setEnabled(true);
        mButtonChoice3.setEnabled(true);
        mButtonChoice4.setEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/quiz/" + query1.trim().toLowerCase());
        searchCourseProBar.setVisibility(View.VISIBLE);
        loadingCousreText.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchCourseProBar.setVisibility(View.GONE);
                loadingCousreText.setVisibility(View.GONE);
                if (!dataSnapshot.exists()) {
                    showPopUp();
                    return;
                }
                    //if (dataSnapshot.exists()) {
                    findViewById(R.id.quiz_layout).setVisibility(View.VISIBLE);
                    total_question_number = (dataSnapshot.getChildrenCount());
                    total_question.setText("Question: " + currentQuestion + "/" + total_question_number + "");
                    course_code.setText(query1.trim().toUpperCase());
                    //startTimer(2000, count_down);
                    //}

                    total++;

                    if (total > total_question_number) {
                        total--;
                        Log.i("yo", answered.toString() + total);
                        // open result activity
                        finish();
                        Intent i = new Intent(QuizActivity.this, Result_Activity.class);
                        i.putExtra("Total", String.valueOf(total));
                        i.putExtra("Correct", String.valueOf(correct));
                        i.putExtra("Incorrect", String.valueOf(wrong));
                        i.putExtra("points", String.valueOf(points));
                        i.putExtra("total_question", String.valueOf(total_question_number));
                        i.putExtra("query",query1);
                        i.putExtra("answered",answered.toString());
                        i.putExtra("score",mScore);
                        i.putExtra("current_q",currentQuestion);

                        startActivity(i);
                       // stopTimer();
                        mButtonChoice1.setEnabled(false);
                        mButtonChoice2.setEnabled(false);
                        mButtonChoice3.setEnabled(false);
                        mButtonChoice4.setEnabled(false);
                }else {
                    databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("e_literacy/exam/quiz/" + query1.trim().toLowerCase()).child(String.valueOf(total));
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final QuestionLibrary questionLibrary = dataSnapshot.getValue(QuestionLibrary.class);
                                mQuestionView.setText(questionLibrary.getQuestion());
                                mButtonChoice1.setText(questionLibrary.getOption1());
                                mButtonChoice2.setText(questionLibrary.getOption2());
                                mButtonChoice3.setText(questionLibrary.getOption3());
                                mButtonChoice4.setText(questionLibrary.getOption4());
                                currentQuestion++;


                                mButtonChoice1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total,0);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            mButtonChoice1.setBackgroundColor(Color.GREEN);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correct++;
                                                    points = points + 15;
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);

                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);
                                                }
                                            }, 1500);
                                        } else {
                                            answered.put(total,0);
                                            Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points - 5;
                                            mButtonChoice1.setBackgroundColor(Color.RED);
                                            if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice2.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice3.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice4.setBackgroundColor(Color.GREEN);
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);

                                                }
                                            }, 1500);
                                        }
                                    }
                                });
                                mButtonChoice2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total,1);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            mButtonChoice2.setBackgroundColor(Color.GREEN);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correct++;
                                                    points = points + 15;
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);
                                                }
                                            }, 1500);
                                        } else {
                                            answered.put(total,1);
                                            Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points - 5;
                                            mButtonChoice2.setBackgroundColor(Color.RED);
                                            if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice1.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice3.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice4.setBackgroundColor(Color.GREEN);
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);

                                                }
                                            }, 1500);
                                        }
                                    }
                                });
                                mButtonChoice3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total,2);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            mButtonChoice3.setBackgroundColor(Color.GREEN);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correct++;
                                                    points = points + 15;
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);
                                                }
                                            }, 1500);
                                        } else {
                                            answered.put(total,2);
                                            Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points - 5;
                                            mButtonChoice3.setBackgroundColor(Color.RED);
                                            if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice1.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice2.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice4.setBackgroundColor(Color.GREEN);
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);

                                                }
                                            }, 1500);
                                        }
                                    }
                                });
                                mButtonChoice4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total,3);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            mButtonChoice4.setBackgroundColor(Color.GREEN);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correct++;
                                                    points = points + 15;
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);
                                                }
                                            }, 1500);
                                        } else {
                                            answered.put(total,3);
                                            Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points - 5;
                                            mButtonChoice4.setBackgroundColor(Color.RED);
                                            if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice1.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice2.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice3.setBackgroundColor(Color.GREEN);
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);

                                                }
                                            }, 1500);
                                        }
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(QuizActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateScore(int point) {
        mScoreView.setText("Score: " + mScore);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    public Boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnected()))
                return true;
            else return false;
        } else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
    @Override
    public void onBackPressed() {
        interstitialAd.setAdListener(new AdListener() {
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
                startActivity(new Intent(QuizActivity.this, UserActivity.class));
                finish();
            }
        });

        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }else {
            //Do something else
            super.onBackPressed();
            mCountDownTimer.cancel();
            startActivity(new Intent(QuizActivity.this, UserActivity.class));
            finish();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideo();

    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {
        quitResult();

    }

    @Override
    protected void onPause() {
        mAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAd.destroy(this);
        super.onDestroy();
    }
}
