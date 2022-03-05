package com.project.myapplication.Activities;

import android.os.Bundle;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.Adapter.CartAdapter;
import com.project.myapplication.Model.CartModel;
import com.project.myapplication.Model.ViewOrderModel;
import com.project.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ShoppingCartActivity extends AppCompatActivity {

    RecyclerView rv_cart;
    ArrayList<CartModel> list ;
    CartAdapter adapter;
    GoogleSignInAccount acct;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        init();
    }

    public void init() {
        rv_cart = findViewById(R.id.rv_cart);
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

        gettotalprice(list);

    }

    private int gettotalprice(List<CartModel> mItems) {
        adapter.notifyDataSetChanged();
        int total = 0;
        for (int i = 0; i < mItems.size(); i++) {
            total += Integer.parseInt(String.valueOf(mItems.get(i).getItemPrice()));
        }
        Toast.makeText(getApplicationContext(), String.valueOf(total), Toast.LENGTH_SHORT).show();
        return total;
    }
}