package com.example.saleservice.classes;

public class stockclass {
    String name;
    Double price;
    Integer pquan;
    Integer squan;

    public stockclass(String name, Double price, Integer pquan, Integer squan) {
        this.name = name;
        this.price = price;
        this.pquan = pquan;
        this.squan = squan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getPquan() {
        return pquan;
    }

    public void setPquan(Integer pquan) {
        this.pquan = pquan;
    }

    public Integer getSquan() {
        return squan;
    }

    public void setSquan(Integer squan) {
        this.squan = squan;
    }
}
