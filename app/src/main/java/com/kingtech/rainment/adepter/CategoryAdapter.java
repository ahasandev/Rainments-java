package com.kingtech.rainment.adepter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingtech.rainment.R;
import com.kingtech.rainment.activitis.CartActivity;
import com.kingtech.rainment.activitis.CategoryActivity;
import com.kingtech.rainment.model.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.categoryviewholder>{

    Context context;
    ArrayList<Category> categories;

    public CategoryAdapter(Context context, ArrayList<Category>categories){
        this.context = context;
        this.categories = categories;
    }
    @NonNull
    @Override
    public categoryviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout, parent, false);
        return new categoryviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categoryviewholder holder, int position) {
        Category category = categories.get(position);

        String name = category.getName();
        String icon = category.getIcon();
//        String color = category.getColor();
//        String bref = category.getBref();
       int id = category.getId();
        holder.textView.setText(name);

        Picasso.get()
                .load(icon)
                .placeholder(R.drawable.rainment)
                .into(holder.profile_image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryActivity.class);
            intent.putExtra("categoryName",name);
            intent.putExtra("catId",id);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return categories.size() ;
    }

    public static class categoryviewholder extends RecyclerView.ViewHolder{

        CircleImageView profile_image;
        TextView textView;

        public categoryviewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cattilte);
            profile_image = itemView.findViewById(R.id.profile_image);

        }
    }

    }
