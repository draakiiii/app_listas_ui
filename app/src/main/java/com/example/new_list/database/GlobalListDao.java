package com.example.new_list.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface GlobalListDao {

    @Query("SELECT * FROM globalList")
    List<GlobalList> getAll();

    @Query("SELECT * FROM globalList WHERE name LIKE :name LIMIT 1")
    GlobalList findByName(String name);

    @Query("SELECT * FROM globalList WHERE id LIKE :id LIMIT 1")
    GlobalList findById(int id);

    @Query("DELETE FROM globalList WHERE id = :id")
    void deleteById(int id);

    @Query("UPDATE globalList SET name = :name WHERE id =:id")
    void updateItem(String name, int id);

    @Insert(onConflict = REPLACE)
    void insert(GlobalList globalList);
}
