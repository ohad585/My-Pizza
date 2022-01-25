package com.example.mypizza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.User;

public class login_page_fragment extends Fragment {
    TextView email;
    TextView password;
    String uEmail;
    String uPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.login_page_fragment, container, false);
        email =view.findViewById(R.id.login_email_et);
        password =view.findViewById(R.id.login_password_et);
        Button loginBtn=view.findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uEmail = email.getText().toString();
                uPass= password.getText().toString();
                loginUser();
            }
        });
        return view;
    }
    public void loginUser(){
        Log.d("TAG", "loginUser: "+uEmail);
        Model.instance.signInWithEmailPass(uEmail,uPass,(User user, boolean success)->{
            if(success){
                Log.d("TAG", "loginUser: "+user.getEmail()+" "+user.getUid());
            }
            else {
                Log.d("TAG", "loginUser: Failed");
            }
        });
    }
}