package com.example.new_list.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.new_list.R;
import com.example.new_list.database.DatabaseGlobalList;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.model.GlobalList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddGlobalList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGlobalList extends Fragment {

    private GlobalMethods databaseGlobal;
    private EditText inputTitle;
    private EditText inputDescription;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddGlobalList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGlobalList newInstance(String param1, String param2) {
        AddGlobalList fragment = new AddGlobalList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_global_list, container, false);
        databaseGlobal = new GlobalMethods(getContext());
        inputTitle = view.findViewById(R.id.inputTitleGlobal);
        inputDescription = view.findViewById(R.id.inputDescriptionGlobal);
        return inflater.inflate(R.layout.fragment_add_global_list, container, false);
    }

    public void addGlobalList() {
        if (!inputTitle.getText().equals("")) {
//            databaseGlobal.addItem(new GlobalList(inputTitle.getText().toString(),));
        }
    }
}