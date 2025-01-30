package com.example.swiftmart.Model;

import java.util.List;

public class ProductModel {

    String pid, name, description, price, company, category, oid, totalAmount;
    private List<String> imgurls;
    private boolean isWishlisted;
    private String qty;
    private double totalPrice;
    private double averageRating;
    private int reviewCount;

    public ProductModel(){

    }

    public ProductModel(
            String pid,
            String name,
            String description,
            String price,
            String company,
            String category,
            List<String> imgurls,
            boolean isWishlisted,
            String oid,
            String qty,
            double totalPrice,
            String totalAmount,
            double averageRating,
            int reviewCount
    ){
        this.pid = pid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.company = company;
        this.category = category;
        this.imgurls = imgurls;
        this.isWishlisted = isWishlisted;
        this.oid = oid;
        this.qty = qty;
        this.totalPrice = totalPrice;
        this.totalAmount = totalAmount;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public List<String> getImgurls() {
        return imgurls;
    }

    public void setImgurls(List<String> imgurls) {
        this.imgurls = imgurls;
    }

    public boolean isWishlisted() {
        return isWishlisted;
    }

    public void setWishlisted(boolean wishlisted) {
        isWishlisted = wishlisted;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) { // Add this setter
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
