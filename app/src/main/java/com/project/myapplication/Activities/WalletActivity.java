package com.project.myapplication.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityWalletBinding;

import java.util.HashMap;

public class WalletActivity extends AppCompatActivity {
    ActivityWalletBinding binding;
    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        onclick();
        getruppee();

    }

    private void getruppee() {
        if (acct != null) {
            String personEmail = acct.getEmail();
            FirebaseFirestore.getInstance().collection("User").document(personEmail).collection("Amount").document("moneyinaccount").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.exists()) {
                        String amountinstring = value.getString("amounts");
                        binding.textView13.setText(amountinstring);
                        setruppee(amountinstring);

                    } else {
                        setvalue();
                    }
                }
            });
        }
    }

    private void setvalue() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("amounts", "1");
        FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Amount").document("moneyinaccount").set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //
            }
        });
    }

    private void setruppee(String amount) {
        binding.addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acct != null) {
                    String moreamount = binding.amountEditText.getText().toString();

                    int oldone = Integer.parseInt(amount);
                    if (!moreamount.isEmpty()) {

                        if (!(oldone >= 10000)) {
                            String personEmail = acct.getEmail();
                            int newone = Integer.parseInt(moreamount);

                            int finalone = newone + oldone;


                            if (finalone <= 10000) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("amounts", String.valueOf(finalone));
                                FirebaseFirestore.getInstance().collection("User").document(personEmail).collection("Amount").document("moneyinaccount").update(map).addOnCompleteListener(WalletActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(WalletActivity.this, "Money Added!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed ! Something Went Wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });

                            } else {

                                final Dialog dialog = new Dialog(WalletActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                                dialog.setContentView(R.layout.exceed_wallet_limit_dialog);

                                CardView ok = (CardView) dialog.findViewById(R.id.ok);


                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
//                                Toast.makeText(getApplicationContext(), "You had exceed your Wallet Limit!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                dialog.show();
                            }
                        }
                        } else {
                            binding.amountEditText.setError("Please Enter Something Here!");
                        }
                    }
                }
            });
        }

        private void onclick () {

            binding.rs500.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected(binding.rs500, binding.cv500, binding.amountEditText);
                    deselect(binding.rs1000, binding.cv1000);
                    deselect(binding.rs2000, binding.cv2000);

                }
            });

            binding.rs1000.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected(binding.rs1000, binding.cv1000, binding.amountEditText);
                    deselect(binding.rs500, binding.cv500);
                    deselect(binding.rs2000, binding.cv2000);
                }
            });

            binding.rs2000.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected(binding.rs2000, binding.cv2000, binding.amountEditText);
                    deselect(binding.rs1000, binding.cv1000);
                    deselect(binding.rs500, binding.cv500);
                }
            });
        }

        private void deselect (Button btn, MaterialCardView cv){
            cv.setCardBackgroundColor(getResources().getColor(R.color.white));
            btn.setTextColor(getResources().getColor(R.color.black));
        }

        private void selected (Button button, MaterialCardView cv, EditText editText){
            cv.setCardBackgroundColor(getResources().getColor(R.color.profile_text_color));
            button.setTextColor(getResources().getColor(R.color.white));
            String amount = button.getText().toString().substring(1);
            editText.setText(amount);
        }
    }