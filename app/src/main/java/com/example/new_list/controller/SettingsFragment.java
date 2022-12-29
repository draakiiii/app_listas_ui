package com.example.new_list.controller;

import static android.app.Activity.RESULT_OK;
import static com.example.new_list.MainActivity.navigationView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.new_list.MainActivity;
import com.example.new_list.R;
import com.example.new_list.database.CategoryMethods;
import com.example.new_list.database.GlobalListDao;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.model.Category;
import com.example.new_list.model.GlobalList;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

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
                    showToastError(e);
                }
            }
        });

        TextView deleteAllCategories = view.findViewById(R.id.btn_delete_categories);
        deleteAllCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (categoryMethods.getAllCategories().size() > 0) showSpinnerCategories();
                    else Toast.makeText(getContext(),R.string.no_category ,Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    showToastError(e);
                }
            }
        });

        TextView importExport = view.findViewById(R.id.btn_import_export_lists);
        importExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showDialogExportImport();
                } catch (Exception e) {
                    showToastError(e);
                }
            }
        });

        return view;
    }

    public void showDialogExportImport() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this.getActivity(), R.style.CustomMaterialDialog).setTitle(R.string.dialog_title_close_filter);
        builder.setTitle(R.string.import_export_lists);
        builder.setMessage(R.string.dialog_message_close_filter);
        builder.setPositiveButton(R.string.import_lists, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.export_lists, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    exportFile();
                } catch (Exception e) {
                    showToastError(e);
                }

            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void showSpinnerCategories() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.spinner_categories, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()).setTitle(R.string.add_category);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        Spinner categorySpinner = promptView.findViewById(R.id.categorySpinnerDelete);

        List<Category> categories = new ArrayList<>();
        categories.addAll(categoryMethods.getAllCategories());
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, categories);

        // Crear un ArrayAdapter con la lista de categorías
        adapterSpinner = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, categories);
        categorySpinner.setAdapter(adapterSpinner);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Button buttonConfirm = promptView.findViewById(R.id.buttonDeleteCategory);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categorySpinner.getSelectedItemId() != -1) {
                    categoryMethods.delete((Category) categorySpinner.getSelectedItem());
                    categories.remove(categorySpinner.getSelectedItemId());
                    alert.dismiss();
                } else Toast.makeText(getContext(),R.string.errorIntroduceName ,Toast.LENGTH_SHORT).show();

            }
        });

        final Button buttonCancel = (Button) promptView.findViewById(R.id.buttonCancelCategory);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }

    public void exportFile() {
        // Crear un array con las opciones de formato de archivo
        final String[] fileFormats = {"TXT", "JSON", "CSV"};

        // Mostrar un diálogo de selección de archivo
        new AlertDialog.Builder(getContext())
                .setTitle("Seleccionar formato de archivo")
                .setItems(fileFormats, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // Obtener el formato de archivo seleccionado
                            String fileFormat = fileFormats[which];

                            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                            File exportFile = new File(downloadsDir, "globalLists." + fileFormat.toLowerCase());
                            exportFile.createNewFile();

                            FileOutputStream fos = new FileOutputStream(exportFile);

                            // Crear un BufferedWriter a partir del FileOutputStream
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

                            // Ejecutar una consulta para obtener todos los objetos de la base de datos
                            List<GlobalList> globalLists = database.getItems();

                            // Iterar sobre la lista de objetos y escribir cada uno en el fichero según el formato seleccionado
                            switch (fileFormat) {
                                case "TXT":
                                    for (GlobalList globalList : globalLists) {
                                        bw.write(globalList.toString());
                                        bw.newLine();
                                    }
                                    break;
                                case "JSON":
                                    Gson gson = new Gson();
                                    for (GlobalList globalList : globalLists) {
                                        bw.write(gson.toJson(globalList));
                                        bw.newLine();
                                    }
                                    break;
                                case "CSV":
                                    // Escribir los títulos de las columnas en la primera línea
                                    bw.write("ID,Nombre,Listas");
                                    bw.newLine();

                                    // Escribir los datos de cada objeto en una línea separada
                                    for (GlobalList globalList : globalLists) {
                                        bw.write(globalList.getId() + "," + globalList.getName() + "," + globalList.getLists());
                                        bw.newLine();
                                    }
                                    break;
                            }

                            // Cerrar el BufferedWriter y el FileOutputStream
                            bw.close();
                            fos.close();
                            Toast.makeText(getContext(), R.string.file_save_download,Toast.LENGTH_SHORT).show();
                        }catch (Exception e) {
                            showToastError(e);
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

//    public void importFile() {
//        // Mostrar un diálogo de selección de archivo
//        new AlertDialog.Builder(getContext())
//                .setTitle("Seleccionar archivo a importar")
//                .setPositiveButton("Seleccionar", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Obtener el archivo seleccionado
//                        // (en este ejemplo asumimos que el usuario ha seleccionado un archivo válido)
//                        File importFile = getSelectedFile();
//
//                        try {
//                            // Abrir el archivo y leer su contenido
//                            FileInputStream fis = new FileInputStream(importFile);
//                            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//
//                            // Array para almacenar los objetos importados
//                            List<GlobalList> globalLists = new ArrayList<>();
//
//
//
//                            // Llamar a getSelectedFile pasando la instancia de ActivityResultLauncher como argumento
//                            getSelectedFile();
//
//                            // Determine el formato del archivo a partir de su extensión
//                            String fileFormat = getFileFormat(importFile);
//                            switch (fileFormat) {
//                                case "TXT":
//                                    // Procesar el contenido del archivo como si fuera una lista de objetos en formato TXT
//                                    String line;
//                                    while ((line = br.readLine()) != null) {
//                                        // Parsear cada línea a un objeto y añadirlo a la lista
//                                        GlobalList globalList = parseTxtLine(line);
//                                        globalLists.add(globalList);
//                                    }
//                                    break;
//                                case "JSON":
//                                    // Procesar el contenido del archivo como si fuera una lista de objetos en formato JSON
//                                    Gson gson = new Gson();
//                                    String json;
//                                    while ((json = br.readLine()) != null) {
//                                        // Parsear cada línea a un objeto y añadirlo a la lista
//                                        GlobalList globalList = gson.fromJson(json, GlobalList.class);
//                                        globalLists.add(globalList);
//                                    }
//                                    break;
//                                case "CSV":
//                                    // Procesar el contenido del archivo como si fuera una lista de objetos en formato CSV
//                                    // Saltarse la primera línea (títulos de las columnas)
//                                    br.readLine();
//
//                                    String csvLine;
//                                    while ((csvLine = br.readLine()) != null) {
//                                        // Parsear cada línea a un objeto y añadirlo a la lista
//                                        GlobalList globalList = parseCsvLine(csvLine);
//                                        globalLists.add(globalList);
//                                    }
//                                    break;
//                            }
//
//                            // Cerrar el BufferedReader y el FileInputStream
//                            br.close();
//                            fis.close();
//
//                            // Insertar los objetos importados en la base de datos
//                            database.addItems(globalLists);
//
//                            // Mostrar un mensaje de éxito
//                            Toast.makeText(getContext(), "Datos importados con éxito", Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                            showToastError(e);
//                        }
//                    }
//                })
//                .setNegativeButton("Cancelar", null)
//                .show();
//    }

    // Declara una constante para el código de solicitud del Intent
    private static final int PICK_FILE_REQUEST_CODE = 1;

    // Declara una variable para almacenar el archivo seleccionado
    private File selectedFile;

    // Este método se ejecutará cuando el usuario haga clic en el botón para seleccionar un archivo
    public void selectFile(View view) {
        // Crea el Intent para seleccionar un archivo
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Inicia la actividad para seleccionar un archivo y espera un resultado
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    public void showToastError(Exception e) {
        Toast.makeText(getActivity(),R.string.error,Toast.LENGTH_SHORT).show();
        System.out.println("ERROR: " + e.getLocalizedMessage());
    }
}
