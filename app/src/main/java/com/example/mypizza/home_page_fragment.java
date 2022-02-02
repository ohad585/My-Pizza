package com.example.mypizza;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class home_page_fragment extends Fragment {
    Button loginBtn;
    Button regBtn;
    NavController navCtrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.home_page_fragment, container, false);
        setHasOptionsMenu(true);
        NavHostFragment nav_host = (NavHostFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        navCtrl = nav_host.getNavController();
        NavigationUI.setupActionBarWithNavController((AppCompatActivity) getContext(),navCtrl);
        loginBtn=view.findViewById(R.id.home_page_login_btn);
        regBtn=view.findViewById(R.id.home_page_reg_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navCtrl.navigate(R.id.login_page_fragment);
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navCtrl.navigate(R.id.registration_page_fragment);
            }
        });
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear(); //hide menu at this page that how isnt sign in or reg cant enter
    }





}