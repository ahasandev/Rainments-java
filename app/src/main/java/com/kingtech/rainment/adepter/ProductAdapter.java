package com.kingtech.rainment.adepter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kingtech.rainment.R;
import com.kingtech.rainment.activitis.ProductDetails;
import com.kingtech.rainment.model.Productlist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.productviewholder> {
    Context context;
    ArrayList<Productlist>productlist;


    public ProductAdapter(Context context, ArrayList<Productlist> productlist) {
        this.context = context;
        this.productlist = productlist;
    }



    @NonNull
    @Override
    public productviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myview = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_product,parent,false);
        return new productviewholder(myview);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull productviewholder holder, int position) {
        Productlist produtlist1 = productlist.get(position);
        double price = produtlist1.getPrice();
        String image = produtlist1.getImage();

        Picasso.get()
                .load(image)
                .placeholder(R.drawable.pd_dress)
                .into(holder.imageView);
        holder.price.setText("Tk."+price);

      holder.imageView.setOnClickListener(v -> {
          Intent intent = new Intent(context, ProductDetails.class);
          intent.putExtra("tilte",produtlist1.getTitle());
          intent.putExtra("image",produtlist1.getImage());
          intent.putExtra("price",produtlist1.getPrice());
          intent.putExtra("id",produtlist1.getId());
          context.startActivity(intent);
      });


    }

    @Override
    public int getItemCount() {
        return productlist.size();
    }

    public static class productviewholder extends RecyclerView.ViewHolder{
        TextView price;
        ImageView imageView;

        public productviewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgeview2);
            price = itemView.findViewById(R.id.pricetext);
//            crat_add = itemView.findViewById(R.id.crat_add);
        }

    }
}
