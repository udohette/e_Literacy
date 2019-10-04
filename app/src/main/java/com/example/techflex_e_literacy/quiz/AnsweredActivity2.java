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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AnsweredActivity2 extends AppCompatActivity {

    String answered;
    String query;
    String totalq;
    String score;

    private TextView mQuestionView, textView7,score_count;
    private Button prev,next,quit;
    Toolbar toolbar;
    EditText answer;
    int total = 0;
    long total_question_number = 0;
    int currentQuestion = 0;

    ArrayList questionno = new ArrayList();
    Map<String,String> map;
    String q;

    String answer1;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answered2);
        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mQuestionView = findViewById(R.id.question);
        textView7 = findViewById(R.id.question_count);
        answer = findViewById(R.id.answer);
        score_count = findViewById(R.id.score);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        quit = findViewById(R.id.quit);

        Intent i = getIntent();
        query = i.getStringExtra("query");
        answered = i.getStringExtra("answered");
        answer1 = i.getStringExtra("answer");
        score = i.getStringExtra("score");
        String questions = i.getStringExtra("Total");
        String total_question = i.getStringExtra("total_question");
        totalq = i.getStringExtra("total_question");

        textView7.setText("Questions Answered: "+questions+"/"+total_question+"");
        score_count.setText("correctly answered: "+score);

        Log.i("yo_answerwd",query+"  "+answered+" "+answer1);
        answered = answered.substring(1, answered.length()-1);           //remove curly brackets
        String[] keyValuePairs = answered.split(",");           //split the string to creat key-value pairs
        map= new HashMap<>();
        try{
            for (String pair: keyValuePairs){
                String[] entry = pair.split("=");
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
        updateQuestion(query,true);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setBackgroundColor(Color.GREEN);
                updateQuestion(query,true);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setBackgroundColor(Color.RED);
                updateQuestion(query,false);
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AnsweredActivity2.this, UserActivity.class));
            }
        });
    }

    public  void updateQuestion(final String query1, final boolean flag){
        q = query1;
        Log.i("testing",query1);
        prev.setEnabled(true);
        next.setEnabled(true);
        answer.setEnabled(false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/fbq/"+query1.trim().toLowerCase());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    return;
                }
                findViewById(R.id.quiz_layout).setVisibility(View.VISIBLE);

                if (flag)
                    total++;
                else
                    total--;

                if (total > total_question_number) {
                    total--;

                    // open result activity
                    finish();
                    Intent intent = new Intent(AnsweredActivity2.this, UserActivity.class);
                    startActivity(intent);
                    prev.setEnabled(false);
                    next.setEnabled(false);
                    answer.setEnabled(false);
                    quit.setEnabled(false);
                } else {
                    Log.i("yo_answerwd",total-1+"");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/fbq/"+query1.trim().toLowerCase()).child(String.valueOf(total));
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                final QuestionLibraryFBQ fbq = dataSnapshot.getValue(QuestionLibraryFBQ.class);
                                mQuestionView.setText(fbq.getQuestion());
                                currentQuestion++;

                                /*if (currentQuestion > 5 && mCountDownTimer != null){
                                    showPopUp2();
                                    stopTimer();
                                    prev.setEnabled(false);
                                    next.setEnabled(false);
                                    submit.setEnabled(true);

                                }*/

                                switch (Objects.requireNonNull(map.get(questionno.get(total-1)))){
                                    case "0":
                                        next.setBackgroundColor(Color.RED);
                                        answer.setText(fbq.getAnswer());
                                        Toast.makeText(AnsweredActivity2.this,answer.getText().toString().trim().toUpperCase()+" was wrong",Toast.LENGTH_LONG).show();
                                        break;
                                }
                                if (answer.getText().toString().equals(fbq.getAnswer())){
                                    next.setBackgroundColor(Color.GREEN);
                                    answer.setText(fbq.getAnswer());
                                    Toast.makeText(AnsweredActivity2.this,fbq.getAnswer().toLowerCase()+" was the right answer",Toast.LENGTH_LONG).show();
                                }else{
                                    next.setBackgroundColor(Color.RED);
                                    answer.setText(answer1);
                                    //Toast.makeText(AnsweredActivity2.this,answer.setText(answer1)+ " was wrong",Toast.LENGTH_LONG).show();

                                }

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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
