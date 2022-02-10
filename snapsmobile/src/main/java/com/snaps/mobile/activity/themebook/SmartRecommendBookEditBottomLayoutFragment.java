package com.snaps.mobile.activity.themebook;

import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.net.xml.bean.XML_BasePage;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCover;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SnapsCustomLinearLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SnapsCustomLinearSpacingItemDecoration;
import com.snaps.mobile.activity.themebook.adapter.SmartRecommendBookEditorBottomLayoutListAdapter;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import errorhandle.SnapsAssert;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA;
import static com.snaps.mobile.activity.themebook.SmartRecommendBookPageEditActivity.HANDLE_MSG_CHANGE_LAYOUT;


public class SmartRecommendBookEditBottomLayoutFragment extends Fragment implements View.OnClickListener {
	private SnapsHandler snapsHandler = null;

	private ArrayList<XML_BasePage> layoutList = null;
	private SmartRecommendBookEditorBottomLayoutListAdapter layoutAdapter = null;
	private XML_BasePage originalLayout = null;
	private SnapsPageEditRequestInfo editRequestInfo = null;

	public static SmartRecommendBookEditBottomLayoutFragment newInstance(SnapsHandler snapsHandler) {
		SmartRecommendBookEditBottomLayoutFragment fragment = new SmartRecommendBookEditBottomLayoutFragment();
		fragment.setSnapsHandler(snapsHandler);
		return fragment;
	}

	public void setSnapsHandler(SnapsHandler snapsHandler) {
		this.snapsHandler = snapsHandler;
	}

	public SmartRecommendBookEditBottomLayoutFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fetchIntentInfo();

		layoutList = getLayoutList();
		SnapsAssert.assertNotNull(layoutList);
		if (layoutList == null) {
			return;
		}

		//아래 순서를 변경하지 말 것.
		initBaseMultiformFlag();

		initSelectedItemOnLayoutList();

		sortLayoutListByPriority();

