package com.example.mypizza.Model;

import java.util.HashMap;
import java.util.Map;

public class Pizza {
    private String price;
    private String description;
    private String ImgUrl;


    public Pizza(String p,String d) {
        price=p;
        description=d;
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
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }


    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("price", getPrice());
        json.put("description", getDescription());
        json.put("imgUrl",getImgUrl());
        return json;
    }

    static Pizza fromJson(Map<String,Object> json){
        String description = (String)json.get("description");
        if (description == null){
            return null;
        }
        String price = (String)json.get("price");
        String imgUrl = (String)json.get("imgUrl");
        Pizza pizza = new Pizza(price,description);
        pizza.setImgUrl(imgUrl);
        return pizza;
    }

}
