package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.PopupWindow;
import android.content.Context;
import android.view.View;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.vuforia.samples.VuforiaSamples.R;

public class PopUtil extends PopupWindow {
    private View view;
    public Button takePhoto;
    public Button choosePhoto;
    public Button cancel;

    PopUtil(Context context){
        this.view = LayoutInflater.from(context).inflate(R.layout.activity_pop, null);

        takePhoto = (Button)view.findViewById(R.id.item_popupwindows_camera);
        choosePhoto = (Button)view.findViewById(R.id.item_popupwindows_Photo);
        cancel = (Button)view.findViewById(R.id.item_popupwindows_cancel);

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dismiss();
            }
        });
        this.setOutsideTouchable(true);
        this.view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e ){
                int height  = view.findViewById(R.id.pop_layout).getTop();
                int cur_height = (int)e.getY();
                if(e.getAction() == MotionEvent.ACTION_UP) {
                    Log.e("cur_height", cur_height+"");
                    Log.e("height", height+"");
                    if(cur_height < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        this.setContentView(this.view);
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        ColorDrawable background = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(background);
        this.setAnimationStyle(R.style.pop_anim);
    }
}