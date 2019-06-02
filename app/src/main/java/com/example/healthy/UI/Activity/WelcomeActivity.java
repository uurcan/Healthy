package com.example.healthy.UI.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.healthy.Adapter.TutorialPagerAdapter;
import com.example.healthy.R;
import com.example.healthy.Utils.PreferencesUtil;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener{

    private PreferencesUtil preferencesUtil;
    private LinearLayout dotsLayout;
    private Button btnSkip,btnNext;
    private ViewPager viewPager;
    private int[] layouts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesUtil = new PreferencesUtil(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4
        };

        addBottomDots(0);
        changeStatusBarColor();
        TutorialPagerAdapter tutorialPagerAdapter = new TutorialPagerAdapter(this,layouts);
        viewPager.setAdapter(tutorialPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerChangeListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_next:
                switchNextPage();
                break;
            case R.id.btn_skip:
                launchHomeScreen();
                break;
        }
    }
    private void switchNextPage(){
        int current = getItem();
        if (current < layouts.length){
            viewPager.setCurrentItem(current);
        }else {
            launchHomeScreen();
        }
    }
    ViewPager.OnPageChangeListener viewPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == layouts.length -1){
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            }else {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
    private void changeStatusBarColor() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length ; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0){
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        }
    }
    private int getItem(){
        return viewPager.getCurrentItem() + 1;
    }

    private void launchHomeScreen(){
        preferencesUtil.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, BMIResultsActivity.class));
        finish();
    }

}
