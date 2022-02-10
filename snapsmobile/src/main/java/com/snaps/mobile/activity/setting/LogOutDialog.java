package com.snaps.mobile.activity.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;

public class LogOutDialog extends Dialog implements android.view.View.OnClickListener {

	SnapsSettingActivity mSSA;


	private TextView WebContents;

	private TextView mTitleView;

	private LinearLayout mBtnCancel;
	private LinearLayout mBtnAgree;

	private String mTitle;
	private String mMessage;

	public LogOutDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		// TODO Auto-generated constructor stub
	}

	public LogOutDialog(Context context, String title, String message) {
		super(context);
		this.mTitle = title;
		this.mMessage = message;
	}

	public LogOutDialog(SnapsSettingActivity ssa,  Context context, String message) {
		super(context);
		this.mSSA = ssa;
		this.mMessage = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_custom_webview);

		WebContents = (TextView) findViewById(R.id.webMessage);

		mBtnCancel = (LinearLayout) findViewById(R.id.btnDelCancel);
		mBtnAgree = (LinearLayout) findViewById(R.id.btnDelOk);

		// 애니메이션 효과 주기
		getWindow().getAttributes().windowAnimations = R.style.upload_DialogAnimation;

		setWebMessage(mMessage);

		mBtnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mBtnAgree.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mSSA.logoutStep();
				MessageUtil.toast(mSSA, R.string.logout_complete);
				Intent intent = new Intent(Const_VALUE.LOGIN_ACTION);
				mSSA.sendBroadcast(intent);
				dismiss();
			}
		});
	}

	@Override
	public void onBackPressed() {
		dismiss();
		super.onBackPressed();
	}



	@Override
	public void onClick(View v) {

	}

	void setWebMessage(String content) {
		WebContents.setText(content);
	}

}

