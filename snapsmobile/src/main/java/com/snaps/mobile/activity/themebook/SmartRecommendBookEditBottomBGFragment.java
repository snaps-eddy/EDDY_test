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
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SnapsCustomLinearLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SnapsCustomLinearSpacingItemDecoration;
import com.snaps.mobile.activity.themebook.adapter.SmartRecommendBookEditorBottomBGResListAdapter;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import errorhandle.SnapsAssert;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA;
import static com.snaps.mobile.activity.themebook.SmartRecommendBookPageEditActivity.HANDLE_MSG_CHANGE_BG;


public class SmartRecommendBookEditBottomBGFragment extends Fragment implements View.OnClickListener {
	private SnapsHandler snapsHandler = null;

	private ArrayList<XML_BasePage> bgList = null;
	private SmartRecommendBookEditorBottomBGResListAdapter bgListAdapter = null;
	private XML_BasePage originalBG = null;
	private SnapsPageEditRequestInfo editRequestInfo = null;

	public static SmartRecommendBookEditBottomBGFragment newInstance(SnapsHandler snapsHandler) {
		SmartRecommendBookEditBottomBGFragment fragment = new SmartRecommendBookEditBottomBGFragment();
		fragment.setSnapsHandler(snapsHandler);
		return fragment;
	}

	public void setSnapsHandler(SnapsHandler snapsHandler) {
		this.snapsHandler = snapsHandler;
	}

	public SmartRecommendBookEditBottomBGFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fetchIntentInfo();

		bgList = getBgList();
		SnapsAssert.assertNotNull(bgList);
		if (bgList == null) {
			return;
		}

		//?????? ????????? ???????????? ??? ???.
		initBaseMultiformFlag();

		initSelectedItemOnLayoutList();

		sortLayoutListByPriority();

		originalBG = findCurrentSelectedBGInLayoutList();
	}

	private void fetchIntentInfo() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			bundle.setClassLoader( MyPhotoSelectImageData.class.getClassLoader() );
			editRequestInfo = (SnapsPageEditRequestInfo) bundle.getSerializable(EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA);
		}
	}

	private ArrayList<XML_BasePage> getBgList() {
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		return smartSnapsManager.getPagBGResListOfAnalysisPhotoBook();
	}

	private void initBaseMultiformFlag() {
		if (bgList == null || bgList.isEmpty()) return;

		String basePageBGId = editRequestInfo.getBasePageBGId();
		if (basePageBGId == null) return;

		for (XML_BasePage layout : bgList) {
			if (layout != null) {
				String templateId = layout.F_TMPL_ID;
				if (basePageBGId.equalsIgnoreCase(templateId)) {
					layout.F_IS_BASE_MULTIFORM = true;
				} else {
					layout.F_IS_BASE_MULTIFORM = false;
				}
			}
		}
	}

	private void initSelectedItemOnLayoutList() {
		if (bgList == null || bgList.isEmpty() || editRequestInfo == null) return;

		//?????? ???????????? ?????? ??????????????? ?????????, ????????? ?????????..
		//?????????, ?????? ???????????? ????????? ??????????????? ????????? ?????????.
		String currentPageBGId = editRequestInfo.getCurrentPageBGId();
		if (currentPageBGId == null) return;

		for (XML_BasePage layout : bgList) {
			if (layout != null) {
				String templateId = layout.F_TMPL_ID;

				if (currentPageBGId.equalsIgnoreCase(templateId)) {
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
		if (bgList == null || bgList.size() < 2) return;

		Collections.sort(bgList, new Comparator<XML_BasePage>() {
			@Override
			public int compare(XML_BasePage lPage, XML_BasePage rPage) {
				return lPage.F_SORT_PRIORITY <  rPage.F_SORT_PRIORITY ? -1 : (lPage.F_SORT_PRIORITY >  rPage.F_SORT_PRIORITY ? 1: 0);
			}
		});

	}

	private XML_BasePage findCurrentSelectedBGInLayoutList() {
		if (bgList == null || bgList.isEmpty()) return null;

		for (XML_BasePage layout : bgList) {
			if (layout != null && layout.F_IS_SELECT) {
				return layout;
			}
		}
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.smart_snaps_analysis_edit_bottom_bg_list_fragment, container, false);

		RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.smart_snaps_analysis_edit_cover_bottom_bg_list_fragment_recyclerView);
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

	private SmartRecommendBookEditorBottomBGResListAdapter createBottomLayoutListAdapter() {
		bgListAdapter = new SmartRecommendBookEditorBottomBGResListAdapter(getActivity(), false);
		bgListAdapter.setData(bgList);
		bgListAdapter.setItemClickListener(new SnapsCommonResultListener<XML_BasePage>() {
			@Override
			public void onResult(XML_BasePage themeCover) {
				onBGItemClick(themeCover);
			}
		});

		return bgListAdapter;
	}

	private void onBGItemClick(XML_BasePage selectCover) {
		if (bgList == null) return;

		for (XML_BasePage cover : bgList) {
			if (cover == null) continue;
			cover.F_IS_SELECT = cover == selectCover;
		}

		if (editRequestInfo != null)
			editRequestInfo.setCurrentPageBGId(selectCover.F_TMPL_ID);

		if (bgListAdapter != null)
			bgListAdapter.notifyData(bgList);

		requestChangeBG(selectCover);
	}

	private void requestChangeBG(XML_BasePage selectLayout) {
		Message msg = new Message();
		msg.what = HANDLE_MSG_CHANGE_BG;

		if (isChangedBG()) {
			msg.obj = selectLayout;
			originalBG = selectLayout;
		}

		snapsHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {}

	private boolean isChangedBG() {
		XML_BasePage currentSelectedBG = findCurrentSelectedBGInLayoutList();
		return currentSelectedBG != originalBG;
	}
}