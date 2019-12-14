package com.example.techflex_e_literacy.mainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.techflex_e_literacy.R;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mPager;
    private int[] layouts = {R.layout.first_slide,R.layout.second_slide,R.layout.third_slide,R.layout.fourth_slide};
    private MpagerAdapter mMpagerAdapter;

    private Button bnSkip,bnNext;

    private LinearLayout Dots_layouts;
    private ImageView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new PreferenceManager(this).checkPrefences()){
            loadLoginScreen();
        }
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_welcome_screen);
        mPager = findViewById(R.id.viewPager);
        mMpagerAdapter = new MpagerAdapter(layouts,this);
        mPager.setAdapter(mMpagerAdapter);

        Dots_layouts = findViewById(R.id.dotsLayouts);
        bnNext = findViewById(R.id.bnNext);
        bnSkip = findViewById(R.id.bnSkip);

        bnSkip.setOnClickListener(this);
        bnNext.setOnClickListener(this);
        
        createDost(0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDost(position);
                if (position == layouts.length-1){
                    bnNext.setText("Start");
                    bnSkip.setVisibility(View.INVISIBLE);
                }else {
                    bnNext.setText("Next");
                    bnSkip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void createDost(int current_position){
        if (Dots_layouts != null){
            Dots_layouts.removeAllViews();
            dots = new ImageView[layouts.length];
            for (int i = 0; i<layouts.length; i++){
                dots[i] = new ImageView(this);
                if (i == current_position){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dots));
                }else {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.default_dots));
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4,0,4,0);
                Dots_layouts.addView(dots[i],params);
            }
        }
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.bnNext:
                    loadNextSlide();
                    break;
                case  R.id.bnSkip:
                    loadLoginScreen();
                    new PreferenceManager(this).writePrefence();
                    break;

            }
    }
    public void loadLoginScreen(){
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
    public void loadNextSlide(){
        int nextSlide = mPager.getCurrentItem()+1;
        if (nextSlide < layouts.length){
            mPager.setCurrentItem(nextSlide);
        }else {
            loadLoginScreen();
            new PreferenceManager(this).writePrefence();
        }
    }
}
