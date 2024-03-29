package com.project.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.myapplication.Model.ViewOrderModel;
import com.project.myapplication.databinding.ActivityPaymentSuccessfulBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Random;

public class PaymentSuccessful extends AppCompatActivity {

    String item_name, item_desc, item_price, item_salesman_name, item_image, date, invoice_number, ID,address;
    GoogleSignInAccount account;
    int total, newamountl, itemprice;

    ActivityPaymentSuccessfulBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentSuccessfulBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
    }

    private void getIntentData() {
        item_name = getIntent().getStringExtra("item_name");
        item_desc = getIntent().getStringExtra("item_desc");
        item_price = getIntent().getStringExtra("item_price");
        item_salesman_name = getIntent().getStringExtra("item_salesman_name");
        item_image = getIntent().getStringExtra("item_image");
        date = getIntent().getStringExtra("date");
        invoice_number = getIntent().getStringExtra("invoice_number");
        ID = getIntent().getStringExtra("ID");
        address = getIntent().getStringExtra("address");

        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        binding.itemNameSuccess.setText(item_name);
        binding.itemPriceSuccess.setText(item_price);
        binding.salesmanName.setText(item_salesman_name);
        Picasso.get().load(item_image).into(binding.pImg);


        ViewOrderModel model = new ViewOrderModel(invoice_number, "1", item_price, System.currentTimeMillis(), item_name,address);

        FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Orders").document(invoice_number).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Storing data into SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

                // Creating an Editor object to edit(write to the file)
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                // Storing the key and its value as the data fetched from edittext
                myEdit.putInt("invoice", Integer.parseInt(invoice_number));
                myEdit.commit();
                cutmoneyifwallet();
                onclick();

            }
        });


    }

    private void cutmoneyifwallet() {
        FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Amount").document("moneyinaccount").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    String amountinstring = value.getString("amounts");
                    total = Integer.parseInt(amountinstring);
                    itemprice = Integer.parseInt(item_price);

                    int newamounts;

                    if (!ID.equals("razorpay")) {

                        Random r = new Random();
                        int i1 = r.nextInt(80 - 65) + 65;

                        binding.textView41.setText(String.valueOf(i1));

                        newamounts = total - itemprice + i1;
                    } else {
                        newamounts = total - itemprice;
                        binding.cashbackCard.setVisibility(View.GONE);
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref2", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("amount", String.valueOf(newamounts));
                    myEdit.commit();


                }
            }
        });
    }

    @Override
    public void onStop() {
        SharedPreferences sh = getSharedPreferences("MySharedPref2", MODE_APPEND);
        String s1 = sh.getString("amount", "");

        HashMap<String, Object> map = new HashMap<>();
        map.put("amounts", s1);
        FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Amount").document("moneyinaccount").set(map).addOnCompleteListener(PaymentSuccessful.this, new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        super.onStop();

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
}