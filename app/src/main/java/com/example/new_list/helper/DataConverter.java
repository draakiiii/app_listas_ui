package com.example.new_list.helper;

import androidx.room.TypeConverter;

import com.example.new_list.model.Category;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;
import com.example.new_list.model.Section;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Array;
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
        Type listType = new TypeToken<ArrayList<ArrayList>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    @TypeConverter
    public static ArrayList<Item> fromStringItem(String value) {
        Type listType = new TypeToken<ArrayList<Item>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayListItem(ArrayList<Item> list) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Item>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    @TypeConverter
    public static ArrayList<Section> fromStringSection(String value) {
        Type listType = new TypeToken<ArrayList<Section>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayListSection(ArrayList<Section> list) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Section>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    @TypeConverter
    public static ArrayList<Item> changeItemType (ArrayList<Item> list) {
        return fromStringItem(fromArrayListItem(list));
    }

    @TypeConverter
    public static ArrayList<Section> changeSectionType (ArrayList<Section> list) {
        return fromStringSection(fromArrayListSection(list));
    }

    @TypeConverter
    public static ArrayList<Category> fromStringCategories(String value) {
        Type listType = new TypeToken<ArrayList<Category>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayListCategories(ArrayList<Category> categories) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Category>>() {}.getType();
        String json = gson.toJson(categories, listType);
        return json;
    }

    @TypeConverter
    public static Category fromStringCategory(String value) {
        Type listType = new TypeToken<Category>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromCategoryToString(Category category) {
        Gson gson = new Gson();
        Type listType = new TypeToken<Category>() {}.getType();
        String json = gson.toJson(category, listType);
        return json;
    }

}