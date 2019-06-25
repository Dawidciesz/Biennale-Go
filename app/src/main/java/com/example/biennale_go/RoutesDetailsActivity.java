package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RoutesDetailsActivity extends AppCompatActivity {
    private String name, image, description, color;
    private ArrayList polyline;
    private TextView nameTextView, descriptionTextView, streetsTextView;
    private Bundle b;
    private ImageView routesImageView;
    private Button showOnMapButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_details);
        b = getIntent().getExtras();
        if (b != null) {
            name = (String) b.getString("name");
            image = (String) b.getString("image");
            color = (String) b.getString("color");
            description = (String) b.getString("description");
            polyline = (ArrayList) b.getSerializable("polyline");
            final ArrayList streets = (ArrayList) b.getSerializable("streets");
            nameTextView = (TextView) findViewById(R.id.nameTextView);
            nameTextView.setText(name);
            descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
            descriptionTextView.setText(description);
            streetsTextView = (TextView) findViewById(R.id.streetsTextView);
            streetsTextView.setText(streets.toString());

            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            routesImageView = (ImageView) findViewById(R.id.routesImageView);
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

            showOnMapButton = (Button) findViewById(R.id.showOnMapButton);
            showOnMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RoutesDetailsActivity.this, MapsActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("polyline", polyline);
                    b.putString("polylineColor", color);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }
    }
}
