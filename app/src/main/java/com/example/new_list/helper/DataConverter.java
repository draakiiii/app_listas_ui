package com.example.new_list.helper;

import androidx.room.TypeConverter;

import com.example.new_list.model.GlobalList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataConverter implements Serializable {

    @TypeConverter
    public static ArrayList<ArrayList> fromString(String value) {
        Type listType = new TypeToken<ArrayList<ArrayList>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<ArrayList> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}