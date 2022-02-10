package com.snaps.mobile.activity.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.fragment.app.Fragment;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IBetween.LOGO_TYPE;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryMainActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class HomeMenuBase extends Fragment {
	private static final String TAG = HomeMenuBase.class.getSimpleName();
	public enum eSlideMenuItem {
		HOME,
		CART,
		ORDER,
		COUPON,
		INVITE
	}

	public interface ILogoDownloadListener {
		public void onFinished(LOGO_TYPE TYPE, Bitmap drawable);
	}

	public interface OnSlideMenuLitener {
		public void onCloseMenu();
	}

	public abstract void setCartCount();

	public abstract void setNotice(boolean on);

	protected void goToCartList(Context con) {
	}

	protected void goToDiaryList(Context con) {
		Intent diaryItt = new Intent(con, SnapsDiaryMainActivity.class);
		startActivity(diaryItt);
	}

	protected String getCartListText() {
		String naviBarTitle = "";
		try {
			naviBarTitle = URLEncoder.encode(getString(R.string.cart), "utf-8");
		} catch (UnsupportedEncodingException e) {
			Dlog.e(TAG, e);
		}

		return naviBarTitle;
	}
}
