package com.kingtech.rainment.activitis;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kingtech.rainment.R;
import com.kingtech.rainment.adepter.CategoryAdapter;
import com.kingtech.rainment.adepter.ProductAdapter;
import com.kingtech.rainment.model.Productlist;
import com.kingtech.rainment.model.Category;
import com.kingtech.rainment.utils.Constants;
import com.kingtech.rainment.utils.NetworkChangeReceiver;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {
    private LottieAnimationView progressBar;
    private ImageCarousel carousel;
    private DrawerLayout drawerLayout;
    private Cart cart;
    private AlertDialog dialog;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private RecyclerView productList,categoriesList;
    private ArrayList<Category>categorilist = new ArrayList<>();
    private ArrayList<Productlist>productlist = new ArrayList<>();
    private final NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productList = findViewById(R.id.recyclerview);
        categoriesList =findViewById(R.id.recyclerView2);
        carousel = findViewById(R.id.carousel);
        progressBar = findViewById(R.id.progressBar);
        drawerLayout = findViewById(R.id.drawerlayout);
        Toolbar materialToolbar = findViewById(R.id.materialtoolbar);
        NavigationView navigationView = findViewById(R.id.navigationview);
        cart = TinyCartHelper.getCart();

        progressBar.setVisibility(View.VISIBLE);

        getRecentOffers();//backslider
        initCategories();//categories
        initProducts();//home production

        //=======================Start Toolbar View ==============================================//
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout, materialToolbar,R.string.close_drawer,R.string.open_drawer);
        drawerLayout.addDrawerListener(toggle);
        //===============================End Toolbar View ========================================//

        //=======================Start Navigation Drawer==========================================//
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.toolbaar_slide);
        materialToolbar.startAnimation(animation);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId()==R.id.profile){
                Toast.makeText(MainActivity.this, "Home click Succeed", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId()==R.id.favorite) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Toast.makeText(MainActivity.this, "Profile click Succeed", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId()==R.id.notification) {
                Toast.makeText(MainActivity.this, "Notification click Succeed", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId()==R.id.mycard) {
                if (cart.isCartEmpty()){
                    Toast.makeText(MainActivity.this, "Add Item To Your Cart", Toast.LENGTH_SHORT).show();
                    item.setCheckable(false);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else {startActivity(new Intent(this,CartActivity.class));}

            }
            return true;
        });
        //=======================End Navigation Drawer============================================//





    }//===============End OnCreate========================//


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.rainment);
        builder.setTitle("Exit !");
        builder.setMessage("Do you want to exit ?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> finishAffinity());

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void initCategories() {
        categorilist = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categorilist);
        getCategories();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        categoriesList.setLayoutManager(layoutManager);
        categoriesList.setAdapter(categoryAdapter);
    }

    void initProducts() {
        productlist = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productlist);
        getRecentProducts();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productList.setLayoutManager(layoutManager);
        productList.setAdapter(productAdapter);
    }


    void getCategories() {
        RequestQueue queue = Volley.newRequestQueue(this);
        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, response -> {
            try {
                Log.e("error", response);
                JSONObject mainObj = new JSONObject(response);
                if(mainObj.getString("status").equals("success")) {
                    JSONArray categoriesArray = mainObj.getJSONArray("categories");
                    for(int i =0; i< categoriesArray.length(); i++) {
                        JSONObject object = categoriesArray.getJSONObject(i);
                        Category category = new Category(
                                object.getString("name"),
                                Constants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                object.getString("color"),
                                object.getString("brief"),
                                object.getInt("id")
                            );categorilist.add(category);
                        }categoryAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });queue.add(request);
    }

    void getRecentProducts(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?count=20";
        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")){
                    JSONArray productsArray = object.getJSONArray("products");
                    for(int i =0; i< productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        Productlist product = new Productlist(
                                childObj.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString("image"),
                                childObj.getString("status"),
                                childObj.getDouble("price"),
                                childObj.getDouble("price_discount"),
                                childObj.getInt("stock"),
                                childObj.getInt("id")

                        );
                        productlist.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { });

        queue.add(request);
    }

    void getRecentOffers() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")) {
                    JSONArray offerArray = object.getJSONArray("news_infos");
                    for(int i =0; i < offerArray.length(); i++) {
                        JSONObject childObj =  offerArray.getJSONObject(i);
                        carousel.addData(
                                new CarouselItem(
                                        Constants.NEWS_IMAGE_URL + childObj.getString("image"),
                                        childObj.getString("title")
                                )
                        );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
        queue.add(request);
    }


    //===============network change =============

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if (isConnected) {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }

        } else {

            if (dialog == null || !dialog.isShowing()) {

                ShowDialog();
            }

        }
    }


    @SuppressLint("ResourceAsColor")
    private void ShowDialog() {

        dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(R.layout.no_internet_dialog)
                .setCancelable(false)
                .create();
        dialog.show();


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));



    }









}//===================End AppCompatActivity==============//