package com.example.new_list;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Configuration;
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
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    public NavigationView navigationView;
    private GlobalMethods database;
    private Menu menu;
    private Menu menuPrivate;
    private ArrayList<GlobalList> arrayLists;
    private static AtomicInteger countButtonFolder;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        countButtonFolder = new AtomicInteger(0);
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
        for (GlobalList globalListNew : arrayLists) {
            addGlobalListToMenu(globalListNew);
            System.out.println(globalListNew);
        }
        mDrawer.close();
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
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_settings:
                setTitle(R.string.settings);
                mDrawer.close();
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.flContent, SettingsFragment.class,null)
                        .commit();
                break;
            case R.id.nav_theme:
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
                mDrawer.close();
                break;
            case R.id.nav_add:
                addGlobalListDialog();
                break;
            case R.id.nav_main:
                openMainView();
                break;
            case R.id.nav_create_folder:
                System.out.println("NO DISPONIBLE");
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
        deleteGlobalListMenu(globalList);
        setTitle(globalList.getName());
        Bundle bundle = new Bundle();
        bundle.putInt("globallist", globalList.getId());
        mDrawer.close();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.flContent, PrivateListFragment.class,bundle)
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
    public void deleteGlobalListMenu(GlobalList globalList) {
        toolbar.getMenu().clear();  // De esta forma no se duplica
        toolbar.getMenu().add("Remove").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteGlobalListConfirmDialog(globalList);
                return false;
            }
        });
    }

    // Método que lanza un diálogo para confirmar la eliminación de la lista global
    protected void deleteGlobalListConfirmDialog(GlobalList globalList) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.confirm_delete_global_list, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this).setTitle(R.string.title_delete_list);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        final TextView textViewDeleteList = (TextView) promptView.findViewById(R.id.tv_confirmDelete);
        textViewDeleteList.setText(R.string.confirm_delete);
        final Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmDeleteList);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.deleteItem(globalList.getId());
                navigationView.getMenu().removeItem(globalList.getId());
                openMainView();
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

        alert.show();
    }

    protected void addGlobalListDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.fragment_add_global_list, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this).setTitle(R.string.title_create_list);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_edit);
        alert.setCanceledOnTouchOutside(true);
        final EditText editTextInputGlobalTitle = (EditText) promptView.findViewById(R.id.inputTitleGlobal);

        final Button buttonConfirm = (Button) promptView.findViewById(R.id.buttonConfirmGlobal);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        final Button buttonCancel = (Button) promptView.findViewById(R.id.buttonCancelGlobal);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }

}