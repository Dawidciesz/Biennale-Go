package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizListActivity extends AppCompatActivity {
    private LinearLayout quizListPanel;
    private Button newButton;
    private ArrayList scoresList;
    private static final String TAG = "QuizListActicity";
//    TODO GLOBAL ID
    private Integer id = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        quizListPanel = (LinearLayout) findViewById(R.id.quizListPanel);
        scoresList = new ArrayList();
        fetchScores();
    }

    public void fetchScores() {
        // get user score
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("quizzes_scores").document(id.toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Integer> scores = new HashMap<String, Integer>();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        scoresList = new ArrayList((ArrayList) document.getData().get("scores"));
                        for(int i=0; i<scoresList.size(); i++) {
                            Map<String, String> temp = new HashMap<String, String>();
                            temp.putAll((HashMap<String,String>)scoresList.get(i));
                            String name = temp.get("name");
                            Integer score = Integer.parseInt(temp.get("score"));
                            scores.put(name, score);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                fetchQuizzes(scores);
            }
        });
    }

    public void fetchQuizzes(final Map<String, Integer> scores) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("quizzes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        // generate new button with quizzes data
                        final ArrayList questions = (ArrayList) document.getData().get("questions");

                        newButton = new Button(QuizListActivity.this);
                        newButton.setText(document.getData().get("name").toString());
                        newButton.setClickable(true);
                        newButton.setGravity(Gravity.CENTER);
                        newButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(QuizListActivity.this, QuizActivity.class);
                                Bundle b = new Bundle();
                                b.putString("name", document.getData().get("name").toString());
                                b.putSerializable("questions", questions);
                                Integer key = 0;
                                for(Integer i = scores.size(); i>=0; i--) {
                                    if(scores.get(document.getData().get("name").toString()) != null) break;
                                    key++;
                                }
                                b.putInt("key", key);
                                b.putSerializable("scoresList", scoresList);

                                intent.putExtras(b);
                                startActivity(intent);
                            }
                        });
                        Drawable img =  getDrawable( R.drawable.award );
                        Double questionsCount = 0.0;
                        if(questions != null) {
                            questionsCount = Double.valueOf(questions.size());
                        }
                            if(scores.get(document.getData().get("name").toString()) != null) {
                                Double score = Double.valueOf(scores.get(document.getData().get("name").toString()));
                                if(score != 0 && questionsCount != 0) {
                                    Double percentage = (score/questionsCount) * 100;
                                    if(percentage >= 80.0) {
                                        img =  getDrawable( R.drawable.awardgold );
                                    } else if(percentage >= 50.0) {
                                        img =  getDrawable( R.drawable.awardsilver );
                                    } else if(percentage >= 30.0) {
                                        img =  getDrawable( R.drawable.awardbronze );
                                    }
                                }
                            }
                        img.setBounds( 0, 0, 60, 60 );
                        newButton.setCompoundDrawables( img, null, img, null );
                        quizListPanel.addView(newButton);

                        // margin button
                        newButton = new Button(QuizListActivity.this);
                        newButton.setVisibility(View.INVISIBLE);
                        quizListPanel.addView(newButton);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                findViewById(R.id.quizListPanel).setVisibility(View.VISIBLE);
            }
        });
    }


}
