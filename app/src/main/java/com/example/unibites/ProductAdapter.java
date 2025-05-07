package com.example.unibites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public interface OnProductClickListener {
        void onProductClick(Product product, int position);
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_card_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText("$" + product.getPrice());

        // Set rating if available
//        if (holder.tvRating != null && product.getRating() > 0) {
//            holder.tvRating.setText(String.valueOf(product.getRating()));
//        }

        // Load image from URL using Glide
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.chilli_biryani)
                            .error(R.drawable.chilli_biryani))
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.chilli_biryani);
        }

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product, position);
            }
        });

        // Add to cart button click listener
        holder.btnAddToCart.setOnClickListener(v -> {
            addToCart(product);
        });
    }

    private void addToCart(Product product) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(context, "Please login to add items to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // Create a map of product data to save to Firestore
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productId", product.getId());
        cartItem.put("name", product.getName());
        cartItem.put("price", product.getPrice());
        cartItem.put("imageUrl", product.getImageUrl());
        cartItem.put("quantity", 1); // Default quantity
        cartItem.put("timestamp", System.currentTimeMillis());

        // Check if the product already exists in the cart
        db.collection("carts")
                .document(userId)
                .collection("items")
                .whereEqualTo("productId", product.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Product doesn't exist in cart, add it
                            db.collection("carts")
                                    .document(userId)
                                    .collection("items")
                                    .add(cartItem)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Product exists, update quantity
                            DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                            docRef.update("quantity", task.getResult().getDocuments().get(0).getLong("quantity") + 1)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Item quantity updated in cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Error checking cart: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView tvRating;
        Button btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            tvRating = itemView.findViewById(R.id.tv_rating);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}