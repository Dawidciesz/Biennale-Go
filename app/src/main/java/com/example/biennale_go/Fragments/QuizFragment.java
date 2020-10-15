package com.example.biennale_go.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.biennale_go.Classes.QuizPicture;
import com.example.biennale_go.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class QuizFragment extends Fragment {
    private TextView questionNumberTextView, questionDescriptionTextView;
    private Button questionAnswerA, questionAnswerB, questionAnswerC, questionAnswerD;
    private ImageView imageAnswerA, imageAnswerB, imageAnswerC, imageAnswerD;
    private ArrayList scoresList = new ArrayList(), questions;

    private Integer questionNumber = 0, maxQuestionNumber;
    private Bundle b;
    private ImageView questionNumberImage;
    private String name;
    private Integer points = 0;
    private RelativeLayout buttonsView, imageView;
    private View view;
    private ArrayList<QuizPicture> quizPictures = new ArrayList<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

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

       imageAnswerA = (ImageView) view.findViewById(R.id.imageA);
       imageAnswerB = (ImageView) view.findViewById(R.id.imageB);
       imageAnswerC = (ImageView) view.findViewById(R.id.imageC);
       imageAnswerD = (ImageView) view.findViewById(R.id.imageD);
        questionNumberImage = (ImageView) view.findViewById(R.id.imageView4);

        buttonsView = (RelativeLayout) view.findViewById(R.id.buttonsView);
        imageView = (RelativeLayout) view.findViewById(R.id.imageView);

        b = getArguments();

        if (b != null) {
            if(b.getSerializable("questions") != null) {

            }
            name = b.getString("name");
            questions = new ArrayList((ArrayList) b.getSerializable("questions"));
            quizPictures =  new ArrayList((ArrayList)  b.getSerializable("images"));
            if(b.getSerializable("scoresList") != null)
            scoresList = new ArrayList((ArrayList) b.getSerializable("scoresList"));
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
        imageAnswerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("a");
            }
        });
        imageAnswerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("b");
            }
        });
        imageAnswerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("c");
            }
        });
        imageAnswerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("d");
            }
        });
        return view;
    }

//    private Drawable getImage(String path) {
//                StorageReference islandRef = storageRef.child(path);
//                Drawable x;
//                final long ONE_MEGABYTE = 1024 * 1024;
//                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//
//                        Drawable d = Drawable.createFromStream(new ByteArrayInputStream(bytes), null);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle any errors
//                    }
//        });
//        return x;
//    }
    private void fillQuestion() {
        Resources res = getResources();
        HashMap<String, String> question = new HashMap<>((HashMap<String, String>) questions.get(questionNumber));
        questionNumberTextView.setText("Pytanie nr " + (questionNumber + 1));
        questionDescriptionTextView.setText(question.get("description"));
        if (question.get("answerA").contains("quizzes")) {
            for (int i = 0; i < quizPictures.size(); i++) {
                if (Integer.parseInt(quizPictures.get(i).getQuestionNumber()) == (questionNumber+1)) {
                    imageAnswerA.setImageDrawable(quizPictures.get(i).getA());
                    imageAnswerB.setImageDrawable(quizPictures.get(i).getB());
                    imageAnswerC.setImageDrawable(quizPictures.get(i).getC());
                    imageAnswerD.setImageDrawable(quizPictures.get(i).getD());
                    buttonsView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                }
            }
        }
        else {
            questionAnswerA.setText(question.get("answerA"));
            questionAnswerB.setText(question.get("answerB"));
            questionAnswerC.setText(question.get("answerC"));
            questionAnswerD.setText(question.get("answerD"));
            buttonsView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }


        int resId = getResources().getIdentifier("quiznum" + (questionNumber + 1),"drawable",getActivity().getPackageName());
        questionNumberImage.setImageDrawable(res.getDrawable(resId));
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
            b2.putString("name",name);
            b2.putInt("points", points);
            b2.putInt("maxPoints", maxQuestionNumber+1);
            b2.putSerializable("scoresList", scoresList);
            Fragment testFragment = new QuizSummaryFragment();
            testFragment.setArguments(b2);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, testFragment);
            fragmentTransaction.commit();
        }
    }
}
