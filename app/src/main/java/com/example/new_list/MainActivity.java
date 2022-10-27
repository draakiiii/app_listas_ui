package com.example.new_list;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.new_list.controller.DialogFragmentAddGlobal;
import com.example.new_list.controller.Settings;
import com.example.new_list.database.GlobalMethods;
import com.example.new_list.model.GlobalList;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    public NavigationView navigationView;
    private GlobalMethods database;
    private Menu menu;
    private ArrayList<GlobalList> arrayLists;

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
        menu = toolbar.getMenu();

        // Setup drawer view
        setupDrawerContent(navigationView);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for (int i = 0; i < arrayLists.size(); i++) {
            menu.add(0, i, Menu.NONE, arrayLists.get(i).getName());
        }
        return super.onPrepareOptionsMenu(menu);
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
                fragmentClass = Settings.class;
                break;
            case R.id.nav_add:
//                fragmentClass = DialogFragmentAddGlobal.class;
                DialogFragmentAddGlobal dialogFragmentAddGlobal = new DialogFragmentAddGlobal();
                dialogFragmentAddGlobal.show(getSupportFragmentManager(),"DialogFragmentAddGlobal");
                break;
            default:
                fragmentClass = Settings.class;
        }

//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

}