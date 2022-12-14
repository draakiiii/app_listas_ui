package com.example.new_list.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.new_list.model.Category;

@Database(entities = {Category.class}, version = 1)
public abstract class DatabaseCategory extends RoomDatabase {
    private static DatabaseCategory INSTANCE;

    public static DatabaseCategory getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),DatabaseCategory.class,"categories")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract CategoryDAO categoryDAO();
}
