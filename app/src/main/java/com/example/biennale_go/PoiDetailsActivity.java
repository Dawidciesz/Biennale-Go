package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PoiDetailsActivity extends AppCompatActivity {
    private String name, address, description;
    private TextView nameTextView, addressTextView, descriptionTextView;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_details);
        b = getIntent().getExtras();
        if (b != null) {
            name = (String) b.getString("name");
            address = (String) b.getString("address");
            description = (String) b.getString("description");
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        nameTextView.setText(name);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        addressTextView.setText(address);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(description);
        }
    }
}
