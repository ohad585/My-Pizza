package com.example.mypizza;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;
import com.example.mypizza.Model.Review;
import com.example.mypizza.Model.User;
import com.squareup.picasso.Picasso;

public class write_review_fragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESAULT_SUCCESS = 0;

    View view;
    TextView tops;
    TextView price;
    TextView review;
    ImageView img;
    View progBar;
    Button saveBtn;
    Button cnclBtn;
    ImageButton addReviewPhoto;
    Bitmap bitmap;

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
        addReviewPhoto = view.findViewById(R.id.write_review_pizza_img_btn);
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
        addReviewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {//img was taken and return to code
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
        }
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
        if(bitmap == null){
            //Please add pizza photo first
            return;
        }
        String rev = review.getText().toString();
        Review r = new Review(rev,u.getEmail(),p.getDescription());
        Log.d("TAG", "save: "+rev+" user:"+u.getEmail());
        Model.instance.addReview(r, new Model.AddReviewListener() {
            @Override
            public void onComplete( String reviewID) {
                Log.d("TAG", "onComplete: Review saved");
                r.setReviewID(reviewID);
                Model.instance.saveReviewImg(bitmap, r.getReviewID(), new Model.SaveImageListener() {
                    @Override
                    public void onComplete(String url) {
                        r.setImgUrl(url);
                        Model.instance.updateReviewUrl(r, url, new Model.UpdateReviewUrlListener() {
                            @Override
                            public void onComplete() {

                            }
                        });
                    }
                });
            }
        });

    }
}