package com.example.new_list.database;

import android.content.Context;

import androidx.room.Room;

import com.example.new_list.model.Category;

import java.util.List;

public class CategoryMethods {
    private CategoryDAO categoryDAO;
    private static CategoryMethods db;

    public CategoryMethods(Context context) {
        Context appContext = context.getApplicationContext();
        DatabaseCategory database = Room.databaseBuilder(appContext, DatabaseCategory.class, "categories")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        categoryDAO = database.categoryDAO();
    }

    public CategoryMethods get(Context context) {
        if (db == null) {
            db = new CategoryMethods(context);
        }
        return db;
    }

    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return categoryDAO.getCategoryById(id);
    }

    public void insert(Category category) {
        categoryDAO.insert(category);
    }

    public void update(Category category) {
        categoryDAO.update(category);
    }

    public void delete(Category category) {
        categoryDAO.delete(category);
    }

}
