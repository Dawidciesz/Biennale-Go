package com.example.biennale_go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.biennale_go.Utility.CurrentUser;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.biennale_go.Utility.Constants.ERROR_DIALOG_REQUEST;
import static com.example.biennale_go.Utility.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.biennale_go.Utility.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MenuActivity extends Activity {
    private LinearLayout mapCard, quizCard, poiCard, routesCard, profilCard, adminPanel, logOut;
    private boolean mLocationPermissionGranted = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        PackageInfo info;
        try {

            info = getPackageManager().getPackageInfo("com.example.biennale_go", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
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
        profilCard = (LinearLayout) findViewById(R.id.rankingCard);
        profilCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRankingFragment();
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
                openAdminFragment();
            }
        });
        logOut = (LinearLayout) findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent i = new Intent(MenuActivity.this, LoginRegisterActivity.class);
                startActivity(i);
            }
        });

        if(savedInstanceState.getString("fragmnet").equals("roads")) {
            openRoutesListActivity();
        } else if(savedInstanceState.getString("fragmnet").equals("pois")) {
            openPoiActivity();
        }
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
    public void openRankingFragment(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment","Ranking");
        startActivity(intent);
    }
    public void openAdminFragment(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment","Admin");
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
