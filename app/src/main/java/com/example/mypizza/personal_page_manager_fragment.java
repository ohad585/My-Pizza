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

import com.example.mypizza.Model.Pizza;
import com.squareup.picasso.Picasso;

public class personal_page_manager_fragment extends Fragment {
    Button addPizzaBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.personal_page_manager__fragment, container, false);
        addPizzaBtn= view.findViewById(R.id.personal_page_manager_addPizza_btn);
        addPizzaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_personal_page_manager_fragment_to_add_pizza_fragment);
            }
        });


        return view;
    }

}