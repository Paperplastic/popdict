package com.ampify.dictionarypopup;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ampify.dictionarypopup.Database.Word;
import com.ampify.dictionarypopup.Database.WordRepository;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private WordRepository mRepository;
    private LiveData<List<Word>> mHistoryWords;
    private LiveData<List<Word>> mSavedWordsDateDesc;
    private LiveData<List<Word>> mSavedWordsDateAsc;
    private LiveData<List<Word>> mSavedWordsAphDesc;
    private LiveData<List<Word>> mSavedWordsAphAsc;

    public ViewModel(Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mHistoryWords = mRepository.getHistoryWords();
        mSavedWordsDateDesc = mRepository.getSavedWordsDateDesc();
        mSavedWordsDateAsc = mRepository.getSavedWordsDateAsc();
        mSavedWordsAphDesc = mRepository.getSavedWordsAphDesc();
        mSavedWordsAphAsc = mRepository.getSavedWordsAphAsc();
    }

    public LiveData<List<Word>> getHistoryWords() {
        return mHistoryWords;
    }

    public LiveData<List<Word>> getSavedWordsDateDesc() {
        return mSavedWordsDateDesc;
    }

    public LiveData<List<Word>> getSavedWordsDateAsc() {
        return mSavedWordsDateAsc;
    }

    public LiveData<List<Word>> getSavedWordsAphDesc() {
        return mSavedWordsAphDesc;
    }

    public LiveData<List<Word>> getSavedWordsAphAsc() {
        return mSavedWordsAphAsc;
    }

    public void getWord(String word, WordRepository.GetWordAsyncTask.GetWordResult resultInterface) {
        mRepository.getWord(word, resultInterface);
    }

    public void favourite(String word, Boolean saved) {
        mRepository.favourite(word, saved);
    }

    public void note(String word, String note) {
        mRepository.note(word, note);
    }

    public void delete(Word word) {
        mRepository.delete(word);
    }

    public void deleteHistory() {
        mRepository.deleteHistory();
    }

}
