package com.example.new_list.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.new_list.model.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM item")
    List<Item> getAll();

    @Query("SELECT * FROM item WHERE id IN (:itemIds)")
    List<Item> loadAllByIds(int[] itemIds);

    @Query("SELECT * FROM item WHERE title LIKE :title LIMIT 1")
    Item findByName(String title);

    @Query("SELECT * FROM item WHERE id LIKE :id LIMIT 1")
    Item findById(int id);

    @Query("SELECT * FROM item WHERE description LIKE :description LIMIT 1")
    Item findByDescription(String description);

    @Query("DELETE FROM item WHERE id = :id")
    void deleteById(int id);

    @Query("UPDATE item SET description = :description, title= :title WHERE id =:id")
    void updateItem(String title, String description, int id);

    @Insert(onConflict = REPLACE)
    void insert(Item item);
}
