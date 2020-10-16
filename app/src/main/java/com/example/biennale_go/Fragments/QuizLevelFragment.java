package com.example.biennale_go.Fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.biennale_go.Classes.QuizPicture;
import com.example.biennale_go.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class QuizLevelFragment extends Fragment {
    private LinearLayout quizLevelPanel, headerContainer;
    private RelativeLayout loadingPanel, preLayout;
    private TextView quizNameTextView;
    private Bundle b;
    private String name;
    private View view;
    private Button newButton, startQuizButton;
    private int index = 0;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private ArrayList<String> quizStringPictures = new ArrayList<>();
    private ArrayList<String> quizIds = new ArrayList<>();
    private ArrayList<QuizPicture> quizPictures = new ArrayList<>();
    private Integer quizLevel;
    private ArrayList<Drawable> quizDrawablePictures = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_quiz_level, container, false);

        quizNameTextView = (TextView) view.findViewById(R.id.quizNameTextView);
        quizLevelPanel = (LinearLayout) view.findViewById(R.id.quizLevelPanel);
        headerContainer = (LinearLayout) view.findViewById(R.id.headerContainer);
        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);
        preLayout = (RelativeLayout) view.findViewById(R.id.preLayout);
        startQuizButton = (Button) view.findViewById(R.id.startQuizButton);

        b = getArguments();
        if (b != null) {
            name = b.getString("name");
            fetchQuizData();
            quizNameTextView.setText(name);
            addButtons();
            headerContainer.setVisibility(View.VISIBLE);
            startQuizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment testFragment = new QuizFragment();
                    b.putSerializable("images", quizPictures);
                    testFragment.setArguments(b);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, testFragment);
                    fragmentTransaction.commit();
                }
            });
        }

        return view;
    }

    public void fetchQuizData() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("quizes").document(name).collection("questions");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().get("questionType").equals("picture")) {
                            quizStringPictures.add(document.getData().get("answerA").toString());
                            quizStringPictures.add(document.getData().get("answerB").toString());
                            quizStringPictures.add(document.getData().get("answerC").toString());
                            quizStringPictures.add(document.getData().get("answerD").toString());
                            quizIds.add(document.getId());
                        }
                    }
                    getTheImages();
                } else {
                }
            }
        });

    }

    public void getTheImages() {
        StorageReference islandRef = storageRef.child(quizStringPictures.get(index));
        final long ONE_MEGABYTE = 1024 * 1024 * 10;

        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                quizDrawablePictures.add(Drawable.createFromStream(new ByteArrayInputStream(bytes), null));
                index++;
                if (quizDrawablePictures.size() >= 4) {
                    quizPictures.add(new QuizPicture(
                            quizDrawablePictures.get(0),
                            quizDrawablePictures.get(1),
                            quizDrawablePictures.get(2),
                            quizDrawablePictures.get(3),
                            quizIds.get(0)));
                    for (int i = 0; i < 4; i++) {
                        quizDrawablePictures.remove(0);
                        quizStringPictures.remove(0);
                    }
                    quizIds.remove(0);
                    index = 0;
                }
                if (quizStringPictures.size() == 0) {
                    view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    view.findViewById(R.id.quizLevelPanel).setVisibility(View.VISIBLE);

                }
                if (quizStringPictures.size() == 0) return;
                else getTheImages();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    public void addButtons() {
        for (Integer i = 1; i < 6; i++) {
            newButton = new Button(getContext());
            final Integer j = i;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quizLevel = j;
                    preLayout.setVisibility(View.VISIBLE);
                }
            });
            newButton.setText("QUIZ POZIOM " + j.toString());
            newButton.setClickable(true);
            newButton.setGravity(Gravity.LEFT);
            newButton.setBackgroundResource(R.drawable.list_button_selector);
            newButton.setTextColor(Color.parseColor("#000000"));
            newButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
            newButton.setPadding(10, 30, 10, 0);
            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.award);
            img = ContextCompat.getDrawable(getContext(), R.drawable.lockopen);
            img.setBounds(0, -10, 40, 40);
            newButton.setCompoundDrawables(null, null, img, null);
            quizLevelPanel.addView(newButton);
            TextView spaceView = new TextView(getContext());
            spaceView.setVisibility(View.INVISIBLE);
            spaceView.setHeight(20);
            spaceView.setPadding(0, 0, 0, 0);
            quizLevelPanel.addView(spaceView);
        }
    }
}
