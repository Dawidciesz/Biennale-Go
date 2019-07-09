package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.biennale_go.Adapters.QuizListAdapter;
import com.example.biennale_go.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class QuizListFragment extends Fragment implements QuizListAdapter.OnItemClick {
    private RecyclerView recyclerView;
    private List<String> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button addQuiz;
    private EditText newQuizName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QuizListAdapter(items, this);
        recyclerView.setAdapter(adapter);
        newQuizName = (EditText) view.findViewById(R.id.fieldNewQuiz);
        newQuizName.setVisibility(View.GONE);
        addQuiz = (Button) view.findViewById(R.id.addNewQuizButton);
        addQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newQuizName.isShown()) {
                    newQuizName.setVisibility(View.VISIBLE);
                    addQuiz.setText("Stwórz quiz");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("quizName", newQuizName.getText().toString());
                    Fragment testFragment = new AddQuestionFragment();
                    testFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment_container, testFragment);
                    fragmentTransaction.commit();
                    newQuizName.setVisibility(View.GONE);
                    addQuiz.setText("Dodaj nowy quiz");
                    addNewQuiz();
                }
            }
        });

        addItemsToAdapter();
        db.collection("quizes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        items.add(document.getId());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });
        return view;
    }

    public void addItemsToAdapter() {

    }

    @Override
    public void onItemClick(int posision) {
        Bundle bundle = new Bundle();
        bundle.putString("quizName", items.get(posision));
        Fragment testFragment = new QuizQuestionListFragment();
        testFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, testFragment);
        fragmentTransaction.commit();
    }

    private void addNewQuiz() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("name", addQuiz.getText().toString());
        db.collection("quizes").document(addQuiz.getText().toString()).collection("questions");
        db.collection("quizes").document(addQuiz.getText().toString()).set(docData);
    }
}
