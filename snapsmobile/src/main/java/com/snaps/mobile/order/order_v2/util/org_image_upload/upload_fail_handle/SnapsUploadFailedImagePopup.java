package com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_VALUE;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.CustomGridLayoutManager;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImageData;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImagePopupAttribute;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ysjeong on 2017. 4. 24..
 */

public class SnapsUploadFailedImagePopup extends RelativeLayout {
    private static final String TAG = SnapsUploadFailedImagePopup.class.getSimpleName();

    public interface SnapsUploadFailedImagePopupListener {
        void onShowUploadFailedImagePopup();
        void onSelectedUploadFailedImage(List<MyPhotoSelectImageData> uploadFailedImageList);
    }

    private SnapsUploadFailedImagePopupListener popupListener = null;
    private Activity activity = null;
    private boolean isFinishing;
    private boolean isLandscapeMode;
    private boolean isPhotoPrint;
    private String projCode = null;
    private SnapsSuperRecyclerView recyclerView = null;
    private SnapsUploadFailedImageAdapter photoAdapter = null;

    private SnapsUploadFailedImageSpacingItemDecoration itemDecoration = null;

    public static SnapsUploadFailedImagePopup createInstanceWithPopupListener(SnapsUploadFailedImagePopupAttribute attribute, SnapsUploadFailedImagePopupListener popupListener) {
        return new SnapsUploadFailedImagePopup(attribute, popupListener);
    }

    public static SnapsUploadFailedImagePopupAttribute createUploadFailedImagePopupAttribute(Activity activity, String projCode, boolean isLandscapeMode) {
        return new SnapsUploadFailedImagePopupAttribute.UploadFailedImagePopupBuilder()
                .setActivity(activity)
                .setProjCode(projCode)
                .setLandscapeMode(isLandscapeMode)
                .create();
    }

    public static SnapsUploadFailedImagePopupAttribute createPhotoPrintUploadFailedImagePopupAttribute(Activity activity, String projCode, boolean isLandscapeMode) {
        return new SnapsUploadFailedImagePopupAttribute.UploadFailedImagePopupBuilder()
                .setActivity(activity)
                .setProjCode(projCode)
                .setLandscapeMode(isLandscapeMode)
                .setPhotoPrint(true)
                .create();
    }

    private SnapsUploadFailedImagePopup(SnapsUploadFailedImagePopupAttribute attribute, SnapsUploadFailedImagePopupListener popupListener) {
        super(attribute.getActivity());
        this.activity = attribute.getActivity();
        this.projCode = attribute.getProjCode();
        this.isLandscapeMode = attribute.isLandscapeMode();
        this.popupListener = popupListener;
        this.isPhotoPrint = attribute.isPhotoPrint();
    }

    public static void showUploadFailedImageList(@NonNull SnapsUploadFailedImagePopupAttribute attribute, @NonNull SnapsUploadFailedImagePopupListener popupListener) throws Exception {
        SnapsUploadFailedImagePopup uploadFailedImagePopup = createInstanceWithPopupListener(attribute, popupListener);
        uploadFailedImagePopup.showUploadFailedImageList();
    }

    public static boolean showOrgImgUploadFailPopupIfGetUploadFailIntent(Activity activity, Intent intent, HomeUIHandler homeUIHandler) throws Exception {
        String orgImgUploadFailedProjCode = intent.getStringExtra("orgImgUploadFailedProjCode");
        return !StringUtil.isEmpty(orgImgUploadFailedProjCode) && showOrgImgUploadFailPopup(activity, orgImgUploadFailedProjCode, homeUIHandler);
    }

    private static boolean showOrgImgUploadFailPopup(final Activity activity, String projCode, final HomeUIHandler homeUIHandler) {
        if (SnapsUploadFailedImageDataCollector.getFailedImageDataCount(projCode) <= 0) return false;

        SnapsUploadFailedImagePopupAttribute popupAttribute = SnapsUploadFailedImagePopup.createPhotoPrintUploadFailedImagePopupAttribute(activity, projCode, false);
        SnapsUploadFailedImageDataCollector.showUploadFailedOrgImageListPopup(popupAttribute, new SnapsUploadFailedImagePopup.SnapsUploadFailedImagePopupListener() {
            @Override
            public void onShowUploadFailedImagePopup() {}

            @Override
            public void onSelectedUploadFailedImage(List<MyPhotoSelectImageData> uploadFailedImageList) {
                SnapsMenuManager.goToCartList(activity, homeUIHandler);
            }
        });
        return true;
    }

