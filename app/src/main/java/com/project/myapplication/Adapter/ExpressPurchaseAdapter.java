package com.project.myapplication.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.project.myapplication.Activities.ItemDetails;
import com.project.myapplication.Activities.MainActivity;
import com.project.myapplication.Activities.ShoppingCartActivity;
import com.project.myapplication.Model.CartModel;
import com.project.myapplication.Model.ExpressPurchaseModel;
import com.project.myapplication.R;
import com.squareup.picasso.Picasso;

import io.realm.Realm;

public class ExpressPurchaseAdapter extends FirebaseRecyclerAdapter<ExpressPurchaseModel, ExpressPurchaseAdapter.expressPurchaseViewHolder> {

    private Realm realm;


    public ExpressPurchaseAdapter(@NonNull FirebaseRecyclerOptions<ExpressPurchaseModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull expressPurchaseViewHolder holder, int position, @NonNull ExpressPurchaseModel model) {
        realm = Realm.getDefaultInstance();
        holder.itemNameTV.setText(model.getItemName());
        holder.itemPriceTV.setText(String.valueOf(model.getItemPrice()));
        try {
            Picasso.get().load(model.getItemImage()).into(holder.itemImageIV);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.mDrawerLayout.getContext(), ItemDetails.class);
                intent.putExtra("item_name", model.getItemName());
                intent.putExtra("item_desc", model.getDescription());
                intent.putExtra("item_price", String.valueOf(model.getItemPrice()));
                intent.putExtra("item_salesman_name", model.getSalesman());
                intent.putExtra("item_image", model.getItemImage());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.mDrawerLayout.getContext().startActivity(intent);
            }
        });

        CartModel user = realm.where(CartModel.class).equalTo("itemImage", model.getItemImage()).findFirst();

        if (user == null) {
            // Not Exists

            holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartModel cmodel = new CartModel();
                cmodel.setItemName(model.getItemName());
                cmodel.setItemPrice(model.getItemPrice());
                cmodel.setItemImage(model.getItemImage());

                realm = Realm.getDefaultInstance();


                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // inside on execute method we are calling a method
                        // to copy to real m database from our modal class.
                        realm.copyToRealmOrUpdate(cmodel);
                        holder.cart.setText("Visit to Cart !");
                        holder.addtocart.setCardBackgroundColor(holder.addtocart.getContext().getResources().getColor(R.color.purple));

                        holder.cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.addtocart.getContext().startActivity(new Intent(holder.addtocart.getContext(), ShoppingCartActivity.class));
                            }
                        });
                    }
                });
            }
        });
    }else{
            holder.cart.setText("Visit to Cart !");
            holder.addtocart.setCardBackgroundColor(holder.addtocart.getContext().getResources().getColor(R.color.purple));

            holder.cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.addtocart.getContext().startActivity(new Intent(holder.addtocart.getContext(), ShoppingCartActivity.class));
                }
            });}
    }

    @NonNull
    @Override
    public expressPurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_main_recycler_view, parent, false);
        return new expressPurchaseViewHolder(view);
    }

    class expressPurchaseViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImageIV;
        TextView itemNameTV, itemPriceTV, itemDescTV, itemSalesmanTV;
        CardView itemCardViewCV, addtocart;
        View view;
        Button cart;

        public expressPurchaseViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImageIV = itemView.findViewById(R.id.itemImage);
            itemNameTV = itemView.findViewById(R.id.itemName);
            itemPriceTV = itemView.findViewById(R.id.itemPrice);
            itemDescTV = itemView.findViewById(R.id.itemDesc);
            itemSalesmanTV = itemView.findViewById(R.id.salesmanName);
            itemCardViewCV = itemView.findViewById(R.id.itemCardView);
            addtocart = itemView.findViewById(R.id.cardView3);
            cart = itemView.findViewById(R.id.button3);
            this.view = itemView;
        }
    }
}
