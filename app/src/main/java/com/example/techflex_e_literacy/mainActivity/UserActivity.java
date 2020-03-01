package com.example.techflex_e_literacy.mainActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.techflex_e_literacy.GlideApp;
import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.cbt_activity.CBTTestPage;
import com.example.techflex_e_literacy.chatApi.ChatAPI;
import com.example.techflex_e_literacy.model.User;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CHOOSE_IMAGE = 11;
    private static final int JOB_ID = 101;
    private JobScheduler jobScheduler;
    private JobInfo jobInfo;
    CircleImageView image_profile;
    DatabaseReference mReference;
    FirebaseUser fuser;

    TextView user_profile_name, take_practice_button, gp,gp4, project_topic_button, seminar_button, it_placement_button, e_course, about_us,
            past_questions, summary, contact_us,advert,timee_table,portal_analysis,tma_score_keeper, course_videos;
    ImageView chat;
    ImageView user_profile_button;
    private FirebaseAuth firebaseAuth;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        MobileAds.initialize(this,APP_ID);

        //creating AdMobs
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("313C6DAD9D1C192244C9AB5CCC279361")
                .build();
        mAdView.loadAd(adRequest);
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
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        TextView marquee = findViewById(R.id.marquee);
        marquee.setSelected(true);


        //Job scheduler service initialization
        ComponentName componentName = new ComponentName(this,MJobScheduler.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,componentName);
        builder.setPeriodic(5000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        jobInfo = builder.build();
        jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);


        jobScheduler.schedule(jobInfo);
        //Toast.makeText(this,"Job Scheduled",Toast.LENGTH_SHORT).show();

        FirebaseMessaging.getInstance().subscribeToTopic("updates");

        //startService(new Intent(this, TimerService.class));

        user_profile_name = findViewById(R.id.user_profile_name);
       // map = findViewById(R.id.map);
        user_profile_button = findViewById(R.id.profile_image);
        take_practice_button = findViewById(R.id.take_practice_button);
        chat = findViewById(R.id.chat);
        gp = findViewById(R.id.gp);
        gp4 = findViewById(R.id.gp4);
        marquee = findViewById(R.id.marquee);
        course_videos = findViewById(R.id.course_video);
        tma_score_keeper = findViewById(R.id.tma_score_keeper);
        timee_table = findViewById(R.id.time_table);
        portal_analysis = findViewById(R.id.portal_analysis);
        //project_topic_button = findViewById(R.id.project_topic_button);
        //seminar_button = findViewById(R.id.seminar_topic_button);
        it_placement_button = findViewById(R.id.it_placement);
        e_course = findViewById(R.id.e_course_button);
       // about_us = findViewById(R.id.about_us);
        past_questions = findViewById(R.id.get_past_questions);
        summary = findViewById(R.id.summary);
        contact_us = findViewById(R.id.contact_us);
        advert = findViewById(R.id.advert);
       // notify = findViewById(R.id.notify);


        toolbar = findViewById(R.id.toolbar);

        user_profile_button.setOnClickListener(this);
        take_practice_button.setOnClickListener(this);
        portal_analysis.setOnClickListener(this);
        timee_table.setOnClickListener(this);
        chat.setOnClickListener(this);
        gp.setOnClickListener(this);
        gp4.setOnClickListener(this);
        tma_score_keeper.setOnClickListener(this);
        //downlaod_pq.setOnClickListener(this);
        //view_pq.setOnClickListener(this);
       // project_topic_button.setOnClickListener(this);
        e_course.setOnClickListener(this);
       // about_us.setOnClickListener(this);
        past_questions.setOnClickListener(this);
        it_placement_button.setOnClickListener(this);
       // seminar_button.setOnClickListener(this);
        summary.setOnClickListener(this);
        contact_us.setOnClickListener(this);
        advert.setOnClickListener(this);
        course_videos.setOnClickListener(this);
        //notify.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            //start  the LoginActivity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                assert user != null;
                user_profile_name.setText(user.getUsername());
                    if (user.getImageURL().equals("default")){
                        user_profile_button.setImageResource(R.mipmap.ic_e_learn_foreground);
                    }else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(user_profile_button);
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int id2 = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id2 == R.id.action_quit) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent =   new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    @Override
    public void onClick(View view) {
        if (view == take_practice_button){
            Intent intent = new Intent(UserActivity.this, CBTTestPage.class);
            startActivity(intent);

        }
        if (view == gp){
            //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://qydxpmzouxyk4jkk11srnw-on.drv.tw/gpa_cal/techflex/scale5.html"));
            startActivity(intent);
            if (mInterstitialAd.isLoaded()){
                mInterstitialAd.show();
            }else {
                //Do something else
            }
        }
        if (view == gp4){
            //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://qydxpmzouxyk4jkk11srnw-on.drv.tw/gpa_cal/techflex/scale4.html"));
            startActivity(intent);
            if (mInterstitialAd.isLoaded()){
                mInterstitialAd.show();
            }else {
                //Do something else
            }
        }

        //String number = "+2348025774336";
        if (view == project_topic_button){
            String url = "https://api.whatsapp.com/send?phone=2348025774336&text=I%20need%20your%20service%20on";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == e_course){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://nou.edu.ng/courseware"));
            startActivity(intent);
            if (mInterstitialAd.isLoaded()){
                mInterstitialAd.show();
            }else {
                //Do something else
            }
        }
        if (view == timee_table){
            Toast.makeText(this, "Till  Exam Start!", Toast.LENGTH_SHORT).show();
            /*Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("http://tommydigitaltech.tk/timetable/"));
            startActivity(intent);*/
        }
        if (view == portal_analysis){
            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
        }
        if (view == about_us) {
            Intent intent = new Intent(UserActivity.this, ABout.class);
            startActivity(intent);
        }
        if (view == past_questions) {
            String url = "https://api.whatsapp.com/send?phone=2348025774336&text=I%20need%20past%20questions%20for";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == it_placement_button) {
            String url = "https://api.whatsapp.com/send?phone=2348136559569&text=I%20need%20your%20service%20on%20IT%20Placement,%20Log Book Filling,%20IT Report";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == seminar_button) {
            String url = "https://api.whatsapp.com/send?phone=2348025774336&text=I%20need%20your%20service%20on%20Seminar Topic";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == summary) {
            String url = "https://api.whatsapp.com/send?phone=2348025774336&text=I%20need%20your%20service%20on%20Course Summary";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == contact_us){
            Intent intent = new Intent(UserActivity.this, Contact.class);
            startActivity(intent);
        }
        if (view == advert){
            Intent intent = new Intent(UserActivity.this, Contact.class);
            startActivity(intent);
        }
        if (view == chat){
            startActivity(new Intent(UserActivity.this, ChatAPI.class));
        }
        if (view == tma_score_keeper){
            startActivity(new Intent(UserActivity.this,TMAScoreSheet.class));
        }
        if (view == course_videos){
            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
        }
    }
}
