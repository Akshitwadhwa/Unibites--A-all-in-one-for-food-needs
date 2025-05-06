package com.example.unibites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> productList;
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.productList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, context, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> newProductList) {
        this.productList = new ArrayList<>(newProductList);
        notifyDataSetChanged();
    }

    public void addProduct(Product product) {
        productList.add(product);
        notifyItemInserted(productList.size() - 1);
    }

    public void removeProduct(int position) {
        if (position >= 0 && position < productList.size()) {
            productList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }

        public void bind(Product product, Context context, OnProductClickListener listener) {
            productName.setText(product.getName());
            productPrice.setText(context.getString(R.string.price_format, product.getPrice()));

            // Load image using Glide
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.unilogo)
                    .error(R.drawable.unilogo)
                    .into(productImage);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}