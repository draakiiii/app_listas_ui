package com.example.new_list.controller;

import static com.example.new_list.MainActivity.navigationView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.new_list.MainActivity;
import com.example.new_list.R;
import com.example.new_list.database.CategoryMethods;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.model.Category;
import com.example.new_list.model.GlobalList;

public class SettingsFragment extends Fragment {

    private GlobalMethods database;
    private CategoryMethods categoryMethods;
    private MainActivity mainActivity;

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
        categoryMethods = new CategoryMethods(getContext());
        mainActivity = new MainActivity();

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
                    for (GlobalList globalList:database.getItems()) {
                        navigationView.getMenu().removeItem(globalList.getId());
                    }
                    database.deleteAll();
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getLocalizedMessage());
                }
            }
        });

        TextView deleteAllCategories = view.findViewById(R.id.btn_delete_categories);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getLocalizedMessage());
                }
            }
        });

        return view;
    }

//    protected void showSpinnerCategories() {
//        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//        View promptView = layoutInflater.inflate(R.layout.spinner_categories, null);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()).setTitle(R.string.add_category);
//        alertDialogBuilder.setView(promptView);
//        alertDialogBuilder.setCancelable(false);
//        AlertDialog alert = alertDialogBuilder.create();
//        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
//        alert.setCanceledOnTouchOutside(true);
//        Spinner categorySpinner = (Spinner) promptView.findViewById(R.id.categorySpinner);
//        categorySpinner.setAdapter(adapterSpinner);
//        final Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmGlobal);
//        buttonConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!editTextInputGlobalTitle.getText().toString().matches("")) {
//                    alert.dismiss();
//                } else Toast.makeText(getContext(),R.string.errorIntroduceName ,Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        final Button buttonCancel = (Button) promptView.findViewById(R.id.buttonCancelGlobal);
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alert.dismiss();
//            }
//        });
//
//        alert.show();
//    }
}
