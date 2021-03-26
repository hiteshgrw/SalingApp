package com.example.saleservice.classes;

public class schoolmgmtclass {
    private String name;
    private Integer id;

    public schoolmgmtclass() {
    }

    public schoolmgmtclass(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
