package com.ampify.dictionarypopup.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {

    private WordDao mWordDao;
    private LiveData<List<Word>> mHistoryWords;
    private LiveData<List<Word>> mSavedWordsDateDesc;
    private LiveData<List<Word>> mSavedWordsDateAsc;
    private LiveData<List<Word>> mSavedWordsAphDesc;
    private LiveData<List<Word>> mSavedWordsAphAsc;

    public WordRepository(Application application) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(application);
        mWordDao = wordDatabase.wordDao();
        mHistoryWords = mWordDao.getHistory();
        mSavedWordsDateDesc = mWordDao.getSavedWordsDateDesc();
        mSavedWordsDateAsc = mWordDao.getSavedWordsDateAsc();
        mSavedWordsAphDesc = mWordDao.getSavedWordsAphDesc();
        mSavedWordsAphAsc = mWordDao.getSavedWordsAphAsc();
    }

    //Dummy class for word update
    private static class UpdateParam {
        private String mWord;
        private String mNote;
        private boolean mFavourite;

        public UpdateParam(String word, String note) {
            mWord = word;
            mNote = note;
        }

        public UpdateParam(String word, boolean favourite) {
            mWord = word;
            mFavourite = favourite;
        }
    }

    //Database actions

    public void favourite(String word, boolean saved) {
        UpdateParam param = new UpdateParam(word, saved);
        new UpdateFavouriteWordAsyncTask(mWordDao).execute(param);
    }

    public void note(String word, String note) {
        UpdateParam param = new UpdateParam(word, note);
        new UpdateNoteWordAsyncTask(mWordDao).execute(param);
    }

    public void getWord(String word, GetWordAsyncTask.GetWordResult resultInterface) {
        new GetWordAsyncTask(mWordDao, resultInterface).execute(word);
    }

    public void delete(Word word) {
        new DeleteWordAsyncTask(mWordDao).execute(word);
    }

    public void deleteHistory() {
        new DeleteHistoryAsyncTask(mWordDao).execute();
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


    //AsyncTasks
    private static class UpdateFavouriteWordAsyncTask extends AsyncTask<UpdateParam, Void, Void> {

        private WordDao mWordDao;

        private UpdateFavouriteWordAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(UpdateParam... params) {
            mWordDao.favourite(params[0].mWord, params[0].mFavourite);
            return null;
        }

    }

    private static class UpdateNoteWordAsyncTask extends AsyncTask<UpdateParam, Void, Void> {

        private WordDao mWordDao;

        private UpdateNoteWordAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(UpdateParam... params) {
            mWordDao.note(params[0].mWord, params[0].mNote);
            return null;
        }
    }

    public static class GetWordAsyncTask extends AsyncTask<String, Void, Word> {

        public interface GetWordResult{
            void getResult(Word word);
        }

        private WordDao mWordDao;
        private GetWordResult mResult;

        private GetWordAsyncTask(WordDao wordDao, GetWordResult result) {
            mWordDao = wordDao;
            mResult = result;
        }

        @Override
        protected Word doInBackground(String... strings) {
            return mWordDao.getWord(strings[0]);
        }

        @Override
        protected void onPostExecute(Word word) {
            super.onPostExecute(word);
            mResult.getResult(word);
        }
    }

    private static class DeleteWordAsyncTask extends AsyncTask<Word, Void, Void> {

        private WordDao mWordDao;

        private DeleteWordAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... word) {
            mWordDao.delete(word[0]);
            return null;
        }
    }

    private static class DeleteHistoryAsyncTask extends AsyncTask<Word, Void, Void> {

        private WordDao mWordDao;

        private DeleteHistoryAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... word) {
            mWordDao.removeHistory();
            mWordDao.deleteHistory();
            return null;
        }
    }

}
