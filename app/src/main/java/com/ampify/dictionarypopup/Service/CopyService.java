package com.ampify.dictionarypopup.Service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.MainActivity;
import com.ampify.dictionarypopup.R;

public class CopyService extends IntentService {

    NotificationManagerCompat mNotificationManager;

    public CopyService() {
        super("CopyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = NotificationManagerCompat.from(this);
        createNotification();
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipboardManager.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                CharSequence text = item.getText();
                String word;
                if (text != null && !URLUtil.isValidUrl(text.toString())) {
                    word = text.toString();
                } else {
                    word = null;
                }
                if (!TextUtils.isEmpty(word)) {
                    String[] firstWord = word.replaceAll("\\p{Punct}", "")
                            .replaceFirst("\\s+","").split("\\s+");
                    notificationType(firstWord[0].toLowerCase());
                }

            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this,0, notificationIntent, 0);

        Intent nightIntent = new Intent(this, NightReceiver.class);
        PendingIntent nightPendingIntent = PendingIntent
                .getBroadcast(this, 1, nightIntent, 0);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean night = sharedPref.getBoolean("night_mode", false);
        NotificationCompat.Action action;
        if (night) {
            action = new NotificationCompat.Action(R.drawable.ic_night, "Night Mode", nightPendingIntent);
        } else {
            action = new NotificationCompat.Action(R.drawable.ic_night, "Day Mode", nightPendingIntent);
        }
        NotificationCompat.Builder notification = new NotificationCompat
                .Builder(this, Constants.CHANNEL_FOREGROUND)
                .setContentTitle("Popup")
                .setContentText("Copy a word to get it's definition")
                .setSmallIcon(R.drawable.ic_notification)
                .setShowWhen(false)
                .setContentIntent(pendingIntent)
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        startForeground(1, notification.build());
    }

    public void notificationType(String word) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String type = sharedPreferences.getString("notification_list", "");
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra(Constants.WORD_ARG, word);
        NotificationCompat.Builder builder;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch(type) {
            case "null":
                sendBroadcast(intent);
                break;
            case "popup":
                 builder = new NotificationCompat
                        .Builder(this, Constants.CHANNEL_POPUP)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Tap to show definition")
                        .setContentText(word)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setTimeoutAfter(10000)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                mNotificationManager.notify(2, builder.build());
                break;
            case "silent":
                builder = new NotificationCompat
                        .Builder(this, Constants.CHANNEL_SILENT)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Tap to show definition")
                        .setContentText(word)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setTimeoutAfter(10000)
                        .setPriority(NotificationCompat.PRIORITY_LOW);

                mNotificationManager.notify(3, builder.build());
                break;
        }
    }

}
