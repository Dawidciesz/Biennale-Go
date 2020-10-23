package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.biennale_go.MainActivity;
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

import androidx.activity.OnBackPressedCallback;
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).onSlideViewButtonClick();
            }                // Handle the back button event

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return view;
    }
    }