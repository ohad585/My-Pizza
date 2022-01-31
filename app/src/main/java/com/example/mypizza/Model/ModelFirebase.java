package com.example.mypizza.Model;


import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ModelFirebase {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void addUser(User user, Model.AddUserListener listener) {
        db.collection("users")
                .document(user.getEmail()).set(user.toJson())
                .addOnSuccessListener((successListener)-> {
                    listener.onComplete(true);
                })
                .addOnFailureListener((e)-> {
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
    public void reg(String email,String password,Model.RegistrationByMailPassListener listener){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
    public void signInWithEmail(String email,String password ,Model.SignInWithEmailPassListener listener ){
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
                                    listener.onComplete(u,true);
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            listener.onComplete(null,false);
                        }
                    }
                });
    }


    public void saveImage(Bitmap bitmap, String description,Model.SaveImageListener listener) {
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

    public void getPizzaByDescription(String description, Model.GetPizzaByDescriptionListener listener) {
        DocumentReference docRef = db.collection("pizzas").document(description);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //if sucess return the pizza
                        Pizza p = Pizza.fromJson(document.getData());
                        listener.onComplete(p);
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

    public void addPizza(Pizza p, Model.AddPizzaListener listener) {
        db.collection("pizzas")
                .document(p.getDescription()).set(p.toJson())
                .addOnSuccessListener((successListener)-> {
                    listener.onComplete(true);
                })
                .addOnFailureListener((e)-> {
                    Log.d("TAG", e.getMessage());
                });
    }

    public void getCurrentUser(Model.getCurrentUserListener listener){
        FirebaseUser user = mAuth.getCurrentUser();
        listener.onComplete(new User(user));
    }
}
