package com.example.techflex_e_literacy.mainActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class ForgotPassword extends AppCompatActivity {
    EditText resetpassword_edit_text;
    FirebaseAuth firebaseAuth;
    private CircularProgressButton cirLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);
        resetpassword_edit_text = findViewById(R.id.reset_password_edit_text);

        cirLoginButton = findViewById(R.id.cirLoginButton);
        cirLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cirLoginButton.startAnimation();
                String email = resetpassword_edit_text.getText().toString();
                if (email.equals("")){
                    Toast.makeText(ForgotPassword.this,"Field Can't be empty",Toast.LENGTH_LONG).show();
                }else {
                    firebaseAuth.sendPasswordResetEmail(resetpassword_edit_text.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPassword.this,"Password send to your email",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPassword.this,LoginActivity.class));
                                finish();
                            }else {
                                cirLoginButton.stopAnimation();
                                cirLoginButton.revertAnimation();
                                Toast.makeText(ForgotPassword.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(ForgotPassword.this,"Password reset was unsuccessful\nTry Again\nCheck Spellings or Contact Admin",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onSupportNavigateUp();
        startActivity(new Intent(ForgotPassword.this,LoginActivity.class));
    }
}
