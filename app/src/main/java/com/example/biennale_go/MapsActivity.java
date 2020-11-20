package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.PendingIntent;
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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biennale_go.Classes.PoiInfoWindow;
import com.example.biennale_go.Classes.RetrieveFeedTask;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.GeofenceHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.InfoWindowAdapter{
    private GoogleMap mMap;
    private ImageView poiButton, routesButton, followButton;
    LocationManager locationManager;
    private TextView  dialogPoiText;
    private static final String TAG = "Geofence";
    private ArrayList<String> poiNames, poiAddresses, poiDescriptions, poiImages, poiScores;
    private ArrayList<Double> poiLatitude, poiLongitude;
    private ArrayList<Polyline> polylineList = new ArrayList<>();
    private ArrayList<LatLng> endOfPolyline = new ArrayList<>();
    private ArrayList<Bitmap> poiBitmapImages = new ArrayList<>();
    private ArrayList<Marker> markers= new ArrayList<>();
    private CircleImageView poiDialogImage;
    private RelativeLayout loadingPanel, mapPanel;
    private FrameLayout dialogFrameLayout;
    private View dialog;
    private Double POICollisionRange = (360.0 * 30.0) / 40075000.0; // 100 meters
    //    private ArrayList polyline;
    private ArrayList<ArrayList> polyline = new ArrayList<>();

    private Bundle b;
    private double userLat = 0;
    private double userLong = 0;
    private CameraPosition cameraPosition;
    private Button closePoiDialogButton;
    private String searchPoiName;
    private Boolean playerMarkFlag = false, followPlayerFlag = true;
    private Marker playerMarker;
    final LatLngBounds elblagBorder = new LatLngBounds(new LatLng(54.146831,19.386889  ), new LatLng(54.189640, 19.437335));
    private String id = CurrentUser.uId;
    private ImageView galleryLogo;
    private GeoApiContext mGeoApiContext;
    private View v;
    private TextView title;
    private ImageView iconImage;
    LatLng userCurrentLocation;
    private int routeIsdraw = 0;
    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;
    private GeofenceHelper geofenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        v = getLayoutInflater().inflate(R.layout.infowindow_map, null);
        title = (TextView) v.findViewById(R.id.window_title);
        iconImage = (ImageView) v.findViewById(R.id.window_image);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        dialogPoiText = (TextView) findViewById(R.id.dialogPoiText);
        mapPanel = (RelativeLayout) findViewById(R.id.mapPanel);
        galleryLogo = (ImageView) findViewById(R.id.galleryLogo);
        galleryLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading_scale));
        dialogFrameLayout = (FrameLayout) findViewById(R.id.dialogLayout);
        poiDialogImage = (CircleImageView) findViewById(R.id.poiDialogImage);
        followButton = (ImageView) findViewById(R.id.followButton);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followPlayerFlag = !followPlayerFlag;
                if (followPlayerFlag) {
                    followButton.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.lockclosed));
                } else {
                    followButton.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.lockopen));
                }
            }
        });
        closePoiDialogButton = (Button) findViewById(R.id.closePoi);
        closePoiDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialogFrameLayout.setZ(0);
                }});
        b = getIntent().getExtras();
        if (b != null) {
            polyline = (ArrayList) b.getSerializable("polyline");
            searchPoiName = (String) b.getString("searchPoiName");
        }
        if ((polyline != null) && (mMap != null)) {
            drawRoutes();
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
        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key2))
                    .build();
        }
        routeIsdraw = 0;
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
    }

    private void calculateDirections(LatLng from, LatLng to){
        Log.d(TAG, "calculateDirections: calculating directions.");
        endOfPolyline.add(new LatLng(to.latitude,
                        to.longitude));
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                to.latitude,
                to.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.mode(TravelMode.WALKING);
        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
                        from.latitude,
                        from.longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }
