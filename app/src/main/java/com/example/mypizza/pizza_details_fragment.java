package com.example.mypizza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;
import com.squareup.picasso.Picasso;

public class pizza_details_fragment extends Fragment {
    View view;
    Pizza p;

    TextView price;
    TextView tops;
    ImageView img;
    View progBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.pizza_details, container, false);
        String pid = pizza_details_fragmentArgs.fromBundle(getArguments()).getPid();
        Model.instance.getPizzaByDescription(pid,(pizza -> {
            p=pizza;
            update();
        }));
        price = view.findViewById(R.id.pizza_details_actual_price_tv);
        tops = view.findViewById(R.id.pizza_details_actual_toppings_tv);
        img = view.findViewById(R.id.pizza_details_img);
        progBar = view.findViewById(R.id.pizza_details_progBar);

        progBar.setVisibility(View.VISIBLE);
        return view;
    }
    void update(){
        price.setText(p.getPrice());
        tops.setText(p.getDescription());
        String url = p.getImgUrl();
        if(url != null){
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.avatar)
                    .into(img);

        }
        progBar.setVisibility(View.INVISIBLE);
    }
}