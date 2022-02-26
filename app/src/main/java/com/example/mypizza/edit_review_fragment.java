package com.example.mypizza;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.squareup.picasso.Picasso;

public class edit_review_fragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    View view;
    TextView email_et;
    TextView review_et;
    Button save_btn;
    Button cancel_btn;
    Button delete_btn;
    ImageButton img_btn;
    View progBar;
    Review review;
    Bitmap bitmap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.edit_review_fragment, container, false);
        email_et = view.findViewById(R.id.edit_review_mail_et);
        review_et = view.findViewById(R.id.edit_review_review_et);
        save_btn = view.findViewById(R.id.edit_review_save_btn);
        cancel_btn = view.findViewById(R.id.edit_review_cancel_btn);
        delete_btn = view.findViewById(R.id.edit_review_delete_btn);
        img_btn = view.findViewById(R.id.edit_review_img);
        progBar = view.findViewById(R.id.edit_review_progBar);
        String reviewID = edit_review_fragmentArgs.fromBundle(getArguments()).getReviewID();
        Model.instance.getReviewByID(reviewID, (review) -> {
            review.setReviewID(reviewID);
            updateDisplay(review);
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        progBar.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == android.app.Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            img_btn.setImageBitmap(bitmap);
        }
    }

    private void updateDisplay(Review review) {
        progBar.setVisibility(View.VISIBLE);
        if (review != null) {
            this.review = review;
            email_et.setText(review.getWriterEmail());
            email_et.setEnabled(false);
            review_et.setText(review.getReview());
            if (review.getImgUrl() != null) {
                Picasso.get()
                        .load(review.getImgUrl())
                        .placeholder(R.drawable.pizza)
                        .into(img_btn);
            }
        }
        progBar.setVisibility(View.GONE);
    }

    private void save() {
        save_btn.setEnabled(false);
        cancel_btn.setEnabled(false);
        delete_btn.setEnabled(false);
        review.setReview(review_et.getText().toString());
        updateReviewText();
        updateReviewImg();
        Navigation.findNavController(view).navigateUp();
        Model.instance.reloadReviewsListByMail(review.getWriterEmail());
    }




    private void delete() {
        save_btn.setEnabled(false);
        cancel_btn.setEnabled(false);
        delete_btn.setEnabled(false);
        review.setDeleted(true);
        review.setLastUpdated(System.currentTimeMillis());
        Model.instance.deleteReview(review, new Model.DeleteReviewListener() {
            @Override
            public void onComplete() {
                Navigation.findNavController(view).navigateUp();
                Model.instance.reloadReviewsListByMail(review.getWriterEmail());
            }
        });
    }

    private void updateReviewText() {
        Model.instance.updateReview(review, new Model.UpdateReviewListener() {
            @Override
            public void onComplete(Review r) {
                Model.instance.reloadReviewsListByMail(review.getWriterEmail());
            }
        });
    }
    private void updateReviewImg() {
        if (bitmap == null) {
            Log.d("TAG", "bitmap is null");
        } else {
            Model.instance.UpdateReviewImg(bitmap, review.getReviewID(), new Model.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    review.setImgUrl(url);
                    Model.instance.updateReviewUrl(review,review.getImgUrl(), new Model.UpdateReviewUrlListener() {
                        @Override
                        public void onComplete() {
                        }
                    });
                }
            });
        }
        Model.instance.reloadReviewsListByMail(review.getWriterEmail());
    }

}


