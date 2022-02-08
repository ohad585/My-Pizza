package com.example.mypizza.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.mypizza.MyApplication;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Review {
    final static String REVIEW = "review";
    final static String WRITEREMAIL = "writerEmail";
    final static String PIZZAID = "pizzaID";
    final static String REVIEWUPDATE = "REVIEW_LAST_UPDATE";
    final static String LAST_UPDATED = "LAST_UPDATE";

    @PrimaryKey
    @NonNull
    private String review;
    private String writerEmail;
    private String pizzaID;
    Long lastUpdated = new Long(0);
    private String ReviewID;


    public Review(String review,String writer,String pizza){
        this.review=review;
        this.writerEmail = writer;
        this.pizzaID = pizza;
    }

    public Review(){}

    public String getPizzaID() {
        return pizzaID;
    }

    public String getReview() {
        return review;
    }

    public String getWriterEmail() {
        return writerEmail;
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
        return json;
    }

    static Review fromJson(Map<String,Object> json){
        Log.d("TAG8","R from J");
        String review = (String)json.get(REVIEW);
        if (review == null){
            return null;
        }
        String writerE = (String)json.get(WRITEREMAIL);
        String pizzaID = (String)json.get(PIZZAID);
        Review r = new Review(review,writerE,pizzaID);
        return r;
    }

    static Long getLocalLastUpdated(){
        Log.d("TAG3", "setLocalLastUpdated: "+ MyApplication.getContext());
        Long localLastUpdate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong(REVIEWUPDATE,0);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date){
        Log.d("TAG3", "setLocalLastUpdated: "+MyApplication.getContext());
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
}
