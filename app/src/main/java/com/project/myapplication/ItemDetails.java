package com.project.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ItemDetails extends AppCompatActivity {

    TextView itemNameSingle, itemDescSingle, itemSalesmanName, itemPriceSingle;
    ImageView itemImageSingle;

    private String name, salesman, desc, image, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemNameSingle = findViewById(R.id.itemNameInside);
        itemDescSingle = findViewById(R.id.itemDesc);
        itemSalesmanName = findViewById(R.id.salesmanName);
        itemPriceSingle = findViewById(R.id.itemPriceInside);
        itemImageSingle = findViewById(R.id.itemImageInside);

        name = getIntent().getStringExtra("item_name");
        salesman = getIntent().getStringExtra("item_salesman_name");
        desc = getIntent().getStringExtra("item_desc");
        image = getIntent().getStringExtra("item_image");
        price = getIntent().getStringExtra("item_price");

        Picasso.get().load(image).into(itemImageSingle);
        itemNameSingle.setText(name);
        itemSalesmanName.setText(salesman);
        itemDescSingle.setText(desc);
        itemPriceSingle.setText(price);
    }
}