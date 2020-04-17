package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.biennale_go.Utility.CurrentUser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private ImageView poiButton, routesButton;
    LocationManager locationManager;
    private static final String TAG = "QuizMapActicity";
    private ArrayList<String> poiNames, poiAddresses, poiDescriptions, poiImages, poiScores;
    private ArrayList<Double> poiLatitude, poiLongitude;
    private RelativeLayout loadingPanel, mapPanel;
    private Double POICollisionRange = (360.0 * 100.0) / 40075000.0; // 100 meters
    private ArrayList polyline;
    private Bundle b;
    private double userLat = 0;
    private double userLong = 0;
    private CameraPosition cameraPosition;
    private Button followButton;
    private String polylineColor, searchPoiName;
    private Boolean playerMarkFlag = false, followPlayerFlag = true;
    private Marker playerMarker;
    final LatLngBounds elblagBorder = new LatLngBounds(new LatLng(54.146831,19.386889  ), new LatLng(54.189640, 19.437335));
    private String id = CurrentUser.uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        mapPanel = (RelativeLayout) findViewById(R.id.mapPanel);

        followButton = (Button) findViewById(R.id.followButton);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followPlayerFlag = !followPlayerFlag;
                if (followPlayerFlag) followButton.setBackgroundColor(Color.parseColor("#018786"));
                else followButton.setBackgroundColor(Color.GRAY);
            }
        });

        b = getIntent().getExtras();
        if (b != null) {
            polyline = (ArrayList) b.getSerializable("polyline");
            polylineColor = (String) b.getString("polylineColor");
            searchPoiName = (String) b.getString("searchPoiName");
        }

        poiNames = new ArrayList<String>();
        poiScores = new ArrayList<String>();
        poiImages = new ArrayList<String>();
        poiAddresses = new ArrayList<String>();
        poiDescriptions = new ArrayList<String>();
        poiLatitude = new ArrayList<Double>();
        poiLongitude = new ArrayList<Double>();
        poiButton = (ImageView) findViewById(R.id.poiButton);
        poiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPoiActivity();
            }
        });
        routesButton = (ImageView) findViewById(R.id.routesButton);
        routesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRoutesListActivity();
            }
        });
        getLocation();
        if(polyline != null || searchPoiName != null) {
            followPlayerFlag = false; // dont follow player if routes are available
            if (followPlayerFlag) followButton.setBackgroundColor(Color.parseColor("#018786"));
            else followButton.setBackgroundColor(Color.GRAY);
        }
    }

    public void openPoiActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("names", poiNames);
        b.putSerializable("scores", poiScores);
        b.putSerializable("images", poiImages);
        b.putSerializable("addresses", poiAddresses);
        b.putSerializable("descriptions", poiDescriptions);
        intent.putExtras(b);
        intent.putExtra("fragment","Poi");
        startActivity(intent);
    }

    public void openRoutesListActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment","RoutesList");
        startActivity(intent);
    }

    void getLocation() {
        try {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                    10, this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
//                    10, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    void setMapLocation(double latitude, double longtitude) {
        LatLng userCurrentLocation = new LatLng(latitude, longtitude);
        float zoomLevel = 17.0f;
        if (userLat < 1) {
            userLat = latitude;
            userLong = longtitude;
        }
        else if(userLat != userCurrentLocation.latitude && userLong != userCurrentLocation.longitude) {
            Location locationA = new Location("A");
            locationA.setLatitude(userLat);
            locationA.setLongitude(userLong);
//            locationA.setLatitude(userLat);
//            locationA.setLongitude(userLong);
//            locationA.setLatitude(playerMarker.getPosition().latitude);
//            locationA.setLongitude(playerMarker.getPosition().longitude);
            Location locationB = new Location("B");
            locationB.setLatitude(userCurrentLocation.latitude);
            locationB.setLongitude(userCurrentLocation.longitude);
            CurrentUser.distance = Math.round(locationA.distanceTo(locationB));
            if (CurrentUser.distance >= 8 && CurrentUser.distance <= 40) {
                CurrentUser.distance_traveled += CurrentUser.distance;
                updateData();
            }
            userLat = latitude;
            userLong = longtitude;
        }
        if(!playerMarkFlag) addPlayerMarker(userCurrentLocation);
        else playerMarker.setPosition(new LatLng(latitude, longtitude));
        if(followPlayerFlag) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, zoomLevel));
        }
        checkIfPoi(latitude, longtitude);
        if(loadingPanel.getVisibility() != View.GONE) {
            loadingPanel.setVisibility(View.GONE);
            mapPanel.setVisibility(View.VISIBLE);
        }
    }
    public void updateData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("distance_traveled", CurrentUser.distance_traveled);
        data.put("score", Math.round(CurrentUser.distance_traveled /100));
        db.collection("users").document(CurrentUser.email).update(data);
    }
    void addPlayerMarker(LatLng userCurrentLocation){
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.usermarker);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        playerMarker = mMap.addMarker(new MarkerOptions().position(userCurrentLocation).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        playerMarkFlag = !playerMarkFlag;
    }

    private void checkIfPoi(double player_latitude, double player_longtitude){
        for (Integer i = 0; i<poiNames.size(); i++) {
            final Double POI_latitude = poiLatitude.get(i);
            final Double POI_longtitude = poiLongitude.get(i);

            if (player_latitude < POI_latitude + POICollisionRange &&
                    player_latitude + POICollisionRange > POI_latitude &&
                    player_longtitude < POI_longtitude + POICollisionRange &&
                    player_longtitude + POICollisionRange > POI_longtitude) {
                updatePoiScores(poiNames.get(i));
            }
        }
    }

    public void drawRoutes() {
        for(Integer i = 1; i<polyline.size();i++) {
            ArrayList firstEl = (ArrayList) polyline.get(i-1);
            ArrayList secondEl = (ArrayList) polyline.get(i);
            Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add(new LatLng((Double) firstEl.get(0), (Double) firstEl.get(1)),
                           new  LatLng((Double) secondEl.get(0), (Double) secondEl.get(1))));
            polyline1.setColor(Color.parseColor(polylineColor));
        }
        ArrayList firstEl = (ArrayList) polyline.get(0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((Double) firstEl.get(0), (Double) firstEl.get(1)), 16));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setLatLngBoundsForCameraTarget(elblagBorder);
        mMap.setMinZoomPreference(13.8f);

        mMap.setBuildingsEnabled(true);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(elblagBorder.getCenter())
