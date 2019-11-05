package com.example.biennale_go;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.biennale_go.Fragments.AdminPanelFragment;
import com.example.biennale_go.Fragments.PoiFragment;
import com.example.biennale_go.Fragments.ProfilFragment;
import com.example.biennale_go.Fragments.QuizListFragment;
import com.example.biennale_go.Fragments.RankingFragment;
import com.example.biennale_go.Fragments.RoutesListFragment;
import com.example.biennale_go.Utility.CurrentUser;

public class MainActivity extends FragmentActivity {
    private ImageView quizButton, mapButton, mainMenuButton;
    private static final String TAG = "MainActivity";
    private String startFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CurrentUser.isLogged) {

        }
        CurrentUser.setCurrentUser();
        setContentView(R.layout.activity_main);
        if (getIntent().getExtras() != null) {
            startFragment = getIntent().getExtras().getString("fragment");
            if (startFragment.equals("QuizList")) {
                openQuizList();
            }
            else if (startFragment.equals("RoutesList")) {
                openRoutesList();
            }
            else if (startFragment.equals("Poi")) {
                openPoi();
            }
            else if (startFragment.equals("Profil")) {
                openProfil();
            }
            else if (startFragment.equals("Ranking")) {
                openRanking();
            }
            else if (startFragment.equals("Admin")) {
                openAdminPanelFragment();
            }
        }



//        button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openMapActivity();
//            }
//        });
//        quizButton = (Button) findViewById(R.id.quizButton);
//        quizButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openQuizListActivity();
//            }
//        });

        mapButton = (ImageView) findViewById(R.id.map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });

        mainMenuButton = (ImageView) findViewById(R.id.menu);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuActivity();
            }
        });
    }
    public void openMapActivity(){
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

    public void openMenuActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void openEmailPasswordActivity(){
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
    }





}
