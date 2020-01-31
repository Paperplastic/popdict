package com.ampify.dictionarypopup.Service;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.Fragments.NoteFragment;
import com.ampify.dictionarypopup.R;
import com.ampify.dictionarypopup.ViewModel;
import com.ampify.dictionarypopup.databinding.FragmentCardBinding;

import java.util.ArrayList;
import java.util.Locale;


public class PopupActivity extends AppCompatActivity
        implements NoteFragment.NoteListener {

    private GestureDetector mGestureDetector;
    FragmentCardBinding binding;
    private ViewModel mViewModel;
    private String mWordName;
    private ArrayList<String> mNoun;
    private ArrayList<String> mVerb;
    private ArrayList<String> mAdj;
    private ArrayList<String> mAdv;
    private boolean mFavourited;
    private String mNote;
    private SharedPreferences sharedPref;
    private boolean mNightMode;
    private TextToSpeech mTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mNightMode = sharedPref.getBoolean("night_mode", false);
        if(mNightMode) {
            setTheme(R.style.DialogNight);
        } else {
            setTheme(R.style.DialogDay);
        }

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setBackgroundDrawableResource(R.color.transparent);

        //set variables
        mWordName = getIntent().getStringExtra(Constants.WORD_ARG);
        mNoun = getIntent().getStringArrayListExtra(Constants.NOUN_ARG);
        mVerb = getIntent().getStringArrayListExtra(Constants.VERB_ARG);
        mAdj = getIntent().getStringArrayListExtra(Constants.ADJ_ARG);
        mAdv = getIntent().getStringArrayListExtra(Constants.ADV_ARG);
        mFavourited = getIntent().getBooleanExtra(Constants.FAVOURITE_ARG, false);
        mNote = getIntent().getStringExtra(Constants.NOTE_ARG);
        mViewModel = new ViewModelProvider(this).get(ViewModel.class);

        //binding
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_card);

        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTextToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    }
                }
            }
        });

        binding.wordName.setText(mWordName);

        //inflate definition
        if (mNoun != null) {
            View definitionPos = getLayoutInflater().inflate(R.layout.definition_pos, null);
            TextView pos = definitionPos.findViewById(R.id.definition_pos);
            pos.setText("noun");
            binding.definitionFrame.addView(definitionPos);
            inflateDefinition(mNoun);
        }

        if (mVerb != null) {
            View definitionPos = getLayoutInflater().inflate(R.layout.definition_pos, null);
            TextView pos = definitionPos.findViewById(R.id.definition_pos);
            pos.setText("verb");
            binding.definitionFrame.addView(definitionPos);
            inflateDefinition(mVerb);
        }

        if (mAdj != null) {
            View definitionPos = getLayoutInflater().inflate(R.layout.definition_pos, null);
            TextView pos = definitionPos.findViewById(R.id.definition_pos);
            pos.setText("adjective");
            binding.definitionFrame.addView(definitionPos);
            inflateDefinition(mAdj);
        }

        if (mAdv != null) {
            View definitionPos = getLayoutInflater().inflate(R.layout.definition_pos, null);
            TextView pos = definitionPos.findViewById(R.id.definition_pos);
            pos.setText("adverb");
            binding.definitionFrame.addView(definitionPos);
            inflateDefinition(mAdv);
        }

        favouriteIcon();
        noteIcon();

        binding.iconNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                NoteFragment noteFragment = NoteFragment.newInstance(mWordName, mNote, PopupActivity.this);
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

        binding.iconAudio.setColorFilter(ContextCompat.getColor(this, R.color.colorIconUnselected));
        binding.iconAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextToSpeech.speak(mWordName, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        binding.closeDialog.setColorFilter(ContextCompat.getColor(this, R.color.colorIconUnselected));
        binding.closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //gesture
        mGestureDetector = new GestureDetector(this, new SimpleGesture());

    }

    public void inflateDefinition(ArrayList<String> list) {
        for (String def : list) {
            View definitonBlock = getLayoutInflater().inflate(R.layout.definition_list_item, null);
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
                definition.setText("\u2022 " + def + "\n");
                sentence.setVisibility(View.GONE);
            }
            binding.definitionFrame.addView(definitonBlock);
        }

    }

    public void favouriteIcon() {
        if (mFavourited) {
            binding.iconFavourite.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        } else {
            binding.iconFavourite.setColorFilter(ContextCompat.getColor(this, R.color.colorIconUnselected));
        }
    }

    public void noteIcon() {
        if (mNote == null || mNote.isEmpty()) {
            binding.iconNote.setColorFilter(ContextCompat.getColor(this, R.color.colorIconUnselected));
        } else {
            binding.iconNote.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        }
    }

    @Override
    public void noteListener(String note) {
        mNote = note;
        noteIcon();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return mGestureDetector.onTouchEvent(ev);
    }


    private class SimpleGesture extends GestureDetector.SimpleOnGestureListener {

        public static final String DEBUG_TAG = "gesturedebug";
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        public void onSwipeRight() {
            finish();
        }

        public void onSwipeLeft() {
            finish();
        }

        public void onSwipeTop() {

        }

        public void onSwipeBottom() {

        }

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
