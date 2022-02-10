package com.snaps.mobile.activity.themebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.animation.SnapsAnimationHandler;
import com.snaps.common.utils.animation.frame_animation.SnapsFrameAnimationResFactory;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import errorhandle.CatchActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FTextView;

import static com.snaps.mobile.activity.themebook.SmartSnapsTypeSelectActivity.eSmartSnapsTypeSelectTutorial.TYPE_AUTO_ROTATION;
import static com.snaps.mobile.activity.themebook.SmartSnapsTypeSelectActivity.eSmartSnapsTypeSelectTutorial.TYPE_CENTER_FACE;
import static com.snaps.mobile.activity.themebook.SmartSnapsTypeSelectActivity.eSmartSnapsTypeSelectTutorial.TYPE_SORT_BY_DATE;

public class SmartSnapsTypeSelectActivity extends CatchActivity implements View.OnClickListener, GoHomeOpserver.OnGoHomeOpserver {
	private static final String TAG = SmartSnapsTypeSelectActivity.class.getSimpleName();
	private static final long TIME_OF_AUTO_SWIPE = 2000;

	enum eSmartSnapsTypeSelectTutorial {
		TYPE_CENTER_FACE,
		TYPE_SORT_BY_DATE,
		TYPE_AUTO_ROTATION
	}

	private List<ImageView> indicators = null;

	private ConvenientBanner convenientBanner = null;

	private Set<AnimationView> centerFaceAnimationsSet = new HashSet<>();
	private Set<AnimationView> sortByDateAnimationsSet = new HashSet<>();
	private Set<AnimationView> autoRotationAnimationsSet = new HashSet<>();
	private boolean isFirstAnimationFinished = false;

	private int viewPagerPosition = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
		
		setContentView(R.layout.smart_snaps_type_select_activity);

		TextView titleText = (TextView) findViewById(R.id.ThemeTitleText);
		titleText.setText(R.string.smart_snaps_tutorial_title);

		findViewById(R.id.smart_snaps_type_select_activity_smart_choice).setOnClickListener(this);

		font.FTextView normalChoiceBtn = (FTextView) findViewById(R.id.smart_snaps_type_select_activity_normal_choice);
		normalChoiceBtn.setOnClickListener(this);
		SpannableString content = new SpannableString(normalChoiceBtn.getText().toString());
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		normalChoiceBtn.setText(content);

		LinearLayout indicatorLayout = (LinearLayout) findViewById(R.id.smart_snaps_type_select_activity_viewpager_indicator_layout);
		createIndicator(indicatorLayout);

		List<eSmartSnapsTypeSelectTutorial> tutorialTypeList = new LinkedList<>();
		for (eSmartSnapsTypeSelectTutorial type : eSmartSnapsTypeSelectTutorial.values())
			tutorialTypeList.add(type);

		convenientBanner = (ConvenientBanner) findViewById(R.id.smart_snaps_type_select_activity_viewpager);
		convenientBanner.setPageIndicator(null);
		convenientBanner.setPages(
				new CBViewHolderCreator<SmartSnapsTypeSelectTutorialPagerAdapter>() {
					@Override
					public SmartSnapsTypeSelectTutorialPagerAdapter createHolder() {
						return new SmartSnapsTypeSelectTutorialPagerAdapter();
					}
				}, tutorialTypeList);

		convenientBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageSelected(final int position) {
				viewPagerPosition = position;
				handleOnPageChanged(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {}
		});

		ViewPager innerViewPager = convenientBanner.getViewPager();
		innerViewPager.setClipToPadding(false);
		innerViewPager.setPadding(UIUtil.convertDPtoPX(this, 45), 0, UIUtil.convertDPtoPX(this, 45), 0);
		innerViewPager.setPageMargin(UIUtil.convertDPtoPX(this, 22));

		try {
			setIndicator(0);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		GoHomeOpserver.addGoHomeListener(this);
	}

