package com.example.mypizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.User;

public class MainActivity extends AppCompatActivity {
    NavController navCtrl;
    User u=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavHostFragment nav_host = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        navCtrl = nav_host.getNavController();
        NavigationUI.setupActionBarWithNavController(this,navCtrl);
        if(u!=null){
            Model.instance.getCurrentUser(new Model.getCurrentUserListener() {
                @Override
                public void onComplete(User user) {
                    u=user;
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.base_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Model.instance.getCurrentUser(new Model.getCurrentUserListener() {
            @Override
            public void onComplete(User user) {
                u=user;
            }
        });
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.menu_pizzas_menu:
                    navCtrl.navigate(R.id.pizzas_display_fragment);
                    return true;
                case R.id.menu_personal_page:
                    //add check if user is manager or client and to assign to u User parm
                    if (u.isAdmin()==true){
                        navCtrl.navigate(R.id.personal_page_manager_fragment);
                    }
                    else if (u.isAdmin()==false){
                        navCtrl.navigate(R.id.personal_page_costumer_fragment);
                    }
//                    else{
//                        DialogFragment newFragment = new personalPageDialogFragment();
//                        newFragment.show(getSupportFragmentManager(), "TAG");
//                    }

                    return true;

                case android.R.id.home:
                    navCtrl.navigateUp();
                default:
                    return NavigationUI.onNavDestinationSelected(item, navCtrl);
            }
        }
        return true;
    }

}