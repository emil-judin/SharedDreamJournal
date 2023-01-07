package com.judin.android.shareddreamjournal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.fragments.DreamFavoritesFragment;
import com.judin.android.shareddreamjournal.fragments.DreamListFragment;

public class MainActivity extends SingleFragmentActivity {
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected Fragment createFragment() {
        return DreamListFragment.newInstance();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        setSupportActionBar(toolbar);
        setupDrawerContent(navigationView);

        drawerToggle = new ActionBarDrawerToggle(this,
                drawer,
                R.string.drawer_open,
                R.string.drawer_close);
        drawer.addDrawerListener(drawerToggle);

        // setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem){
        Fragment fragment;
        Class fragmentClass;

        if(menuItem.getItemId() == R.id.nav_home_fragment){
            fragmentClass = DreamListFragment.class;
        } else if(menuItem.getItemId() == R.id.nav_favorites_fragment){
            fragmentClass = DreamFavoritesFragment.class;
        } else {
            fragmentClass = DreamListFragment.class;
        }

        try{
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        //deal with normal menuItems
        return super.onOptionsItemSelected(item);
    }
}