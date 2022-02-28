package com.project.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.databinding.ActivityPreferencesBinding;

import java.util.HashMap;

public class Preferences extends AppCompatActivity {
    ActivityPreferencesBinding binding;
    GoogleSignInAccount account;
    String gender, pref = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setup();

    }

    private void setup() {
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        //GENERAL
        binding.generalRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.preferencesRadio.setChecked(false);
                binding.male.setChecked(false);
                binding.female.setChecked(false);
                binding.a1.setChecked(false);
                binding.a2.setChecked(false);
                binding.a3.setChecked(false);
                binding.a4.setChecked(false);
                binding.a5.setChecked(false);
                binding.a6.setChecked(false);

                binding.button6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("preference", "general");

                        FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Preferences").document(account.getId()).set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                });
                    }
                });

            }
        });

        //CUSTOM
        binding.preferencesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.male.setClickable(true);
                binding.female.setClickable(true);
                binding.a1.setClickable(true);
                binding.a2.setClickable(true);
                binding.a3.setClickable(true);
                binding.a4.setClickable(true);
                binding.a5.setClickable(true);
                binding.generalRadio.setChecked(false);
                binding.male.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender = "m";
                        binding.female.setChecked(false);
                    }
                });
                binding.female.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender = "f";
                        binding.male.setChecked(false);

                    }
                });
                binding.a1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = "a";
                        binding.a2.setChecked(false);
                        binding.a3.setChecked(false);
                        binding.a4.setChecked(false);
                        binding.a5.setChecked(false);
                        binding.a6.setChecked(false);

                    }
                });

                binding.a2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = "b";
                        binding.a1.setChecked(false);
                        binding.a3.setChecked(false);
                        binding.a4.setChecked(false);
                        binding.a5.setChecked(false);
                        binding.a6.setChecked(false);

                    }
                });

                binding.a3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = "c";
                        binding.a2.setChecked(false);
                        binding.a1.setChecked(false);
                        binding.a4.setChecked(false);
                        binding.a5.setChecked(false);
                        binding.a6.setChecked(false);

                    }
                });

                binding.a4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = "d";
                        binding.a2.setChecked(false);
                        binding.a3.setChecked(false);
                        binding.a1.setChecked(false);
                        binding.a5.setChecked(false);
                        binding.a6.setChecked(false);

                    }
                });

                binding.a5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = "e";
                        binding.a2.setChecked(false);
                        binding.a3.setChecked(false);
                        binding.a4.setChecked(false);
                        binding.a1.setChecked(false);
                        binding.a6.setChecked(false);

                    }
                });

                binding.a6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = "f";
                        binding.a2.setChecked(false);
                        binding.a3.setChecked(false);
                        binding.a4.setChecked(false);
                        binding.a5.setChecked(false);
                        binding.a1.setChecked(false);

                    }
                });

                binding.button6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pref.equals("null")) {
                            Toast.makeText(getApplicationContext(), "Select Your pref", Toast.LENGTH_SHORT).show();
                        } else if (gender.equals("null")) {
                            Toast.makeText(getApplicationContext(), "Select Your Gender", Toast.LENGTH_SHORT).show();
                        } else {

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("preference", gender + pref);
                            FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Preferences").document(account.getId()).set(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        }
                                    });


                        }
                    }
                });


            }
        });
        binding.male.setClickable(false);
        binding.female.setClickable(false);
        binding.a1.setClickable(false);
        binding.a2.setClickable(false);
        binding.a3.setClickable(false);
        binding.a4.setClickable(false);
        binding.a5.setClickable(false);
    }
}