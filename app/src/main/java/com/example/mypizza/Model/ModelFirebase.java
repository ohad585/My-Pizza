package com.example.mypizza.Model;


import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

public class ModelFirebase {
    final static String USERS_DB = "users";
    final static String PIZZA_DASH = "pizza/";
    final static String JPG = ".jpg";
    final static String REVIEWS_DASH = "reviews/";
    final static String PIZZAS_DB = "pizzas";
    final static String REVIEWS_DB = "reviews";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ModelFirebase() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void addUser(User user, Model.AddUserListener listener) {
        db.collection("users")
                .document(user.getEmail()).set(user.toJson())
                .addOnSuccessListener((successListener) -> {
                    listener.onComplete(true);
                })
                .addOnFailureListener((e) -> {
                    listener.onComplete(false);
                });
    }

    public void getUserByEmail(String email, Model.GetUserByUserNameListener listener) {
        DocumentReference docRef = db.collection(USERS_DB).document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User u = User.fromJson(document.getData());
                        listener.onComplete(u);
                    } else {
                        listener.onComplete(null);
                    }
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }

    public interface getAdminData {
        void onComplete(boolean isAdmin);
    }

    public void checkUserAdmin(User user, getAdminData listener) {
        DocumentReference docRef = db.collection(USERS_DB).document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User u = User.fromJson(document.getData());
                        listener.onComplete(u.isAdmin());
                    }
                }
            }
        });
    }


    public void reg(String email, String password, Model.RegistrationByMailPassListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    listener.onComplete(mAuth.getCurrentUser().getUid());
                } else {
                    listener.onComplete(null);
                }
            }
        });

    }

    public void signInWithEmail(String email, String password, Model.SignInWithEmailPassListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            getUserByEmail(user.getEmail(), new Model.GetUserByUserNameListener() {
                                @Override
                                public void onComplete(User u) {
                                    listener.onComplete(u, true);
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            listener.onComplete(null, false);
                        }
                    }
                });
    }


    public void saveImage(Bitmap bitmap, String description, Model.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(PIZZA_DASH + description + JPG);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> listener.onComplete(null))//failure
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()//success
                        .addOnSuccessListener(uri -> {
                            Uri downloadUrl = uri;
                            listener.onComplete(downloadUrl.toString());
                        }));
    }
    public void saveImageReview(Bitmap bitmap, String reviewId, Model.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(REVIEWS_DASH + reviewId + JPG);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> listener.onComplete(null))//failure
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()//success
                        .addOnSuccessListener(uri -> {
                            Uri downloadUrl = uri;
                            listener.onComplete(downloadUrl.toString());
                        }));
    }

    public void getPizzaByID(String id, Model.GetPizzaByIDListener listener) {
        DocumentReference docRef = db.collection(PIZZAS_DB).document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Pizza pz = Pizza.fromJson(document.getData());
                        listener.onComplete(pz);
                    } else {
                        listener.onComplete(null);
                    }
                }
            }
        });
    }

    public void getPizzaByDescription(String description, Model.GetPizzaByDescriptionListener listener) {
        db.collection(PIZZAS_DB).whereEqualTo(Pizza.DESC, description)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //if sucess return the pizza
                            Pizza p = Pizza.fromJson(document.getData());
                            listener.onComplete(p);
                        } else {
                            listener.onComplete(null);
                        }
                    }
                } else listener.onComplete(null);
            }
        });
    }


    public void addPizza(Pizza p, Model.AddPizzaListener listener, Model.AddPizzaListener listener2) {
        db.collection(PIZZAS_DB)
                .add(p.toJson())
                .addOnSuccessListener((successListener) -> {
                    listener.onComplete(true, successListener.getId().toString());
                    listener2.onComplete(false, null);
                })
                .addOnFailureListener((e) -> {
                    Log.d("TAG", e.getMessage());
                });
    }

    public void getCurrentUser(Model.getCurrentUserListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user ==null){
            listener.onComplete(null);
            return;
        }
        User usr=new User();
        usr.setEmail(user.getEmail());
        usr.setUid(user.getUid());
        checkUserAdmin(usr, new getAdminData() {
            @Override
            public void onComplete(boolean isAdmin) {
                usr.setAdmin(isAdmin);
                listener.onComplete(usr);
            }
        });
    }

    public void getAllPizzas(long since, Model.GetAllPizzasListener listener) {
        db.collection(PIZZAS_DB)
                .whereGreaterThanOrEqualTo(Pizza.LAST_UPDATED, new Timestamp(since, 0))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Pizza> pizzasList = new LinkedList<Pizza>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Pizza s = Pizza.fromJson(doc.getData());
                        if (s != null) {
                            pizzasList.add(s);
                        }
                    }
                } else {

                }
                listener.onComplete(pizzasList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });
    }

    public void addReview(Review r, Model.AddReviewListener listener) {
        db.collection(REVIEWS_DB).add(r.toJson())
                .addOnSuccessListener((successListener) -> {
                    r.setReviewID(successListener.getId());
                    String reviewID=successListener.getId();
                    listener.onComplete(reviewID);
                })
                .addOnFailureListener((e) -> {
                    Log.d("TAG", e.getMessage());
                });
    }

    public void getAllReviews(long since, Model.GetAllReviewsListener listener) {
        db.collection(REVIEWS_DB)
                .whereGreaterThanOrEqualTo(Review.LAST_UPDATED,new Timestamp(since, 0))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Review> ReviewList = new LinkedList<Review>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Review s = Review.fromJson(doc.getData());
                        s.setReviewID(doc.getId());
                        if (s != null) {
                            ReviewList.add(s);
                        }
                    }
                } else {

                }
                listener.onComplete(ReviewList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });
    }

    public void getReviewByID(String reviewID, Model.getReviewByIDListener listener) {
        DocumentReference docRef = db.collection(REVIEWS_DB).document(reviewID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Review s = Review.fromJson(document.getData());
                        listener.onComplete(s);
                    } else {

                        listener.onComplete(null);
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                    listener.onComplete(null);
                }
            }
        });
    }


    public void getAllReviewsByWriterMail(long since,String writerMail, Model.GetAllReviewsForUserListener listener) {
        db.collection(REVIEWS_DB)
                .whereGreaterThanOrEqualTo(Review.LAST_UPDATED, new Timestamp(since, 0))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Review> ReviewList = new LinkedList<Review>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Review s = Review.fromJson(doc.getData());
                        s.setReviewID(doc.getId());
                        if (s != null && !s.isDeleted() && s.getWriterEmail()==writerMail) {
                            ReviewList.add(s);
                        }
                    }
                } else {

                }
                listener.onComplete(ReviewList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });
    }

    public void updateReview(Review review, Model.UpdateReviewListener listener) {
        DocumentReference docRef = db.collection(REVIEWS_DB).document(review.getReviewID());
        docRef.update(Review.REVIEW,review.getReview(),Review.LAST_UPDATED, FieldValue.serverTimestamp())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                        listener.onComplete(review);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error updating document", e);
                    }
                });
    }
    public void deleteReview(Review review, Model.DeleteReviewListener listener) {
        DocumentReference docRef = db.collection(REVIEWS_DB).document(review.getReviewID());
        docRef.update(Review.IS_DELETED, true, Review.LAST_UPDATED, FieldValue.serverTimestamp())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                listener.onComplete();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error deleting document", e);
                    }
                });
    }
    public void updateReviewUrl(Review review,String url, Model.UpdateReviewUrlListener listener) {
        DocumentReference docRef = db.collection(REVIEWS_DB).document(review.getReviewID());
        docRef.update(Review.IMGURL,review.getImgUrl(),Review.LAST_UPDATED, FieldValue.serverTimestamp())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                        listener.onComplete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error updating document", e);
                    }
                });
    }

    public void UpdateReviewImg(Bitmap bitmap, String reviewId, Model.SaveImageListener listener) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child(REVIEWS_DASH + reviewId + JPG);
            imageRef.delete();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> listener.onComplete(null))//failure
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()//success
                            .addOnSuccessListener(uri -> {
                                Uri downloadUrl = uri;
                                listener.onComplete(downloadUrl.toString());
                            }));
        }
    }