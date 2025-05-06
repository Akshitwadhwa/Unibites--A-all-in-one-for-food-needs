package com.example.unibites;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private TextView totalPrice;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPrice = findViewById(R.id.totalPrice);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Set up back button
        btnBack.setOnClickListener(v -> finish());

        // Initialize cart items (you can load this from your data source)
        cartItems = new ArrayList<>();
        // Add some sample items
        cartItems.add(new CartItem("Burger", 5.99, R.drawable.img_3));
        cartItems.add(new CartItem("Pizza", 8.99, R.drawable.img_3));

        // Set up RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter(cartItems, new CartAdapter.OnCartItemListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                updateTotalPrice();
            }
        });
        cartRecyclerView.setAdapter(adapter);

        // Update total price
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        totalPrice.setText(String.format("$%.2f", total));
    }
} 