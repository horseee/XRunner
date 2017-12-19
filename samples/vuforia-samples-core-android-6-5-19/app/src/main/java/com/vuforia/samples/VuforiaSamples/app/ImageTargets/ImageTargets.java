/*===============================================================================
Copyright (c) 2016-2017 PTC Inc. All Rights Reserved.


Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.vuforia.samples.VuforiaSamples.app.ImageTargets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Text;
import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.Image;
import com.vuforia.ObjectTracker;
import com.vuforia.State;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;
import com.vuforia.samples.SampleApplication.SampleApplicationControl;
import com.vuforia.samples.SampleApplication.SampleApplicationException;
import com.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.vuforia.samples.SampleApplication.utils.SampleApplicationGLView;
import com.vuforia.samples.SampleApplication.utils.Texture;
import com.vuforia.samples.VuforiaSamples.R;
import com.vuforia.samples.VuforiaSamples.ui.ActivityList.DBHelper;
import com.vuforia.samples.VuforiaSamples.ui.ActivityList.ResultActivity;
import com.vuforia.samples.VuforiaSamples.ui.ActivityList.RunActivity;
import com.vuforia.samples.VuforiaSamples.ui.SampleAppMenu.SampleAppMenu;
import com.vuforia.samples.VuforiaSamples.ui.SampleAppMenu.SampleAppMenuGroup;
import com.vuforia.samples.VuforiaSamples.ui.SampleAppMenu.SampleAppMenuInterface;


public class ImageTargets extends Activity implements SampleApplicationControl, SampleAppMenuInterface
{
    private static final String LOGTAG = "ImageTargets";
    
    SampleApplicationSession vuforiaAppSession;
    
    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    private int mStartDatasetsIndex = 0;
    private int mDatasetsNumber = 0;
    private ArrayList<String> mDatasetStrings = new ArrayList<String>();
    
    // Our OpenGL view:
    private SampleApplicationGLView mGlView;
    
    // Our renderer:
    private ImageTargetRenderer mRenderer;
    
    private GestureDetector mGestureDetector;
    
    // The textures we will use for rendering:
    private Vector<Texture> mTextures;
    
    private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;
    private boolean mContAutofocus = true;
    private boolean mExtendedTracking = false;

    private View mFocusOptionView;
    private View mFlashOptionView;
    
    private RelativeLayout mUILayout;
    
    private SampleAppMenu mSampleAppMenu;

    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    
    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;
    
    boolean mIsDroidDevice = false;

    //Location Varible
    private Button finish = null;
    //声明AMapLocationClient类对象，定位发起端
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    public AMapLocationListener mLocationListener = new ImageTargets.MyAMapLocationListener();
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private final String TAG = "Location";
    private Button button;
    private Button test;

    public boolean isImage;
    public boolean isModel;
    public Var var;
    public Test element;
    private TextView record_dis;
    public int selectTextureNum;
    public int cmd;
    private double nStart = 0;

    private DBHelper mdbhelper;

    private double x,y;
    public int RecordCount;
    public boolean IsFirstRecord;
    public String dateID;
    public boolean isFinish = false;
    public float distance, speed;
    public LatLng prePoint;
    public String name = null;

    private int seconds;
    private TextView runningtime;
    private int randomNumber;
    public int ModelNumber = 0;

    private Vibrator vibrator=null;
    private boolean isVibra = false;

    // Called when the activity first starts or the user navigates back to an
    // activity.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_targets);
        Intent intent = getIntent();
        if(intent != null) {
            name = intent.getStringExtra("UserID");
        }
        
        vuforiaAppSession = new SampleApplicationSession(this);
        
        startLoadingAnimation();
        mDatasetStrings.add("Tarmac.xml");
        mDatasetStrings.add("Image.xml");

        vuforiaAppSession
            .initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        mGestureDetector = new GestureDetector(this, new GestureListener());
        
        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();
        mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith(
            "droid");


        //StartLocation
        Log.e(TAG, "Map has been started!");
        mdbhelper = new DBHelper(this);

        var = new Var();
        element = new Test();
        var.registerObserver(element);
        var.setVar(0);
        isImage = false;
        isModel = false;
        isVibra = false;
        selectTextureNum = 0;

        RecordCount = 0;
        IsFirstRecord = true;
        distance = 0;
        isFinish = false;
        //randomNumber = (int) (Math.random()*800 + 200);//产生20-30的随机数
        randomNumber = 0;
        Log.e("random", "" + randomNumber);

        //开始定位
        vibrator = ( Vibrator ) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
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

                    if (IsFirstRecord) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                        Date date = new Date(aMapLocation.getTime());
                        dateID = df.format(date);
                        IsFirstRecord = false;
                        prePoint = new LatLng(y,x);
                    }
                    if (!isFinish){
                        mdbhelper.insertPointPosition(dateID, RecordCount, y, x, speed);
                        RecordCount ++;
                        LatLng NowPoint = new LatLng(y, x);
                        distance+= AMapUtils.calculateLineDistance(NowPoint, prePoint);
                        prePoint = NowPoint;
                        TextView dis_text = (TextView) mUILayout.findViewById(R.id.distance_edit);
                        String dis_str = "" + (int)distance+"m";
                        dis_text.setText(dis_str);
                        TextView Calorie = (TextView) mUILayout.findViewById(R.id.calorie_edit);
                        double calor = distance * 60 * 1.036/1000;
                        String result = String.format("%.1f kcal",calor);
                        Calorie.setText(result);


                        if (distance > randomNumber-1) {
                            if (!isVibra) {
                                vibrator.vibrate(1000);//震动一秒
                                isVibra = true;
                                ModelNumber = (int)(Math.random() * 6.99);
                                Log.e("randomModelNumber", "" + ModelNumber);
                                cmd = 7;
                                menuProcess(cmd);
                                Log.e("XML",mStartDatasetsIndex+"");
                                isImage = !isImage;
                            }
                        }

                        if (distance > randomNumber + 50) {
                            isVibra = false;

                            if (var.getVar() == 1)
                                mdbhelper.updateModelData(name, ModelNumber, true, 1);

                            int newDis = (int)(Math.random()*1);
                            randomNumber = newDis + (int)distance;
                            Log.e("random", ""+randomNumber);
                            cmd = 6;
                            var.setVar(0);
                            menuProcess(cmd);
                            isImage = !isImage;
                            Log.e("XML",mStartDatasetsIndex+"");
                        }

                        Log.e("Distance ", dis_str);
                        Log.e("Speed ", "="+speed);
                        Log.e("isFinish ", "=" + isFinish);
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


    
    // Process Single Tap event to trigger autofocus
    private class GestureListener extends
        GestureDetector.SimpleOnGestureListener
    {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();

        @Override
        public void onShowPress(MotionEvent e) {
            Log.i("MotionEvent", "onShowPress");
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            boolean result = CameraDevice.getInstance().setFocusMode(
                    CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
            if (!result)
                Log.e("SingleTapUp", "Unable to trigger focus");

            // Generates a Handler to trigger continuous auto-focus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable()
            {
                public void run()
                {
                    if (mContAutofocus)
                    {
                        final boolean autofocusResult = CameraDevice.getInstance().setFocusMode(
                                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

                        if (!autofocusResult)
                            Log.e("SingleTapUp", "Unable to re-enable continuous auto-focus");
                    }
                }
            }, 1000L);

            return true;
        }
    }
    
    // We want to load specific textures from the APK, which we will later use
    // for rendering.
    private void loadTextures()
    {
        mTextures.add(Texture.loadTextureFromApk("texture1.png", getAssets()));
        Log.e("TEXTURE", mTextures.get(0).mWidth+" "+mTextures.get(0).mHeight);
        mTextures.add(Texture.loadTextureFromApk("texture2.png", getAssets()));
        Log.e("TEXTURE", mTextures.get(1).mWidth+" "+mTextures.get(1).mHeight);
        mTextures.add(Texture.loadTextureFromApk("texture3.png", getAssets()));
        Log.e("TEXTURE", mTextures.get(2).mWidth+" "+mTextures.get(2).mHeight);
        mTextures.add(Texture.loadTextureFromApk("texture4.png", getAssets()));
        Log.e("TEXTURE", mTextures.get(3).mWidth+" "+mTextures.get(3).mHeight);
        mTextures.add(Texture.loadTextureFromApk("texture5.png", getAssets()));
        Log.e("TEXTURE", mTextures.get(4).mWidth+" "+mTextures.get(4).mHeight);
        mTextures.add(Texture.loadTextureFromApk("texture6.png", getAssets()));
        Log.e("TEXTURE", mTextures.get(5).mWidth+" "+mTextures.get(5).mHeight);
        mTextures.add(Texture.loadTextureFromApk("texture7.png", getAssets()));
        Log.e("TEXTURE", mTextures.get(6).mWidth+" "+mTextures.get(6).mHeight);
        mTextures.add(Texture.loadTextureFromApk("ImageTargets/Buildings.jpeg",
                getAssets()));
    }

    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume()
    {
        Log.d(LOGTAG, "onResume");
        super.onResume();

        showProgressIndicator(true);
        
        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        vuforiaAppSession.onResume();
    }
    
    
    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config)
    {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);
        
        vuforiaAppSession.onConfigurationChanged();
    }
    
    
    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause()
    {
        Log.d(LOGTAG, "onPause");
        super.onPause();
        
        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
        
        // Turn off the flash
        if (mFlashOptionView != null && mFlash)
        {
            // OnCheckedChangeListener is called upon changing the checked state
            setMenuToggle(mFlashOptionView, false);
        }
        
        try
        {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
    }
    
    
    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();
        
        try
        {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        // Unload texture:
        mTextures.clear();
        mTextures = null;
        
        System.gc();
    }
    
    
    // Initializes AR application components.
    private void initApplicationAR()
    {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new ImageTargetRenderer(this, vuforiaAppSession);
        mRenderer.selectTextures(selectTextureNum);
        mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);
    }
    
    
    private void startLoadingAnimation()
    {
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
            null);
        
        mUILayout.setVisibility(View.VISIBLE);
        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUILayout
            .findViewById(R.id.loading_indicator);
        
        // Shows the loading indicator at start
        loadingDialogHandler
            .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
        
        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        
    }
    
    
    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData()
    {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset == null)
            mCurrentDataset = objectTracker.createDataSet();

        if (mCurrentDataset == null)
            return false;

        if (!mCurrentDataset.load(
                mDatasetStrings.get(mCurrentDatasetSelectionIndex),
                STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;

        if (!objectTracker.activateDataSet(mCurrentDataset))
            return false;

        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++)
        {
            Trackable trackable = mCurrentDataset.getTrackable(count);
            if(isExtendedTrackingActive())
            {
                trackable.startExtendedTracking();
            }

            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data "
                    + (String) trackable.getUserData());
        }

        return true;
    }
    
    
    @Override
    public boolean doUnloadTrackersData()
    {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset != null && mCurrentDataset.isActive())
        {
            if (objectTracker.getActiveDataSet(0).equals(mCurrentDataset)
                    && !objectTracker.deactivateDataSet(mCurrentDataset))
            {
                result = false;
            } else if (!objectTracker.destroyDataSet(mCurrentDataset))
            {
                result = false;
            }

            mCurrentDataset = null;
        }

        return result;
    }

    @Override
    public void onVuforiaResumed()
    {
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
    }

    @Override
    public void onVuforiaStarted()
    {
        if (mContAutofocus)
        {
            // Set camera focus mode
            if(!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO))
            {
                // If continuous autofocus mode fails, attempt to set to a different mode
                if(!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO))
                {
                    CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
                }

                // Update Toggle state
                setMenuToggle(mFocusOptionView, false);
            }
            else
            {
                // Update Toggle state
                setMenuToggle(mFocusOptionView, true);
            }
        }
        else
        {
            setMenuToggle(mFocusOptionView, false);
        }

        showProgressIndicator(false);
    }


    public void showProgressIndicator(boolean show)
    {
        if (loadingDialogHandler != null)
        {
            if (show)
            {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
            }
            else
            {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
            }
        }
    }


    @Override
    public void onInitARDone(SampleApplicationException exception)
    {

        if (exception == null)
        {
            initApplicationAR();

            mRenderer.setActive(true);

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));

            mUILayout = (RelativeLayout) View.inflate(this, R.layout.activity_image_targets,
                    null);

            Log.e(TAG, "Button has been started!");
            //test = (Button)mUILayout.findViewById(R.id.test);
            /*test.setOnClickListener(new View.OnClickListener() {
                @Override
                public  void onClick(View v){
                    if(!isImage)
                        cmd = 7;
                    else{
                        cmd = 6;
                        var.setVar(0);
                    }
                    menuProcess(cmd);
                    isImage = !isImage;
                    Log.e("XML",mStartDatasetsIndex+"");
                }
            });*/
            final TextView runningtime = (TextView)mUILayout.findViewById(R.id.duration_edit);
            new CountDownTimer(10000000, 1000) {

                public void onTick(long millisUntilFinished) {
                    Long UserRunTime = (10000000 -millisUntilFinished) / 1000;
                    long hour = UserRunTime / 3600;
                    long minute = UserRunTime / 60;
                    long second = UserRunTime % 60;
                    if (hour != 0)
                        runningtime.setText(hour + ":" + minute + ":" + second);
                    else if (second < 10)
                        runningtime.setText(minute + ":0"+second);
                    else if (second >= 10)
                        runningtime.setText(minute + ":"+second);
                }
                public void onFinish() {
                    Log.e(TAG, "done!");
                }
            }.start();


            button = (Button)mUILayout.findViewById(R.id.finish);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button.setBackgroundResource(R.drawable.start_radius_2);
                            break;
                        case MotionEvent.ACTION_UP:
                            Intent ToPathActivity = new Intent(ImageTargets.this, ResultActivity.class);
                            ToPathActivity.putExtra("PointNumber", RecordCount);
                            ToPathActivity.putExtra("DateID", dateID);
                            ToPathActivity.putExtra("UserID", name);

                            mdbhelper.insertDistance(name, dateID, distance, (runningtime.getText()).toString(), 2.0f);
                            mdbhelper.updateLevelAndDistance(name, distance);

                            Cursor cursor = mdbhelper.getDateDis(name, dateID);
                            if (cursor != null) {
                                while (cursor.moveToNext()) {
                                    double dis = cursor.getFloat(cursor.getColumnIndex("Distance"));
                                    Log.e("TotalDis", " " + dis);
                                    String time = cursor.getString(cursor.getColumnIndex("Time"));
                                    Log.e("Time", time);
                                }
                            }

                            mLocationClient.stopLocation();
                            mLocationClient.onDestroy();
                            mLocationClient.unRegisterLocationListener(mLocationListener);
                            startActivity(ToPathActivity);
                            isFinish = true;
                            finish();
                            break;
                    }
                    return true;
                }
            });


            mUILayout.setVisibility(View.VISIBLE);

            // Adds the inflated layout to the view
            addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));

            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();

            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);

            vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);

            mSampleAppMenu = new SampleAppMenu(this, this, "Settings",
                    mGlView, mUILayout, null);
            setSampleAppMenuSettings();

        } else
        {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }


    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message)
    {
        final String errorMessage = message;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (mErrorDialog != null)
                {
                    mErrorDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ImageTargets.this);
                builder
                        .setMessage(errorMessage)
                        .setTitle(getString(R.string.INIT_ERROR))
                        .setCancelable(false)
                        .setIcon(0)
                        .setPositiveButton(getString(R.string.button_OK),
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        finish();
                                    }
                                });

                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }
    
    
    @Override
    public void onVuforiaUpdate(State state)
    {
        if (mSwitchDatasetAsap)
        {
            mSwitchDatasetAsap = false;
            TrackerManager tm = TrackerManager.getInstance();
            ObjectTracker ot = (ObjectTracker) tm.getTracker(ObjectTracker
                    .getClassType());
            if (ot == null || mCurrentDataset == null
                    || ot.getActiveDataSet(0) == null)
            {
                Log.d(LOGTAG, "Failed to swap datasets");
                return;
            }

            doUnloadTrackersData();
            doLoadTrackersData();
        }
    }
    
    
    @Override
    public boolean doInitTrackers()
    {
        // Indicate if the trackers were initialized correctly
        boolean result = true;
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;
        // Trying to initialize the image tracker
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null)
        {
            Log.e(
                    LOGTAG,
                    "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else
        {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
        return result;
    }
    
    
    @Override
    public boolean doStartTrackers()
    {
        // Indicate if the trackers were started correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();
        return result;
    }
    
    
    @Override
    public boolean doStopTrackers()
    {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();
        return result;
    }
    
    
    @Override
    public boolean doDeinitTrackers()
    {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        
        return result;
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // Process the Gestures
        if (mSampleAppMenu != null && mSampleAppMenu.processEvent(event))
            return true;
        
        return mGestureDetector.onTouchEvent(event);
    }
    
    
    boolean isExtendedTrackingActive()
    {
        return mExtendedTracking;
    }
    
    final public static int CMD_BACK = -1;
    final public static int CMD_EXTENDED_TRACKING = 1;
    final public static int CMD_AUTOFOCUS = 2;
    final public static int CMD_FLASH = 3;
    final public static int CMD_CAMERA_FRONT = 4;
    final public static int CMD_CAMERA_REAR = 5;
    final public static int CMD_DATASET_START_INDEX = 6;
    
    
    // This method sets the menu's settings
    private void setSampleAppMenuSettings()
    {
        SampleAppMenuGroup group;

        group = mSampleAppMenu.addGroup("", false);
        group.addTextItem(getString(R.string.menu_back), -1);

        group = mSampleAppMenu.addGroup("", true);
        group.addSelectionItem(getString(R.string.menu_extended_tracking),
                CMD_EXTENDED_TRACKING, false);
        mFocusOptionView = group.addSelectionItem(getString(R.string.menu_contAutofocus),
                CMD_AUTOFOCUS, mContAutofocus);
        mFlashOptionView = group.addSelectionItem(
                getString(R.string.menu_flash), CMD_FLASH, false);

        CameraInfo ci = new CameraInfo();
        boolean deviceHasFrontCamera = false;
        boolean deviceHasBackCamera = false;
        for (int i = 0; i < Camera.getNumberOfCameras(); i++)
        {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
                deviceHasFrontCamera = true;
            else if (ci.facing == CameraInfo.CAMERA_FACING_BACK)
                deviceHasBackCamera = true;
        }

        if (deviceHasBackCamera && deviceHasFrontCamera)
        {
            group = mSampleAppMenu.addGroup(getString(R.string.menu_camera),
                    true);
            group.addRadioItem(getString(R.string.menu_camera_front),
                    CMD_CAMERA_FRONT, false);
            group.addRadioItem(getString(R.string.menu_camera_back),
                    CMD_CAMERA_REAR, true);
        }

        group = mSampleAppMenu
                .addGroup(getString(R.string.menu_datasets), true);
        mStartDatasetsIndex = CMD_DATASET_START_INDEX;
        Log.e("DatasetIndex", mStartDatasetsIndex+"");
        mDatasetsNumber = mDatasetStrings.size();

        //group.addRadioItem("Stones & Chips", mStartDatasetsIndex, true);
        group.addRadioItem("Disable", mStartDatasetsIndex, true);
        group.addRadioItem("Able", mStartDatasetsIndex + 1 , false);

        mSampleAppMenu.attachMenu();
    }


    private void setMenuToggle(View view, boolean value)
    {
        // OnCheckedChangeListener is called upon changing the checked state
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            ((Switch) view).setChecked(value);
        } else
        {
            ((CheckBox) view).setChecked(value);
        }
    }
    
    
    @Override
    public boolean menuProcess(int command)
    {

        boolean result = true;

        switch (command)
        {
            //结束ARCamera
            case CMD_BACK:

                finish();
                break;
            //设置闪光灯
            case CMD_FLASH:

                result = CameraDevice.getInstance().setFlashTorchMode(!mFlash);
                if (result)
                {
                    mFlash = !mFlash;
                } else
                {
                    showToast(getString(mFlash ? R.string.menu_flash_error_off
                            : R.string.menu_flash_error_on));
                    Log.e(LOGTAG,
                            getString(mFlash ? R.string.menu_flash_error_off
                                    : R.string.menu_flash_error_on));
                }
                break;

            case CMD_AUTOFOCUS:

                if (mContAutofocus)
                {
                    result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);

                    if (result)
                    {
                        mContAutofocus = false;
                    } else
                    {
                        showToast(getString(R.string.menu_contAutofocus_error_off));
                        Log.e(LOGTAG,
                                getString(R.string.menu_contAutofocus_error_off));
                    }
                } else
                {
                    result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

                    if (result)
                    {
                        mContAutofocus = true;
                    } else
                    {
                        showToast(getString(R.string.menu_contAutofocus_error_on));
                        Log.e(LOGTAG,
                                getString(R.string.menu_contAutofocus_error_on));
                    }
                }
                break;

            case CMD_CAMERA_FRONT:
            case CMD_CAMERA_REAR:

                // Turn off the flash
                if (mFlashOptionView != null && mFlash)
                {
                    setMenuToggle(mFlashOptionView, false);
                }
                vuforiaAppSession.stopCamera();
                vuforiaAppSession
                        .startAR(command == CMD_CAMERA_FRONT ? CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_FRONT
                                : CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_BACK);
                break;

            case CMD_EXTENDED_TRACKING:

                for (int tIdx = 0; tIdx < mCurrentDataset.getNumTrackables(); tIdx++)
                {
                    Trackable trackable = mCurrentDataset.getTrackable(tIdx);

                    if (!mExtendedTracking)
                    {
                        if (!trackable.startExtendedTracking())
                        {
                            Log.e(LOGTAG,
                                    "Failed to start extended tracking target");
                            result = false;
                        } else
                        {
                            Log.d(LOGTAG,
                                    "Successfully started extended tracking target");
                        }
                    } else
                    {
                        if (!trackable.stopExtendedTracking())
                        {
                            Log.e(LOGTAG,
                                    "Failed to stop extended tracking target");
                            result = false;
                        } else
                        {
                            Log.d(LOGTAG,
                                    "Successfully started extended tracking target");
                        }
                    }
                }
                if (result)
                    mExtendedTracking = !mExtendedTracking;
                break;

            default:

                if (command >= mStartDatasetsIndex
                        && command < mStartDatasetsIndex + mDatasetsNumber)
                {
                    mSwitchDatasetAsap = true;
                    mCurrentDatasetSelectionIndex = command
                            - mStartDatasetsIndex;
                }
                break;
        }
        return result;
    }

    public void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private class Test implements IObserver {
        @Override
        public void notify(int v) {
            String text = "Model is collected.";
            showToast(text);
        }
    }
}
