package com.ampify.dictionarypopup.Fragments;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.Database.Word;
import com.ampify.dictionarypopup.R;
import com.ampify.dictionarypopup.Searcher.Inquiry;
import com.ampify.dictionarypopup.ViewModel;
import com.ampify.dictionarypopup.databinding.FragmentDefinitionBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class DefinitionFragment extends Fragment
        implements NoteFragment.NoteListener, Inquiry.InquiryListener {

    private FragmentDefinitionBinding binding;
    private SharedPreferences sharedPref;
    private ViewModel mViewModel;
    private String[] mList;
    private String mWordName;
    private ArrayList<String> mNoun;
    private ArrayList<String> mVerb;
    private ArrayList<String> mAdj;
    private ArrayList<String> mAdv;
    private boolean mFavourited;
    private String mNote;
    private SearchView mSearchView;
    private TextToSpeech mTextToSpeech;

    public DefinitionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomNavigationView btm = getActivity().findViewById(R.id.nav_view);
        if (btm.getVisibility() == View.GONE) {
            btm.setVisibility(View.VISIBLE);
        }

        mWordName = getArguments().getString(Constants.WORD_ARG);
        mNoun = getArguments().getStringArrayList(Constants.NOUN_ARG);
        mVerb = getArguments().getStringArrayList(Constants.VERB_ARG);
        mAdj = getArguments().getStringArrayList(Constants.ADJ_ARG);
        mAdv = getArguments().getStringArrayList(Constants.ADV_ARG);
        mFavourited = getArguments().getBoolean(Constants.FAVOURITE_ARG, false);
        mNote = getArguments().getString(Constants.NOTE_ARG);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> set = sharedPref.getStringSet(Constants.LIST_ARG, null);
        mList = set.toArray(new String[0]);

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);

        mTextToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTextToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    }
                } else {
                }
            }
        });

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_definition, container, false);

        binding.wordName.setText(mWordName);

        //inflate definition
        if (mNoun != null) {
            View definitionPos = inflater.inflate(R.layout.definition_pos, container, false);
            TextView pos = definitionPos.findViewById(R.id.definition_pos);
            pos.setText("noun");
            binding.definitionFrame.addView(definitionPos);
            inflateDefinition(mNoun, inflater, container);
        }

        if (mVerb != null) {
            View definitionPos = inflater.inflate(R.layout.definition_pos, container, false);
            TextView pos = definitionPos.findViewById(R.id.definition_pos);
            pos.setText("verb");
            binding.definitionFrame.addView(definitionPos);
            inflateDefinition(mVerb, inflater, container);
        }

        if (mAdj != null) {
            View definitionPos = inflater.inflate(R.layout.definition_pos, container, false);
            TextView pos = definitionPos.findViewById(R.id.definition_pos);
            pos.setText("adjective");
            binding.definitionFrame.addView(definitionPos);
            inflateDefinition(mAdj, inflater, container);
        }

        if (mAdv != null) {
            View definitionPos = inflater.inflate(R.layout.definition_pos, container, false);
            TextView pos = definitionPos.findViewById(R.id.definition_pos);
            pos.setText("adverb");
            binding.definitionFrame.addView(definitionPos);
            inflateDefinition(mAdv, inflater, container);
        }

        favouriteIcon();
        noteIcon();

        binding.iconNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                NoteFragment noteFragment = NoteFragment.newInstance(mWordName, mNote, DefinitionFragment.this);
                noteFragment.show(fm, "note fragment");
            }
        });

        binding.iconFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.favourite(mWordName, !mFavourited);
                mFavourited = !mFavourited;
                favouriteIcon();
            }
        });

        binding.iconAudio.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorIconUnselected));
        binding.iconAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextToSpeech.speak(mWordName, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        return binding.getRoot();
    }

    public void inflateDefinition(ArrayList<String> list, LayoutInflater inflater, ViewGroup container) {
        for (String def : list) {
            View definitonBlock = inflater.inflate(R.layout.definition_list_item, container, false);
            String[] part = def.split(";\\s\"");
            TextView definition = definitonBlock.findViewById(R.id.item_definition);
            TextView sentence = definitonBlock.findViewById(R.id.item_sentence);
            if (part.length > 1) {
                definition.setText("\u2022 " + part[0]);
                String sentences = "";
                for (int i = 1; i < part.length; i++) {
                    sentences += "\"" + part[i] + "\n";
                }
                sentence.setText(sentences);
            } else {
                definition.setText("\u2022 " + def);
                sentence.setVisibility(View.GONE);
            }
            binding.definitionFrame.addView(definitonBlock);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.appbar_menu, menu);

        Boolean mNight = sharedPref.getBoolean("night_mode", false);
        if (mNight) {
            menu.findItem(R.id.search).getIcon()
                    .setColorFilter(getResources().getColor(R.color.colorToolbarIconUnselected), PorterDuff.Mode.SRC_IN);
        }

        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.setQuery("", false);
                mSearchView.setIconified(true);
                new Inquiry(getContext(), DefinitionFragment.this).execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if (getContext() != null) {
            SearchView.SearchAutoComplete autoComplete = mSearchView
                    .findViewById(androidx.appcompat.R.id.search_src_text);

            ArrayAdapter<String> dataAdapter =
                    new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, mList);

            autoComplete.setAdapter(dataAdapter);
            autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String word = (String) parent.getItemAtPosition(position);
                    new Inquiry(getContext(), DefinitionFragment.this).execute(word);
                }
            });
        }
    }

    public void favouriteIcon() {
        if (mFavourited) {
            binding.iconFavourite.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
        } else {
            binding.iconFavourite.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorIconUnselected));
        }
    }

    public void noteIcon() {
        if (mNote == null || mNote.isEmpty()) {
            binding.iconNote.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorIconUnselected));
        } else {
            binding.iconNote.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    @Override
    public void onResponse(Word word) {
        Bundle args = new Bundle();
        args.putString(Constants.WORD_ARG, word.getWord());
        args.putStringArrayList(Constants.NOUN_ARG, word.getNoun());
        args.putStringArrayList(Constants.VERB_ARG, word.getVerb());
        args.putStringArrayList(Constants.ADJ_ARG, word.getAdj());
        args.putStringArrayList(Constants.ADV_ARG, word.getAdv());
        NavHostFragment.findNavController(this).popBackStack(R.id.definitionFragment, true);
        NavHostFragment.findNavController(this)
                .navigate(R.id.definitionFragment, args);
    }

    @Override
    public void noteListener(String note) {
        mNote = note;
        noteIcon();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }
}
