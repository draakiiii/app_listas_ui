package com.example.new_list.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.NonNull;

public class Item {

    // Contador autoincremental
    @Ignore
    private static final AtomicInteger count = new AtomicInteger(0);

    public int id;

    public String title;

    public String description;

    public String dateStart;

    public String dateEnd;


    public Item(String title, String description, String dateStart, String dateEnd) {
        this.id = count.incrementAndGet();
        this.title = title;
        this.description = description;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
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

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Override
    public String toString() {
        return title + " | " + description + " | " + id;
    }
}
