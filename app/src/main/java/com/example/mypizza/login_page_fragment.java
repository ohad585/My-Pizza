package com.example.mypizza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class login_page_fragment extends Fragment {
    TextView userName;
    TextView userPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.login_page_fragment, container, false);
        userName=view.findViewById(R.id.login_email_et);
        userPass=view.findViewById(R.id.login_password_et);
        
        Button loginBtn=view.findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkUser(uName,uPass);
            }
        });
        return view;
    }
}