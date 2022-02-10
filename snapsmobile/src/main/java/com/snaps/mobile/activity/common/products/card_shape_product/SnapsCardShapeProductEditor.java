package com.snaps.mobile.activity.common.products.card_shape_product;

import android.os.Message;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditorCommonImplement;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by ysjeong on 2017. 10. 19..
 */

public abstract class SnapsCardShapeProductEditor extends SnapsProductBaseEditorCommonImplement {
    private static final String TAG = SnapsCardShapeProductEditor.class.getSimpleName();

    public static final int MAX_PHOTO_CARD_QUANTITY = 24;

    public static final int MAX_NEWYEARS_CARD_QUANTITY = 10;

    public static final int MAX_CARD_QUANTITY = 999;

    protected TextView photoCardCurrentCount, photoCardMaxCount = null;

    public SnapsCardShapeProductEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setPROJ_NAME("");
        Config.setIS_OVER_LENTH_CARD_MSG(false);
    }

    @Override
    public void initHiddenPageOnLoadedTemplate(SnapsTemplate template) {
        if (template.getPages() != null) {
            template._hiddenPageList = new ArrayList<SnapsPage>();
            for (int ii = template.getPages().size() - 1; ii >= 0; ii--) {
                SnapsPage page = template.getPages().get(ii);
                if (page != null && page.type != null && page.type.equalsIgnoreCase("hidden")) {
                    template._hiddenPageList.add(0, page);
                    template.getPages().remove(page);
                }
            }

            refreshPagesId(template.getPages());
        }
    }

    @Override
    public void showGalleryPopOverView(final View view, final int position) {
        if (getSnapsHandler() != null) {
            Message msg = new Message();
            msg.what = HANDLER_MSG_SHOW_POPOVER_VIEW;
            msg.arg1 = position;
            msg.obj = view;
            getSnapsHandler().sendMessageDelayed(msg, 200);
        }
    }

    @Override
    public void handleOnFirstResume() {
        if (getEditInfo() != null) {
            if (!getEditInfo().IS_EDIT_MODE()) {
                getEditInfo().setTemplateUrl(SnapsTemplate.getTemplateUrl());
            }
        }

        String templateUrl = getEditInfo() != null ? getEditInfo().getTemplateUrl() : "";
        getTemplateHandler(templateUrl);
    }

    @Override
    public void onCenterPagerSelected(int page) {
        int pageChangeType = (page > getCurrentPageIndex() ? EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NEXT : (page < getCurrentPageIndex() ? EditActivityThumbnailUtils.PAGE_MOVE_TYPE_PREV
                : EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NONE));
        getEditInfo().setCurrentPageIndex(page);
        setThumbnailSelectionDragView(pageChangeType, page);
    }

    protected void checkPhotoCardChangeDesignTutorial(int page) {
        try {
            int[] targetPageIdxArr = getPhotoCardPairPageIdxArr(page);
            if (isBothEditedPhotoCardPairPage(targetPageIdxArr)) {
                showDesignChangeTutorial();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void showDesignChangeTutorial() {}

    private boolean isBothEditedPhotoCardPairPage(int[] targetPageIdxArr) throws Exception {
        for (int pageIdx : targetPageIdxArr) {
            SnapsPage snapsPage = getPageList().get(pageIdx);
            if (!snapsPage.isEditedPage()) return false;
        }
        return true;
    }

    private int[] getPhotoCardPairPageIdxArr(int page) {
        int[] targetPageIdxArr = new int[2];
        targetPageIdxArr[0] = page % 2 != 0 ? page-1 : page;
        targetPageIdxArr[1] = page % 2 != 0 ? page : page+1;
        return targetPageIdxArr;
    }

    @Override
    public void onClickedChangeDesign() {
        showChangePageActcity(false);
    }

    @Override
    public void setCardShapeLayout() {
        setBasicCardShapeLayout();
    }

    private void setBasicCardShapeLayout() {
        if (findViewById(R.id.horizontal_line) != null)
            findViewById(R.id.horizontal_line).setVisibility(View.VISIBLE);

        RelativeLayout addPageLy = getEditControls().getAddPageLy();
        if (addPageLy != null)
            addPageLy.setVisibility(View.GONE);

        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null)
            coverModify.setVisibility(View.VISIBLE);

        ImageView textModify = getEditControls().getThemeTextModify();
        if (textModify != null)
            textModify.setVisibility(View.GONE);

        View galleryView = findViewById(isLandScapeScreen() ? R.id.activity_edit_themebook_gallery_ly_h : R.id.activity_edit_themebook_gallery_ly_v);
        if (galleryView != null)
            galleryView.setVisibility(View.VISIBLE);

        if (getThumbnailRecyclerView() != null)
            getThumbnailRecyclerView().setVisibility(View.VISIBLE);

        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        previewBtn.setVisibility(View.GONE);
    }

    protected void refreshPhotoCardQuantityFloatingView(int maxQuantity) {
        try {
            int currentTotalCardQuantity = getCurrentTotalCardQuantity();
            photoCardCurrentCount.setText(String.valueOf(currentTotalCardQuantity));
            photoCardMaxCount.setText(String.valueOf(maxQuantity));
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private int getCurrentTotalCardQuantity() throws Exception {
        if (getTemplate() == null || getTemplate().getPages() == null) return 0;
        ArrayList<SnapsPage> pageList = getTemplate().getPages();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii+=2) {
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return totalQuantity;
    }

    @Override
    public void refreshPageThumbnail() {
        //썸네일 갱신
        Queue<Integer> pageLoadQueue = getEditInfo().getPageLoadQueue();
        while (pageLoadQueue != null && !pageLoadQueue.isEmpty()) {
            Integer nextPage = pageLoadQueue.poll();
            if (nextPage != null && nextPage >= 0) {
                PhotobookCommonUtils.changePageThumbnailState(getPageList(), nextPage, false);
            }
        }

        try {
            BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
            if (thumbnailAdapter != null) {
                thumbnailAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        //자동 저장 파일 갱신
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan != null) {
            if (saveMan.isRecoveryMode()) {
                finishRecovery();
            } else {
                exportAutoSaveTemplate();
            }
        }

        //화면 로테이션이 잠겨 있다면 풀어 준다.
        if (getSnapsHandler() != null)
            getSnapsHandler().sendEmptyMessageDelayed(HANDLER_MSG_UNLOCK_ROTATE_BLOCK, 1000);
    }

    @Override
    public boolean isTwinShapeBottomThumbnail() {
        return true;
    }
}
