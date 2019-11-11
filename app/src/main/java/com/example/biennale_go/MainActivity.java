package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.biennale_go.Fragments.AdminPanelFragment;
import com.example.biennale_go.Fragments.PoiFragment;
import com.example.biennale_go.Fragments.ProfilFragment;
import com.example.biennale_go.Fragments.QuizListFragment;
import com.example.biennale_go.Fragments.RankingFragment;
import com.example.biennale_go.Fragments.RoutesListFragment;
import com.example.biennale_go.Utility.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    private ImageView mapButton, mainMenuButton;
    private static final String TAG = "MainActivity";
    private String startFragment;
    private ArrayList scoresList;
    private ArrayList<String> poiScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CurrentUser.isLogged) {

        }
        CurrentUser.setCurrentUser();
        setContentView(R.layout.activity_main);
        if (getIntent().getExtras() != null) {
            startFragment = getIntent().getExtras().getString("fragment");
            if (startFragment.equals("QuizList")) {
                openQuizList();
            } else if (startFragment.equals("RoutesList")) {
                openRoutesList();
            } else if (startFragment.equals("Poi")) {
                openPoi();
            } else if (startFragment.equals("Profil")) {
                openProfil();
            } else if (startFragment.equals("Ranking")) {
                openRanking();
            } else if (startFragment.equals("Admin")) {
                openAdminPanelFragment();
            }
        }

        mapButton = (ImageView) findViewById(R.id.map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });

        mainMenuButton = (ImageView) findViewById(R.id.menu);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuActivity();
            }
        });
        refreshScore();
    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void openAdminPanelFragment() {
        Fragment adminPanelFragment = new AdminPanelFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, adminPanelFragment);
        fragmentTransaction.commit();
    }

    public void openQuizList() {
        Fragment quizListFragment = new QuizListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, quizListFragment);
        fragmentTransaction.commit();
    }

    public void openRoutesList() {
        Fragment routesListFragment = new RoutesListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, routesListFragment);
        fragmentTransaction.commit();
    }

    public void openPoi() {
        Fragment poiFragment = new PoiFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, poiFragment);
        fragmentTransaction.commit();
    }

    public void openProfil() {
        Fragment profilFragment = new ProfilFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, profilFragment);
        fragmentTransaction.commit();
    }

    public void openRanking() {
        Fragment rankingFragment = new RankingFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, rankingFragment);
        fragmentTransaction.commit();
    }

    public void openMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void refreshScore() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("quizzes_scores").document(CurrentUser.uId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CurrentUser.score = 0;
                        scoresList = new ArrayList((ArrayList) document.getData().get("scores"));
                        for (int i = 0; i < scoresList.size(); i++) {
                            Map<String, String> temp = new HashMap<String, String>();
                            temp.putAll((HashMap<String, String>) scoresList.get(i));
                            Integer score = Integer.parseInt(temp.get("score"));
                            if (score > 0) {
                                CurrentUser.score += score;
                            }
                            CurrentUser.score += CurrentUser.distance_traveled;
                        }
                    } else {
                    }

                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }

        });

        DocumentReference doc = db.collection("POI_scores").document(CurrentUser.uId);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        poiScores = (ArrayList<String>) document.getData().get("scores");
                    } else {
                        poiScores = new ArrayList<String>();
                        Map<String, Object> data = new HashMap<>();
                        data.put("scores", poiScores);
                        Log.d(TAG, "No such document");
                        db.collection("POI_scores").document(CurrentUser.uId).set(data);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        if (poiScores != null) {
            CurrentUser.score += poiScores.size();
        }
        DocumentReference docUpdateScores = db.collection("users").document(CurrentUser.email);
        docRef.update("score", CurrentUser.score);
    }
}
