package com.example.mypizza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Review;
import com.squareup.picasso.Picasso;

public class edit_review_fragment extends Fragment {
    View view;
    TextView email_et;
    TextView review_et;
    Button save_btn;
    Button cancel_btn;
    Button delete_btn;
    ImageView img_btn;
    View progBar;
    Review review;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.edit_review_fragment, container, false);
        email_et=view.findViewById(R.id.edit_review_mail_et);
        review_et=view.findViewById(R.id.edit_review_review_et);
        save_btn=view.findViewById(R.id.edit_review_save_btn);
        cancel_btn=view.findViewById(R.id.edit_review_cancel_btn);
        delete_btn=view.findViewById(R.id.edit_review_delete_btn);
        img_btn=view.findViewById(R.id.edit_review_img);
        progBar=view.findViewById(R.id.edit_review_progBar);
        String reviewID = edit_review_fragmentArgs.fromBundle(getArguments()).getReviewID();
        Model.instance.getReviewByID(reviewID, (review)->{
            updateDisplay(review);
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        progBar.setVisibility(View.VISIBLE);

        return view;
    }

    private void updateDisplay(Review review) {
        this.review = review;
        email_et.setText(review.getWriterEmail());
        email_et.setEnabled(false);
        review_et.setText(review.getReview());
//        if (review.getAvatarUtl() != null) {
//            Picasso.get()
//                    .load(review.getAvatarUtl())
//                    .placeholder(R.drawable.avatar)
//                    .into(img_btn);
//        }
        progBar.setVisibility(View.GONE);
    }

    private void save(){
        review.setReview(review_et.getText().toString());
        Log.d("TAG",review.getReview());
        Model.instance.updateReview(review, new Model.UpdateReviewListener() {
            @Override
            public void onComplete(Review r) {
                Navigation.findNavController(view).navigateUp();
                Model.instance.reloadReviewsListByMail(review.getWriterEmail());
            }
        });
    }


}