package com.example.saleservice.classes;

public class userdetails {
    Integer id;
    String email;
    String uid;
    String pass;
    Integer schid;

    public userdetails(Integer id, String email, String uid, String pass, Integer schid) {
        this.id = id;
        this.email = email;
        this.uid = uid;
        this.pass = pass;
        this.schid = schid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Integer getSchid() {
        return schid;
    }

    public void setSchid(Integer schid) {
        this.schid = schid;
    }
}
