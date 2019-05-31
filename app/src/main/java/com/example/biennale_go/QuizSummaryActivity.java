package com.example.biennale_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class QuizSummaryActivity extends AppCompatActivity {
    private Button exitButton;
    private TextView pointsTextView;
    private Bundle b;
    private Integer points;
    private Integer maxPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_summary);
        pointsTextView = (TextView) findViewById(R.id.pointsTextView);
        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        b = getIntent().getExtras();

        if (b != null) {
            points =  b.getInt("points");
            maxPoints =  b.getInt("maxPoints");
            pointsTextView.setText(points + "/" + maxPoints);
        }
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
