package com.snaps.mobile.activity.themebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeContents;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeContents.ThemeContents;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.adapter.ThemeBookContentsAdapter;
import com.snaps.mobile.activity.themebook.holder.ThemeContentsHolder;

import errorhandle.CatchActivity;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class ThemeContensActivity extends CatchActivity {
	private static final String TAG = ThemeContensActivity.class.getSimpleName();
	TextView mNextBtn;
	ImageView mPreBtn;
	TextView mThemeTitle;

	GridView mGridContents;
	public Xml_ThemeContents xmlThemeContents;
	public String mCategory = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
		
		boolean isLandScapeMode = UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this);
		
		setContentView(isLandScapeMode ? R.layout.activity_theme_contents_landscape : R.layout.activity_theme_contents);

		mThemeTitle = (TextView) findViewById(R.id.ThemeTitleText);
		mThemeTitle.setText(getString(R.string.contents_design_title_text));//"컨텐츠 디자인 선택");
		mNextBtn = (TextView) findViewById(R.id.ThemebtnTopNext);
		mNextBtn.setText(getString(R.string.confirm));//"확인");
		mPreBtn = (ImageView) findViewById(R.id.ThemeTitleLeft);
		
		if(findViewById(R.id.ThemeTitleLeftLy) != null) {
			findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}

		mGridContents = (GridView) findViewById(R.id.gridContentsList);

		mCategory = getIntent().getStringExtra("category_code");

		Dlog.d("onCreate() category:" + mCategory);

		mPreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onBackPressed();

			}
		});

		// 완료버튼 처리함수...
		mNextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (selectedIndex > -1) {

					// MyPhotoSelectedData를 넘겨야 함...

					Intent intent = new Intent();
					
					Bundle bundle = new Bundle();
					bundle.putParcelable("imgData", makeMyPhotoData());
					intent.putExtras(bundle);
					
					setResult(RESULT_OK, intent);
					finish();
				} else {
				}

			}
		});

		ATask.executeVoidDefProgress(ThemeContensActivity.this, new ATask.OnTask() {

			@Override
			public void onPre() {

			}

			@Override
			public void onBG() {

				xmlThemeContents = GetParsedXml.getThemeBookContents(mCategory, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

			}

			@Override
			public void onPost() {

				if (xmlThemeContents == null)
					Toast.makeText(ThemeContensActivity.this, R.string.loading_fail, Toast.LENGTH_LONG).show();
				else
					initLayout();

			}

		});

	}

	private MyPhotoSelectImageData makeMyPhotoData() {

		ThemeContents d = xmlThemeContents.bgList.get(selectedIndex);
		MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
		imgData.KIND = Const_VALUES.SELECT_CONTENT;
		imgData.F_IMG_NAME = d.F_RSRC_NAME;
		imgData.PATH = d.F_EIMG_PATH;
		imgData.THUMBNAIL_PATH = d.F_DIMG_PATH;
		return imgData;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	int selectedIndex = -1; // -1선택이 되어있지 않을상태... 최소 0시작
	ThemeContentsHolder selectedView = null;

	void initLayout() {
		final ThemeBookContentsAdapter contentsAdapter = new ThemeBookContentsAdapter(ThemeContensActivity.this);

		int columnWidth = UIUtil.getCalcWidth(ThemeContensActivity.this, Const_VALUE.IMAGE_ALBUM_COLS);

		mGridContents.setColumnWidth(columnWidth);
		mGridContents.setAdapter(contentsAdapter);
		mGridContents.setFocusable(false);
		mGridContents.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		mGridContents.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ThemeContentsHolder vh = (ThemeContentsHolder) view.getTag();
				// data
				ThemeContents d = xmlThemeContents.bgList.get(position);

				if (position != selectedIndex && selectedIndex >= 0) {
					ThemeContents seletedData = xmlThemeContents.bgList.get(selectedIndex);
					seletedData.F_IS_SELECT = false;
				}

				if (d.F_IS_SELECT) {
					d.F_IS_SELECT = false;
					if (selectedIndex == position)
						selectedIndex = -1;
				} else {

					d.F_IS_SELECT = true;
					selectedIndex = position;

					selectedView = vh;
				}

				int start = mGridContents.getFirstVisiblePosition();
				int end = mGridContents.getLastVisiblePosition();
				if (selectedIndex != position && selectedView != null && selectedIndex >= start && selectedIndex <= end) {
					selectedView.imgCoverAlbum.setAlpha(255);
					selectedView.imgCoverSelect.setVisibility(View.INVISIBLE);
				}

				contentsAdapter.notifyDataSetChanged();

			}
		});
	}

}
