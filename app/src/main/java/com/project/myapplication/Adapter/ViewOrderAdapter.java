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

import com.project.myapplication.Model.ViewOrderModel;
import com.project.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.ViewHolder> {

    Context context;
    ArrayList<ViewOrderModel> list;

    public ViewOrderAdapter(Context context, ArrayList<ViewOrderModel> list) {
        this.context = context;
        this.list = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_orders_past_single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ViewOrderModel model = list.get(position);
        holder.invoice.setText(model.getInvoice_number());
        holder.noofproduct.setText(model.getNumber_of_products());
        holder.totalprice.setText(model.getTotal_price());
        holder.textViewn21.setText(model.getItem_name());
        Long currentTime = model.getDate_of_delivery();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
        Date date = new Date(currentTime);
        String time = simpleDateFormat.format(date);
        holder.dateofdeliver.setText(time);

        holder.ViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"expresspurchase4531@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "My Order Problem");
                i.putExtra(Intent.EXTRA_TEXT   , "Your Query Here...... \n\n\n\n\n\n\n"+"Product Invoice Number : "+model.getInvoice_number());
                try {
                    context.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context.getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView invoice, noofproduct, totalprice, dateofdeliver,textViewn21;
        Button ViewDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            invoice = itemView.findViewById(R.id.textView21);
            noofproduct = itemView.findViewById(R.id.textView24);
            totalprice = itemView.findViewById(R.id.textView25);
            dateofdeliver = itemView.findViewById(R.id.textView26);
            textViewn21 = itemView.findViewById(R.id.textViewn21);
            ViewDetail = itemView.findViewById(R.id.button5);
        }
    }
}