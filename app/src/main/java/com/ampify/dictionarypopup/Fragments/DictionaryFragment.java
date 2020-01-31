package com.ampify.dictionarypopup.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.Database.Word;
import com.ampify.dictionarypopup.Searcher.Inquiry;
import com.ampify.dictionarypopup.R;
import com.ampify.dictionarypopup.databinding.FragmentDictionaryBinding;

import java.util.Set;

public class DictionaryFragment extends Fragment
        implements Inquiry.InquiryListener {

    private FragmentDictionaryBinding binding;
    private String[] mList;
    private String mWordOfTheDay;

    public DictionaryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> set = sharedPref.getStringSet(Constants.LIST_ARG, null);
        mList = set.toArray(new String[0]);

        int index = sharedPref.getInt("dailyWord", 2);
        mWordOfTheDay = mList[index];

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_dictionary, container, false);

        binding.wordDay.setText("" + mWordOfTheDay);
        binding.wordDayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Inquiry(getContext(), DictionaryFragment.this).execute(mWordOfTheDay);
            }
        });

        int options = binding.searchView.getImeOptions();
        binding.searchView.setImeOptions(options| EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new Inquiry(getContext(), DictionaryFragment.this).execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //set autocomplete
        if (getContext() != null) {
            SearchView.SearchAutoComplete autoComplete = binding.searchView
                    .findViewById(androidx.appcompat.R.id.search_src_text);

            ArrayAdapter<String> dataAdapter =
                    new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1, mList);

            autoComplete.setAdapter(dataAdapter);
            autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String word = (String) parent.getItemAtPosition(position);
                    new Inquiry(getContext(), DictionaryFragment.this).execute(word);
                }
            });
        }

        return binding.getRoot();
    }

    //Search result change DefinitonFragment
    @Override
    public void onResponse(Word word) {
        Bundle args = new Bundle();
        args.putString(Constants.WORD_ARG, word.getWord());
        args.putStringArrayList(Constants.NOUN_ARG, word.getNoun());
        args.putStringArrayList(Constants.VERB_ARG, word.getVerb());
        args.putStringArrayList(Constants.ADJ_ARG, word.getAdj());
        args.putStringArrayList(Constants.ADV_ARG, word.getAdv());
        args.putBoolean(Constants.FAVOURITE_ARG, word.isSaved());
        args.putString(Constants.NOTE_ARG, word.getNote());
        args.putStringArray(Constants.LIST_ARG, mList);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_dictionaryFragment_to_definitionFragment, args);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}


