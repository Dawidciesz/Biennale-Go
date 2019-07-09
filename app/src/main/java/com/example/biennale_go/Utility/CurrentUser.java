package com.example.biennale_go.Utility;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class CurrentUser {

    public static String uId;
    public static String name;
    public static String email;
    public static String avatarUrl;
    public static String gender;
    public static int age;
    public static int score;


    public static boolean isLogged;



    public static void setCurrentUser() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
           isLogged = false;
        } else {
            isLogged = true;
            email = currentUser.getEmail();
            uId = currentUser.getUid();
            setCurrentUserInfo(email);

        }
    }
    private static void setCurrentUserInfo(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        avatarUrl = document.getData().get("avatar").toString();
                        name =  document.getData().get("name").toString();
                        gender = document.getData().get("gender").toString();
                        age = Integer.parseInt(document.getData().get("age").toString());
                        score = Integer.parseInt(document.getData().get("score").toString());
                        score = Integer.parseInt(document.getData().get("score").toString());
                    }
                }}});
    }
}
//TODO stworz tabele uzytk. w firestore i tam dodaj rozne pola takie fajne jak uid czy email aby mozna bylo zidnetyfikowac obecnie zal uzytk. i w ten sposob dostac dane :)