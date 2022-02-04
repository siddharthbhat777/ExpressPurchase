package com.project.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.Model.CartModel;
import com.project.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<CartModel> list;
    Context context;
    int quantity = 1;
    int price;
    Realm realm;

    public CartAdapter(List<CartModel> list, Context context) {
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

        realm = Realm.getDefaultInstance();

        price = model.getItemPrice();

        Picasso.get().load(model.getItemImage()).into(holder.thumbnail);
        holder.tittle.setText(model.getItemName());
        holder.price.setText("Rs " + String.valueOf(model.getItemPrice()));
        holder.quantity.setText(String.valueOf(quantity));

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quantity==15){
                    //nothing
                }
                else {
                    price = price + model.getItemPrice();

                    holder.price.setText("Rs " + String.valueOf(price));
                    quantity++;
                    holder.quantity.setText(String.valueOf(quantity));
                }

            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity == 1) {
                    //do nothing
                    holder.quantity.setText(String.valueOf(quantity));

                } else {
                    price = price - model.getItemPrice();
                    holder.price.setText("Rs " + String.valueOf(price));
                    quantity--;

                    holder.quantity.setText(String.valueOf(quantity));

                }
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<CartModel> rows = realm.where(CartModel.class).equalTo("itemImage", model.getItemImage()).findAll();
                        rows.deleteAllFromRealm();
                        notifyItemRemoved(position);
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, plus, minus;
        TextView tittle, price, quantity, remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.img);
            plus = itemView.findViewById(R.id.cart_plus_img);
            minus = itemView.findViewById(R.id.cart_minus_img);
            tittle = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price_text);
            quantity = itemView.findViewById(R.id.cart_product_quantity_tv);
            remove = itemView.findViewById(R.id.remove_cart);

        }
    }
}
