package com.kingtech.rainment.activitis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kingtech.rainment.R;
import com.kingtech.rainment.adepter.CartAdapter;
import com.kingtech.rainment.model.Productlist;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
    private  TextView totalamount;


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Button placeorders = findViewById(R.id.placeorders);
        RecyclerView cartrecyler = findViewById(R.id.cartlist);
        totalamount = findViewById(R.id.totalamount);
        Cart cart = TinyCartHelper.getCart();

        ArrayList<Productlist> productlists = new ArrayList<>();
        @SuppressLint("SetTextI18n") CartAdapter cartAdapter = new CartAdapter(this, productlists, () -> totalamount.setText("Tk." + cart.getTotalPrice()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CartActivity.this);
        cartrecyler.setLayoutManager(linearLayoutManager);
        cartrecyler.setAdapter(cartAdapter);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Productlist product = (Productlist) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            productlists.add(product);
        }
        totalamount.setText(String.format("Tk. %.2f",cart.getTotalPrice()));


        placeorders.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, CheckoutActivity.class)));


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}