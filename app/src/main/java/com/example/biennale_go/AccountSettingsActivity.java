package com.example.biennale_go;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.biennale_go.Adapters.MenuListAdapter;
import com.example.biennale_go.Adapters.profilePictureAdapter;
import com.example.biennale_go.Utility.MenuListItem;
import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountSettingsActivity  extends Activity implements profilePictureAdapter.OnMenuItemClick {


    private RecyclerView recyclerView;

    private List<Drawable> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);
        FirebaseApp.initializeApp(this);
        recyclerView = (RecyclerView) findViewById(R.id.profilPicturesRecycler);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        Resources res = getResources();
        adapter = new profilePictureAdapter(items, this);
        recyclerView.setAdapter(adapter);
        items.add(res.getDrawable(R.drawable.ic_android));
        items.add(res.getDrawable(R.drawable.ic_formy));
        items.add(res.getDrawable(R.drawable.ic_formy));
        items.add(res.getDrawable(R.drawable.ic_android));
        items.add(res.getDrawable(R.drawable.ic_formy));
        items.add(res.getDrawable(R.drawable.ic_formy));
        items.add(res.getDrawable(R.drawable.ic_formy));
        items.add(res.getDrawable(R.drawable.ic_android));


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onMenuItemClick(int position) {
        System.out.print("sad");
        items.get(position).setTint(300);
        adapter.notifyDataSetChanged();


        }
        //            slideUp(listView, 0);
        //            isUp = !isUp;
    }