//                .zoom(12)
//                .bearing(300)
//                .tilt(45)
//                .build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
//                30000, null);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder b = new LatLngBounds.Builder();
                b.include(new LatLng(54.146831,19.386889));
                b.include( new LatLng(54.189640, 19.437335));
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(b.build(),  20);
                mMap.animateCamera(cu, 10, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        Log.e(TAG, "Start animate onFinish");
                        CameraPosition cp = new CameraPosition.Builder()
                                .zoom(21.0f)
                                .target(playerMarker.getPosition())
                                .tilt(45.0f)
                                .bearing(35.0f)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "Start animate onCancel");
                    }
                });
            }
        });



        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
        fetchPOIScores();
        if(polyline != null) drawRoutes();
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
        Log.d("MapsActivity: ", "onProviderEnabled. "+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("MapsActivity: ", "onProviderDisabled. "+provider);
    }

    private void fetchPOI() {
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.checkedmarker);
        Bitmap b=bitmapdraw.getBitmap();
        final Bitmap checkedMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.questionmarker);
        b=bitmapdraw.getBitmap();
        final Bitmap questionMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

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
                        LatLng POI = new LatLng(latitude, longitude);
                        if(poiScores.contains(name)) {
                            mMap.addMarker(new MarkerOptions().position(POI).title(name).icon(BitmapDescriptorFactory.fromBitmap(checkedMarker)));
                        } else {
                            mMap.addMarker(new MarkerOptions().position(POI).title(name).icon(BitmapDescriptorFactory.fromBitmap(questionMarker)));
                        }
                        if(searchPoiName != null && searchPoiName.equals(name)) {
                            float zoomLevel = 17.0f;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(POI, zoomLevel));
                        }
                        poiNames.add(name);
                        poiImages.add(image);
                        poiAddresses.add(address);
                        poiDescriptions.add(description);
                        poiLatitude.add(latitude);
                        poiLongitude.add(longitude);
                    }
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
                        poiScores = (ArrayList<String>) document.getData().get("scores");
                    } else {
                        poiScores = new ArrayList<String>();
                        Map<String, Object> data = new HashMap<>();
                        data.put("scores",poiScores);
                        Log.d(TAG, "No such document");
                        db.collection("POI_scores").document(CurrentUser.uId).set(data);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                fetchPOI();
            }
        });
    }

    private void updatePoiScores(String name) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(!poiScores.contains(name)) {
            poiScores.add(name);
            DocumentReference docRef = db.collection("POI_scores").document(id.toString());
            docRef.update("scores", poiScores);

            DocumentReference docinfo =   db.collection("users").document(CurrentUser.email);
            Map<String, Object> info = new HashMap<>();
            info.put("name", name);
            info.put("visited_count", 1);
            docinfo.collection("POI_visited").document(name).set(info);
        }
        else {
            if (CurrentUser.visitedPOIMap.get(name) != null) {
                DocumentReference docCount = db.collection("users").document(CurrentUser.email).collection("POI_visited").document(name);
                docCount.update("visited_count", Integer.parseInt(CurrentUser.visitedPOIMap.get(name).toString()) + 1);
            }
        }
    }
}
