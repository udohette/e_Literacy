package com.example.techflex_e_literacy.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techflex_e_literacy.R;

public class CardPayment extends AppCompatActivity {
    Button btnMove;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_payment);

        btnMove = findViewById(R.id.ok);
        username = (EditText)findViewById(R.id.username);
        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().trim().length()==0){
                    Toast.makeText(CardPayment.this,"Enter Cart Number...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String input = username.getText().toString();
                    Intent i = new Intent(CardPayment.this,Bill.class);
                    i.putExtra("input",input);
                    startActivity(i);
                }
            }
        });
    }
}
