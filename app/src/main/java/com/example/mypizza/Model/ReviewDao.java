package com.example.mypizza.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ReviewDao {
    @Query("select * from Review")
    List<Review> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Review... reviews);

    @Delete
    void delete(Review review);

    @Query("SELECT * FROM Review WHERE writerEmail=:email ")
    List<Review>  getReviewByMail(String email);

    @Query("UPDATE Review SET isDeleted = :isDel WHERE ReviewID =:id")
    void logicDelete(Boolean isDel, String id);

    @Query("UPDATE Review SET review = :review WHERE ReviewID =:id")
    void UpdateLocal(String review, String id);



}
