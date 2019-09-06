package com.example.biennale_go.Fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.biennale_go.R;
import java.util.ArrayList;
import java.util.HashMap;

public class QuizFragment extends Fragment {
    private TextView questionNumberTextView, questionDescriptionTextView;
    private Button questionAnswerA, questionAnswerB, questionAnswerC, questionAnswerD;
    private ArrayList scoresList = new ArrayList(), questions;
    private Integer questionNumber = 0, maxQuestionNumber, key;
    private Bundle b;
    private Integer points = 0;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_quiz, container, false);

        questionNumberTextView = (TextView) view.findViewById(R.id.questionNumberTextView);
        questionDescriptionTextView = (TextView) view.findViewById(R.id.questionDescriptionTextView);
        questionAnswerA = (Button) view.findViewById(R.id.answerA);
        questionAnswerB = (Button) view.findViewById(R.id.answerB);
        questionAnswerC = (Button) view.findViewById(R.id.answerC);
        questionAnswerD = (Button) view.findViewById(R.id.answerD);

        b = getArguments();

        if (b != null) {
            questions = new ArrayList((ArrayList) b.getSerializable("questions"));
            scoresList = new ArrayList((ArrayList) b.getSerializable("scoresList"));
            key = b.getInt("key");
            maxQuestionNumber = questions.size()-1;
            fillQuestion();
        }

        questionAnswerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("a");
            }
        });
        questionAnswerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("b");
            }
        });
        questionAnswerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("c");
            }
        });
        questionAnswerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("d");
            }
        });
        return view;
    }


    private void fillQuestion() {
        HashMap<String, String> question = new HashMap<>((HashMap<String, String>) questions.get(questionNumber));
        questionNumberTextView.setText("Pytanie nr " + (questionNumber + 1));
        questionDescriptionTextView.setText(question.get("description"));
        questionAnswerA.setText(question.get("answerA"));
        questionAnswerB.setText(question.get("answerB"));
        questionAnswerC.setText(question.get("answerC"));
        questionAnswerD.setText(question.get("answerD"));
    }

    private void checkAnswer(String answer) {
        HashMap<String, String> question = new HashMap<>((HashMap<String, String>) questions.get(questionNumber));
        if (answer.equals(question.get("correct"))) {
            points++;
        }
        if(!questionNumber.equals(maxQuestionNumber)) {
            questionNumber++;
            fillQuestion();
        } else {
            Bundle b2 = new Bundle();
            b2.putInt("points", points);
            b2.putInt("maxPoints", maxQuestionNumber+1);
            b2.putInt("key", key);
            b2.putSerializable("scoresList", scoresList);
            Fragment testFragment = new QuizSummaryFragment();
            testFragment.setArguments(b2);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, testFragment);
            fragmentTransaction.commit();
        }
    }
}
