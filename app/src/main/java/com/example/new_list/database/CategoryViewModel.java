package com.example.new_list.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.new_list.model.Category;
import com.example.new_list.model.GlobalList;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private final List<Category> categories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);

        categories = DatabaseCategory.getInstance(getApplication()).categoryDAO().getAllCategories();
    }

    public List<Category> getCategory() {
        return categories;
    }
}