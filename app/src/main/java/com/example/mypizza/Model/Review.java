package com.example.mypizza.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.mypizza.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Review {
    final static String REVIEW = "review";
    final static String WRITEREMAIL = "writerEmail";
    final static String PIZZAID = "pizzaID";
    final static String REVIEWUPDATE = "REVIEW_LAST_UPDATE";
    final static String LAST_UPDATED = "LAST_UPDATE";
    //final static String REVIEW_ID = "REVIEW_ID";
    final static String IS_DELETED="IS_DELETED";
    final static String IMGURL = "imgUrl";



    @PrimaryKey
    @NonNull
    private String ReviewID;
    private String review;
    private String writerEmail;
    private String pizzaID;
    Long lastUpdated = new Long(0);
    private boolean isDeleted;
    private String imgUrl;



    public Review(String review,String writer,String pizza){
        this.review=review;
        this.writerEmail = writer;
        this.pizzaID = pizza;
        //this.ReviewID=writerEmail+pizza;
        isDeleted=false;
        imgUrl = "";
    }

    public Review(){}

    @Override
    public String toString() {
        return "Review{" +
                "ReviewID='" + ReviewID + '\'' +
                ", review='" + review + '\'' +
                ", writerEmail='" + writerEmail + '\'' +
                ", pizzaID='" + pizzaID + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", isDeleted=" + isDeleted +
                '}';
    }

    public String getPizzaID() {
        return pizzaID;
    }

    public String getReview() {
        return review;
    }

    public String getWriterEmail() {
        return writerEmail;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setPizzaID(String pizzaID) {
        this.pizzaID = pizzaID;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setWriterEmail(String writerEmail) {
        this.writerEmail = writerEmail;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(REVIEW, getReview());
        json.put(WRITEREMAIL, getWriterEmail());
        json.put(PIZZAID,getPizzaID());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        //json.put(REVIEW_ID,getReviewID());
        json.put(IS_DELETED,isDeleted());
        json.put(IMGURL,getImgUrl());


        return json;
    }

    static Review fromJson(Map<String,Object> json){
        String review = (String)json.get(REVIEW);
        if (review == null){
            return null;
        }
        String imgUrl = (String)json.get(IMGURL);
        boolean isDeleted= (boolean)json.get(IS_DELETED);
        String writerE = (String)json.get(WRITEREMAIL);
        String pizzaID = (String)json.get(PIZZAID);
        Timestamp ts =(Timestamp)json.get(LAST_UPDATED);
        Review r = new Review(review,writerE,pizzaID);
        r.setLastUpdated(new Long(ts.getSeconds()));
        r.setDeleted(isDeleted);
        r.setImgUrl(imgUrl);
        return r;
    }

    static Long getLocalLastUpdated(){
        Long localLastUpdate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong(REVIEWUPDATE,0);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date){
        SharedPreferences.Editor editor = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong(REVIEWUPDATE,date);
        editor.commit();
        Log.d("TAG", "new lud " + date);
    }

    public String getReviewID() {
        return ReviewID;
    }

    public void setReviewID(String reviewID) {
        ReviewID = reviewID;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
