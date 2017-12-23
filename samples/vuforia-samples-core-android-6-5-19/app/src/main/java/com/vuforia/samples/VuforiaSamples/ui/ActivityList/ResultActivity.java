package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import com.vuforia.samples.VuforiaSamples.R;
import com.vuforia.samples.VuforiaSamples.app.ImageTargets.ImageTargets;

public class ResultActivity extends Activity {

    //AMap是地图对象
    private AMap aMap;
    private MapView mapView;
    private final String TAG = "Location";
    private double x,y;
    Polyline polyline = null;
    public List<LatLng> LatlngList = null;
    public List<Float> SpeedList = null;
    public DBHelper mdbhelper;
    public float Distance;
    public String Username;
    public String dateID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "Map has been successfully finished!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mdbhelper = new DBHelper(this);
        LatlngList = new ArrayList<LatLng>();
        SpeedList = new ArrayList<Float>();

        Intent i = getIntent();
        dateID = i.getStringExtra("DateID");
        Username = i.getStringExtra("UserID");
        Log.e("DateID ", " " + dateID);
        Log.e("UserID", Username);

        Cursor cursor = mdbhelper.getPersonRecord(Username);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String temp = cursor.getString(cursor.getColumnIndex("UserID"));
                Distance = cursor.getFloat(cursor.getColumnIndex("Distance"));
                float speed = cursor.getFloat(cursor.getColumnIndex("AverageSpeed"));
                Log.e("name", temp);
                Log.e("speed", ""+speed);
                Log.e("RunDis ", " " + Distance);
            }
        }

        cursor = mdbhelper.getOnePointData(dateID);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndex("DateID"));
                double la = cursor.getDouble(cursor.getColumnIndex("Latitude"));
                double lo = cursor.getDouble(cursor.getColumnIndex("Longitude"));
                int ptr = cursor.getInt(cursor.getColumnIndex("RecordID"));
                float PointSpeed = cursor.getFloat(cursor.getColumnIndex("Speed"));
                Log.e("Position", " " + ptr);
                Log.e("DateId: ", date);
                Log.e("Latitude:", " " + la);
                Log.e("Longitude: ", " " + lo);
                Log.e("Speed:", " " + PointSpeed);
                LatLng temp = new LatLng(la, lo);
                LatlngList.add(temp);
                SpeedList.add(PointSpeed);
            }
        }

        mapView = (MapView) findViewById(R.id.map_showpath);
        Log.e(TAG, "Map has been successfully started!");
        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
            //settings.setMyLocationButtonEnabled(true);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(LatlngList.get(0)));
        }
        PaintPath();

        final Button backButton = (Button) findViewById(R.id.Back);
        backButton.setEnabled(false);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.putExtra("UserID", Username);
                startActivity(intent);
                finish();
            }

        });

        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                getscreenBitmap();
                backButton.setEnabled(true);
                Log.e(TAG, "done!");
            }
        }.start();
    }

    private void getscreenBitmap() {
        Log.e("31","1111");

        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {
                Log.e("11", "1111");
                mdbhelper.insertUserPathImage(Username, dateID, bitmap);
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int status) {
                Log.e("21", "1111");
                //mdbhelper.insertUserPathImage(Username, dateID, bitmap);
            }
        });
    }

    private void PaintPath() {
        int size = SpeedList.size();
        LatLng pre = LatlngList.get(0);
        Log.e("Size: ", " "+ size);
        for (int i=1; i<size; i++) {
            if (SpeedList.get(i) >= 2.22) {
                List<LatLng> FastList = new ArrayList<LatLng>();
                FastList.add(pre);
                LatLng temp = LatlngList.get(i);
                FastList.add(temp);
                polyline =aMap.addPolyline(new PolylineOptions().addAll(FastList).width(20).color(Color.argb(200, 127, 255, 0)));
                pre = temp;
            }
            else if (SpeedList.get(i) < 2.22 && SpeedList.get(i) >= 1.67) {
                List<LatLng> MidList = new ArrayList<LatLng>();
                MidList.add(pre);
                LatLng temp = LatlngList.get(i);
                MidList.add(temp);
                polyline =aMap.addPolyline(new PolylineOptions().addAll(MidList).width(20).color(Color.argb(200, 0, 191, 255)));
                pre = temp;
            }
            else if (SpeedList.get(i) < 1.67 && SpeedList.get(i) >= 1.11) {
                List<LatLng> SlowList = new ArrayList<LatLng>();
                SlowList.add(pre);
                LatLng temp = LatlngList.get(i);
                SlowList.add(temp);
                polyline =aMap.addPolyline(new PolylineOptions().addAll(SlowList).width(20).color(Color.argb(200, 255, 215, 0)));
                pre = temp;
            }
            else {
                List<LatLng> OtherList = new ArrayList<LatLng>();
                OtherList.add(pre);
                LatLng temp = LatlngList.get(i);
                OtherList.add(temp);
                polyline =aMap.addPolyline(new PolylineOptions().addAll(OtherList).width(20).color(Color.argb(200, 255, 215, 0)));
                pre = temp;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}

