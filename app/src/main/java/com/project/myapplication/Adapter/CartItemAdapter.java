package com.project.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.Model.CartModel;
import com.project.myapplication.R;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    ArrayList<CartModel> list;
    Context context;

    public CartItemAdapter(ArrayList<CartModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartModel model = list.get(position);
        holder.sno.setText(String.valueOf(position+1));
        holder.price.setText(String.valueOf(model.getNewprice()));
        holder.name.setText(String.valueOf(model.getItemName()));
        holder.quantity.setText(String.valueOf(model.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView quantity, name, sno, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.quantity);
            sno = itemView.findViewById(R.id.sno);
            price = itemView.findViewById(R.id.itemPriceSudccess);
            name = itemView.findViewById(R.id.itemNameSudccess);
        }
    }
}
