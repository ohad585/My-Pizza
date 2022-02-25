package com.example.mypizza;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;
import com.squareup.picasso.Picasso;

import java.util.List;


public class pizzas_display_fragment extends Fragment {
    View view;
    PizzasDisplayFragmentViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    View progBar;




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel=new ViewModelProvider(this).get(PizzasDisplayFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.pizzas_display, container, false);

        progBar = view.findViewById(R.id.pizzas_display_progressBar);
        progBar.setVisibility(View.VISIBLE);
        RecyclerView list = view.findViewById(R.id.pizzas_display_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                progBar.setVisibility(View.VISIBLE);
                Pizza pz = viewModel.getData().getValue().get(position);
                pizzas_display_fragmentDirections.ActionPizzasDisplayFragmentToPizzaDetailsFragment action = pizzas_display_fragmentDirections.actionPizzasDisplayFragmentToPizzaDetailsFragment(pz.getDescription());
                Navigation.findNavController(view).navigate(action);
                Log.d("TAG", "onItemClick: "+pz.getDescription());
            }
        });
        swipeRefresh = view.findViewById(R.id.pizzas_display_swipe_refresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Model.instance.reloadPizzasList();
            }
        });


        if(viewModel.getData()==null){refreshData();};

        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Pizza>>() {
            @Override
            public void onChanged(List<Pizza> pizzas) {
                adapter.notifyDataSetChanged();
            }
        });

        swipeRefresh.setRefreshing(Model.instance.getPizzaListLoadingState().getValue()== Model.LoadingState.loading);
        Model.instance.getPizzaListLoadingState().observe(getViewLifecycleOwner(),loadingState ->
                swipeRefresh.setRefreshing(loadingState == Model.LoadingState.loading));
        progBar.setVisibility(View.INVISIBLE);
        return view;
    }
    private void refreshData() {
        Log.d("TAG", "refreshData: ");
    }


    interface OnItemClickListener{
        void onItemClick(int position, View v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView priceTv;
        TextView topsTv;
        ImageView avatar;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            priceTv = itemView.findViewById(R.id.pizza_row_actual_price_tv);
            topsTv = itemView.findViewById(R.id.pizza_row_actual_tops_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(pos,v);
                    }
                }
            });
            avatar = itemView.findViewById(R.id.pizza_row__img);
        }

        public void bind(Pizza pizza){
            priceTv.setText(pizza.getPrice());
            topsTv.setText(pizza.getDescription());
            String url = pizza.getImgUrl();
            if(url != null){
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.avatar)
                        .into(avatar);

            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.pizza_row,parent,false);
            MyViewHolder holder = new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Pizza pizza = viewModel.getData().getValue().get(position);
            holder.bind(pizza);
        }

        @Override
        public int getItemCount() {
            if (viewModel.getData().getValue() == null) return 0;
            return viewModel.getData().getValue().size();
        }
    }
}