package com.snaps.mobile.activity.themebook;

import android.os.Bundle;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SnapsCustomLinearLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SnapsCustomLinearSpacingItemDecoration;
import com.snaps.mobile.activity.themebook.adapter.SmartRecommendBookEditorBottomPhotoListAdapter;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookEditDragImageInfo;

import static com.snaps.mobile.activity.themebook.SmartRecommendBookPageEditActivity.HANDLE_MSG_START_IMAGE_SELECT_ACTIVITY;
import static com.snaps.mobile.activity.themebook.SmartRecommendBookPageEditActivity.HANDLE_MSG_START_PHOTO_DRAGGING;


public class SmartRecommendBookEditBottomPhotoFragment extends Fragment implements View.OnClickListener {
	private SnapsHandler snapsHandler = null;

	private SmartRecommendBookEditorBottomPhotoListAdapter photoListAdapter = null;
	private RecyclerView recyclerView = null;

	public static SmartRecommendBookEditBottomPhotoFragment newInstance(SnapsHandler snapsHandler) {
		SmartRecommendBookEditBottomPhotoFragment fragment = new SmartRecommendBookEditBottomPhotoFragment();
		fragment.setSnapsHandler(snapsHandler);
		return fragment;
	}

	public void setSnapsHandler(SnapsHandler snapsHandler) {
		this.snapsHandler = snapsHandler;
	}

	public SmartRecommendBookEditBottomPhotoFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.smart_snaps_analysis_edit_bottom_photo_fragment, container, false);

		ImageView addBtn = (ImageView) v.findViewById(R.id.smart_snaps_analysis_edit_cover_bottom_photo_fragment_add_btn);
		if (addBtn != null)
			addBtn.setOnClickListener(this);

		recyclerView = (RecyclerView) v.findViewById(R.id.smart_snaps_analysis_edit_cover_bottom_photo_fragment_recycler_view);
		if (recyclerView != null) {
			SnapsCustomLinearLayoutManager linearLayoutManager = new SnapsCustomLinearLayoutManager(getActivity());
			linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
			recyclerView.setLayoutManager(linearLayoutManager);
			SnapsCustomLinearSpacingItemDecoration itemDecoration = new SnapsCustomLinearSpacingItemDecoration(getActivity());
			recyclerView.addItemDecoration(itemDecoration);
			recyclerView.setAdapter(createBottomPhotoListAdapter());
		}

		return v;
	}

	private SmartRecommendBookEditorBottomPhotoListAdapter createBottomPhotoListAdapter() {
		photoListAdapter = new SmartRecommendBookEditorBottomPhotoListAdapter(getActivity(), false);
		photoListAdapter.setItemDragListener(new SnapsCommonResultListener<SmartRecommendBookEditDragImageInfo>() {
			@Override
			public void onResult(SmartRecommendBookEditDragImageInfo dragPhoto) {
				onItemDragging(dragPhoto);
			}
		});

		return photoListAdapter;
	}

	private void onItemDragging(SmartRecommendBookEditDragImageInfo dragPhoto) {
        if (dragPhoto == null || snapsHandler == null) return;

        Message msg = new Message();
        msg.what = HANDLE_MSG_START_PHOTO_DRAGGING;
        msg.obj = dragPhoto;

        snapsHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

		if (v.getId() == R.id.smart_snaps_analysis_edit_cover_bottom_photo_fragment_add_btn) {
			performAddPhoto();
		}
	}

	private void performAddPhoto() {
		Message msg = new Message();
		msg.what = HANDLE_MSG_START_IMAGE_SELECT_ACTIVITY;

		snapsHandler.sendMessage(msg);
	}

	public void refreshPhotoSelectedState() {
		if (photoListAdapter != null)
			photoListAdapter.notifyAllImageList();
	}

	public void smoothScrollToPosition(int position) {
		if (recyclerView != null)
			recyclerView.smoothScrollToPosition(position);
	}
}