package com.example.healthy.UI.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.healthy.Fragment.FoodFragment;
import com.example.healthy.Fragment.HomeFragment;
import com.example.healthy.Fragment.PedometerFragment;
import com.example.healthy.Fragment.ReminderFragment;
import com.example.healthy.Fragment.WaterFragment;
import com.example.healthy.R;
import com.example.healthy.Utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.ncapdevi.fragnav.FragNavController;

import static com.example.healthy.Model.Logo.mTabIconsSelected;

public class MainActivity extends BaseActivity implements
        FragNavController.RootFragmentListener {

    private Toolbar toolbar;
    private String[] TABS;
    private TabLayout bottomTabLayout;
    private FragNavController mNavController;
    private TextView toolbarTitleText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar =  findViewById(R.id.toolbar);
        toolbarTitleText = toolbar.findViewById(R.id.toolbarTitle);

        bottomTabLayout =  findViewById(R.id.bottom_tab_layout);
        TABS = getResources().getStringArray(R.array.tab_name);
        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
                .rootFragmentListener(this, TABS.length)
                .build();
        initializeToolbar();
        initializeTabs();
        switchTab(0);
        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switchTab(tab.getPosition());
            }
        });

    }

    private void initializeToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initializeTabs() {
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(getTabView(i));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public View getTabView(int position) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item_bottom, null);
        ImageView icon =  view.findViewById(R.id.tab_icon);
        icon.setImageDrawable(Utils.setDrawableSelector(MainActivity.this,
                             mTabIconsSelected[position], mTabIconsSelected[position]));
        return view;
    }

    public void switchTab(int position) {
        mNavController.switchTab(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case FragNavController.TAB1:
                return new HomeFragment();
            case FragNavController.TAB2:
                return new FoodFragment();
            case FragNavController.TAB3:
                return new WaterFragment();
            case FragNavController.TAB4:
                return new PedometerFragment();
            case FragNavController.TAB5:
                return new ReminderFragment();

        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    public void updateToolbarTitle(String title) {
        if(getSupportActionBar() != null)
            toolbarTitleText.setText(title);
    }

}