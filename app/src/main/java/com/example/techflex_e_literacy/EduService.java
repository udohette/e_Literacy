package com.example.techflex_e_literacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.cbt_activity.CBTTestPage;
import com.example.techflex_e_literacy.mainActivity.UserActivity;

public class EduService extends AppCompatActivity implements View.OnClickListener {
    TextView take_practice_button, gp,gp4, project_topic_button, seminar_button, it_placement_button, e_course, about_us,
            past_questions, summary, contact_us,advert,timee_table,portal_analysis,tma_score_keeper, course_videos;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edu_service);

        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        take_practice_button = findViewById(R.id.take_practice_button);
        it_placement_button = findViewById(R.id.it_placement);
        past_questions = findViewById(R.id.get_past_questions);
        summary = findViewById(R.id.summary);
        course_videos = findViewById(R.id.course_video);

        past_questions.setOnClickListener(this);
        it_placement_button.setOnClickListener(this);
        // seminar_button.setOnClickListener(this);
        summary.setOnClickListener(this);
        take_practice_button.setOnClickListener(this);
        course_videos.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == seminar_button) {
            String url = "https://wa.me/2348025774336?text=I'm%20interested%20in%20Writing%20Seminar%20and%20Process";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == summary) {
            String url = "https://wa.me/2348025774336?text=I'm%20interested%20in%20Summary%20for%20the%20following%20courses";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == course_videos){
            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
        }
        if (view == past_questions) {
            String url = "https://wa.me/2348025774336?text=I'm%20interested%20in%20Past%20Questions%20for%20the%20following%20courses";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == it_placement_button) {
            String url = "https://wa.me/2348136559569?text=I'm%20interested%20in%20IT%20Placement%20and%20Process";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == take_practice_button){
            Intent intent = new Intent(EduService.this, CBTTestPage.class);
            startActivity(intent);

        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EduService.this, UserActivity.class);
        startActivity(intent);
    }
}