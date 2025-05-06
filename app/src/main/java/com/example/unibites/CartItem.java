package com.example.unibites;

public class CartItem {
    private String name;
    private double price;
    private int quantity;
    private int imageResource;

    public CartItem(String name, double price, int imageResource) {
        this.name = name;
        this.price = price;
        this.quantity = 1;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageResource() {
        return imageResource;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
} 