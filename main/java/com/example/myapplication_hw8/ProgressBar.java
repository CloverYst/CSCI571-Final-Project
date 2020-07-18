package com.example.myapplication_hw8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;



public class ProgressBar extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // get data from MainActivity
        final Intent pre_intent = getIntent();
        String data = pre_intent.getStringExtra("data");
        // create the Json obj to post
        JSONObject d = new JSONObject();
        try {
            d.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // send the request
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://yst-cs571hw9.wl.r.appspot.com/ebay";
        //String url = "http://10.0.2.2:8080/ebay";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, d, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("(***************************)succeed");
                //System.out.println(response);
                String data = response.toString();


                //no records
                JSONObject d;
                try {
                    d = new JSONObject(data);
                    //System.out.println(d);
                    int count =(int) d.get("count");
                    System.out.println(count);
                    if(count==0) {
                        Intent intent = new Intent(ProgressBar.this, NoResult.class);
                        startActivity(intent);
                    }
                    else
                    {
                        String keyword=pre_intent.getStringExtra("keyword");
                        Intent intent=new Intent(ProgressBar.this, ResultFound.class);
                        intent.putExtra("data", data);
                        intent.putExtra("keyword",keyword);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("(***************************)error");
                System.out.println(error);
            }
        });
        requestQueue.add(request);
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {



                Intent intent=new Intent(ProgressBar.this,
                        ResultFound.class);
                //Intent is used to switch from one activity to another.
                //System.out.println(data);
                //intent.putExtra("data", data);
                startActivity(intent);
                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);*/
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}