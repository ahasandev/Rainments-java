package com.kingtech.rainment.activitis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kingtech.rainment.adepter.CartAdapter;
import com.kingtech.rainment.databinding.CheckoutActivityBinding;
import com.kingtech.rainment.model.Productlist;
import com.kingtech.rainment.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity {

    CheckoutActivityBinding binding;
    private double totalPrice = 0;
    private final int tax = 120;
    private ProgressDialog progressDialog;
    private Cart cart;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CheckoutActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        ArrayList<Productlist> products = new ArrayList<>();
        cart = TinyCartHelper.getCart();

        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Productlist product = (Productlist) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        @SuppressLint("DefaultLocale") CartAdapter adapter = new CartAdapter(this, products, () -> binding.subtotal.setText(String.format("Tk. %.2f", cart.getTotalPrice())));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
//        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);

        binding.subtotal.setText(String.format("Tk. %.2f",cart.getTotalPrice()));

//        totalPrice = (cart.getTotalPrice().doubleValue() * tax / 100) + cart.getTotalPrice().doubleValue();
          totalPrice = tax + cart.getTotalPrice().doubleValue();
        binding.total.setText("Tk. " + totalPrice);


        binding.checkoutBtn.setOnClickListener(view -> {

            String nameBox =binding.nameBox.getText().toString();
            String phoneBox =binding.phoneBox.getText().toString();
            String emailBox =binding.emailBox.getText().toString();
            String addressBox =binding.addressBox.getText().toString();

            if (nameBox.trim().length()>0 && phoneBox.trim().length()>10 && addressBox.trim().length()>0 && emailBox.contains(".com")){
                processOrder();
            } else {
                binding.phoneBox.setError("Check Your Phone Number");
                binding.nameBox.setError("Check Your  Name");
                binding.addressBox.setError("Check Your  Address");
                Toast.makeText(CheckoutActivity.this,"Check Your Information",Toast.LENGTH_SHORT).show();
            }

        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }



    void processOrder() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject productOrder = new JSONObject();
        JSONObject dataObject = new JSONObject();
        try {

            productOrder.put("address",binding.addressBox.getText().toString());
            productOrder.put("buyer",binding.nameBox.getText().toString());
            productOrder.put("comment", binding.commentBox.getText().toString());
            productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update", Calendar.getInstance().getTimeInMillis());
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
            productOrder.put("email", binding.emailBox.getText().toString());
            productOrder.put("phone", binding.phoneBox.getText().toString());
            productOrder.put("serial", "cab8c1a4e4421a3b");
            productOrder.put("shipping", "");
            productOrder.put("shipping_location", "");
            productOrder.put("shipping_rate", "0.0");
            productOrder.put("status", "WAITING");
            productOrder.put("tax", tax);
            productOrder.put("total_fees", totalPrice);

            JSONArray product_order_detail = new JSONArray();
            for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
                Productlist product = (Productlist) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);

                JSONObject productObj = new JSONObject();
                productObj.put("amount", quantity);
                productObj.put("price_item", product.getPrice());
                productObj.put("product_id", product.getId());
                productObj.put("product_name", product.getTitle());
                product_order_detail.put(productObj);
            }

            dataObject.put("product_order",productOrder);
            dataObject.put("product_order_detail",product_order_detail);

            Log.e("err", dataObject.toString());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.POST_ORDER_URL, dataObject, response -> {
            try {
                if (response.getString("status").equals("success")) {
                    Toast.makeText(CheckoutActivity.this, "Success order.", Toast.LENGTH_SHORT).show();
                    String orderNumber = response.getJSONObject("data").getString("code");
                    new AlertDialog.Builder(CheckoutActivity.this)
                            .setTitle("Order Successful")
                            .setCancelable(false)
                            .setMessage("Your order number is: " + orderNumber)
                            .setPositiveButton("Lets Shopping", (dialogInterface, i) -> {
                                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                                intent.putExtra("orderCode", orderNumber);
                                startActivity(intent);
                            }).show();
                } else {
                    new AlertDialog.Builder(CheckoutActivity.this)
                            .setTitle("Order Failed")
                            .setMessage("Something went wrong, please try again.")
                            .setCancelable(false)
                            .setPositiveButton("Close", (dialogInterface, i) -> {

                            }).show();
                    Toast.makeText(CheckoutActivity.this, "Failed order.", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                Log.e("res", response.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, error -> {}) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Security","secure_code");
                return headers;
            }
        } ;
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}