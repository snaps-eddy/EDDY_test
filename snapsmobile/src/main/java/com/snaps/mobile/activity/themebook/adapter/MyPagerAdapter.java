//package com.snaps.mobile.activity.themebook.adapter;
//
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.snaps.mobile.activity.themebook.EditThemeBookActivity;
//
////Nothing special about this adapter, just throwing up colored views for demo
//public class MyPagerAdapter extends PagerAdapter {
//
//	EditThemeBookActivity mEditTheme;
//	LayoutInflater mInflater;
//
//	boolean mIsClick = false;
//	TextView mMiddleBtn;
//
//	LinearLayout mPopLayout;
//
//	TextView mPopTopBtn;
//	TextView mPopMiddleBtn;
//	LinearLayout mPopBottomBtn;
//
//	public MyPagerAdapter(EditThemeBookActivity activity) {
//		super();
//		this.mEditTheme = activity;
//
//		this.mInflater = LayoutInflater.from(mEditTheme);
//	}
//
//	@Override
//	public Object instantiateItem(ViewGroup container, int position) {
//
//		View v = null;
//
//		((ViewPager) container).addView(v);
//		return v;
//	}
//
//	@Override
//	public void destroyItem(ViewGroup container, int position, Object object) {
//		container.removeView((View) object);
//	}
//
//	@Override
//	public int getCount() {
//		return 3;
//	}
//
//	@Override
//	public boolean isViewFromObject(View view, Object object) {
//		return (view == object);
//	}
//}
