package com.snaps.mobile.activity.intro;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.ui.UI;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;

import errorhandle.CatchActivity;

public class IntroActivity extends CatchActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);

		UI.<TextView> findViewById(this, R.id.btnSnapsJoin).setText(Html.fromHtml("<u>"+getString(R.string.signup)+"</u>"));
	}

	public void onClick(View v) {
		SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_LOGIN);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
