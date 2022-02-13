package com.example.mypizza;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Review;

import java.util.List;

public class costumerReviewViewModel extends ViewModel{
        LiveData<List<Review>> data = Model.instance.getAllReviewsForUser();
        public LiveData<List<Review>> getData() {
            return data;
        }
    }

