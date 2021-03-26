package com.example.saleservice.classes;

public class schoolusername {
    String uname;
    String school;

    public schoolusername(String uname, String school) {
        this.uname = uname;
        this.school = school;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
