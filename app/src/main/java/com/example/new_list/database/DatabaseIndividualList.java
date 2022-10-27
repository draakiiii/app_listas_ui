package com.example.new_list.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.new_list.model.Item;

@Database(entities = {Item.class}, version = 1)
public abstract class DatabaseIndividualList extends RoomDatabase {
    public abstract ItemDao itemDao();
}
