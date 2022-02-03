package com.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CategoryClickInterface {

    EditText searchEditText;
    CardView voiceSearchCardView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ExpressPurchaseAdapter adapter;

    CategoryAdapter categoryAdapter;
    ArrayList<CategoryModel> list;


    RecyclerView recyclerViewItems;

    CardView settingsCv, aboutCv, walletCv;

    SharedPreferences sharedPreferences;

    public static DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private ActionBarDrawerToggle mDrawerToggle;

    RecyclerView rv_chip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initChips();

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

        //manual searching option
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

        //voice search option
        voiceSearchCardView = findViewById(R.id.searchCardView);
        voiceSearchCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your desired item name!");
                try {
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setup Navigation Drawer Layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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

                FirebaseRecyclerOptions<ExpressPurchaseModel> options =
                        new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("items"), ExpressPurchaseModel.class)
                                .build();
                adapter = new ExpressPurchaseAdapter(options);
                adapter.startListening();
                recyclerViewItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initChips() {

        rv_chip = findViewById(R.id.chipGroup);
        rv_chip.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        rv_chip.setHasFixedSize(true);
        list = new ArrayList<>();

        db.collection("categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();
                list.add(new CategoryModel("All"));
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    CategoryModel model = snapshot.toObject(CategoryModel.class);
                    list.add(model);
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });


        categoryAdapter = new CategoryAdapter(this, list, this);
        rv_chip.setAdapter(categoryAdapter);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK & null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchEditText.setText(result.get(0));
            }
        }
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

    @Override
    public void onClick(int position, CategoryModel name) {
//        Toast.makeText(getApplicationContext(), name.getCategoryName(), Toast.LENGTH_SHORT).show();
        showCategoryWiseData(name);
    }

    private void showCategoryWiseData(CategoryModel model) {

        if (model.getCategoryName().equals("All")) {

            FirebaseRecyclerOptions<ExpressPurchaseModel> options =
                    new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("items"), ExpressPurchaseModel.class)
                            .build();
            adapter = new ExpressPurchaseAdapter(options);
            adapter.startListening();
            recyclerViewItems.setAdapter(adapter);
        } else {

            FirebaseRecyclerOptions<ExpressPurchaseModel> options =
                    new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("items").orderByChild("category").equalTo(model.getCategoryName()), ExpressPurchaseModel.class)
                            .build();
            adapter = new ExpressPurchaseAdapter(options);
            adapter.startListening();
            recyclerViewItems.setAdapter(adapter);


        }
    }
}