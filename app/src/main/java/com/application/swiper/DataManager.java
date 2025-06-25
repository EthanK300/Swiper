package com.application.swiper;

import android.util.Pair;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DataManager {
    @Query("SELECT * FROM Task")
    List<Task> getAll();

    @Query("SELECT COUNT(*) AS row_count FROM Task")
    int getTotalNum();

    @Insert
    void addTask(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateAll(List<Task> tasks);
}
