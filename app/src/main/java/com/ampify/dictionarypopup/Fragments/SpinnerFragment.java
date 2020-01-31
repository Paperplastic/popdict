package com.ampify.dictionarypopup.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.R;
import com.ampify.dictionarypopup.Searcher.AutoCompleteAsync;

import java.util.Random;
import java.util.Set;

public class SpinnerFragment extends Fragment {

    private Set<String> mList;
    private int mIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        mList = sharedPref.getStringSet(Constants.LIST_ARG, null);
        if (mList == null) {
            autoCompleteDB();
        } else {
            mIndex = sharedPref.getInt("dailyWord", 0);
            String[] listArray = mList.toArray(new String[0]);
            String checkWord = listArray[mIndex];
            int random = new Random().nextInt(147306);
            while (checkWord.contains(" ") || checkWord.matches(".*\\d.*")) {
                checkWord = listArray[random];
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("dailyWord", random);
                editor.commit();
            }
            mIndex = sharedPref.getInt("dailyWord", 0);
            navToDictionary();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spinner, container, false);
        return v;
    }

    public void autoCompleteDB() {
        new AutoCompleteAsync(getContext(), new AutoCompleteAsync.AutoCompleteInterface() {
            @Override
            public void getAutoCompleteList(Set<String> list) {
                mList = list;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putStringSet(Constants.LIST_ARG, mList);
                editor.commit();

                navToDictionary();
            }
        }).execute();
    }

    public void navToDictionary() {
        Bundle args = new Bundle();
        getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
        args.putInt(Constants.RANDOM_INT_ARG, mIndex);
        final NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.spinnerFragment, true)
                .build();
        NavHostFragment.findNavController(SpinnerFragment.this)
                .navigate(R.id.action_spinnerFragment_to_dictionaryFragment, args, navOptions);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}
