package com.snaps.mobile.activity.common.products.multi_page_product;


import android.content.Intent;
import androidx.fragment.app.FragmentActivity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.structure.SnapsTemplate;

import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.name_sticker.NameStickerWriteActivity;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.edit_activity_tools.adapter.BabyNameStickerThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class BabyNameStickerEditor extends SnapsMultiPageEditor {

    public BabyNameStickerEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistTitleActLayout();
        FrameLayout dragLayer = (FrameLayout) findViewById(isLandScapeScreen() ? R.id.drag_layer_h : R.id.drag_layer_v);
        if (dragLayer != null) {
            TextView themeTitleText = (TextView) dragLayer.findViewById(R.id.ThemeTitleText);
            themeTitleText.setText(getString(R.string.baby_name_sticker));
            View titleTextView = dragLayer.findViewById(R.id.topView);
            if (titleTextView != null)
                titleTextView.setVisibility(View.VISIBLE);
            LinearLayout linearLayoutCount = (LinearLayout)dragLayer.findViewById(R.id.linearLayoutCount);
            if(linearLayoutCount != null)
            linearLayoutCount.setVisibility(View.GONE);
        }
        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null)
            coverModify.setVisibility(View.VISIBLE);

        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null)
            previewBtn.setVisibility(View.GONE);
        Config.setIsBabyNameStickerEditScreen(true);

    }



    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setComplete(false, getActivity());
        Config.setPROJ_NAME("");
    }

    @Override
    public void onClickedChangeDesign() {
        showChangePageActcity(false);
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();
        showTouchEditTextTutorial();
    }

    @Override
    public BaseEditActivityThumbnailAdapter createThumbnailAdapter() {
        return new BabyNameStickerThumbnailAdapter(getActivity(), getEditInfo());
    }

    @Override
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_edit_new_years_card);
    }

    @Override
    public void onFinishedFirstSmartSnapsAnimation() {
        showEditActivityTutorial();
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_BABY_NANE_STICKER;
    }

    @Override
    public void onClickedBgControl() {
        Intent intent = new Intent(getActivity(), NameStickerWriteActivity.class);
        intent.putExtra("edit",true);
        intent.putExtra("page",  getEditControls().getCenterPager().getCurrentItem());
        getActivity().startActivityForResult(intent,REQ_NAME_STICKER_EDIT_TEXT);
    }

    @Override
    protected void showTextEditTutorial() {

    }

    private void showTouchEditTextTutorial() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        if (snapsTemplate == null || snapsTemplate.getPages() == null) return ;
        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if(centerPager != null && centerPager.getCurrentItem() == 0) {
            String msg = null;
            SnapsBgControl snapsLayoutControl = (SnapsBgControl)snapsTemplate.getPages().get(0).getLayerBgs().get(0);
            if (snapsLayoutControl != null) {
                msg = getActivity().getString(R.string.text_area_touch_edit);

                RelativeLayout rootView = getEditControls().getRootView();
                if (rootView != null) {
                    int topMargin = UIUtil.convertDPtoPX(getActivity(),60);
                        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(
                                SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
                                .setText(msg)
                                .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_NAME_STICKER_TEXT_EDIT_TUTORIAL)
                                .setTargetView(rootView)
                                .setTopMargin(topMargin)
                                .create());
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Config.setIsBabyNameStickerEditScreen(true);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
