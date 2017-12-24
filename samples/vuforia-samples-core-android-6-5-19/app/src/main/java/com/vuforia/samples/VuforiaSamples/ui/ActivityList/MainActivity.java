package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.vuforia.samples.VuforiaSamples.R;

import java.io.File;

public class MainActivity extends Activity {
    private ProgressBarView progressBarView;
    private Button mission;
    private Button models;
    private Button run;
    private TextView logout;
    private TextView history;
    private ImageView mImage;

    private PopUtil popUtil;
    private DBHelper mdbhelper;
    private File tempFile;
    private String fileName;
    public Bitmap bitmap;

    private String name;

    private static final int PHOTO_REQUEST_CAMERA = 1; //拍摄
    private static final int PHOTO_REQUEST_GALLERY = 2; //从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3; //结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBarView = new ProgressBarView(this);
        this.setContentView(R.layout.activity_main);
        popUtil = new PopUtil(this);
        mdbhelper = new DBHelper(this);
        mImage = (ImageView)findViewById(R.id.image_icon);
        fileName = System.currentTimeMillis()+".jpg";

        mImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                popUtil.showAtLocation(MainActivity.this.findViewById(R.id.menu), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        popUtil.takePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                if(hasSdcard()){
                    tempFile = new File(Environment.getExternalStorageDirectory(),fileName);
                    Uri uri = Uri.fromFile(tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
                startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
            }
        });

        popUtil.choosePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY); //开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
            }
        });

        Intent intent = getIntent();
        if(intent != null){
            name = intent.getStringExtra("UserID");
            TextView username = findViewById(R.id.ct_username);
            Log.e("UserID" , name);
            username.setText(name);
            Cursor cursor = mdbhelper.getUserInfo(name);
            cursor.moveToFirst();
            int ageColumnIndex = cursor.getColumnIndex("Age");
            int age = cursor.getInt(ageColumnIndex);
            int genderColumnIndex = cursor.getColumnIndex("Gender");
            int value = cursor.getInt(genderColumnIndex);
            String gender = null;
            if(value == 1){
                gender = "Male";
            }
            else{
                gender = "Female";
            }
            TextView userage = findViewById(R.id.ct_age);
            String ageString = Integer.toString(age);
            userage.setText("\t"+ageString);

            TextView usergender = findViewById(R.id.ct_gender);
            usergender.setText("\t"+ gender);

            TextView userLevel = findViewById(R.id.ct_level);
            userLevel.setText("\t"+mdbhelper.getUserLevel(name));

            TextView userDis = findViewById(R.id.ct_distance);
            int Rundis = (int)mdbhelper.getUserDis(name);
            float res = (float) (Rundis * 1.0 )/1000;
            String format = String.format("%.1f km", res);
            userDis.setText(format);

            mImage.setImageBitmap(mdbhelper.getUserImage(name));

        }

        mission = (Button)findViewById(R.id.button2);
        mission.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mission.setBackgroundResource(R.drawable.task_2);
                        break;
                    case MotionEvent.ACTION_UP:
                        mission.setBackgroundResource(R.drawable.task);
                        Intent i = new Intent(MainActivity.this, MissionActivity.class);
                        i.putExtra("UserID", name);
                        startActivity(i);
                        finish();
                        break;
                }
                return true;
            }
        });
        models = (Button)findViewById(R.id.button3);
        models.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        models.setBackgroundResource(R.drawable.gift_2);
                        break;
                    case MotionEvent.ACTION_UP:
                        models.setBackgroundResource(R.drawable.gift);
                        Intent i = new Intent(MainActivity.this, FigureActivity.class);
                        i.putExtra("UserID", name);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
        run = (Button)findViewById(R.id.button1);
        run.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        run.setBackgroundResource(R.drawable.btn_radius_2);
                        break;

                    case MotionEvent.ACTION_UP:
                        run.setBackgroundResource(R.drawable.btn_radius);
                        Intent intent = new Intent(MainActivity.this, AboutScreen.class);
                        intent.putExtra("ABOUT_TEXT_TITLE", "Image Targets");
                        intent.putExtra("ACTIVITY_TO_LAUNCH",
                                "app.ImageTargets.ImageTargets");
                        intent.putExtra("ABOUT_TEXT", "ImageTargets/IT_about.html");
                        intent.putExtra("UserID", name);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        logout = (TextView)findViewById(R.id.nv_logout);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        });
        history = (TextView)findViewById(R.id.nv_history);
        history.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, historyActivity.class);
                i.putExtra("UserID", name);
                startActivity(i);
            }
        });
    }

    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 350);
        intent.putExtra("outputY", 350);

        intent.putExtra("outputFormat", "JEPG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(MainActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                bitmap = data.getParcelableExtra("data");
                //需要判断一下原来有没有图片。
                mdbhelper.updateUserImage(name, bitmap);
                mImage.setImageBitmap(bitmap);
            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        popUtil.dismiss();
    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

}
