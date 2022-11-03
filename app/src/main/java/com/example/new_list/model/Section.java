package com.example.new_list.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.NonNull;

@Entity
public class Section {

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
    public ArrayList<Item> listOfItems;

    public Section(String title, ArrayList<Item> listOfItems) {
        this.id = count.getAndIncrement();
        this.title = title;
        this.listOfItems = listOfItems;
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

    public ArrayList<Item> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(ArrayList<Item> listOfItems) {
        this.listOfItems = listOfItems;
    }

    @Override
    public String toString() {
        return "Columns{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", listOfItems=" + listOfItems +
                '}';
    }
}
