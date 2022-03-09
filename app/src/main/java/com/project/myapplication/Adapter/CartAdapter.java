package com.project.myapplication.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.Model.CartModel;
import com.project.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<CartModel> list;
    Context context;
    int current_pos;
    GoogleSignInAccount acct;

    public CartAdapter(ArrayList<CartModel> list, Context context) {
        this.list = list;
        this.context = context;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cartitem_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartModel model = list.get(position);
        acct = GoogleSignIn.getLastSignedInAccount(context);




        Picasso.get().load(model.getItemImage()).into(holder.thumbnail);
        holder.title.setText(model.getItemName());
        holder.price.setText("₹ " + String.valueOf(model.getNewprice()));
        holder.quantity.setText(String.valueOf(model.getQuantity()));

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (model.getQuantity()==15){
                    //nothing
                }
                else {

                    model.setQuantity(model.getQuantity()+1);;
                    model.setNewprice(model.getItemPrice()*model.getQuantity());

                    HashMap<String,Object> map = new HashMap<>();
                    map.put("quantity",model.getQuantity());
                    map.put("newprice",model.getNewprice());


                    FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Cart")
                            .document(model.getItemName()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                        });
                    holder.price.setText("₹ " + String.valueOf(model.getNewprice()));

                            holder.quantity.setText(String.valueOf(model.getQuantity()));
                }

            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getQuantity() == 1) {
                    //do nothing
                    holder.quantity.setText(String.valueOf(model.getQuantity()));

                } else {

                    model.setQuantity(model.getQuantity()-1);;
                    model.setNewprice(model.getItemPrice()*model.getQuantity());

                    HashMap<String,Object> map = new HashMap<>();
                    map.put("quantity",model.getQuantity());
                    map.put("newprice",model.getNewprice());

                    FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Cart")
                            .document(model.getItemName()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                    });
                    holder.quantity.setText(String.valueOf(model.getQuantity()));
                    holder.price.setText("₹ " + String.valueOf(model.getNewprice()));

                }
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
current_pos = position;
                new AlertDialog.Builder(context)
                        .setTitle("Remove Item")
                        .setMessage("Are you sure to remove this item from your cart ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore.getInstance().collection("User").document(acct.getEmail()).collection("Cart")
                                        .document(model.getItemName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        notifyItemRemoved(current_pos);

                                    }
                                })
;
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })


                        .setCancelable(false).show();


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, price, quantity;
        CardView plus, minus, remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.img);
            plus = itemView.findViewById(R.id.plus_cart_card);
            minus = itemView.findViewById(R.id.minus_cart_card);
            title = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price_text);
            quantity = itemView.findViewById(R.id.cart_product_quantity_tv);
            remove = itemView.findViewById(R.id.remove_cart_card);

        }
    }
}
