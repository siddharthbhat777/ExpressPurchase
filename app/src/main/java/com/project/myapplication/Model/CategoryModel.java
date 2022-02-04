package com.project.myapplication.Model;

public class CategoryModel {
    String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryModel(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryModel() {
    }
}
