package com.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ExpressPurchaseAdapter adapter;

    RecyclerView recyclerViewItems;

    CardView settingsCv;

    SharedPreferences sharedPreferences;

    public static DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Status Bar Color
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.purple));

        //Night Mode
        sharedPreferences = getSharedPreferences("night", 0);
        boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);
        if (booleanValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Setup Actionbar / Toolbar
        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_drawer);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_drawer);
        //getSupportActionBar().setLogo(R.drawable.action_bar_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup Navigation Drawer Layout
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        settingsCv = findViewById(R.id.settingsCardView);

        settingsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        recyclerViewItems = findViewById(R.id.itemsRV);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ExpressPurchaseModel> options =
                new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("items"), ExpressPurchaseModel.class)
                        .build();

        adapter = new ExpressPurchaseAdapter(options);
        recyclerViewItems.setAdapter(adapter);
        recyclerViewItems.setHasFixedSize(true);
        recyclerViewItems.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        //adapter.getSnapshots();
        //adapter.onDataChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}