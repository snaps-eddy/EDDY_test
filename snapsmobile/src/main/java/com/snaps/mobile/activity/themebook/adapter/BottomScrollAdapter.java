//package com.snaps.mobile.activity.themebook.adapter;
//
//import com.snaps.mobile.activity.themebook.EditThemeBookActivity;
//
//import android.graphics.Color;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//public class BottomScrollAdapter extends BaseAdapter {
//
//	EditThemeBookActivity mEditTheme;
//
//	public BottomScrollAdapter(EditThemeBookActivity activity) {
//		this.mEditTheme = activity;
//	}
//
//	@Override
//	public int getCount() {
//		// TODO Auto-generated method stub
//		return 60;
//	}
//
//	@Override
//	public Object getItem(int position) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		TextView view;
//		if(convertView == null)
//		{
//			view = new TextView(mEditTheme.getApplicationContext());
//
//		}else
//		{
//			view = (TextView)convertView;
//		}
//
//		view.setText("Item " + (position + 1));
//		view.setTextColor(Color.WHITE);
//		view.setGravity(Gravity.CENTER);
//		view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50));
//
//
//
//		return view;
//	}
//
//}
