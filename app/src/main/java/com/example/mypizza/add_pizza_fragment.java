package com.example.mypizza;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;


public class add_pizza_fragment extends Fragment {
    View view;
    TextView pizzaPrice;
    TextView pizzaDescription;
    ImageButton pizzaImg;
    Bitmap bitmap;
    Button addPizzaBtn;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESAULT_SUCCESS = 0;
    String price;
    String description;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.add_pizza_fragment, container, false);
        pizzaPrice = view.findViewById(R.id.add_pizza_et_price);
        pizzaDescription = view.findViewById(R.id.add_pizza_et_description);
        addPizzaBtn = view.findViewById(R.id.add_pizza_add_btn);
        pizzaImg = view.findViewById(R.id.add_pizza_add_photo_img);
        pizzaImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
        addPizzaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPizzaBtn.setClickable(false);
                pizzaImg.setClickable(false);
                price = pizzaPrice.getText().toString();
                description = pizzaDescription.getText().toString();
                if(price.length()==0 || description.length()==0 || bitmap==null){
                    DialogFragment newFragment = new addPizzaEmptyFieldsDialog();
                    newFragment.show(getActivity().getSupportFragmentManager(), "TAG");
                    addPizzaBtn.setClickable(true);
                    pizzaImg.setClickable(true);
                }
                else{
                    save();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {//img was taken and return to code
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            pizzaImg.setImageBitmap(bitmap);
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

    private void save() {
        addPizzaBtn.setEnabled(false);
        Pizza p = new Pizza(price, description);
        if (bitmap == null) {
            Navigation.findNavController(view).navigateUp();
        } else {
            Model.instance.saveImage(bitmap, description, new Model.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    p.setImgUrl(url);
                    Model.instance.addPizza(p, new Model.AddPizzaListener() {
                        @Override
                        public void onComplete(boolean flag,String uid) {
                            if (flag == true) {
                                p.setUid(uid);
                                Navigation.findNavController(view).navigate(R.id.action_add_pizza_fragment_to_personal_page_manager_fragment);
                            }
                            if (flag==false){
                                displayDialog("Error","Pizza not saved\ndescription already used");
                            }
                        }
                    });
                }
            });
        }
    }
}



