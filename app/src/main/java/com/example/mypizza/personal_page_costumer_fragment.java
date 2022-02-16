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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Review;
import com.example.mypizza.Model.User;

import org.w3c.dom.Text;

import java.util.List;

public class personal_page_costumer_fragment extends Fragment {
    View view;
    View progBar;
    TextView email;
    costumerReviewViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    User currentUser;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(costumerReviewViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.personal_page_costumer__fragment, container, false);
        email = view.findViewById(R.id.personal_page_costumer_enailAddress_tv);
        progBar = view.findViewById(R.id.personal_page_costumer_progBar);
        progBar.setVisibility(View.VISIBLE);
        Model.instance.getCurrentUser(new Model.getCurrentUserListener() {
            @Override
            public void onComplete(User user) {
                email.setText(user.getEmail());
                progBar.setVisibility(View.INVISIBLE);
                showReviews();
                currentUser=user;
            }
        });
        return view;
    }

    public void showReviews() {
        RecyclerView list = view.findViewById(R.id.personal_page_costumer_reviewList);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        //to fix
        swipeRefresh = view.findViewById(R.id.personal_page_costumer_swiperefresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Model.instance.reloadReviewsListByMail("IcdSO4tqVre2CsZjwciQq5aGHpu2");
            }
        });

        if (viewModel.getData() == null) {
            refreshData();
        }
        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                adapter.notifyDataSetChanged();
            }

        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                progBar.setVisibility(View.VISIBLE);
                Review re = viewModel.getData().getValue().get(position);
                personal_page_costumer_fragmentDirections.ActionPersonalPageCostumerFragmentToEditReviewFragment action =personal_page_costumer_fragmentDirections.actionPersonalPageCostumerFragmentToEditReviewFragment(re.getReviewID());
                Navigation.findNavController(view).navigate(action);
                Log.d("TAG", "review is clicked: " + re.getReviewID());
            }
        });
        swipeRefresh.setRefreshing(Model.instance.getReviewListForUserLoadingState().getValue() == Model.LoadingState.loading);
        Model.instance.getReviewListForUserLoadingState().observe(getViewLifecycleOwner(), loadingState ->
                swipeRefresh.setRefreshing(loadingState == Model.LoadingState.loading));

    }
    private void refreshData(){
        Log.d("TAG", "refreshData: watch reviews");
    }

    interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reviewText;
        TextView writerEmail;
        ImageView pizzaImg;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.review_line_show_review_tv);
            writerEmail = itemView.findViewById(R.id.review_line_show_emailAdd_tv);
            pizzaImg = itemView.findViewById(R.id.review_line_show_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(pos, v);
                    }
                }
            });
        }

        public void bind(Review r) {
            reviewText.setText(r.getReview());
            writerEmail.setText(r.getWriterEmail());
        }
    }
    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
           OnItemClickListener listener;

            public void setOnItemClickListener(OnItemClickListener listener) {
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
                Review review = viewModel.getData().getValue().get(position);
                if(review.isDeleted()==false) {
                    holder.bind(review);
                }
            }


            @Override
            public int getItemCount() {
                if (viewModel.getData().getValue() == null) return 0;
                return viewModel.getData().getValue().size();
            }


        }
    }