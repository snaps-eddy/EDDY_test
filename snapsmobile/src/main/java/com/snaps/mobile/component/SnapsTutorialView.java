package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;

public class SnapsTutorialView extends RelativeLayout {

	static final public String TUTORIAL1 = "tutorial10";
	static final public String TUTORIAL2 = "tutorial20";
	static final public String TUTORIAL3 = "tutorial30";

	static final public String TUTORIAL_PHOTO_PRINT = "tutorial_photo_print"; //사진 인화 튜토리얼

	String mTutorialType;
	Context mContext;
	ImageView mTutorialView;
	
	Bitmap bmTutorial;
	
	public SnapsTutorialView(Context context, String type) {
		super(context);

		mTutorialType = type;

		init(context);

	}

	void init(Context context) {
		this.mContext = context;
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = layoutInflater.inflate(R.layout.tutorialview, this);
		view.setOnTouchListener(mTouchListener);
		ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Setting.set(mContext, mTutorialType, true);
				((ViewGroup) view.getParent()).removeView(view);
			}
		});

		mTutorialView = (ImageView) view.findViewById(R.id.iv_tutorial);
	}

	OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return true;
		}
	};

	public void setTutorialImage(int resid) {
		bmTutorial = CropUtil.getInSampledDecodeBitmapFromResource(mContext.getResources(), resid);
		if(bmTutorial != null && !bmTutorial.isRecycled())
			mTutorialView.setImageBitmap(bmTutorial);
	}

	@Override
	protected void onDetachedFromWindow() {
		Setting.set(mContext, mTutorialType, true);
		super.onDetachedFromWindow();
	}
	
	public void destroy() {
		if(bmTutorial != null && !bmTutorial.isRecycled()) {
			bmTutorial.recycle();
			bmTutorial = null;
		}
	}
}
