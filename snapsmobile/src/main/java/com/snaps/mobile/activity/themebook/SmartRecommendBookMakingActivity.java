package com.snaps.mobile.activity.themebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.sound.SnapsSoundPlayer;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SmartRecommendBookMainActivity;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;

import errorhandle.CatchFragmentActivity;
import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

public class SmartRecommendBookMakingActivity extends CatchFragmentActivity implements ISnapsHandler, View.OnClickListener {
	private static final String TAG = SmartRecommendBookMakingActivity.class.getSimpleName();
	public enum eAnalysisFragment {
		SMART_ANALYSIS_MAKING_FRAGMENT,
	}

	private SnapsHandler snapsHandler = null;

	private SmartRecommendBookMakingFragment makingFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setContentView(R.layout.smart_snaps_analysis_making_activity);

		initialize();
	}

	private void initialize() {
		snapsHandler = new SnapsHandler(this);

		replaceFragment(eAnalysisFragment.SMART_ANALYSIS_MAKING_FRAGMENT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			SnapsSoundPlayer.registerRingerModeStateChangeReceiver(this);

			SnapsSoundPlayer.registerSettingsContentObserver(this);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			SnapsSoundPlayer.unregisterRingerModeStateChangeReceiver(this);

			SnapsSoundPlayer.unregisterSettingsContentObserver(this);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	public void onBackPressed() {
		showCancelConfirm();
	}

	@Override
	public void onClick(View v) {
		if (v == null) return;
		UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

		if (v.getId() == R.id.ThemeTitleLeftLy || (v.getId() == R.id.ThemeTitleLeft)) {
			showCancelConfirm();
		}
	}

	public void showCancelConfirm() {
		MessageUtil.alertnoTitle(this, getString(R.string.smart_analysis_product_cancel_making_confirm_msg), new ICustomDialogListener() {
			@Override
			public void onClick(byte clickedOk) {
				if (clickedOk == ICustomDialogListener.OK) {
					getBackToPrevActivity();
				}
			}
		});
	}

	private void getBackToPrevActivity() {
		SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_make_clickBack)
				.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

//		작업 캔슬 시키자...
		if (makingFragment != null) {
			try {
				makingFragment.cancelTasks();

				makingFragment.finishActivity();

				Config.setPROJ_CODE("");
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

		Intent intent = new Intent(this, ImageSelectActivityV2.class);
		ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
				.setComebackFromEditActivity(true)
				.setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_RECOMMEND_BOOK_PRODUCT)
				.setHomeSelectProduct(Config.SELECT_SMART_ANALYSIS_PHOTO_BOOK)
				.setHomeSelectProductCode(Config.getPROD_CODE())
				.setHomeSelectKind("").create();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
		intent.putExtras(bundle);
		startActivity(intent);

		this.finish();

		this.overridePendingTransition(0, R.anim.anim_fade_out);
	}

	private void requestUpdateProgressWithMessage(Message msg) {
		if (msg == null || makingFragment == null) return;
		try {
			makingFragment.updateProgressValue((Integer) msg.obj, msg.arg1, msg.arg2);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void replaceFragment(eAnalysisFragment fragment) {
		int animIn = getIntent().getIntExtra(Const_VALUES.EXTRAS_HAMBURGER_MENU_START_ANIM_IN, R.anim.anim_fade_in);
		int animOut = getIntent().getIntExtra(Const_VALUES.EXTRAS_HAMBURGER_MENU_START_ANIM_OUT, 0);

		switch (fragment) {
			case SMART_ANALYSIS_MAKING_FRAGMENT:
				replaceSmartAnalysisMakingFragment(animIn, animOut);
				break;
		}
	}

	private void replaceSmartAnalysisMakingFragment(int animIn, int animOut) {
		makingFragment = SmartRecommendBookMakingFragment.newInstance(snapsHandler);
		FragmentUtil.replce(R.id.smart_snaps_analysis_making_activity_fragment_layout, this, makingFragment, null, animIn, animOut);
	}

	/**
	 * 액티비티를 완전히 닫고 나서 액티비티 이동 처리
	 */
	public static final int HANDLE_MSG_CHANGE_TITLE_TEXT = 0;
	public static final int HANDLE_MSG_CANCEL_CONFIRM = 1;
	public static final int HANDLE_MSG_START_NEXT_ACTIVITY = 2;
	public static final int HANDLE_MSG_FINISH_ACTIVITY = 3;
	public static final int HANDLE_MSG_CONSECUTIVE_UPDATE_PROGRESS_BAR = 4;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case HANDLE_MSG_CHANGE_TITLE_TEXT:
				changeTitleTextWithMessage(msg);
				break;
			case HANDLE_MSG_CANCEL_CONFIRM:
				SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_make_clickCancel)
						.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

				showCancelConfirm();
				break;
			case HANDLE_MSG_START_NEXT_ACTIVITY:
				startEditActivity();
				break;
			case HANDLE_MSG_FINISH_ACTIVITY:
				getBackToPrevActivity();
				break;
			case HANDLE_MSG_CONSECUTIVE_UPDATE_PROGRESS_BAR:
				requestUpdateProgressWithMessage(msg);
				break;
		}
	}

	private void changeTitleTextWithMessage(Message msg) {
		if (msg == null) return;
		try {
			Object titleObj = msg.obj;
			if (titleObj != null && titleObj instanceof String) {
				TextView themeTitle = (TextView) findViewById(R.id.ThemeTitleText);
				if (themeTitle != null) {
					themeTitle.setText((String)titleObj);
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(this, e);
		}
	}

	private void startEditActivity() {
		Intent intent = new Intent(this, SmartRecommendBookMainActivity.class);
//		saveIntent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SMART_ANALYSIS_PHOTO_BOOK.ordinal());
//		saveIntent.putExtra("templete", TEMPLATE_PATH);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

		startActivity(intent);
		finish();
	}
}
