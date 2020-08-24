package com.deeptha.cashier;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Record {
    private SimpleIntegerProperty barcode;
    private SimpleStringProperty product;
    private SimpleDoubleProperty price;
    private SimpleIntegerProperty stock;
    private SimpleIntegerProperty quantity;
    private SimpleDoubleProperty totalPrice;

    //for billing
    Record(int barcode, String product, double price, int stock, int quantity, double totalPrice){
        this.barcode = new SimpleIntegerProperty(barcode);
        this.product = new SimpleStringProperty(product);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.totalPrice = new SimpleDoubleProperty(totalPrice);
    }

    //for showing products
    Record(int barcode, String product, double price, int stock){
        this.barcode = new SimpleIntegerProperty(barcode);
        this.product = new SimpleStringProperty(product);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
    }

    //for checking the stocks
    Record(int barcode, String product, int stock){
        this.barcode = new SimpleIntegerProperty(barcode);
        this.product = new SimpleStringProperty(product);
        this.stock = new SimpleIntegerProperty(stock);
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public int getBarcode() {
        return barcode.get();
    }

    public void setBarcode(int barcode) {
        this.barcode.set(barcode);
    }

    public String getProduct() {
        return product.get();
    }

    public void setProduct(String product) {
        this.product.set(product);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }
}
