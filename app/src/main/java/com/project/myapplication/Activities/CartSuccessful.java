package com.project.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.Adapter.CartItemAdapter;
import com.project.myapplication.Model.CartModel;
import com.project.myapplication.databinding.ActivityCartSuccessfulBinding;

import java.util.ArrayList;

public class CartSuccessful extends AppCompatActivity {

    CartItemAdapter adapter;
    ArrayList<CartModel> list;
    ActivityCartSuccessfulBinding binding;
    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartSuccessfulBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setuprecyclerview();
        onclick();

    }

    private void setuprecyclerview() {
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Cart")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            list.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                CartModel model = snapshot.toObject(CartModel.class);
                                list.add(model);
                            }
                            gettotalprice(list);

                        } else {
                            Toast.makeText(getApplicationContext(), "Nothing here", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }

                });

        adapter = new CartItemAdapter(list, this);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView2.setAdapter(adapter);
    }

    private int gettotalprice(ArrayList<CartModel> mItems) {
        int total = 0;
        for (int i = 0; i < mItems.size(); i++) {
            total += Integer.parseInt(String.valueOf(mItems.get(i).getNewprice()));
        }
        binding.textView45.setText("Total : Rs " + total);
        return total;
    }

    private void onclick() {


        binding.button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), ViewOrders.class));
                finish();
            }
        });
        binding.button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        //delete from cart
        FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Cart").document().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
        super.onStop();
    }
}