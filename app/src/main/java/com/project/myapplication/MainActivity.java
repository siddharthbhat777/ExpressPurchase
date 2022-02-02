package com.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText searchEditText;
    ArrayList<ExpressPurchaseModel> searchArrayList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ExpressPurchaseAdapter adapter;

    RecyclerView recyclerViewItems;

    CardView settingsCv, aboutCv, walletCv;

    SharedPreferences sharedPreferences;

    public static DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private ActionBarDrawerToggle mDrawerToggle;

    ChipGroup chipGroup;
    //list for categorry

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //creating method for better understanding !
        initchipgroup();


        //Status Bar Color
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.purple));

        //Night Mode
        sharedPreferences = getSharedPreferences("night", 0);
        boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);
        if (booleanValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        findViewById(R.id.main_bg_img).setVisibility(View.INVISIBLE);

        // Setup Actionbar / Toolbar
        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_drawer);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_drawer);
        //getSupportActionBar().setLogo(R.drawable.action_bar_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //searching option
        searchArrayList = new ArrayList<>();
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    search(s.toString());
                } else {
                    search("");
                }
            }
        });

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
        aboutCv = findViewById(R.id.aboutCardView);
        walletCv = findViewById(R.id.walletCardView);

        settingsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        aboutCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, About.class);
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

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                findViewById(R.id.main_bg_img).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void search(String s) {
        s = s.toLowerCase();
        FirebaseRecyclerOptions<ExpressPurchaseModel> options =
                new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("items").orderByChild("search").startAt(s).endAt(s + "\uf8ff"), ExpressPurchaseModel.class)
                        .build();
        adapter = new ExpressPurchaseAdapter(options);
        adapter.startListening();
        recyclerViewItems.setAdapter(adapter);
    }

    private void initchipgroup() {
        chipGroup = findViewById(R.id.chipGroup);

        getData();
        //creatingitemfromlist();

    }

    private void getData() {
        db.collection("categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot snapshot : value.getDocuments()){
                    String categoryName = snapshot.getString("categoryName");
                    creatingitemfromlist(categoryName);
                }
            }
        });
    }

    private void creatingitemfromlist(String categoryName) {

            Chip lChip = new Chip(this);
            lChip.setText(categoryName);
            lChip.setTextColor(getResources().getColor(R.color.white));
            lChip.setChipBackgroundColor(getResources().getColorStateList(R.color.black));

            chipGroup.addView(lChip, chipGroup.getChildCount());
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}