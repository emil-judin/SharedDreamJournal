package com.judin.android.shareddreamjournal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    private TextView mUsernameText;
    private ImageView mLogOutView;

    private FirebaseUser mUser;

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
        mToolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(mToolbar);
        setupDrawerContent(mNavigationView);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawer,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);

        // setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        Fragment fragment = null;
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

        Log.e(TAG, fragment.toString());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        Log.e(TAG, fragment.toString());

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        //deal with normal menuItems
        return super.onOptionsItemSelected(item);
    }
}