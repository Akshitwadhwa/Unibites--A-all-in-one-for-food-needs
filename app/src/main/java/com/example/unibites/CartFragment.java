package com.example.unibites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView tvSubtotal, tvTax, tvTotal, tvEmptyCart;
    private Button btnCheckout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private double subtotal = 0.0;
    private double tax = 0.0;
    private double total = 0.0;
    private final double TAX_RATE = 0.1; // 10% tax rate

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvTax = view.findViewById(R.id.tvTax);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Set up RecyclerView
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartItemList, new CartAdapter.OnCartItemActionListener() {
            @Override
            public void onQuantityChanged(CartItem cartItem, int position, int newQuantity) {
                updateCartItemQuantity(cartItem, newQuantity);
            }

            @Override
            public void onRemoveItem(CartItem cartItem, int position) {
                removeCartItem(cartItem);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cartAdapter);

        // Set up checkout button
        btnCheckout.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            } else {
               // Intent intent = new Intent(getContext(), PaymentActivity.clas);
               // startActivity(intent);
              //  Toast.makeText(getContext(), "Proceeding to checkout", Toast.LENGTH_SHORT).show();
            }
        });

        // Load cart items
        loadCartItems();

        return view;
    }

    private void loadCartItems() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showEmptyCart();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("carts")
                .document(userId)
                .collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cartItemList.clear();
                        subtotal = 0.0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CartItem cartItem = document.toObject(CartItem.class);
                            cartItem.setId(document.getId());
                            cartItemList.add(cartItem);
                            subtotal += cartItem.getTotalPrice();
                        }

                        calculateTotals();
                        cartAdapter.notifyDataSetChanged();

                        if (cartItemList.isEmpty()) {
                            showEmptyCart();
                        } else {
                            hideEmptyCart();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error loading cart items: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        showEmptyCart();
                    }
                });
    }

    private void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        if (newQuantity <= 0) {
            // Remove the item if quantity is 0 or less
            removeCartItem(cartItem);
            return;
        }

        db.collection("carts")
                .document(userId)
                .collection("items")
                .document(cartItem.getId())
                .update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    // Update the item in the list
                    for (int i = 0; i < cartItemList.size(); i++) {
                        if (cartItemList.get(i).getId().equals(cartItem.getId())) {
                            cartItemList.get(i).setQuantity(newQuantity);
                            cartAdapter.notifyItemChanged(i);
                            break;
                        }
                    }

                    // Recalculate totals
                    calculateTotals();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update quantity: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void removeCartItem(CartItem cartItem) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection("carts")
                .document(userId)
                .collection("items")
                .document(cartItem.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the item from the list
                    for (int i = 0; i < cartItemList.size(); i++) {
                        if (cartItemList.get(i).getId().equals(cartItem.getId())) {
                            cartItemList.remove(i);
                            cartAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }

                    // Recalculate totals
                    calculateTotals();

                    if (cartItemList.isEmpty()) {
                        showEmptyCart();
                    }

                    Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to remove item: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void calculateTotals() {
        subtotal = 0.0;

        for (CartItem item : cartItemList) {
            subtotal += item.getTotalPrice();
        }

        tax = subtotal * TAX_RATE;
        total = subtotal + tax;

        // Format currency values
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        tvSubtotal.setText(currencyFormat.format(subtotal));
        tvTax.setText(currencyFormat.format(tax));
        tvTotal.setText(currencyFormat.format(total));
    }

    private void showEmptyCart() {
        tvEmptyCart.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvSubtotal.setText("₹0.00");
        tvTax.setText("₹0.00");
        tvTotal.setText("₹0.00");
    }

    private void hideEmptyCart() {
        tvEmptyCart.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}