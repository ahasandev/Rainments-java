package com.kingtech.rainment.model;



public class Category {
    private String name,icon,color,bref;
    private int id;

    public Category(String name, String icon, String color, String bref, int id) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.bref = bref;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBref() {
        return bref;
    }

    public void setBref(String bref) {
        this.bref = bref;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
