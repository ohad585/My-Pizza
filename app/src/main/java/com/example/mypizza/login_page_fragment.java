package com.example.mypizza;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.User;

public class login_page_fragment extends Fragment {
    View view;
    TextView email;
    TextView password;
    View progBar;
    String uEmail;
    String uPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.login_page_fragment, container, false);
        email =view.findViewById(R.id.login_email_et);
        password =view.findViewById(R.id.login_password_et);
        progBar = view.findViewById(R.id.login_progressBar);
        progBar.setVisibility(View.INVISIBLE);
        Button loginBtn=view.findViewById(R.id.login_login_btn);
        setHasOptionsMenu(true);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progBar.setVisibility(View.VISIBLE);
                uEmail = email.getText().toString();
                uPass= password.getText().toString();
                if(uEmail.matches("") || uPass.matches("")){
                    displayDialog("Error","All fields must be filled");
                    progBar.setVisibility(View.INVISIBLE);
                    return;
                }
                else loginUser();
            }
        });
        return view;
    }
    void displayDialog(String title,String msg){
        AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void loginUser(){
        Log.d("TAG", "loginUser: "+uEmail);
        Model.instance.signInWithEmailPass(uEmail,uPass,(User user, boolean success)->{
            if(success){
                progBar.setVisibility(View.INVISIBLE);
                Log.d("TAG", "loginUser: "+user.getEmail()+" "+user.getUid());
                if(user.isAdmin()==false){
                    Navigation.findNavController(view).navigate(R.id.action_login_page_fragment_to_personal_page_costumer_fragment);
                }
                else{
                    Navigation.findNavController(view).navigate(R.id.action_login_page_fragment_to_personal_page_manager_fragment);

                }
            }
            else {
                displayDialog("Error","Login Failed \nEmail or Password is incorrect");
                progBar.setVisibility(View.INVISIBLE);
                Log.d("TAG", "loginUser: Failed");
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); //hide menu at this page that how isnt sign in or reg cant enter
    }

}