package com.snaps.mobile.activity.diary.customview;

import android.app.Activity;
import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryTutorialListener;

import java.util.ArrayList;
import java.util.List;

public class SnapsDiaryTutorialView extends RelativeLayout {
	private final int[] TUTORIAL_IMG_RESOURCE_ID_ARR = new int[] {
			R.drawable.img_diary_tutorial_01,
			R.drawable.img_diary_tutorial_02,
			R.drawable.img_diary_tutorial_03,
			R.drawable.img_diary_tutorial_04
	};
	private final int[] TUTORIAL_DESC_TEXT_RES_ARR = new int[] {
			R.string.diary_tutorial_desc_01, R.string.diary_tutorial_desc_02, R.string.diary_tutorial_desc_03, R.string.diary_tutorial_desc_04
	};

	private Activity mActivity;
	private ISnapsDiaryTutorialListener listener;
	private List<ImageView> mIndicators = null;
	private TextView mDescTextView = null;
	private LinearLayout indicatorLayout;
	private LinearLayout btnLayout;

	public SnapsDiaryTutorialView(Activity context, ISnapsDiaryTutorialListener listener) {
		super(context);
		this.listener = listener;
		init(context);
	}

	private void init(Activity act) {
		this.mActivity = act;
		LayoutInflater layoutInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = layoutInflater.inflate(R.layout.tutorial_img_diary_view, this);

		indicatorLayout = (LinearLayout) view.findViewById(R.id.tutorial_for_diary_indicator_layout);
		btnLayout = (LinearLayout) view.findViewById(R.id.tutorial_img_diary_view_pager_item_btn_ly);

		Button btnLeft = (Button) view.findViewById(R.id.tutorial_img_diary_view_pager_item_left_btn);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClosedTutorialView(ISnapsDiaryTutorialListener.SNAPS_DIARY_TUTORIAL_BTN_01);
			}
		});

		Button btnRight= (Button) view.findViewById(R.id.tutorial_img_diary_view_pager_item_right_btn);
		btnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClosedTutorialView(ISnapsDiaryTutorialListener.SNAPS_DIARY_TUTORIAL_BTN_02);
			}
		});

		findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClosedTutorialView(ISnapsDiaryTutorialListener.SNAPS_DIARY_CLOSE);
			}
		});
		findViewById(R.id.ThemeTitleLeft).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClosedTutorialView(ISnapsDiaryTutorialListener.SNAPS_DIARY_CLOSE);
			}
		});
		findViewById(R.id.btnTitleClose).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClosedTutorialView(ISnapsDiaryTutorialListener.SNAPS_DIARY_CLOSE);
			}
		});

		mDescTextView = (TextView) view.findViewById(R.id.tutorial_for_diary_desc);

		final ViewPager viewPager = (ViewPager) view.findViewById(R.id.tutorial_img_diary_view_pager);
		SnapsDiaryTutorialImageAdapter adapter = new SnapsDiaryTutorialImageAdapter(act);
		viewPager.setAdapter(adapter);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageSelected(int position) {
				setBottomUI(position);
			}
		});

		LinearLayout lyBottom = (LinearLayout) view.findViewById(R.id.tutorial_img_diary_view_bottom_layout);
		lyBottom.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				viewPager.onTouchEvent(event);
				return true;
			}
		});

		createIndicator(indicatorLayout);
		setBottomUI(0);
	}

	private void setBottomUI(int position) {
		setDescText(position);
		if (position >= TUTORIAL_DESC_TEXT_RES_ARR.length - 1) {
			indicatorLayout.setVisibility(View.GONE);
			btnLayout.setVisibility(View.VISIBLE);
		} else {
			indicatorLayout.setVisibility(View.VISIBLE);
			btnLayout.setVisibility(View.GONE);
			setIndicator(position);
		}
	}

	private void setDescText(int position) {
		if (TUTORIAL_DESC_TEXT_RES_ARR.length <= position || mDescTextView == null) return;
		mDescTextView.setText(TUTORIAL_DESC_TEXT_RES_ARR[position]);
	}

	private void setIndicator(int position) {
		if (mIndicators == null || mIndicators.size() <= position) return;

		for (int ii = 0; ii < mIndicators.size(); ii++) {
			ImageView imageView = mIndicators.get(ii);
			if (imageView == null) return;

			if (imageView.getDrawable() != null) {
				imageView.getDrawable().setCallback(null);
				imageView.setImageBitmap(null);
			}

			imageView.setImageResource(position == ii ? R.drawable.img_diary_tutorial_dot_focus : R.drawable.img_diary_tutorial_dot); //FIXME
		}
	}

	private void createIndicator(LinearLayout layout) {
		mIndicators = new ArrayList<>();
		for (int ii = 0; ii < TUTORIAL_IMG_RESOURCE_ID_ARR.length; ii++) {
			ImageView indicator = new ImageView(mActivity);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			indicator.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			layoutParams.leftMargin = ii > 0 ? UIUtil.convertDPtoPX(mActivity, 8) : 0;
			indicator.setLayoutParams(layoutParams);

			layout.addView(indicator);
			mIndicators.add(indicator);
		}
	}

	public class SnapsDiaryTutorialImageAdapter extends PagerAdapter {
		Context context;

		SnapsDiaryTutorialImageAdapter(Context context){
			this.context=context;
		}
		@Override
		public int getCount() {
			return TUTORIAL_IMG_RESOURCE_ID_ARR.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View view = layoutInflater.inflate(R.layout.tutorial_img_diary_view_pager_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.tutorial_img_diary_view_pager_item_iv);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			ImageLoader.with(getContext()).load(TUTORIAL_IMG_RESOURCE_ID_ARR[position]).placeholder(R.drawable.color_drawable_eeeeee).into(imageView);

			((ViewPager) container).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}
}
