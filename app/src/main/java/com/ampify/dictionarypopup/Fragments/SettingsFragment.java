package com.ampify.dictionarypopup.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.ampify.dictionarypopup.MainActivity;
import com.ampify.dictionarypopup.R;
import com.ampify.dictionarypopup.Service.CopyService;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SwitchPreference service = findPreference("service");

        final MainActivity main = (MainActivity) getActivity();

        service.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!sharedPreferences.getBoolean("service", false)) {
                    Intent intent = new Intent(getActivity(), CopyService.class);
                    main.startReceiver();
                    getActivity().startService(intent);
                } else {
                    Intent intent = new Intent(getActivity(), CopyService.class);
                    main.stopReceiver();
                    getActivity().stopService(intent);
                }
                return true;
            }
        });

        ListPreference notification = findPreference("notification_list");
        notification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return true;
            }
        });
    }

}
