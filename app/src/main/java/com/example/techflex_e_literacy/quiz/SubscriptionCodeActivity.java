package com.example.techflex_e_literacy.quiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.PasswordPopUp;
import com.example.techflex_e_literacy.mainActivity.UserActivity;

import java.security.SecureRandom;

public class SubscriptionCodeActivity extends AppCompatActivity {
    private String password;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_code);
        password = generateRandomKey(6);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showPopUp();
    }

    void showPopUp3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SubscriptionCodeActivity.this);
        builder.setIcon(R.drawable.noun1);
        builder.setTitle("Invalid Activation Code");
        builder.setMessage("Kindly retype code carefully\n contact support for assistance");
        builder.setPositiveButton("retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showPopUp();

            }

        });
        builder.setNegativeButton("quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(SubscriptionCodeActivity.this, UserActivity.class));
            }
        });
        builder.show();
        AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private String generateRandomKey(int len) {
        SecureRandom rnd = new SecureRandom();


        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));

        Log.e("Password", sb.toString());
        return sb.toString();
    }

    public void showPopUp() {
        final PasswordPopUp ssnPopUp = new PasswordPopUp(SubscriptionCodeActivity.this);
        ssnPopUp.setCancelable(true);
        ssnPopUp.setCanceledOnTouchOutside(false);
        ssnPopUp.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        //ssnPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        ssnPopUp.show();
        final TextView tvSubmit = ssnPopUp.findViewById(R.id.tv_submit);
        final EditText etSSN = ssnPopUp.findViewById(R.id.et_ssn);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etSSN.getText().toString().trim())
                        && TextUtils.equals(etSSN.getText().toString(), password)) {
                    startActivity(new Intent(SubscriptionCodeActivity.this, QuizActivity.class));
                } else {
                    showPopUp3();
                    //Toast.makeText(SubscriptionCodeActivity.this, "Invalid Activation Code", Toast.LENGTH_SHORT).show();
                }
                ssnPopUp.dismiss();

            }
        });
        Window window = ssnPopUp.getWindow();
        window.setLayout(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);

    }
}
