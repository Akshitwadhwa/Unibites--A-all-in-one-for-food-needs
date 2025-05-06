package com.example.unibites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onQuantityChanged(int position, int newQuantity);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));
        
        // Set item image
        holder.itemImage.setImageResource(item.getImageResource());

        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            holder.quantityText.setText(String.valueOf(newQuantity));
            if (listener != null) {
                listener.onQuantityChanged(position, newQuantity);
            }
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                holder.quantityText.setText(String.valueOf(newQuantity));
                if (listener != null) {
                    listener.onQuantityChanged(position, newQuantity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemPrice;
        TextView quantityText;
        ImageButton increaseButton;
        ImageButton decreaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
        }
    }
} 