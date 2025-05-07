package com.example.unibites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;
    private OnCartItemActionListener listener;
    private NumberFormat currencyFormat;

    public interface OnCartItemActionListener {
        void onQuantityChanged(CartItem cartItem, int position, int newQuantity);
        void onRemoveItem(CartItem cartItem, int position);
    }

    public CartAdapter(Context context, List<CartItem> cartItemList, OnCartItemActionListener listener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        // Set item details
        holder.tvCartItemName.setText(cartItem.getName());
        holder.tvCartItemPrice.setText(currencyFormat.format(cartItem.getPrice()));
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.tvCartItemTotal.setText(currencyFormat.format(cartItem.getTotalPrice()));

        // Load image from URL using Glide
        if (cartItem.getImageUrl() != null && !cartItem.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(cartItem.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.chilli_biryani)
                            .error(R.drawable.chilli_biryani))
                    .into(holder.ivCartItemImage);
        } else {
            holder.ivCartItemImage.setImageResource(R.drawable.chilli_biryani);
        }

        // Setup quantity controls
        holder.btnDecreaseQuantity.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity >= 1) {
                if (listener != null) {
                    listener.onQuantityChanged(cartItem, holder.getAdapterPosition(), newQuantity);
                }
            }
        });

        holder.btnIncreaseQuantity.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            if (newQuantity <= 99) { // Set a reasonable maximum quantity
                if (listener != null) {
                    listener.onQuantityChanged(cartItem, holder.getAdapterPosition(), newQuantity);
                }
            }
        });

//        holder.btnRemoveItem.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onRemoveItem(cartItem, holder.getAdapterPosition());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItemList = newCartItems;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCartItemImage;
        TextView tvCartItemName;
        TextView tvCartItemPrice;
        TextView tvQuantity;
        TextView tvCartItemTotal;
        ImageButton btnDecreaseQuantity;
        ImageButton btnIncreaseQuantity;
        ImageButton btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartItemImage = itemView.findViewById(R.id.itemImageView);
            tvCartItemName = itemView.findViewById(R.id.itemNameTextView);
            tvCartItemPrice = itemView.findViewById(R.id.itemDetailsTextView);
            tvQuantity = itemView.findViewById(R.id.quantityTextView);
            tvCartItemTotal = itemView.findViewById(R.id.priceTextView);
            btnDecreaseQuantity = itemView.findViewById(R.id.decreaseButton);
            btnIncreaseQuantity = itemView.findViewById(R.id.increaseButton);
        }
    }
}