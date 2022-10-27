package com.example.new_list.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.NonNull;

@Entity
public class Item {

    // Contador autoincremental
    @Ignore
    private static final AtomicInteger count = new AtomicInteger(0);

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "title")
    @NonNull
    public String title;

    @ColumnInfo(name = "description")
    public String description;


    public Item(String title, String description) {
        this.id = count.incrementAndGet();
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return title + " | " + description + " | " + id;
    }
}
