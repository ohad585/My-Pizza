package com.example.mypizza.Model;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mypizza.MyApplication;

@Database(entities = {Pizza.class}, version = 6)
abstract class AppLocalDbRepositoryPizza extends RoomDatabase {
    public abstract PizzaDao pizzaDao();

}

public class AppLocalDBPizza {
    static public final AppLocalDbRepositoryPizza db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepositoryPizza.class,
                    "pizzas.db")
                    .fallbackToDestructiveMigration()
                    .build();
    private AppLocalDBPizza(){}
}