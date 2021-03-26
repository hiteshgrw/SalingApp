package com.example.saleservice.classes;

public class barclass {
    String name;
    Double pr,sale;

    public barclass(String name, Double pr, Double sale) {
        this.name = name;
        this.pr = pr;
        this.sale = sale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPr() {
        return pr;
    }

    public void setPr(Double pr) {
        this.pr = pr;
    }

    public Double getSale() {
        return sale;
    }

    public void setSale(Double sale) {
        this.sale = sale;
    }
}
