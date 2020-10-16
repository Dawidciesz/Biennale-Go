package com.example.biennale_go.Fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.biennale_go.MainActivity;
import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class QuizSummaryFragment extends Fragment {
    private Button exitButton;
    private TextView pointsTextView;
    private Bundle b;
    private Integer points, maxPoints;
    private ImageView awardImageView;
    private ArrayList scoresList = new ArrayList();
    private String id = CurrentUser.uId, name;
    private View view;
    private int scoresTotal = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_quiz_summary, container, false);
        pointsTextView = (TextView) view.findViewById(R.id.pointsTextView);
        exitButton = (Button) view.findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
        awardImageView = (ImageView) view.findViewById(R.id.awardImageView);
        b = getArguments();
        if (b != null) {
            name = b.getString("name");
            points =  b.getInt("points");
            maxPoints =  b.getInt("maxPoints");
            scoresList = new ArrayList((ArrayList) b.getSerializable("scoresList"));
            pointsTextView.setText(points + "/" + maxPoints);
            updateScoreList();
        }
        return view;
    }

    public void updateScoreList() {
        HashMap scoresMap = new HashMap();
        scoresMap = (HashMap) scoresList.get(0);
        if((Integer) scoresMap.get(name) < points) {
            scoresMap.put(name, points);
            ArrayList newScoresList = new ArrayList();
            scoresTotal += points;
            for ( Object hashKey : scoresMap.keySet() ) {
                HashMap<String, String> newHashmap = new HashMap<String, String>();
                newHashmap.put("name", hashKey.toString());
                newHashmap.put("score", scoresMap.get(hashKey).toString());
                newScoresList.add(newHashmap);
            }
            Collections.reverse(newScoresList);
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("quizzes_scores").document(id);
            docRef.update("scores", newScoresList);
            DocumentReference docUpdateScores = db.collection("users").document(CurrentUser.email);
            docUpdateScores.update("score", scoresTotal);
        }
    }

    public void openMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
