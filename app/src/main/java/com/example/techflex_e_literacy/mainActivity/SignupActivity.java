package com.example.techflex_e_literacy.mainActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity{
    EditText username_edittext, password_edittext, userEmail_edittext, department,state,study_center;
    Button login_now_button, create_account_button;
    TextView link_login;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        progressBar = new ProgressBar(this);

        username_edittext = findViewById(R.id.username_edit_text);
        password_edittext = findViewById(R.id.password_edit_text);
        login_now_button = findViewById(R.id.login_now_button);
        state = findViewById(R.id.state);
        study_center = findViewById(R.id.study_center);
        create_account_button = findViewById(R.id.create_account_button);
        userEmail_edittext = findViewById(R.id.user_email_edit_text);
        link_login = findViewById(R.id.link_login);
        department = findViewById(R.id.department);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();

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
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });

        create_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = username_edittext.getText().toString().trim();
                final String userEmail = userEmail_edittext.getText().toString().trim();
                final String userPassword = password_edittext.getText().toString().trim();
                final String department1 = department.getText().toString().trim();
                final String userState = state.getText().toString().trim();
                final String stud_center = study_center.getText().toString().trim();
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
                if (department1.isEmpty()) {
                    department.setError(getString(R.string.input_error_phone));
                    department.requestFocus();
                    return;
                }

                registerUser(userName,userEmail,userPassword,department1,userState,stud_center);


            }
        });

    }
    private void registerUser(final String username, String email, String password, final String department, final String state, final String study_cen){
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String id = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("User").child(id);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", id);
                    hashMap.put("username", username);
                    hashMap.put("department",department);
                    hashMap.put("state",state);
                    hashMap.put("study_center",study_cen);
                    hashMap.put("imageURL", "default");
                    hashMap.put("status","offline");
                    //hashMap.put("timestamp",ServerValue.TIMESTAMP);
                    hashMap.put("search",username.toLowerCase());
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                //Toast.makeText(LoginActivity.this, "Login Error\ncheck details\ncheck internet connection", Toast.LENGTH_LONG).show();
                                if (!isConnected(SignupActivity.this))showPopUp();
                                else {
                                    Toast.makeText(SignupActivity.this, "Login Error\ncheck details\ncheck internet connection", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    });
                } else {
                   // Toast.makeText(SignupActivity.this, "You can't Register with  this Email", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(LoginActivity.this, "Login Error\ncheck details\ncheck internet connection", Toast.LENGTH_LONG).show();
                    if (!isConnected(SignupActivity.this))showPopUp();
                    else {
                        Toast.makeText(SignupActivity.this, "Login Error\ncheck details\ncheck internet connection", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

            }

        });
    }
    public void showPopUp() {
        new AlertDialog.Builder(SignupActivity.this)
                .setTitle("No Internet Connect")
                .setMessage("Check Login details\nYou  need a mobile internet or Wifi to  access this.")
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();

    }
    public Boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnected()))
                return true;
            else return false;
        } else
            return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onSupportNavigateUp();
        startActivity(new Intent(SignupActivity.this,LoginActivity.class));
    }
}
