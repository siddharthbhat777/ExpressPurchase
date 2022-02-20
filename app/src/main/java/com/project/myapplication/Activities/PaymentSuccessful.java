package com.project.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Random;

public class PaymentSuccessful extends AppCompatActivity {

    String item_name, item_desc, item_price, item_salesman_name, item_image, date, invoice_number, ID;
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

        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        binding.itemNameSudccess.setText(item_name);
        binding.itemPriceSudccess.setText(item_price);


        ViewOrderModel model = new ViewOrderModel(invoice_number, "1", item_price, System.currentTimeMillis(), item_name);

        FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Orders").document(invoice_number).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

// Storing data into SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

// Storing the key and its value as the data fetched from edittext
                myEdit.putInt("invoice", Integer.parseInt(invoice_number));

// Once the changes have been made,
// we need to commit to apply those changes made,
// otherwise, it will throw an error
                myEdit.commit();
                cutmoneyifwallet();
                onclick();

            }
        });


    }

    private void cutmoneyifwallet() {
        //    if (ID.equals("wallet")) {
        FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Amount").document("moneyinaccount").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    String amountinstring = value.getString("amounts");
                    total = Integer.parseInt(amountinstring);
                    itemprice = Integer.parseInt(item_price);

                    Random r = new Random();
                    int i1 = r.nextInt(80 - 65) + 65;

                    binding.textView41.setText(String.valueOf(i1));

                    int newamounts = total - itemprice + i1;

//                    Toast.makeText(getApplicationContext(), "new" + String.valueOf(newamounts), Toast.LENGTH_SHORT).show();

// Storing data into SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref2", MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

// Storing the key and its value as the data fetched from edittext
                    myEdit.putString("amount", String.valueOf(newamounts));

// Once the changes have been made,
// we need to commit to apply those changes made,
// otherwise, it will throw an error
                    myEdit.commit();


                }
            }
        });
    }

    @Override
    public void onStop() {

        // Retrieving the value using its keys the file name
// must be same in both saving and retrieving the data
        SharedPreferences sh = getSharedPreferences("MySharedPref2", MODE_APPEND);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
        String s1 = sh.getString("amount", "");

        HashMap<String, Object> map = new HashMap<>();
        map.put("amounts", s1);
//
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