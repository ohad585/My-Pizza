package com.example.mypizza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;
import com.example.mypizza.Model.Review;
import com.example.mypizza.Model.User;
import com.squareup.picasso.Picasso;

public class write_review_fragment extends Fragment {

    View view;
    TextView tops;
    TextView price;
    TextView review;
    ImageView img;
    View progBar;
    Button saveBtn;
    Button cnclBtn;

    Pizza p;
    User u;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.write_review, container, false);
        tops = view.findViewById(R.id.write_review_actual_tops_tv);
        price = view.findViewById(R.id.write_review_actual_price_tv);
        review = view.findViewById(R.id.write_review_et);
        img = view.findViewById(R.id.write_review_img);
        progBar = view.findViewById(R.id.write_review_progBar);
        progBar.setVisibility(View.VISIBLE);

        String pid = pizza_details_fragmentArgs.fromBundle(getArguments()).getPid();
        Model.instance.getPizzaByDescription(pid,(pizza -> {
            p=pizza;
            update();
        }));
        Model.instance.getCurrentUser(new Model.getCurrentUserListener() {
            @Override
            public void onComplete(User user) {
                u=user;
            }
        });
        saveBtn = view.findViewById(R.id.write_review_save_btn);
        cnclBtn = view.findViewById(R.id.write_review_cncl_btn);

        cnclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                write_review_fragmentDirections.ActionWriteReviewFragmentToPizzaDetailsFragment action = write_review_fragmentDirections.actionWriteReviewFragmentToPizzaDetailsFragment(p.getDescription());
                Navigation.findNavController(view).navigate(action);
            }
        });

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

    void save(){
        String rev = review.getText().toString();
        Review r = new Review(rev,u.getEmail(),p.getDescription());
        Log.d("TAG", "save: "+rev+" user:"+u.getEmail());
        Model.instance.addReview(r, new Model.AddReviewListener() {
            @Override
            public void onComplete( String reviewID) {
                Log.d("TAG", "onComplete: Review saved");
                r.setReviewID(reviewID);
            }
        });

    }
}