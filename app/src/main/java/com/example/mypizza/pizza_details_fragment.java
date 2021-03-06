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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;
import com.example.mypizza.Model.Review;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;


public class pizza_details_fragment extends Fragment {
    View view;
    Pizza p;
    allReviewViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    List<Review> pizzaReviewList = new LinkedList<>();
    TextView price;
    TextView tops;
    ImageView img;
    Button writeReviewBtn;
    View progBar;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(allReviewViewModel.class);
    }

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
        writeReviewBtn = view.findViewById(R.id.pizza_details_review_btn);
        swipeRefresh = view.findViewById(R.id.pizza_details_swipe_refresh);
        RecyclerView list = view.findViewById(R.id.pizza_details_reviews_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        writeReviewBtn.setClickable(false);
        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pizza_details_fragmentDirections.ActionPizzaDetailsFragmentToWriteReviewFragment action = pizza_details_fragmentDirections.actionPizzaDetailsFragmentToWriteReviewFragment(p.getDescription());
                Navigation.findNavController(view).navigate(action);
            }
        });
        if(viewModel.getData()==null){refreshData();};
        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                updatePizzaReviewList();
            }

        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Model.instance.reloadReviewsList();
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Review re = pizzaReviewList.get(position);
            }
        });

        swipeRefresh.setRefreshing(Model.instance.getReviewListLoadingState().getValue()== Model.LoadingState.loading);
        Model.instance.getReviewListLoadingState().observe(getViewLifecycleOwner(),loadingState ->
                swipeRefresh.setRefreshing(loadingState == Model.LoadingState.loading));

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
        writeReviewBtn.setClickable(true);
        updatePizzaReviewList();
    }

    void updatePizzaReviewList(){
        if(p==null) {
            return;
        }
        pizzaReviewList=new LinkedList<>();
        for(Review r:viewModel.getData().getValue()){
            if(r.getPizzaID().matches(p.getDescription())){
                pizzaReviewList.add(r);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void refreshData() {
        updatePizzaReviewList();
    }

    interface OnItemClickListener{
        void onItemClick(int position, View v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reviewText;
        TextView writerEmail;
        ImageView reviewImg;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.review_line_show_review_tv);
            writerEmail = itemView.findViewById(R.id.review_line_show_emailAdd_tv);
            reviewImg = itemView.findViewById(R.id.review_line_show_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(pos,v);
                    }
                }
            });
        }

        public void bind(Review r) {
            reviewText.setText(r.getReview());
            writerEmail.setText(r.getWriterEmail());
            String url = r.getImgUrl();
            if(url != null)
            {
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.avatar)
                        .into(reviewImg);
            }

        }
    }



    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.review_line_show, parent, false);
            MyViewHolder holder = new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Review review = pizzaReviewList.get(position);
            holder.bind(review);
        }


        @Override
        public int getItemCount() {
            if (pizzaReviewList == null) return 0;
            return pizzaReviewList.size();
        }


    }
}