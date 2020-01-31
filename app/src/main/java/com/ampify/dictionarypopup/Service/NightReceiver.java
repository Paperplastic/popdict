package com.ampify.dictionarypopup.Service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.MainActivity;
import com.ampify.dictionarypopup.R;

public class NightReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("night_receiver"));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean night = sharedPref.getBoolean("night_mode", false);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("night_mode", !night);
        editor.commit();
        NotificationCompat.Builder builder = notification(context, !night);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    public NotificationCompat.Builder notification(Context context, boolean night) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context,0, notificationIntent, 0);

        Intent nightIntent = new Intent(context, NightReceiver.class);
        PendingIntent nightPendingIntent = PendingIntent
                .getBroadcast(context, 1, nightIntent, 0);

        NotificationCompat.Action action;

        if (night) {
            action = new NotificationCompat.Action(R.drawable.ic_night, "Night Mode", nightPendingIntent);
        } else {
            action = new NotificationCompat.Action(R.drawable.ic_night, "Day Mode", nightPendingIntent);
        }

        NotificationCompat.Builder notification = new NotificationCompat
                .Builder(context, Constants.CHANNEL_FOREGROUND)
                .setContentTitle("Popup")
                .setContentText("Copy a word to get it's definition")
                .setSmallIcon(R.drawable.ic_notification)
                .setShowWhen(false)
                .setContentIntent(pendingIntent)
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return notification;
    }
}
