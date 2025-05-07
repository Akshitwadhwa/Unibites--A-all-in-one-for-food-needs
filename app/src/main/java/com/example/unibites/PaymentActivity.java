/*package com.example.unibites;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private LinearLayout cartItemsContainer;
    private TextView totalItemsPrice, totalGST, grandTotal;
    private Button payWithQR, payWithUPI, payWithCash;
    private ImageView backIcon;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;

    private List<CartItem> cartItems = new ArrayList<>();
    private double itemsTotal = 0.0;
    private double gstAmount = 0.0;
    private double finalTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();

        // Fetch cart items
        fetchCartItems();
    }
    private void initViews() {
        cartItemsContainer = findViewById(R.id.cart_items_container);
        totalItemsPrice = findViewById(R.id.total_items_price);
        totalGST = findViewById(R.id.total_gst);
        grandTotal = findViewById(R.id.grand_total);
        payWithQR = findViewById(R.id.pay_with_qr_button);
        payWithUPI = findViewById(R.id.pay_with_upi_button);
        payWithCash = findViewById(R.id.pay_with_cash_button);
        backIcon = findViewById(R.id.back_icon);
    }

    private void setClickListeners() {
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        payWithCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder("Cash");
            }
        });

        payWithQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder("QR Code");
            }
        });

        payWithUPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder("UPI");
            }
        });
    }

    private void fetchCartItems() {
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please login to view your cart", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cartItems.clear();
                            itemsTotal = 0.0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String itemId = document.getId();
                                String itemName = document.getString("name");
                                double itemPrice = document.getDouble("price");
                                int quantity = document.getLong("quantity").intValue();
                                String imageUrl = document.getString("imageUrl");

                                CartItem item = new CartItem(itemId, itemName, itemPrice, quantity, imageUrl);
                                cartItems.add(item);
                                itemsTotal += (itemPrice * quantity);
                            }

                            // Calculate total amounts
                            calculateTotals();

                            // Display cart items
                            displayCartItems();

                        } else {
                            Toast.makeText(CartCheckoutActivity.this, "Error fetching cart: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void calculateTotals() {
        // Calculate GST (9.1% in this example, based on your layout showing ₹4.55 GST on ₹50)
        gstAmount = Math.round((itemsTotal * 0.091) * 100.0) / 100.0;
        finalTotal = itemsTotal + gstAmount;

        // Update UI
        totalItemsPrice.setText("₹ " + String.format(Locale.getDefault(), "%.2f", itemsTotal));
        totalGST.setText("₹ " + String.format(Locale.getDefault(), "%.2f", gstAmount));
        grandTotal.setText("₹ " + String.format(Locale.getDefault(), "%.2f", finalTotal));
    }

    private void displayCartItems() {
        // Clear existing views
        cartItemsContainer.removeAllViews();

        // Add each cart item to the container
        for (CartItem item : cartItems) {
            View itemView = getLayoutInflater().inflate(R.layout.cart_item_layout, cartItemsContainer, false);

            ImageView itemImage = itemView.findViewById(R.id.item_image);
            TextView itemName = itemView.findViewById(R.id.item_name);
            TextView itemQuantity = itemView.findViewById(R.id.item_quantity);
            TextView itemPrice = itemView.findViewById(R.id.item_price);

            // Set item data
            // Use Glide or Picasso to load image from URL if available
            // For example: Glide.with(this).load(item.getImageUrl()).into(itemImage);
            itemName.setText(item.getName());
            itemQuantity.setText(String.valueOf(item.getQuantity()));
            itemPrice.setText("₹ " + String.format(Locale.getDefault(), "%.2f", item.getPrice() * item.getQuantity()));

            cartItemsContainer.addView(itemView);
        }
    }

    private void placeOrder(String paymentMethod) {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create order details
        Map<String, Object> order = new HashMap<>();
        order.put("userId", userId);
        order.put("orderDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        order.put("totalAmount", finalTotal);
        order.put("itemsTotal", itemsTotal);
        order.put("gstAmount", gstAmount);
        order.put("paymentMethod", paymentMethod);
        order.put("status", "Placed");

        // Create items list
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("itemId", item.getId());
            orderItem.put("name", item.getName());
            orderItem.put("price", item.getPrice());
            orderItem.put("quantity", item.getQuantity());
            orderItems.add(orderItem);
        }
        order.put("items", orderItems);

        // Add order to Firestore
        db.collection("orders")
                .add(order)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Clear cart after successful order
                            clearCart();
                        } else {
                            Toast.makeText(CartCheckoutActivity.this, "Error placing order: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void clearCart() {
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }

                            Toast.makeText(CartCheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                            // Navigate back to home page
                            Intent intent = new Intent(CartCheckoutActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}*/