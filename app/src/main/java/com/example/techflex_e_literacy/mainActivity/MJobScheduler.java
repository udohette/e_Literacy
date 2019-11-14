package com.example.techflex_e_literacy.mainActivity;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.quiz.Bill;
import com.example.techflex_e_literacy.quiz.QuizActivity;

import java.lang.ref.WeakReference;

public class MJobScheduler extends JobService {
    private  MJobExecuter mJobExecuter;
    @Override
    public  boolean onStartJob(final JobParameters params) {
            mJobExecuter = new MJobExecuter(){
            @Override
            protected void onPostExecute(String s) {
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                jobFinished(params,false);
            }
        };
        mJobExecuter.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobExecuter.cancel(true);
        return false;
    }
    void expiredSubscriptionPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MJobScheduler.this);
        builder.setIcon(R.drawable.back_img);
        builder.setTitle("Attention!");
        builder.setMessage("Your Subscription has Expired, Kindly renew");
        builder.setPositiveButton("Renew", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MJobScheduler.this, Bill.class);
                startActivity(intent);
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MJobScheduler.this, UserActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
        AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }
}
