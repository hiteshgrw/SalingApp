package com.example.saleservice.classes;

public class invoiceamountmap {
    Integer invno;
    String schoolname;
    String classname;
    Double amount;

    public invoiceamountmap(Integer invno, String schoolname, String classname, Double amount) {
        this.invno = invno;
        this.schoolname = schoolname;
        this.classname = classname;
        this.amount = amount;
    }

    public Integer getInvno() {
        return invno;
    }

    public void setInvno(Integer invno) {
        this.invno = invno;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
