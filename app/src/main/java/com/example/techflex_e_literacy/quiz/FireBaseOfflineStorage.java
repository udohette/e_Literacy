package com.example.techflex_e_literacy.quiz;

import android.view.View;

import com.google.firebase.database.FirebaseDatabase;

public class FireBaseOfflineStorage extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
