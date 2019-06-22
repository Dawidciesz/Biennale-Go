package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.biennale_go.Adapters.QuizQuestionListAdapter;
import com.example.biennale_go.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class QuizQuestionListFragment extends Fragment implements QuizQuestionListAdapter.OnItemClick {
    private RecyclerView recyclerView;
    private List<String> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String quizName;
    private Button addQuizQuestion;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.quizName = getArguments().getString("quizName");
        View view = inflater.inflate(R.layout.fragment_quiz_question_list, container, false);
        addQuizQuestion = (Button) view.findViewById(R.id.addNewQuizQuestionButton);
        addQuizQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("quizName", getArguments().getString("quizName"));
                Fragment testFragment = new AddQuestionFragment();
                testFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, testFragment);
                fragmentTransaction.commit();
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QuizQuestionListAdapter(items, this, getArguments().getString("quizName"));
        recyclerView.setAdapter(adapter);


        addItemsToAdapter();
        db.collection("quizes").document(quizName).collection("questions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        Log.d(TAG, "onItemClick: xdxdxd    " + posision);
        Bundle bundle = new Bundle();
        bundle.putString("quizName", getArguments().getString("quizName"));
        bundle.putString("questionName", items.get(posision));
        Fragment testFragment = new AddQuestionFragment();
        testFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container, testFragment);
        fragmentTransaction.commit();
    }
}


