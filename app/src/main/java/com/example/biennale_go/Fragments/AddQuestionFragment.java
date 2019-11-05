package com.example.biennale_go.Fragments;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

public class AddQuestionFragment extends Fragment {
    private String quizName;
    private String questionName;

    private Button addQuiz;
    private EditText question;
    private EditText answerA;
    private EditText answerB;
    private EditText answerC;
    private EditText answerD;
    private EditText correct;
    private EditText description;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_quiz, container, false);
        addQuiz = (Button) view.findViewById(R.id.create);
        question = (EditText) view.findViewById(R.id.fieldQuestion);
        answerA = (EditText) view.findViewById(R.id.fieldAnswerA);
        answerB = (EditText) view.findViewById(R.id.fieldAnswerB);
        answerC = (EditText) view.findViewById(R.id.fieldAnswerC);
        answerD = (EditText) view.findViewById(R.id.fieldAnswerD);
        correct = (EditText) view.findViewById(R.id.correctAnswer);
        description = (EditText) view.findViewById(R.id.fieldDescription);
        addQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadQuiz();
                Bundle bundle = new Bundle();

                bundle.putString("quizName",  getArguments().getString("quizName"));

                Fragment questionListFragment = new AdminQuizQuestionListFragment();
                questionListFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, questionListFragment);
                fragmentTransaction.commit();
            }
        });

        this.quizName = getArguments().getString("quizName");
        if (getArguments().getString("questionName") != null) {
            this.questionName = getArguments().getString("questionName");
            question.setVisibility(View.GONE);
        } else {
            this.questionName = "";
        }
        return view;
    }

    private void uploadQuiz() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("answerA", answerA.getText().toString());
        docData.put("answerB", answerB.getText().toString());
        docData.put("answerC", answerC.getText().toString());
        docData.put("answerD", answerD.getText().toString());
        docData.put("correctAnswer", correct.getText().toString());
        docData.put("description", description.getText().toString());

        db.collection("quizes").document(quizName)
                .collection("questions").document(questionName.equals("") ?
                question.getText().toString() : questionName).set(docData);
    }
}

