package com.project.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.myapplication.Model.CartModel;
import com.project.myapplication.R;
import com.squareup.picasso.Picasso;

import io.realm.Realm;

public class ItemDetails extends AppCompatActivity {

    TextView itemNameSingle, itemDescSingle, itemSalesmanName, itemPriceSingle, atc, bn;
    ImageView itemImageSingle;
    LinearLayout addToCart, buyNow;
    private Realm realm;


    private String name, salesman, desc, image, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        realm = Realm.getDefaultInstance();

        itemNameSingle = findViewById(R.id.itemNameInside);
        itemDescSingle = findViewById(R.id.itemDesc);
        itemSalesmanName = findViewById(R.id.salesmanName);
        itemPriceSingle = findViewById(R.id.itemPriceInside);
        itemImageSingle = findViewById(R.id.itemImageInside);
        addToCart = findViewById(R.id.addToCartClick);
        buyNow = findViewById(R.id.buyNowClick);
        atc = findViewById(R.id.atcTV);
        bn = findViewById(R.id.buyTV);
        //cart = findViewById(R.id.button);

        name = getIntent().getStringExtra("item_name");
        salesman = getIntent().getStringExtra("item_salesman_name");
        desc = getIntent().getStringExtra("item_desc");
        image = getIntent().getStringExtra("item_image");
        price = getIntent().getStringExtra("item_price");

        Picasso.get().load(image).into(itemImageSingle);
        itemNameSingle.setText(name);
        itemSalesmanName.setText(salesman);
        itemDescSingle.setText(Html.fromHtml(desc));
        itemPriceSingle.setText(price);
        addtocart();
        buynow();
    }

    private void buynow() {
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PaymentOptions.class);
                intent.putExtra("item_name", name);
                intent.putExtra("item_desc", desc);
                intent.putExtra("item_price", String.valueOf(price));
                intent.putExtra("item_salesman_name", salesman);
                intent.putExtra("item_image", image);
                startActivity(intent);
            }
        });

    }

    private void addtocart() {

        CartModel user = realm.where(CartModel.class).equalTo("itemImage", image).findFirst();

        if (user == null) {
            // Not Exists

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CartModel cmodel = new CartModel();
                    // on below line we are getting id for the course which we are storing.
                    Number id = realm.where(CartModel.class).max("id");

                    // on below line we are
                    // creating a variable for our id.
                    long nextId;

                    // validating if id is null or not.
                    if (id == null) {
                        // if id is null
                        // we are passing it as 1.
                        nextId = 1;
                    } else {
                        // if id is not null then
                        // we are incrementing it by 1
                        nextId = id.intValue() + 1;
                    }
                    cmodel.setId(nextId);
                    cmodel.setItemName(name);
                    cmodel.setItemPrice(Integer.parseInt(price));
                    cmodel.setItemImage(image);


                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // inside on execute method we are calling a method
                            // to copy to real m database from our modal class.
                            realm.copyToRealmOrUpdate(cmodel);
                            atc.setText("VIEW CART");

                            addToCart.setBackgroundResource(R.drawable.linear_item_details_atc_clicked);

                            addToCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.mDrawerLayout.getContext(), ShoppingCartActivity.class);
                                    intent.putExtra("item_name", name);
                                    intent.putExtra("item_desc",desc);
                                    intent.putExtra("item_price",price);
                                    intent.putExtra("item_salesman_name" ,salesman);
                                    intent.putExtra("item_image",image);
                                    intent.putExtra("code","1");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });

                }
            });
        } else {
            // exist
            atc.setText("VIEW CART");
            addToCart.setBackgroundResource(R.drawable.linear_item_details_atc_clicked);
            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.mDrawerLayout.getContext(), ShoppingCartActivity.class);
                    intent.putExtra("item_name", name);
                    intent.putExtra("item_desc",desc);
                    intent.putExtra("item_price",price);
                    intent.putExtra("item_salesman_name" ,salesman);
                    intent.putExtra("item_image",image);
                    intent.putExtra("code","1");

                    startActivity(intent);
                    finish();
                    }
            });
        }

    }
}