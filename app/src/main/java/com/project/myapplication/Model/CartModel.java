package com.project.myapplication.Model;

public class CartModel {
    String itemName;
    int itemPrice;
    int newprice;
    String itemImage;
    long time;
    int quantity;
    String address;

    public CartModel(String itemName, int itemPrice, int newprice, String itemImage, long time, int quantity , String address) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.newprice = newprice;
        this.itemImage = itemImage;
        this.time = time;
        this.quantity = quantity;
        this.address = address;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getNewprice() {
        return newprice;
    }

    public void setNewprice(int newprice) {
        this.newprice = newprice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
