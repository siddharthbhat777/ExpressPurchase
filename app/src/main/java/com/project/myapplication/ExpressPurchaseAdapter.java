package com.project.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

public class ExpressPurchaseAdapter extends FirebaseRecyclerAdapter<ExpressPurchaseModel, ExpressPurchaseAdapter.expressPurchaseViewHolder> {

    Context context;

    public ExpressPurchaseAdapter(@NonNull FirebaseRecyclerOptions<ExpressPurchaseModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull expressPurchaseViewHolder holder, int position, @NonNull ExpressPurchaseModel model) {
        holder.itemNameTV.setText(model.getItemName());
        holder.itemPriceTV.setText(model.getItemPrice());
        Picasso.get().load(model.getItemImage()).into(holder.itemImageIV);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }

    @NonNull
    @Override
    public expressPurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_main_recycler_view, parent, false);
        return new expressPurchaseViewHolder(view);
    }

    class expressPurchaseViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImageIV;
        TextView itemNameTV, itemPriceTV;

        public expressPurchaseViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImageIV = itemView.findViewById(R.id.itemImage);
            itemNameTV = itemView.findViewById(R.id.itemName);
            itemPriceTV = itemView.findViewById(R.id.itemPrice);
        }
    }
}
