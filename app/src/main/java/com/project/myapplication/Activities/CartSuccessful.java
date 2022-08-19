package com.project.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.project.myapplication.Model.ViewOrderModel;
import com.project.myapplication.databinding.ActivityCartSuccessfulBinding;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartSuccessful extends AppCompatActivity {

    CartItemAdapter adapter;
    ArrayList<CartModel> list;
    ArrayList<CartModel> newlist;
    ActivityCartSuccessfulBinding binding;
    ExecutorService service = Executors.newSingleThreadExecutor();
    GoogleSignInAccount acct;
    String address;

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
        newlist = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Cart")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            list.clear();
                            newlist.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                CartModel model = snapshot.toObject(CartModel.class);
                                list.add(model);
                            }
                            gettotalprice(list);
                            setdata(list);

                        } else {
                            Toast.makeText(getApplicationContext(), "Nothing here", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }

                });

        adapter = new CartItemAdapter(list, this);
        newlist = list;
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView2.setAdapter(adapter);
    }

    private void setdata(ArrayList<CartModel> list) {
        for (CartModel models : list) {

            Random r = new Random();
            int i1 = r.nextInt(10000000 - 10000) + 10000;
            String invoice_number = String.valueOf(i1);

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {
                        address = value.getString("address");

                    }
                }
            });
            ViewOrderModel model = new ViewOrderModel(invoice_number, String.valueOf(models.getQuantity()), String.valueOf(models.getNewprice()), System.currentTimeMillis(), models.getItemName(), address);


            FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Orders").document(invoice_number).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putInt("invoice", Integer.parseInt(invoice_number));
                    myEdit.commit();

                }
            });
            break;

        }

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
        for (CartModel model : newlist) {
            deleteDatabaseItems(model.getItemName());
        }
        super.onStop();
    }

    private void deleteDatabaseItems(String invoice) {
        FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Cart").document(invoice).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                finish();
            }
        });

    }
}