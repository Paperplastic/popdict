package com.ampify.dictionarypopup.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean night = sharedPref.getBoolean("night_mode", true);
        if (night) {
            Intent service = new Intent(context, CopyService.class);
            context.startService(service);
        }
    }
}
