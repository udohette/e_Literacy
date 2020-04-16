package com.example.techflex_e_literacy.mainActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.techflex_e_literacy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout login_relative_layout;
    ImageView how_to;
    EditText username_edittext, password_edittext, userEmail_edittext;
    TextView signup_now_button,reset_password_button;
    private FirebaseAuth auth;
    private CircularProgressButton cirLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.main_activity_layout);

        cirLoginButton = findViewById(R.id.cirLoginButton);
        cirLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cirLoginButton.startAnimation();
                userLogin();
            }
        });

        TextView marquee = findViewById(R.id.marquee);
        marquee.setSelected(true);

        username_edittext = findViewById(R.id.username_edit_text);
        password_edittext = findViewById(R.id.password_edit_text);
       // login_now_button = findViewById(R.id.login_now_button);
        signup_now_button = findViewById(R.id.sign_up_btn);
        how_to = findViewById(R.id.how_to);
        reset_password_button = findViewById(R.id.password_reset_btn);
        //login_relative_layout = findViewById(R.id.login_relative_layout);
        userEmail_edittext = findViewById(R.id.user_email_edit_text_edit);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //login_relative_layout.setVisibility(View.VISIBLE);

            }
        };

        handler.postDelayed(runnable, 3000);

        signup_now_button.setOnClickListener(this);
        how_to.setOnClickListener(this);
        reset_password_button.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Kindly  Login ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private void DisplayNotifcation(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        //get  firebase auth  instance
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            //login  activity here
            //start  the contentActivity
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
            finish();

        }
    }

    public Boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info!= null && info.isConnected()){
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile!= null && mobile.isConnected()) || (wifi != null && wifi.isConnected())) return true;
            else return false;
        }else
            return false;

    }
    public void showPopUp() {
        new AlertDialog.Builder(LoginActivity.this)
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_quit) {
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void userLogin() {
        String userEmail = userEmail_edittext.getText().toString().trim();
        String userPassword = password_edittext.getText().toString().trim();

        if (userEmail == null) {
            //Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show();
            username_edittext.setError(getString(R.string.input_error_email));
            username_edittext.requestFocus();
            cirLoginButton.revertAnimation();
            cirLoginButton.stopAnimation();
            //stopping  the function executing further
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            userEmail_edittext.setError(getString(R.string.input_error_email_invalid));
            userEmail_edittext.requestFocus();
            cirLoginButton.revertAnimation();
            cirLoginButton.stopAnimation();
            return;
        }
        if (userPassword == null) {
            username_edittext.setError(getString(R.string.input_error_password));
            username_edittext.requestFocus();
            cirLoginButton.revertAnimation();
            cirLoginButton.stopAnimation();
            //stopping  the function executing further
            return;
        }
        if (userPassword.length() < 8) {
            password_edittext.setError(getString(R.string.input_error_password_length));
            password_edittext.requestFocus();
            cirLoginButton.revertAnimation();
            cirLoginButton.stopAnimation();
            return;
        }
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //start  the contentActivity
                    startActivity(new Intent(getApplicationContext(), UserActivity.class));
                    finish();
                } else {
                    //Toast.makeText(LoginActivity.this, "Login Error\ncheck details\ncheck internet connection", Toast.LENGTH_LONG).show();
                    if (!isConnected(LoginActivity.this))showPopUp();
                    else {
                        Toast.makeText(LoginActivity.this, "Login Error\ncheck details\ncheck internet connection", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == signup_now_button){
            finish();
            startActivity(new Intent(this, SignupActivity.class));
            //overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        }
        if (view == reset_password_button){
            finish();
            startActivity(new Intent(this, ForgotPassword.class));
        }
        if (view == how_to){
            startActivity(new Intent(LoginActivity.this,HowTo.class));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v("TAG", "Screen Orientation Changed");
    }
}
