package com.snaps.mobile.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.SnapsBaseActivity;
import com.snaps.mobile.activity.home.RenewalHomeActivity;

import errorhandle.logger.Logg;

@SuppressLint("HandlerLeak")
public class PushDialogTextActivity extends SnapsBaseActivity {
	private static final String TAG = PushDialogTextActivity.class.getSimpleName();
	String mMainActivity = "com.snaps.mobile.activity.home.HomeActivity";
	String userNo = "";
	int _cart_count = 0;
	String pushTarget = "";
	String pushFullTarget = "";
	boolean isRunning = false;

	public static PushDialogTextActivity pushdialogtextactivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pushdialogtextactivity = this;

		setContentView(R.layout.activity_push_dialog_t_out);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		// 키잠금 해제하기
		// 화면 켜기
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		String pushTitle = getIntent().getExtras().getString("wakePushTitle");
		String pushMsg = getIntent().getExtras().getString("wakePushMsg");
		pushTarget = getIntent().getExtras().getString("wakePushTarget");
		pushFullTarget = getIntent().getExtras().getString("wakePushFullTarget");

		RelativeLayout backGround = (RelativeLayout) findViewById(R.id.push_inner_background);
		backGround.setBackgroundColor(Color.argb(100, 0, 0, 0));

		final RelativeLayout backGround_out = (RelativeLayout) findViewById(R.id.push_inner_background_out);
		backGround.setBackgroundColor(Color.argb(100, 0, 0, 0));

		TextView PushTitle = (TextView) findViewById(R.id.pushTitle);
		PushTitle.setPaintFlags(PushTitle.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

		TextView PushMsg = (TextView) findViewById(R.id.pushMsg);

		PushTitle.setText(pushTitle);
		PushTitle.setTextColor(Color.rgb(239, 65, 35));
		PushMsg.setText(pushMsg);

		TextView PushCancel = (TextView) findViewById(R.id.push_Cancel);
		PushCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				mHandler.removeMessages(0);

			}
		});

		// P0001 메인화면
		// P0002 스토어
		// P0003 장바구니
		// P0004 주문배송
		// P0005 쿠폰관리
		// P0006 공지사항
		// P0007 이벤트

		TextView PushOk = (TextView) findViewById(R.id.pushOk);
		PushOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (Config.isAppProcess()) {

					backGround_out.setVisibility(View.INVISIBLE);

					showAlert();

				} else {

					goIntentTarget();

				}

			}
		});

		mHandler.sendEmptyMessage(0);

	}

	public void showAlert() {
		MessageUtil.alertnoTitle(PushDialogTextActivity.this, " " + getString(R.string.confirm_move_page_and_dont_save_editing_info), new ICustomDialogListener() {
			
			@Override
			public void onClick(byte clickedOk) {
				if(clickedOk == ICustomDialogListener.OK)
					goIntentTarget();
				else {
					finish();
					mHandler.removeMessages(0);
				}
			}
		});
	}
	
	

	public void goIntentTarget() {
		Intent goHomeIntent = new Intent(PushDialogTextActivity.this, RenewalHomeActivity.class);
		goHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Dlog.d("goIntentTarget() " + pushTarget);

		goHomeIntent.putExtra("gototarget", pushTarget);
		goHomeIntent.putExtra("fullurl", pushFullTarget);

		startActivity(goHomeIntent);
		finish();
		mHandler.removeMessages(0);
	}

	private int value = 0;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			value++;

			mHandler.sendEmptyMessageDelayed(0, 1000);

			if (value == 30) {
				finish();
			}

		}
	};

	@Override
	protected void onDestroy() {
		pushdialogtextactivity = null;
		super.onDestroy();
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
