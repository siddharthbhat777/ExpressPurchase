package com.project.myapplication.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CartModel  {
    private String itemName;
    private int itemPrice;
    private String itemImage;
    int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemImage() {
        return itemImage;
    }

    public CartModel() {
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }
}
