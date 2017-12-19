package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.vuforia.samples.VuforiaSamples.R;

public class TabFragment extends Fragment {
    private int page;
    public static final String PAGE_POSITION = "page_position";
    private String[] names;
    private TextView tv_fragment;

    public static TabFragment getInstance(int position) {
        TabFragment tabPageFragment = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PAGE_POSITION, position);
        tabPageFragment.setArguments(bundle);
        return tabPageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = this.getArguments().getInt(PAGE_POSITION);
        names = getResources().getStringArray(R.array.tab_title);
        System.out.println(page);
    }
}
