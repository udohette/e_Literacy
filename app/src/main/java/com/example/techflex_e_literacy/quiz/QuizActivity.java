package com.example.techflex_e_literacy.quiz;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizActivity extends AppCompatActivity {
    private TextView mScoreView, course_code;
    CountDownTimer mCountDownTimer = null;
    private int questionCount;

    private Button mButtonChoice1;
    private Button mButtonChoice2;
    private TextView mQuestionView, count_down, question_count;
    Toolbar toolbar;
    private int mScore = 0;
    int total = 0;
    int correct = 0;
    int wrong = 0;
    private Button mButtonChoice3, mButtonChoice4;
    DatabaseReference databaseReference;
    private int questionCounterTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);

        toolbar = findViewById(R.id.toolbar);
        (getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mScoreView = findViewById(R.id.score);
        mQuestionView = findViewById(R.id.question);
        mButtonChoice1 = findViewById(R.id.choice1);
        mButtonChoice2 = findViewById(R.id.choice2);
        mButtonChoice3 = findViewById(R.id.choice3);
        mButtonChoice4 = findViewById(R.id.choice4);
        count_down = findViewById(R.id.textview_count_down);
        question_count = findViewById(R.id.question_count);
        course_code = findViewById(R.id.course_code);


        handleIntent(getIntent());
        // updateQuestions();

    }

    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            updateQuestions(query);
            //Log.v("TESTING",query);
            //use the query to search your data somehow

        }
    }

    public void updateQuestions(final String query1) {
        mButtonChoice1.setEnabled(true);
        mButtonChoice2.setEnabled(true);
        mButtonChoice3.setEnabled(true);
        mButtonChoice4.setEnabled(true);
        total++;
        if (total >) {
            // open result activity
            Intent i = new Intent(QuizActivity.this, Result_Activity.class);
            i.putExtra("Total", String.valueOf(total));
            i.putExtra("Correct", String.valueOf(correct));
            i.putExtra("Incorrect", String.valueOf(wrong));
            mButtonChoice1.setEnabled(false);
            mButtonChoice2.setEnabled(false);
            mButtonChoice3.setEnabled(false);
            mButtonChoice4.setEnabled(false);
            startActivity(i);
            stopTimer();

        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("quiz/" + query1.trim()).child(String.valueOf(total));

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e("Count", "" + dataSnapshot.getChildrenCount());

                    final QuestionLibrary questionLibrary = dataSnapshot.getValue(QuestionLibrary.class);
                    question_count.setText("Total Question:" + dataSnapshot.getChildrenCount());
                    //Log.d("Questions: ",questionLibrary.getQuestion());
                    if (questionLibrary == null) {
                        showDialog(QuizActivity.this, "Available Soon\ntry another course");
                        return;
                    }
                    startTimer(73, count_down);
                    course_code.setText(query1.toUpperCase());
                    mQuestionView.setText(questionLibrary.getQuestion());
                    mButtonChoice1.setText(questionLibrary.getOption1());
                    mButtonChoice2.setText(questionLibrary.getOption2());
                    mButtonChoice3.setText(questionLibrary.getOption3());
                    mButtonChoice4.setText(questionLibrary.getOption4());


                    mButtonChoice1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mButtonChoice1.setEnabled(false);
                            mButtonChoice2.setEnabled(false);
                            mButtonChoice3.setEnabled(false);
                            mButtonChoice4.setEnabled(false);
                            if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                mScore = mScore + 1;
                                updateScore(mScore);
                                Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                mButtonChoice1.setBackgroundColor(Color.GREEN);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        correct++;
                                        mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                        updateQuestions(query1);

                                    }
                                }, 1500);
                            } else {
                                Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                wrong = wrong + 1;
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
                                mScore = mScore + 1;
                                updateScore(mScore);
                                Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                mButtonChoice2.setBackgroundColor(Color.GREEN);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        correct++;
                                        mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                        updateQuestions(query1);
                                    }
                                }, 1500);
                            } else {
                                Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                wrong = wrong + 1;
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
                                mScore = mScore + 1;
                                updateScore(mScore);
                                Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                mButtonChoice3.setBackgroundColor(Color.GREEN);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        correct++;
                                        mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                        updateQuestions(query1);

                                    }
                                }, 1500);
                            } else {
                                Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                wrong = wrong + 1;
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
                                mScore = mScore + 1;
                                updateScore(mScore);
                                Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                mButtonChoice4.setBackgroundColor(Color.GREEN);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        correct++;
                                        mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                        updateQuestions(query1);
                                    }
                                }, 1500);
                            } else {
                                Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                wrong = wrong + 1;
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



                                    }
                                }, 1500);
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.v("Failed to  Read Value", databaseError.toException().getMessage());

                }
            });

        }
    }

    public void reverseTimer(int seconds, final TextView tv) {
        mCountDownTimer = new CountDownTimer(seconds * 1000 + 1000, 1000) {
            @Override
            public void onTick(long millsUntilFinised) {
                int seconds = (int) (millsUntilFinised / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

            }

            @Override
            public void onFinish() {
                tv.setText("Done!");
                Intent intent = new Intent(QuizActivity.this, Result_Activity.class);
                intent.putExtra("Total", String.valueOf(total));
                intent.putExtra("Correct", String.valueOf(correct));
                intent.putExtra("Incorrect", String.valueOf(wrong));
                startActivity(intent);
            }

        }.start();
    }

    public void startTimer(int seconds, final TextView tv) {
        if (mCountDownTimer == null) {
            reverseTimer(seconds, tv);
        }
    }

    public void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();

        }
    }


    private void updateScore(int point) {
        mScoreView.setText("Score: " + mScore);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
