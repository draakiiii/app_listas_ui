package com.example.new_list.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.new_list.model.GlobalList;

import java.util.List;

public class GlobalListViewModel extends AndroidViewModel {

    private final List<GlobalList> globalList;

    public GlobalListViewModel(@NonNull Application application) {
        super(application);

        globalList = DatabaseGlobalList.getInstance(getApplication()).globalListDao().getAll();
    }

    public List<GlobalList> getGlobalList() {
        return globalList;
    }
}
