package com.project.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.Adapter.CartAdapter;
import com.project.myapplication.Model.CartModel;
import com.project.myapplication.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class ShoppingCartActivity extends AppCompatActivity implements PaymentResultListener {

    RecyclerView rv_cart;
    ArrayList<CartModel> list;
    CartAdapter adapter;
    GoogleSignInAccount acct;
    private FirebaseFirestore db;
    CardView proceed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        init();
    }

    public void init() {
        rv_cart = findViewById(R.id.rv_cart);
        proceed = findViewById(R.id.checkout_cart);
        db = FirebaseFirestore.getInstance();
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        setuprv();
    }

    private void setuprv() {
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


        adapter = new CartAdapter(list, this);
        rv_cart.setLayoutManager(new LinearLayoutManager(this));
        rv_cart.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private int gettotalprice(ArrayList<CartModel> mItems) {
        int total = 0;
        for (int i = 0; i < mItems.size(); i++) {
            total += Integer.parseInt(String.valueOf(mItems.get(i).getNewprice()));
        }
        showrazorpay(total);
        return total;
    }

    private void showrazorpay(int total) {
        if (total!=0) {
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Activity activity = ShoppingCartActivity.this;
                    final Checkout co = new Checkout();
                    try {
                        JSONObject options = new JSONObject();
                        options.put("name", "Express Purchase");
                        options.put("description", "Payment for the Selected Item");
                        //You can omit the image option to fetch the image from dashboard
                        options.put("currency", "INR");
                        // amount is in paise so please multiple it by 100
                        //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
                        options.put("amount", total * 100);
                        JSONObject preFill = new JSONObject();
                        // put mobile number
                        options.put("prefill.contact", "9113618974");

                        // put email
                        options.put("prefill.email", "express@purchase.com");

                        options.put("prefill", preFill);

                        co.setKeyID("rzp_test_niDBTfGnyFEjJS");
                        co.open(activity, options);


                    } catch (Exception e) {
                        Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Random r = new Random();
        int i1 = r.nextInt(10000000 - 10000) + 10000;
        Intent intent = new Intent(getApplicationContext(), CartSuccessful.class);
        intent.putExtra("date/time", System.currentTimeMillis());
        intent.putExtra("invoice_number", String.valueOf(i1));
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

    }
}