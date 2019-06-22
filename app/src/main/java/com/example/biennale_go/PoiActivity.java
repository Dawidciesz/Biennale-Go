package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PoiActivity extends AppCompatActivity {
    private Bundle b;
    private LinearLayout poiPanel;
    private RelativeLayout loadingPanel;
    private ArrayList<String> names, scores, addresses, descriptions, images;
    private Button newButton;
    private static final String TAG = "PoiActivity";
    //    TODO GLOBAL ID
    private Integer id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        poiPanel = (LinearLayout) findViewById(R.id.poiPanel);
        b = getIntent().getExtras();

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
//                        Double latitude = (Double) document.getData().get("latitude");
//                        Double longitude = (Double) document.getData().get("longitude");
//                        LatLng POI = new LatLng(latitude, longitude);
                        names.add(name);
                        images.add(image);
                        addresses.add(address);
                        descriptions.add(description);
//                        poiLatitude.add(latitude);
//                        poiLongitude.add(longitude);
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
        DocumentReference docRef = db.collection("POI_scores").document(id.toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        scores = (ArrayList<String>) document.getData().get("scores");
                    } else {
                        Log.d(TAG, "No such document");
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

            newButton = new Button(PoiActivity.this);
            newButton.setText(name);
            newButton.setClickable(true);
            newButton.setGravity(Gravity.CENTER);
            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PoiActivity.this, PoiDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("name", name);
                    b.putString("image", image);
                    b.putString("address", address);
                    b.putString("description", description);
                    b.putBoolean("checked", scores.contains(name));
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
            if(scores.contains(name)) {
                Drawable img =  getDrawable( R.drawable.checked );
                img.setBounds( 0, 0, 60, 60 );
                newButton.setCompoundDrawables( img, null, img, null );
            }
            poiPanel.addView(newButton);
            // TODO FIX MARGINS
            newButton = new Button(PoiActivity.this);
            newButton.setVisibility(View.INVISIBLE);
            poiPanel.addView(newButton);
        }
    }
}
