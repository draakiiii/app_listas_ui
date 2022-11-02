package com.example.new_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.new_list.controller.DialogFragmentAddGlobal;
import com.example.new_list.controller.MainFragment;
import com.example.new_list.controller.PrivateListFragment;
import com.example.new_list.controller.Settings;
import com.example.new_list.controller.SettingsFragment;
import com.example.new_list.database.GlobalListDao;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.database.ItemAdapter;
import com.example.new_list.helper.DataConverter;
import com.example.new_list.model.GlobalList;
import com.example.new_list.model.Item;
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Set;
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
//                fragmentClass = DialogFragmentAddGlobal.class;
                DialogFragmentAddGlobal dialogFragmentAddGlobal = new DialogFragmentAddGlobal();
                dialogFragmentAddGlobal.show(getSupportFragmentManager(),"DialogFragmentAddGlobal");
                break;
            case R.id.nav_main:
                openMainView();
                break;
            case R.id.nav_create_folder:
                System.out.println("NO DISPONIBLE");
                addFolderToMenu();
//                database.deleteAll();
            default:
                Toast.makeText(this,"JAJA",Toast.LENGTH_LONG);
        }

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    public void addFolderToMenu() {
    }

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

    public void openGlobalList(GlobalList globalList) {
        toolbar.getMenu().clear();
        toolbar.getMenu().add("Remove").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                database.deleteItem(globalList.getId());
                navigationView.getMenu().removeItem(globalList.getId());
                openMainView();
                return false;
            }
        });
        setTitle(globalList.getName());
        Bundle bundle = new Bundle();
        bundle.putInt("globallist", globalList.getId());
        mDrawer.close();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.flContent, PrivateListFragment.class,bundle)
                .commit();
    }

    public void addGlobalListToArray(GlobalList globalList) {
        addGlobalListToMenu(globalList);
        arrayLists.add(globalList);
        openGlobalList(globalList);
    }

    public void openMainView() {
        setTitle(R.string.app_name);
        mDrawer.close();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.flContent, MainFragment.class,null)
                .commit();
    }

}