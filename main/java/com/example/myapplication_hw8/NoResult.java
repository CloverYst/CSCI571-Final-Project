package com.example.myapplication_hw8;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class NoResult extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_result);
        toolbar = findViewById(R.id.toolbar_noresult);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Toast t = Toast.makeText(getApplicationContext(),
                "No Records",
                Toast.LENGTH_LONG);
        t.show();
    }
}
