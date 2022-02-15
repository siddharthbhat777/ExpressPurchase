package com.project.myapplication.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.myapplication.databinding.ActivityPaymentOptionsBinding;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class PaymentOptions extends AppCompatActivity {
    ActivityPaymentOptionsBinding binding;
    int wallet_amounts, item_price;
    String name, salesman, desc, image, price;

    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getdatafromintent();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getdatafromintent() {
        name = getIntent().getStringExtra("item_name");
        salesman = getIntent().getStringExtra("item_salesman_name");
        desc = getIntent().getStringExtra("item_desc");
        image = getIntent().getStringExtra("item_image");
        price = getIntent().getStringExtra("item_price");

        setdata();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setdata() {

        Picasso.get().load(image).into(binding.imageView10);
        binding.textView22.setText(name);
        binding.textView32.setText(price);
        item_price = Integer.parseInt(price);
        checkifeligibleforwallet();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkifeligibleforwallet() {
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personEmail = acct.getEmail();
            FirebaseFirestore.getInstance().collection("User").document(personEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {
                        String amountinstring = value.getString("amounts");
                        wallet_amounts = Integer.parseInt(amountinstring);


//                        Toast.makeText(getApplicationContext(), wallet_amounts, Toast.LENGTH_SHORT).show();

                        if (item_price > 10000) {
                            //invisible
                            binding.checkBox.setVisibility(View.GONE);
                            binding.textView23.setVisibility(View.GONE);
                            binding.textView27.setVisibility(View.GONE);
                            binding.textView28.setVisibility(View.GONE);
                            binding.textView18.setVisibility(View.GONE);
                            binding.button7.setVisibility(View.GONE);

                            //visible
                            binding.radioButton2.setVisibility(View.VISIBLE);
                            binding.imageView8.setVisibility(View.VISIBLE);
                            binding.materialCardView3.setVisibility(View.VISIBLE);


                            showrazorpay();
                        } else if (item_price <= 10000 && wallet_amounts < item_price) {
                            //invisible
                            binding.textView28.setVisibility(View.GONE);
                            binding.radioButton2.setVisibility(View.GONE);
                            binding.imageView8.setVisibility(View.GONE);
                            binding.textView18.setVisibility(View.GONE);
                            binding.materialCardView3.setVisibility(View.GONE);

                            //visible
                            binding.materialCardView2.setVisibility(View.VISIBLE);
                            binding.checkBox.setVisibility(View.VISIBLE);
                            binding.checkBox.setText("Insufficient Money in Wallet!");
                            binding.checkBox.setTextColor(Color.RED);
                            binding.textView27.setVisibility(View.VISIBLE);
                            binding.textView23.setVisibility(View.VISIBLE);
                            binding.textView29.setText("Insufficient money need more money ! plzz add the money by clicking on below buttons");
                            addmoney();

                        } else if (item_price <= 10000 && wallet_amounts > item_price) {     //invisible
                            binding.textView28.setVisibility(View.GONE);
                            binding.radioButton2.setVisibility(View.GONE);
                            binding.imageView8.setVisibility(View.GONE);
                            binding.textView18.setVisibility(View.GONE);
                            binding.materialCardView2.setVisibility(View.GONE);

                            //visible
                            binding.materialCardView3.setVisibility(View.VISIBLE);
                            binding.checkBox.setVisibility(View.VISIBLE);
                            binding.checkBox.setText("Amount Availble in Wallet ");
                            binding.textView29.setText("CLICK ON BELOW BUTTON TO PAY USING YOUR APP WALLET MONEY.HAVE A CHANCE TO WIN CASHBACK(:");

                            payfromwallet();
                        }

                    }
                }
            });
        }
    }

    private void addmoney() {
        binding.button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WalletActivity.class));
            }
        });
    }

    private void payfromwallet() {
        binding.button8.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Random random = new Random();
                random.ints(10000000, 1000000000);
                String random_num = random.toString();
                Intent intent = new Intent(getApplicationContext(), PaymentSuccessful.class);
                intent.putExtra("item_name", name);
                intent.putExtra("item_desc", desc);
                intent.putExtra("item_price", String.valueOf(price));
                intent.putExtra("item_salesman_name", salesman);
                intent.putExtra("item_image", image);
                intent.putExtra("date/time", System.currentTimeMillis());
                intent.putExtra("invoice_number", random_num);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showrazorpay() {
        binding.button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Random random = new Random();
                random.ints(10000000, 1000000000);
                String random_num = random.toString();
                Intent intent = new Intent(getApplicationContext(), PaymentSuccessful.class);
                intent.putExtra("item_name", name);
                intent.putExtra("item_desc", desc);
                intent.putExtra("item_price", String.valueOf(price));
                intent.putExtra("item_salesman_name", salesman);
                intent.putExtra("item_image", image);
                intent.putExtra("date/time", System.currentTimeMillis());
                intent.putExtra("invoice_number", random_num);
                startActivity(intent);
            }
        });
    }
}