package com.example.techflex_e_literacy.mainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.techflex_e_literacy.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Contact extends AppCompatActivity {
    Toolbar toolbar;
    EditText name, email,subject, message;
    Button send;
    private AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);

        name = findViewById(R.id.user_name_edit_text);
        email = findViewById(R.id.user_email_edit_text);
        subject = findViewById(R.id.user_subject_edit_text);
        message = findViewById(R.id.user_message_edit_text);
        send = findViewById(R.id.post_message);

        toolbar = findViewById(R.id.toolbar);
        (getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1      = name.getText().toString();
                String email1     = email.getText().toString();
                String subject1   = subject.getText().toString();
                String message1   = message.getText().toString();
                if (TextUtils.isEmpty(name1)){
                    name.setError("Enter Your Name");
                    name.requestFocus();
                    return;
                }

                Boolean onError = false;
                if (!isValidEmail(email1)) {
                    onError = true;
                    email.setError("Invalid Email");
                    return;
                }

                if (TextUtils.isEmpty(subject1)){
                    subject.setError("Enter Your Subject");
                    subject.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(message1)){
                    message.setError("Enter Your Message");
                    message.requestFocus();
                    return;
                }

                Intent sendEmail = new Intent(android.content.Intent.ACTION_SEND);

                /* Fill it with Data */
                sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"udohette@gmail.com"});
                sendEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, subject1);
                sendEmail.setType("message/rfc822");
                sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,
                        "Name: "+name1+'\n'+"Email ID: "+email1+'\n'+"Message: "+'\n'+message1);

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(sendEmail, "Send mail..."));

            }

        });

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
        //Get a Tracker (should auto-report)

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // validating email id

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
