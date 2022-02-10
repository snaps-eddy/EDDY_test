package com.snaps.mobile.activity.hamburger_menu;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.customui.SnapsUnderlineTextView;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;
import com.snaps.mobile.activity.hamburger_menu.ui_by_language.ISnapsHamburgerMenuUIByLanguageStrategy;
import com.snaps.mobile.activity.hamburger_menu.ui_by_language.SnapsHamburgerMenuUISetterForChina;
import com.snaps.mobile.activity.hamburger_menu.ui_by_language.SnapsHamburgerMenuUISetterForEng;
import com.snaps.mobile.activity.hamburger_menu.ui_by_language.SnapsHamburgerMenuUISetterForJpn;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.NoticeItem;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;


public class SnapsHamburgerMenuFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = SnapsHamburgerMenuFragment.class.getSimpleName();
	private ISnapsHamburgerMenuListener mListenter = null;
	private SnapsMenuManager.eHAMBURGER_ACTIVITY eCurrentActivity = null;

	private ISnapsHamburgerMenuUIByLanguageStrategy uiByLanguageStrategy = null;

	public static SnapsHamburgerMenuFragment newInstance(ISnapsHamburgerMenuListener listenter, SnapsMenuManager.eHAMBURGER_ACTIVITY eActivity) {
		SnapsHamburgerMenuFragment fragment = new SnapsHamburgerMenuFragment();
		fragment.mListenter = listenter;
		fragment.eCurrentActivity = eActivity;
		return fragment;
	}

	public  SnapsHamburgerMenuFragment() {}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_hamburger_menu_fragment, container, false);

		ImageView ivGrade = (ImageView) v.findViewById(R.id.activity_hamburger_menu_grade_iv);
		TextView tvUserId = (TextView) v.findViewById(R.id.activity_hamburger_menu_user_id_tv);
		ImageView ivUserIdArrow = (ImageView) v.findViewById(R.id.activity_hamburger_menu_user_id_arrow_iv);
		TextView tvCartCount = (TextView) v.findViewById(R.id.activity_hamburger_menu_cart_tv);
		TextView tvCouponCount = (TextView) v.findViewById(R.id.activity_hamburger_menu_coupon_tv);
		TextView tvNotice = (TextView) v.findViewById(R.id.activity_hamburger_menu_notice_tv);

		setGradeImage(ivGrade);

		setUserId(tvUserId, ivUserIdArrow);

		setCartCount(tvCartCount);

		setCouponCount(tvCouponCount);

		setNotice(tvNotice);

		regiterLiteners(v);

		drawUnderline(v);

		return getUIByLanguage(v);
	}

	@Override
	public void onClick(View v) {
		UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

		if (mListenter != null) {
			if (v.getId() == R.id.activity_hamburger_menu_user_id_ly) {
				String userNo = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO);
				if (userNo.length() < 1) { //로그인이 안 되어 있는 상태.
					mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_LOG_IN);
				}
			} else if (v.getId() == R.id.activity_hamburger_home_utv) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_HOME);
			} else if (v.getId() == R.id.activity_hamburger_event_utv) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_EVENT);
			} else if (v.getId() == R.id.activity_hamburger_diary_utv) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_DIARY);
			} else if (v.getId() == R.id.activity_hamburger_customer_utv) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_CUSTOMER);
			} else if (v.getId() == R.id.activity_hamburger_menu_setting_iv) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_SETTING);
			} else if (v.getId() == R.id.activity_hamburger_menu_close_iv) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK);
			} else if (v.getId() == R.id.activity_hamburger_menu_order_ly) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_ORDER);
			} else if (v.getId() == R.id.activity_hamburger_menu_cart_ly) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_CART);
			} else if (v.getId() == R.id.activity_hamburger_menu_coupon_ly) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_COUPON);
			} else if (v.getId() == R.id.activity_hamburger_menu_notice_ly || v.getId() == R.id.activity_hamburger_menu_notice_tv) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_NOTICE);
			} else if (v.getId() == R.id.activity_hamburger_menu_grade_iv) {
				mListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_MY_SNAPS);
			}
		}
	}

	private View getUIByLanguage(View v) {
		//FIXME 채널코드로 구분하도록 변경 필요 함...

		if (Config.useKorean() || v == null) return v; //기본 UI는 한국

		if (Config.useEnglish())
			uiByLanguageStrategy = new SnapsHamburgerMenuUISetterForEng();
		else if (Config.useJapanese())
			uiByLanguageStrategy = new SnapsHamburgerMenuUISetterForJpn();
		else if (Config.useChinese())
			uiByLanguageStrategy = new SnapsHamburgerMenuUISetterForChina();

		return uiByLanguageStrategy != null ? uiByLanguageStrategy.getConverterView(v) : v;
	}

	private void drawUnderline(View v) {
		if (v == null) return;

		SnapsUnderlineTextView utvHomeView = (SnapsUnderlineTextView) v.findViewById(R.id.activity_hamburger_home_utv);
		SnapsUnderlineTextView utvEventView = (SnapsUnderlineTextView) v.findViewById(R.id.activity_hamburger_event_utv);
		SnapsUnderlineTextView utvDiaryView = (SnapsUnderlineTextView) v.findViewById(R.id.activity_hamburger_diary_utv);
		SnapsUnderlineTextView utvCustomerView = (SnapsUnderlineTextView) v.findViewById(R.id.activity_hamburger_customer_utv);

		if (eCurrentActivity != null) {
			switch (eCurrentActivity) {
				case HOME:
					utvHomeView.drawUnderline(true);
					break;
				case EVENT:
					utvEventView.drawUnderline(true);
					break;
				case DIARY:
					utvDiaryView.drawUnderline(true);
					break;
				case CUSTOMER:
					utvCustomerView.drawUnderline(true);
					break;
			}
		}
	}

	private void regiterLiteners(View v) {
		if (v == null) return;

		int[] arClickListenerIds = {
				R.id.activity_hamburger_home_utv,
				R.id.activity_hamburger_event_utv,
				R.id.activity_hamburger_diary_utv,
				R.id.activity_hamburger_customer_utv,
				R.id.activity_hamburger_menu_setting_iv,
				R.id.activity_hamburger_menu_close_iv,
				R.id.activity_hamburger_menu_grade_iv,
				R.id.activity_hamburger_menu_user_id_ly,
				R.id.activity_hamburger_menu_order_ly,
				R.id.activity_hamburger_menu_cart_ly,
				R.id.activity_hamburger_menu_coupon_ly,
				R.id.activity_hamburger_menu_notice_ly,
				R.id.activity_hamburger_menu_notice_tv,
		};

		for (int id : arClickListenerIds) {
			View view = v.findViewById(id);
			if (view != null) view.setOnClickListener(this);
		}
	}

	private void setNotice(TextView noticeTextView) {
		if (noticeTextView == null) return;
		MenuDataManager menuDataManager = MenuDataManager.getInstance();
		if (menuDataManager == null) return;
		NoticeItem noticeItem = menuDataManager.getNoticeItem();
		if (noticeItem != null && noticeItem.getTitle() != null)
			noticeTextView.setText(noticeItem.getTitle());
	}

	private void setCartCount(TextView cartTextView) {
		cartTextView.setText(String.valueOf(5));

		if (!SnapsTPAppManager.isThirdPartyApp(getActivity())) {
			String userNo = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO);
			if (userNo.length() > 0) {
				int cartCount = Setting.getInt(getActivity(), Const_VALUE.KEY_CART_COUNT);

				if (cartCount <= 0) {
					cartTextView.setText("");
				} else {
					cartTextView.setVisibility(View.VISIBLE);
					cartTextView.setText(String.valueOf(Math.min(99, cartCount)));
				}
			} else {
				cartTextView.setText("");
			}
		}
	}

	private void setCouponCount(TextView couponCountTextView) {
		if (!SnapsTPAppManager.isThirdPartyApp(getActivity())) {
			String userNo = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO);
			if (userNo.length() > 0) {
				int couponCount = Setting.getInt(getActivity(), Const_VALUE.KEY_COUPON_COUNT);

				if (couponCount <= 0) {
					couponCountTextView.setText("");
				} else {
					couponCountTextView.setVisibility(View.VISIBLE);
					couponCountTextView.setText(String.valueOf(Math.min(99, couponCount)));
				}
			} else {
				couponCountTextView.setText("");
			}
		}
	}

	private void setUserId(TextView userId, ImageView arrow) {
		if (userId == null) return;

		String userNo = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO);
		String userName = Setting.getString(getActivity(), Const_VALUE.KEY_USER_INFO_USER_NAME);

		if (userNo.length() > 0) { //로그인이 되어 있는 상태
			if(userName.length() > 0)
				userId.setText(userName);
			else {
				userId.setText(userNo);
			}

			if (arrow != null)
				arrow.setVisibility(View.GONE);
		} else {
			userId.setText(getString(R.string.login));

			if (arrow != null)
				arrow.setVisibility(View.VISIBLE);
		}
	}

	private void setGradeImage(ImageView imageView) {
		if (imageView == null || !Config.useKorean()) return;

		//로그인 체크
		String userNo = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO);
		if (userNo.length() > 0) {
			String userGrade = Setting.getString(getActivity(), Const_VALUE.KEY_USER_INFO_GRADE_CODE);
			SnapsMenuManager.eMemberGrade eUserGrade = SnapsMenuManager.eMemberGrade.getGrade(userGrade);
			Drawable drawable = null;
			try  {
				drawable = eUserGrade.getResource(getActivity());
			} catch (PackageManager.NameNotFoundException e) { Dlog.e(TAG, e); }

			if (drawable  != null)
				imageView.setImageDrawable(drawable);
		} else  {
			imageView.setImageResource(R.drawable.img_hamburger_menu_default_grade);
		}
	}
}
