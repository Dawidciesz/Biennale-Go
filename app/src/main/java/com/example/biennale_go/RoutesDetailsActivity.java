package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RoutesDetailsActivity extends AppCompatActivity {
    private String name, image, description;
    private TextView nameTextView, descriptionTextView;
    private Bundle b;
    private ImageView routesImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_details);
        b = getIntent().getExtras();
        if (b != null) {
            name = (String) b.getString("name");
            image = (String) b.getString("image");
            description = (String) b.getString("description");
            nameTextView = (TextView) findViewById(R.id.nameTextView);
            nameTextView.setText(name);
            descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
            descriptionTextView.setText(description);

//            StrictMode.ThreadPolicy policy = new
//                    StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            routesImageView = (ImageView) findViewById(R.id.routesImageView);
//            URL url = null;
//            try {
//                url = new URL(image);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            Bitmap bmp = null;
//            try {
//                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            routesImageView.setImageBitmap(bmp);
        }
    }
}
