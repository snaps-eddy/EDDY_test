package com.snaps.mobile.activity.photoprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTaskResult;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.webview.WebviewActivity;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FTextView;

/**
 * 사진인화
 * 
 * @author jines100
 * 
 */
public class SelectPhotoPrintActivity extends SnapsBaseFragmentActivity {
	private static final String TAG = SelectPhotoPrintActivity.class.getSimpleName();
	// 상수선언
	public static final String PRODUCT_DATA = "product_data";

	private ListView mListView;
	ArrayList<PhotoPrintProductInfo> mData;
	PhotoPrintProductAdapter mAdapter;

	HashMap<String, Integer> mIconInfo = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		// 레이아웃을 설정한다.
		setContentView(R.layout.activity_select_photoprint_list);
		mListView = (ListView) findViewById(R.id.listphoto);
		// 리스너 등록
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				// intent로 데이터 넘기기 구현 하자 Bundle extras
				Intent intent = new Intent(SelectPhotoPrintActivity.this, ImageSelectActivityV2.class);

				// 상품정보를 넘긴다..
				FileUtil.saveInnerFile(SelectPhotoPrintActivity.this, mData.get(position), PRODUCT_DATA);

				intent.putExtra(Const_EKEY.HOME_SELECT_PRODUCT, Config.SELECT_PHOTO_PRINT);

				startActivity(intent);

			}
		});

		// 타이트를 설정
		TextView title = (TextView) findViewById(R.id.txtTitleText);
		title.setText(R.string.select_size);

		ImageView backButton = (ImageView) findViewById(R.id.btnTitleLeft);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		RelativeLayout backButtonLy = (RelativeLayout) findViewById(R.id.btnTitleLeftLy);
		backButtonLy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 상품 안내번튼...
		FTextView detailView = (FTextView) findViewById(R.id.btnProductComment);
		detailView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equalsIgnoreCase(Config.CHANNEL_SNAPS_KOR)) {
					Intent intent = WebviewActivity.getIntent(SelectPhotoPrintActivity.this, getString(R.string.detail_info), SnapsAPI.PRODUCT_PHOTOPRINT_URL());
					startActivity(intent);

				}
				else if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equalsIgnoreCase(Config.CHANNEL_SNAPS_JPN)) {
					Intent intent = WebviewActivity.getIntent(SelectPhotoPrintActivity.this, getString(R.string.detail_info), SnapsAPI.PRODUCT_PHOTOPRINT_URL());
					startActivity(intent);
				}
			}
		});

		// 리스트에 들어가 이미지 설정...
		makeIconInfo();
		// 초기화..
		init();

	}

	void init() {
		ATask.executeBooleanDefProgress(this, new OnTaskResult() {

			@Override
			public void onPre() {

			}

			@Override
			public void onPost(boolean result) {
				if (result) {
					// 리스트 아댑터를 설정한다...
					mAdapter = new PhotoPrintProductAdapter(SelectPhotoPrintActivity.this, mData);
					mListView.setAdapter(mAdapter);
				} else {
					// 파일 파싱 에러...
					// 사진정보 가져오기 실패 토스트 띄우기...
				}

			}

			@Override
			public boolean onBG() {
				// 저장이 된 사진인화 정보 xml를 읽어 온다.
				// 파싱을 해서.. PhotoPrintProductInfo만들어 arraylist에 넣는다.
				try {
                    mData = MenuDataManager.getInstance().getMenuData().photoPrintProductInfoArray;
					for (PhotoPrintProductInfo item : mData) {
						item.productThumbnail = mIconInfo.get(item.productName).intValue();
					}

					return true;
				} catch (Exception e) {
					Dlog.e(TAG, e);
					return false;
				}

			}
		});
	}

	void makeIconInfo() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		try {
			ViewUnbindHelper.unbindReferences(getWindow().getDecorView(), null, false);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
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