//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(geofenceList);
//        return builder.build();
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        // Reuse the PendingIntent if we already have it.
//        if (geofencePendingIntent != null) {
//            return geofencePendingIntent;
//        }
//        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
//        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
//        // calling addGeofences() and removeGeofences().
//        geofencePendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.
//                FLAG_UPDATE_CURRENT);
//        return geofencePendingIntent;
//    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();
                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    polylineList.add(mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath)));
                    polylineList.get(polylineList.size()-1).setColor(Color.BLUE);
                    polylineList.get(polylineList.size()-1).setClickable(true);
                    polylineList.get(polylineList.size()-1).setWidth(10);
                    polylineList.get(polylineList.size()-1).setStartCap(new RoundCap());
                    polylineList.get(polylineList.size()-1).setStartCap(new RoundCap());
                }
            }
        });
    }
    public void openPoiActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("names", poiNames);
        b.putSerializable("scores", poiScores);
        b.putSerializable("images", poiImages);
        b.putSerializable("addresses", poiAddresses);
        b.putSerializable("descriptions", poiDescriptions);
        intent.putExtra("fragment","pois");
        startActivity(intent);
    }

    public void openRoutesListActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment","routes");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        routeIsdraw = 0;
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50, 3, this);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 3, this);
            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    void setMapLocation(double latitude, double longtitude) {
        userCurrentLocation = new LatLng(latitude, longtitude);
        if (!polyline.isEmpty() && routeIsdraw < 2) {
            drawRoutes();
            routeIsdraw++;
        }
        float zoomLevel = 17.0f;
        if (userLat < 1) {
            userLat = latitude;
            userLong = longtitude;
        }
        else if(userLat != userCurrentLocation.latitude && userLong != userCurrentLocation.longitude) {
            Location locationA = new Location("A");
            locationA.setLatitude(userLat);
            locationA.setLongitude(userLong);
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
        checkIfRoad(latitude, longtitude);
//        if(loadingPanel.getVisibility() != View.GONE) {
//            loadingPanel.setVisibility(View.GONE);
//            mapPanel.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    public void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
    private void checkIfPoi(double player_latitude, double player_longtitude){
        for (Integer i = 0; i < poiNames.size(); i++) {
            final Double POI_latitude = poiLatitude.get(i);
            final Double POI_longtitude = poiLongitude.get(i);
            if (player_latitude < POI_latitude + POICollisionRange &&
                    player_latitude + POICollisionRange > POI_latitude &&
                    player_longtitude < POI_longtitude + POICollisionRange &&
                    player_longtitude + POICollisionRange > POI_longtitude) {
            if (!poiScores.contains(poiNames.get(i))) {
                dialogFrameLayout.setZ(1);
                dialogPoiText.setText("Gratulacje!  Odkryłeś Formę Biennale: " + poiNames.get(i));
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.checkedmarker);
                Bitmap b = bitmapdraw.getBitmap();
                final Bitmap checkedMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                markers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(checkedMarker));
                poiDialogImage.setImageBitmap(poiBitmapImages.get(i));
            }
                updatePoiScores(poiNames.get(i));
            }
        }
    }
    private void checkIfRoad(double player_latitude, double player_longtitude){
        for (Integer i = 0; i<polylineList.size(); i++) {
            final Double POI_latitude =endOfPolyline.get(i).latitude;
            final Double POI_longtitude = endOfPolyline.get(i).longitude;

            if (player_latitude < POI_latitude + POICollisionRange &&
                    player_latitude + POICollisionRange > POI_latitude &&
                    player_longtitude < POI_longtitude + POICollisionRange &&
                    player_longtitude + POICollisionRange > POI_longtitude) {
                polylineList.get(i).setColor(Color.RED);
                polylineList.get(i).setZIndex(1);
            }
        }
    }
    public void drawRoutes() {
        for (Integer i = 0; i < polyline.size(); i++) {
            ArrayList secondEl = (ArrayList) polyline.get(i);
            if (i == 0) {
                LatLng firstEl = userCurrentLocation;
                calculateDirections(firstEl, new LatLng((Double) secondEl.get(0), (Double) secondEl.get(1)));
            } else {
                ArrayList firstEl = (ArrayList) polyline.get(i - 1);
                calculateDirections(new LatLng((Double) firstEl.get(0), (Double) firstEl.get(1)), new LatLng((Double) secondEl.get(0), (Double) secondEl.get(1)));
            }
        }
        ArrayList firstEl = (ArrayList) polyline.get(0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((Double) firstEl.get(0), (Double) firstEl.get(1)), 16));
    }

    private void addGeofence(LatLng latLng, float radius, String poiName) {

        Geofence geofence = geofenceHelper.getGeofence(poiName, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                        addCircle(latLng, 200);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        dialogFrameLayout.setVisibility(View.VISIBLE);
        mMap = googleMap;
        mMap.setLatLngBoundsForCameraTarget(elblagBorder);
        mMap.setMinZoomPreference(13.8f);
        mMap.setMyLocationEnabled(true);
        fetchPOIScores();
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


        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                iconImage.setImageDrawable(null);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null) {
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            PoiInfoWindow event = (PoiInfoWindow) marker.getTag();
                            if (event == null) {
                                return null;
                            }
                            title.setText(event.getName());
                            iconImage.setImageBitmap(event.getBitMapImage());
                            return v;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            return null;
                        }
                    });
                }
                return false;
            }
        });
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                PoiInfoWindow event = (PoiInfoWindow) marker.getTag();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                intent.putExtra("infoWindowClick", "true");
                intent.putExtra("poiName", event.getName());
                intent.putExtra("fragment", "true");
                intent.putExtra("poiImage", event.getImage());
                intent.putExtra("poiAddress", event.getAddress());
                intent.putExtra("poiDescription", event.getDescription());
                intent.putExtra("longitude", event.getLongitude().toString());
                intent.putExtra("latitude", event.getLatitude().toString());
                startActivity(intent);
            }
        });

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
                        PoiInfoWindow poiInfoWindow = new PoiInfoWindow();
                        poiInfoWindow.setName(name);
                        poiInfoWindow.setDescription(description);
                        poiInfoWindow.setImage(image);
                        poiInfoWindow.setAddress(address);
                        poiInfoWindow.setLatitude(latitude);
                        poiInfoWindow.setLongitude(longitude);

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
//                        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//                        if (SDK_INT > 8) {
//                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                                    .permitAll().build();
//                            StrictMode.setThreadPolicy(policy);
//                            URL url = null;
//                            try {
//                                url = new URL(image);
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            }
//                            Bitmap result = null;
//                            try {
//                                result = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            poiBitmapImages.add(result);
//                            poiInfoWindow.setBitMapImage(result);
//                        }
                        RetrieveFeedTask asyncTask = new RetrieveFeedTask(new AsyncResponse() {
                            @Override
                            public void processFinish(Bitmap output) {
                                if (poiScores.contains(name)) {
                                    Marker poMark = mMap.addMarker(new MarkerOptions().position(POI).title(name).icon(BitmapDescriptorFactory.fromBitmap(checkedMarker)));
                                    poMark.setTag(poiInfoWindow);
                                    markers.add(poMark);
                                } else {
                                    Marker poMark = mMap.addMarker(new MarkerOptions().position(POI).title(name).icon(BitmapDescriptorFactory.fromBitmap(questionMarker)));
                                    poMark.setTag(poiInfoWindow);
                                    markers.add(poMark);
                                }
                                poiBitmapImages.add(output);
                                poiInfoWindow.setBitMapImage(output);
                            }
                        });
                        asyncTask.execute(image);
                        addGeofence(new LatLng(latitude, longitude), 200, name);
                    }
                    loadingPanel.setVisibility(View.GONE);
                    mapPanel.setVisibility(View.VISIBLE);
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

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
