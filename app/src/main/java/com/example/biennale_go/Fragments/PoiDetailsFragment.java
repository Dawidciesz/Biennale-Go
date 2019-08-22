package com.example.biennale_go.Fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biennale_go.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PoiDetailsFragment extends Fragment {
    private String name, image, address, description;
    private Boolean checked;
    private TextView nameTextView, addressTextView, descriptionTextView;
    private Bundle b;
    private ImageView PoiImageView, checkedImageView;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_poi_details, container, false);
        b = getArguments();
        if (b != null) {
            name = (String) b.getString("name");
            image = (String) b.getString("image");
            address = (String) b.getString("address");
            description = (String) b.getString("description");
            checked = (Boolean) b.getBoolean("checked");
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            nameTextView.setText(name);
            addressTextView = (TextView) view.findViewById(R.id.addressTextView);
            addressTextView.setText(address);
            descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
            descriptionTextView.setText(description);
            checkedImageView = (ImageView) view.findViewById(R.id.checkedImageView);
            checkedImageView.setImageResource(R.drawable.checked);
            Log.d("check test", checked ? "true" : "false");
            if (checked) checkedImageView.setVisibility(View.VISIBLE);
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            PoiImageView = (ImageView) view.findViewById(R.id.poiImageView);
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
            PoiImageView.setImageBitmap(bmp);
        }
        return view;
    }
}

