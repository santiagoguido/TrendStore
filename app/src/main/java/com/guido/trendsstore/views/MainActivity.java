package com.guido.trendsstore.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.guido.trendsstore.R;
import com.guido.trendsstore.utils.adapter.ShoeItemAdapter;
import com.guido.trendsstore.utils.model.ShoeCart;
import com.guido.trendsstore.utils.model.ShoeItem;
import com.guido.trendsstore.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ShoeItemAdapter.ShoeClickedListeners {

    private RecyclerView recyclerView;
    private List<ShoeItem> shoeItemList;
    private ShoeItemAdapter adapter;
    private CartViewModel viewModel;
    private List<ShoeCart> shoeCartList;
    private CoordinatorLayout coordinatorLayout;
    private ImageView cartImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();
        setUpList();

        adapter.setShoeItemList(shoeItemList);
        recyclerView.setAdapter(adapter);


        cartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getAllCartItems().observe(this, new Observer<List<ShoeCart>>() {
            @Override
            public void onChanged(List<ShoeCart> shoeCarts) {
                shoeCartList.addAll(shoeCarts);
            }
        });
    }
    private void setUpList(){
        shoeItemList.add(new ShoeItem("Complete fit" , "Dolce Gabbana New Arrivals" , R.drawable.dg_conjunto_men , 19990));
        shoeItemList.add(new ShoeItem("Letter Black" , "Dolce Gabbana New Arrivals" , R.drawable.dg_abrigo_black , 23900));
        shoeItemList.add(new ShoeItem("Jacket Black" , "Dolce Gabbana New Arrivals" , R.drawable.dg_black_jacket_caz , 33799));
        shoeItemList.add(new ShoeItem("Black Complete" , "Dolce Gabbana New Arrivals" , R.drawable.dg_conjunto_men_black , 27800));
        shoeItemList.add(new ShoeItem("Jack Men" , "Dolce Gabbana New Arrivals" , R.drawable.dg_jacket_men_black , 31980));
        shoeItemList.add(new ShoeItem("Stampado Men" , "Dolce Gabbana New Arrivals" , R.drawable.dg_jacket_stampado , 30699));
        shoeItemList.add(new ShoeItem("Men Shirt" , "Dolce Gabbana New Arrivals" , R.drawable.dg_shirt_men_white , 33699));
        shoeItemList.add(new ShoeItem("Men Sweater" , "Dolce Gabbana New Arrivals" , R.drawable.dg_sudadera_grabado , 26900));
        shoeItemList.add(new ShoeItem("Multix Men" , "Dolce Gabbana New Arrivals" , R.drawable.dg_sudadera_multix , 19870));
        shoeItemList.add(new ShoeItem("Sweater Black" , "Dolce Gabbana New Arrivals" , R.drawable.dg_sudaderamen , 24900));

    }

    private void initializeVariables() {

        cartImageView = findViewById(R.id.cartIv);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        shoeCartList = new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        shoeItemList = new ArrayList<>();
        recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ShoeItemAdapter(this);

    }

    @Override
    public void onCardClicked(ShoeItem shoe) {

        Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
        intent.putExtra("shoeItem", shoe);
        startActivity(intent);
    }

    @Override
    public void onAddToCartBtnClicked(ShoeItem shoeItem) {
        ShoeCart shoeCart = new ShoeCart();
        shoeCart.setShoeName(shoeItem.getShoeName());
        shoeCart.setShoeBrandName(shoeItem.getShoeBrandName());
        shoeCart.setShoePrice(shoeItem.getShoePrice());
        shoeCart.setShoeImage(shoeItem.getShoeImage());

        final int[] quantity = {1};
        final int[] id = new int[1];

        if (!shoeCartList.isEmpty()) {
            for (int i = 0; i < shoeCartList.size(); i++) {
                if (shoeCart.getShoeName().equals(shoeCartList.get(i).getShoeName())) {
                    quantity[0] = shoeCartList.get(i).getQuantity();
                    quantity[0]++;
                    id[0] = shoeCartList.get(i).getId();
                }
            }
        }

        Log.d("TAG", "onAddToCartBtnClicked: " + quantity[0]);

        if (quantity[0] == 1) {
            shoeCart.setQuantity(quantity[0]);
            shoeCart.setTotalItemPrice(quantity[0] * shoeCart.getShoePrice());
            viewModel.insertCartItem(shoeCart);
        } else {
            viewModel.updateQuantity(id[0], quantity[0]);
            viewModel.updatePrice(id[0], quantity[0] * shoeCart.getShoePrice());
        }

        makeSnackBar("Item Added To Cart");
    }

    private void makeSnackBar(String msg) {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT)
                .setAction("Go to Cart", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, CartActivity.class));
                    }
                }).show();
    }
}
