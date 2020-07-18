package com.example.myapplication_hw8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity  {
    Button SEARCH;
    EditText KW,LP,HP;
    CheckBox neww,used,uns;
    Spinner sb;
    TextView wkw,wp;
    List<CheckBox> checkBoxList = new ArrayList<CheckBox>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KW=(EditText) findViewById(R.id.kw);
        LP=(EditText) findViewById(R.id.Low);
        HP=(EditText) findViewById(R.id.High);
        neww=(CheckBox) findViewById(R.id.neww);
        used=(CheckBox) findViewById(R.id.used);
        uns=(CheckBox) findViewById(R.id.uns);
        sb=(Spinner) findViewById(R.id.sb);
        wkw=(TextView) findViewById(R.id.wkw);
        wp=(TextView) findViewById(R.id.wp);
    }
    public void search(View view) throws JSONException {
      wkw.setText("");
      wp.setText("");
      int next=0;
      String kw=KW.getText().toString();
      if(kw.matches(""))
      {
       wkw.setText("Please enter mandartory field");
          Toast t1 = Toast.makeText(getApplicationContext(),
                  "Please fill all fields with errors",
                  Toast.LENGTH_LONG);
          t1.show();
      }
      else
          next++;
      String low=LP.getText().toString();
      String high=HP.getText().toString();
      if(!low.matches("")&!high.matches("")) {
          int lp = Integer.parseInt(LP.getText().toString());
          int hp = Integer.parseInt(HP.getText().toString());
          if (lp < 0 || hp < 0 || lp > hp) {
              wp.setText("Please enter valid price values");
              Toast t2 = Toast.makeText(getApplicationContext(),
                      "Please fill all fields with errors",
                      Toast.LENGTH_LONG);
              t2.show();
          }
          else
              next++;
      }
      else if(low.matches("")&!high.matches(""))
      {
          if(Integer.parseInt(high)<0) {
              wp.setText("Please enter valid price values");
              Toast t3 = Toast.makeText(getApplicationContext(),
                      "Please fill all fields with errors",
                      Toast.LENGTH_LONG);
              t3.show();
          }
          else
              next++;
      }
      else if(!low.matches("")&high.matches(""))
      {
          if(Integer.parseInt(low)<0) {
              wp.setText("Please enter valid price values");
              Toast t4 = Toast.makeText(getApplicationContext(),
                      "Please fill all fields with errors",
                      Toast.LENGTH_LONG);
              t4.show();
          }
          else
              next++;
      }
      else
       next++;
      String sort_by=(sb.getSelectedItem()).toString();

        String data = "";
        data += "{'key_word':" + "'" + kw + "'";
        if (!low.equals("")){
            data += ",'range_from':'" + low + "'";
        }
        if (!high.equals("")){
            data += ",'range_to':'" + high + "'";
        }
        String cond="[";
        if(neww.isChecked())
            cond+= "'" + neww.getText() + "',";
        if(used.isChecked())
            cond+= "'" + used.getText() + "',";
        if(uns.isChecked())
            cond+= "'" + uns.getText() + "',";
        if (cond.substring(cond.length()-1,cond.length()).equals(",")){
            cond = cond.substring(0, cond.length()-1);
        }
        cond += "]";
        if (!cond.equals("[]")){
            data += ",'condition':" + cond;
        }
        data += ",'sort_by':'" + sort_by + "'}";
        //System.out.println(data);
        String d = data.replaceAll("'", "\"");

      if(next==2) {
          Intent intent = new Intent(MainActivity.this,
                  ProgressBar.class);
          intent.putExtra("data", d);
          intent.putExtra("keyword",kw);
          startActivity(intent);
      }

    }
    public void clear(View view)
    {
        KW.setText("");
        LP.setText("");
        HP.setText("");
        neww.setChecked(false);
        used.setChecked(false);
        uns.setChecked(false);
        sb.setSelection(0);
        wkw.setText("");
        wp.setText("");
    }
};



