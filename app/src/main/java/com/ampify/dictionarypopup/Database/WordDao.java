package com.ampify.dictionarypopup.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDao {

    @Insert
    void insert(Word word);

    @Query("UPDATE word_table SET saved_column = :saved WHERE word_column = :word")
    void favourite(String word, boolean saved);

    @Query("UPDATE word_table SET note_column = :note WHERE word_column = :word")
    void note(String word, String note);

    @Delete()
    void delete(Word word);

    @Query("DELETE FROM word_table WHERE saved_column = 0")
    void removeHistory();

    @Query("UPDATE word_table SET history_column = 0 WHERE history_column = 1")
    void deleteHistory();

    @Query("SELECT * FROM word_table WHERE word_column = :word")
    Word getWord(String word);

    @Query("SELECT * FROM word_table WHERE history_column = 1 ORDER BY mId DESC")
    //livedata gives realtime update
    LiveData<List<Word>> getHistory();

    @Query("SELECT * FROM word_table WHERE saved_column = 1 ORDER BY mId DESC")
    LiveData<List<Word>> getSavedWordsDateDesc();

    @Query("SELECT * FROM word_table WHERE saved_column = 1 ORDER BY mId ASC")
    LiveData<List<Word>> getSavedWordsDateAsc();

    @Query("SELECT * FROM word_table WHERE saved_column = 1 ORDER BY word_column DESC")
    LiveData<List<Word>> getSavedWordsAphDesc();

    @Query("SELECT * FROM word_table WHERE saved_column = 1 ORDER BY word_column ASC")
    LiveData<List<Word>> getSavedWordsAphAsc();

}
