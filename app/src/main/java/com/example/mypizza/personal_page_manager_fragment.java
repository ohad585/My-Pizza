package com.example.mypizza;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;
import com.example.mypizza.Model.User;
import com.squareup.picasso.Picasso;

public class personal_page_manager_fragment extends Fragment {
    Button addPizzaBtn;
    Button watchAndRemovePizza;

    User currentUser;
    TextView email;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.personal_page_manager__fragment, container, false);
        addPizzaBtn= view.findViewById(R.id.personal_page_manager_addPizza_btn);
        watchAndRemovePizza= view.findViewById(R.id.personal_page_manager_watch_and_edit_btn);
        email = view.findViewById(R.id.personal_page_manager_enailAddress_tv);
//        progBar = view.findViewById(R.id.personal_page_costumer_progBar);
//        progBar.setVisibility(View.VISIBLE);
        addPizzaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_personal_page_manager_fragment_to_add_pizza_fragment);
            }
        });
        watchAndRemovePizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_personal_page_manager_fragment_to_watch_all_reviews_manager_fragment);
            }
        });
        Model.instance.getCurrentUser(new Model.getCurrentUserListener() {
            @Override
            public void onComplete(User user) {
                currentUser=user;
                email.setText(user.getEmail());
            }
        });


        return view;
    }

}