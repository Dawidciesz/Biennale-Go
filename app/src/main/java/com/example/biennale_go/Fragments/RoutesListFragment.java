package com.example.biennale_go.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.biennale_go.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RoutesListFragment extends Fragment {
    private LinearLayout routesListPanel;
    private Button newButton;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_routes_list, container, false);
        routesListPanel = (LinearLayout) view.findViewById(R.id.routesListPanel);
        fetchRoutes();
        return view;
    }

    public ArrayList polylineSerialize(ArrayList polyline) {
        ArrayList newPolyline = new ArrayList();
        for(Integer i = 0; i<polyline.size(); i++) {
            GeoPoint geoPoint = (GeoPoint) polyline.get(i);
            ArrayList latLong = new ArrayList();
            latLong.add(geoPoint.getLatitude());
            latLong.add(geoPoint.getLongitude());
            newPolyline.add(latLong);
        }
        return newPolyline;
    }

    public void fetchRoutes() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("routes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        final ArrayList streets = (ArrayList) document.getData().get("streets");
                        final String name = (String) document.getData().get("name");
                        final String description = (String) document.getData().get("description");
                        final ArrayList polyline = polylineSerialize((ArrayList) document.getData().get("polyline"));
                        final String color = (String) document.getData().get("color");
                        final String image = (String) document.getData().get("image");

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
                                b.putString("description", description);
                                b.putSerializable("polyline", polyline);
                                b.putString("image", image);
                                b.putString("color", color);
                                b.putSerializable("streets", streets);
                                Fragment testFragment = new RoutesDetailsFragment();
                                testFragment.setArguments(b);
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, testFragment);
                                fragmentTransaction.commit();
                            }
                        });
                        routesListPanel.addView(newButton);
                        newButton = new Button(getContext());
                        newButton.setVisibility(View.INVISIBLE);
                        routesListPanel.addView(newButton);
                    }
                } else {
                    Log.d("Error!", "Error getting documents: ", task.getException());
                }
                view.findViewById(R.id.routesListPanel).setVisibility(View.VISIBLE);
            }
        });
    }
}
