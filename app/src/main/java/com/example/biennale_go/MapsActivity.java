package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.biennale_go.Fragments.PoiFragment;
import com.example.biennale_go.Fragments.RoutesListFragment;
import com.example.biennale_go.Utility.DirectionsJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private ImageView poiButton, routesButton;
    LocationManager locationManager;
    private static final String TAG = "QuizMapActicity";
    private ArrayList<String> poiNames, poiAddresses, poiDescriptions, poiImages, poiScores;
    private ArrayList<Double> poiLatitude, poiLongitude;
    private RelativeLayout loadingPanel, mapPanel;
    private Double POICollisionRange = (360.0 * 100.0) / 40075000.0; // 100 meters
    private ArrayList markerPoints = new ArrayList();
    private ArrayList polyline;
    private Bundle b;
    private String polylineColor;

    //    TODO GLOBAL ID
    private Integer id = 1;

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

        b = getIntent().getExtras();
        if (b != null) {
            polyline = (ArrayList) b.getSerializable("polyline");
            polylineColor = (String) b.getString("polylineColor");
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
    }

    public void openPoiActivity(){
        Intent intent = new Intent(this, PoiFragment.class);
        Bundle b = new Bundle();
        b.putSerializable("names", poiNames);
        b.putSerializable("scores", poiScores);
        b.putSerializable("images", poiImages);
        b.putSerializable("addresses", poiAddresses);
        b.putSerializable("descriptions", poiDescriptions);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void openRoutesListActivity(){
        Intent intent = new Intent(this, RoutesListFragment.class);
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
        if(polyline == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, zoomLevel));
        }
        checkIfPoi(latitude, longtitude);
        if(loadingPanel.getVisibility() != View.GONE) {
            loadingPanel.setVisibility(View.GONE);
            mapPanel.setVisibility(View.VISIBLE);
        }
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
        // Set listeners for click events.
//        mMap.setOnPolylineClickListener(this);
//        mMap.setOnPolygonClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchPOIScores();
        if(polyline != null) drawRoutes();



//        //TODO ROUTES CODE - TEST
//        LatLng sydney = new LatLng(-34, 151);
//        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
//
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                if (markerPoints.size() > 1) {
//                    markerPoints.clear();
//                    mMap.clear();
//                }
//
//                // Adding new item to the ArrayList
//                markerPoints.add(latLng);
//
//                // Creating MarkerOptions
//                MarkerOptions options = new MarkerOptions();
//
//                // Setting the position of the marker
//                options.position(latLng);
//
//                if (markerPoints.size() == 1) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                } else if (markerPoints.size() == 2) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }
//
//                // Add new marker to the Google Map Android API V2
//                mMap.addMarker(options);
//
//                // Checks, whether start and end locations are captured
//                if (markerPoints.size() >= 2) {
//                    LatLng origin = (LatLng) markerPoints.get(0);
//                    LatLng dest = (LatLng) markerPoints.get(1);
//
//                    // Getting URL to the Google Directions API
//                    String url = getDirectionsUrl(origin, dest);
//
//                    DownloadTask downloadTask = new DownloadTask();
//
//                    // Start downloading json data from Google Directions API
//                    downloadTask.execute(url);
//                }
//
//            }
//        });
        // TEST CODE - END
    }
    private class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            if(result.size() > 0) {

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
            }

            }

// Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null)
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
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
                        String image = document.getData().get("image").toString();
                        String address = document.getData().get("address").toString();
                        String description = document.getData().get("description").toString();
                        Double latitude = (Double) document.getData().get("latitude");
                        Double longitude = (Double) document.getData().get("longitude");
                        LatLng POI = new LatLng(latitude, longitude);
                        if(poiScores.contains(name)) {
                            mMap.addMarker(new MarkerOptions().position(POI).title(name).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        } else {
                            mMap.addMarker(new MarkerOptions().position(POI).title(name).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }

                        poiNames.add(name);
                        poiImages.add(image);
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

    private void fetchPOIScores() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("POI_scores").document(id.toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        poiScores = (ArrayList<String>) document.getData().get("scores");
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

    private void updatePoiScores(String name) {
        if(!poiScores.contains(name)) {
            Log.d("It works!", "element is here!");
            poiScores.add(name);
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("POI_scores").document(id.toString());
            docRef.update("scores", poiScores);
        }
    }
}
