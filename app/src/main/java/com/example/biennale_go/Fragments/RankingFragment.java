package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.biennale_go.Adapters.QuizListAdapter;
import com.example.biennale_go.Adapters.RankingListAdapter;
import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.RankingItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

public class RankingFragment extends Fragment implements RankingListAdapter.OnItemClick {
    private RecyclerView recyclerView;
    private List<RankingItem> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText newQuizName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.ranking_list_recycler);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RankingListAdapter(items, this);
        recyclerView.setAdapter(adapter);
        newQuizName = (EditText) view.findViewById(R.id.fieldNewQuiz);

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        items.add(new RankingItem(document.get("name").toString(),
                                Integer.parseInt(document.get("score").toString()),
                                Double.parseDouble(document.get("distance_traveled").toString())));
                        adapter.notifyDataSetChanged();
                    }
                    Collections.sort(items, new Comparator<RankingItem>() {
                        @Override
                        public int compare(RankingItem o1, RankingItem o2) {
                            return Integer.valueOf(o2.getScore()).compareTo(Integer.valueOf(o1.getScore()));
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                for (int i=0;i<items.size();i++) {
                    if (items.get(i).getName().equals(CurrentUser.name)) {
                        recyclerView.smoothScrollToPosition(i);
                    }
                }
            }
        });
        return view;
    }


    @Override
    public void onItemClick(int posision) {
//        Bundle bundle = new Bundle();
//        bundle.putString("quizName", items.get(posision));
//        Fragment testFragment = new AdminQuizQuestionListFragment();
//        testFragment.setArguments(bundle);
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container, testFragment);
//        fragmentTransaction.commit();
    }

}
