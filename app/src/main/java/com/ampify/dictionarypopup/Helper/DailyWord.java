package com.ampify.dictionarypopup.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Random;

public class DailyWord extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int random = new Random().nextInt(147306);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("dailyWord", random);
        editor.commit();
    }
}
