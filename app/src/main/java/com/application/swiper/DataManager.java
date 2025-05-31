package com.application.swiper;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataManager {
    @Query("SELECT * FROM Tasks")
    List<Tasks> getAll();
}
