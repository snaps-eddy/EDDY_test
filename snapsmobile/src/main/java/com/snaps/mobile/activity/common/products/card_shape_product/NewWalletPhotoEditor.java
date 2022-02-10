package com.snaps.mobile.activity.common.products.card_shape_product;

import android.graphics.Point;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.mobile.R;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.NewWalletActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.tutorial.SnapsEditActivityTutorialUtil;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class NewWalletPhotoEditor extends SnapsCardShapeProductEditor {

    public NewWalletPhotoEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_edit_photo_card);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setCardShapeLayout();
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_WALLET_PHOTO;
    }

    @Override
    public void onThumbnailViewClick(View view, int position) {
        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null && position != centerPager.getCurrentItem()) {
            centerPager.setCurrentItem(position);
        }
    }

    @Override
    public boolean isLackMinPageCount() {
        return getTemplate().getPages().size() <= 2;
    }

    @Override
    public BaseEditActivityThumbnailAdapter createThumbnailAdapter() {
        return new NewWalletActivityThumbnailAdapter(getActivity(), getEditInfo());
    }

    @Override
    public void handleAfterRefreshList(int startPageIDX, int endPageIdx) {
        int startIdx = Math.min(Math.max(0, startPageIDX), getPageList().size() - 1);
        int endIdx = Math.min(getPageList().size() - 1, endPageIdx);

        offerQueue(startIdx, endIdx);
        refreshPageThumbnail();

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null)
            centerPager.setCurrentItem(startIdx, true);

        setThumbnailSelectionDragView(EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NONE, startIdx);
    }

    @Override
    public void setThumbnailSelectionDragView(int pageChangeType, int page) {
        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null) {
            thumbnailUtil.setSelectionCardShapeDragView(pageChangeType, page);
        }
    }

    @Override
    public void refreshSelectedNewImageDataHook(MyPhotoSelectImageData imageData) {
        if (imageData != null) {
            checkPhotoCardChangeDesignTutorial(imageData.pageIDX);
        }
    }

    @Override
    public void notifyTextControlFromIntentDataHook(SnapsTextControl control) {
        checkPhotoCardChangeDesignTutorial(control.getPageIndex());
    }

    @Override
    public void showDesignChangeTutorial() {
        if (getEditInfo().IS_EDIT_MODE()) return;

        FrameLayout tooltipTutorialLayout = getEditControls().getTooltipTutorialLayout();
        ImageView coverModify = getEditControls().getThemeCoverModify();

        SnapsTutorialAttribute attribute = new SnapsTutorialAttribute.Builder()
                .setTooltipTutorialLayout(tooltipTutorialLayout)
                .setLandscapeMode(isLandScapeScreen())
                .setTargetView(coverModify)
                .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_WALLET_PHOTO_CHANGE_DESIGN)
                .create();

        SnapsEditActivityTutorialUtil.showTooltipTutorial(getActivity(), attribute);
    }

    @Override
    public void initPaperInfoOnLoadedTemplate(SnapsTemplate template) {
        if (!Config.getPROJ_CODE().equalsIgnoreCase("")) {
            Config.setPAPER_CODE(template.info.F_PAPER_CODE);
            Config.setGLOSSY_TYPE(template.info.F_GLOSSY_TYPE);
        } else {
            template.info.F_PAPER_CODE = Config.getPAPER_CODE();
            template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
        }
    }

    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) { /** 페이지 추가 개념이 없다 **/ }

    @Override
    public Point getNoPrintToastOffsetForScreenLandscape() {
        return new Point(ResolutionConstants.NO_PRINT_TOAST_OFFSETX_LANDSCAPE_PHOTOCARD, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_LANDSCAPE_PHOTOCARD);
    }

    @Override
    public Point getNoPrintToastOffsetForScreenPortrait() {
        return new Point(ResolutionConstants.NO_PRINT_TOAST_OFFSETX_PHOTOCARD, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_PHOTOCARD);
    }

    @Override
    public void setPreviewBtnVisibleState() {
        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null) {
            previewBtn.setVisibility(View.GONE);
        }
    }

    public void handleScreenRotatedHook() {
        setCardShapeLayout();
    }

    @Override
    public int getLastEditPageIndex() {
        return 0;
    }
}
