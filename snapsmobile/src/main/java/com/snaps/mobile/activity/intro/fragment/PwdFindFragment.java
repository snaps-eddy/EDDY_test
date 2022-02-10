package com.snaps.mobile.activity.intro.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UI;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class PwdFindFragment extends Fragment implements  View.OnFocusChangeListener {
	// layout
	EditText mEditName;
	EditText mEditEmail;

	ImageView  mEditNameUnderline;
	ImageView  mEditEmailUnderline;

	// config
	boolean isOk = false;

	boolean mIdOn = false;
	boolean mPwdOn = false;

	TextView mPwdFindBtn;
	TextView mPwdFindText;

	ISnapsHamburgerMenuListener menuListenter = null;

	public static PwdFindFragment newInstance(ISnapsHamburgerMenuListener listenter) {
		PwdFindFragment fragment = new PwdFindFragment();
		fragment.menuListenter = listenter;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_loginp_pwdfind, container, false);

		mEditName = (EditText) v.findViewById(R.id.editName);
		mEditEmail = (EditText) v.findViewById(R.id.editEmail);

		mEditName.setOnFocusChangeListener(this);
		mEditEmail.setOnFocusChangeListener(this);

		mEditNameUnderline = (ImageView) v.findViewById(R.id.editNameUnderline);
		mEditEmailUnderline = (ImageView) v.findViewById(R.id.editEmailUnderline);

		/** 전화걸기는 국내 버전에만 적용한다. **/
		View telLayout = v.findViewById(R.id.fragment_loginp_pwdfind_tel_layout);
		if (!Config.useKorean()) {
			if (telLayout != null) {
				telLayout.setVisibility(View.GONE);
			}
		}

		mPwdFindBtn = (TextView) v.findViewById(R.id.btnPwdFind);
		mPwdFindText = (TextView) v.findViewById(R.id.editTxt);

		UI.<TextView> findViewById(v, R.id.btnPwdFind).setOnClickListener(onClick);
		UI.<TextView> findViewById(v, R.id.editTxt).setOnClickListener(onClick);
		UI.<ImageView> findViewById(v, R.id.fragment_loginp_login_back_iv).setOnClickListener(onClick);

		mEditName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {

				if (s.length() > 0) {
					mIdOn = true;

				} else {
					mIdOn = false;
				}

				if (mIdOn && mPwdOn) {
					mPwdFindBtn.setBackgroundResource(R.drawable.selector_red_btn);
				} else {
					mPwdFindBtn.setBackgroundResource(R.drawable.selector_black_btn);
				}
			}
		});

		mEditEmail.addTextChangedListener(new TextWatcher() {

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

				if (mIdOn && mPwdOn) {
					mPwdFindBtn.setBackgroundResource(R.drawable.selector_red_btn);
				} else {
					mPwdFindBtn.setBackgroundResource(R.drawable.selector_black_btn);
				}
			}
		});

		return v;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (v.getId() == R.id.editName) {
				mEditNameUnderline.setBackgroundColor(Color.WHITE);
				mEditEmailUnderline.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
			} else if (v.getId() == R.id.editEmail) {
				mEditNameUnderline.setBackgroundColor(getResources().getColor(R.color.color_join_edit_text_normal));
				mEditEmailUnderline.setBackgroundColor(Color.WHITE);
			}
		}
	}

	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnPwdFind) {// 비밀번호 찾기

				if (mIdOn && mPwdOn) {
					pwdFind();
				} else {
					if (!mIdOn) {
						MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_name), new ICustomDialogListener() {
							@Override
							public void onClick(byte clickedOk) {
								mEditName.requestFocus();
							}
						});
					} else if (!mPwdOn) {
						MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_input_email), new ICustomDialogListener() {
							@Override
							public void onClick(byte clickedOk) {
								mEditEmail.requestFocus();
							}
						});
					}
				}
			} else if (v.getId() == R.id.editTxt) {
				boolean permissionGranted = true;
				if(Build.VERSION.SDK_INT > 22 ) {
					if(getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
						if( shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) )
							requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Const_VALUE.REQ_CODE_PERMISSION); // 설명을 보면 한번 사용자가 거부하고, 다시 묻지 않기를 체크하지 않았을때 여기를 탄다고 한다. 이때 설명을 넣고 싶으면 이걸 지우고 넣자.
						else requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Const_VALUE.REQ_CODE_PERMISSION);
						permissionGranted = false;
					}
				}

				if (permissionGranted) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15774701"));
					startActivity(intent);
				}
			} else if (v.getId() == R.id.fragment_loginp_login_back_iv) {
				if (menuListenter != null)
					menuListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK);
			}
		}
	};

	/**
	 * 스냅스 비밀번호 찾기
	 */
	void pwdFind() {

		final String findName = mEditName.getText().toString();
		final String findEmail = mEditEmail.getText().toString();

		if ("".equals(findEmail) || "".equals(findName)) {// 빈칸체크
			MessageUtil.toast(getActivity(), R.string.login_validate_email);
			return;
		}
		if (!StringUtil.isValidEmail(findEmail)) {// 이메일체크
			MessageUtil.toast(getActivity(), R.string.login_validate_id);
			return;
		}

		ATask.executeVoidDefProgress(getActivity(), new ATask.OnTask() {
			@Override
			public void onPre() {
			}

			@Override
			public void onBG() {
				isOk = HttpReq.snapsNewPwdFind(getActivity(), findEmail, findName, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			}

			@Override
			public void onPost() {
				if (isOk) {// 비번찾기 성공
					MessageUtil.toast(getActivity(), R.string.login_send_pwd_email);
				} else {
					MessageUtil.toast(getActivity(), R.string.login_send_pwd_email_fail);
				}

				if (menuListenter != null)
					menuListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_COMPLATE_PWD_FIND);
			}
		});
	}

}
