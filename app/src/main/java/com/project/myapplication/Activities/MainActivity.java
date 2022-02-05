package com.project.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
import com.project.myapplication.Interfaces.CategoryClickInterface;
import com.project.myapplication.Model.CategoryModel;
import com.project.myapplication.Adapter.ExpressPurchaseAdapter;
import com.project.myapplication.Model.ExpressPurchaseModel;
import com.project.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "MainActivity";
    GoogleSignInOptions gso;

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
//    ExecutorService service = Executors.newSingleThreadExecutor(); // for doing work in background thread


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup signinmethod
        setupsignin();

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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
//        mDrawerLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                mDrawerToggle.syncState();
//            }
//        });

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

    private void setupsignin() {

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // this is not an error! don't worry about this one i am also seeing this warning in my project
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

        if (requestCode == 2) {
            if (resultCode == RESULT_OK & null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchEditText.setText(result.get(0));
            }
        }
        else {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken(), account.getId(), account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString());
                } catch (ApiException e) {
                    Toast.makeText(MainActivity.this, "Failed ! Something wrong happens", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onActivityResult: " + e.getLocalizedMessage());

                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String token, String idToken, String gmail, String name, String photourl) {
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            setcompletedata(token, idToken, gmail, name, photourl); // storing user data in firestore

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onActivityResult: " + task.getException().getLocalizedMessage());

                        }
                    }
                });
    }

    private void setcompletedata(String token, String idToken, String gmail, String name, String photourl) {
//        service.execute(new Runnable() {
//            @Override
//            public void run() {


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users").document(idToken).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            // if the user is already in databse means if the user had already sign n to the app and again uninstall the app but agin he is doing signin to the account
                            // no need to show gender dialog

                        } else {

                            // not in databse , new user

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("name", name); // storing user name in databse
                            map.put("email", gmail); // storing gmail in database
                            map.put("uid", idToken); // this is the unique id of every user // Note: with this uid u can access ur user complete detail
                            map.put("image", photourl); // storing photo url


                            db.collection("Users").document(idToken).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                    //show gender dialog
                                    }
                                }
                            });
                        }
                    }
                });
            }
//        });
//    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // dialog will not show because user is already signin
        }else{
            signin(); // showing dialog
        }

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

    private void signin() {
        // user is not signed in
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
}