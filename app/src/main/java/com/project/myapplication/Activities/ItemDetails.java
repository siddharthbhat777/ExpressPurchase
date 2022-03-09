package com.project.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.myapplication.Model.CartModel;
import com.project.myapplication.R;
import com.squareup.picasso.Picasso;

import io.realm.Realm;

public class ItemDetails extends AppCompatActivity {

    TextView itemNameSingle, itemDescSingle, itemSalesmanName, itemPriceSingle, atc, bn;
    ImageView itemImageSingle;
    MaterialCardView addToCart, buyNow;
    private Realm realm;


    private String name, salesman, desc, image, price;
    private GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

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
        String personEmail = acct.getEmail();

        FirebaseFirestore.getInstance().collection("User").document(personEmail).collection("Cart")
                .document(name).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    //Item is in the cart before
                    atc.setText("VIEW CART");
                    addToCart.setCardBackgroundColor(getResources().getColor(R.color.purple));
                    addToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(MainActivity.mDrawerLayout.getContext(), ShoppingCartActivity.class);
                            intent.putExtra("item_name", name);
                            intent.putExtra("item_desc", desc);
                            intent.putExtra("item_price", price);
                            intent.putExtra("item_salesman_name", salesman);
                            intent.putExtra("item_image", image);
                            intent.putExtra("code", "1");

                            startActivity(intent);
                            finish();
                        }
                    });
                } else {

                    addToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            CartModel cmodel = new CartModel();
                            cmodel.setItemName(name);
                            cmodel.setItemPrice(Integer.parseInt(price));
                            cmodel.setNewprice(Integer.parseInt(price));
                            cmodel.setQuantity(Integer.parseInt("1"));
                            cmodel.setTime(System.currentTimeMillis());
                            cmodel.setItemImage(image);

                            FirebaseFirestore.getInstance().collection("User").document(personEmail).collection("Cart")
                                    .document(name).set(cmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {


                                    atc.setText("VIEW CART");

                                    addToCart.setCardBackgroundColor(getResources().getColor(R.color.purple));

                                    addToCart.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(MainActivity.mDrawerLayout.getContext(), ShoppingCartActivity.class);
                                            intent.putExtra("item_name", name);
                                            intent.putExtra("item_desc", desc);
                                            intent.putExtra("item_price", price);
                                            intent.putExtra("item_salesman_name", salesman);
                                            intent.putExtra("item_image", image);
                                            intent.putExtra("code", "1");
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }
        });


    }

}
