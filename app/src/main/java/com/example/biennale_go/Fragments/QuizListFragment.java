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
import com.example.biennale_go.Utility.CurrentUser;
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
    private View view;
    private String id = CurrentUser.uId;
    private ArrayList questions = new ArrayList(), quizzesNames = new ArrayList(), scoresList = new ArrayList();
    private Map<String, Integer> scores = new HashMap<String, Integer>();
    private Map<String, Object> quizData = new HashMap<>();
    private static final String TAG = "QuizListActicity";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_quiz_list, container, false);
        super.onCreate(savedInstanceState);
        quizListPanel = (LinearLayout) view.findViewById(R.id.quizListPanel);
        fetchQuizzesNames();
        return view;
    }

    public void fetchScores() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("quizzes_scores").document(id);
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
                            scores.put(name, score);
                        }
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        ArrayList scoresArray = new ArrayList();
                        for(Integer i = 0; i<quizzesNames.size(); i++) {
                            Map<String,String> tempMap = new HashMap<>();
                            tempMap.put("name", quizzesNames.get(i).toString());
                            tempMap.put("score","0");
                            scoresArray.add(tempMap);
                        }
                        scoresList = scoresArray;
                        for(int i=0; i<scoresList.size(); i++) {
                            Map<String, String> temp = new HashMap<String, String>();
                            temp.putAll((HashMap<String,String>)scoresList.get(i));
                            String name = temp.get("name");
                            Integer score = Integer.parseInt(temp.get("score"));
                            scores.put(name, score);
                        }
                        data.put("scores",scoresArray);
                        db.collection("quizzes_scores").document(CurrentUser.uId).set(data);
                    }
                    addButtons();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void fetchQuizzesNames() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("quizes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().get("name") != null) {
                            quizzesNames.add(document.getData().get("name").toString());
                        }
                    }
                    fetchQuizzesData();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void fetchQuizzesData() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        for(final Object name : quizzesNames) {
            CollectionReference docRef = db.collection("quizes").document(name.toString()).collection("questions");
            docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList questionsArray = new ArrayList();
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            questionsArray.add(document.getData());
                        }
                        quizData.put(name.toString(), questionsArray);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
        fetchScores();
    }

    public void addButtons() {
        for(Integer i  = 0; i<quizzesNames.size(); i++) {
            newButton = new Button(getContext());

            final Integer j = i;
            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("name", quizzesNames.get(j).toString());
                    if(quizData.get(quizzesNames.get(j).toString()) != null) {
                        b.putSerializable("questions", (ArrayList) quizData.get(quizzesNames.get(j).toString()));
                    }
                    ArrayList scoresList = new ArrayList();
                    scoresList.add(scores);
                    b.putSerializable("scoresList", scoresList);
                    Fragment testFragment = new QuizFragment();
                    testFragment.setArguments(b);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, testFragment);
                    fragmentTransaction.commit();
                }
            });
            newButton.setText(quizzesNames.get(j).toString());
            newButton.setClickable(true);
            newButton.setGravity(Gravity.CENTER);
            newButton.setBackgroundColor(Color.parseColor("#ffffff"));
            newButton.setTextColor(Color.parseColor("#00574b"));
            newButton.setPadding(10,0,10,0);
            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.award);
            Double questionsCount = 0.0;
            if(quizData.get(quizzesNames.get(j).toString()) != null) {
                    ArrayList tempArray = (ArrayList) quizData.get(quizzesNames.get(j).toString());
                    questionsCount = Double.valueOf(tempArray.size());
            }
            if(scores.get(quizzesNames.get(j).toString()) != null) {
                Double score = Double.valueOf(scores.get(quizzesNames.get(j).toString()));
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
            newButton = new Button(getContext());
            newButton.setVisibility(View.INVISIBLE);
            quizListPanel.addView(newButton);
        }
        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        view.findViewById(R.id.quizListPanel).setVisibility(View.VISIBLE);
    }
}
