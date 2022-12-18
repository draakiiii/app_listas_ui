package com.example.new_list.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.new_list.helper.DataConverter;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.NonNull;

public class  Item {

    // Contador autoincremental
    @Ignore
    private static final AtomicInteger count = new AtomicInteger(0);

    public int id;

    public String title;

    public String description;

    public String dateStart;

    public String dateEnd;

    public Category category;

    private Category subcategorySelected;


    public Item(String title, String description, String dateStart, String dateEnd, Category category) {
        this.id = count.incrementAndGet();
        this.title = title;
        this.description = description;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.category = category;
    }

    public Item(String title, String description, String dateStart, String dateEnd, Category category, Category subcategorySelected) {
        this.id = count.incrementAndGet();
        this.title = title;
        this.description = description;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.category = category;
        this.subcategorySelected = subcategorySelected;
    }

    public int getId() {
        return id;
    }

    public Category getSubcategorySelected() {
        return subcategorySelected;
    }

    public void setSubcategorySelected(Category subcategorySelected) {
        this.subcategorySelected = subcategorySelected;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return title + " | " + description + " | " + id;
    }
}
