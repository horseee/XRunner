package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vuforia.samples.VuforiaSamples.R;

import java.util.List;

/**
 * Created by horseee on 2017/12/13.
 */

public class GeneralAdapter extends BaseAdapter{
    private LayoutInflater mLayoutInflater;
    private Context mcontext;
    private List<Gerneral> generals;

    public GeneralAdapter(Context context, List<Gerneral> list){
        mcontext = context;
        mLayoutInflater = LayoutInflater.from(context);
        generals = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return generals.size();
    }


    @Override
    public Gerneral getItem(int position) {
        // TODO Auto-generated method stub
        return generals.get(position);
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    //简单来说就是拿到单行的一个布局，然后根据不同的数值，填充主要的listView的每一个item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Log.e("count", getCount() + "");
        Log.e("position", position + "");
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.history_list, null);

            //viewHolder.mpathimage = (ImageView) convertView.findViewById(R.id.history_img);
            viewHolder.mdate = (TextView) convertView.findViewById(R.id.history_date);
            viewHolder.mdis = (TextView) convertView.findViewById(R.id.history_dist);
            viewHolder.mdur = (TextView) convertView.findViewById(R.id.history_time);
            viewHolder.mcal = (TextView) convertView.findViewById(R.id.history_calorie);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Gerneral general =  generals.get(position);
        viewHolder.mpathimage.setImageBitmap(general.getRunningPath());
        viewHolder.mdate.setText(general.getRunningTime());
        viewHolder.mdis.setText(general.getRunningDis());
        viewHolder.mdur.setText(general.getRunningDuration());
        viewHolder.mcal.setText(general.getRunningCalor());

        return convertView;

    }

    private static class ViewHolder{
        public TextView mdate;
        public TextView mdis;
        public TextView mdur;
        public TextView mcal;
        public ImageView mpathimage;
    }

}