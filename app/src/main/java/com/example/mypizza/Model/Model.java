package com.example.mypizza.Model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mypizza.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.LinkedList;
import java.util.List;

public class Model {
    public static final Model instance = new Model();
    ModelFirebase modelFirebase = new ModelFirebase();
    MutableLiveData<List<Pizza>> pizzasListLd = new MutableLiveData<>();
    MutableLiveData<LoadingState> pizzaListLoadingState = new MutableLiveData<LoadingState>();

    MutableLiveData<List<Review>> reviewsListLd = new MutableLiveData<>();
    MutableLiveData<LoadingState> reviewsListLoadingState = new MutableLiveData<LoadingState>();

    MutableLiveData<List<Review>> reviewsListForUserLd = new MutableLiveData<>();
    MutableLiveData<LoadingState> reviewsListForUserLoadingState = new MutableLiveData<LoadingState>();



    public enum LoadingState {
        loading,
        loaded
    }

    private Model() {
        pizzaListLoadingState.setValue(LoadingState.loaded);
        reloadPizzasList();
        reviewsListLoadingState.setValue(LoadingState.loaded);
        reloadReviewsList();
        reviewsListForUserLoadingState.setValue(LoadingState.loaded);
    }


    public LiveData<LoadingState> getPizzaListLoadingState() {
        return pizzaListLoadingState;
    }

    public LiveData<LoadingState> getReviewListLoadingState() {
        return reviewsListLoadingState;
    }

    public LiveData<LoadingState> getReviewListForUserLoadingState() {
        return reviewsListForUserLoadingState;
    }

    public LiveData<List<Pizza>> getAllPizzas() {
        return pizzasListLd;
    }

    public LiveData<List<Review>> getAllReviews() {
        return reviewsListLd;
    }

    public LiveData<List<Review>> getAllReviewsForUser() {
        return reviewsListForUserLd;
    }

    public interface GetAllPizzasListener {
        void onComplete(List<Pizza> data);
    }

    public interface GetAllReviewsListener {
        void onComplete(List<Review> data);
    }

    public interface GetAllReviewsForUserListener {
        void onComplete(List<Review> data);
    }

    public void reloadPizzasList() {
        pizzaListLoadingState.setValue(LoadingState.loading);
        //1. get local last update
        Long localLastUpdate = Pizza.getLocalLastUpdated();
        Log.d("TAG", "Reload pizzas localLastUpdate: " + localLastUpdate);
        //2. get all students record since local last update from firebase
        modelFirebase.getAllPizzas(localLastUpdate, (list) -> {
            MyApplication.executorService.execute(() -> {
                //3. update local last update date
                //4. add new records to the local db
                Long lLastUpdate = new Long(0);
                Log.d("TAG", "pizzas returned " + list.size());
                for (Pizza s : list) {
                    AppLocalDBPizza.db.pizzaDao().insertAll(s);
                    if (s.getLastUpdated() > lLastUpdate) {
                        lLastUpdate = s.getLastUpdated();
                    }
                }
                Pizza.setLocalLastUpdated(lLastUpdate);

                //5. return all records to the caller
                List<Pizza> stList = AppLocalDBPizza.db.pizzaDao().getAll();

                pizzasListLd.postValue(stList);
                pizzaListLoadingState.postValue(LoadingState.loaded);
            });
        });
    }

    public void reloadReviewsList() {
        reviewsListLoadingState.setValue(LoadingState.loading);
        //1. get local last update
        Long localLastUpdate = Review.getLocalLastUpdated();
        Log.d("TAG", "review localLastUpdate: " + localLastUpdate);
        modelFirebase.getAllReviews(localLastUpdate, (list) -> {
            MyApplication.executorService.execute(() -> {
                Long lLastUpdate = new Long(0);
                for (Review s : list) {
                    AppLocalDBReview.db.reviewDao().insertAll(s);
                    Log.d("TAG", "reloadReviewsList: local db updated");
                    if (s.getLastUpdated() > lLastUpdate) {
                        lLastUpdate = s.getLastUpdated();
                    }
                }
                Review.setLocalLastUpdated(lLastUpdate);
                //5. return all records to the caller
                List<Review> stList = new LinkedList<>();
                for(Review s : AppLocalDBReview.db.reviewDao().getAll()){
                    if(!s.isDeleted()) {
                        Log.d("TAG", "Review not deleted "+s.getReview() + s.isDeleted());
                        stList.add(s);
                    }
                }
                reviewsListLd.postValue(stList);
                reviewsListLoadingState.postValue(LoadingState.loaded);
            });
        });
    }

