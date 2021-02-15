package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.biennale_go.MainActivity;
import com.example.biennale_go.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AddPOIFragment extends Fragment {
    private String POIName;
    private Button addPOI;
    private EditText address;
    private EditText latitude;
    private EditText longitude;
    private EditText description;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_poi, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).onSlideViewButtonClick();
            }                // Handle the back button event

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        addPOI = (Button) view.findViewById(R.id.create);
        address = (EditText) view.findViewById(R.id.adres);
        latitude = (EditText) view.findViewById(R.id.latitude);
        longitude = (EditText) view.findViewById(R.id.longitude);
        description = (EditText) view.findViewById(R.id.description);
        addPOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment POIListFragment = new AdminPOIListFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, POIListFragment);
                fragmentTransaction.commit();
                uploadPOI();
            }
        });

        this.POIName = getArguments().getString("POIName");
        return view;
    }

    private void uploadPOI() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("address", address.getText().toString());
        docData.put("description", description.getText().toString());
        docData.put("image", "");
        docData.put("name", POIName);
        docData.put("latitude", Double.parseDouble(latitude.getText().toString()));
        docData.put("longitude", Double.parseDouble(longitude.getText().toString()));

        db.collection("POI").document(POIName).set(docData);
    }
}

