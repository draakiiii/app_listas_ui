package com.example.new_list.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.new_list.helper.DataConverter;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;

@Database(entities = {GlobalList.class}, version = 3)
@TypeConverters(DataConverter.class)
public abstract class DatabaseGlobalList extends RoomDatabase {

    private static DatabaseGlobalList INSTANCE;

    public static DatabaseGlobalList getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),DatabaseGlobalList.class,"globalList")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract GlobalListDao globalListDao();
}
