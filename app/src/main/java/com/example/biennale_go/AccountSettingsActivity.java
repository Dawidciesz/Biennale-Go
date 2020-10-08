package com.example.biennale_go;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.biennale_go.Adapters.MenuListAdapter;
import com.example.biennale_go.Adapters.profilePictureAdapter;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.MenuListItem;
import com.example.biennale_go.Utility.ProfilPictureItem;
import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountSettingsActivity  extends Activity implements profilePictureAdapter.OnProfilePictureItemClick {


    private RecyclerView recyclerView;

    private List<ProfilPictureItem> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView chosenPicture;
    private FrameLayout red, green, blue, yellow;
    private Button cancel, next;
    private String profileColor = "", profilName = "";


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);
        FirebaseApp.initializeApp(this);
        chosenPicture = (ImageView) findViewById(R.id.choosen_image);
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
                profileColor = "0xFFFFFF00";

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


            }
        });
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
//                DocumentReference docRef = db.collection("users").document(CurrentUser.email);
//                docRef.update("profil_color", profileColor);
//                docRef.update("profil_img", profilName);
//                db.collection("users").document(CurrentUser.email).set(user)
//                db.collection("users").document(CurrentUser.email).set(user)
                    db.collection("users").document(CurrentUser.email).update("profile_color", profileColor);
                    db.collection("users").document(CurrentUser.email).update("profile_img", profilName);

                CurrentUser.profilPictureColor = profileColor;
                CurrentUser.profilPictureId = profilName;
                Intent intent = new Intent(AccountSettingsActivity.this, MainActivity.class);
                intent.putExtra("from","new_account");
                startActivity(intent);

            }
        });




        recyclerView = (RecyclerView) findViewById(R.id.profilPicturesRecycler);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    @Override
    public void onProfilePictureItemClick(int position, String imageName) {
        profilName = imageName;
        chosenPicture.setImageDrawable(items.get(position).getImage());
    }
    //            slideUp(listView, 0);
        //            isUp = !isUp;
    }


