package com.snaps.mobile.activity.common.products.multi_page_product;

import androidx.fragment.app.FragmentActivity;
import android.view.View;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.SloganActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by kimduckwon on 2018. 1. 15..
 */

public class SloganEditor extends SnapsCounterPageEditor{
    private static final String TAG = SloganEditor.class.getSimpleName();

    public final static int MAX_SLOGAN_QUANTITY = 10;
    public SloganEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_SLOGAN;
    }

    @Override
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_edit_new_years_card);
    }

    @Override
    public int setMaxQuantity() {
        return MAX_SLOGAN_QUANTITY;
    }

    @Override
    public String setTitle() {
        return getString(R.string.paper_slogan);
    }

    @Override
    public boolean addTemplatePage(SnapsTemplate snapsTemplate) {
        return false;
    }

    @Override
    public String getDeletePageMessage() {
        return getString(R.string.slogan_delete);
    }

    @Override
    public void changeTemplatePage(SnapsTemplate snapsTemplate) {

    }

    @Override
    protected int getCurrentTotalQuantity() throws Exception {
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
    public boolean isLackMinPageCount() {
        return getTemplate().getPages().size() <= 1;
    }
    @Override
    public BaseEditActivityThumbnailAdapter createThumbnailAdapter() {
        return new SloganActivityThumbnailAdapter(getActivity(), getEditInfo());
    }

    @Override
    public void deletePage(int index) {
        if (getPageList() == null || getPageList().size() <= index)
            return;

        // 삭제전에 이미지 데이터가 있으면 삭제하기..
        SnapsPage leftPage = null;
        SnapsPage rightPage = null;
        if(index % 2 == 0) {
            leftPage = getPageList().get(index);
            rightPage = getPageList().get(index + 1);
        } else  {
            leftPage = getPageList().get(index -1);
            rightPage = getPageList().get(index);
        }

        SnapsOrderManager.removeBackgroundUploadOrgImagesInPage(leftPage);

        // 페이지 삭제
        getPageList().remove(leftPage);
        getPageList().remove(rightPage);

        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null) {
            thumbnailUtil.sortPagesIndex(getActivity(), getCurrentPageIndex());
        }

        BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
        if (thumbnailAdapter != null) {
            try {
                thumbnailAdapter.notifyItemRemoved(index/2);
            } catch (Exception e) { Dlog.e(TAG, e); }
        }

        // maxpage 설정...
        getTemplate().setApplyMaxPage();
        exportAutoSaveTemplate();

        refreshList(index, getPageList().size() - 1);
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
    public void onThumbnailViewLongClick(View view, int position) {
        if (getTemplate() != null && getTemplate().getPages() != null && getTemplate().getPages().size() > 2)
            showGalleryPopOverView(view, position);
    }

    @Override
    protected int getTotalQuantityExcludeSelectedPage(int pagePosition) throws Exception {
        ArrayList<SnapsPage> pageList = getTemplate().getPages();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii+= 2) {
            if (pagePosition == ii ) continue;
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return totalQuantity;
    }

    @Override
    public boolean isTwinShapeBottomThumbnail() {
        return true;
    }
}

