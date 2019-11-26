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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TrialActivity extends AppCompatActivity {
    private static final long  START_TIME_IN_MILLIS = 2700000;

    private static final String DEFAULT = "N/A";

    private TextView mScoreView;
    private TextView mQuestionView, count_down, total_question, course_code, loadingCousreText;
    private Button mButtonChoice1;
    private Button mButtonChoice2;
    private Button mButtonChoice3, mButtonChoice4, quit,pause,reset;
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
    HashMap<Integer, Integer> answered = new HashMap<>();
    DatabaseReference databaseReference;
    DatabaseReference mDatabaseReference;

    private CountDownTimer mCountDownTimer;
    private boolean mTimeRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    String timeLeftFormatted;


    List<SubscriptionValidation> mValidationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz2);





        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listView = findViewById(R.id.listview_sub);
        mValidationList = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("courseRegDb");


        mScoreView = findViewById(R.id.score);
        pause = findViewById(R.id.pause);
        mQuestionView = findViewById(R.id.question);
        mButtonChoice1 = findViewById(R.id.choice1);
        mButtonChoice2 = findViewById(R.id.choice2);
        mButtonChoice3 = findViewById(R.id.choice3);
        mButtonChoice4 = findViewById(R.id.choice4);
        count_down = findViewById(R.id.textview_count_down);
        searchCourseProBar = findViewById(R.id.courseSearch);
        total_question = findViewById(R.id.question_count);
        loadingCousreText = findViewById(R.id.loadingCourse);
        course_code = findViewById(R.id.course_code);
        reset = findViewById(R.id.button_reset);
        quit = findViewById(R.id.quit);
        //num = FirebaseDatabase.getInstance().getReference("NumberOfQuestion");

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp3();

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


        handleIntent(getIntent());
    }
    public void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                upDateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimeRunning = false;
                pause.setText("Retake Quiz");
                pause.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.VISIBLE);
                count_down.setText("Time Over");
                count_down.setTextColor(Color.WHITE);
                Log.i("yo", answered.toString());
                Intent intent = new Intent(TrialActivity.this, Result_Activity.class);
                intent.putExtra("Total", String.valueOf(total));
                intent.putExtra("Correct", String.valueOf(correct));
                intent.putExtra("Incorrect", String.valueOf(wrong));
                intent.putExtra("points", String.valueOf(points));
                intent.putExtra("total_question", String.valueOf(total_question_number));
                intent.putExtra("query", q);
                intent.putExtra("answered", answered.toString());
                intent.putExtra("score", String.valueOf(mScore));
                startActivity(intent);

            }
        }.start();
        mTimeRunning = true;
        pause.setText("Pause Quiz");
        reset.setVisibility(View.INVISIBLE);
    }
    public void pauseTimer(){
        mButtonChoice1.setEnabled(false);
        mButtonChoice2.setEnabled(false);
        mButtonChoice3.setEnabled(false);
        mButtonChoice4.setEnabled(false);
        mCountDownTimer.cancel();
        mTimeRunning = false;
        pause.setText("Resume Quiz");
        reset.setVisibility(View.VISIBLE);

    }
    public void resetTimer(){
        mButtonChoice1.setEnabled(false);
        mButtonChoice2.setEnabled(false);
        mButtonChoice3.setEnabled(false);
        mButtonChoice4.setEnabled(false);
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        upDateCountDownText();
        reset.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);

    }
    public void upDateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis / 1000) /60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        count_down.setText(timeLeftFormatted);
        if (mTimeLeftInMillis < 6000) {
            count_down.setTextColor(Color.RED);
        } else {
            count_down.setTextColor(Color.WHITE);
        }
    }
        @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            //updateQuestions(query);
            start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            SharedPreferences sharedPreferences = getSharedPreferences("Activation", Context.MODE_PRIVATE);
            String code = sharedPreferences.getString("code",DEFAULT);
            if (code.equals(DEFAULT)) {
               return;
            }else {
                Intent intent = new Intent(TrialActivity.this,QuizActivity.class);
                startActivity(intent);
                Log.v("TAG","Save Code IS: "+code);
            }

        }

    }
    void start() {
        new AlertDialog.Builder(TrialActivity.this)
                .setTitle("!Attention")
                .setMessage("Are You ready To  Start Quiz?")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       updateQuestions(query);
                       startTimer();

                    }

                }).setNegativeButton("Not ready", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setCancelable(false).show();

    }

    void showPopUp3() {
         new AlertDialog.Builder(TrialActivity.this)
        .setMessage("are you sure you  want to  quit?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               quitResult();

            }

        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setCancelable(false).show();

    }

    void showPopUp2() {
        new AlertDialog.Builder(TrialActivity.this)
                .setTitle("!Attention")
                .setMessage("Maximum Attended Reached for Demo Version\nKindly Subscribe for more questions")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(TrialActivity.this,Bill.class);
                        startActivity(intent);

                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               quitResult();
            }
        }).setCancelable(false).show();
    }

    void showPopUp() {
         new AlertDialog.Builder(TrialActivity.this)
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
        new AlertDialog.Builder(TrialActivity.this)
        .setTitle("Attention!")
        .setMessage("Your Subscription has Expired, Kindly renew")
        .setPositiveButton("Renew", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(TrialActivity.this, Bill.class);
                startActivity(intent);
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(TrialActivity.this, UserActivity.class);
                startActivity(intent);
            }
        }).setCancelable(false).show();

    }
    public void quitResult(){
        Log.i("yo",answered.toString());
        Intent intent = new Intent(TrialActivity.this, Result_Activity.class);
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

   /* public void totalQuestionNumber() {
        int number_of_question = total;
        String id = num.push().getKey();
        NumberOfQuestion numberOfQuestion = new NumberOfQuestion(number_of_question, id);
        num.child(id).setValue(numberOfQuestion);
        //Toast.makeText(this,"CourseAdded Successfully",Toast.LENGTH_SHORT).show();
        Log.d("TAG", number_of_question + "");

    }*/


    private void updateQuestions(final String query1) {
        q = query1;
        Log.i("teating", query1);
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
                 //startTimer(60, count_down);
                //}

                total++;

                if (total > total_question_number) {
                    total--;
                    Log.i("yo", answered.toString() + total);
                    // open result activity
                    finish();
                    Intent i = new Intent(TrialActivity.this, Result_Activity.class);
                    i.putExtra("Total", String.valueOf(total));
                    i.putExtra("Correct", String.valueOf(correct));
                    i.putExtra("Incorrect", String.valueOf(wrong));
                    i.putExtra("points", String.valueOf(points));
                    i.putExtra("total_question", String.valueOf(total_question_number));
                    i.putExtra("query", query1);
                    i.putExtra("answered", answered.toString());
                    i.putExtra("score", mScore);
                    i.putExtra("current_q", currentQuestion);

                    startActivity(i);
                   //stopTimer();
                    mButtonChoice1.setEnabled(false);
                    mButtonChoice2.setEnabled(false);
                    mButtonChoice3.setEnabled(false);
                    mButtonChoice4.setEnabled(false);
                } else {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/quiz/" + query1.trim().toLowerCase()).child(String.valueOf(total));
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


                                if (currentQuestion > 5 && mCountDownTimer != null) {
                                    showPopUp2();
                                    mCountDownTimer.cancel();
                                    //totalQuestionNumber();
                                    mButtonChoice1.setEnabled(false);
                                    mButtonChoice2.setEnabled(false);
                                    mButtonChoice3.setEnabled(false);
                                    mButtonChoice4.setEnabled(false);


                                }

                                mButtonChoice1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total, 0);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(TrialActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
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
                                            answered.put(total, 0);
                                            Toast.makeText(TrialActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
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
                                            answered.put(total, 1);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(TrialActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
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
                                            answered.put(total, 1);
                                            Toast.makeText(TrialActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
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
                                            answered.put(total, 2);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(TrialActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
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
                                            answered.put(total, 2);
                                            Toast.makeText(TrialActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
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
                                            answered.put(total, 3);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(TrialActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
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
                                            answered.put(total, 3);
                                            Toast.makeText(TrialActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(TrialActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TrialActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
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
        super.onBackPressed();
        startActivity(new Intent(TrialActivity.this, UserActivity.class));
    }

}


//Getting Users Subscription Start Date and Users Subscription End Date
   /* public class SubscriptionAdapter extends ArrayAdapter<SubscriptionValidation> {
        private Activity context;
        private List<SubscriptionValidation> mValidations;


        public SubscriptionAdapter(Activity context, List<SubscriptionValidation> mvalidation) {
            super(context, R.layout.list_layout, mvalidation);
            this.context = context;
            this.mValidations = mvalidation;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

            TextView startDate = listViewItem.findViewById(R.id.textView1);
            TextView endDate = listViewItem.findViewById(R.id.textView2);
            SubscriptionValidation subscriptionValidation = mValidations.get(position);
            startDate.setText(subscriptionValidation.getStartDate());
            endDate.setText(subscriptionValidation.getEndDate());
            //Log.d("TAG",subscriptionValidation.getStartDate());
            //Log.d("TAG",subscri*/