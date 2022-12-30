package com.example.new_list;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_list.controller.MainFragment;
import com.example.new_list.controller.PrivateListFragment;
import com.example.new_list.controller.SettingsFragment;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.helper.DataConverter;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;
import com.example.new_list.model.Section;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private static Toolbar toolbar, oldToolbar;
    public static NavigationView navigationView;
    private GlobalMethods database;
    private PrivateListFragment privateListFragment;
    private ArrayList<GlobalList> arrayLists;
    private androidx.appcompat.app.AlertDialog alert;
    private final int DELETE_DIALOG = 0, ADD_DIALOG = 1;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        privateListFragment = new PrivateListFragment();

        database = new GlobalMethods(this);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Find our drawer view
        navigationView = (NavigationView) findViewById(R.id.nvView);
        arrayLists = (ArrayList<GlobalList>) database.getItems();

        // Setup drawer view
        setupDrawerContent(navigationView);
        openMainView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Cuando se crea por primera vez el menú lateral, añade todas las listas globales
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("------- LISTAS GLOBALES --------");
        fillLeftMenu();
        return true;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch(menuItem.getItemId()) {
            case R.id.nav_settings:
                clearGlobalListMenu();
                setTitle(R.string.settings);
                mDrawer.close();
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.flContent, SettingsFragment.class,null)
                        .commit();
                break;
            case R.id.nav_add:
                addGlobalListDialog();
                break;
            case R.id.nav_main:
                clearGlobalListMenu();
                openMainView();
                break;
            case R.id.nav_create_folder:
                Toast.makeText(getBaseContext(), R.string.unavailable, Toast.LENGTH_SHORT).show();
            default:
                System.out.println("Default.");
        }

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    // Método que añade la lista global al menú lateral. También llama al método openGlobalList para abrir automáticamente dicha lista
    public void addGlobalListToMenu(GlobalList globalList) {
        navigationView.getMenu().add(Menu.NONE,globalList.getId(),Menu.NONE,globalList.getName()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    openGlobalList(globalList);
                    return true;
                } catch (Exception e) {
                    System.out.println("ERROR --> " + e.getLocalizedMessage());
                    return false;
                }
            }
        });
    }

    //Método que abre una lista global
    public void openGlobalList(GlobalList globalList) {
        optionsGlobalList(globalList);
        setTitle(globalList.getName());
        Bundle bundle = new Bundle();
        bundle.putInt("globallist", globalList.getId());
        if (mDrawer != null) mDrawer.close();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.flContent, PrivateListFragment.class, bundle)
                .commit();
    }

    // Método que añade una lista global al array
    public void addGlobalListToArray(GlobalList globalList) {
        addGlobalListToMenu(globalList);
        arrayLists.add(globalList);
        openGlobalList(globalList);
    }

    // Método que abre la pantalla principal
    public void openMainView() {
        setTitle(R.string.app_name);
        mDrawer.close();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.flContent, MainFragment.class,null)
                .commit();
    }

    // Método que añade a la toolbar el botón de Remove y le añade el onClick
    public void optionsGlobalList(GlobalList globalList) {
        toolbar.getMenu().clear();  // De esta forma no se duplica
        toolbar.getMenu().add(R.string.delete_list).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteGlobalListConfirmDialog(globalList);
                return false;
            }
        });
        toolbar.getMenu().add(R.string.rename_list).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                renameGlobalListDialog(globalList);
                return false;
            }
        });
        toolbar.getMenu().add(R.string.show_stats).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showStats(globalList);
                return false;
            }
        });

    }

    public void clearGlobalListMenu() {
        toolbar.getMenu().clear();
    }

    // Método que lanza un diálogo para confirmar la eliminación de la lista global
    protected void deleteGlobalListConfirmDialog(GlobalList globalList) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.confirm_delete_global_list, null);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialDialog).setTitle(R.string.title_delete_list);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);

        final TextView textViewDeleteList = (TextView) promptView.findViewById(R.id.tv_confirmDelete);
        textViewDeleteList.setText(R.string.confirm_delete);
        final Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmDeleteList);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.deleteItem(globalList.getId());
                navigationView.getMenu().removeItem(globalList.getId());
                openMainView();
                clearGlobalListMenu();
                alert.dismiss();
                Toast.makeText(getApplicationContext(),R.string.list_deleted,Toast.LENGTH_LONG).show();

            }
        });

        final Button buttonCancel = (Button) promptView.findViewById(R.id.buttonCancelDeleteList);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    // Método que permite renombrar listas
    protected void renameGlobalListDialog(GlobalList globalList) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.fragment_add_global_list, null);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialDialog).setTitle(R.string.rename_list);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);

        final EditText editTextInputGlobalTitle = (EditText) promptView.findViewById(R.id.inputTitleGlobal);
        editTextInputGlobalTitle.setText(globalList.getName());

        alertDialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editTextInputGlobalTitle.getText().toString().matches("")) {
                    String newName = editTextInputGlobalTitle.getText().toString();
                    database.rename(globalList.getId(), newName);
                    globalList.setName(newName);
                    System.out.println("Global list renamed");
                    navigationView.getMenu().findItem(globalList.getId()).setTitle(newName);
                    setTitle(newName);
                    alert.dismiss();
                } else Toast.makeText(getApplicationContext(),R.string.errorIntroduceName ,Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });
        alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    protected void addGlobalListDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.fragment_add_global_list, null);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialDialog).setTitle(R.string.title_create_list);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);
        final EditText editTextInputGlobalTitle = (EditText) promptView.findViewById(R.id.inputTitleGlobal);

        alertDialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editTextInputGlobalTitle.getText().toString().matches("")) {
                    ArrayList<ArrayList> arrayList = new ArrayList<>();
                    GlobalList globalList = new GlobalList(editTextInputGlobalTitle.getText().toString(), DataConverter.fromArrayList(arrayList));
                    database.addItem(globalList);
                    System.out.println("Global list added");
                    addGlobalListToArray(globalList);
                    alert.dismiss();
                } else Toast.makeText(getApplicationContext(),R.string.errorIntroduceName ,Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });

        alert  = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    protected void showStats(GlobalList globalList) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.show_stats, null);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialDialog).setTitle(R.string.stats_title);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);

        globalList = database.findById(globalList.getId());
        final TextView stats = (TextView) promptView.findViewById(R.id.stats);
        ArrayList<Section> arrayOfArrays = DataConverter.fromStringSection(globalList.getLists());
        if (arrayOfArrays.size() != 0 && arrayOfArrays.get(0).getListOfItems().size() != 0) {
            // Crear un mapa para almacenar los contadores de categorías
            Map<String, Integer> categoryCounts = new HashMap<>();
            Map<String, Map<String, Integer>> subcategoryCounts = new HashMap<>();
            for (int i = 0; i < arrayOfArrays.size(); i++) {
                ArrayList<Item> arrayOfItemsPrivate = DataConverter.changeItemType(arrayOfArrays.get(i).getListOfItems());
                for (Item item:arrayOfItemsPrivate) {
                    String categoryName = item.getCategory().getName();

                    // Si ya se ha contado una ocurrencia de esta categoría, incrementar el contador
                    if (categoryCounts.containsKey(categoryName)) {
                        categoryCounts.put(categoryName, categoryCounts.get(categoryName) + 1);
                    }
                    // Si no, agregar una nueva entrada al mapa con un contador inicial de 1
                    else {
                        categoryCounts.put(categoryName, 1);
                    }

                    if (item.getSubcategorySelected() != null) {
                        String subcategoryName = item.getSubcategorySelected().getName();
                        Map<String, Integer> subcategoryCountsForCategory;
                        if (subcategoryCounts.containsKey(categoryName)) {
                            subcategoryCountsForCategory = subcategoryCounts.get(categoryName);
                        } else {
                            subcategoryCountsForCategory = new HashMap<>();
                            subcategoryCounts.put(categoryName, subcategoryCountsForCategory);
                        }

                        // Si ya se ha contado una ocurrencia de esta subcategoría, incrementar el contador
                        if (subcategoryCountsForCategory.containsKey(subcategoryName)) {
                            subcategoryCountsForCategory.put(subcategoryName, subcategoryCountsForCategory.get(subcategoryName) + 1);
                        }
                        // Si no, agregar una nueva entrada al mapa con un contador inicial de 1
                        else {
                            subcategoryCountsForCategory.put(subcategoryName, 1);
                        }
                    }
                }
            }

            // Imprimir los resultados
            String statsString = "";
            for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
                if (statsString != "") statsString = statsString + "\n";
                statsString = statsString + entry.getKey() + ": " + entry.getValue() + "";
                Map<String, Integer> subcategoryCountsForCategory = subcategoryCounts.get(entry.getKey());
                if (subcategoryCountsForCategory != null) {
                    for (Map.Entry<String, Integer> entrySub : subcategoryCountsForCategory.entrySet()) {
                        if (!entrySub.getKey().toString().matches(getString(R.string.add_category).toString()) && !entrySub.getKey().toString().matches(getString(R.string.general).toString())) {
                            statsString = statsString + "\n     " + entrySub.getKey() + ": " + entrySub.getValue();
                        }
                    }
                }
            }
            stats.setText(statsString);
        } else {
            stats.setText(R.string.show_stats_view);
        }

        alertDialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });
        alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    public void addMenuItem(MenuItem.OnMenuItemClickListener onMenuItemClickListener, String text) {
        toolbar.getMenu().add(text).setOnMenuItemClickListener(onMenuItemClickListener);
    }

    public void menuCross(GlobalList globalList) {
        toolbar.setTitle("view mode");
        toolbar.getMenu().clear();
        toolbar.getMenu().add("Close").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openGlobalList(globalList);
                return false;
            }
        });
    }

    public void fillLeftMenu() {
        for (GlobalList globalListNew : arrayLists) {
            addGlobalListToMenu(globalListNew);
            System.out.println(globalListNew);
        }
        mDrawer.close();
    }

    public String toolbarTitle() {
        return toolbar.getTitle().toString();
    }

}