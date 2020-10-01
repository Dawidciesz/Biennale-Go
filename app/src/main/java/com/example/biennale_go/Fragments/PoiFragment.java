package com.example.biennale_go.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.biennale_go.Classes.PoiClass;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PoiFragment extends Fragment {
    private Bundle b;
    private LinearLayout poiPanel;
    private RelativeLayout loadingPanel;
    private ArrayList<String> scores;
    private ArrayList<PoiClass> poiList;
    private Button newButton;
    private String id = CurrentUser.uId;
    private View view;
    private ImageView galleryLogo;
    private boolean firstSign = true;
    private static final String TAG = "PoiFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_poi, container, false);
        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);
        poiPanel = (LinearLayout) view.findViewById(R.id.poiPanel);

        galleryLogo = (ImageView) view.findViewById(R.id.galleryLogo);
        galleryLogo.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.loading_scale));

        b = getArguments();

        if (b != null) {
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
        poiList = new ArrayList();

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
                        Double latitude = (Double) document.getData().get("latitude");
                        Double longitude = (Double) document.getData().get("longitude");

                        PoiClass newPoi = new PoiClass(name, description, address, image, latitude, longitude);

                        poiList.add(newPoi);
                    }
                    sortPoiList();
                    generatePoiButtons();
                    switchLoadingPanel();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void sortPoiList(){
        Comparator<PoiClass> compareByName = new Comparator<PoiClass>() {
            @Override
            public int compare(PoiClass o1, PoiClass o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        Collections.sort(poiList, compareByName);
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
        String categorySign = "";

        for (Integer i = 0; i<poiList.size(); i++) {
            final String name = poiList.get(i).getName();
            final String image = poiList.get(i).getImage();
            final String address = poiList.get(i).getAddress();
            final String description = poiList.get(i).getDescription();

            if(!categorySign.equals(name.substring(0, 1))){
                categorySign = name.substring(0, 1);
                generateCategorySign(categorySign);
            }

            newButton = new Button(getContext());
            newButton.setText(name);
            newButton.setClickable(true);
            newButton.setGravity(Gravity.CENTER);
            newButton.setBackgroundResource(R.drawable.list_button_selector);
            newButton.setTextColor(Color.parseColor("#000000"));
            newButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
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
//            if(scores.contains(name)) {
//                Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.checked );
//                img.setBounds( 0, 0, 60, 60 );
//                newButton.setCompoundDrawables( img, null, img, null );
//            }
            poiPanel.addView(newButton);
        }
    }

    private void generateCategorySign(String categorySign) {
        if(firstSign){
        firstSign = false;
    }else{
        newButton = new Button(getContext());
        newButton.setText("");
        newButton.setClickable(false);
        newButton.setBackgroundColor(Color.parseColor("#ffffff"));
        newButton.setTextColor(Color.parseColor("#000000"));
        poiPanel.addView(newButton);
    }

        newButton = new Button(getContext());
        newButton.setText(categorySign.toUpperCase());
        newButton.setClickable(false);
        newButton.setGravity(Gravity.CENTER);
        newButton.setBackgroundColor(Color.parseColor("#ffffff"));
        newButton.setTextColor(Color.parseColor("#000000"));
        newButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);

        poiPanel.addView(newButton);
    }
}
