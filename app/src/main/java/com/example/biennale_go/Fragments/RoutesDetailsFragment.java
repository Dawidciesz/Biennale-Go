package com.example.biennale_go.Fragments;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biennale_go.MainActivity;
import com.example.biennale_go.MapsActivity;
import com.example.biennale_go.R;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RoutesDetailsFragment extends Fragment {
    private String name, image, description, color;
    private ArrayList polyline;
    private TextView nameTextView, descriptionTextView, streetsTextView;
    private Bundle b;
    private ImageView routesImageView, showOnMapButton;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_routes_details, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).onSlideViewButtonClick();
            }                // Handle the back button event

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        b = getArguments();
        if (b != null) {
            name = (String) b.getString("name");
            image = (String) b.getString("image");
            color = (String) b.getString("color");
            description = (String) b.getString("description");
            polyline = (ArrayList) b.getSerializable("polyline");
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
            descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
            descriptionTextView.setText(description);
            streetsTextView = (TextView) view.findViewById(R.id.streetsTextView);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            routesImageView = (ImageView) view.findViewById(R.id.routesImageView);
            URL url = null;
            try {
                url = new URL(image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            routesImageView.setImageBitmap(bmp);

            showOnMapButton = (ImageView) view.findViewById(R.id.showOnMapButton);
            showOnMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MapsActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("polyline", polyline);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }
        return view;
    }
}
