package com.example.mypizza.Model;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mypizza.MyApplication;
@Database(entities = {Review.class}, version =6)
abstract class AppLocalDbRepositoryReview extends RoomDatabase {
    public abstract ReviewDao reviewDao();
}
public class AppLocalDBReview {
    static public final AppLocalDbRepositoryReview db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepositoryReview.class,
                    "reviews1.db")
                    .fallbackToDestructiveMigration()
                    .build();
    private AppLocalDBReview(){}
}
