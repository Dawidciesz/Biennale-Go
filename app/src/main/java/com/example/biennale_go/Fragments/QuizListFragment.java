package com.example.biennale_go.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private ImageView galleryLogo;
    private static final String TAG = "QuizListActicity";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_quiz_list, container, false);
        galleryLogo = (ImageView) view.findViewById(R.id.galleryLogo);
        galleryLogo.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.loading_scale));
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
                    view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
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
                    Fragment testFragment = new QuizLevelFragment();
                    testFragment.setArguments(b);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, testFragment);
                    fragmentTransaction.commit();
                }
            });
            newButton.setText(quizzesNames.get(j).toString());
            newButton.setClickable(true);
            newButton.setGravity(Gravity.CENTER);
            newButton.setBackgroundResource(R.drawable.list_button_selector);
            newButton.setTextColor(Color.parseColor("#000000"));
            newButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
            newButton.setPadding(10,0,10,0);

            quizListPanel.addView(newButton);
            TextView spaceView = new TextView(getContext());
            spaceView.setVisibility(View.INVISIBLE);
            spaceView.setHeight(20);
            spaceView.setPadding(0,0,0,0);
            quizListPanel.addView(spaceView);
        }
        view.findViewById(R.id.quizListPanel).setVisibility(View.VISIBLE);
        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

    }
}
