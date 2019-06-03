package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PoiActivity extends AppCompatActivity {
    private Bundle b;
    private LinearLayout poiPanel;
    private ArrayList<String> names, addresses, descriptions;
    private Button newButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
        poiPanel = (LinearLayout) findViewById(R.id.poiPanel);
        b = getIntent().getExtras();

        if (b != null) {
            names = new ArrayList((ArrayList) b.getSerializable("names"));
            addresses = new ArrayList((ArrayList) b.getSerializable("addresses"));
            descriptions = new ArrayList((ArrayList) b.getSerializable("descriptions"));
            generatePoiButtons();
        }
    }

    private void generatePoiButtons() {
        for (Integer i = 0; i<names.size(); i++) {
            final String name = names.get(i);
            final String address = addresses.get(i);
            final String description = descriptions.get(i);

            newButton = new Button(PoiActivity.this);
            newButton.setText(name);
            newButton.setClickable(true);
            newButton.setGravity(Gravity.CENTER);
            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PoiActivity.this, PoiDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("name", name);
                    b.putString("address", address);
                    b.putString("description", description);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
            poiPanel.addView(newButton);
            // TODO FIX MARGINS
            newButton = new Button(PoiActivity.this);
            newButton.setVisibility(View.INVISIBLE);
            poiPanel.addView(newButton);
        }
    }
}
