package com.example.new_list.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.new_list.helper.DataConverter;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.NonNull;

@Entity(tableName = "categories")
public class Category {
    // Contador autoincremental

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    // Esto es un array de Category. Sirve para hacer una lista de las subcategorías de esta categoría en cuestión
    @TypeConverters(DataConverter.class)
    @ColumnInfo(name = "arrayOfSubcategories")
    private String arrayOfSubcategories;

    public Category(String name) {
        this.name = name;
    }

    public String getArrayOfSubcategories() {
        return arrayOfSubcategories;
    }

    public void setArrayOfSubcategories(String arrayOfSubcategories) {
        this.arrayOfSubcategories = arrayOfSubcategories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
