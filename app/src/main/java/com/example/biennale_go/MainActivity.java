package com.example.biennale_go;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biennale_go.Adapters.MenuListAdapter;
import com.example.biennale_go.Fragments.AdminPanelFragment;
import com.example.biennale_go.Fragments.PoiDetailsFragment;
import com.example.biennale_go.Fragments.PoiFragment;
import com.example.biennale_go.Fragments.ProfilFragment;
import com.example.biennale_go.Fragments.QuizListFragment;
import com.example.biennale_go.Fragments.RankingFragment;
import com.example.biennale_go.Fragments.RoutesListFragment;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.HideLoadingPanel;
import com.example.biennale_go.Utility.MenuListItem;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.biennale_go.Utility.Constants.ERROR_DIALOG_REQUEST;
import static com.example.biennale_go.Utility.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.biennale_go.Utility.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class MainActivity extends FragmentActivity implements MenuListAdapter.OnMenuItemClick, HideLoadingPanel {
    private ImageView mapButton, upButton, mainMenuButton;
    private static final String TAG = "MainActivity";
    private int y;
    boolean isUp;
    boolean isProfilUp;
    private RecyclerView recyclerView;
    private RelativeLayout listView;
    private ConstraintLayout profilBar;
    private LinearLayout buttons, topBar;
    private List<MenuListItem> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView profilePicture;
    private TextView pointsForm, pointsKm, profilName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ObjectAnimator oax;
    ObjectAnimator oay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PackageInfo info;
        Resources res = getResources();
        Intent intent = getIntent();
        setContentView(R.layout.activity_main);

        items.add(new MenuListItem("MAPA", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("QUIZ", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("FORMY", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("TRASY", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("PROFIL", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("RANKING", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("WYLOGUJ", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        try {
            info = getPackageManager().getPackageInfo("com.example.biennale_go", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        if (!CurrentUser.isLogged) {
        }
        mapButton = (ImageView) findViewById(R.id.map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentUser.mLocationPermissionGranted) {
                    openMapActivity();
                } else {
                    getLocationPermission();
                }
            }
        });
        profilBar = (ConstraintLayout) findViewById(R.id.imageView4);
        profilePicture = (ImageView) findViewById(R.id.profilPicture);
        listView = (RelativeLayout) findViewById(R.id.list_view);
        buttons = (LinearLayout) findViewById(R.id.emailPasswordButtons);
        topBar = (LinearLayout) findViewById(R.id.topBar);
        pointsForm = (TextView) findViewById(R.id.profilForm);
        pointsKm = (TextView) findViewById(R.id.profilKm);
        profilName = (TextView) findViewById(R.id.profilName);
        upButton = (ImageView) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSlideViewButtonClick();
            }
        });
        mainMenuButton = (ImageView) findViewById(R.id.menu);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSlideViewButtonClick();
            }
        });
        isUp = false;
        isProfilUp = false;
        if (CurrentUser.profilPictureId == null || CurrentUser.profilPictureId.equals("")) {
            CurrentUser.setCurrentUser(this);
        } else if (!intent.getStringExtra("fragment").equals("pois")
                && !intent.getStringExtra("fragment").equals("routes")
                && !intent.getStringExtra("infoWindowClick").equals("true")) {
            onSlideViewButtonClick();
        }
        if (checkMapServices()) {
            if (!CurrentUser.mLocationPermissionGranted) {
                getLocationPermission();
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }
        }
        if (intent.getStringExtra("fragment") != null && intent.getStringExtra("fragment").equals("pois")) {
            profilBar.setVisibility(View.INVISIBLE);
            openPoi();
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
        if (intent.getStringExtra("fragment") != null && intent.getStringExtra("fragment").equals("routes")) {
            profilBar.setVisibility(View.INVISIBLE);
            openRoutesList();
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
        if (intent.getStringExtra("infoWindowClick") != null && intent.getStringExtra("infoWindowClick").equals("true")) {
            Fragment poiFragment = new PoiDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", intent.getStringExtra("poiName"));
            bundle.putString("infoWindowClicked", "true");
            bundle.putString("image", intent.getStringExtra("poiImage"));
            bundle.putString("address", intent.getStringExtra("poiAddress"));
            bundle.putString("description", intent.getStringExtra("poiDescription"));
            poiFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, poiFragment);
            fragmentTransaction.commit();
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            profilBar.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void openAdminPanelFragment() {
        Fragment adminPanelFragment = new AdminPanelFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, adminPanelFragment);
        fragmentTransaction.commit();
    }

    public void openQuizList() {
        Fragment quizListFragment = new QuizListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, quizListFragment);
        fragmentTransaction.commit();
    }

    public void openRoutesList() {
        Fragment routesListFragment = new RoutesListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, routesListFragment);
        fragmentTransaction.commit();
    }

    public void openPoi() {
        Fragment poiFragment = new PoiFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, poiFragment);
        fragmentTransaction.commit();
    }

    public void openProfil() {
        Fragment profilFragment = new ProfilFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, profilFragment);
        fragmentTransaction.commit();
    }

    public void openRanking() {
        Fragment rankingFragment = new RankingFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, rankingFragment);
        fragmentTransaction.commit();
    }

    public void openMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void slideDown(View view, int yHight) {
        if (profilBar.getVisibility() == View.GONE || profilBar.getVisibility() == View.INVISIBLE) {
            profilBar.setVisibility(View.VISIBLE);
        }
        view.setVisibility(View.VISIBLE);
        if (CurrentUser.profilPictureId != null) {
            profilePicture.setImageDrawable(getResources().getDrawable(Integer.parseInt(CurrentUser.profilPictureId)));
            profilePicture.setColorFilter(Color.parseColor(CurrentUser.profilPictureColor), PorterDuff.Mode.SRC_IN);
        }
        pointsForm.setText(String.valueOf(CurrentUser.visitedPOIList.size()));
        DecimalFormat df = new DecimalFormat("###.###");
        df.setMinimumFractionDigits(2);
        pointsKm.setText(String.valueOf( df.format(CurrentUser.distance_traveled/1000)));
        profilName.setText(String.valueOf(CurrentUser.name));
        int hightOfY = yHight;
        if (yHight > 1)
            hightOfY = yHight - view.getHeight();
        oax = ObjectAnimator.ofInt(view, "translationX", 0, 0);
        oay = ObjectAnimator.ofFloat(view, "translationY", hightOfY, 0);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(600);
        set.playTogether(oax, oay);
        set.start();
        buttons.setZ(0);
        view.setZ(1);
        view.setZ(1);
    }

    public void slideUp(View view, int yHight) {
        oax = ObjectAnimator.ofInt(view, "translationX", 0, 0);
        oay = ObjectAnimator.ofFloat(view, "translationY", yHight, -view.getHeight());
        AnimatorSet set = new AnimatorSet();
        set.setDuration(600);
        set.playTogether(oax, oay);
        set.start();
    }

        public void onSlideViewButtonClick() {
        recyclerView = (RecyclerView) findViewById(R.id.ranking_list_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MenuListAdapter(items, this);
        recyclerView.setAdapter(adapter);
        int[] location = new int[2];
        listView.getLocationOnScreen(location);
        if (oax != null)
            y = Math.round((Float) oay.getAnimatedValue());
        else
            y = -2000;
        if (isUp
        ) {
            slideUp(listView, y);

        } else if (isUp == false
        ) {
            slideDown(listView, y);
        }
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            isUp = !isUp;
    }
    public void slideProfilDown(View view, int yHight) {
        view.setVisibility(View.VISIBLE);
        int hightOfY = yHight;

        if (yHight > 1)
            hightOfY = yHight - view.getHeight();
        oax = ObjectAnimator.ofInt(view, "translationX", -3000, view.getWidth());
        oay = ObjectAnimator.ofFloat(view, "translationY", -100,  topBar.getHeight());
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(oax, oay);
        set.start();
        view.setZ(0);
    }

    public void slideProfilUp(View view, int yHight) {
        oax = ObjectAnimator.ofInt(view, "translationX", view.getWidth(), 0);
        oay = ObjectAnimator.ofFloat(view, "translationY", topBar.getHeight(),-100);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(oax, oay);
        set.start();
        view.setZ(0);
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

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            CurrentUser.mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        CurrentUser.mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CurrentUser.mLocationPermissionGranted = true;
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
                if (!CurrentUser.mLocationPermissionGranted) {
                    getLocationPermission();
                }
            }
        }
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onMenuItemClick(int position) {
        if (items.get(position).getName() != null) {
            String fragmentName = items.get(position).getName();
            if (fragmentName.equals("QUIZ")) {
                openQuizList();
            } else if (fragmentName.equals("TRASY")) {
                openRoutesList();
            } else if (fragmentName.equals("FORMY")) {
                openPoi();
            } else if (fragmentName.equals("PROFIL")) {
                openProfil();
            } else if (fragmentName.equals("RANKING")) {
                openRanking();
            } else if (fragmentName.equals("ADMIN")) {
                openAdminPanelFragment();
            } else if (fragmentName.equals("MAPA")) {
                if (CurrentUser.mLocationPermissionGranted) {
                    openMapActivity();
                } else {
                    getLocationPermission();
                }
            } else if (fragmentName.equals("WYLOGUJ")) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                CurrentUser.logout();
                Intent i = new Intent(MainActivity.this, LoginRegisterActivity.class);
                startActivity(i);
            }
            if (!CurrentUser.mLocationPermissionGranted && fragmentName.equals("MAPA")) {
            } else {
                slideUp(listView, 0);
                isUp = !isUp;
            }
        }
    }

    @Override
    public void hidePanel() {
        onSlideViewButtonClick();

    }
}

