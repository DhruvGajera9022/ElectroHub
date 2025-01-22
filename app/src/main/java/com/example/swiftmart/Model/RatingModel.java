package com.example.swiftmart.Model;

public class RatingModel {

    String pid, rating, rid, uid;

    public RatingModel() {
    }

    public RatingModel(String pid, String rating, String rid, String uid) {
        this.pid = pid;
        this.rating = rating;
        this.rid = rid;
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
