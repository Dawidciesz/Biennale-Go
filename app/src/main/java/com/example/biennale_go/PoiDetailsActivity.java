package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PoiDetailsActivity extends AppCompatActivity {
    private String name, image, address, description;
    private TextView nameTextView, addressTextView, descriptionTextView;
    private Bundle b;
    private ImageView PoiImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_details);
        b = getIntent().getExtras();
        if (b != null) {
            name = (String) b.getString("name");
            image = (String) b.getString("image");
            address = (String) b.getString("address");
            description = (String) b.getString("description");
            nameTextView = (TextView) findViewById(R.id.nameTextView);
            nameTextView.setText(name);
            addressTextView = (TextView) findViewById(R.id.addressTextView);
            addressTextView.setText(address);
            descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
            descriptionTextView.setText(description);

            StrictMode.ThreadPolicy policy = new
            StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            PoiImageView = (ImageView) findViewById(R.id.poiImageView);
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
    }
}
