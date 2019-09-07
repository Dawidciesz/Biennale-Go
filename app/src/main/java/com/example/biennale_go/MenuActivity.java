package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.biennale_go.Utility.CurrentUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.biennale_go.Utility.Constants.ERROR_DIALOG_REQUEST;
import static com.example.biennale_go.Utility.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.biennale_go.Utility.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MenuActivity extends AppCompatActivity {
    private LinearLayout mapCard, quizCard, poiCard, routesCard, profilCard, adminPanel, logOut;
    private boolean mLocationPermissionGranted = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CurrentUser.setCurrentUser();
        setContentView(R.layout.activity_main_menu);

        mapCard = (LinearLayout) findViewById(R.id.mapCard);
        mapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });
        profilCard = (LinearLayout) findViewById(R.id.profilCard);
        profilCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfilActivity();
            }
        });
        quizCard = (LinearLayout) findViewById(R.id.quizCard);
        quizCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuizListActivity();
            }
        });
        poiCard = (LinearLayout) findViewById(R.id.poiCard);
        poiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPoiActivity();
            }
        });
        routesCard = (LinearLayout) findViewById(R.id.routesCard);
        routesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRoutesListActivity();
            }
        });
        adminPanel = (LinearLayout) findViewById(R.id.adminPanel);
        adminPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
        logOut = (LinearLayout) findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MenuActivity.this, EmailPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    public void openMapActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void openQuizListActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment","QuizList");
        startActivity(intent);
    }

    public void openRoutesListActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment","RoutesList");
        startActivity(intent);
    }

    public void openPoiActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment","Poi");
        startActivity(intent);
    }
             public void openProfilActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment","Profil");
        startActivity(intent);
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(!mLocationPermissionGranted){
                getLocationPermission();
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Aplikacja wymaga do działania aktywnej usługi GPS, czy chesz ją aktywować?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MenuActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MenuActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(!mLocationPermissionGranted){
                    getLocationPermission();
                }
            }
        }
    }
}
