package com.example.new_list.controller;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.new_list.MainActivity;
import com.example.new_list.R;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.helper.DataConverter;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class DialogFragmentAddGlobal extends DialogFragment {
    private EditText inputTitle;
    private Button button_confirm_add;
    private Button button_cancel_add;
    private GlobalMethods database;
    private MainActivity activity;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_global_list,container,false);
        super.onCreate(savedInstanceState);
        activity = (MainActivity) this.getActivity();
        button_confirm_add = view.findViewById(R.id.buttonConfirmGlobal);
        button_cancel_add = view.findViewById(R.id.buttonCancelGlobal);
        inputTitle = view.findViewById(R.id.inputTitleGlobal);
        database = new GlobalMethods(getContext());
        button_confirm_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputTitle.getText().toString().matches("")) {
                    ArrayList<ArrayList> arrayList = new ArrayList<>();
                    GlobalList globalList = new GlobalList(inputTitle.getText().toString(),DataConverter.fromArrayList(arrayList));
                    database.addItem(globalList);
                    System.out.println("Global list added");
                    activity.addGlobalListToArray(globalList);
                    closeDialog();
                }
            }
        });

        button_cancel_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });
        return view;
    }


    public void closeDialog() {
        dismiss();
    }

}
