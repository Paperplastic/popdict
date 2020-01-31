package com.ampify.dictionarypopup.Searcher;

import android.content.Context;
import android.os.AsyncTask;

import com.ampify.dictionarypopup.Database.Word;

//Asynctask for word search
public class Inquiry extends AsyncTask<String, Void, Word> {

    public interface InquiryListener {
        void onResponse(Word word);
    }

    private Context mContext;
    private InquiryListener mListener;

    public Inquiry(Context context, InquiryListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Word doInBackground(String... strings) {
        Search s = new Search(mContext);
        return s.searchWord(strings[0]);
    }

    @Override
    protected void onPostExecute(Word word) {
        super.onPostExecute(word);
        mListener.onResponse(word);
    }
}
