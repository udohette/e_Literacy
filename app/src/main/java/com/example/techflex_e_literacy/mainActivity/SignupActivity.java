package com.example.techflex_e_literacy.mainActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    EditText username_edittext, password_edittext, userEmail_edittext, userPhonenumber;
    Button login_now_button, signup_now_button, reset_password_button, create_account_button;
    TextView link_login;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    String token;
    int count = 0;
    DatabaseReference db_total_users, getDb_total_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        progressBar = new ProgressBar(this);

        db_total_users = FirebaseDatabase.getInstance().getReference();

        getDb_total_users = db_total_users.child("Total_Users");



        //get  firebase auth  instance
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            finish();
            //start  the contentActivity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        username_edittext = findViewById(R.id.username_edit_text);
        password_edittext = findViewById(R.id.password_edit_text);
        login_now_button = findViewById(R.id.login_now_button);
        signup_now_button = findViewById(R.id.sign_up_btn);
        create_account_button = findViewById(R.id.create_account_button);
        userEmail_edittext = findViewById(R.id.user_email_edit_text);
        link_login = findViewById(R.id.link_login);
        userPhonenumber = findViewById(R.id.userPhone_edit_text);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);


        create_account_button.setOnClickListener(this);
        link_login.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Thanks for using our Service", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
    private void registerUser(){


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    token = task.getResult().getToken();
                    //Toast.makeText(SignupActivity.this,"Token Saved",Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(SignupActivity.this,"Token not save",Toast.LENGTH_SHORT).show();
                    Log.v("TAG","Token not save");

                }

            }
        });
        final String userName = username_edittext.getText().toString().trim();
        final String userEmail = userEmail_edittext.getText().toString().trim();
        String userPassword = password_edittext.getText().toString().trim();
        final String phone = userPhonenumber.getText().toString().trim();

        if (userName.isEmpty()) {
            username_edittext.setError(getString(R.string.input_error_name));
            username_edittext.requestFocus();
            //stopping  the function executing further
            return;
        }

        if (userEmail.isEmpty()) {
            userEmail_edittext.setError(getString(R.string.input_error_email));
            userEmail_edittext.requestFocus();
            //stopping  the function executing further
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            userEmail_edittext.setError(getString(R.string.input_error_email_invalid));
            userEmail_edittext.requestFocus();
            return;
        }
        if (userPassword.isEmpty()) {
            password_edittext.setError(getString(R.string.input_error_password));
            password_edittext.requestFocus();
            //stopping  the function executing further
            return;
        }
        if (userPassword.length() < 8) {
            password_edittext.setError(getString(R.string.input_error_password_length));
            password_edittext.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            userPhonenumber.setError(getString(R.string.input_error_phone));
            userPhonenumber.requestFocus();
            return;
        }
        if (phone.length() != 11) {
            userPhonenumber.setError(getString(R.string.input_error_phone_invalid));
            userPhonenumber.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //finish();
                    User user = new User(
                            userName,
                            userEmail,
                            phone,
                            token
                    );
                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()
                    ).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                getDb_total_users.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int count = (int)dataSnapshot.child("count:").getValue();
                                        getDb_total_users.child("count").setValue(count++);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                               // count++;
                                Toast.makeText(SignupActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //display  a failure message
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.already_register), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        //setting up onclick  for SignUp  button
        if (view == create_account_button) {
            registerUser();

        }
        if (view == reset_password_button) {
            // will open the forgot password activity
        }
        if (view == link_login) {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onSupportNavigateUp();
        startActivity(new Intent(SignupActivity.this,LoginActivity.class));
    }
}
