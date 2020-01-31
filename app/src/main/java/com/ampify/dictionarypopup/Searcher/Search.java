package com.ampify.dictionarypopup.Searcher;

import android.content.Context;

import com.ampify.dictionarypopup.Database.Word;
import com.ampify.dictionarypopup.Database.WordDatabase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

//TODO: check whole class logic
public class Search {

    private Context mContext;
    private IDictionary dict;

    public Search(Context context) {
        mContext = context;
    }

    //TODO: set word POS in database
    public Word searchWord(String s) {
        try {
            File file = new File(mContext.getExternalFilesDir(null), "Wordnet/dict");
            URL url = file.toURI().toURL();
            dict = new Dictionary(url);
            dict.open();

            String morphedWord = morph(s);

            Word w = WordDatabase.getDatabase(mContext).wordDao().getWord(morphedWord);
            if (w == null) {
                ArrayList<String> noun = formatGloss(morphedWord, POS.NOUN);
                ArrayList<String> verb = formatGloss(morphedWord, POS.VERB);
                ArrayList<String> adj = formatGloss(morphedWord, POS.ADJECTIVE);
                ArrayList<String> adv = formatGloss(morphedWord, POS.ADVERB);
                Word word = new Word(morphedWord, noun, verb, adj, adv, null, false, true);
                WordDatabase.getDatabase(mContext).wordDao().insert(word);
                return word;
            } else {
                WordDatabase.getDatabase(mContext).wordDao().delete(w);
                Word word = new Word(w.getWord(), w.getNoun(), w.getVerb(), w.getAdj(), w.getAdv(),
                        w.getNote(), w.isSaved(), true);
                WordDatabase.getDatabase(mContext).wordDao().insert(word);
                return word;
            }

        } catch(IOException e) {

        }
        return null;
    }

    public String morph(String word) {
        if (dict.getIndexWord(word, POS.NOUN) != null) {
            return word;
        }
        WordnetStemmer stemmer = new WordnetStemmer(dict);
        for (String s : stemmer.findStems(word, POS.NOUN)) {
            if (dict.getIndexWord(s, POS.NOUN) != null) {
                return s;
            }
        }
        for (String s : stemmer.findStems(word, POS.VERB)) {
            if (dict.getIndexWord(s, POS.VERB) != null) {
                return s;
            }
        }
        for (String s : stemmer.findStems(word, POS.ADJECTIVE)) {
            if (dict.getIndexWord(s, POS.ADJECTIVE) != null) {
                return s;
            }
        }
        for (String s : stemmer.findStems(word, POS.ADVERB)) {
            if (dict.getIndexWord(s, POS.ADVERB) != null) {
                return s;
            }
        }
        return word;
    }

    public ArrayList<String> formatGloss(String word, POS pos) {
        IIndexWord idxWord = dict.getIndexWord(word, pos);
        if (idxWord == null) {
            return null;
        } else {
            ArrayList<String> def = new ArrayList<>();
            int i = 0;
            for (IWordID wid : idxWord.getWordIDs()) {
                IWord wd = dict.getWord(wid);
                def.add(wd.getSynset().getGloss());
            }
            return def;
        }
    }

}
