package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class RoutesListActivity extends AppCompatActivity {
    private LinearLayout routesListPanel;
    private Button newButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);
        routesListPanel = (LinearLayout) findViewById(R.id.routesListPanel);

        fetchRoutes();
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
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        final ArrayList streets = (ArrayList) document.getData().get("streets");
                        final String name = (String) document.getData().get("name");
                        final String description = (String) document.getData().get("description");
                        final ArrayList polyline = polylineSerialize((ArrayList) document.getData().get("polyline"));
                        final String color = (String) document.getData().get("color");
                        final String image = (String) document.getData().get("image");

                        newButton = new Button(RoutesListActivity.this);
                        newButton.setText(name);
                        newButton.setClickable(true);
                        newButton.setGravity(Gravity.CENTER);
                        newButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(RoutesListActivity.this, RoutesDetailsActivity.class);
                                Bundle b = new Bundle();
                                b.putString("name", name);
                                b.putString("description", description);
                                b.putSerializable("polyline", polyline);
                                b.putString("image", image);
                                b.putString("color", color);
                                b.putSerializable("streets", streets);

                                intent.putExtras(b);
                                startActivity(intent);
                            }
                        });

                        routesListPanel.addView(newButton);

                        // margin button
                        newButton = new Button(RoutesListActivity.this);
                        newButton.setVisibility(View.INVISIBLE);
                        routesListPanel.addView(newButton);
                    }
                } else {
                    Log.d("Error!", "Error getting documents: ", task.getException());
                }
                findViewById(R.id.routesListPanel).setVisibility(View.VISIBLE);
            }
        });
    }
}
