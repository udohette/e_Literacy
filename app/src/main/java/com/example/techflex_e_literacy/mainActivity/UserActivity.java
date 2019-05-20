package com.example.techflex_e_literacy.mainActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.techflex_e_literacy.GlideApp;
import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.cbt_activity.CBTTestPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CHOOSE_IMAGE = 11;

    TextView user_profile_name, take_practice_button, gp,project_topic_button,seminar_button,logbook_button,e_course;
    ImageView dropdown_option_menu;
    ImageView user_profile_button;
    private FirebaseAuth firebaseAuth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        user_profile_name = findViewById(R.id.user_profile_name);
        dropdown_option_menu = findViewById(R.id.drop_down_option_menu);
        user_profile_button = findViewById(R.id.user_profile_photo);
        take_practice_button = findViewById(R.id.take_practice_button);
        gp = findViewById(R.id.gp);
        project_topic_button = findViewById(R.id.project_topic_button);
        seminar_button = findViewById(R.id.seminar_topic_button);
        logbook_button = findViewById(R.id.logbook_button);
        e_course = findViewById(R.id.e_course_button);

        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dropdown_option_menu.setOnClickListener(this);
        user_profile_button.setOnClickListener(this);
        take_practice_button.setOnClickListener(this);
        gp.setOnClickListener(this);
        project_topic_button.setOnClickListener(this);
        e_course.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            //start  the MainActivity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            user_profile_name.setText("Welcome " + user.getDisplayName());
            if (user.getPhotoUrl() != null) {
                GlideApp.with(this).load(user.getPhotoUrl().toString())
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(user_profile_button);
            } else {
                if (user_profile_name.getDisplay() == null) {
                    user_profile_name.setText("please update profile");
                }
            }

        }

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
            startActivity(new Intent(this, MainActivity.class));
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
    public void onClick(View view) {
        if (view == user_profile_button) {
        }
        if (view == dropdown_option_menu) {
            PopupMenu popupMenu = new PopupMenu(UserActivity.this, dropdown_option_menu);
            popupMenu.getMenuInflater().inflate(R.menu.profile_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.log_out_button:
                            firebaseAuth.signOut();
                            finish();
                            //start  the contentActivity
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            return true;
                        case R.id.update_profile_button:
                            startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
                            return true;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
        if (view == take_practice_button){
            Intent intent = new Intent(UserActivity.this, CBTTestPage.class);
            startActivity(intent);

        }
        if (view == gp){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("http://www.techflexco.com/scale5.php"));
            startActivity(intent);
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
            intent.setData(Uri.parse("http://www.nou.edu.ng/courseware"));
            startActivity(intent);
        }

    }
}
