package com.example.mypizza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.User;

import org.w3c.dom.Text;

public class personal_page_costumer_fragment extends Fragment {
    User user;
    View view;
    View progBar;
    TextView email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.personal_page_costumer__fragment, container, false);
        email = view.findViewById(R.id.personal_page_costumer_enailAddress_tv);
        progBar = view.findViewById(R.id.personal_page_costumer_progBar);
        progBar.setVisibility(View.VISIBLE);
        Model.instance.getCurrentUser(new Model.getCurrentUserListener() {
            @Override
            public void onComplete(User user) {
                email.setText(user.getEmail());
                progBar.setVisibility(View.INVISIBLE);
            }
        });


        return view;
    }
}