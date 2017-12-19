package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.net.Uri;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;

import com.vuforia.samples.VuforiaSamples.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignUpActivity extends Activity {
    private ImageView mImage;
    private Button createA;
    private EditText username;
    private EditText password;
    private EditText confirm;
    private RadioGroup gender;
    private EditText age;
    private PopUtil popUtil;
    private File tempFile;
    private String fileName;
    public Bitmap bitmap;

    private int gender_select = 1;
    private static final int PHOTO_REQUEST_CAMERA = 1; //拍摄
    private static final int PHOTO_REQUEST_GALLERY = 2; //从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3; //结果

    public DBHelper mdahelper;
    public String TAG = "Info ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        createA = (Button)findViewById(R.id.button1);
        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
        confirm = (EditText)findViewById(R.id.editText3);
        age = (EditText)findViewById(R.id.editText4);
        mImage = (ImageView)findViewById(R.id.personal_icon);
        gender = (RadioGroup)findViewById(R.id.gender);
        mdahelper = new DBHelper(this);
        popUtil = new PopUtil(this);
        fileName = System.currentTimeMillis()+".jpg";

        findViewById(R.id.rootview).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        mImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                popUtil.showAtLocation(SignUpActivity.this.findViewById(R.id.rootview), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
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

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId){
                switch(checkedId){
                    case R.id.male:
                        gender_select = 1;
                        break;
                    case R.id.female:
                        gender_select = 2;
                        break;
                }
            }
        });

        createA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String name = username.getText().toString();
                String key = password.getText().toString();
                String KeyAgain = confirm.getText().toString();
                String Age = age.getText().toString();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(KeyAgain) && !TextUtils.isEmpty(Age) && !TextUtils.isEmpty(key)){
                    int age = Integer.parseInt(Age);
                    if(mdahelper.canUserInsert(name)){
                        if (key.equals(KeyAgain)) {
                            Log.e(TAG, "UserName:" + name);
                            Log.e(TAG, "Password:" +key);
                            Log.e(TAG, "age: "+ Age);
                            mdahelper.insertUser(name, key, gender_select, age);

                            Bitmap bm = ((BitmapDrawable)mImage.getDrawable()).getBitmap();
                            mdahelper.insertUserImage(name, bm);
                            mdahelper.initUserModel(name);

                            Log.e("level", ""+mdahelper.getUserLevel(name));
                            Log.e("dis", ""+mdahelper.getUserDis(name));
                            Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            String warning = "Please confirm your password.";
                            sendMessage(warning);
                        }
                    }
                    else{
                        String warning = "Username has existed.";
                        sendMessage(warning);
                    }
                }
                else{
                    String warning = "Columns cannot be empty.";
                    sendMessage(warning);
                }
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
                Toast.makeText(SignUpActivity .this, "No SDCard.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                bitmap = data.getParcelableExtra("data");
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

    protected void sendMessage(String warning){
        Toast toast = Toast.makeText(SignUpActivity.this, warning + " Sign up failed.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
