package com.example.mypizza.Model;


import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String password;
    private String phoneNum;
    private String email;
    private String uid;
    private boolean admin;

    public User() {}

    public User(String em,String pass, String phone,boolean ad,String id) {
        password=pass;
        phoneNum=phone;
        email=em;
        uid=id;
        admin=ad;
    }

    public User(FirebaseUser user) {
        this.email=user.getEmail();
        this.password="Classified";
        this.phoneNum=user.getPhoneNumber();;
        this.uid=user.getUid();
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

    public String getUid() { return uid; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setUid(String uid) { this.uid = uid; }

    public boolean isAdmin() { return admin; }

    public void setAdmin(boolean Admin) {
       this.admin=Admin;
    }



    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("email", getEmail());
        json.put("password", getPassword());
        json.put("phone", getPhoneNum());
        json.put("uid",getUid());
        json.put("admin",isAdmin());
        return json;
    }


    static User fromJson(Map<String,Object> json){
        String email = (String)json.get("email");
        if (email == null){
            return null;
        }
        String password = (String)json.get("password");
        String phone = (String)json.get("phone");
        String uid = (String)json.get("uid");
        boolean admin= (boolean)json.get("admin");
        User user = new User(email,password,phone,admin,uid);
        return user;
    }










}