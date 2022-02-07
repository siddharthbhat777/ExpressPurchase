package com.project.myapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
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
    int amounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onclick();
        getruppee();
        setruppee();

    }

    private void getruppee() {
        if (acct != null) {
            String personEmail = acct.getEmail();
            FirebaseFirestore.getInstance().collection("User").document(personEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    String amountinstring = value.getString("amounts");
                    amounts = Math.round(Float.parseFloat(amountinstring) * 100);
                    binding.textView13.setText(amountinstring);
                }
            });
        }
    }

    private void setruppee() {
        binding.addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acct != null) {
                    String personEmail = acct.getEmail();
                    String moreamount = binding.amountEditText.getText().toString().substring(1);
                    int newamount = Math.round(Float.parseFloat(moreamount) * 100);

                    int add = newamount + amounts;

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("amounts", String.valueOf(add));

                    FirebaseFirestore.getInstance().collection("User").document(personEmail).set(map).addOnCompleteListener(WalletActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(WalletActivity.this, "Money Added!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed ! Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }
        });
    }

    private void onclick() {

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

    private void deselect(Button btn, MaterialCardView cv) {
        cv.setCardBackgroundColor(getResources().getColor(R.color.white));
        btn.setTextColor(getResources().getColor(R.color.black));
    }

    private void selected(Button button, MaterialCardView cv, EditText editText) {
        cv.setCardBackgroundColor(getResources().getColor(R.color.profile_text_color));
        button.setTextColor(getResources().getColor(R.color.white));
        String amount = button.getText().toString().substring(1);
        editText.setText(amount);
    }
}
