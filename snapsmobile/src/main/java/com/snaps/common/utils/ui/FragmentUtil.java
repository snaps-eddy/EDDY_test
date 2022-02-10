package com.snaps.common.utils.ui;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.snaps.common.utils.log.Dlog;

public class FragmentUtil {
	private static final String TAG = FragmentUtil.class.getSimpleName();

	public static final String TAG_DETAIL = "ImageDetail";
	public static final String TAG_SELECT = "OptionSelect";

	public static void replce(int frameRsId, FragmentActivity act, Fragment newFragment) {
		replce(frameRsId, act, newFragment, null);
	}

	public static void replce(int frameRsId, FragmentActivity act, Fragment newFragment, String tag) {
		FragmentManager fm = act.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
			fm.popBackStack();// Fragment를 replace 하기 전에 혹시 BackStack이 있다면 비워준다.
		}
		if (tag == null) {
			ft.replace(frameRsId, newFragment);
		} else {
			ft.replace(frameRsId, newFragment, tag);
		}
		ft.commit();
	}

	public static void replce(int frameRsId, FragmentActivity act, Fragment newFragment, String tag, int animA, int animB) {
		replce(frameRsId, act, newFragment, tag, animA, animB, 0, 0);
	}

	public static void replce(int frameRsId, FragmentActivity act, Fragment newFragment, String tag, int animA, int animB, int animOutA, int animOutB) {
		FragmentManager fm = act.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
			fm.popBackStack();// Fragment를 replace 하기 전에 혹시 BackStack이 있다면 비워준다.
		}

		if (animA >= 0 && animB >= 0) {
			ft.setCustomAnimations(animA, animB, animOutA, animOutB);
		}

		if (tag == null) {
			ft.replace(frameRsId, newFragment);
		} else {
			ft.replace(frameRsId, newFragment, tag);
		}
		ft.commitAllowingStateLoss();
	}

	public static void replceBackStack(int frameRsId, FragmentActivity act, Fragment newFragment, int animA, int animB) {
		replceBackStack(frameRsId, act, newFragment, null, animA, animB);
	}

	public static void replceBackStack(int frameRsId, FragmentActivity act, Fragment newFragment) {
		replceBackStack(frameRsId, act, newFragment, null, -1, -1);
	}

	public static void replceBackStack(int frameRsId, FragmentActivity act, Fragment newFragment, String tag) {
		replceBackStack(frameRsId, act, newFragment, tag, -1, -1);
	}

	public static void replceBackStack(int frameRsId, FragmentActivity act, Fragment newFragment, String tag, int animA, int animB) {
		replceBackStack(frameRsId, act, newFragment, tag, animA, animB, 0, 0);
	}

	public static void replceBackStack(int frameRsId, FragmentActivity act, Fragment newFragment, String tag, int animA, int animB, int animOutA, int animOutB) {
		FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();

		if (animA >= 0 && animB >= 0) {
			ft.setCustomAnimations(animA, animB, animOutA, animOutB);
		}

		if (tag == null) {
			ft.replace(frameRsId, newFragment);
		} else {
			ft.replace(frameRsId, newFragment, tag);
		}

		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	public static int getStackCount(FragmentActivity act) {
		FragmentManager fm = act.getSupportFragmentManager();
		return fm.getBackStackEntryCount();
	}

	public static void toBack(Activity act) {
		act.onBackPressed();
	}

	public static void onBackPressed(FragmentActivity act) {
		try {
			FragmentManager fm = act.getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			if (fm.getBackStackEntryCount() > 0) {
				fm.popBackStack();
				ft.commit();
			}
		} catch (IllegalStateException e) {
			Dlog.e(TAG, e);
		}
	}
}
