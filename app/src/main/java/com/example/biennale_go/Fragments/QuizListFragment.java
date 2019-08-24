package com.example.biennale_go.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.biennale_go.R;
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

public class QuizListFragment extends Fragment {
    private LinearLayout quizListPanel;
    private Button newButton;
    private ArrayList scoresList;
    private static final String TAG = "QuizListActicity";
    private  View view;
//    TODO GLOBAL ID
    private Integer id = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_quiz_list, container, false);
        super.onCreate(savedInstanceState);
        quizListPanel = (LinearLayout) view.findViewById(R.id.quizListPanel);
        scoresList = new ArrayList();
        fetchScores();
        return view;
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
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        // generate new button with quizzes data
                        final ArrayList questions = (ArrayList) document.getData().get("questions");

                        newButton = new Button(getContext());
                        newButton.setText(document.getData().get("name").toString());
                        newButton.setClickable(true);
                        newButton.setGravity(Gravity.CENTER);
                        newButton.setBackgroundColor(Color.parseColor("#ffffff"));
                        newButton.setTextColor(Color.parseColor("#00574b"));
                        newButton.setPadding(10,0,10,0);
                        newButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
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
                                Fragment testFragment = new QuizFragment();
                                testFragment.setArguments(b);
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, testFragment);
                                fragmentTransaction.commit();
                            }
                        });
                        Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.award);
                        Double questionsCount = 0.0;
                        if(questions != null) {
                            questionsCount = Double.valueOf(questions.size());
                        }
                            if(scores.get(document.getData().get("name").toString()) != null) {
                                Double score = Double.valueOf(scores.get(document.getData().get("name").toString()));
                                if(score != 0 && questionsCount != 0) {
                                    Double percentage = (score/questionsCount) * 100;
                                    if(percentage >= 80.0) {
                                        img =   ContextCompat.getDrawable(getContext(), R.drawable.awardgold );
                                    } else if(percentage >= 50.0) {
                                        img =   ContextCompat.getDrawable(getContext(), R.drawable.awardsilver );
                                    } else if(percentage >= 30.0) {
                                        img =  ContextCompat.getDrawable(getContext(), R.drawable.awardbronze );
                                    }
                                }
                            }
                        img.setBounds( 0, 0, 60, 60 );
                        newButton.setCompoundDrawables( img, null, img, null );
                        quizListPanel.addView(newButton);

                        // margin button
                        newButton = new Button(getContext());
                        newButton.setVisibility(View.INVISIBLE);
                        quizListPanel.addView(newButton);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                view.findViewById(R.id.quizListPanel).setVisibility(View.VISIBLE);
            }
        });
    }


}