		originalLayout = findCurrentSelectedDesignInLayoutList();
	}

	private void fetchIntentInfo() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			bundle.setClassLoader( MyPhotoSelectImageData.class.getClassLoader() );
			editRequestInfo = (SnapsPageEditRequestInfo) bundle.getSerializable(EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA);
		}
	}

	private ArrayList<XML_BasePage> getLayoutList() {
		if (editRequestInfo == null) return null;

		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		if (editRequestInfo.isCover()) {
			Xml_ThemeCover cover = smartSnapsManager.getCoverDesignListOfAnalysisPhotoBook();
			if (cover != null && cover.bgList != null)  return (ArrayList<XML_BasePage>) cover.bgList.clone();
		} else if (editRequestInfo.isTitlePage()) {
			return smartSnapsManager.getIndexDesignListOfAnalysisPhotoBook();
		} else {
			return smartSnapsManager.getPageDesignListOfAnalysisPhotoBook(editRequestInfo);
		}

		return null;
	}

	private void initBaseMultiformFlag() {
		if (layoutList == null || layoutList.isEmpty()) return;

		String baseMultiformId = editRequestInfo.getBaseMultiformId();
		if (baseMultiformId == null) return;

		final String prefixSS_ = "SS_";

		for (XML_BasePage layout : layoutList) {
			if (layout != null) {
				String templateId = layout.F_TMPL_ID;
				if (!StringUtil.isEmpty(templateId)) {
                    if (!baseMultiformId.equalsIgnoreCase(templateId) && templateId.startsWith(prefixSS_)) {
						templateId = templateId.substring(prefixSS_.length());
					}
				}

				if (baseMultiformId.equalsIgnoreCase(templateId)) {
					layout.F_IS_BASE_MULTIFORM = true;
				} else {
					layout.F_IS_BASE_MULTIFORM = false;
				}
			}
		}
	}

	private void initSelectedItemOnLayoutList() {
		if (layoutList == null || layoutList.isEmpty() || editRequestInfo == null) return;

		//현재 선택되어 있는 레이아웃이 있다면, 앞으로 옮긴다..
		//없으면, 최초 템플릿에 기록된 레이아웃을 앞으로 옮긴다.
		String currentMultiformId = editRequestInfo.getCurrentPageMultiformId();
		if (currentMultiformId == null) return;

		final String prefixSS_ = "SS_";

		for (XML_BasePage layout : layoutList) {
			if (layout != null) {
				String templateId = layout.F_TMPL_ID;
				if (!StringUtil.isEmpty(templateId)) {
                    if (!currentMultiformId.equalsIgnoreCase(templateId) && templateId.startsWith(prefixSS_)) {
						templateId = templateId.substring(prefixSS_.length());
					}
				}

				if (currentMultiformId.equalsIgnoreCase(templateId)) {
					layout.F_IS_SELECT = true;
					layout.F_SORT_PRIORITY = 0;
				} else {
					layout.F_IS_SELECT = false;

					if (layout.F_IS_BASE_MULTIFORM) {
						layout.F_SORT_PRIORITY = 1;
					} else {
						layout.F_SORT_PRIORITY = Integer.MAX_VALUE;
					}
				}
			}
		}
	}

	private void sortLayoutListByPriority() {
		if (layoutList == null || layoutList.size() < 2) return;

		Collections.sort(layoutList, new Comparator<XML_BasePage>() {
			@Override
			public int compare(XML_BasePage lPage, XML_BasePage rPage) {
				return lPage.F_SORT_PRIORITY <  rPage.F_SORT_PRIORITY ? -1 : (lPage.F_SORT_PRIORITY >  rPage.F_SORT_PRIORITY ? 1: 0);
			}
		});

	}

	private XML_BasePage findCurrentSelectedDesignInLayoutList() {
		if (layoutList == null || layoutList.isEmpty()) return null;

		for (XML_BasePage layout : layoutList) {
			if (layout != null && layout.F_IS_SELECT) {
				return layout;
			}
		}
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.smart_snaps_analysis_edit_bottom_layout_fragment, container, false);

		RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.smart_snaps_analysis_edit_cover_bottom_layout_fragment_recyclerView);
		if (recyclerView != null) {
			SnapsCustomLinearLayoutManager linearLayoutManager = new SnapsCustomLinearLayoutManager(getActivity());
			linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
			recyclerView.setLayoutManager(linearLayoutManager);
			SnapsCustomLinearSpacingItemDecoration itemDecoration = new SnapsCustomLinearSpacingItemDecoration(getActivity());
			recyclerView.addItemDecoration(itemDecoration);
			recyclerView.setAdapter(createBottomLayoutListAdapter());
		}

		return v;
	}

	private SmartRecommendBookEditorBottomLayoutListAdapter createBottomLayoutListAdapter() {
		layoutAdapter = new SmartRecommendBookEditorBottomLayoutListAdapter(getActivity(), false);
		layoutAdapter.setData(layoutList);
		layoutAdapter.setItemClickListener(new SnapsCommonResultListener<XML_BasePage>() {
			@Override
			public void onResult(XML_BasePage themeCover) {
				onCoverItemClick(themeCover);
			}
		});

		return layoutAdapter;
	}

	private void onCoverItemClick(XML_BasePage selectCover) {
		if (layoutList == null) return;

		for (XML_BasePage cover : layoutList) {
			if (cover == null) continue;
			cover.F_IS_SELECT = cover == selectCover;
		}

		if (editRequestInfo != null)
			editRequestInfo.setCurrentPageMultiformId(selectCover.F_TMPL_ID);

		if (layoutAdapter != null)
			layoutAdapter.notifyData(layoutList);

		requestChangeDesign(selectCover);
	}

	private void requestChangeDesign(XML_BasePage selectLayout) {
		Message msg = new Message();
		msg.what = HANDLE_MSG_CHANGE_LAYOUT;

		if (isChangedCover()) {
			msg.obj = selectLayout;
			originalLayout = selectLayout;
		}

		snapsHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {}

	private boolean isChangedCover() {
		XML_BasePage currentSelectedCover = findCurrentSelectedDesignInLayoutList();
		return currentSelectedCover != originalLayout;
	}
}