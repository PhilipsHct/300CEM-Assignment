package com.example.assignment_footage.models;

import java.sql.Date;

public class Footage {
    private int id;
    private String name;
    private Date footageDate;
    private String remark;

    public Footage(){}

    public Footage(int id, String name, Date footageDate, String remark) {
        this.id = id;
        this.name = name;
        this.footageDate = footageDate;
        this.remark = remark;
    }

    public Footage(String name, Date footageDate, String remark) {
        this.name = name;
        this.footageDate = footageDate;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFootageDate() {
        return footageDate;
    }

    public void setFootageDate(Date footageDate) {
        this.footageDate = footageDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
