package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.vuforia.samples.VuforiaSamples.R;

public class PopMission extends PopupWindow {
    private View view;

    PopMission(Context context){
        this.view = LayoutInflater.from(context).inflate(R.layout.mission_content, null);
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
