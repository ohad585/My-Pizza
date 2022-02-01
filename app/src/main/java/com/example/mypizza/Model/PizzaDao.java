package com.example.mypizza.Model;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PizzaDao {
    @Query("select * from Pizza")
    List<Pizza> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Pizza... pizzas);

    @Delete
    void delete(Pizza pizza);

    @Query("SELECT * FROM Pizza WHERE description=:desc ")
    Pizza getPizzaByDescription(String desc);
}
