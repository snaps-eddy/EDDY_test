package com.snaps.mobile.activity.themebook.design_list.Interface;

import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.AbsListView;

import com.kmshack.newsstand.ScrollTabHolder;
import com.snaps.mobile.activity.themebook.design_list.adapter.BaseThemeDesignListAdapter;

import java.util.List;

/**
 * Created by kimduckwon on 2017. 11. 29..
 */

public abstract class ThemeDesignListAPI implements View.OnClickListener,ViewPager.OnPageChangeListener, ScrollTabHolder {

    public abstract void onCreate();
    public abstract void onResume();
    public abstract void onPause();
    public abstract void onDestroy();

    public abstract void getIntent();
    public abstract void setTitleText();
    public abstract void performBackButton();
    public abstract void performNextButton();
    public abstract void loadDesignTemplate();
    public abstract int getLimitViewCount();
    public abstract boolean isSuccessLoadDesignList();
    public abstract void setLayoutState();
    public abstract boolean isSinglePage();
    public abstract boolean isMultiPage();
    public abstract void setSinglePageLayout();
    public abstract void setMultiPageLayout();
    public abstract void loadDesignList();
    public abstract void setSingleDesignDataList();
    public abstract void setMultiDesignDataList();
    public abstract List getSelectData() ;
    public abstract  BaseThemeDesignListAdapter.DesignListAdapterAttribute  getAttribute(int position);

    @Override
    public abstract void onClick(View view);
    @Override
    public abstract void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
    @Override
    public abstract void onPageSelected(int position);
    @Override
    public abstract void onPageScrollStateChanged(int state);
    @Override
    public abstract void adjustScroll(int scrollHeight);
    @Override
    public abstract void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);
}
