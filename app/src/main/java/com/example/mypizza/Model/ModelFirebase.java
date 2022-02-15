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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
        DocumentReference docRef = db.collection("users").document(email);
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
                    Log.d("TAG", "get failed with ", task.getException());
                    listener.onComplete(null);
                }
            }
        });
    }

    public void checkUser(String name, String pass, Model.checkLogInListener listener) {
        DocumentReference docRef = db.collection("users").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User u = User.fromJson(document.getData());
                        String passFromDB = u.getPassword();
                        if (pass == passFromDB) {
                            //login success
                            listener.onComplete(u);
                        }
                    } else {
                        //name doesnt match to password
                        listener.onComplete(null);
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                    listener.onComplete(null);
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
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getUserByEmail(user.getEmail(), new Model.GetUserByUserNameListener() {
                                @Override
                                public void onComplete(User u) {
                                    listener.onComplete(u, true);
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            listener.onComplete(null, false);
                        }
                    }
                });
    }


    public void saveImage(Bitmap bitmap, String description, Model.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("pizza/" + description + ".jpg");

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
        Log.d("TAG", "getPizzaByID: " + id);
        DocumentReference docRef = db.collection("pizzas").document(id);
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
        db.collection("pizzas").whereEqualTo("description", description)
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
        db.collection("pizzas")
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
        listener.onComplete(new User(user));
    }

    public void getAllPizzas(long since, Model.GetAllPizzasListener listener) {
        db.collection("pizzas")
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
        db.collection("reviews").document(r.getReviewID()).set(r.toJson())
                .addOnSuccessListener((successListener) -> {
                    listener.onComplete();
                })
                .addOnFailureListener((e) -> {
                    Log.d("TAG", e.getMessage());
                });
    }

    public void getAllReviews(long since, Model.GetAllReviewsListener listener) {
        db.collection("reviews")
//                .whereGreaterThanOrEqualTo(Review.LAST_UPDATED,new Timestamp(since, 0))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Review> ReviewList = new LinkedList<Review>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Review s = Review.fromJson(doc.getData());
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
            DocumentReference docRef = db.collection("reviews").document(reviewID);
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


    public void getAllReviewsByWriterMail(String writerMail, Model.GetAllReviewsForUserListener listener) {
        db.collection("reviews").whereEqualTo("writerEmail", writerMail)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Review> ReviewList = new LinkedList<Review>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Review s = Review.fromJson(doc.getData());
                        Log.d("TAG", "onComplete: " + s.getReview());
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

    public void updateReview(Review review, Model.UpdateReviewListener listener) {
        DocumentReference docRef = db.collection("reviews").document(review.getReviewID());
        docRef.update("review", review.getReview()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error updating document", e);
                    }
            });
    }
}