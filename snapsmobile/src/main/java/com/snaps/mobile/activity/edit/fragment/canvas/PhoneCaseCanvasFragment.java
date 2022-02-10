package com.snaps.mobile.activity.edit.fragment.canvas;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.edit.spc.PhoneCaseCanvas;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;

public class PhoneCaseCanvasFragment extends SimplePhotoBookCanvasFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		boolean isFragmentForCartThumbnail = viewPager == null;
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pagecanvas2, container, false);

		if (isFragmentForCartThumbnail) {
			canvas = new PhoneCaseCanvas(getActivity(), true);
			canvas.setId(R.id.fragment_root_view_id);
			rootView.addView(canvas);
			makeSnapsCanvas();
			return rootView;
		}

		canvas = new SnapsCanvasFactory().createPageCanvas(getActivity(), Config.getPROD_CODE());
		canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		canvas.setGravity(Gravity.CENTER);
		canvas.setId(R.id.fragment_root_view_id);
		rootView.addView(canvas);

		boolean isVisibleButton = getArguments().getBoolean("visibleButton", true);
		canvas.setEnableButton(isVisibleButton);

		boolean isPageSaving = getArguments().getBoolean("pageSave", false);
		canvas.setIsPageSaving(isPageSaving);

		if (isPreview) {
			canvas.setZoomable(false);
			canvas.setIsPreview(true);
		}

		canvas.setLandscapeMode(isLandscapeMode);

		canvas.setSnapsPageClickListener(view -> {
			if (itemClickListener != null && canvas != null) {
				itemClickListener.onResult(new SnapsPageEditRequestInfo.Builder().setPageIndex(canvas.getPageNumber()).create());
			}
		});

		makeSnapsCanvas();

		if (viewPager != null) {
			canvas.setViewPager(viewPager);
			viewPager.addCanvas(canvas);
			viewPager.setPreventViewPagerScroll(canvas.isPreventViewPagerScroll());
		}
		return rootView;
	}

}
