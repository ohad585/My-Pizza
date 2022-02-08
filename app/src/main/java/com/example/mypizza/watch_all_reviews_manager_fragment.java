package com.example.mypizza;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;
import com.example.mypizza.Model.Review;
import com.squareup.picasso.Picasso;

import java.util.List;

public class watch_all_reviews_manager_fragment extends Fragment {
    View view;
    allReviewViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    View progBar;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(allReviewViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.watch_all_reviews_manager, container, false);
        progBar = view.findViewById(R.id.watch_all_reviews_progressBar);
        progBar.setVisibility(View.INVISIBLE);
        RecyclerView list = view.findViewById(R.id.watch_all_reviews_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new MyAdapter();
        list.setAdapter(adapter);
        swipeRefresh = view.findViewById(R.id.watch_all_reviews_swipe_refresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Model.instance.reloadReviewsList();
            }
        });

        if(viewModel.getData()==null){refreshData();};
        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                adapter.notifyDataSetChanged();
                Log.d("TAG6", viewModel.getData().getValue().toString());

            }
        });
        swipeRefresh.setRefreshing(Model.instance.getReviewListLoadingState().getValue()== Model.LoadingState.loading);
        Model.instance.getReviewListLoadingState().observe(getViewLifecycleOwner(),loadingState ->
                swipeRefresh.setRefreshing(loadingState == Model.LoadingState.loading));

        return view;
    }
    private void refreshData() {
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reviewText;
        TextView writerEmail;
        ImageView pizzaImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.review_line_show_review_tv);
            writerEmail = itemView.findViewById(R.id.review_line_show_emailAdd_tv);
            pizzaImg = itemView.findViewById(R.id.review_line_show_img);

        }

        public void bind(Review r) {
            reviewText.setText(r.getReview());
            writerEmail.setText(r.getWriterEmail());

        }
    }



    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.review_line_show, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Review review = viewModel.getData().getValue().get(position);
//            Log.d("TAG liron", viewModel.getData().getValue().toString() );
            holder.bind(review);
        }


        @Override
        public int getItemCount() {
            if (viewModel.getData().getValue() == null) return 0;
            return viewModel.getData().getValue().size();
        }
    }
}

