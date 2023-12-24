package com.kingtech.rainment.adepter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kingtech.rainment.R;
import com.kingtech.rainment.databinding.QuentityDilogBoxBinding;
import com.kingtech.rainment.model.Productlist;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.cartviewholder> {
   private final Context context;
   private final ArrayList<Productlist>product;
   private final CartListener cartListener;
   private final Cart cart;

    public interface CartListener{
        void onQuantityChanged();
    }


    public CartAdapter(Context context, ArrayList<Productlist> product,CartListener cartListener) {
        this.context = context;
        this.product = product;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }


    @NonNull
    @Override
    public cartviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);

        return new cartviewholder(myview);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull cartviewholder holder, int position) {
       Productlist productlist = product.get(position);
       holder.cartqty.setText(productlist.getQuantity()+" (Items)");
       holder.title.setText(productlist.getTitle());
       holder.price.setText("Tk. "+productlist.getPrice());
        Picasso.get()
                .load(productlist.getImage())
                .placeholder(R.drawable.pd_dress)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            QuentityDilogBoxBinding quentityBinding = QuentityDilogBoxBinding.inflate(LayoutInflater.from(context));
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(quentityBinding.getRoot())
                    .create();
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            quentityBinding.productName.setText(productlist.getTitle());
            quentityBinding.productStock.setText("Stock: "+productlist.getStock());
            quentityBinding.quantity.setText(String.valueOf(productlist.getQuantity()));
            int stock = productlist.getStock();

            quentityBinding.plusBtn.setOnClickListener(v1 -> {
            int quantity = productlist.getQuantity();
            quantity++;
            if (quantity>stock){
                Toast.makeText(context,"Out of Stock",Toast.LENGTH_SHORT).show();
            }else {
            productlist.setQuantity(quantity);
            quentityBinding.quantity.setText(String.valueOf(quantity));
            notifyDataSetChanged();
            cart.updateItem(productlist, productlist.getQuantity());
            cartListener.onQuantityChanged();
            }


            });
            quentityBinding.minusBtn.setOnClickListener(v3 -> {
            int quantity = productlist.getQuantity();
            if (quantity>1){
                quantity--;
                productlist.setQuantity(quantity);
                quentityBinding.quantity.setText(String.valueOf(quantity));
                notifyDataSetChanged();
                cart.updateItem(productlist, productlist.getQuantity());
                cartListener.onQuantityChanged();
            }
            });
            quentityBinding.saveBtn.setOnClickListener(v2 -> {
                dialog.dismiss();
                notifyDataSetChanged();
            });




        });//<---------------itemview onclick end---------------->


    }




    @Override
    public int getItemCount() {
        return product.size();
    }

    public static class cartviewholder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title,price,cartqty;

        public cartviewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cartimge);
            title = itemView.findViewById(R.id.carttile);
            price = itemView.findViewById(R.id.cartprice);
            cartqty = itemView.findViewById(R.id.cartqty);
        }
    }
}
