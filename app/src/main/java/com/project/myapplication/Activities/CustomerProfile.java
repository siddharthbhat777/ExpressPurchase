package com.project.myapplication.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerProfile extends AppCompatActivity {

    ActivityCustomerProfileBinding binding;
    ExecutorService service = Executors.newSingleThreadExecutor(); // for doing work in background
    // BACKGROUND THREAD MEANS :- like when u download any file on chrome than if u exist that app and open another app than the download will not pause because that is doing worki n background!
    GoogleSignInAccount acct ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
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



                    FirebaseFirestore.getInstance().collection("User").document(personEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                String address = value.getString("address");
                                binding.editTextTextPersonName.setText(address);
                            }
                        }
                    });

                binding.button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("address", binding.editTextTextPersonName.getText().toString());

                        FirebaseFirestore.getInstance().collection("User").document(personEmail).set(map).addOnCompleteListener(CustomerProfile.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CustomerProfile.this, "Updated!", Toast.LENGTH_SHORT).show();
                                    binding.editTextTextPersonName.setClickable(false);
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
