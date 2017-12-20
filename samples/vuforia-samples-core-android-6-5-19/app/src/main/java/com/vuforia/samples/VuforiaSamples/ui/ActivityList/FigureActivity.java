package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vuforia.samples.VuforiaSamples.R;

public class FigureActivity extends AppCompatActivity{
    private TabLayout tb_layout;
    private ViewPager viewPager;
    private TabAdaper tabAdaper;
    private String[] titles;
    private ActionBar actionBar;
    private TextView tbGotNumber;
    private TextView tbImage;
    private String name;
    private DBHelper mdbhelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figure);
        mdbhelper = new DBHelper(this);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbImage = (TextView) findViewById(R.id.tb_img);
        titles = getResources().getStringArray(R.array.tab_title);
        tabAdaper = new TabAdaper(getSupportFragmentManager(), titles);
        tb_layout = (TabLayout)findViewById(R.id.tb_layout);
        tbGotNumber = (TextView) findViewById(R.id.tb_count);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(tabAdaper);

        Intent intent = getIntent();
        if(intent != null) {
            name = intent.getStringExtra("UserID");
            Log.e("UserID" , name);
        }

        tb_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        tbImage.setBackgroundResource(R.drawable.mad);
                        tbGotNumber.setText("Got " + String.valueOf(mdbhelper.GetModelData(name, 0)));
                        break;
                    case 1:
                        tbImage.setBackgroundResource(R.drawable.happy);
                        tbGotNumber.setText("Got " + String.valueOf(mdbhelper.GetModelData(name, 1)));
                        break;
                    case 2:
                        tbImage.setBackgroundResource(R.drawable.surprise);
                        tbGotNumber.setText("Got " + String.valueOf(mdbhelper.GetModelData(name, 2)));
                        break;
                    case 3:
                        tbImage.setBackgroundResource(R.drawable.neutral);
                        tbGotNumber.setText("Got " + String.valueOf(mdbhelper.GetModelData(name, 3)));
                        break;
                    case 4:
                        tbImage.setBackgroundResource(R.drawable.sad);
                        tbGotNumber.setText("Got " + String.valueOf(mdbhelper.GetModelData(name, 4)));
                        break;
                    case 5:
                        tbImage.setBackgroundResource(R.drawable.laugh);
                        tbGotNumber.setText("Got " + String.valueOf(mdbhelper.GetModelData(name, 5)));
                        break;
                    case 6:
                        tbImage.setBackgroundResource(R.drawable.surprise);
                        tbGotNumber.setText("Got " + String.valueOf(mdbhelper.GetModelData(name, 6)));
                }
                /*select.setOnTouchListener(new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event){
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                select.setBackgroundResource(R.drawable.start_radius_2);
                                break;
                            case MotionEvent.ACTION_UP:
                                select.setBackgroundResource(R.drawable.start_radius);
                                Intent i = new Intent(FigureActivity.this, MainActivity.class);
                                Log.e("Tab Pos ", tab.getPosition()+"");
                                startActivity(i);
                                finish();
                                break;
                        }
                        return true;
                    }
                });*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tb_layout.setupWithViewPager(viewPager);
        tb_layout.setTabMode(TabLayout.MODE_SCROLLABLE);
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
