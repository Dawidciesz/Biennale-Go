package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private Button  poiButton;
    LocationManager locationManager;
    private static final String TAG = "QuizMapActicity";
    private ArrayList<String> poiNames, poiAddresses, poiDescriptions;
    private ArrayList<Double> poiLatitude, poiLongitude;
    private RelativeLayout loadingPanel, mapPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        mapPanel = (RelativeLayout) findViewById(R.id.mapPanel);

        poiNames = new ArrayList<String>();
        poiAddresses = new ArrayList<String>();
        poiDescriptions = new ArrayList<String>();
        poiLatitude = new ArrayList<Double>();
        poiLongitude = new ArrayList<Double>();
        poiButton = (Button) findViewById(R.id.poiButton);
        poiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPoiActivity();
            }
        });

        getLocation();
    }

    public void openPoiActivity(){
        Intent intent = new Intent(this, PoiActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("names", poiNames);
        b.putSerializable("addresses", poiAddresses);
        b.putSerializable("descriptions", poiDescriptions);
        intent.putExtras(b);
        startActivity(intent);
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    void setMapLocation(double latitude, double longtitude) {
        LatLng userCurrentLocation = new LatLng(latitude, longtitude);
        float zoomLevel = 17.0f;
        mMap.addMarker(new MarkerOptions().position(userCurrentLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, zoomLevel));

        if(loadingPanel.getVisibility() != View.GONE) {
            loadingPanel.setVisibility(View.GONE);
            mapPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchPOI();
    }

    @Override
    public void onLocationChanged(Location location) {
        setMapLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(MapsActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fetchPOI() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("POI");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getData().get("name").toString();
                        String address = document.getData().get("address").toString();
                        String description = document.getData().get("description").toString();
                        Double latitude = (Double) document.getData().get("latitude");
                        Double longitude = (Double) document.getData().get("longitude");
                        LatLng POI = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(POI).title(name));
                        poiNames.add(name);
                        poiAddresses.add(address);
                        poiDescriptions.add(description);
                        poiLatitude.add(latitude);
                        poiLongitude.add(longitude);
//                        float zoomLevel = 17.0f;
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(POI, zoomLevel));
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
