package com.ampify.dictionarypopup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ampify.dictionarypopup.Helper.DailyWord;
import com.ampify.dictionarypopup.Service.CopyService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private BottomNavigationView mBottomNavigationView;
    private ArrayList<String> mList = new ArrayList<>();
    private SharedPreferences sharedPref;
    private AlarmManager mAlarmManager;
    private boolean mService;
    private boolean mNightMode;

    public BroadcastReceiver nightReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            recreate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mNightMode = sharedPref.getBoolean("night_mode", false);
        if(mNightMode) {
            setTheme(R.style.AppThemeNight);
        } else {
            setTheme(R.style.AppThemeDay);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NavigationBar -- toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //NavigationBar -- navigation
        mBottomNavigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_fragment);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);


        mBottomNavigationView.setVisibility(View.GONE);

        createNotificationChannel();

        Intent intent = new Intent(this, DailyWord.class);
        PendingIntent pd = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
        boolean i = (pd != null);
        if (!i) {
            getWordOfTheDay();
        }

        //start on download page if wordnet not downloaded
        checkWordNetExists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dictionary_menu, menu);
        if (mNightMode) {
            Drawable nightIcon = menu.findItem(R.id.night_mode).getIcon();
            nightIcon.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            menu.findItem(R.id.night_mode).setIcon(nightIcon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                navController.navigate(R.id.settingsFragment);
                return true;
            case R.id.action_about:
                navController.navigate(R.id.aboutFragment);
                return true;
            case R.id.night_mode:
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("night_mode", !mNightMode);
                editor.commit();
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkWordNetExists() {
        File file = new File(getExternalFilesDir(null), "Wordnet/dict");
        if (file.exists()) {
            PreferenceManager.setDefaultValues(this, R.xml.preference, true);
            startCopyService();
        }
    }

    public void startCopyService() {
        mService = sharedPref.getBoolean("service", false);
        if (mService) {
            Intent service = new Intent(this, CopyService.class);
            startService(service);
            registerReceiver(nightReceiver, new IntentFilter("night_receiver"));
        }
    }

    public void startReceiver() {
        registerReceiver(nightReceiver, new IntentFilter("night_receiver"));
    }

    public void stopReceiver() {
        unregisterReceiver(nightReceiver);
    }

    public void getWordOfTheDay() {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, DailyWord.class);
        intent.putExtra(Constants.HOUR_DAILY_WORD, 0);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                (1000 * 60 * 60 * 24), alarmIntent);
    }

    @TargetApi(26)
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelForeground = new NotificationChannel(Constants.CHANNEL_FOREGROUND, "dictionary popup", NotificationManager.IMPORTANCE_DEFAULT);
            channelForeground.setDescription("foreground notification");
            NotificationChannel channelPopup = new NotificationChannel(Constants.CHANNEL_POPUP, "popup", NotificationManager.IMPORTANCE_HIGH);
            channelForeground.setDescription("popup notification");
            NotificationChannel channelSilent = new NotificationChannel(Constants.CHANNEL_SILENT, "silent", NotificationManager.IMPORTANCE_LOW);
            channelForeground.setDescription("silent notification");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelForeground);
            manager.createNotificationChannel(channelPopup);
            manager.createNotificationChannel(channelSilent);
        }
    }

    //Navigation logic
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        navController.popBackStack();
        switch (id) {

            case R.id.to_dictionary_fragment:
                Bundle args = new Bundle();
                args.putStringArrayList(Constants.LIST_ARG, mList);
                navController.navigate(R.id.dictionaryFragment);
                break;

            case R.id.to_word_fragment:
                navController.navigate(R.id.wordFragment);
                break;

            case R.id.to_history_fragment:
                navController.navigate(R.id.historyFragment);
                break;
        }
        return true;
    }

}
