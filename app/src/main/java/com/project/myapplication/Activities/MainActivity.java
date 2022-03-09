package com.project.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.Adapter.CategoryAdapter;
import com.project.myapplication.Adapter.ExpressPurchaseAdapter;
import com.project.myapplication.Interfaces.CategoryClickInterface;
import com.project.myapplication.Model.CategoryModel;
import com.project.myapplication.Model.ExpressPurchaseModel;
import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CategoryClickInterface {

    private EditText searchEditText;
    private CardView voiceSearchCardView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ExpressPurchaseAdapter adapter;

    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryModel> list;

    private GoogleSignInAccount currentUserAccount;


    private RecyclerView recyclerViewItems;

    private CardView settingsCv, aboutCv, walletCv, profileCv, viewOrdersCv;

    private SharedPreferences sharedPreferences;

    public static DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private ActionBarDrawerToggle mDrawerToggle;

    private RecyclerView rv_chip;

    private ActivityMainBinding binding; // to access all the items of layout easily

//    ExecutorService service = Executors.newSingleThreadExecutor(); // for doing work in background thread


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUserAccount = GoogleSignIn.getLastSignedInAccount(this);

        initChips();

        //profile
        populateDrawerUI();

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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_drawer);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //mToolbar.setNavigationIcon(R.drawable.ic_menu_drawer);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_drawer);
        //getSupportActionBar().setLogo(R.drawable.action_bar_logo);

//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_drawer);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                    startActivityForResult(intent, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.profileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CustomerProfile.class));
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
//
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        settingsCv = findViewById(R.id.settingsCardView);
        aboutCv = findViewById(R.id.aboutCardView);
        walletCv = findViewById(R.id.walletCardView);
        profileCv = findViewById(R.id.profileCardView);
        viewOrdersCv = findViewById(R.id.viewOrdersCardView);

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

        walletCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });

        profileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerProfile.class);
                startActivity(intent);
            }
        });

        viewOrdersCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewOrders.class);
                startActivity(intent);
            }
        });

        recyclerViewItems = findViewById(R.id.itemsRV);
        recyclerViewItems.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                findViewById(R.id.main_bg_img).setVisibility(View.VISIBLE);

                setupRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void populateDrawerUI() {
        if (currentUserAccount != null) {
            String personName = currentUserAccount.getDisplayName();
            String personPhoto = currentUserAccount.getPhotoUrl().toString();

            Picasso.get().load(personPhoto).into(binding.profilePictureDrawer);
            binding.profileNameDrawer.setText(personName);

        }
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
                assert value != null;
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    CategoryModel model = snapshot.toObject(CategoryModel.class);
                    list.add(model);
                }
                categoryAdapter = new CategoryAdapter(MainActivity.this, list, MainActivity.this);
                rv_chip.setAdapter(categoryAdapter);
            }
        });
    }


    private void search(String s) {
        s = s.toLowerCase();
        FirebaseRecyclerOptions<ExpressPurchaseModel> options =
                new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("items").orderByChild("search").startAt(s).endAt(s + "\uf8ff"), ExpressPurchaseModel.class)
                        .build();
        updateOption(options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if (requestCode == 2) {
        if (resultCode == RESULT_OK & null != data) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchEditText.setText(result.get(0));
        }
        //}

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onClick(int position, CategoryModel name) {
//        Toast.makeText(getApplicationContext(), name.getCategoryName(), Toast.LENGTH_SHORT).show();
        showCategoryWiseData(name);
    }

    private void showCategoryWiseData(CategoryModel model) {

        FirebaseRecyclerOptions<ExpressPurchaseModel> options;
        if (model.getCategoryName().equals("All")) {
            options = new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("items").orderByChild("preference"), ExpressPurchaseModel.class)
                    .build();
        } else {
            options = new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("items").orderByChild("category").equalTo(model.getCategoryName()), ExpressPurchaseModel.class)
                    .build();
        }
        updateOption(options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_right_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(MainActivity.this, ShoppingCartActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    public void setupRecyclerView() {

        if (currentUserAccount == null || currentUserAccount.getEmail() == null || currentUserAccount.getId() == null) {
            Toast.makeText(this, "Error: Unable to get account detail please try again", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("User").document(currentUserAccount.getEmail()).collection("Preferences").document(currentUserAccount.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    String pref = value.getString("preference");
                    setRecyclerViewQuery(pref);
                } else {
                    setRecyclerViewQuery(null);
                }
            }
        });

    }

    private void setRecyclerViewQuery(String preferences) {

        // Declaring variable for FirebaseRecyclerOptions
        FirebaseRecyclerOptions<ExpressPurchaseModel> options;

        // Null check over preference
        if (preferences == null || preferences.equals("general")) {
            options = new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("items"), ExpressPurchaseModel.class)
                    .build();
        } else {
            options = new FirebaseRecyclerOptions.Builder<ExpressPurchaseModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("items").orderByChild("preference").equalTo(preferences), ExpressPurchaseModel.class)
                    .build();
        }

        // Setting adapter with assigned options
        setAdapter(options);
    }

    private void setAdapter(FirebaseRecyclerOptions<ExpressPurchaseModel> options) {
        adapter = new ExpressPurchaseAdapter(options);
        adapter.startListening();
        recyclerViewItems.setAdapter(adapter);
    }

    private void updateOption(FirebaseRecyclerOptions<ExpressPurchaseModel> options) {
        adapter = new ExpressPurchaseAdapter(options);
        adapter.updateOptions(options);
        adapter.startListening();
        recyclerViewItems.setAdapter(adapter);
    }
}