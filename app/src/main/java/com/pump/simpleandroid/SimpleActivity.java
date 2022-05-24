package com.pump.simpleandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;



public class SimpleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);


        ImageButton tylpan = (ImageButton) findViewById(R.id.tylpan);
        tylpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(SimpleActivity.this, MainActivity.class);
                startActivity(go);
            }
        });
    }
}