    private void showUploadFailedImageList() throws Exception {
        if (!SnapsUploadFailedImageDataCollector.isExistFailedImageData(getProjCode())) throw new SnapsOrderException("not exist upload failed images");
        else if (activity == null || activity.isFinishing()) throw new SnapsOrderException("activity is finishing.");

        initLayout();

        addContentView();

        SnapsUploadFailedImageDataCollector.setShowingUploadFailPopup(true);

        if (popupListener != null)
            popupListener.onShowUploadFailedImagePopup();
    }

    private void addContentView() {
        if (activity == null || activity.isFinishing()) return;
        activity.addContentView(this, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private List<MyPhotoSelectImageData> getUploadFailedOrgImageList() throws Exception {
        SnapsUploadFailedImageData failedImageData = SnapsUploadFailedImageDataCollector.getUploadFailedImageData(getProjCode());
        if (failedImageData != null) {
            Dlog.d("getUploadFailedOrgImageList() show upload failed image list size:" + failedImageData.getUploadFailedImageList().size());
            return failedImageData.getUploadFailedImageList();
        }
        throw new SnapsOrderException("project code error!");
    }

    void initLayout() throws Exception {
        this.isFinishing = false;

        int layoutId = isLandscapeMode ? R.layout.upload_failed_org_img_list_popup_for_landscape : R.layout.upload_failed_org_img_list_popup;

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(layoutId, this);

        final List<MyPhotoSelectImageData> imageDataList = getUploadFailedOrgImageList();
        if (imageDataList == null) return;

        TextView tvCount = (TextView) view.findViewById(R.id.upload_failed_org_img_list_popup_count);
        tvCount.setText(String.valueOf(imageDataList.size()));

        TextView performBtn = (TextView) view.findViewById(R.id.upload_failed_org_img_list_popup_perform_btn);
        performBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSnapsCanvasUploadFailedUploadOrgImgDataFlag(imageDataList);

                if (popupListener != null)
                    popupListener.onSelectedUploadFailedImage(imageDataList);

                closePopup();
            }
        });

        if (isPhotoPrint) {
            performBtn.setText(activity.getString(R.string.go_to_cart));
            TextView tvDesc = (TextView) view.findViewById(R.id.upload_failed_org_img_list_popup_desc);
            tvDesc.setText(activity.getString(R.string.photo_print_upload_failed_org_img_popup_desc));
        }

        recyclerView = (SnapsSuperRecyclerView) view.findViewById(R.id.upload_failed_org_img_list_popup_recyclerview);

        loadUploadFailedImageList(imageDataList);
    }

    private void loadUploadFailedImageList(List<MyPhotoSelectImageData> imageDataList) {
        if (imageDataList == null) return;

        photoAdapter = new SnapsUploadFailedImageAdapter(activity, isLandscapeMode);
        photoAdapter.setData((ArrayList<MyPhotoSelectImageData>) imageDataList);

        final int COLUMN_COUNT = isLandscapeMode ? Const_VALUE.IMAGE_GRID_COLS_LANDSCAPE : Const_VALUE.IMAGE_GRID_COLS;

        if (itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }

        itemDecoration = new SnapsUploadFailedImageSpacingItemDecoration(activity, UIUtil.convertDPtoPX(activity, 8), COLUMN_COUNT);

        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(activity, COLUMN_COUNT);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(photoAdapter);
    }

    /**
     * 업로드 실패된 사진 파란 아이콘 나오게 하기 위해
     */
    public void setSnapsCanvasUploadFailedUploadOrgImgDataFlag(List<MyPhotoSelectImageData> uploadFailedImageList) {
        if (uploadFailedImageList == null || uploadFailedImageList.isEmpty()) return;
        for (MyPhotoSelectImageData imageData : uploadFailedImageList) {
            imageData.isUploadFailedOrgImage = true;
        }
    }

    private void closePopup() {
        if (isFinishing) return;
        isFinishing = true;
        try {
            ((ViewGroup) getParent()).removeView(this);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        SnapsUploadFailedImageDataCollector.setShowingUploadFailPopup(false);
    }

    public String getProjCode() {
        return projCode;
    }
}

