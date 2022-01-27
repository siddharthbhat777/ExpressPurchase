package com.project.myapplication;

public class ExpressPurchaseModel {
    private String itemHeading;
    private String itemPrice;
    private String itemImage;
    private String gender;
    private String age;
    private String category;

    ExpressPurchaseModel(){}

    public ExpressPurchaseModel(String itemHeading, String itemPrice, String itemImage, String gender, String age, String category) {
        this.itemHeading = itemHeading;
        this.itemPrice = itemPrice;
        this.itemImage = itemImage;
        this.gender = gender;
        this.age = age;
        this.category = category;
    }

    public String getItemHeading() {
        return itemHeading;
    }

    public void setItemHeading(String itemHeading) {
        this.itemHeading = itemHeading;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
