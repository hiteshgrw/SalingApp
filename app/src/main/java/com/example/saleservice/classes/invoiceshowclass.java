package com.example.saleservice.classes;

public class invoiceshowclass {
    String name;
    String cmpname;
    Integer quanity;
    Double price;
    Double dis;
    Double total;

    public invoiceshowclass(String name, String cmpname, Integer quanity, Double price, Double dis, Double total) {
        this.name = name;
        this.cmpname = cmpname;
        this.quanity = quanity;
        this.price = price;
        this.dis = dis;
        this.total = total;
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

    public Integer getQuanity() {
        return quanity;
    }

    public void setQuanity(Integer quanity) {
        this.quanity = quanity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDis() {
        return dis;
    }

    public void setDis(Double dis) {
        this.dis = dis;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
