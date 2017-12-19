package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.database.Cursor;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vuforia.samples.VuforiaSamples.R;

public class RunActivity extends Activity {
    private Button finish = null;
    //声明AMapLocationClient类对象，定位发起端
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    public AMapLocationListener mLocationListener = new RunActivity.MyAMapLocationListener();
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private final String TAG = "Location";
    Button button;
    private DBHelper mdbhelper;
    private double x,y;
    public int RecordCount;
    public boolean IsFirstRecord;
    public String dateID;
    public boolean isFinish = false;
    public float distance, speed;
    public LatLng prePoint;
    public long startTime, presentTime;
    public String UserName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_targets);
        Log.e(TAG, "Map has been successfully started!");
        Intent intent = getIntent();
        if(intent != null) {
            UserName = intent.getStringExtra("UserID");
        }

        mdbhelper = new DBHelper(this);

        button = (Button)findViewById(R.id.finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = mdbhelper.getOnePointData(dateID);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String date = cursor.getString(cursor.getColumnIndex("DateID"));
                        double la = cursor.getDouble(cursor.getColumnIndex("Latitude"));
                        double lo = cursor.getDouble(cursor.getColumnIndex("Longitude"));
                        int ptr = cursor.getInt(cursor.getColumnIndex("RecordID"));
                        float spe  = cursor.getFloat(cursor.getColumnIndex("Speed"));
                        Log.e("Position", " " + ptr);
                        Log.e("DateId: ", date);
                        Log.e("Latitude:", " " + la);
                        Log.e("Longitude: ", " " + lo);
                        Log.e("Speed: ", " "+spe);
                    }

                }

                Intent ToPathActivity = new Intent(RunActivity.this, ResultActivity.class);
                ToPathActivity.putExtra("PointNumber", RecordCount);
                ToPathActivity.putExtra("DateID", dateID);

                mdbhelper.insertDistance(UserName, dateID, distance,  "HELLO", 0.0f);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        double dis = cursor.getFloat(cursor.getColumnIndex("Distance"));
                        Log.e("TotalDis", " " + dis);
                    }
                }

                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
                mLocationClient.unRegisterLocationListener(mLocationListener);

                startActivity(ToPathActivity);
                isFinish = true;
                finish();
            }
        });

        RecordCount = 0;
        IsFirstRecord = true;
        distance = 0;
        isFinish = false;
        //开始定位
        location();
    }

    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private class MyAMapLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    Log.e("位置：", aMapLocation.getAddress());
                    x = aMapLocation.getLongitude();
                    y = aMapLocation.getLatitude();
                    speed = aMapLocation.getSpeed();
                    presentTime = aMapLocation.getTime();

                    if (IsFirstRecord) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                        Date date = new Date(aMapLocation.getTime());
                        dateID = df.format(date);
                        IsFirstRecord = false;
                        prePoint = new LatLng(y,x);
                        startTime = presentTime;
                    }
                    if (!isFinish){
                        mdbhelper.insertPointPosition(dateID, RecordCount, y, x, speed);
                        RecordCount ++;
                        LatLng NowPoint = new LatLng(y, x);
                        distance+= AMapUtils.calculateLineDistance(NowPoint, prePoint);
                        prePoint = NowPoint;
                        Log.e("Distance ", "=" + distance);
                        Log.e("Speed ", "="+speed);
                        Log.e("Time ", "=" + (presentTime - startTime));
                        //Log.e("isFinish ", "=" + isFinish);
                    }
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    }

}
