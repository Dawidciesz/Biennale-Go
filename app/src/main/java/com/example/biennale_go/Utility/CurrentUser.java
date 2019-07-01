package com.example.biennale_go.Utility;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CurrentUser {

    public static String uId;
    public static String email;
    public static String name;
    public static boolean isLogged;


    public static void setCurrentUser() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
           isLogged = false;
        } else {
            isLogged = true;
            uId = currentUser.getEmail();
            email = currentUser.getUid();
        }
    }
}
//TODO stworz tabele uzytk. w firestore i tam dodaj rozne pola takie fajne jak uid czy email aby mozna bylo zidnetyfikowac obecnie zal uzytk. i w ten sposob dostac dane :)