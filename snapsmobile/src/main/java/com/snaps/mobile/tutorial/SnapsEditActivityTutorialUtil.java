package com.snaps.mobile.tutorial;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.utils.animation.AnimationFactory;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.mobile.tutorial.tooltip_tutorial.TooltipTutorialFactory;
import com.snaps.mobile.tutorial.tooltip_tutorial.attributes.TooltipTutorialCreator;
import com.snaps.common.utils.system.DateUtil;

import java.util.Calendar;

import errorhandle.SnapsAssert;
import font.FTextView;

/**
 * Created by ysjeong on 2017. 7. 31..
 */

public class SnapsEditActivityTutorialUtil {
	private static final String TAG = SnapsEditActivityTutorialUtil.class.getSimpleName();
	public static void showTooltipTutorial(@NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute) {
		try {
			if (checkBlockTutorial(attribute)) {
				return;
			}

			TooltipTutorialCreator tooltipTutorialCreator = createTooltipTextView(activity, attribute);

			FTextView tooltipTextView = addTooltipViewOnActivity(tooltipTutorialCreator, attribute.getTooltipTutorialLayout());

			addTooltipViewDismissListener(attribute.getTooltipTutorialLayout(), tooltipTextView, tooltipTutorialCreator);
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(activity, e);
		}
	}

	private static FTextView addTooltipViewOnActivity(TooltipTutorialCreator tooltipTutorialCreator, ViewGroup parentView) throws Exception {
		FTextView tooltipTextView = tooltipTutorialCreator.createTooltipTextView();
		parentView.removeAllViews();
		parentView.addView(tooltipTextView);
		return tooltipTextView;
	}

	private static void addTooltipViewDismissListener(final ViewGroup parentView, final FTextView tooltipTextView, TooltipTutorialCreator tooltipTutorialCreator) throws Exception {
		parentView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					parentView.setOnTouchListener(null);
					AnimationFactory.fadeOutAndRemoveView(parentView, tooltipTextView);
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
				return false;
			}
		});

		if (tooltipTutorialCreator.isAutoHideAfterDelay()) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						parentView.setOnTouchListener(null);
						AnimationFactory.fadeOutAndRemoveView(parentView, tooltipTextView);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}
			}, tooltipTutorialCreator.getAutoHideDelay());
		}
	}

	//FIXME 각 투토리얼 객체가 처리할 수 있도록 수정하자
	private static boolean checkBlockTutorial(SnapsTutorialAttribute attribute) throws Exception {
		return isPhotoCardQuantityTutorialBlock(attribute)
				|| isPhotoCardLongClickDeleteTutorialBlock(attribute)
				|| isPhotoCardChangeDesignTutorialBlock(attribute)
				|| isWalletPhotoChangeDesignTutorialBlock(attribute);
	}

	private static boolean isPhotoCardQuantityTutorialBlock(SnapsTutorialAttribute attribute) throws Exception {
		if (attribute.getTutorialId() != SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_QUANTITY) {
			return false;
		}

		Context context = ContextUtil.getContext();
		long lastShownTime = context != null ? Setting.getLong(context, Const_VALUE.KEY_SHOWN_TIME_FOR_PHOTO_CARD_QUANTITY_TUTORIAL) : 0;
		Calendar lastShownCalendar = Calendar.getInstance();
		lastShownCalendar.setTimeInMillis(lastShownTime);
		if (DateUtil.isToday(lastShownCalendar)) {
			return true;
		}

		if (context != null) {
			Setting.set(context, Const_VALUE.KEY_SHOWN_TIME_FOR_PHOTO_CARD_QUANTITY_TUTORIAL, System.currentTimeMillis());
		}
		return false;
	}

	private static boolean isPhotoCardLongClickDeleteTutorialBlock(SnapsTutorialAttribute attribute) throws Exception {
		if (attribute.getTutorialId() != SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_LONG_CLICK_DELETE) {
			return false;
		}

		Context context = ContextUtil.getContext();
		long lastShownTime = context != null ? Setting.getLong(context, Const_VALUE.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_LONG_CLICK_DELETE) : 0;
		Calendar lastShownCalendar = Calendar.getInstance();
		lastShownCalendar.setTimeInMillis(lastShownTime);
		if (DateUtil.isToday(lastShownCalendar)) {
			return true;
		}

		if (context != null) {
			Setting.set(context, Const_VALUE.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_LONG_CLICK_DELETE, System.currentTimeMillis());
		}
		return false;
	}

	private static boolean isPhotoCardChangeDesignTutorialBlock(SnapsTutorialAttribute attribute) throws Exception {
		if (attribute.getTutorialId() != SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_DESIGN) {
			return false;
		}

		Context context = ContextUtil.getContext();
		long lastShownTime = context != null ? Setting.getLong(context, Const_VALUE.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_DESIGN) : 0;
		Calendar lastShownCalendar = Calendar.getInstance();
		lastShownCalendar.setTimeInMillis(lastShownTime);
		if (DateUtil.isToday(lastShownCalendar)) {
			return true;
		}

		if (context != null) {
			Setting.set(context, Const_VALUE.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_DESIGN, System.currentTimeMillis());
		}
		return false;
	}

	private static boolean isWalletPhotoChangeDesignTutorialBlock(SnapsTutorialAttribute attribute) throws Exception {
		if (attribute.getTutorialId() != SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_WALLET_PHOTO_CHANGE_DESIGN) {
			return false;
		}

		Context context = ContextUtil.getContext();
		long lastShownTime = context != null ? Setting.getLong(context, Const_VALUE.TUTORIAL_ID_TOOLTIP_WALLET_PHOTO_CHANGE_DESIGN) : 0;
		Calendar lastShownCalendar = Calendar.getInstance();
		lastShownCalendar.setTimeInMillis(lastShownTime);
		if (DateUtil.isToday(lastShownCalendar)) {
			return true;
		}

		if (context != null) {
			Setting.set(context, Const_VALUE.TUTORIAL_ID_TOOLTIP_WALLET_PHOTO_CHANGE_DESIGN, System.currentTimeMillis());
		}
		return false;
	}

	private static TooltipTutorialCreator createTooltipTextView(@NonNull Activity activity, @NonNull SnapsTutorialAttribute attribute) throws Exception {
		return TooltipTutorialFactory.createTooltipTutorial(activity, attribute);
	}
}
