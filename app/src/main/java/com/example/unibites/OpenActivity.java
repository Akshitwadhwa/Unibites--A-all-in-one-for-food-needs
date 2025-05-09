package com.example.unibites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open);

        // Set up click listeners for the restaurant cards
        FrameLayout qcafeCard = findViewById(R.id.qcafe_card);
        FrameLayout smoothieZoneCard = findViewById(R.id.smoothie_zone_card);
        FrameLayout fruitCornerCard = findViewById(R.id.fruit_corner_card);

        qcafeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(OpenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        smoothieZoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to Smoothie Zone menu
                Toast.makeText(OpenActivity.this, "Smoothie Zone selected", Toast.LENGTH_SHORT).show();
            }
        });

        fruitCornerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to Fruit Corner menu
                Toast.makeText(OpenActivity.this, "Fruit Corner selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 