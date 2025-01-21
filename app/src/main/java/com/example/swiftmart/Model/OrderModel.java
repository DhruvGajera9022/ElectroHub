package com.example.swiftmart.Model;

import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    String pid, name, description,price, qty, company, category, oid, date, time;
    private ArrayList<String> imgurls;

    public OrderModel() {
    }

    public OrderModel(String pid, String name, String description, String company, String category, String oid, String date, String time, ArrayList<String> imgurls, String qty, String price) {
        this.pid = pid;
        this.name = name;
        this.description = description;
        this.company = company;
        this.category = category;
        this.oid = oid;
        this.date = date;
        this.time = time;
        this.imgurls = imgurls;
        this.qty = qty;
        this.price = price;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getImgurls() {
        return imgurls;
    }

    public void setImgurls(ArrayList<String> imgurls) {
        this.imgurls = imgurls;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
