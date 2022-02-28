package com.project.myapplication.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.Adapter.CartAdapter;
import com.project.myapplication.Model.CartModel;
import com.project.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ShoppingCartActivity extends AppCompatActivity {

    RecyclerView rv_cart;
    List<CartModel> list = new ArrayList<>();
    CartAdapter adapter;
    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        init();
    }

    public void init() {
        rv_cart = findViewById(R.id.rv_cart);
        realm = Realm.getDefaultInstance();

        setuprv();
    }

    private void setuprv() {
        list = realm.where(CartModel.class).findAll();
        adapter = new CartAdapter(list, this);
        rv_cart.setLayoutManager(new LinearLayoutManager(this));
        rv_cart.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (list.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Nothing in Cart", Toast.LENGTH_SHORT).show();
            // Show anything when there is nothing in cart
        }

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