    public void reloadReviewsListByMail(String writerMail) {
        reviewsListForUserLoadingState.setValue(LoadingState.loading);
        Long localLastUpdate = Review.getLocalLastUpdated();
        modelFirebase.getAllReviewsByWriterMail(localLastUpdate,writerMail,(list) -> {
            MyApplication.executorService.execute(() -> {
                Long lLastUpdate = new Long(0);
                for (Review s : list) {
                    AppLocalDBReview.db.reviewDao().insertAll(s);
                    if (s.getLastUpdated() > lLastUpdate) {
                        lLastUpdate = s.getLastUpdated();
                    }
                }
                Review.setLocalLastUpdated(lLastUpdate);
                //5. return all records to the caller
                List<Review> stList = new LinkedList<>();
                for(Review s : AppLocalDBReview.db.reviewDao().getReviewByMail(writerMail)) {
                    if (!s.isDeleted())
                        stList.add(s);
                }
                reviewsListForUserLd.postValue(stList);
                reviewsListForUserLoadingState.postValue(LoadingState.loaded);
            });
        });
    }


    public interface AddUserListener {
        void onComplete(boolean flag);
    }

    public void addUser(User user, AddUserListener listener) {
        getUserByEmail(user.getEmail(), new GetUserByUserNameListener() {
            @Override
            public void onComplete(User u) {
                if (u == null) {
                    modelFirebase.addUser(user, listener);
                } else {
                    listener.onComplete(false);
                }
            }
        });
    }

    public interface GetUserByUserNameListener {
        void onComplete(User u);
    }

    public void getUserByEmail(String email, GetUserByUserNameListener listener) {
        modelFirebase.getUserByEmail(email, listener);
    }

    public interface checkLogInListener {
        void onComplete(User u);
    }
//    public void checkUser(String name, String pass,checkLogInListener listener) {
//        modelFirebase.checkUser(name,pass,listener);
//
//    }

    public interface RegistrationByMailPassListener {
        void onComplete(String uid);
    }

    public void regModel(String email, String pass, RegistrationByMailPassListener listener) {
        modelFirebase.reg(email, pass, listener);
    }

    public interface SignInWithEmailPassListener {
        void onComplete(User user, boolean success);
    }

    public void signInWithEmailPass(String email, String password, SignInWithEmailPassListener listener) {
        modelFirebase.signInWithEmail(email, password, listener);
    }

    public interface SaveImageListener {
        void onComplete(String url);
    }

    public void saveImage(Bitmap bitmap, String description, SaveImageListener listener) {
        modelFirebase.saveImage(bitmap, description, listener);
    }

    public interface AddPizzaListener {
        void onComplete(boolean flag, String uid);
    }

    public interface GetPizzaByDescriptionListener {
        void onComplete(Pizza p);
    }

    public void getReviewByID(String reviewID, getReviewByIDListener listener) {
        modelFirebase.getReviewByID(reviewID, listener);
    }

    public interface getReviewByIDListener {
        void onComplete(Review r);
    }

    public void getPizzaByDescription(String description, GetPizzaByDescriptionListener listener) {
        modelFirebase.getPizzaByDescription(description, listener);
    }

    public void addPizza(Pizza pizza, AddPizzaListener listener) {
        modelFirebase.addPizza(pizza, listener, new AddPizzaListener() {
            @Override
            public void onComplete(boolean flag, String uid) {
                reloadPizzasList();
            }
        });
    }

    public interface getCurrentUserListener {
        void onComplete(User user);
    }

    public void getCurrentUser(getCurrentUserListener listener) {
        modelFirebase.getCurrentUser(listener);
    }

    public interface GetPizzaByIDListener {
        void onComplete(Pizza pizza);
    }

    public void getPizzaById(String id, GetPizzaByIDListener listener) {
        modelFirebase.getPizzaByID(id, listener);
    }

    public interface AddReviewListener {
        void onComplete( String reviewID);
    }

    public void addReview(Review r, AddReviewListener listener) {
        modelFirebase.addReview(r, listener);
    }
    public interface UpdateReviewListener {
        void onComplete(Review r);
    }
    public void updateReview(Review review,UpdateReviewListener listener) {
        modelFirebase.updateReview(review, listener);
    }
    public interface DeleteReviewListener {
        void onComplete();
    }

    public void deleteReview(Review review, DeleteReviewListener listener) {
        modelFirebase.deleteReview(review, listener);
        MyApplication.executorService.execute(()->{
            AppLocalDBReview.db.reviewDao().logicDelete(true,review.getReviewID());
        });
        reloadReviewsList();
    }

}
