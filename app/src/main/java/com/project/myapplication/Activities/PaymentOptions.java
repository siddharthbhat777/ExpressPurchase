package com.project.myapplication.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityPaymentOptionsBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Random;

public class PaymentOptions extends AppCompatActivity implements PaymentResultListener {
    ActivityPaymentOptionsBinding binding;
    int wallet_amounts;
    int item_price;
    String name, salesman, desc, image, price;
    String type = "";


    GoogleSignInAccount acct;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        binding.textView32.setText("₹ " + price);
        checkifeligibleforwallet();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkifeligibleforwallet() {
        item_price = Math.round(Float.parseFloat(price));
//        Toast.makeText(getApplicationContext(), "amount in item"+String.valueOf(item_price), Toast.LENGTH_SHORT).show();

        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personEmail = acct.getEmail();
            FirebaseFirestore.getInstance().collection("User").document(personEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {
                        String address = value.getString("address");
                        binding.textView29.setText(address);
                    }
                }
            });

            FirebaseFirestore.getInstance().collection("User").document(personEmail).collection("Amount").document("moneyinaccount").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {
                        String amountinstring = value.getString("amounts");
                        wallet_amounts = Integer.parseInt(amountinstring);


//                        Toast.makeText(getApplicationContext(), "amounr in wallet"+String.valueOf(wallet_amounts), Toast.LENGTH_SHORT).show();

                        if (item_price > 10000) {
                            //invisible
                            binding.materialCardView2.setVisibility(View.GONE);
                            binding.textView28.setVisibility(View.GONE);
                            binding.checkBox.setVisibility(View.GONE);
                            binding.textView18.setVisibility(View.GONE);

                            //visible
                            binding.radioButton2.setVisibility(View.VISIBLE);
                            binding.imageView8.setVisibility(View.VISIBLE);


                            showrazorpay();
                        } else if (item_price <= 10000 && wallet_amounts < item_price) {

                            binding.textView18.setVisibility(View.VISIBLE);

                            binding.checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //express wallet
                                    binding.radioButton2.setChecked(false);

                                    type = "express";
                                    binding.textView18.setVisibility(View.VISIBLE);
                                    binding.materialCardView2.setVisibility(View.VISIBLE);

                                    addmoney();

                                }
                            });
                            binding.radioButton2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //razor pay
                                    binding.checkBox.setChecked(false);
                                    type = "razorpay";

                                    binding.materialCardView2.setVisibility(View.GONE);
                                    binding.textView18.setVisibility(View.GONE);
                                    binding.linearProceedToPay.setVisibility(View.VISIBLE);

                                    showrazorpay();

                                }
                            });

                        } else if (item_price <= 10000 && wallet_amounts > item_price) {

                            binding.materialCardView2.setVisibility(View.GONE);
                            binding.textView18.setVisibility(View.GONE);
                            binding.linearProceedToPay.setVisibility(View.VISIBLE);
                            //binding.checkBox.setTextColor(Color.WHITE);

                            payfromwallet();

                            //invisible
                            binding.checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //express wallet


                                    binding.radioButton2.setChecked(false);

                                    type = "express";
                                    binding.materialCardView2.setVisibility(View.GONE);
                                    binding.textView18.setVisibility(View.GONE);
                                    binding.linearProceedToPay.setVisibility(View.VISIBLE);
                                    payfromwallet();

                                }
                            });
                            binding.radioButton2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //razor pay
                                    binding.checkBox.setChecked(false);
                                    type = "razorpay";

                                    binding.materialCardView2.setVisibility(View.GONE);
                                    binding.textView18.setVisibility(View.GONE);
                                    binding.linearProceedToPay.setVisibility(View.VISIBLE);

                                    showrazorpay();

                                }
                            });
                        }

                    }
                }
            });
        }
    }

    private void addmoney() {
        binding.linearProceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(PaymentOptions.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                dialog.setContentView(R.layout.insufficient_funds_dialog);

                CardView ok = (CardView) dialog.findViewById(R.id.ok);


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        binding.button7.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(), WalletActivity.class));
                            }
                        });
                    }
                });


                dialog.show();

            }
        });
    }

    private void payfromwallet() {


        binding.linearProceedToPay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int i1 = r.nextInt(10000000 - 10000) + 10000;
                Intent intent = new Intent(getApplicationContext(), PaymentSuccessful.class);
                intent.putExtra("item_name", name);
                intent.putExtra("item_desc", desc);
                intent.putExtra("item_price", String.valueOf(price));
                intent.putExtra("item_salesman_name", salesman);
                intent.putExtra("item_image", image);
                intent.putExtra("date", System.currentTimeMillis());
                intent.putExtra("invoice_number", String.valueOf(i1));
                intent.putExtra("ID", "wallet");
                startActivity(intent);
                finish();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showrazorpay() {


        binding.linearProceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = PaymentOptions.this;
                final Checkout co = new Checkout();
                try {
                    JSONObject options = new JSONObject();
                    options.put("name", "Express Purchase");
                    options.put("description", "Payment for the Selected Item");
                    //You can omit the image option to fetch the image from dashboard
                    options.put("currency", "INR");
                    // amount is in paise so please multiple it by 100
                    //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
                    options.put("amount", item_price * 100);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPaymentSuccess(String s) {
        Random r = new Random();
        int i1 = r.nextInt(10000000 - 10000) + 10000;
        Intent intent = new Intent(getApplicationContext(), PaymentSuccessful.class);
        intent.putExtra("item_name", name);
        intent.putExtra("item_desc", desc);
        intent.putExtra("item_price", String.valueOf(price));
        intent.putExtra("item_salesman_name", salesman);
        intent.putExtra("item_image", image);
        intent.putExtra("date/time", System.currentTimeMillis());
        intent.putExtra("invoice_number", String.valueOf(i1));
        intent.putExtra("ID", "razorpay");
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
    }

}