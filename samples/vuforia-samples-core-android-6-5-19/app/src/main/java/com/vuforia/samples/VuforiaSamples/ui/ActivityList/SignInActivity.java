package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.vuforia.samples.VuforiaSamples.R;

public class SignInActivity extends Activity {

    private EditText username = null;
    private EditText password = null;
    private Button signin = null;
    private Button signup = null;
    private DBHelper mdbhelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
        mdbhelper = new DBHelper(this);
        findViewById(R.id.routeview).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        //mdbhelper.DropUserLevel();
        //mdbhelper.getUserLevel((username.getText()).toString());
        signin = (Button) findViewById(R.id.button1);
        signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String name = username.getText().toString();
                String key = password.getText().toString();
                boolean IsCorrect = mdbhelper.IsPasswordCorrect(name, key);
                Log.e("result ", "+" + IsCorrect);
                if (IsCorrect) {
                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                    i.putExtra("UserID", name);
                    startActivity(i);
                    finish();
                }
                else
                    sendMessage();
            }
        });
        signup = (Button) findViewById(R.id.button2);
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    public void sendMessage() {
        Toast toast = Toast.makeText(SignInActivity.this, "Sign in failed.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
