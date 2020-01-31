package com.ampify.dictionarypopup.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;

@Entity(tableName = "word_table")
public class Word {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "word_column")
    private String mWord;

    @ColumnInfo(name = "definition_column")
    private String mDefinition;

    @TypeConverters(Converter.class)
    private ArrayList<String> mNoun;

    @TypeConverters(Converter.class)
    private ArrayList<String> mVerb;

    @TypeConverters(Converter.class)
    private ArrayList<String> mAdj;

    @TypeConverters(Converter.class)
    private ArrayList<String> mAdv;

    @ColumnInfo(name = "note_column")
    private String mNote;

    @ColumnInfo(name = "saved_column")
    private boolean mSaved;

    @ColumnInfo(name = "history_column")
    private boolean mHistory;

    public Word(String word, ArrayList<String> noun, ArrayList<String> verb, ArrayList<String> adj,
                ArrayList<String> adv, String note, boolean saved, boolean history) {
        mWord = word;
        mNoun = noun;
        mVerb = verb;
        mAdj = adj;
        mAdv = adv;
        mNote = note;
        mSaved = saved;
        mHistory = history;
    }

    public void setWord(String word) {
        mWord = word;
    }

    public void setDefinition(String definition) {
        mDefinition = definition;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public void setSaved(boolean saved) {
        mSaved = saved;
    }

    public void setHistory(boolean history) {
        mHistory = history;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public String getWord() {
        return mWord;
    }

    public String getDefinition() {
        return mDefinition;
    }

    public String getNote() {
        return mNote;
    }

    public boolean isSaved() {
        return mSaved;
    }

    public boolean isHistory() {
        return mHistory;
    }

    public ArrayList<String> getNoun() {
        return mNoun;
    }

    public void setNoun(ArrayList<String> noun) {
        mNoun = noun;
    }

    public ArrayList<String> getVerb() {
        return mVerb;
    }

    public void setVerb(ArrayList<String> verb) {
        mVerb = verb;
    }

    public ArrayList<String> getAdj() {
        return mAdj;
    }

    public void setAdj(ArrayList<String> adj) {
        mAdj = adj;
    }

    public ArrayList<String> getAdv() {
        return mAdv;
    }

    public void setAdv(ArrayList<String> adv) {
        mAdv = adv;
    }

}