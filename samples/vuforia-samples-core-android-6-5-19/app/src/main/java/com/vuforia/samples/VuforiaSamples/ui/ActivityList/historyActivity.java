package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.vuforia.samples.VuforiaSamples.R;

import java.util.ArrayList;
import java.util.List;

public class historyActivity extends AppCompatActivity {
    private List<Gerneral> generals;
    private ListView lvGeneral;
    private ScrollView scrollView;
    private GeneralAdapter generalAdapt;
    private Context context;
    private ActionBar actionBar;
    private DBHelper mdbhelper = null;

    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        scrollView = (ScrollView)this.findViewById(R.id.scroll_list);
        context = this;

        mdbhelper = new DBHelper(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("UserID");
        DataGetFromDatabase();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void  DataGetFromDatabase() {
        Log.e("username", username);
        Cursor cur = mdbhelper.getPersonRecord(username);

        generals = new ArrayList<>();

        int count = 0;
        if (cur!=null) {
            while (cur.moveToNext()) {
                String time = cur.getString(cur.getColumnIndex("DateID"));
                String day = time.substring(0,4) + "." + time.substring(4, 6) + "." + time.substring(6, 8) +
                        " " + time.substring(8, 10) + ":" + time.substring(10);
                Log.e("day", day);

                Float RunDis = cur.getFloat(cur.getColumnIndex("Distance"));
                String Dis = String.format("%.1f m", RunDis);
                Log.e("distance", "" + RunDis);

                String duration = cur.getString(cur.getColumnIndex("Time"));
                Log.e("time", duration);

                double calor = RunDis * 60 * 1.036/1000;
                String result = String.format("%.1f kcal",calor);
                Log.e("calor", "" +result);

                //Bitmap path = mdbhelper.getRunningPathImage(username, time);
                Bitmap path = null;

                Gerneral temp = new Gerneral(day, Dis, duration, result, path);
                count ++;
                generals.add(temp);
            }
        }
        lvGeneral = (ListView) findViewById(R.id.day_list);
        generalAdapt = new GeneralAdapter(this, generals);
        lvGeneral.setAdapter(generalAdapt);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}