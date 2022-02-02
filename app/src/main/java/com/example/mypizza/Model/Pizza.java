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
public class Pizza {
    final static String DESC = "description";
    final static String IMGURL = "imgUrl";
    final static String PRICE = "price";
    final static String PIZZASUPDATE = "PIZZAS_LAST_UPDATE";
    final static String LAST_UPDATED = "LAST_UPDATE";

    @PrimaryKey
    @NonNull
    private String description;
    private String uid;
    private String price;
    private String imgUrl;
    Long lastUpdated = new Long(0);



    public Pizza(){}
    public Pizza(String p,String d) {
        price=p;
        description=d;
        uid="";
        imgUrl="";
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(PRICE, getPrice());
        json.put(DESC, getDescription());
        json.put(IMGURL,getImgUrl());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
    }

    static Pizza fromJson(Map<String,Object> json){
        String description = (String)json.get(DESC);
        if (description == null){
            return null;
        }
        String price = (String)json.get(PRICE);
        String imgUrl = (String)json.get(IMGURL);
        Timestamp ts =(Timestamp)json.get(LAST_UPDATED);
        Pizza pizza = new Pizza(price,description);
        pizza.setImgUrl(imgUrl);
        pizza.setLastUpdated(new Long(ts.getSeconds()));
        return pizza;
    }
    static Long getLocalLastUpdated(){
        Log.d("TAG3", "setLocalLastUpdated: "+MyApplication.getContext());
        Long localLastUpdate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong(PIZZASUPDATE,0);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date){
        Log.d("TAG3", "setLocalLastUpdated: "+MyApplication.getContext());
        SharedPreferences.Editor editor = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong(PIZZASUPDATE,date);
        editor.commit();
        Log.d("TAG", "new lud " + date);
    }

}