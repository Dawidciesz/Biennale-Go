package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.biennale_go.Adapters.POIListAdapter;
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


public class AdminPOIListFragment extends Fragment implements POIListAdapter.OnItemClick {
    private RecyclerView recyclerView;
    private List<String> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button addPOI;
    private EditText newPOIName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new POIListAdapter(items, this);
        recyclerView.setAdapter(adapter);
        newPOIName = (EditText) view.findViewById(R.id.fieldNewQuiz);
        newPOIName.setVisibility(View.GONE);
        addPOI = (Button) view.findViewById(R.id.addNewQuizButton);
        addPOI.setText("Dodaj nowy POI");
        addPOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newPOIName.isShown()) {
                    newPOIName.setVisibility(View.VISIBLE);
                    addPOI.setText("Stwórz nowe POI");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("POIName", newPOIName.getText().toString());
                    Fragment addPOIFragment = new AddPOIFragment();
                    addPOIFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, addPOIFragment);
                    fragmentTransaction.commit();
                    newPOIName.setVisibility(View.GONE);
                    addPOI.setText("Dodaj nowy POI");
                    addNewPOI();
                }
            }
        });

        addItemsToAdapter();
        db.collection("POI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
//        Bundle bundle = new Bundle();
//        bundle.putString("POIName", items.get(posision));
//        Fragment testFragment = new AdminQuizQuestionListFragment();
//        testFragment.setArguments(bundle);
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container, testFragment);
//        fragmentTransaction.commit();

        Bundle bundle = new Bundle();
        bundle.putString("POIName", items.get(posision));
        Fragment testFragment = new AddPOIFragment();
        testFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, testFragment);
        fragmentTransaction.commit();
    }

    private void addNewPOI() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("name",  newPOIName.getText().toString());
//        db.collection("POI").document( newQuizName.getText().toString()).collection("questions");
        db.collection("POI").document( newPOIName.getText().toString()).set(docData);
    }
}
