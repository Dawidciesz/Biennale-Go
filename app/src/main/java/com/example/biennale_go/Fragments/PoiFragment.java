package com.example.biennale_go.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PoiFragment extends Fragment {
    private Bundle b;
    private LinearLayout poiPanel;
    private RelativeLayout loadingPanel;
    private ArrayList<String> names, scores, addresses, descriptions, images;
    private Button newButton;
    private String id = CurrentUser.uId;
    private View view;
    private static final String TAG = "PoiFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_poi, container, false);
        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);
        poiPanel = (LinearLayout) view.findViewById(R.id.poiPanel);
        b = getArguments();

        if (b != null) {
            names = new ArrayList((ArrayList) b.getSerializable("names"));
            scores = new ArrayList((ArrayList) b.getSerializable("scores"));
            images = new ArrayList((ArrayList) b.getSerializable("images"));
            addresses = new ArrayList((ArrayList) b.getSerializable("addresses"));
            descriptions = new ArrayList((ArrayList) b.getSerializable("descriptions"));
            generatePoiButtons();
            switchLoadingPanel();
        } else {
            fetchPOIScores();
        }
        return view;
    }

    private void switchLoadingPanel() {
        if(loadingPanel.getVisibility() != View.GONE) {
            loadingPanel.setVisibility(View.GONE);
            poiPanel.setVisibility(View.VISIBLE);
        }
    }

    private void fetchPOI() {
        names = new ArrayList();
        images = new ArrayList();
        addresses = new ArrayList();
        descriptions = new ArrayList();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("POI");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getData().get("name").toString();
                        String image = document.getData().get("image").toString();
                        String address = document.getData().get("address").toString();
                        String description = document.getData().get("description").toString();
                        names.add(name);
                        images.add(image);
                        addresses.add(address);
                        descriptions.add(description);
                    }
                    generatePoiButtons();
                    switchLoadingPanel();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


    private void fetchPOIScores() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("POI_scores").document(id);
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
                        data.put("scores",scores);
                        db.collection("POI_scores").document(CurrentUser.uId).set(data);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                fetchPOI();
            }
        });
    }

    private void generatePoiButtons() {
        for (Integer i = 0; i<names.size(); i++) {
            final String name = names.get(i);
            final String image = images.get(i);
            final String address = addresses.get(i);
            final String description = descriptions.get(i);

            newButton = new Button(getContext());
            newButton.setText(name);
            newButton.setClickable(true);
            newButton.setGravity(Gravity.CENTER);
            newButton.setBackgroundColor(Color.parseColor("#ffffff"));
            newButton.setTextColor(Color.parseColor("#00574b"));
            newButton.setPadding(10,0,10,0);
            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("name", name);
                    b.putString("image", image);
                    b.putString("address", address);
                    b.putString("description", description);
                    b.putBoolean("checked", scores.contains(name));

                    Fragment testFragment = new PoiDetailsFragment();
                    testFragment.setArguments(b);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, testFragment);
                    fragmentTransaction.commit();
                }
            });
            if(scores.contains(name)) {
                Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.checked );
                img.setBounds( 0, 0, 60, 60 );
                newButton.setCompoundDrawables( img, null, img, null );
            }
            poiPanel.addView(newButton);
            newButton = new Button(getContext());
            newButton.setVisibility(View.INVISIBLE);
            poiPanel.addView(newButton);
        }
    }
}
