package com.example.saleservice.classes;

public class invlstsetmapper {
    String name;
    String items;
    Double tprice;

    public invlstsetmapper(String name, String items, Double tprice) {
        this.name = name;
        this.items = items;
        this.tprice = tprice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public Double getTprice() {
        return tprice;
    }

    public void setTprice(Double tprice) {
        this.tprice = tprice;
    }
}
