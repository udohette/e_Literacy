package com.example.techflex_e_literacy.mainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.pojos.MyAdapter;
import com.example.techflex_e_literacy.pojos.Places;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleAPI extends AppCompatActivity {
    Spinner sp1;
    ListView lview1;
    String[] listItem;
    TextView view1, view2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_a_p_i);


        sp1 = findViewById(R.id.sp1);
        lview1 = findViewById(R.id.lview1);
        view1 = findViewById(R.id.tv1);
        view2 = findViewById(R.id.tv2);

    }

    public void getPlaces(View view) {
        Retrofit r = new Retrofit.Builder().baseUrl("https://maps.googleapis.com/").
                addConverterFactory(GsonConverterFactory.create()).build();
        final PlacesAPI places = r.create(PlacesAPI.class);
        Call<Places> call = places.getPlaces();
        call.enqueue(new Callback<Places>() {
            @Override
            public void onResponse(Call<Places> call, Response<Places> response) {
                Places places_ = response.body();
                MyAdapter adapter = new MyAdapter(GoogleAPI.this, places_);
                lview1.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Places> call, Throwable t) {

            }
        });

    }
}