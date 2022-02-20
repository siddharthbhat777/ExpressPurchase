package com.project.myapplication.Model;

public class PaidModel {
    String name,no_of_items,price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(String no_of_items) {
        this.no_of_items = no_of_items;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public PaidModel() {
    }

    public PaidModel(String name, String no_of_items, String price) {
        this.name = name;
        this.no_of_items = no_of_items;
        this.price = price;
    }
}
