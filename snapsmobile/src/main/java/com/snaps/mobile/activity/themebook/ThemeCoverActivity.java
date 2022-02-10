package com.snaps.mobile.activity.themebook;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import errorhandle.logger.Logg;

import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCover;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCover.ThemeCover;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.OrientationManager.OrientationChangeListener;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.adapter.ThemeBookCoverAdapter;
import com.snaps.mobile.activity.themebook.holder.ThemeCoverHolder;

import java.util.ArrayList;
import java.util.List;

import errorhandle.CatchActivity;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class ThemeCoverActivity extends CatchActivity implements OrientationChangeListener {

	TextView mNextBtn;
	ImageView mPreBtn;
	TextView mThemeTitle;

	GridView gridPhoneList;
	public Xml_ThemeCover xmlThemeCover;
	public boolean isSelect = false;
	ThemeBookCoverAdapter phoneListAdapter;
	ThemeCoverHolder vh;
	String mTitle = "";
	String mThemeCoverID = "";
	public float mRatio = 0.0f;

	boolean isReturnType = false;// false이면 커버아이디, true이면 커버 xml path를 리턴한다.
	boolean isLeatherCover = false;
	
	boolean m_isLandScapeMode = false;
	boolean m_isBlockOrientationChange = false;
	private OrientationManager orientationManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		m_isLandScapeMode = UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this);
		if (m_isLandScapeMode) {
			UIUtil.updateFullscreenStatus(this, true);
		} else {
			UIUtil.updateFullscreenStatus(this, false);
		}
		
		if(m_isLandScapeMode)
			setContentView(R.layout.activity_theme_cover_landscape);
		else
			setContentView(R.layout.activity_theme_cover);

		isLeatherCover = getIntent().getBooleanExtra("leatherCover", false);

		mTitle = getIntent().getStringExtra("themetitle");
		isReturnType = getIntent().getBooleanExtra("returnType", false);

		if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook())
			mRatio = getIntent().getFloatExtra("simplecoverRatio", 0.0f);
		
		recoveryViews();

		ATask.executeVoidDefProgress(ThemeCoverActivity.this, new ATask.OnTask() {

			@Override
			public void onPre() {
				setEnableOrientationSensor(false);
			}

			@Override
			public void onBG() {

				String prmTmplClssCode = null;
				if (isLeatherCover)
					prmTmplClssCode = "045003";

				xmlThemeCover = GetParsedXml.getThemeBookCover(Config.getPROD_CODE(), prmTmplClssCode, isLeatherCover, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			}

			@Override
			public void onPost() {
				setEnableOrientationSensor(true);
				if (xmlThemeCover == null)
					Toast.makeText(ThemeCoverActivity.this, R.string.loading_fail, Toast.LENGTH_LONG).show();
				else
					initLayout();
			}
		});
	}
	
	private void recoveryViews() {

		mThemeTitle = (TextView) findViewById(R.id.ThemeTitleText);
		mThemeTitle.setText(getString(R.string.edit_cover));
		mNextBtn = (TextView) findViewById(R.id.ThemebtnTopNext);
		mNextBtn.setText(getString(R.string.confirm));
		mPreBtn = (ImageView) findViewById(R.id.ThemeTitleLeft);
		
		if(findViewById(R.id.ThemeTitleLeftLy) != null) {
			findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}

		gridPhoneList = (GridView) findViewById(R.id.gridCoverList);
		
		mPreBtn.setOnClickListener(v -> onBackPressed());

		mNextBtn.setOnClickListener(v -> {
            ThemeCover coverData = getSelectedCoverData();

            if (coverData != null) {
                Intent data = new Intent();
                // xml path를 반
                if (isReturnType)
                    data.putExtra("coverXMLPATH", coverData.F_XML_PATH);
                else
                    data.putExtra("coverID", coverData.F_TMPL_ID);

                Config.setTMPL_COVER(coverData.F_TMPL_CODE);

                setResult(RESULT_OK, data);
                finish();
            } else {
                // 커버를 선택하지 않았다는 토스트 메세지 띄위기....
                MessageUtil.toast(ThemeCoverActivity.this, R.string.theme_cover_select);

            }

        });
		
		gridPhoneList.setFocusable(false);
		gridPhoneList.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		gridPhoneList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// view 가져오
				vh = (ThemeCoverHolder) view.getTag();
				// data
				ThemeCover d = xmlThemeCover.bgList.get(position);

				// 기존에 선택이 되었던걸 비선택을 한다.
				if (position != selectedIndex && selectedIndex >= 0) {
					ThemeCover seletedData = xmlThemeCover.bgList.get(selectedIndex);
					seletedData.F_IS_SELECT = false;
				}

				// 선택이 되어있으면 비선택
				if (d.F_IS_SELECT) {
					d.F_IS_SELECT = false;
					mThemeCoverID = "";

				} else {

					mThemeCoverID = d.F_TMPL_ID;
					d.F_IS_SELECT = true;
					selectedIndex = position;

					selectedView = vh;
				}

				int start = gridPhoneList.getFirstVisiblePosition();
				int end = gridPhoneList.getLastVisiblePosition();
				if (selectedIndex != position && selectedView != null && selectedIndex >= start && selectedIndex <= end) {
					selectedView.imgCoverAlbum.setAlpha(255);
					selectedView.imgCoverSelect.setVisibility(View.INVISIBLE);
				}

				phoneListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public void onOrientationChanged(final int newOrientation) {
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(orientationManager != null)
			orientationManager.removeOpserver(this);
	}

	private void setEnableOrientationSensor(boolean enable) {
		if(enable) {
			m_isBlockOrientationChange = false;
			if(orientationManager != null)
				orientationManager.enable();
		} else {
			m_isBlockOrientationChange = true;
			if(orientationManager != null)
				orientationManager.disable();
		}
	}
	
	private void changeRotatedLayout() {
		if(gridPhoneList == null || phoneListAdapter == null || orientationManager == null) return;
		
		if(orientationManager.getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			if(!m_isLandScapeMode) {
				m_isLandScapeMode = true;					
			}
		} else {
			if(m_isLandScapeMode) {
				m_isLandScapeMode = false;					
			}
		}
		
		if(m_isLandScapeMode)
			setContentView(R.layout.activity_theme_cover_landscape);
		else
			setContentView(R.layout.activity_theme_cover);
		
		recoveryViews();
			
		int colsNums = m_isLandScapeMode ? 4 : 2;
		int columnWidth = UIUtil.getCalcWidth(this, colsNums, m_isLandScapeMode);
		gridPhoneList.setNumColumns(colsNums);
		gridPhoneList.setColumnWidth(columnWidth);
		phoneListAdapter.setGridColumnWidth(m_isLandScapeMode);
		gridPhoneList.setAdapter(phoneListAdapter);
		phoneListAdapter.notifyDataSetChanged();
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
	ThemeCoverHolder selectedView = null;

	void initLayout() {
		phoneListAdapter = new ThemeBookCoverAdapter(ThemeCoverActivity.this);

		int colsNums = m_isLandScapeMode ? 4 : 2;
		int columnWidth = UIUtil.getCalcWidth(this, colsNums, m_isLandScapeMode);
		gridPhoneList.setNumColumns(colsNums);
		gridPhoneList.setColumnWidth(columnWidth);
		phoneListAdapter.setGridColumnWidth(m_isLandScapeMode);
		gridPhoneList.setAdapter(phoneListAdapter);
	}

	/***
	 * 
	 * @return
	 */
	ThemeCover getSelectedCoverData() {
		if (selectedIndex == -1)
			return null;

		return xmlThemeCover.bgList.get(selectedIndex);
	}

}
