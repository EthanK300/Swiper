package com.application.swiper;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {
    public Task(String name, String description, long timestamp){
        this.title = name;
        this.description = description;
        this.timestamp = timestamp;
    }

    public Task(){

    }

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "timestamp")
    public long timestamp;
}
