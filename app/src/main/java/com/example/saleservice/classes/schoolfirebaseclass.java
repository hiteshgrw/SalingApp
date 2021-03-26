package com.example.saleservice.classes;

public class schoolfirebaseclass {
    String name;
    Integer id;

    public schoolfirebaseclass() {
        name = null;
        id = 0;
    }

    public schoolfirebaseclass(String name, Integer id) {
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
