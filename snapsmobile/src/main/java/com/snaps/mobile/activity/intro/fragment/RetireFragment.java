package com.snaps.mobile.activity.intro.fragment;

import android.os.Bundle;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UI;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.structure.SnapsHandler;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class RetireFragment extends Fragment implements View.OnFocusChangeListener, ISnapsHandler {
	// layout
	EditText editRetirePwdConfirm;
	EditText editRetirePwdReConfirm;

	ImageView editRetirePwdConfirmUnderline;
	ImageView editRetirePwdReConfirmUnderline;

	TextView mRetireTitle;
	TextView mRetireConfirm;

	TextView mRetireTxt1;
	TextView mRetireTxt2;
	TextView mRetireTxt3;
	TextView mRetireTxt4;

	View inputAreaLayout;

	ScrollView parentScrollView;
	
	// data
	String userNo;
	
	// config
	boolean isOk = false;
	boolean mPwdOn = false;
	boolean mRePwdOn = false;

	ISnapsHamburgerMenuListener menuListenter = null;

	SnapsHandler mSnapsHanler = null;

	public static RetireFragment newInstance(ISnapsHamburgerMenuListener listenter) {
		RetireFragment fragment = new RetireFragment();
		fragment.menuListenter = listenter;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_loginp_retire, container, false);

		editRetirePwdConfirm = (EditText) v.findViewById(R.id.editRetirePwdConfirm);
		editRetirePwdReConfirm = (EditText) v.findViewById(R.id.editRetirePwdReConfirm);
		mRetireConfirm = (TextView) v.findViewById(R.id.btnRetire);

		editRetirePwdConfirmUnderline = (ImageView) v.findViewById(R.id.editRetirePwdConfirmUnderline);
		editRetirePwdReConfirmUnderline = (ImageView) v.findViewById(R.id.editRetirePwdReConfirmUnderline);

		editRetirePwdConfirm.setOnFocusChangeListener(this);
		editRetirePwdReConfirm.setOnFocusChangeListener(this);

		mRetireTitle = (TextView)v.findViewById(R.id.txtRetireDesc1);
		
		mRetireTxt1 = (TextView)v.findViewById(R.id.txtRetireDesc2_1);
		mRetireTxt2 = (TextView)v.findViewById(R.id.txtRetireDesc2_2);
		mRetireTxt3 = (TextView)v.findViewById(R.id.txtRetireDesc2_3);
		mRetireTxt4 = (TextView)v.findViewById(R.id.txtRetireDesc2_4);

		parentScrollView = (ScrollView)v.findViewById(R.id.fragment_login_retire_scrollview);

		inputAreaLayout = v.findViewById(R.id.retire_input_layout);

		if (!Config.useKorean()) {
			//외국어 버전에서 보여지면 안 되는 텍스트들
			View hideTextLayout01 = v.findViewById(R.id.txtRetireDesc2_2_ly);
			View hideTextLayout02 = v.findViewById(R.id.txtRetireDesc2_4_ly);
			if (hideTextLayout01 != null) hideTextLayout01.setVisibility(View.GONE);
			if (hideTextLayout02 != null) hideTextLayout02.setVisibility(View.GONE);
		}

		userNo = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO);

		UI.<TextView> findViewById(v, R.id.btnRetireCancel).setOnClickListener(onClick);
		UI.<TextView> findViewById(v, R.id.btnRetire).setOnClickListener(onClick);
		UI.<ImageView> findViewById(v, R.id.fragment_loginp_login_back_iv).setOnClickListener(onClick);

		editRetirePwdConfirm.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					mPwdOn = true;
				} else {
					mPwdOn = false;
				}

				if (mPwdOn && mRePwdOn) {
					mRetireConfirm.setBackgroundResource(R.drawable.selector_red_btn);
				} else {
					mRetireConfirm.setBackgroundResource(R.drawable.selector_black_btn);
				}
			}
		});

		editRetirePwdReConfirm.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					mRePwdOn = true;
				} else {
					mRePwdOn = false;
				}

				if (mPwdOn && mRePwdOn) {
					mRetireConfirm.setBackgroundResource(R.drawable.selector_red_btn);
				} else {
					mRetireConfirm.setBackgroundResource(R.drawable.selector_black_btn);
				}
			}
		});

		mSnapsHanler = new SnapsHandler(this);

		return v;
	}

	@Override
	public void handleMessage(Message msg) {
		if (parentScrollView != null) {
			if (inputAreaLayout != null) {
				parentScrollView.scrollTo(0, (int) inputAreaLayout.getY());

				if (parentScrollView.getScrollY() != inputAreaLayout.getY()) {
					mSnapsHanler.sendEmptyMessageDelayed(0, 100);
				}
			}
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (v.getId() == R.id.editRetirePwdConfirm) {
				editRetirePwdConfirmUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_focus));
				editRetirePwdReConfirmUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_normal));
				mSnapsHanler.sendEmptyMessage(0);

			} else if (v.getId() == R.id.editRetirePwdReConfirm) {
				editRetirePwdConfirmUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_normal));
				editRetirePwdReConfirmUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_focus));
				mSnapsHanler.sendEmptyMessage(0);
			}
		}
	}

	/**
	 * 스냅스 탈퇴하기
	 */
	public void requestSnapsRetire(String passWord) {
		final String PASSWORD = passWord;

		ATask.executeVoidDefProgress(getActivity(), new ATask.OnTask() {
			@Override
			public void onPre() {
			}

			@Override
			public void onBG() {
				isOk = HttpReq.snapsRetire(getActivity(), "validate", userNo, PASSWORD, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				if (isOk)
					isOk = HttpReq.snapsRetire(getActivity(), "proc", userNo, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			}

			@Override
			public void onPost() {
				if (isOk) {// 탈퇴 성공
					PrefUtil.clearUserInfo(getActivity(), true);// 모든정보 초기화
					MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.retire_ok), new ICustomDialogListener() {
						@Override
						public void onClick(byte clickedOk) {
							if (menuListenter != null)
								menuListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_COMPLATE_RETIRE);
						}
					});
				} else {
					MessageUtil.toast(getActivity(), R.string.pwdreset_pwd_fail);
				}
			}
		});
	}

	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnRetireCancel || v.getId() == R.id.fragment_loginp_login_back_iv) {// 취소
				if (menuListenter != null)
					menuListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK);

			} else if (v.getId() == R.id.btnRetire) {// 탈퇴
				String pwd = editRetirePwdConfirm.getText().toString();
				if(pwd.trim().length() > 0) {
					if (editRetirePwdConfirm.getText().toString().equals(editRetirePwdReConfirm.getText().toString())) {
						MessageUtil.alertnoTitleTwoBtn(getActivity(), getString(R.string.retire_confirm_text), new ICustomDialogListener() {
							@Override
							public void onClick(byte clickedOk) {
								requestSnapsRetire(editRetirePwdConfirm.getText().toString());
							}
						});
					} else {
						MessageUtil.toast(getActivity(), R.string.pwdreset_pwd_fail);
						editRetirePwdReConfirm.requestFocus();
					}
				} else {
					MessageUtil.toast(getActivity(), R.string.pwdreset_fail_empty);
					editRetirePwdConfirm.requestFocus();
				}
			}
		}
	};
	
	

}
