package com.snaps.mobile.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.SnapsBaseActivity;
import com.snaps.mobile.activity.home.RenewalHomeActivity;

import errorhandle.logger.Logg;

@SuppressLint("HandlerLeak")
public class PushDialogImageActivity extends SnapsBaseActivity {
	private static final String TAG = PushDialogImageActivity.class.getSimpleName();
	String pushTarget = "";
	String pushFullTarget = "";
	boolean isRunning = false;

	public static PushDialogImageActivity pushdialogimageactivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pushdialogimageactivity = this;

		setContentView(R.layout.activity_push_dialog_i_out);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		// 키잠금 해제하기
		// 화면 켜기
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		String pushImagePath = getIntent().getExtras().getString("wakePushImage");
		pushTarget = getIntent().getExtras().getString("wakePushTarget");
		pushFullTarget = getIntent().getExtras().getString("wakePushFullTarget");

		RelativeLayout backGround = (RelativeLayout) findViewById(R.id.push_inner_background);
		backGround.setBackgroundColor(Color.argb(100, 0, 0, 0));

		final RelativeLayout backGround_out = (RelativeLayout) findViewById(R.id.push_inner_background_out);
		backGround_out.setBackgroundColor(Color.argb(100, 0, 0, 0));

		ImageView PushImage = (ImageView) findViewById(R.id.push_circle);

		Dlog.d("onCreate() pushImagePath:" + pushImagePath + ", pushTarget:" + pushTarget);

		ImageLoader.with(this).load("http://" + pushImagePath).into(PushImage);

		ImageView PushCancel = (ImageView) findViewById(R.id.push_circle_cancel);
		PushCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				mHandler.removeMessages(0);

			}
		});
		ImageView PushOk = (ImageView) findViewById(R.id.push_circle_agree);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		pushdialogimageactivity = null;
	}

	public void showAlert() {
		MessageUtil.alertnoTitle(PushDialogImageActivity.this, " " + getString(R.string.confirm_move_page_and_dont_save_editing_info), new ICustomDialogListener() {
			
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
		Intent goHomeIntent = new Intent(PushDialogImageActivity.this, RenewalHomeActivity.class);
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
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
