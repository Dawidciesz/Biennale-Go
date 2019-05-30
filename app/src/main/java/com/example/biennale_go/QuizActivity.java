package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizActivity extends AppCompatActivity {
    private TextView questionNumberTextView;
    private TextView questionDescriptionTextView;
    private Button questionAnswerA;
    private Button questionAnswerB;
    private Button questionAnswerC;
    private Button questionAnswerD;

    private Integer questionNumber = 1;
    private Integer maxQuestionNumber;
    private ArrayList questions;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        questionDescriptionTextView = (TextView) findViewById(R.id.questionDescriptionTextView);
        questionAnswerA = (Button) findViewById(R.id.answerA);
        questionAnswerB = (Button) findViewById(R.id.answerB);
        questionAnswerC = (Button) findViewById(R.id.answerC);
        questionAnswerD = (Button) findViewById(R.id.answerD);

        b = getIntent().getExtras();

        if (b != null) {
            questions = new ArrayList((ArrayList) b.getSerializable("questions"));
            maxQuestionNumber = questions.size();
            fillQuestion();
        }
    }


    private void fillQuestion() {
        HashMap<String, String> question = new HashMap<>((HashMap<String, String>) questions.get(questionNumber));
        questionNumberTextView.setText("Pytanie nr" + (questionNumber + 1));
        questionDescriptionTextView.setText(question.get("description"));
        questionAnswerA.setText(question.get("answerA"));
        questionAnswerB.setText(question.get("answerB"));
        questionAnswerC.setText(question.get("answerC"));
        questionAnswerD.setText(question.get("answerD"));
    }
}
