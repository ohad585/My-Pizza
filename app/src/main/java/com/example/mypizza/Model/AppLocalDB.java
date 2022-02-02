package com.example.mypizza.Model;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mypizza.MyApplication;

@Database(entities = {Pizza.class}, version = 1)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PizzaDao pizzaDao();
}

public class AppLocalDB {
    static public final AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "pizzas.db")
                    .fallbackToDestructiveMigration()
                    .build();
    private AppLocalDB(){}
}