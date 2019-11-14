package com.example.techflex_e_literacy.quiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MakePayment extends AppCompatActivity {
    Button crypto_payment;
    Button paytm;
    Button paypal;
    Button qpay;
    Button bank_trans;
    TextView receipt1;
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_payment_activity);
        crypto_payment = findViewById(R.id.crypto);
        paytm =findViewById(R.id.paytm);
        paypal = findViewById(R.id.paypal);
        qpay = findViewById(R.id.gpay);
        bank_trans = findViewById(R.id.bank_trans);

        Intent i = getIntent();
        //final String input = i.getStringExtra("input");
        //final String q = input + date.getTime()+"$@#";
        crypto_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showPopUp2();
                //Toast.makeText(MakePayment.this,"Payment Successful", Toast.LENGTH_SHORT).show();
               // receipt1.setText(q);

            }
        });
        bank_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showPopUp();
            }
        });
        qpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://pay.google.com/payments/u/0/home#"));
                startActivity(intent);
            }
        });
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.paypal.com/us/signin"));
                startActivity(intent);
            }
        });
        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://paytm.com/"));
                startActivity(intent);
               // receipt1.setText(q);
            }
        });
    }
    void showPopUp() {
        new AlertDialog.Builder(MakePayment.this)
        .setIcon(R.drawable.back_img)
        .setTitle("Payment Info!")
        .setMessage("GT Bank\nDennis Samuel\nAccount Number: 0122494775\n\nUse your Email Address as Description for your Payment")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }

        })
        .show();
    }
    void showPopUp2() {
        new AlertDialog.Builder(MakePayment.this)
        .setIcon(R.drawable.back_img)
        .setTitle("Attention!")
        .setMessage("Kindly Choose your Crypto Choice?")
        .setPositiveButton("Bitcoins", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MakePayment.this,"Coming Soon!", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(MakePayment.this, MakePayment.class);
                startActivity(intent);*/
            }
        }).setNegativeButton("TBC", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               showPopUp3();
            }
        }).show();
    }
    void showPopUp3() {
         new AlertDialog.Builder(MakePayment.this)
        .setIcon(R.drawable.back_img)
        .setTitle("Attention!")
        .setMessage("You're paying halve cash / $500 worth of TBC \n\nWallet Address!" +
                "\nNA6PAW-7C75GG-6JPBKI-UPOTMB-OB6QTK-VQJGST-LFLI\n\nUse your Email Address as Description for your Payment")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }

        }).show();
    }
}
