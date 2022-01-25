package com.example.mypizza.Model;


import java.util.HashMap;
import java.util.Map;

public class User {
    private String password;
    private String phoneNum;
    private String email;

    public User() {}

    public User(String em,String pass, String phone) {
        password=pass;
        phoneNum=phone;
        email=em;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }


    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("email", getEmail());
        json.put("password", getPassword());
        json.put("phone", getPhoneNum());
        return json;
    }

    static User fromJson(Map<String,Object> json){
        String email = (String)json.get("email");
        if (email == null){
            return null;
        }
        String password = (String)json.get("password");
        String phone = (String)json.get("phone");
        User user = new User(email,password,phone);
        return user;
    }










}
