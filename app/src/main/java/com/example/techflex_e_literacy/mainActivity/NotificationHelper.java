package com.example.techflex_e_literacy.mainActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.techflex_e_literacy.R;

import static com.example.techflex_e_literacy.mainActivity.LoginActivity.CHANNEL_ID;

public class NotificationHelper {


    public static void displayNotification(Context context,String title, String body){
        Intent intent = new Intent(context,UserActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.add_user)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1,mBuilder.build());

    }
}
