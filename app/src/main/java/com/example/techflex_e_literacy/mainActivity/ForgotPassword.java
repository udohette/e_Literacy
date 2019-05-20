package com.example.techflex_e_literacy.mainActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    EditText resetpassword_edit_text;
    Button resetpassword_button;
    ProgressBar resetpassword_progressbar;
    TextView email_sent_succesful;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);
        resetpassword_edit_text = findViewById(R.id.reset_password_edit_text);
        resetpassword_progressbar = findViewById(R.id.progressbar_reset);
        resetpassword_button =  findViewById(R.id.reset_password_button);
        email_sent_succesful = findViewById(R.id.email_sent_successful_text_view);

        firebaseAuth = FirebaseAuth.getInstance();
        resetpassword_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetpassword_progressbar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(resetpassword_edit_text.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        resetpassword_progressbar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotPassword.this,"Password send to your email",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ForgotPassword.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
