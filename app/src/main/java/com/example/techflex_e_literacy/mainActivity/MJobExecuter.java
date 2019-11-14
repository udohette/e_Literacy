package com.example.techflex_e_literacy.mainActivity;

import android.content.Context;
import android.os.AsyncTask;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.quiz.SubscriptionCodeActivity;

class MJobExecuter extends AsyncTask<Void, Void, String> {
    Context mContext;
    @Override
    protected String doInBackground(Void... voids) {
        return  "Background Running Task  finishes.......";

    }

}
