package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    private Integer questionNumber = 0;
    private Integer maxQuestionNumber;
    private ArrayList questions;
    private Bundle b;
    private Integer points = 0;

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
            Intent intent = new Intent(QuizActivity.this, QuizSummaryActivity.class);
            Bundle b2 = new Bundle();
            b2.putInt("points", points);
            b2.putInt("maxPoints", maxQuestionNumber+1);
            intent.putExtras(b2);
            startActivity(intent);
        }
    }
}
