package com.example.mypizza;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mypizza.Model.Model;
import com.example.mypizza.Model.Pizza;

import java.util.List;

public class PizzasDisplayFragmentViewModel extends ViewModel {
    LiveData<List<Pizza>> data = Model.instance.getAll();

    public LiveData<List<Pizza>> getData(){return data;}
}
