package com.project.myapplication.Model;

public class ViewOrderModel {
    String invoice_number, number_of_products, total_price, item_name;
    long date_of_delivery;
    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getNumber_of_products() {
        return number_of_products;
    }

    public void setNumber_of_products(String number_of_products) {
        this.number_of_products = number_of_products;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public long getDate_of_delivery() {
        return date_of_delivery;
    }

    public void setDate_of_delivery(long date_of_delivery) {
        this.date_of_delivery = date_of_delivery;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public ViewOrderModel() {
    }

    public ViewOrderModel(String invoice_number, String number_of_products, String total_price, long date_of_delivery, String item_name) {
        this.invoice_number = invoice_number;
        this.number_of_products = number_of_products;
        this.total_price = total_price;
        this.date_of_delivery = date_of_delivery;
        this.item_name = item_name;
    }
}