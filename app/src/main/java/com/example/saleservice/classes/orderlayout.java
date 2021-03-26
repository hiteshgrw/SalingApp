package com.example.saleservice.classes;

public class orderlayout {
    String name;
    String cmpname;
    Integer quantity;

    public orderlayout(String name, String cmpname, Integer quantity) {
        this.name = name;
        this.cmpname = cmpname;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmpname() {
        return cmpname;
    }

    public void setCmpname(String cmpname) {
        this.cmpname = cmpname;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
