package com.example.biennale_go.Fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.biennale_go.R;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizLevelFragment extends Fragment {
    private LinearLayout quizLevelPanel, headerContainer;
    private RelativeLayout loadingPanel, preLayout;
    private TextView quizNameTextView;
    private Bundle b;
    private String name;
    private View view;
    private Button newButton;
    private Integer quizLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_quiz_level, container, false);

        quizNameTextView = (TextView) view.findViewById(R.id.quizNameTextView);
        quizLevelPanel = (LinearLayout) view.findViewById(R.id.quizLevelPanel);
        headerContainer = (LinearLayout) view.findViewById(R.id.headerContainer);
        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);
        preLayout = (RelativeLayout) view.findViewById(R.id.preLayout);

        b = getArguments();

        if (b != null) {
            name = b.getString("name");
            quizNameTextView.setText(name);
            addButtons();
            headerContainer.setVisibility(View.VISIBLE);
            loadingPanel.setVisibility(View.GONE);
        }

        return view;
    }

    public void addButtons() {
//        for(Integer i  = 0; i<quizzesNames.size(); i++) {
        for(Integer i  = 1; i<6; i++) {
            newButton = new Button(getContext());

            final Integer j = i;
            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    quizLevel = j;
                    preLayout.setVisibility(View.VISIBLE);

//                    Bundle b = new Bundle();
//                    b.putString("name", name);
//                    if(quizData.get(quizzesNames.get(j).toString()) != null) {
//                        b.putSerializable("questions", (ArrayList) quizData.get(quizzesNames.get(j).toString()));
//                    }
//                    ArrayList scoresList = new ArrayList();
//                    scoresList.add(scores);
//                    b.putSerializable("scoresList", scoresList);
//                    Fragment testFragment = new QuizLevelFragment();
//                    testFragment.setArguments(b);
//                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment_container, testFragment);
//                    fragmentTransaction.commit();
                }
            });
//            newButton.setText(quizzesNames.get(j).toString());
            newButton.setText("QUIZ POZIOM "+j.toString());
            newButton.setClickable(true);
            newButton.setGravity(Gravity.LEFT);
            newButton.setBackgroundResource(R.drawable.list_button_selector);
            newButton.setTextColor(Color.parseColor("#000000"));
            newButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
            newButton.setPadding(10,30,10,0);
            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.award);
//            Double questionsCount = 0.0;
//            if(quizData.get(quizzesNames.get(j).toString()) != null) {
//                    ArrayList tempArray = (ArrayList) quizData.get(quizzesNames.get(j).toString());
//                    questionsCount = Double.valueOf(tempArray.size());
//            }
//            if(scores.get(quizzesNames.get(j).toString()) != null) {
//                Double score = Double.valueOf(scores.get(quizzesNames.get(j).toString()));
//                if(score != 0 && questionsCount != 0) {
//                    Double percentage = (score/questionsCount) * 100;
//                    if(percentage >= 80.0) {
//                        img =   ContextCompat.getDrawable(getContext(), R.drawable.awardgold );
//                    } else if(percentage >= 50.0) {
//                        img =   ContextCompat.getDrawable(getContext(), R.drawable.awardsilver );
//                    } else if(percentage >= 30.0) {
//                        img =  ContextCompat.getDrawable(getContext(), R.drawable.awardbronze );
//                    }
//                }
//            }
            img =   ContextCompat.getDrawable(getContext(), R.drawable.lockopen );
            img.setBounds( 0, -10, 40, 40 );
            newButton.setCompoundDrawables( null, null, img, null );
            quizLevelPanel.addView(newButton);
            TextView spaceView = new TextView(getContext());
            spaceView.setVisibility(View.INVISIBLE);
            spaceView.setHeight(20);
            spaceView.setPadding(0,0,0,0);
            quizLevelPanel.addView(spaceView);
        }
        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        view.findViewById(R.id.quizLevelPanel).setVisibility(View.VISIBLE);
    }
}
