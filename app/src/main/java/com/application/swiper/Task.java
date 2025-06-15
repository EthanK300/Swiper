package com.application.swiper;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"title", "description", "dueDate"})
public class Task {
    public Task(String name, String description, long dueDate){
        this.title = name;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Task(){

    }

    @NonNull
    @ColumnInfo(name = "title")
    public String title;

    @NonNull
    @ColumnInfo(name = "description")
    public String description;

    @NonNull
    @ColumnInfo(name = "dueDate")
    public long dueDate;
}
