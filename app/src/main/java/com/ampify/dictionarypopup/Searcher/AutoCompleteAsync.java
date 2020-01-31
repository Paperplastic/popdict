package com.ampify.dictionarypopup.Searcher;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

public class AutoCompleteAsync extends AsyncTask<Void, Void, Set<String>> {

    public interface AutoCompleteInterface {
        void getAutoCompleteList(Set<String> list);
    }

    private Context mContext;
    private AutoCompleteInterface mListener;

    public AutoCompleteAsync (Context context, AutoCompleteInterface listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Set<String> doInBackground(Void... voids) {
        Set<String> list = new HashSet<>();
        try {
            File file = new File(mContext.getExternalFilesDir(null), "Wordnet/dict");
            URL url = file.toURI().toURL();
            IDictionary dict = new Dictionary(url);
            dict.open();

            addAutoCompleteWords(dict, POS.NOUN, list);
            addAutoCompleteWords(dict, POS.VERB, list);
            addAutoCompleteWords(dict, POS.ADJECTIVE, list);
            addAutoCompleteWords(dict, POS.ADVERB, list);

        } catch (IOException e) {

        }
        return list;
    }

    @Override
    protected void onPostExecute(Set<String> list) {
        super.onPostExecute(list);
        mListener.getAutoCompleteList(list);
    }

    public void addAutoCompleteWords(IDictionary dict, POS pos, Set<String> list) {
        Iterator i = dict.getIndexWordIterator(pos);
        while (i.hasNext()) {
            IIndexWord word = (IIndexWord) i.next();
            String wordString = word.getLemma().replace("_", " ");
            list.add(wordString);
        }
    }

}