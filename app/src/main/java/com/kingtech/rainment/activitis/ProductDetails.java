package com.kingtech.rainment.activitis;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kingtech.rainment.R;
import com.kingtech.rainment.model.Productlist;
import com.kingtech.rainment.utils.Constants;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;
public class ProductDetails extends AppCompatActivity {
    private ImageCarousel carousel2;
    private ImageView favorited;
    private TextView pddis;
    private Productlist currentProduct;
    private Cart cart;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        TextView pdprice = findViewById(R.id.pdprice);
        Button buybtn = findViewById(R.id.buybtn);
        Button addtocart = findViewById(R.id.addtocart);
        pddis = findViewById(R.id.pdtdis);
        favorited = findViewById(R.id.favorited);
        carousel2 = findViewById(R.id.carousel2);
        cart = TinyCartHelper.getCart();


        String title = getIntent().getStringExtra("tilte");
        double price = getIntent().getDoubleExtra("price",0);
        int id = getIntent().getIntExtra("id",0);
        pdprice.setText("Tk."+price);

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getproductimg(id);
        getProductDetails(id);


        favorited.setOnClickListener(v -> favorited.setImageResource(R.drawable.baseline_favorite));

        buybtn.setOnClickListener(v -> {
            if (cart.isCartEmpty()){
                Toast.makeText(this,"Add product to your cart",Toast.LENGTH_SHORT).show();
            }else {
                startActivity(new Intent(ProductDetails.this, CartActivity.class));
                Toast.makeText(this,"Add product to your cart",Toast.LENGTH_SHORT).show();
            }
                });

        addtocart.setOnClickListener(v -> {
            addtocart.setEnabled(false);
            addtocart.setText("Item Added");
            cart.addItem(currentProduct,1);
        });





    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_cart,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.card){
            if (cart.isCartEmpty()){
                Toast.makeText(this,"Add product to your cart",Toast.LENGTH_SHORT).show();
            }else {
                startActivity(new Intent(ProductDetails.this, CartActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    //=================Actionbar=================//

    @SuppressWarnings("deprecation")
    void getProductDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.GET_PRODUCT_DETAILS_URL + id;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")) {
                    JSONObject product = object.getJSONObject("product");
                    String description = product.getString("description");
                    pddis.setText(Html.fromHtml(description));

                     currentProduct = new Productlist(
                            product.getString("name"),
                            Constants.PRODUCTS_IMAGE_URL + product.getString("image"),
                            product.getString("status"),
                            product.getDouble("price"),
                            product.getDouble("price_discount"),
                            product.getInt("stock"),
                            product.getInt("id")
                    );

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});queue.add(request);
    }

    void getproductimg(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCT_DETAILS_URL + id;
        StringRequest request = new StringRequest(Request.Method.GET,url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")) {
                    JSONObject product = object.getJSONObject("product");
                    JSONArray offerArray = product.getJSONArray("product_images");
                    for(int i =0; i < offerArray.length(); i++) {
                        JSONObject childObj = offerArray.getJSONObject(i);
                        carousel2.addData(new CarouselItem(Constants.PRODUCTS_IMAGE_URL + childObj.getString("name")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});queue.add(request);
    }






}