package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.example.biennale_go.Adapters.RankingListAdapter;
import com.example.biennale_go.MainActivity;
import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.RankingItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    private ArrayList<String> scores = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).onSlideViewButtonClick();
            }                // Handle the back button event

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        recyclerView = (RecyclerView) view.findViewById(R.id.ranking_list_recycler);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        fetchPOIScores();
        adapter = new RankingListAdapter(items, this);
        recyclerView.setAdapter(adapter);
        newQuizName = (EditText) view.findViewById(R.id.fieldNewQuiz);

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("name").toString().equals(CurrentUser.name)) {

                            items.add(new RankingItem(document.get("name").toString(),
                                    Integer.parseInt(document.get("score").toString()) + (scores.size() * 3),
                                    Double.parseDouble(document.get("distance_traveled").toString()),
                                    document.get("profile_img").toString(), document.get("profile_color").toString()));
                            adapter.notifyDataSetChanged();
                        } else {
                            items.add(new RankingItem(document.get("name").toString(),
                                    Integer.parseInt(document.get("score").toString()),
                                    Double.parseDouble(document.get("distance_traveled").toString()),
                                    document.get("profile_img").toString(), document.get("profile_color").toString()));
                            adapter.notifyDataSetChanged();
                        }
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

    private void fetchPOIScores() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("POI_scores").document(CurrentUser.uId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        scores = (ArrayList<String>) document.getData().get("scores");
                    } else {
                        scores = new ArrayList<String>();
                        Map<String, Object> data = new HashMap<>();
                        data.put("scores", scores);
                        db.collection("POI_scores").document(CurrentUser.uId).set(data);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onItemClick(int posision) {
    }

}
