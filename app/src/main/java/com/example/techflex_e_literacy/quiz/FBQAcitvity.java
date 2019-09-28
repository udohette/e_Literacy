package com.example.techflex_e_literacy.quiz;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FBQAcitvity extends AppCompatActivity {
    private static final String TESTING_MESSAGE ="Test";
    private TextView mScoreView;
    private TextView mQuestionView, count_down, total_question, course_code, searchCourseView;
    private Button prev,next,submit;
    ProgressBar searchCourseProBar;
    Toolbar toolbar;
    EditText answer;
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
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fbq_activity);


        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScoreView = findViewById(R.id.score);
        mQuestionView = findViewById(R.id.question);
        count_down = findViewById(R.id.textview_count_down);
        searchCourseProBar= findViewById(R.id.courseSearch);
        searchCourseView = findViewById(R.id.loadingCourse);
        total_question = findViewById(R.id.question_count);
        course_code = findViewById(R.id.course_code);
        answer = findViewById(R.id.answer);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp3();
            }
        });

        handleIntent(getIntent());

    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            updateQuestion(query);
        }
    }
    public  void updateQuestion(final String query1){
        q = query1;
        Log.i("testing",query1);
        prev.setEnabled(false);
        submit.setEnabled(true);
        next.setEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/fbq/"+query1.trim().toLowerCase());
        searchCourseProBar.setVisibility(View.VISIBLE);
        searchCourseView.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchCourseProBar.setVisibility(View.GONE);
                searchCourseView.setVisibility(View.GONE);
                if (!dataSnapshot.exists()){
                    showPopUp();
                    return;
                }
                findViewById(R.id.quiz_layout).setVisibility(View.VISIBLE);
                total_question_number = (dataSnapshot.getChildrenCount());
                total_question.setText("Question: "+currentQuestion+"/"+ total_question_number+"");
                course_code.setText(query1.trim().toUpperCase());
                startTimer(30, count_down);

                total++;
                if (total > total_question_number) {
                    total--;
                    Log.v("testing",answered.toString());
                    // open result activity
                    Intent i = new Intent(FBQAcitvity.this, Result_Activity.class);
                    i.putExtra("Total", String.valueOf(total));
                    i.putExtra("Correct", String.valueOf(correct));
                    i.putExtra("Incorrect", String.valueOf(wrong));
                    i.putExtra("points", String.valueOf(points));
                    i.putExtra("total_question",String.valueOf(total_question_number));
                    i.putExtra("query",query1);
                    i.putExtra("answered",answered.toString());
                    startActivity(i);
                    stopTimer();
                    prev.setEnabled(false);
                    next.setEnabled(false);
                    submit.setEnabled(false);
                } else {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/fbq/"+query1.trim().toLowerCase()).child(String.valueOf(total));
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                final QuestionLibraryFBQ fbq = dataSnapshot.getValue(QuestionLibraryFBQ.class);
                                mQuestionView.setText(fbq.getQuestion());
                                currentQuestion++;
                                if (currentQuestion > 5 && mCountDownTimer != null){
                                    showPopUp2();
                                    stopTimer();
                                    prev.setEnabled(false);
                                    next.setEnabled(false);
                                    submit.setEnabled(true);

                                }
                                next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (answer.getText().toString().toLowerCase().trim().equals(fbq.getAnswer())){
                                            mScore = mScore+1;
                                            updateScore(mScore);
                                            Toast.makeText(FBQAcitvity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            next.setBackgroundColor(Color.GREEN);

                                                    next.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    answer.getText().clear();
                                                    updateQuestion(query1);



                                            searchCourseProBar.setVisibility(View.GONE);
                                            searchCourseView.setVisibility(View.GONE);
                                        }else {
                                            Toast.makeText(FBQAcitvity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points -5;
                                            next.setBackgroundColor(Color.RED);
                                                    answer.getText().clear();
                                                    updateQuestion(query1);

                                            searchCourseProBar.setVisibility(View.GONE);
                                            searchCourseView.setVisibility(View.GONE);
                                                    //next.setBackgroundColor(Color.parseColor("#03A9f4"));
                                        }

                                    }
                                });
                                prev.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (total_question_number > 0){
                                            total_question_number --;
                                            currentQuestion = fbq.getQuestion().length();

                                        }
                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FBQAcitvity.this);
        builder.setIcon(R.drawable.noun1);
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
    void showPopUp2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FBQAcitvity.this);
        builder.setIcon(R.drawable.noun1);
        builder.setTitle("Attention!");
        builder.setMessage("Maximum Attended Reached for Demo Version\nPlease Kindly Subscribe for more course");
        builder.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(FBQAcitvity.this,Bill.class);
                startActivity(intent);

            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dialogInterface.dismiss();
                Intent intent = new Intent(FBQAcitvity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
        AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }
    void showPopUp3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FBQAcitvity.this);
        builder.setIcon(R.drawable.noun1);
        builder.setTitle("Attention!");
        builder.setMessage("If you End Quiz your current progress won't be Save?");
        builder.setPositiveButton("End Quiz", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();

            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
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
    public void reverseTimer(int seconds, final TextView tv) {
        mCountDownTimer = new CountDownTimer(seconds * 1000 + 1000, 1000) {
            @Override
            public void onTick(long millsUntilFinised) {
                int seconds = (int) (millsUntilFinised / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

                if (millsUntilFinised < 10000) {
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setTextColor(Color.WHITE);
                }

            }

            @Override
            public void onFinish() {
                tv.setText("Done!");
                tv.setTextColor(Color.WHITE);
                Log.i("testing",answered.toString());
                Intent intent = new Intent(FBQAcitvity.this, Result_Activity.class);
                intent.putExtra("Total", String.valueOf(total));
                intent.putExtra("Correct", String.valueOf(correct));
                intent.putExtra("Incorrect", String.valueOf(wrong));
                intent.putExtra("points", String.valueOf(points));
                intent.putExtra("total_question", String.valueOf(total_question_number));
                intent.putExtra("query",q);
                intent.putExtra("answered",answered.toString());
                startActivity(intent);
            }

        }.start();
    }
    public void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();

        }
    }
    public void startTimer(int seconds, final TextView tv) {
        if (mCountDownTimer == null) {
            reverseTimer(seconds, tv);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateScore(int point) {
        mScoreView.setText("Score: " + mScore);
    }
}
