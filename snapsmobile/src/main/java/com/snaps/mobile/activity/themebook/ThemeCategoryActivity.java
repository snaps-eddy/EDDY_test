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

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCategory;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.adapter.ThemeBookCategoryAdapter;

import errorhandle.CatchActivity;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class ThemeCategoryActivity extends CatchActivity {

	TextView mNextBtn;
	ImageView mPreBtn;
	TextView mThemeTitle;
	
	GridView mGridCategory;
	public Xml_ThemeCategory xmlThemeCategory;
	public boolean isSelect = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
		
		boolean isLandScapeMode = UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this);
		
		setContentView(isLandScapeMode ? R.layout.activity_theme_category_landscape : R.layout.activity_theme_category);

		mThemeTitle = (TextView) findViewById(R.id.ThemeTitleText);
		mThemeTitle.setText("컨텐츠 디자인 선택");
		mNextBtn = (TextView) findViewById(R.id.ThemebtnTopNext);
		mNextBtn.setVisibility(View.GONE);
		mPreBtn = (ImageView) findViewById(R.id.ThemeTitleLeft);
		
		if(findViewById(R.id.ThemeTitleLeftLy) != null) {
			findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}
		
		mGridCategory = (GridView) findViewById(R.id.gridCategoryList);

		mPreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onBackPressed();

			}
		});

		ATask.executeVoidDefProgress(ThemeCategoryActivity.this, new ATask.OnTask() {

			@Override
			public void onPre() {

			}

			@Override
			public void onBG() {

				xmlThemeCategory = GetParsedXml.getThemeBookCategory(SnapsInterfaceLogDefaultHandler.createDefaultHandler());

			}

			@Override
			public void onPost() {

				if (xmlThemeCategory == null)
					Toast.makeText(ThemeCategoryActivity.this, R.string.loading_fail, Toast.LENGTH_LONG).show();
				else
					initLayout();

			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (getIntent().hasExtra("onFinishImageLoad")) {
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	void initLayout() {
		ThemeBookCategoryAdapter categortAdapter = new ThemeBookCategoryAdapter(ThemeCategoryActivity.this);

		int columnWidth = UIUtil.getCalcWidth(ThemeCategoryActivity.this, Const_VALUE.IMAGE_ALBUM_COLS);

		mGridCategory.setColumnWidth(columnWidth);
		mGridCategory.setAdapter(categortAdapter);
		mGridCategory.setFocusable(false);
		mGridCategory.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		mGridCategory.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(ThemeCategoryActivity.this, ThemeContensActivity.class);
				intent.putExtra("category_code", xmlThemeCategory.bgList.get(position).F_CATEGORY_CODE);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finish();
		}
	}

}
