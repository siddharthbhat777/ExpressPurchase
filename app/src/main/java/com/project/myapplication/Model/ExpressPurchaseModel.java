package com.project.myapplication.Model;

public class ExpressPurchaseModel {
    private String itemName;
    private int itemPrice;
    private String itemImage;
    private String gender;
    private int age;
    private String category;
    private String salesman;
    private String description;
    private String search;

    ExpressPurchaseModel(){}

    public ExpressPurchaseModel(String itemName, int itemPrice, String itemImage, String gender, int age, String category, String salesman, String description, String search) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemImage = itemImage;
        this.gender = gender;
        this.age = age;
        this.category = category;
        this.salesman = salesman;
        this.description = description;
        this.search = search;
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

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
