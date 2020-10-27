package com.example.biennale_go;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biennale_go.Adapters.profilePictureAdapter;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.ProfilPictureItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountSettingsActivity  extends Activity implements profilePictureAdapter.OnProfilePictureItemClick {
    private RecyclerView recyclerView;
    private List<ProfilPictureItem> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView chosenPicture;
    private FrameLayout red, green, blue, yellow;
    private TextView errorMessage;
    private Button cancel, next;
    private String profileColor = "", profilName = "";
    private FirebaseFirestore db;
    FirebaseAuth mAuth;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);
        db = FirebaseFirestore.getInstance();
        FirebaseApp.initializeApp(this);
        chosenPicture = (ImageView) findViewById(R.id.choosen_image);
        chosenPicture.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_star));
        profilName = "2131165369";
        mAuth = FirebaseAuth.getInstance();
        errorMessage = (TextView) findViewById(R.id.error_message);
        red = (FrameLayout) findViewById(R.id.red);
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenPicture.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                profileColor = "0xFFFF0000";
            }
        });
        green = (FrameLayout) findViewById(R.id.green);
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenPicture.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                profileColor = "0xFF00FF00";
            }
        });
        blue = (FrameLayout) findViewById(R.id.blue);
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenPicture.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                profileColor = "0xFF0000FF";
            }
        });
        yellow = (FrameLayout) findViewById(R.id.yellow);
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenPicture.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                profileColor = "0xFFFFFF00";
            }
        });
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBeginningScreen();
            }
        });
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileColor.equals("") || profilName.equals("")) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake));
                } else {
                    createAccount(getIntent().getStringExtra("email"), getIntent().getStringExtra("pass"));
                }
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.profilPicturesRecycler);
//        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        Resources res = getResources();
        adapter = new profilePictureAdapter(items, this);
        recyclerView.setAdapter(adapter);
        items.add(new ProfilPictureItem(res.getDrawable(2131165352), String.valueOf(R.drawable.ic_android)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165351), String.valueOf(R.drawable.ic_1538505207)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165354), String.valueOf(R.drawable.ic_circie_cube)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165358), String.valueOf(R.drawable.ic_heart_circles)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165369), String.valueOf(R.drawable.ic_star)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165370), String.valueOf(R.drawable.ic_tru)));
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            CurrentUser.profilPictureColor = "#" + profileColor.substring(profileColor.lastIndexOf("x") + 1);
                            CurrentUser.profilPictureId = profilName;
                            CurrentUser.name = getIntent().getStringExtra("name");
                            CurrentUser.email = getIntent().getStringExtra("email");
                            CurrentUser.uId = currentUser.getUid();
                            CurrentUser.getPOICount();
                            CurrentUser.fetchPOIScores();
                            createUser();
                            Intent intent = new Intent(AccountSettingsActivity.this, MainActivity.class);
                            intent.putExtra("from", "new_account");
                            startActivity(intent);
                        } else {
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public  void createUser() {
        Intent intent = getIntent();
        Map<String, Object> data = new HashMap<>();
        data.put( "age", 0);
        data.put("name", intent.getStringExtra("name"));
        data.put("distance_traveled", 0);
        data.put("score", 0);
        data.put("profile_img", profilName);
        data.put("profile_color", "#" + profileColor.substring(profileColor.lastIndexOf("x") + 1));
        db.collection("users").document(intent.getStringExtra("email")).set(data);
        DocumentReference docRef =   db.collection("users").document(intent.getStringExtra("email"));
        Map<String, Object> info = new HashMap<>();
        info.put("name", intent.getStringExtra("name"));
        info.put("visited_count", 0);
        docRef.collection("POI_visited").document(intent.getStringExtra("name")).set(info);
    }

    public void openBeginningScreen() {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onProfilePictureItemClick(int position, String imageName) {
        profilName = imageName;
        chosenPicture.setImageDrawable(items.get(position).getImage());
    }
    }


