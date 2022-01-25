package com.example.mypizza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.User;

public class registration_page_fragment extends Fragment {
    TextView pass1_et;
    TextView pass2_et;
    TextView admin_pass;
    TextView phone_et;
    TextView mail_et;
    Button reg_btn;
    Button cncl_btn;
    View admin_cb;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.registration_page_fragment, container, false);
        pass1_et = view.findViewById(R.id.reg_pass1_et);
        pass2_et = view.findViewById(R.id.reg_pass2_et);
        admin_pass = view.findViewById(R.id.reg_admin_pass_et);
        mail_et = view.findViewById(R.id.reg_email_et);
        phone_et = view.findViewById(R.id.reg_phone_et);
        cncl_btn = view.findViewById(R.id.reg_cancel_btn);
        reg_btn = view.findViewById(R.id.reg_reg_btn);
        admin_cb = view.findViewById(R.id.reg_admin_cb);

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
        //Add check for empty values.
        if(admin_cb.isSelected()){
            //Admin Registration
        }else {
            //User Registration
            User user = new User(mail,pass1,phone);
            Model.instance.regModel(mail, pass1, new Model.RegistrationByMailPassListener() {
                @Override
                public void onComplete() {
                    Model.instance.addUser(user, new Model.AddUserListener() {
                        @Override
                        public void onComplete(boolean flag) {
                            if(flag==true){
                                Navigation.findNavController(view).navigate(R.id.action_registration_page_fragment_to_login_page_fragment);
                            }
                            //Add if false (Reg fails
                        }
                    });
                }
            });
        }

    }
}