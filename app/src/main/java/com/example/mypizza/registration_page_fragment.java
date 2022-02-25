package com.example.mypizza;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.User;

public class registration_page_fragment extends Fragment {
    TextView pass1_et;
    TextView pass2_et;
    TextView admin_pass;
    TextView phone_et;
    TextView mail_et;
    View progBar;
    Button reg_btn;
    Button cncl_btn;
    CheckBox admin_cb;
    View view;
    private static final String REG_ADMINPASS = "LOadmin";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.registration_page_fragment, container, false);
        setHasOptionsMenu(true);

        progBar = view.findViewById(R.id.reg_progressBar);
        pass1_et = view.findViewById(R.id.reg_pass1_et);
        pass2_et = view.findViewById(R.id.reg_pass2_et);
        admin_pass = view.findViewById(R.id.reg_admin_pass_et);
        mail_et = view.findViewById(R.id.reg_email_et);
        phone_et = view.findViewById(R.id.reg_phone_et);
        cncl_btn = view.findViewById(R.id.reg_cancel_btn);
        reg_btn = view.findViewById(R.id.reg_reg_btn);
        admin_cb = (CheckBox)view.findViewById(R.id.reg_admin_cb);
        progBar.setVisibility(View.INVISIBLE);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regUser();
            }
        });

        return view;
    }



    void regUser(){
        String phone=phone_et.getText().toString();
        String pass1=pass1_et.getText().toString();
        String pass2=pass2_et.getText().toString();
        String mail=mail_et.getText().toString();
        String apass=admin_pass.getText().toString();
        //check for empty values.
        if(phone.matches("") || pass1.matches("") || pass2.matches("")  || mail.matches("")){
            displayDialog("Error","All fields must be filled");
            return;
        }
        //check pass1=pass2
        if(!pass1.matches(pass2)){
            displayDialog("Error","Passwords don't match");
            return;
        }
        progBar.setVisibility(View.VISIBLE);
        reg_btn.setClickable(false);
        if(admin_cb.isChecked()){
            //Admin Registration
            if(!apass.matches(REG_ADMINPASS)){
                displayDialog("Error","Admin password is incorrect");
                return;
            }
            Model.instance.regModel(mail, pass1, new Model.RegistrationByMailPassListener() {
                @Override
                public void onComplete(String uid) {
                    if (uid==null){
                        progBar.setVisibility(View.INVISIBLE);
                        reg_btn.setClickable(true);
                        displayDialog("Error","Somthing went wrong please try again");
                        return;
                    }
                    User user= new User(mail,pass1,phone,true,uid);
                    Model.instance.addUser(user, new Model.AddUserListener() {
                        @Override
                        public void onComplete(boolean flag) {
                            if(flag==true){
                                Navigation.findNavController(view).navigate(R.id.action_registration_page_fragment_to_login_page_fragment);
                            }else {
                                progBar.setVisibility(View.INVISIBLE);
                                reg_btn.setClickable(true);
                                displayDialog("Error","Somthing went wrong please try again");
                            }
                        }
                    });
                }
            });
        }else {
            //User Registration
            Model.instance.regModel(mail, pass1, new Model.RegistrationByMailPassListener() {
                @Override
                public void onComplete(String uid) {
                    if (uid==null){
                        progBar.setVisibility(View.INVISIBLE);
                        displayDialog("Error","Somthing went wrong please try again");
                        return;
                    }
                    User user= new User(mail,pass1,phone,false,uid);
                    Model.instance.addUser(user, new Model.AddUserListener() {
                        @Override
                        public void onComplete(boolean flag) {
                            if(flag==true){
                                Navigation.findNavController(view).navigate(R.id.action_registration_page_fragment_to_login_page_fragment);
                            }else{
                                progBar.setVisibility(View.INVISIBLE);
                                displayDialog("Error","Somthing went wrong please try again");
                                return;
                            }
                        }
                    });
                }
            });
        }

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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); //hide menu at this page that how isnt sign in or reg cant enter
    }
}