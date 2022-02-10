package com.snaps.mobile.activity.intro.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UI;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class PwdResetFragment extends Fragment implements View.OnFocusChangeListener {
	private static final String TAG = PwdResetFragment.class.getSimpleName();

	// layout
	EditText editCurrPwd;
	EditText editNewPwd;
	EditText editMoreNewPwd;

	ImageView editCurrPwdUnderline;
	ImageView editNewPwdUnderline;
	ImageView editMoreNewPwdUnderline;
	
	TextView mEditTxt;
	
	// data
	String pwdResetResult;
	String userNo;
	
	boolean mCurrPwdOn = false;
	boolean mNewPwdOn = false;
	boolean mMoreNewPwdOn = false;
	
	TextView mLoginBtn;
	
	ISnapsHamburgerMenuListener menuListenter = null;

	public static PwdResetFragment newInstance(ISnapsHamburgerMenuListener listenter) {
		PwdResetFragment fragment = new PwdResetFragment();
		fragment.menuListenter = listenter;
		return fragment;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromInputMethod(editCurrPwd.getWindowToken(), 0);
			imm.hideSoftInputFromInputMethod(editNewPwd.getWindowToken(), 0);
			imm.hideSoftInputFromInputMethod(editMoreNewPwd.getWindowToken(), 0);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_loginp_pwdreset, container, false);
		
		userNo = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_NO);

		editCurrPwd = (EditText) v.findViewById(R.id.editCurrPwd);
		editNewPwd = (EditText) v.findViewById(R.id.editNewPwd);
		editMoreNewPwd = (EditText) v.findViewById(R.id.editMoreNewPwd);

		editCurrPwd.setOnFocusChangeListener(this);
		editNewPwd.setOnFocusChangeListener(this);
		editMoreNewPwd.setOnFocusChangeListener(this);

		mLoginBtn = (TextView) v.findViewById(R.id.btnPwdReset);
		mEditTxt = (TextView) v.findViewById(R.id.editChangeTxt);

		editCurrPwdUnderline = (ImageView) v.findViewById(R.id.editCurrPwdUnderline);
		editNewPwdUnderline = (ImageView) v.findViewById(R.id.editNewPwdUnderline);
		editMoreNewPwdUnderline = (ImageView) v.findViewById(R.id.editMoreNewPwdUnderline);

		editCurrPwd.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				try {
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(editCurrPwd, 0);
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
			}
		}, 200);
		
		UI.<TextView> findViewById(v, R.id.btnPwdReset).setOnClickListener(onClick);
		UI.<ImageView> findViewById(v, R.id.fragment_loginp_login_back_iv).setOnClickListener(onClick);

		editCurrPwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				if (s.length() > 0) {
					mCurrPwdOn = true;

				} else {
					mCurrPwdOn = false;
				}

				allCheckItem();
			}
		});

		editNewPwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					mNewPwdOn = true;
				} else {
					mNewPwdOn = false;
				}

				allCheckItem();
			}
		});

		editMoreNewPwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				if (s.length() > 0) {
					mMoreNewPwdOn = true;

				} else {
					mMoreNewPwdOn = false;
				}

				allCheckItem();
			}
		});
		return v;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (v.getId() == R.id.editCurrPwd) {
				editCurrPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_focus));
				editNewPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_normal));
				editMoreNewPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_normal));
			} else if (v.getId() == R.id.editNewPwd) {
				editCurrPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_normal));
				editNewPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_focus));
				editMoreNewPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_normal));
			} else if (v.getId() == R.id.editMoreNewPwd) {
				editCurrPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_normal));
				editNewPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_normal));
				editMoreNewPwdUnderline.setBackgroundColor(getResources().getColor(R.color.color_white_bg_edit_text_focus));
			}
		}
	}

	public void allCheckItem()
	{
		if (mCurrPwdOn && mNewPwdOn && mMoreNewPwdOn) {
			mLoginBtn.setBackgroundResource(R.drawable.selector_red_btn);
		} else {
			mLoginBtn.setBackgroundResource(R.drawable.selector_black_btn);
		}
	}
	
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnPwdReset) {// 비번재설정
				
				if (mCurrPwdOn && mNewPwdOn && mMoreNewPwdOn) {
					snapsPwdReset();
				} else {
					if (!mCurrPwdOn) {
						MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_current_pwd), new ICustomDialogListener() {
							@Override
							public void onClick(byte clickedOk) {
								editCurrPwd.requestFocus();
							}
						});
					} else if (!mNewPwdOn) {
						MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_new_pwd), new ICustomDialogListener() {
							@Override
							public void onClick(byte clickedOk) {
								editNewPwd.requestFocus();
							}
						});
					} else if (!mMoreNewPwdOn) {
						MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_new_pwd), new ICustomDialogListener() {
							@Override
							public void onClick(byte clickedOk) {
								editMoreNewPwd.requestFocus();
							}
						});
					}
				}
			} else if (v.getId() == R.id.fragment_loginp_login_back_iv) {
				if (menuListenter != null)
					menuListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK);
			}
		}
	};
	
	/**
	 * 스냅스 비번재설정
	 */
	void snapsPwdReset() {
		final String snapsCurrPwd = editCurrPwd.getText().toString();
		final String snapsNewPwd = editNewPwd.getText().toString();
		final String snapsMoreNewPwd = editMoreNewPwd.getText().toString();
		if ("".equals(snapsCurrPwd) || "".equals(snapsNewPwd) || "".equals(snapsMoreNewPwd)) {// 빈칸체크
			MessageUtil.toast(getActivity(), R.string.login_validate);
			return;
		}
		
		if(!snapsNewPwd.equals(snapsMoreNewPwd))
		{
			MessageUtil.toast(getActivity(), R.string.pwdreset_new_pwd_fail);
			return;
		}
		if (!StringUtil.isValidPwd(6, 15, snapsNewPwd) || !StringUtil.isValidPwd(6, 15, snapsMoreNewPwd)) {// 패스워드체크
			MessageUtil.toast(getActivity(), R.string.login_validate_pwd);
			return;
		}

		if(!snapsNewPwd.equals(snapsMoreNewPwd))
		{
			MessageUtil.toast(getActivity(), R.string.pwdreset_pwd_fail);
			return;
		}

		//한글 빼고 모두 허용.
		if (StringUtil.isContainLanguageChar(snapsNewPwd)) { // 패스워드체크
			MessageUtil.toast(getActivity(), R.string.failed_join_cause_contains_not_valid_char);
			return;
		}
		
		ATask.executeVoidDefProgress(getActivity(), new ATask.OnTask() {
			@Override
			public void onPre() {}
			@Override
			public void onBG() {
				pwdResetResult = HttpReq.snapsPwdReset(userNo, snapsCurrPwd, snapsNewPwd, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			}
			@Override
			public void onPost() {
				if ("complete".equals(pwdResetResult)) {// 성공
					MessageUtil.toast(getActivity(), R.string.change_password_complete);
					Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_USER_PWD, snapsNewPwd);
					if (menuListenter != null) {
						menuListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_COMPLATE_PWD_RESET);
					}
				} else if ("empty".equals(pwdResetResult)) {// 현재비번실패
					MessageUtil.toast(getActivity(), R.string.pwdreset_pwd_fail);
				} else if ("fail".equals(pwdResetResult)) {// 실패
					MessageUtil.toast(getActivity(), R.string.change_password_fail);
				}
			}
		});
	}
}
