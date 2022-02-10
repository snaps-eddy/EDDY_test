package com.snaps.mobile.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;

public class SnapsTutorialView2 extends RelativeLayout {
	static final public String TUTORIAL1 = "tutorial10";
	Context mContext;
	public SnapsTutorialView2(Context context) {
		super(context);
		init(context);

	}

	void init(Context context) {
		this.mContext = context;
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = layoutInflater.inflate(R.layout.tutorialview2, this);
		view.setOnTouchListener(mTouchListener);
		ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);

		// 화면 닫기..
		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Setting.set(mContext, TUTORIAL1, true);
				((ViewGroup) view.getParent()).removeView(view);
			}
		});
	}

	OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return true;
		}
	};

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Setting.set(mContext, TUTORIAL1, true);
	}

}
