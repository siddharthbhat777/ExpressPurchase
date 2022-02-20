package com.project.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.Adapter.ViewOrderAdapter;
import com.project.myapplication.R;
import com.project.myapplication.Model.ViewOrderModel;
import com.project.myapplication.databinding.ActivityViewOrdersBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewOrders extends AppCompatActivity {
    ArrayList<ViewOrderModel> list;
    ViewOrderAdapter adapter;
    RecyclerView rv_orders;
    ActivityViewOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setdata();
        initrv();

    }

    private void onclick(String invoice_number) {
        binding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"expresspurchase4531@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "My Order Problem");
                i.putExtra(Intent.EXTRA_TEXT, "Your Query Here...... \n\n\n\n\n\n\n" + "Product Invoice Number : " + invoice_number);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ViewOrders.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setdata() {

        // Retrieving the value using its keys the file name
// must be same in both saving and retrieving the data
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
        int a = sh.getInt("invoice", 0);

// We can then use the data

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Orders").document(String.valueOf(a)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    ViewOrderModel model = value.toObject(ViewOrderModel.class);
                    binding.textView21.setText(model.getInvoice_number());
                    binding.textView24.setText(model.getNumber_of_products());
                    binding.textView25.setText(model.getTotal_price());
                    binding.textView2d1.setText(model.getItem_name());
                    Long currentTime = model.getDate_of_delivery();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
                    Date date = new Date(currentTime);
                    String time = simpleDateFormat.format(date);
                    binding.textView26.setText(time);


                    onclick(model.getInvoice_number());

                } else {
                    Toast.makeText(getApplicationContext(), "Nothing Here", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initrv() {
        rv_orders = findViewById(R.id.recyclerView);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        list = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("User").document(account.getEmail()).collection("Orders").orderBy("date_of_delivery", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    list.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        ViewOrderModel model = snapshot.toObject(ViewOrderModel.class);
                        list.add(model);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing here", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

        });

        adapter = new ViewOrderAdapter(this, list);
        rv_orders.setLayoutManager(new LinearLayoutManager(this));
        rv_orders.setAdapter(adapter);

    }
}