package com.example.techflex_e_literacy.quiz;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.techflex_e_literacy.R;

public class SubDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button cancel, subcribe;


    public SubDialog(Activity a) {
        super(a);
        this.c = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sub_dialog);

        cancel = findViewById(R.id.btn_cancel);
        subcribe =findViewById(R.id.btn_subscribe);

        cancel.setOnClickListener(this);
        subcribe.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                dismiss();
                return;
            case R.id.btn_subscribe:

        }
    }
}
