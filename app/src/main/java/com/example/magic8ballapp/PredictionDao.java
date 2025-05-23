package com.example.magic8ballapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PredictionDao {
    @Insert
    void insert(PredictionItem item);

    @Query("SELECT * FROM predictions ORDER BY id DESC")
    LiveData<List<PredictionItem>> getAll();

    @Query("SELECT * FROM predictions")
    List<PredictionItem> getAllNow();
}