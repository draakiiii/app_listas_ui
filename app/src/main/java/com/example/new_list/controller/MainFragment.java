package com.example.new_list.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.new_list.R;
import com.example.new_list.database.GlobalMethods;

public class MainFragment extends Fragment {

    private GlobalMethods database;
    private TextView tv_main;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        database = new GlobalMethods(getContext());
        int numberOfLists = database.getItems().size();
        tv_main = view.findViewById(R.id.tv_main_numLists);
        if (numberOfLists == 0) tv_main.setText("No tienes listas.");
        else if (numberOfLists == 1) tv_main.setText("Tienes 1 lista.");
        else tv_main.setText("Tienes " + database.getItems().size() + " listas.");

        return view;
    }
}