	private void handleOnPageChanged(final int position) {
		try {
			setIndicator(position);

			startTutorialAnimation(position);

			convenientBanner.postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						releaseOtherPageAnimation(position);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}
			}, 100);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void startAnimationsSet(Set<AnimationView> animationViewSet, eSmartSnapsTypeSelectTutorial tutorialType) {
		if (animationViewSet != null) {
			for (AnimationView animationView : animationViewSet) {
				ImageView imageView = animationView.getImageView();
				if (imageView == null || imageView.getVisibility() != View.VISIBLE) continue;
				startAnimation(imageView, tutorialType);
			}
		}
	}

	private void startAnimation(ImageView imageView, eSmartSnapsTypeSelectTutorial tutorialType) {
		switch (tutorialType) {
			case TYPE_CENTER_FACE:
				SnapsAnimationHandler.startFrameAnimation(this, imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.SMART_SNAPS_TUTORIAL_CENTER_FACE);
				break;
			case TYPE_SORT_BY_DATE:
				SnapsAnimationHandler.startFrameAnimation(this, imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.SMART_SNAPS_TUTORIAL_DATE);
				break;
			case TYPE_AUTO_ROTATION:
				SnapsAnimationHandler.startFrameAnimation(this, imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.SMART_SNAPS_TUTORIAL_ROTATION);
				break;
		}
	}

	private void startTutorialAnimation(int position) throws Exception {
		switch (position) {
			case 0:
				startAnimationsSet(centerFaceAnimationsSet, TYPE_CENTER_FACE);
				break;
			case 1:
				startAnimationsSet(sortByDateAnimationsSet, TYPE_SORT_BY_DATE);
				break;
			case 2:
				startAnimationsSet(autoRotationAnimationsSet, TYPE_AUTO_ROTATION);
				break;
		}
	}

	private void releaseOtherPageAnimation(int position) throws Exception {
		switch (position) {
			case 0:
				recycleDrawables(sortByDateAnimationsSet);
				recycleDrawables(autoRotationAnimationsSet);
				break;
			case 1:
				recycleDrawables(centerFaceAnimationsSet);
				recycleDrawables(autoRotationAnimationsSet);
				break;
			case 2:
				recycleDrawables(sortByDateAnimationsSet);
				recycleDrawables(centerFaceAnimationsSet);
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (convenientBanner != null)
			convenientBanner.startTurning(TIME_OF_AUTO_SWIPE);

		try {
			if (viewPagerPosition >= 0) {
				handleOnPageChanged(viewPagerPosition);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (convenientBanner != null)
			convenientBanner.stopTurning();

		releaseAllAnimations();
	}

	private void releaseAllAnimations() {
		recycleDrawables(centerFaceAnimationsSet);
		recycleDrawables(sortByDateAnimationsSet);
		recycleDrawables(autoRotationAnimationsSet);
	}

	private void recycleDrawables(Set<AnimationView> animationViewSet) {
		if (animationViewSet == null) return;
		for (AnimationView animationView : animationViewSet) {
			if (animationView == null) continue;
			ImageView imageView = animationView.getImageView();
			try {
				ViewUnbindHelper.unbindReferences(imageView, null, true);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

	}

	public class SmartSnapsTypeSelectTutorialPagerAdapter implements Holder<eSmartSnapsTypeSelectTutorial> {
		private font.FTextView tvTitle, tvSubTitle;
		private Context context;
		private ImageView ivAnimation = null;

		@Override
		public View createView(Context context) {
			if (context == null) return null;
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
			if (inflater == null) return null;
			this.context = context;
			View inflateView = inflater.inflate( R.layout.smart_snaps_select_type_tutorial_item_layout, null );
			tvTitle = (FTextView) inflateView.findViewById(R.id.smart_snaps_select_type_tutorial_item_title_tv);
			tvSubTitle = (FTextView) inflateView.findViewById(R.id.smart_snaps_select_type_tutorial_item_sub_title_tv);
			ivAnimation = (ImageView) inflateView.findViewById(R.id.smart_snaps_select_type_tutorial_item_animation_iv);
			return inflateView;
		}

		@Override
		public void UpdateUI(final Context context, final int position, eSmartSnapsTypeSelectTutorial tutorialType) {
			if (tvTitle != null) tvTitle.setText(getTitleByTutorialType(tutorialType));
			if (tvSubTitle != null) tvSubTitle.setText(getSubTitleByTutorialType(tutorialType));

			//이런식으로 처리한 이유는 화면에 보여지는 순간부터 애니메이션을 처리해야하는데 이 메서드는 생성되는 시점에 호출되는 것 같다. 그래서 ViewPager 페이지가 변경되는 시점에 이미지를 로딩한다.
			handleTutorialAnimationWithType(tutorialType);
		}

		private void handleTutorialAnimationWithType(eSmartSnapsTypeSelectTutorial tutorialType) {
			if (tutorialType == null || ivAnimation == null) return;

			try {
				switch (tutorialType) {
					case TYPE_CENTER_FACE:
						centerFaceAnimationsSet.add(new AnimationView(ivAnimation, tutorialType));
						if (!isFirstAnimationFinished && centerFaceAnimationsSet != null) {
							isFirstAnimationFinished = true;
						}
						startAnimation(ivAnimation, tutorialType);
						break;
					case TYPE_SORT_BY_DATE:
						sortByDateAnimationsSet.add(new AnimationView(ivAnimation, tutorialType));
						break;
					case TYPE_AUTO_ROTATION:
						autoRotationAnimationsSet.add(new AnimationView(ivAnimation, tutorialType));
						break;
				}
			} catch (OutOfMemoryError e) {
				Dlog.e(TAG, e);
			}
		}

		private String getTitleByTutorialType(eSmartSnapsTypeSelectTutorial tutorialType) {
			if (tutorialType == null || context == null) return "";
			switch (tutorialType) {
				case TYPE_CENTER_FACE:
					return context.getString(R.string.smart_snaps_tutorial_center_title);
				case TYPE_SORT_BY_DATE:
					return context.getString(R.string.smart_snaps_tutorial_sorting_title);
				case TYPE_AUTO_ROTATION:
					return context.getString(R.string.smart_snaps_tutorial_rotation_title);
			}
			return "";
		}

		private String getSubTitleByTutorialType(eSmartSnapsTypeSelectTutorial tutorialType) {
			if (tutorialType == null || context == null) return "";
			switch (tutorialType) {
				case TYPE_CENTER_FACE:
					return context.getString(R.string.smart_snaps_tutorial_center_sub_title);
				case TYPE_SORT_BY_DATE:
					return context.getString(R.string.smart_snaps_tutorial_sorting_sub_title);
				case TYPE_AUTO_ROTATION:
					return context.getString(R.string.smart_snaps_tutorial_rotation_sub_title);
			}
			return "";
		}
	}

	@Override
	public void onGoHome() {
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v == null) return;

		if (v.getId() == R.id.smart_snaps_type_select_activity_smart_choice) {
			startImageSelectActivityWithSmartSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_CHOICE);
		} else if (v.getId() == R.id.smart_snaps_type_select_activity_normal_choice) {
			startImageSelectActivityWithSmartSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.NORMAL_CHOICE);
		} else if (v.getId() == R.id.ThemeTitleLeftLy || v.getId() == R.id.ThemeTitleLeft) {
			finish();
		}
	}

	private void startImageSelectActivityWithSmartSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType selectType) {
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		smartSnapsManager.setSmartSnapsImageSelectType(selectType);

		Intent intent = new Intent(this, ImageSelectActivityV2.class);
		ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
				.setSmartSnapsImageSelectType(selectType)
				.setHomeSelectProduct(selectType == SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_CHOICE ? Config.SELECT_SMART_SIMPLEPHOTO_BOOK : Config.SELECT_SIMPLEPHOTO_BOOK)
				.setHomeSelectProductCode(Config.getPROD_CODE())
				.setHomeSelectKind("").create();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
		intent.putExtras(bundle);
		startActivity(intent);

		sendSmartSnapsTypeSelectEventToAnalysis(selectType);
	}

	private void sendSmartSnapsTypeSelectEventToAnalysis(SmartSnapsConstants.eSmartSnapsImageSelectType selectType) {
		if (selectType == null) return;
		try {
//			SnapsAnswers.sendCustomEventToAnswer(SnapsAnswersConstants.eEventName.ANSWERS_EVENT_SMART_SNAPS_SELECT_TYPE.toString(),
//					SnapsAnswersConstants.eAttributeName.ANSWERS_ATTRIBUTE_SMART_SELECT_TYPE.toString(),
//					selectType.toString());

			AppsFlyerLib.getInstance().trackEvent(this, "af_smart_snaps_image_select_type_" + selectType , new HashMap<String, Object>());
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void setIndicator(int position) throws Exception {
		if (indicators == null || indicators.size() <= position) return;

		for (int ii = 0; ii < indicators.size(); ii++) {
			ImageView imageView = indicators.get(ii);
			if (imageView == null) return;

			if (imageView.getDrawable() != null) {
				imageView.getDrawable().setCallback(null);
				imageView.setImageBitmap(null);
			}

			imageView.setImageResource(position == ii ? R.drawable.img_smart_snaps_tutorial_dot_on : R.drawable.img_smart_snaps_tutorial_dot_off);
		}
	}

	private void createIndicator(LinearLayout layout) {
		indicators = new ArrayList<>();
		for (int ii = 0; ii < eSmartSnapsTypeSelectTutorial.values().length; ii++) {
			ImageView indicator = new ImageView(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			indicator.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			layoutParams.leftMargin = ii > 0 ? UIUtil.convertDPtoPX(this, 6) : 0;
			indicator.setLayoutParams(layoutParams);

			layout.addView(indicator);
			indicators.add(indicator);
		}
	}

	public static class AnimationView {
		private ImageView imageView;
		private eSmartSnapsTypeSelectTutorial tutorialType;

		public AnimationView(ImageView iv, eSmartSnapsTypeSelectTutorial type) {
			this.imageView = iv;
			this.tutorialType = type;
		}

		public ImageView getImageView() {
			return imageView;
		}

		public void setImageView(ImageView imageView) {
			this.imageView = imageView;
		}

		public eSmartSnapsTypeSelectTutorial getTutorialType() {
			return tutorialType;
		}

		public void setTutorialType(eSmartSnapsTypeSelectTutorial tutorialType) {
			this.tutorialType = tutorialType;
		}
	}
}
