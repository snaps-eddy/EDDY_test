package com.snaps.mobile.activity.common.products.multi_page_product;

import android.graphics.Rect;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.R;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.edit_activity_tools.adapter.AccordionCardThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class AccordionCardEditor extends SnapsMultiPageEditor {

    public AccordionCardEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistTitleActLayout();
        FrameLayout dragLayer = (FrameLayout) findViewById(isLandScapeScreen() ? R.id.drag_layer_h : R.id.drag_layer_v);
        if (dragLayer != null) {
            TextView themeTitleText = (TextView) dragLayer.findViewById(R.id.ThemeTitleText);
            themeTitleText.setText(getString(R.string.snaps_accordion_card));
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

    }

    @Override
    protected boolean initLoadedTemplateInfo(SnapsTemplate template) {
        super.initLoadedTemplateInfo(template);
        // 프레임 타입을 저장한다.
        if (!getEditInfo().IS_EDIT_MODE()) {
            template.info.F_FRAME_TYPE = Config.getFRAME_TYPE();
        }
        return true;
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
    }

    @Override
    public BaseEditActivityThumbnailAdapter createThumbnailAdapter() {
        return new AccordionCardThumbnailAdapter(getActivity(), getEditInfo());
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
        return IAutoSaveConstants.PRODUCT_TYPE_ACCORDION_CARD;
    }

    @Override
    public Rect getQRCodeRect() {
        int lastPage = getTemplate().getPages().size() - 1;
        int qrMargin = 10;

        // 커버를 구한다. 기준위치는 테마북으로 한다.
        SnapsPage coverPage = getTemplate().getPages().get(lastPage);
        int width = coverPage.getOriginWidth();
        int height = (int) Float.parseFloat(coverPage.height);

        Rect rect = new Rect(0, 0, 35, 43);
        rect.offset(width - rect.width() - (qrMargin * 2), height - rect.height() - qrMargin);

        return rect;
    }
}
