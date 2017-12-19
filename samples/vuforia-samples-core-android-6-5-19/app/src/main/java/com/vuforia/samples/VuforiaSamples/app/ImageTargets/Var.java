package com.vuforia.samples.VuforiaSamples.app.ImageTargets;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

interface IObserver{
    public void notify(int v);
}

public class Var {
    public int var;
    private List<IObserver> obs= new ArrayList<IObserver>();
    public void registerObserver(IObserver ob){
        obs.add(ob);
    }
    public void setVar(int var) {
        this.var = var;
        for (IObserver ob : obs) {
            if (this.var == 1){
                try{
                    obs.notify();
                    Log.e("TEXTURE", "model collected");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TEXTURE", "model uncollected");
                }
            }
        }
    }
    public int getVar(){
        return var;
    }
}

