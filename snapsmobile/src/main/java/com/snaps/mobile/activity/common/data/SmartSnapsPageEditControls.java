package com.snaps.mobile.activity.common.data;

import android.app.Activity;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.snaps.mobile.activity.themebook.adapter.SmartRecommendBookDetailEditPagerAdapter;

public class SmartSnapsPageEditControls {
    private Activity activity;
    private ViewPager viewPager;
    private SmartRecommendBookDetailEditPagerAdapter pagerAdapter;
    private View titleLayout;

    public static SmartSnapsPageEditControls createInstanceWithActivity(Activity activity) {
        return new SmartSnapsPageEditControls(activity);
    }

    private SmartSnapsPageEditControls(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public SmartRecommendBookDetailEditPagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public void setPagerAdapter(SmartRecommendBookDetailEditPagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }

    public View getTitleLayout() {
        return titleLayout;
    }

    public void setTitleLayout(View titleLayout) {
        this.titleLayout = titleLayout;
    }
}
