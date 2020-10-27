package com.example.biennale_go.Fragments;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.biennale_go.Adapters.profilePictureAdapter;
import com.example.biennale_go.MainActivity;
import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.ProfilPictureItem;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfilFragment extends Fragment implements profilePictureAdapter.OnProfilePictureItemClick {
    private  View view;
    private RecyclerView recyclerView;
    private List<ProfilPictureItem> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView chosenPicture, chosenPicture2;
    private FrameLayout red, green, blue, yellow, dialog;
    private String profileColor = "", profilName = "";
    private Button saveSettingsButton, showFrameLayoutButton;
    private FirebaseFirestore db;

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_profil, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).onSlideViewButtonClick();
            }                // Handle the back button event
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        profilName = CurrentUser.profilPictureId;
        profileColor = CurrentUser.profilPictureColor;
        Resources res = getResources();
        db = FirebaseFirestore.getInstance();
        FirebaseApp.initializeApp(getContext());
        dialog = (FrameLayout) view.findViewById(R.id.dialog_colors);
        chosenPicture = (ImageView) view.findViewById(R.id.choosen_image);
        chosenPicture2 = (ImageView) view.findViewById(R.id.choosen_image2);
        chosenPicture.setImageDrawable(getResources().getDrawable(Integer.parseInt(CurrentUser.profilPictureId)));
        chosenPicture.setColorFilter(Color.parseColor(CurrentUser.profilPictureColor), PorterDuff.Mode.SRC_IN);
        chosenPicture2.setImageDrawable(getResources().getDrawable(Integer.parseInt(CurrentUser.profilPictureId)));
        chosenPicture2.setColorFilter(Color.parseColor(CurrentUser.profilPictureColor), PorterDuff.Mode.SRC_IN);
        red = (FrameLayout)  view.findViewById(R.id.red);
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenPicture.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                profileColor = "0xFFFF0000";
            }
        });
        green = (FrameLayout)  view.findViewById(R.id.green);
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenPicture.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                profileColor = "0xFF00FF00";
            }
        });
        blue = (FrameLayout)  view.findViewById(R.id.blue);
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenPicture.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                profileColor = "0xFF0000FF";
            }
        });
        yellow = (FrameLayout)  view.findViewById(R.id.yellow);
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenPicture.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                profileColor = "0xFFFFFF00";
            }
        });
        showFrameLayoutButton = (Button)  view.findViewById(R.id.show_frame_layout);
        showFrameLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setVisibility(View.VISIBLE);
                dialog.bringToFront();
                showFrameLayoutButton.setVisibility(View.INVISIBLE);
            }
        });
        saveSettingsButton = (Button)  view.findViewById(R.id.save_settings_button);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileColor.contains("x")) {
                    profileColor = "#" + profileColor.substring(profileColor.lastIndexOf("x") + 1);
                }
                DocumentReference docUpdateScores = db.collection("users").document(CurrentUser.email);
                if (profileColor == null && profilName == null) {
                } else if (profileColor != null && profilName == null) {
                    CurrentUser.profilPictureColor = profileColor;
                    docUpdateScores.update("profile_color", profileColor);
                } else if (profileColor == null && profilName != null) {
                    CurrentUser.profilPictureId = profilName;
                    docUpdateScores.update("profile_img", profilName);
                } else {
                    CurrentUser.profilPictureColor = profileColor;
                    CurrentUser.profilPictureId = profilName;
                    docUpdateScores.update("profile_color", profileColor);
                    docUpdateScores.update("profile_img", profilName);
                }
                dialog.setVisibility(View.INVISIBLE);
                showFrameLayoutButton.setVisibility(View.VISIBLE);
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.profilPicturesRecycler);
//        layoutManager = new LinearLayoutManager(this);
        items.add(new ProfilPictureItem(res.getDrawable(2131165352), String.valueOf(R.drawable.ic_android)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165351), String.valueOf(R.drawable.ic_1538505207)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165354), String.valueOf(R.drawable.ic_circie_cube)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165358), String.valueOf(R.drawable.ic_heart_circles)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165369), String.valueOf(R.drawable.ic_star)));
        items.add(new ProfilPictureItem(res.getDrawable(2131165370), String.valueOf(R.drawable.ic_tru)));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        recyclerView.setLayoutManager(layoutManager);
        adapter = new profilePictureAdapter(items, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onProfilePictureItemClick(int position, String imageName) {
        profilName = imageName;
        chosenPicture.setImageDrawable(items.get(position).getImage());
        chosenPicture2.setImageDrawable(items.get(position).getImage());
    }
}