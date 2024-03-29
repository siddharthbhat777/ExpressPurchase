package com.project.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.project.myapplication.Interfaces.CategoryClickInterface;
import com.project.myapplication.Model.CategoryModel;
import com.project.myapplication.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<CategoryModel> list;
    int selectedPosition = 0;
    CategoryClickInterface item_click;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list, CategoryClickInterface item_click) {
        this.context = context;
        this.list = list;
        this.item_click = item_click;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel model = list.get(position);
        holder.textView.setText(model.getCategoryName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                item_click.onClick(position, list.get(position));

                notifyDataSetChanged();  // notify
            }

        });

        if (selectedPosition != position) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.black));//#ffffff
            holder.textView.setTextColor(context.getResources().getColor(R.color.white));//#000000
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.tag_color));
            holder.textView.setTextColor(context.getResources().getColor(R.color.full_white));//#ffffff
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.categoryView);
            textView = itemView.findViewById(R.id.text);
        }
    }
}
