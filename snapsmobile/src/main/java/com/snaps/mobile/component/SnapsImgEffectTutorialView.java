package com.snaps.mobile.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.UIUtil;


public class SnapsImgEffectTutorialView extends RelativeLayout {
	static final public String TUTORIAL_IMG_EFFECT = "tutorial_img_effect";
	Activity mActivity;
	public SnapsImgEffectTutorialView(Activity context) {
		super(context);
		init(context);
	}

	void init(Activity act) {
//		this.mActivity = act;
//		LayoutInflater layoutInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		final View view = layoutInflater.inflate(R.layout.tutorial_img_effect_view, this);
//		view.setOnTouchListener(mTouchListener);
//
//		boolean isLandscape = UIUtil.fixCurrentOrientationAndReturnBoolLandScape(act);
//
//		ImageView ivClose = (ImageView) view.findViewById(R.id.img_effect_tutorial_center);
//
//		try {
//			if(isLandscape)
//				ivClose.setBackgroundResource(R.drawable.tutorial_image_edit_horizontal);
//			else
//				ivClose.setBackgroundResource(R.drawable.tutorial_image_edit_verical);
//		} catch (OutOfMemoryError e) {
//			this.setVisibility(View.GONE);
//		}
//
//		// 화면 닫기..
//		ivClose.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Setting.set(mActivity, TUTORIAL_IMG_EFFECT, true);
//				((ViewGroup) view.getParent()).removeView(view);
//			}
//		});
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
		Setting.set(mActivity, TUTORIAL_IMG_EFFECT, true);
	}

}
