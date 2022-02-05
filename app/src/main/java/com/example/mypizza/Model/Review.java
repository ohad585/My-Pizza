package com.example.mypizza.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class Review {
    final static String REVIEW = "review";
    final static String WRITEREMAIL = "writerEmail";
    final static String PIZZAID = "pizzaID";

    private String review;
    private String writerEmail;
    private String pizzaID;

    public Review(String review,String writer,String pizza){
        this.review=review;
        this.writerEmail = writer;
        this.pizzaID = pizza;
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

    public void setPizzaID(String pizzaID) {
        this.pizzaID = pizzaID;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setWriterEmail(String writerEmail) {
        this.writerEmail = writerEmail;
    }

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(REVIEW, getReview());
        json.put(WRITEREMAIL, getWriterEmail());
        json.put(PIZZAID,getPizzaID());
        return json;
    }

    static Review fromJson(Map<String,Object> json){
        String review = (String)json.get(REVIEW);
        if (review == null){
            return null;
        }
        String writerE = (String)json.get(WRITEREMAIL);
        String pizzaID = (String)json.get(PIZZAID);
        Review r = new Review(review,writerE,pizzaID);
        return r;
    }

}
