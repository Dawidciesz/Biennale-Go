package com.example.biennale_go.Utility;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class CurrentUser {

    public static String uId;
    public static String name;
    public static String email;
    public static int score = 0;
    public static double distance_traveled;
    public static double distance = 0;
    public static List<String> visitedPOIList = new ArrayList<>();
    public static List<String> completedQuizes = new ArrayList<>();
    public static Map<String, Object> visitedPOIMap = new HashMap<>();
    public static String favoritePOI;
    public static ArrayList<String> poiScores = new ArrayList<>();
    public static int favoritePOICount;
    public static boolean isLogged;
    public static String profilPictureColor;
    public static String profilPictureId;


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
            visitedPOIList.clear();
            getPOICount();
            fetchPOIScores();
            //            getQuizesCount(email);
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
//                        avatarUrl = document.getData().get("avatar").toString();
                        distance_traveled = Double.parseDouble(document.getData().get("distance_traveled").toString());
                        name = document.getData().get("name").toString();
                        profilPictureColor =  document.getData().get("profile_color").toString();
                        profilPictureId =  document.getData().get("profile_img").toString();

                    }
                }}});
    }

    public static void getPOICount() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(email).collection("POI_visited").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            int visitedNumber = 0;

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                favoritePOICount = 0;
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
//                        visitedNumber = Integer.parseInt(document.get("visited_count").toString());

                        visitedPOIList.add(document.getId());
                        visitedPOIMap.put(document.getId(), visitedNumber);
                        if (visitedNumber > favoritePOICount) {
                            favoritePOICount = visitedNumber;
                            favoritePOI = document.getId();
                        }
                    }

                } else {
                    Log.d("POI","get POICount failed");
                }
            }
        });
    }


    private static void fetchPOIScores() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("POI_scores").document(uId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                        poiScores = (ArrayList<String>) document.getData().get("scores");
                } else {
                }
            }
        });
    }

//
//    private static void getQuizesCount(String email) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("users").document(email).collection("quizes_scores").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot document : task.getResult()) {
//                        completedQuizes.add(document.getId());
//                        score = score + Integer.parseInt(document.get("points").toString());
//                    }
//                } else {
//                    Log.d("QUIZ","get getQuizesCount failed");
//                }
//            }
//        });

}
