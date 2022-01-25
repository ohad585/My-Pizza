package com.example.mypizza.Model;

public class Model {
    public static final Model instance = new Model();
    ModelFirebase modelFirebase = new ModelFirebase();
    private Model(){}



    public interface AddUserListener{
        void onComplete(boolean flag);
    }

    public void addUser(User user, AddUserListener listener) {
        getUserByEmail(user.getEmail(), new GetUserByUserNameListener() {
            @Override
            public void onComplete(User u) {
                if (u==null){
                    modelFirebase.addUser(user, listener);
                }
                else{
                    listener.onComplete(false);
                }
            }
        });
    }

    public interface GetUserByUserNameListener{
        void onComplete(User u);
    }


    public void getUserByEmail(String email,GetUserByUserNameListener listener) {
        modelFirebase.getUserByEmail(email, listener);
    }
    public interface checkLogInListener{
        void onComplete(User u);
    }
//    public void checkUser(String name, String pass,checkLogInListener listener) {
//        modelFirebase.checkUser(name,pass,listener);
//
//    }

    public interface RegistrationByMailPassListener{
        void onComplete(String uid);
    }

    public void regModel(String email,String pass,RegistrationByMailPassListener listener){
        modelFirebase.reg(email, pass, listener);
    }
    public interface SignInWithEmailPassListener{
        void onComplete(User user, boolean success);
    }
    public void signInWithEmailPass(String email,String password,SignInWithEmailPassListener listener){
        modelFirebase.signInWithEmail(email,password,listener);
    }
}
