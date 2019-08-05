package com.example.biennale_go;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.biennale_go.Fragments.AdminPanelFragment;
import com.example.biennale_go.Fragments.PoiFragment;
import com.example.biennale_go.Fragments.QuizListFragment;
import com.example.biennale_go.Fragments.RoutesListFragment;
import com.example.biennale_go.Utility.CurrentUser;

public class MainActivity extends FragmentActivity {
    private Button quizButton, adminPanelButton, mainMenuButton;
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

        adminPanelButton = (Button) findViewById(R.id.adminPanelButton);
        adminPanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdminPanelFragment();
            }
        });

        mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuActivity();
            }
        });
    }

    public void openAdminPanelFragment() {
        Fragment adminPanelFragment = new AdminPanelFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, adminPanelFragment);
        fragmentTransaction.commit();
    }
    public void openQuizList() {
        Fragment adminPanelFragment = new QuizListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, adminPanelFragment);
        fragmentTransaction.commit();
    }

    public void openRoutesList() {
        Fragment adminPanelFragment = new RoutesListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, adminPanelFragment);
        fragmentTransaction.commit();
    }

    public void openPoi() {
        Fragment adminPanelFragment = new PoiFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, adminPanelFragment);
        fragmentTransaction.commit();
    }

    public void openMenuActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void openEmailPasswordActivity(){
        Intent intent = new Intent(this, EmailPasswordActivity.class);
        startActivity(intent);
    }





}
