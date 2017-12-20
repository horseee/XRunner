package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;

import com.vuforia.samples.VuforiaSamples.R;

public class MissionActivity extends AppCompatActivity {
    //定义四个数组，分别做显示用
    private MyAdapter myAdapter;
    private List<HashMap<String, Object>> items;
    private ListView listView;
    private Context context;
    private ActionBar actionBar;

    private List<String> MissionList = null;
    private DBHelper mdbhelper;
    private String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_mission);

        Intent intent = getIntent();
        if(intent != null) {
            name = intent.getStringExtra("UserID");
        }

        mdbhelper = new DBHelper(this);
        MissionList = new ArrayList<>();
        MissionList.add("This is an easy mission. You can exchange 30 experience using only 1 Emerald. But the diamond seems to lose its value.");
        MissionList.add("This is also an easy mission. You can exchange 70 experience using 2 Marbles. Better than the first one.");
        MissionList.add("This mission takes you some time. You can exchange 130 experience using 1 Malachite and 3 Demantoids. ");
        MissionList.add("In this mission, you can exchange 150 experience using 2 Agates and 2 Ambers. Emmmmmm. Not so good.");
        MissionList.add("In this mission, you can exchange 200 experience using 2 Marbles and 3 Kyanites. Maybe it's worth trying.");
        MissionList.add("This mission will take 3 Emeralds and 2 Agates and 1 Kyanite. In turn you will get 250 experience. ");
        MissionList.add("This mission needs 1 Marble and 3 Malachites and 3 Ambers and give you 280 experience back.  ");
        MissionList.add("4 Malachites and 4 Demantoids for 350 experience. Since that you have run for a looooong distance. ");
        MissionList.add("This time you need to give 3 Emerald and 6 Malachites and 1 Amber(totally 10 diamonds) to exchange for 500 exp.");
        MissionList.add("This is an difficult mission.This mission needs 12 Agates and you will get 700 experience!!");
        MissionList.add("WOW! The most difficult and valuable mission. Using 7 Marbles and 7 Demantoids and you will get 800 experience!!!");

        myAdapter = new MyAdapter(this);
        listView = (ListView)this.findViewById(R.id.item_list);
        items = new ArrayList<HashMap<String, Object>>();
        for (int i = 1; i < 12; i++) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("seq", "");
            hashMap.put("name", "Task " + i);
            items.add(hashMap);
        }
        listView.setAdapter(myAdapter);
        setListViewHeightBasedOnChildren(listView);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            itemHolder holder = null;
            int[] ModelExp = {30, 70, 130, 150, 200, 250, 280, 350, 500, 700, 800};
            if (convertView == null) {
                holder = new itemHolder();
                convertView = mInflater.inflate(R.layout.item_list, null);
                holder.title = (TextView)convertView.findViewById(R.id.item_name);
                holder.seq = (TextView)convertView.findViewById(R.id.item_seq);
                holder.item_content = (LinearLayout)convertView.findViewById(R.id.item_content);
                convertView.setTag(holder);
            }else {
                holder = (itemHolder)convertView.getTag();
            }
            holder.title.setText((String)items.get(position).get("name"));
            Log.e("Postion", "" + position);
            holder.seq.setText(ModelExp[position] + " exp");
            holder.item_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInfo(position);
                }
            });
            return convertView;
        }
    }

    public final class itemHolder{
        public TextView title;
        public TextView seq;
        public LinearLayout item_content;
    }

    public void showInfo(final int position){
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.mydialog).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.mission_content);
        TextView title = (TextView)window.findViewById(R.id.mission_title);
        final Button accept = (Button)window.findViewById(R.id.mission_accept);
        Button back = (Button)window.findViewById(R.id.mission_back);
        TextView content = (TextView)window.findViewById(R.id.mission_taskcontent);
        title.setText("Task"+" "+(position+1));
        content.setText(MissionList.get(position));
        final int[] model = new int[7];

        Cursor cursor = mdbhelper.getUserModelInf(name);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int index = cursor.getInt(cursor.getColumnIndex("ModelID"));
                int count = cursor.getInt(cursor.getColumnIndex("ModelCount"));
                model[index] = count;
            }
        }
        else {
            Log.e("database ","query error");
        }

        switch (position) {
            case 0:
                if (model[0] >= 1)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 1:
                if (model[1] >= 2)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 2:
                if (model[3] >= 1 && model[5] >= 3)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 3:
                if (model[2] >= 2 && model[6] >=2)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 4:
                if (model[1] >= 2 && model[4] >= 3)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 5:
                if (model[0] >= 3 && model[2] >= 2 && model[4] >= 1)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 6:
                if (model[1] >= 1 && model[3] >= 3 && model[6] >= 3)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 7:
                if (model[3] >= 4 && model[5] >= 4)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 8:
                if (model[0] >= 3 && model[3] >= 6 && model[6] >= 1)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 9:
                if (model[2] >= 12)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;
            case 10:
                if (model[1] >= 7 && model[5] >= 7)
                    accept.setEnabled(true);
                else
                    accept.setEnabled(false);
                break;

            default:
                accept.setEnabled(false);

        }
        window.setGravity(Gravity.CENTER);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                alertDialog.dismiss();
            }
        });

        accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                switch (position) {

                    case 0:
                        model[0] -= 1;
                        mdbhelper.updateModelData(name, 0, false, 1);
                        mdbhelper.updateExpandLevel(name, 0);
                        if (model[0] >= 1)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 1:
                        model[1] -= 2;
                        mdbhelper.updateModelData(name, 1, false, 2);
                        mdbhelper.updateExpandLevel(name, 1);
                        if (model[1] >= 2)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 2:
                        model[3] -= 1;
                        model[5] -= 3;
                        mdbhelper.updateModelData(name, 3, false, 1);
                        mdbhelper.updateModelData(name, 5, false, 3);
                        mdbhelper.updateExpandLevel(name, 2);
                        if (model[3] >= 1 && model[5] >= 3)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 3:
                        model[2] -= 2;
                        model[6] -= 2;
                        mdbhelper.updateModelData(name, 2, false, 2);
                        mdbhelper.updateModelData(name, 6, false, 2);
                        mdbhelper.updateExpandLevel(name, 3);
                        if (model[2] >= 2 && model[6] >= 2)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 4:
                        model[1] -= 2;
                        model[4] -= 3;
                        mdbhelper.updateModelData(name, 1, false, 2);
                        mdbhelper.updateModelData(name, 4, false, 3);
                        mdbhelper.updateExpandLevel(name, 4);
                        if (model[1] >= 2 && model[4] >= 3)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 5:
                        model[0] -= 3;
                        model[2] -= 2;
                        model[4] -= 1;
                        mdbhelper.updateModelData(name, 4, false, 1);
                        mdbhelper.updateModelData(name, 0, false, 3);
                        mdbhelper.updateModelData(name, 2, false, 2);
                        mdbhelper.updateExpandLevel(name, 5);
                        if (model[0] >= 3 && model[2] >= 2 && model[4] >= 1)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 6:
                        model[1] -= 1;
                        model[3] -= 3;
                        model[6] -= 3;
                        mdbhelper.updateModelData(name, 1, false, 1);
                        mdbhelper.updateModelData(name, 3, false, 3);
                        mdbhelper.updateModelData(name, 6, false, 3);
                        mdbhelper.updateExpandLevel(name, 6);
                        if (model[1] >= 1 && model[3] >= 3 && model[6] >= 3)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 7:

                        model[3] -= 4;
                        model[5] -= 4;
                        mdbhelper.updateModelData(name, 3, false, 4);
                        mdbhelper.updateModelData(name, 5, false, 4);
                        mdbhelper.updateExpandLevel(name, 7);
                        if (model[3] >= 4 && model[5] >= 4)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 8:
                        model[0] -= 3;
                        model[3] -= 6;
                        model[6] -= 1;
                        mdbhelper.updateModelData(name, 0, false, 3);
                        mdbhelper.updateModelData(name, 3, false, 6);
                        mdbhelper.updateModelData(name, 6, false, 1);
                        mdbhelper.updateExpandLevel(name, 8);
                        if (model[0] >= 3 && model[3] >= 6 && model[6] >= 1)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 9:
                        model[2] -= 12;
                        mdbhelper.updateModelData(name, 2, false, 12);
                        mdbhelper.updateExpandLevel(name, 9);
                        if (model[2] >= 12)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                    case 10:
                        model[1] -= 7;
                        model[5] -= 7;
                        mdbhelper.updateModelData(name, 1, false, 7);
                        mdbhelper.updateModelData(name, 5, false, 7);
                        mdbhelper.updateExpandLevel(name, 10);
                        if (model[1] >= 7 && model[5] >= 7)
                            accept.setEnabled(true);
                        else
                            accept.setEnabled(false);
                        break;
                }
            }
        });
    }
}