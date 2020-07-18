package com.example.myapplication_hw8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.*;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ResultFound extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public String data;
    public String[] items = {"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1",};
    public int count;

    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_found);
        Toolbar toolbar = findViewById(R.id.toolbar_rf);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // get data
        Intent pre_intent = getIntent();
        data = pre_intent.getStringExtra("data");
        JSONObject d;
        try {
            d = new JSONObject(data);
            //System.out.println(d);
            count =(int) d.get("count");
            if(count>50)
                count=50;
            //System.out.println(count);
            JSONArray it = d.getJSONArray("items");
            items = new String[count];
            for(int i = 0; i < count; i++){
                items[i] = it.getJSONObject(i).toString();
            }
            //System.out.println(items);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView view_count = (TextView) findViewById(R.id.count);
        view_count.setText(String.valueOf(count));
        // TODO: keyword
        String KeyWord=pre_intent.getStringExtra("keyword");
        TextView view_kw= (TextView) findViewById(R.id.keyword);
        view_kw.setText(KeyWord);
        // recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        mAdapter = new RecycleItem(items);
        recyclerView.setAdapter(mAdapter);

        // refresh
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                // get data
                mAdapter = new RecycleItem(items);
                recyclerView.setAdapter(mAdapter);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}

