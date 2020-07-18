package com.example.myapplication_hw8;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.*;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.TextView;



public class ItemDetail extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String oldData;

    // item details
    String id = "";
    String title = "";
    String item_url = "";
    String price = "", ShippingPrice = "", handingTime = "", OneDayShipping = "", shippingType = "", shippingToLocation = "", Expedited_shipping = "";
    String[] pictureURL;
    String subtitle = "", brand = "", FeedBack_score = "", userID = "", positiveFB_percent = "", FB_ratingstar = "",refund = "", refund_Within = "", ShippingCost_PaidBy = "", ReturnsAccepted = "";
    String spec1="", spec2="", spec3="", spec4="", spec5="";

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");




        init_viewpaper();
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                init_viewpaper();
            }
        }, 1000);*/

    }

    private void init_viewpaper(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_selected);
        tabLayout.getTabAt(1).setIcon(R.drawable.sellericon);
        int tabIconColor= ContextCompat.getColor(this,R.color.seller);
        tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).setIcon(R.drawable.truck);


        // get data
        Intent pre_intent = getIntent();
        oldData = pre_intent.getStringExtra("data");

        System.out.println(oldData);

        JSONObject d;

        // get old data from json string
        try {
            d = new JSONObject(oldData);
            //System.out.println(d);
            id = (String)d.get("itemID");
            if (d.has("title"))title = (String)d.get("title");
            if (d.has("item_url"))item_url = (String)d.get("item_url");
            if (d.has("total_price"))price = d.get("total_price").toString();
            if (d.has("shippingCost"))ShippingPrice = d.get("shippingCost").toString();
            if (d.has("handlingTime"))handingTime = (String)d.get("handlingTime");
            if (d.has("oneDayShipping")) OneDayShipping = (String)d.get("oneDayShipping");
            if (d.has("shippingType"))shippingType = (String)d.get("shippingType");
            if (d.has("shippingToLocation")) shippingToLocation = (String)d.get("shippingToLocation");
            if (d.has("expeditedShipping")) Expedited_shipping = (String)d.get("expeditedShipping");
            System.out.println(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //set Toolbar Title
        ((TextView)findViewById(R.id.detail_title)).setText(title);

        //set redirct
        ImageView imgClick =(ImageView) findViewById(R.id.redirect);
        imgClick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println(item_url);
                Uri uri= Uri.parse(item_url);
                Intent browserIntent= new Intent(Intent.ACTION_VIEW,uri);
                startActivity(browserIntent);
            }
        });


        // get new detail json
        d = new JSONObject();
        try {
            d.put("data", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://yst-cs571hw9.wl.r.appspot.com/details";
        //String url = "http://10.0.2.2:8080/details";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, d, new Response.Listener<JSONObject>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("(***************************)succeed details");
                System.out.println(response);
                JSONObject data = response;
                try {
                    if (data.has("title"))  title = (String)data.get("title");
                    String temp = data.get("pictureURL").toString();
                    temp = temp.substring(1, temp.length()-1);
                    temp = temp.replace("\"", "");
                    pictureURL = temp.split(",");
                    if (data.has("subtitle"))  subtitle = data.get("subtitle").toString();
                    if (data.has("brand"))  brand = data.get("brand").toString();
                    if (data.has("feedbackscore"))FeedBack_score = data.get("feedbackscore").toString();
                    if (data.has("userID"))userID = data.get("userID").toString();
                    if (data.has("positivefb"))positiveFB_percent = data.get("positivefb").toString();
                    if (data.has("fdratingstar"))FB_ratingstar = data.get("fdratingstar").toString();
                    if (data.has("refund"))refund = data.get("refund").toString();
                    if (data.has("rWithin"))refund_Within = data.get("rWithin").toString();
                    if (data.has("SCpaidby"))ShippingCost_PaidBy = data.get("SCpaidby").toString();
                    if (data.has("rAccept"))ReturnsAccepted = data.get("rAccept").toString();
                    if (data.has("spec1"))spec1 = data.get("spec1").toString();
                    if (data.has("spec2"))spec2 = data.get("spec2").toString();
                    if (data.has("spec3"))spec3 = data.get("spec3").toString();
                    if (data.has("spec4"))spec4 = data.get("spec4").toString();
                    if (data.has("spec5"))spec5 = data.get("spec5").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LinearLayout progressgroup = (LinearLayout)findViewById(R.id.progress_group);
                progressgroup.removeAllViews();
                // Product
                // set title
                ((TextView)findViewById(R.id.itemSubTitle)).setText(title);
                // set image url
                LinearLayout images_group = (LinearLayout)findViewById(R.id.product_images);
                images_group.removeAllViews();
                for (int i = 0; i < pictureURL.length; i++){
                    ImageView imageView = new ImageView(getBaseContext());
                    imageView.setLayoutParams(new LayoutParams(1000, LayoutParams.MATCH_PARENT));
                    Picasso.get().load(pictureURL[i]).into(imageView);
                    images_group.addView(imageView);
                }
                // set price
                ((TextView)findViewById(R.id.Price)).setText("$"+price);
                // set shipping cost
                if(ShippingPrice.equals("0"))
                    ((TextView)findViewById(R.id.ShipCost)).setText("Free Shipping");
                else
                    ((TextView)findViewById(R.id.ShipCost)).setText("Ships for $"+ShippingPrice);
                //set brand
                ((TextView)findViewById(R.id.brandvalue)).setText(brand);
                if(!spec1.equals(""))
                    ((TextView)findViewById(R.id.spc1)).setText("•"+spec1);
                if(!spec2.equals(""))
                    ((TextView)findViewById(R.id.spc2)).setText("•"+spec2);
                if(!spec3.equals(""))
                    ((TextView)findViewById(R.id.spc3)).setText("•"+spec3);
                if(!spec4.equals(""))
                    ((TextView)findViewById(R.id.spc4)).setText("•"+spec4);
                if(!spec5.equals(""))
                    ((TextView)findViewById(R.id.spc5)).setText("•"+spec5);



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("(***************************)error details");
                System.out.println(error);
            }
        });
        requestQueue.add(request);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //viewPager.setCurrentItem(position);
                if (position==0) {
                    LinearLayout progressgroup = (LinearLayout)findViewById(R.id.progress_group);
                    progressgroup.removeAllViews();
                    // Product
                    // set title
                    ((TextView)findViewById(R.id.itemSubTitle)).setText(title);
                    // set image url
                    LinearLayout images_group = (LinearLayout)findViewById(R.id.product_images);
                    images_group.removeAllViews();
                    for (int i = 0; i < pictureURL.length; i++){
                        ImageView imageView = new ImageView(getBaseContext());
                        imageView.setLayoutParams(new LayoutParams(1000, LayoutParams.MATCH_PARENT));
                        Picasso.get().load(pictureURL[i]).into(imageView);
                        images_group.addView(imageView);
                    }
                    // set price
                    ((TextView)findViewById(R.id.Price)).setText("$"+price);
                    // set shipping cost
                    if(ShippingPrice.equals("0"))
                        ((TextView)findViewById(R.id.ShipCost)).setText("Free Shipping");
                    else
                        ((TextView)findViewById(R.id.ShipCost)).setText("Ships for $"+ShippingPrice);
                    //set brand
                    ((TextView)findViewById(R.id.brandvalue)).setText(brand);
                    if(!spec1.equals(""))
                        ((TextView)findViewById(R.id.spc1)).setText("•"+spec1);
                    if(!spec2.equals(""))
                        ((TextView)findViewById(R.id.spc2)).setText("•"+spec2);
                    if(!spec3.equals(""))
                        ((TextView)findViewById(R.id.spc3)).setText("•"+spec3);
                    if(!spec4.equals(""))
                        ((TextView)findViewById(R.id.spc4)).setText("•"+spec4);
                    if(!spec5.equals(""))
                        ((TextView)findViewById(R.id.spc5)).setText("•"+spec5);

                }
                else if (position == 1){
                    if(!FeedBack_score.equals(""))
                       ((TextView)findViewById(R.id.FS)).setText("•Feedback Score:"+FeedBack_score);
                    if(!userID.equals(""))
                        ((TextView)findViewById(R.id.UI)).setText("•User ID:"+userID);
                    if(!positiveFB_percent.equals(""))
                        ((TextView)findViewById(R.id.PFP)).setText("•Positive Feedback Percent:"+positiveFB_percent);
                    if(!FB_ratingstar.equals(""))
                        ((TextView)findViewById(R.id.FRS)).setText("•Feedback Rating Star:"+FB_ratingstar);
                    if(!refund.equals(""))
                        ((TextView)findViewById(R.id.RF)).setText("•Refund:"+refund);
                    if(!refund_Within.equals(""))
                        ((TextView)findViewById(R.id.RW)).setText("•Returns Within:"+refund_Within);
                    if(!ShippingCost_PaidBy.equals(""))
                        ((TextView)findViewById(R.id.SCPB)).setText("•Shipping Cost Paid By:"+ShippingCost_PaidBy);
                    if(!ReturnsAccepted.equals(""))
                        ((TextView)findViewById(R.id.RA)).setText("•Returns Accepted:"+ReturnsAccepted);
                }
                else{
                    if(!handingTime.equals(""))
                        ((TextView)findViewById(R.id.HT)).setText("•Handling Time:"+handingTime);
                    if(!OneDayShipping.equals(""))
                        ((TextView)findViewById(R.id.ODSA)).setText("•One Day Shipping Available:"+OneDayShipping);
                    if(!shippingType.equals(""))
                        ((TextView)findViewById(R.id.ST)).setText("•Shipping Type:"+shippingType);
                    if(!FB_ratingstar.equals(""))
                        ((TextView)findViewById(R.id.SF)).setText("•Shipping From:"+FB_ratingstar);
                    if(!shippingToLocation.equals(""))
                        ((TextView)findViewById(R.id.STL)).setText("•Ship To Locations:"+shippingToLocation);
                    if(!Expedited_shipping.equals(""))
                        ((TextView)findViewById(R.id.ES)).setText("•Expeditied Shipping:"+Expedited_shipping);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}