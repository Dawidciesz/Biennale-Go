package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QuizListActivity extends AppCompatActivity {
    private LinearLayout quizListPanel;
    private Button newButton;
    private static final String TAG = "QuizListActicity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        quizListPanel = (LinearLayout) findViewById(R.id.quizListPanel);
        fetchQuizzes();
    }

    public void fetchQuizzes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("quizzes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
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
                                ArrayList questions = (ArrayList) document.getData().get("questions");
                                b.putString("name", document.getData().get("name").toString());
                                b.putSerializable("questions", questions);
                                intent.putExtras(b);
                                startActivity(intent);
                            }
                        });
                        quizListPanel.addView(newButton);
                        // TODO FIX MARGINS
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
