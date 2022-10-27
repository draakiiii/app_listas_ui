package com.example.new_list.database;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.room.Room;

import com.example.new_list.model.Item;

import java.util.List;


public class IndividualMethods {
    @SuppressLint("StaticFieldLeak")
    private static IndividualMethods db;
    private ItemDao mItemDao;

    IndividualMethods(Context context) {
        Context appContext = context.getApplicationContext();
        DatabaseIndividualList database = Room.databaseBuilder(appContext, DatabaseIndividualList.class, "individualList")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        mItemDao = database.itemDao();
    }

    public IndividualMethods get(Context context) {
        if (db == null) {
            db = new IndividualMethods(context);
        }
        return db;
    }

    public List<Item> getItems() {
        return mItemDao.getAll();
    }

    public Item getItem(int id) {
        return mItemDao.findById(id);
    }

    public void addItem(Item item) {
        mItemDao.insert(item);
    }

    public void updateItem(Item i) {
        mItemDao.updateItem(i.getTitle(), i.getDescription(), i.getId());
    }

    public void deleteItem(int id) {
        mItemDao.deleteById(id);
    }
}
