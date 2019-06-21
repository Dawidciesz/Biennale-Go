package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuizSummaryActivity extends AppCompatActivity {
    private Button exitButton;
    private TextView pointsTextView;
    private Bundle b;
    private Integer points, maxPoints, key;
    private ImageView awardImageView;
    private ArrayList scoresList = new ArrayList();
    //    TODO GLOBAL ID
    private Integer id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_summary);
        pointsTextView = (TextView) findViewById(R.id.pointsTextView);
        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
        awardImageView = (ImageView) findViewById(R.id.awardImageView);

        b = getIntent().getExtras();

        if (b != null) {
            points =  b.getInt("points");
            maxPoints =  b.getInt("maxPoints");
            key =  b.getInt("key");
            scoresList = new ArrayList((ArrayList) b.getSerializable("scoresList"));
            pointsTextView.setText(points + "/" + maxPoints);

            Double percentage = (Double.valueOf(points)/Double.valueOf(maxPoints)) * 100;
            if(percentage >= 80.0) {
                awardImageView.setImageResource( R.drawable.awardgold );
            } else if(percentage >= 50.0) {
                awardImageView.setImageResource( R.drawable.awardsilver );
            } else if(percentage >= 30.0) {
                awardImageView.setImageResource( R.drawable.awardbronze );
            }

            updateScoreList();
        }
    }

    public void updateScoreList() {
        Map<String, Integer> scores = new HashMap<String, Integer>();
        for(int i=0; i<scoresList.size(); i++) {
            Map<String, String> temp = new HashMap<String, String>();
            temp.putAll((HashMap<String,String>)scoresList.get(i));
            String name = temp.get("name");
            Integer score;
            if(i == key && Integer.parseInt(temp.get("score")) < points) {
                score = points;
            } else {
                score = Integer.parseInt(temp.get("score"));
            }
            scores.put(name, score);
        }

        ArrayList newScoresList = new ArrayList();
        for ( String hashKey : scores.keySet() ) {
            HashMap<String, String> newHashmap = new HashMap<String, String>();
            newHashmap.put("name", hashKey);
            newHashmap.put("score", scores.get(hashKey).toString());
            newScoresList.add(newHashmap);
        }
        Collections.reverse(newScoresList);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("quizzes_scores").document(id.toString());
        docRef.update("scores", newScoresList);
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
