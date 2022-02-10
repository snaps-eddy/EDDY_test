package com.snaps.mobile.activity.common.products.reflectable_slogans;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditorCommonImplement;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.HashMap;

public abstract class BaseReflectableSloganEditor extends SnapsProductBaseEditorCommonImplement {

    private static final String TAG = BaseReflectableSloganEditor.class.getSimpleName();

    BaseReflectableSloganEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();

        showTextEditTutorial(); //편집기 열릴때 텍스트 입력 메뉴의 "편집|삭제" 메뉴 표시
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null) {
            previewBtn.setVisibility(View.GONE);
        }

        ImageView textModify = getEditControls().getThemeTextModify();
        if (textModify != null) {
            textModify.setVisibility(View.GONE);
        }

        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null) {
            coverModify.setVisibility(View.GONE);
        }

        View addPageButton = getEditControls().getAddPageLy();
        if (addPageButton != null) {
            addPageButton.setVisibility(View.GONE);
        }
    }

    @Override
    public SnapsTemplate loadTemplate(String url) {
        SnapsTemplate snapsTemplate = super.loadTemplate(url);

        if (Config.isFromCart()) {
            return snapsTemplate;
        }

        String gradientType = "";

        Intent intent = getActivity().getIntent();
        HashMap<String, String> parameters = (HashMap<String, String>) intent.getSerializableExtra(SnapsProductEditConstants.EXTRA_NAME_ALL_PARAM_MAP);
        try {
            gradientType = parameters.get("prmBackType");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        snapsProductOption.set(SnapsProductOption.KEY_GRADIENT_TYPE, gradientType);

        return snapsTemplate;
    }
}
