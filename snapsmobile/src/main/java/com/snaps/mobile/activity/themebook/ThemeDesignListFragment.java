package com.snaps.mobile.activity.themebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kmshack.newsstand.ScrollTabHolderFragment;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.themebook.adapter.ThemeBookPageAdapter;
import com.snaps.mobile.activity.themebook.holder.ThemeCoverHolder;

public class ThemeDesignListFragment extends ScrollTabHolderFragment {

	boolean m_isLandScapeMode = false;
	public DialogDefaultProgress pageProgress;
	private ThemeCoverHolder vh;
	int selectedIndex = -1; // -1선택이 되어있지 않을상태... 최소 0시작
	ThemeCoverHolder selectedView = null;

	ThemeDesignListActivity.eDesignPhotoCnt designPhotoCnt = null;
	ThemeDesignListActivity designListActivity = null;
	ThemeBookPageAdapter designGridAdapter = null;

	public static ThemeDesignListFragment newInstance(ThemeDesignListActivity designListActivity, ThemeDesignListActivity.eDesignPhotoCnt eDesignPhotoCnt) {
		ThemeDesignListFragment fragment = new ThemeDesignListFragment();
		fragment.designListActivity = designListActivity;
		fragment.designPhotoCnt = eDesignPhotoCnt;
		return fragment;
	}

	public ThemeDesignListFragment() {}

	public void set(boolean landscapeMode, DialogDefaultProgress pageProgress, ThemeCoverHolder holder) {
		this.m_isLandScapeMode = landscapeMode;
		this.pageProgress = pageProgress;
		this.vh = holder;
	}

	public Xml_ThemePage.ThemePage getDesignItem(int pos) {
		if(designListActivity == null || designListActivity.getCurrentDesignList() == null || designListActivity.getCurrentDesignList().size() <= pos) return null;
		return designListActivity.getCurrentDesignList().get(pos);
	}

	public void setSelectedIndex(int idx) {
		this.selectedIndex = idx;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void notifyDataSetChanged() {
		if (designGridAdapter != null)
			designGridAdapter.notifyDataSetChanged();
	}

	public void destroy() {
		if (designGridAdapter != null)
			designGridAdapter.destroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int res = m_isLandScapeMode ? R.layout.activity_theme_design_grid_landscape : R.layout.activity_theme_design_grid;
		View view = inflater.inflate(res, null);
		GridView gridPhoneList = (GridView) view.findViewById(R.id.gridCoverList);
		designGridAdapter = new ThemeBookPageAdapter(getActivity(), designPhotoCnt);
		if(gridPhoneList != null) {

			designGridAdapter.setProgress(pageProgress);

			int colsNums = m_isLandScapeMode ? 3 : 2;
			int columnWidth = UIUtil.getCalcWidth(getActivity(), colsNums, m_isLandScapeMode);
			gridPhoneList.setNumColumns(colsNums);
			gridPhoneList.setColumnWidth(columnWidth);
			designGridAdapter.setLandscapeMode(m_isLandScapeMode);
			designGridAdapter.setGridColumnWidth();
			gridPhoneList.setAdapter(designGridAdapter);

			gridPhoneList.setFocusable(false);
			gridPhoneList.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
			gridPhoneList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					// view 가져오
					vh = (ThemeCoverHolder) view.getTag(); //FIXME 홀
					// data
					Xml_ThemePage.ThemePage d = getDesignItem(position);

					// 기존에 선택이 되었던걸 비선택을 한다.
					if (position != selectedIndex && selectedIndex >= 0) {
						Xml_ThemePage.ThemePage seletedData = getDesignItem(selectedIndex);
						seletedData.F_IS_SELECT = false;
					}

					// 선택이 되어있으면 비선택
					if (d.F_IS_SELECT) {
						d.F_IS_SELECT = false;
						selectedIndex = -1;
					} else {
						d.F_IS_SELECT = true;
						selectedIndex = position;

						selectedView = vh;
					}
					designGridAdapter.notifyDataSetChanged();
				}
			});
		}

		return view;
	}

	@Override
	public void adjustScroll(int scrollHeight) {

	}
}