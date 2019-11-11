package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfilFragment extends Fragment {
    private  View view;
    private TextView userName;
    private TextView placesVisitedText;
    private TextView quizesCompletedText;
    private TextView favoritePlaceText;
    private TextView distanceTraveledText;
    private ArrayList<String> poiScores;
    private int poiSize;
    private ArrayList scoresList;
    private Map<String, Integer> scores = new HashMap<String, Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_profil, container, false);

        fetchScores();
        userName = (TextView) view.findViewById(R.id.userName);
        userName.setText(CurrentUser.name);
        fetchPOIScores();
        placesVisitedText = (TextView) view.findViewById(R.id.map_text_botom);

        quizesCompletedText = (TextView) view.findViewById(R.id.test_text_botom);

        favoritePlaceText = (TextView) view.findViewById(R.id.marker_text_botom);
        favoritePlaceText.setText(CurrentUser.favoritePOI);

        distanceTraveledText = (TextView) view.findViewById(R.id.route_text_botom);
        distanceTraveledText.setText(getResources().getString(R.string.distance_traveled, CurrentUser.distance_traveled));

        return view;
    }
    private void fetchPOIScores() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("POI_scores").document(CurrentUser.uId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        poiScores = (ArrayList<String>) document.getData().get("scores");
                    } else {
                        poiScores = new ArrayList<String>();
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }

                placesVisitedText.setText(getResources().getString(R.string.places_visited, poiScores.size()));
            }
        });
    }

    public void fetchScores() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("quizzes_scores").document(CurrentUser.uId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        scoresList = new ArrayList((ArrayList) document.getData().get("scores"));
                        for(int i=0; i<scoresList.size(); i++) {
                            Map<String, String> temp = new HashMap<String, String>();
                            temp.putAll((HashMap<String,String>)scoresList.get(i));
                            String name = temp.get("name");
                            Integer score = Integer.parseInt(temp.get("score"));
                            if (score > 0){
                                CurrentUser.score += score;
                            scores.put(name, score);
                            }
                        }
                    } else {
                    }

                } else {
                    Log.d("", "get failed with ", task.getException());
                }
                quizesCompletedText.setText(getResources().getString(R.string.quizes_finished, scores.size()));
            }

        });
    }
    }