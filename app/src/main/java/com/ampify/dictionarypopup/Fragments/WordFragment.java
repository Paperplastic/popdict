package com.ampify.dictionarypopup.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
public class WordFragment extends Fragment {

    private ViewModel mViewModel;
    private FragmentWordBinding binding;
    private WordAdapter mAdapter;
    private SharedPreferences sharedPref;
    private int mOrder;

    private ActionMode mActionMode;
    private Menu mContextualMenu;
    private SparseBooleanArray mSelectedWords;
    private List<Word> mWordList;
    private int mItemPosition = 0;

    public WordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedWords = new SparseBooleanArray();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_word, container, false);

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mOrder = sharedPref.getInt(Constants.WORD_LIST_ORDER, 1);
        reorder(mOrder);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(binding.recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.word_menu, menu);
        Boolean mNight = sharedPref.getBoolean("night_mode", false);
        if (mNight) {
            menu.findItem(R.id.word_order).getIcon()
                    .setColorFilter(getResources().getColor(R.color.colorToolbarIconUnselected), PorterDuff.Mode.SRC_IN);
        }
        switch(mOrder) {
            case 1:
                menu.findItem(R.id.date_desc).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.date_asc).setChecked(true);
                break;
            case 3:
                menu.findItem(R.id.aph_desc).setChecked(true);
                break;
            case 4:
                menu.findItem(R.id.aph_asc).setChecked(true);
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.aph_asc:
                sharedPref.edit().putInt(Constants.WORD_LIST_ORDER, 1).commit();
                mOrder = 1;
                reorder(1);
                return true;
            case R.id.aph_desc:
                sharedPref.edit().putInt(Constants.WORD_LIST_ORDER, 2).commit();
                mOrder = 2;
                reorder(2);
                return true;
            case R.id.date_desc:
                sharedPref.edit().putInt(Constants.WORD_LIST_ORDER, 3).commit();
                mOrder = 3;
                reorder(3);
                return true;
            case R.id.date_asc:
                sharedPref.edit().putInt(Constants.WORD_LIST_ORDER, 4).commit();
                mOrder = 4;
                reorder(4);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void reorder(int order) {
        mAdapter = new WordAdapter();
        binding.recyclerView.setAdapter(mAdapter);
        switch (order) {
            case 1:
                mViewModel.getSavedWordsAphAsc().observe(this, new Observer<List<Word>>() {
                    @Override
                    public void onChanged(final List<Word> words) {
                        mAdapter.setWords(words);
                        mWordList = words;
                    }
                });
                break;

            case 2:
                mViewModel.getSavedWordsAphDesc().observe(this, new Observer<List<Word>>() {
                    @Override
                    public void onChanged(final List<Word> words) {
                        mAdapter.setWords(words);
                        mWordList = words;
                    }
                });
                break;

            case 3:
                mViewModel.getSavedWordsDateDesc().observe(this, new Observer<List<Word>>() {
                    @Override
                    public void onChanged(final List<Word> words) {
                        mAdapter.setWords(words);
                        mWordList = words;
                    }
                });
                break;

            case 4:
                mViewModel.getSavedWordsDateAsc().observe(this, new Observer<List<Word>>() {
                    @Override
                    public void onChanged(final List<Word> words) {
                        mAdapter.setWords(words);
                        mWordList = words;
                    }
                });
                break;
        }
    }

    private class WordHolder extends RecyclerView.ViewHolder {

        private final TextView mWordItemView;

        private WordHolder(View view, final List<Word> words) {
            super(view);
            mWordItemView = view.findViewById(R.id.word_item_title);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActionMode == null) {
                        Word word = words.get(getAdapterPosition());
                        mItemPosition = getAdapterPosition();
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
                    } else {
                        toggleSelection(getAdapterPosition());
                        updateToolbar();
                    }

                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mActionMode != null) {
                        return false;
                    }
                    mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(actionModeCallback);
                    toggleSelection(getAdapterPosition());
                    updateToolbar();
                    mAdapter.notifyDataSetChanged();
                    return true;
                }
            });

        }

        private void toggleSelection(int position) {
            if (!mSelectedWords.get(position)) {
                mSelectedWords.put(position, true);
            } else {
                mSelectedWords.delete(position);
            }
            mAdapter.notifyItemChanged(position);
            updateContextualMenu();
        }

        private void updateToolbar() {
            int size = mSelectedWords.size();
            mActionMode.setTitle(size + " selected");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyItemChanged(mItemPosition);
    }

    private class WordAdapter extends RecyclerView.Adapter<WordHolder> {

        private List<Word> mWords;

        public WordAdapter() {
            mWords = new ArrayList<>();
        }

        @Override
        public WordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_word, parent, false);
            return new WordHolder(view, mWords);
        }

        @Override
        public void onBindViewHolder(WordHolder holder, int position) {
            Word word = mWords.get(position);
            holder.mWordItemView.setText(word.getWord());
            CheckBox check = holder.itemView.findViewById(R.id.word_item_check);
            if (mActionMode != null) {
                check.setVisibility(View.VISIBLE);
                if (mSelectedWords.get(holder.getAdapterPosition())) {
                    check.setChecked(true);
                } else {
                    check.setChecked(false);
                }
            } else {
                check.setVisibility(View.INVISIBLE);
            }
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

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.word_contextual_menu, menu);
            mContextualMenu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.delete_word:
                    deleteWords();
                    mode.finish();
                    return true;
                case R.id.select_all:
                    if (mSelectedWords.size() == mAdapter.getItemCount()) {
                        mSelectedWords.clear();
                    } else {
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            if (!mSelectedWords.get(i)) {
                                mSelectedWords.put(i, true);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    updateContextualMenu();

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mSelectedWords.clear();
            mAdapter.notifyDataSetChanged();
        }
    };

    public void deleteWords() {
        for (int i = 0 ; i < mAdapter.getItemCount(); i++) {
            if (mSelectedWords.get(i)) {
                mViewModel.favourite(mWordList.get(i).getWord(), false);
                mViewModel.note(mWordList.get(i).getWord(), "");
            }
        }
    }

    public void updateContextualMenu() {
        MenuItem selectAll = mContextualMenu.findItem(R.id.select_all);
        if (mSelectedWords.size() == mAdapter.getItemCount()) {
            selectAll.setTitle("Deselect All");
        } else {
            selectAll.setTitle("Select All");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }
}
