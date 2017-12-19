package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdaper extends FragmentPagerAdapter {
    private String[] names;

    public TabAdaper(FragmentManager fm, String[] names) {
        super(fm);
        this.names = names;
    }

    @Override
    public Fragment getItem(int position) {
        return TabFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return names[position];
    }
}