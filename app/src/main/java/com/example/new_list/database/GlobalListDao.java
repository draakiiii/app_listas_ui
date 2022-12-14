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

    @Query("DELETE FROM globallist")
    void deleteAll();

    @Query("DELETE FROM globalList WHERE id = :id")
    void deleteById(int id);

    @Query("UPDATE globalList SET listOfLists = :listOfLists WHERE id =:id")
    void updateItem(String listOfLists, int id);

    @Query("UPDATE globalList SET name = :newName WHERE id = :id")
    void rename(int id, String newName);

    @Insert(onConflict = REPLACE)
    void insert(GlobalList globalList);
}
