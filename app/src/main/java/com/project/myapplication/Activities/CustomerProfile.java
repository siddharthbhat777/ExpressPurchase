package com.project.myapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.myapplication.databinding.ActivityCustomerProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CustomerProfile extends AppCompatActivity {

    ActivityCustomerProfileBinding binding;
    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setdata();

    }

    private void setdata() {
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personPhoto = acct.getPhotoUrl().toString();

            Picasso.get().load(personPhoto).into(binding.imageView6);
            binding.textView4.setText(personName);
            binding.textView7.setText(personEmail);

            binding.editTextTextPersonName.setFocusable(false);
            binding.button4.setVisibility(View.GONE);

            binding.editTextTextPersonName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.button4.setVisibility(View.VISIBLE);
                }
            });

            FirebaseFirestore.getInstance().collection("User").document(personEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    String address = value.getString("address");
                    binding.editTextTextPersonName.setText(address);
                }
            });

            binding.button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("address", binding.editTextTextPersonName.getText().toString());

                    FirebaseFirestore.getInstance().collection("User").document(personEmail).set(map).addOnCompleteListener(getParent(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                binding.editTextTextPersonName.setFocusable(false);
                                binding.button4.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed ! Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        }
    }
}