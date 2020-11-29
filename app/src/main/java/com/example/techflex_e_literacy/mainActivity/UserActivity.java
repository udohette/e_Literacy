package com.example.techflex_e_literacy.mainActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.EduService;
import com.example.techflex_e_literacy.chatApi.MessageActivity;
import com.example.techflex_e_literacy.model.Courses;
import com.example.techflex_e_literacy.quiz.Bill;
import com.example.techflex_e_literacy.quiz.QuizActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
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
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {
    private static final int CHOOSE_IMAGE = 11;
    private static final int JOB_ID = 101;
    private JobScheduler jobScheduler;
    private JobInfo jobInfo;
    CircleImageView image_profile;
    DatabaseReference mReference;
    FirebaseUser fuser;

    TextView user_profile_name, take_practice_button, gp, gp4, project_topic_button, seminar_button, it_placement_button, e_course, about_us,
            edu_activity, summary, contact_us, advert, timee_table, portal_analysis, tma_score_keeper, course_videos, google_places;
    ImageView chat, logout;
    ImageView user_profile_button;
    private FirebaseAuth firebaseAuth;

    //creating adBanners Variables;
    private AdView adView;
    private FrameLayout frameLayout;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd mAd;

    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";

    Toolbar toolbar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

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

        //creating AdMobs

       frameLayout = findViewById(R.id.banner_id_ad);
       adView = new AdView(this);
       adView.setAdUnitId(getString(R.string.banner_ad_unit_id));
       frameLayout.addView(adView);
       loadBanner();
        //creating interstitialAd

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
                interstitialAd.loadAd(new AdRequest.Builder().build());
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
        logout = findViewById(R.id.menu_icon);
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
        edu_activity = findViewById(R.id.edu_acitivity);
        summary = findViewById(R.id.summary);
        contact_us = findViewById(R.id.contact_us);
       // notify = findViewById(R.id.notify);
        google_places = findViewById(R.id.googlePlaces);


        toolbar = findViewById(R.id.toolbar);

        user_profile_button.setOnClickListener(this);
        portal_analysis.setOnClickListener(this);
        timee_table.setOnClickListener(this);
        chat.setOnClickListener(this);
        gp.setOnClickListener(this);
        gp4.setOnClickListener(this);
        logout.setOnClickListener(this);
        google_places.setOnClickListener(this);
        tma_score_keeper.setOnClickListener(this);
        //downlaod_pq.setOnClickListener(this);
        //view_pq.setOnClickListener(this);
       // project_topic_button.setOnClickListener(this);
        e_course.setOnClickListener(this);
       // about_us.setOnClickListener(this);
        edu_activity.setOnClickListener(this);
       // seminar_button.setOnClickListener(this);
        contact_us.setOnClickListener(this);
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
    public void loadRewardedVideo(){
        if (!mAd.isLoaded()){
            mAd.loadAd(getString(R.string.rewardedVideo_ad_unit),new AdRequest.Builder().build());
        }
    }
    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
    void expiredSubscriptionPopUp() {
        new AlertDialog.Builder(UserActivity.this)
                .setTitle("Attention!")
                .setMessage("Your Subscription has Expired, Kindly renew")
                .setPositiveButton("Renew", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(UserActivity.this, Bill.class);
                        startActivity(intent);
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }).setCancelable(false).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.master_bottom_navigation,menu);
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
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (mAd.isLoaded()){
            mAd.show();
        }else {
            Intent intent =   new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }


    @Override
    public void onClick(View view) {
        if (view == gp){
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
                    //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://qydxpmzouxyk4jkk11srnw-on.drv.tw/web/scale5.html"));
                    startActivity(intent);

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
                    //loadRewardedVideo();
                    //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://qydxpmzouxyk4jkk11srnw-on.drv.tw/web/scale5.html"));
                    startActivity(intent);



                }
            });
            if (mAd.isLoaded()){
                mAd.show();
            }else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://qydxpmzouxyk4jkk11srnw-on.drv.tw/web/scale5.html"));
                startActivity(intent);

            }
        }
        if (view == gp4){
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
                    //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://qydxpmzouxyk4jkk11srnw-on.drv.tw/web/scale4.html"));
                    startActivity(intent);

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
                   // loadRewardedVideo();
                    //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://qydxpmzouxyk4jkk11srnw-on.drv.tw/web/scale4.html"));
                    startActivity(intent);

                }
            });
            if (mAd.isLoaded()){
                mAd.show();
            }else {
            }
        }

        //String number = "+2348025774336";
        if (view == project_topic_button){
            String url = "https://wa.me/2348025774336?text=I'm%20interested%20in%20IT%20Placement%20and%20Process";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == e_course){
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
                    //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://nou.edu.ng/courseware"));
                    startActivity(intent);

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
                    //loadRewardedVideo();
                    //Toast.makeText(this, "On Maintenance", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://nou.edu.ng/courseware"));
                    startActivity(intent);


                }
            });
            if (mAd.isLoaded()){
                mAd.show();
            }else {
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
            String url = "https://wa.me/2348025774336?text=I'm%20interested%20in%20doing%20my%20portal%20analysis%20before%20graduation";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        if (view == edu_activity){
            startActivity(new Intent(UserActivity.this, EduService.class));
        }
        if (view == about_us) {
            Intent intent = new Intent(UserActivity.this, ABout.class);
            startActivity(intent);
        }
        if (view == contact_us){
            Intent intent = new Intent(UserActivity.this, Contact.class);
            startActivity(intent);
        }
        if (view == chat){
            startActivity(new Intent(UserActivity.this, ChatAPI.class));
        }
        if (view == tma_score_keeper){
            startActivity(new Intent(UserActivity.this, TMAScoreSheet.class));
        }
        if (view == course_videos) {
            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
        }
        if (view == logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == google_places) {
            startActivity(new Intent(UserActivity.this, GoogleAPI.class));
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
        loadRewardedVideo();
        //startActivity(new Intent(this,UserActivity.class));

    }
}
