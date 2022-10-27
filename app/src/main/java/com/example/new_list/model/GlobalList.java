package com.example.new_list.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.new_list.helper.DataConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.NonNull;

@Entity
public class GlobalList implements Serializable {

    // Contador autoincremental
    @Ignore
    private static final AtomicInteger countGlobal = new AtomicInteger(0);

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    @NonNull
    public String name;

    @TypeConverters(DataConverter.class)
    @ColumnInfo(name = "listOfLists")
    public String lists;

    public GlobalList(String name, String lists) {
        this.id = countGlobal.incrementAndGet();
        this.name = name;
        this.lists = lists;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLists() {
        return lists;
    }

    public void setLists(String lists) {
        this.lists = lists;
    }

    @Override
    public String toString() {
        return name + " | " + id + " | " + lists;
    }
}
