package com.application.swiper;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"title", "description", "dueDate", "done"})
public class Task {
    public Task(String name, String description, long dueDate){
        this.title = name;
        this.description = description;
        this.dueDate = dueDate;
        done = false;
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

    @NonNull
    @ColumnInfo(name = "done")
    public boolean done;
}
