package com.example.techflex_e_literacy.quiz;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.PasswordPopUp;
import com.example.techflex_e_literacy.mainActivity.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

import java.security.SecureRandom;

public class SubscriptionCodeActivity extends AppCompatActivity {
    private String password;
    String email = null;
    String phoneNumber = null;
    String displayName = null;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_code);
        showPopUp();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        password = generateRandomKey(6);
        if (user != null){
            email = user.getEmail();
            phoneNumber = user.getPhoneNumber();
            displayName = user.getDisplayName();
        }
        if (!TextUtils.isEmpty(password)) {
            try {
               /* GMailSender sender = new GMailSender("username@gmail.com", "password");
                sender.sendMail("This is Subject",
                        "This is Body",
                        "user@gmail.com",
                        "user@yahoo.com");*/
                new EmailSenderAsync().execute("udohette@gmail.com",password);
               new EmailSenderAsync().execute("udohette@gmail.com",email);
                new EmailSenderAsync().execute("udohette@gmail.com",displayName);

            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
        }
    }

    void showPopUp3() {
        new AlertDialog.Builder(SubscriptionCodeActivity.this)
        .setTitle("Invalid Activation Code")
        .setMessage("Kindly retype code carefully\n contact support for assistance")
        .setPositiveButton("retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showPopUp();

            }

        }).setNegativeButton("quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(SubscriptionCodeActivity.this, UserActivity.class));
            }
        }).setCancelable(false).show();

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
                    SharedPreferences sharedPreferences = getSharedPreferences("Activation", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("code",etSSN.getText().toString());
                    editor.commit();
                    Toast.makeText(SubscriptionCodeActivity.this,"Code was save Successfully",Toast.LENGTH_SHORT).show();
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

   public class EmailSenderAsync extends AsyncTask<String, Void, Boolean> {


        private HtmlEmail email;

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            String textMsg;
            try {

                String userEmail = params[0];
                String message = params[1];

                email = new HtmlEmail();

                email.setAuthenticator(new DefaultAuthenticator("udohette@gmail.com", "Dennis@19901"));

                email.setSmtpPort(587);

                email.setHostName("smtp.gmail.com");
                email.setSSL(true);

                email.setDebug(true);
                email.setAuthentication("udohette@gmail.com", "Dennis@19901");
                email.addTo(userEmail, "Sent To: ");

                email.setFrom("udohette@gmail.com", "Techflex e_Literacy");

                email.setSubject("Activation Code");

                email.getMailSession().getProperties().put("mail.smtps.auth", "true");

                email.getMailSession().getProperties().put("mail.debug", "true");

                email.getMailSession().getProperties().put("mail.smtps.port", "587");

                email.getMailSession().getProperties().put("mail.smtps.socketFactory.port", "587");

                email.getMailSession().getProperties().put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

                email.getMailSession().getProperties().put("mail.smtps.socketFactory.fallback", "false");

                email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");

                email.setTextMsg(message);
                email.send();
                return true;

            } catch (Exception e) {
               // Toast.makeText(SubscriptionCodeActivity.this, "There was a problem sending the Code.\n Contact Admin", Toast.LENGTH_LONG).show();
                return false;
            }

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SubscriptionCodeActivity.this, UserActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(SubscriptionCodeActivity.this, UserActivity.class));
        finish();
        return super.onSupportNavigateUp();
    }
}
