package com.example.expressblog.dao;

import android.provider.Telephony;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.expressblog.entities.Draft;
import com.example.expressblog.entities.Draft;

import java.util.List;

@Dao
public interface DraftDao {
    @Query("SELECT * FROM DRAFTS ORDER BY ID DESC")
    List<Draft> getAllDraft();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDraft(Draft draft);

    @Delete
    void deleteDraft(Draft draft);
}
