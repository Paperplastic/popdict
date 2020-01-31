package com.ampify.dictionarypopup.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.Database.Word;
import com.ampify.dictionarypopup.R;
import com.ampify.dictionarypopup.Service.PopupActivity;
import com.ampify.dictionarypopup.ViewModel;
import com.ampify.dictionarypopup.databinding.FragmentWordBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private ViewModel mViewModel;

    public HistoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentWordBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_word, container, false);
        final HistoryAdapter adapter = new HistoryAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(binding.recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        mViewModel = new ViewModelProvider(this).get(ViewModel.class);
        mViewModel.getHistoryWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable final List<Word> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setWords(words);
            }
        });
        setHasOptionsMenu(true);
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.history_menu, menu);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean mNight = sharedPref.getBoolean("night_mode", false);
        if (mNight) {
            menu.findItem(R.id.delete_history).getIcon()
                    .setColorFilter(getResources().getColor(R.color.colorToolbarIconUnselected), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_history:
                mViewModel.deleteHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class HistoryHolder extends RecyclerView.ViewHolder {

        private final TextView mWordItemView;

        public HistoryHolder(View view, final List<Word> words) {
            super(view);
            mWordItemView = view.findViewById(R.id.word_item_title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Word word = words.get(getAdapterPosition());
                    Bundle args = new Bundle();
                    args.putString(Constants.WORD_ARG, word.getWord());
                    args.putStringArrayList(Constants.NOUN_ARG, word.getNoun());
                    args.putStringArrayList(Constants.VERB_ARG, word.getVerb());
                    args.putStringArrayList(Constants.ADJ_ARG, word.getAdj());
                    args.putStringArrayList(Constants.ADV_ARG, word.getAdv());
                    args.putBoolean(Constants.FAVOURITE_ARG, word.isSaved());
                    args.putString(Constants.NOTE_ARG, word.getNote());
                    Intent intent = new Intent(getContext(), PopupActivity.class);
                    intent.putExtras(args);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }

    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

        private List<Word> mWords;

        public HistoryAdapter() {
            mWords = new ArrayList<>();
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_word, parent, false);
            return new HistoryHolder(view, mWords);
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {
            Word word = mWords.get(position);
            holder.mWordItemView.setText(word.getWord());
        }

        @Override
        public int getItemCount() {
            return mWords.size();
        }

        void setWords(List<Word> words){
            mWords = words;
            notifyDataSetChanged();
        }

    }

}
