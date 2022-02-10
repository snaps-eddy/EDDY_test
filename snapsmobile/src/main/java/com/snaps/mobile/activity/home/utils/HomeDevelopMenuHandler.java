package com.snaps.mobile.activity.home.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.core.app.ActivityCompat;
import android.text.InputType;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.develop.SnapsDevelopHelper;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.utils.pref.PrefUtil;

import errorhandle.logger.SnapsLogger;

public class HomeDevelopMenuHandler {
	private static final String TAG = HomeDevelopMenuHandler.class.getSimpleName();
	//CS운영 기능을 잘못 사용하면서 발생하는 실운영 이슈때문에 제거
	private static final int HOME_MENU_ID_CMD_CATCH = 0;
	private static final int HOME_MENU_ID_DELETE_ALL_INTERNAL_MEMORY = 1;
	private static final int HOME_MENU_ID_FORCE_CRASH = 2;
	private static final int HOME_MENU_ID_USE_DRAW_SMART_SNAPS_SEARCH_AREA = 3;

	private final SparseArray<String> sArrayMenu;

	{
		sArrayMenu = new SparseArray<>();
		sArrayMenu.put(HOME_MENU_ID_CMD_CATCH, "CMD 보기");
		sArrayMenu.put(HOME_MENU_ID_DELETE_ALL_INTERNAL_MEMORY, "메모리 삭제");
		sArrayMenu.put(HOME_MENU_ID_USE_DRAW_SMART_SNAPS_SEARCH_AREA, "스마트 검색 영역 표시");
		sArrayMenu.put(HOME_MENU_ID_FORCE_CRASH, "Crash 강제발생");
	}

	private Activity activity;

	public HomeDevelopMenuHandler(Activity activity) {
		this.activity = activity;
	}

	public void showMenu() {
		if (!Config.isDevelopVersion()) {
			return;
		}

		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_single_choice);
		for (int ii = 0; ii < sArrayMenu.size(); ii++) {
			String text = sArrayMenu.valueAt(ii);
			arrayAdapter.add(text);
		}

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int key) {
				performCommendWithKey(key);
			}
		});

		dialogBuilder.show();
	}

	private void performCommendWithKey(int key) {
		switch (key) {
			case HOME_MENU_ID_DELETE_ALL_INTERNAL_MEMORY:
				deleteAllPreferencesForDebug();
				break;
			case HOME_MENU_ID_FORCE_CRASH:
				SnapsLogger.sendDebugLog(); //만약 동작 안하면 딜레이 후에 호출하자.
				break;
			case HOME_MENU_ID_CMD_CATCH:
				enableCMDCatchMode();
				break;
			case HOME_MENU_ID_USE_DRAW_SMART_SNAPS_SEARCH_AREA:
				Config.setUseDrawSmartSnapsSearchArea(true);
				MessageUtil.toast(activity, "active smart snaps search area drawing.");
				break;
		}
	}

	private void showOrgImageAutoSelectPopup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("input auto select count");

		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String str = input.getText().toString();

				Config.setOrgImageAutoSelectCount(Integer.parseInt(str));
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

			builder.show();
	}

	private void deleteAllPreferencesForDebug() {
		try {
			PrefUtil.deleteAllPreferencesForDebug(activity);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void enableCMDCatchMode() {
		SnapsDevelopHelper.IS_USE_CMD_CATCH = true;
	}

	private void forceAppFinish() {
		try {
			GoHomeOpserver.notifyGoHome();

			activity.moveTaskToBack(true);
			ActivityCompat.finishAffinity(activity);

			//강제로 프로세스를 죽이면, 에러 로그 수집이 잘 안된다....
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					android.os.Process.killProcess(android.os.Process.myPid()); //동작 안하면 딜레이...
				}
			});
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

}
