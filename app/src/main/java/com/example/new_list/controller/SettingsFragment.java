package com.example.new_list.controller;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.new_list.MainActivity;
import com.example.new_list.R;
import com.example.new_list.database.GlobalMethods;
import com.google.android.material.navigation.NavigationView;

public class SettingsFragment extends Fragment {

    private GlobalMethods database;
    private MainActivity mainActivity;
    public NavigationView navigationView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        database = new GlobalMethods(getContext());
        mainActivity = new MainActivity();
        navigationView = view.findViewById(R.id.nvView);

        TextView changeTheme = view.findViewById(R.id.btn_theme);
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nightModeFlags = getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);;
                        break;
                }
            }
        });

        TextView deleteAll = view.findViewById(R.id.btn_delete_all);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    database.deleteAll();
                    Toast.makeText(getActivity(), R.string.must_close_app, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        });

        return view;
    }
}
