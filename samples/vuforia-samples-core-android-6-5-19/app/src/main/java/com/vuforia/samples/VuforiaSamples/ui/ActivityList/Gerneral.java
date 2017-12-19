package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.graphics.Bitmap;

/**
 * Created by horseee on 2017/12/13.
 */

public class Gerneral {
    private String RunningTime;
    private String RunningDis;
    private String RunningDuration;
    private String RunningCalor;
    private Bitmap RunningPath;


    public Gerneral(String a, String b, String c, String d, Bitmap e) {
        RunningTime = a;
        RunningDis = b;
        RunningDuration = c;
        RunningCalor = d;
        RunningPath = e;
    }

    public String getRunningTime() {return RunningTime;}
    public String getRunningDis() {return RunningDis;};
    public String getRunningDuration() {return RunningDuration;}
    public String getRunningCalor() {return RunningCalor;}
    public Bitmap getRunningPath() {return RunningPath;}
}
