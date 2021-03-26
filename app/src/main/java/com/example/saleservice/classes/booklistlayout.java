package com.example.saleservice.classes;

public class booklistlayout {
    String name;
    String cname;
    Double price;
    Integer quantity;

    public booklistlayout(String name, String cname, Double price, Integer quantity) {
        this.name = name;
        this.cname = cname;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
