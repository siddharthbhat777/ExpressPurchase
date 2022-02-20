package com.project.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.Model.PaidModel;
import com.project.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaidAdapter extends RecyclerView.Adapter<PaidAdapter.ViewHolder> {

    Context context;
    ArrayList<PaidModel> list;

    public PaidAdapter(Context context, ArrayList<PaidModel> list) {
        this.context = context;
        this.list = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.paid_items_single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PaidModel model = list.get(position);
        holder.name.setText(model.getName());
        holder.noofproduct.setText(model.getNo_of_items());
        holder.totalprice.setText(model.getPrice());
        holder.sno.setText(holder.getAdapterPosition());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView  noofproduct, totalprice,name,sno;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sno = itemView.findViewById(R.id.srNo);
            noofproduct = itemView.findViewById(R.id.noOfItems);
            totalprice = itemView.findViewById(R.id.itemPriceSuccess);
            name = itemView.findViewById(R.id.itemNameSuccess);

        }
    }
}