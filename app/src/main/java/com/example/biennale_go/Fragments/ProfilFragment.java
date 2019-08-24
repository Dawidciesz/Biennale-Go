package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfilFragment extends Fragment {
    private  View view;
    private TextView userName;
    private TextView placesVisitedText;
    private TextView quizesCompletedText;
    private TextView favoritePlaceText;
    private TextView distanceTraveledText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_profil, container, false);

        userName = (TextView) view.findViewById(R.id.userName);
        userName.setText(CurrentUser.name);

        placesVisitedText = (TextView) view.findViewById(R.id.map_text_botom);
        placesVisitedText.setText(getResources().getString(R.string.places_visited, CurrentUser.visitedPOIList.size()));

        quizesCompletedText = (TextView) view.findViewById(R.id.test_text_botom);
        quizesCompletedText.setText(getResources().getString(R.string.quizes_finished, CurrentUser.completedQuizes.size()));

        favoritePlaceText = (TextView) view.findViewById(R.id.marker_text_botom);
        favoritePlaceText.setText(CurrentUser.favoritePOI);

        distanceTraveledText = (TextView) view.findViewById(R.id.route_text_botom);
        distanceTraveledText.setText(getResources().getString(R.string.distance_traveled, CurrentUser.distance_traveled));

        return view;
    }
}