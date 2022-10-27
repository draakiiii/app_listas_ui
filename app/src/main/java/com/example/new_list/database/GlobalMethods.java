package com.example.new_list.database;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.room.Room;

import com.example.new_list.model.GlobalList;

import java.util.List;


public class GlobalMethods {
    @SuppressLint("StaticFieldLeak")
    private static GlobalMethods db;
    private GlobalListDao globalListDao;


    public GlobalMethods(Context context) {
        Context appContext = context.getApplicationContext();
        DatabaseGlobalList database = Room.databaseBuilder(appContext, DatabaseGlobalList.class, "globalList")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        globalListDao = database.globalListDao();
    }

    public GlobalMethods get(Context context) {
        if (db == null) {
            db = new GlobalMethods(context);
        }
        return db;
    }

    public List<GlobalList> getItems() {
        return globalListDao.getAll();
    }

    public GlobalList globalList(int id) {
        return globalListDao.findById(id);
    }

    public void addItem(GlobalList globalList) {
        globalListDao.insert(globalList);
    }

    public void updateItem(GlobalList globalList) {
        globalListDao.updateItem(globalList.getName(), globalList.getId());
    }

    public void deleteItem(int id) {
        globalListDao.deleteById(id);
    }
}
