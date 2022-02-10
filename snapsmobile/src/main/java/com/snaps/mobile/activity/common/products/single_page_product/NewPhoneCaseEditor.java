package com.snaps.mobile.activity.common.products.single_page_product;

import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import android.view.View;
import android.widget.ImageView;

import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.HashMap;

public class NewPhoneCaseEditor extends SnapsSinglePageEditor {

    private static final String TAG = NewPhoneCaseEditor.class.getSimpleName();

    public NewPhoneCaseEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistThumbnailLayout();
    }

    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setPROJ_NAME("");
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();
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
    protected boolean initLoadedTemplateInfo(SnapsTemplate template) {
        super.initLoadedTemplateInfo(template);

        if (!getEditInfo().IS_EDIT_MODE()) {
            template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
            template.info.F_FRAME_TYPE = Config.getFRAME_TYPE();
        }
        return true;
    }

    @Override
    public void handleScreenRotatedHook() {
        initControlVisibleStateOnActivityCreate();
    }

    /**
     * 글리터 케이스인 경우 frameType 이라는 코드가 함께 넘어온다. // 블랙, 화이트
     *
     * @param url
     * @return
     */
    @Override
    public SnapsTemplate loadTemplate(String url) {
        SnapsTemplate snapsTemplate = super.loadTemplate(url);

        if (Config.isFromCart()) {
            return snapsTemplate;
        }

        Intent intent = getActivity().getIntent();
        HashMap<String, String> parameters = (HashMap<String, String>) intent.getSerializableExtra(SnapsProductEditConstants.EXTRA_NAME_ALL_PARAM_MAP);

        try {
            String caseCode = parameters.get("paperCode");
            String caseColor = parameters.get("frametype"); // Camel 아님. 주의
            String deviceColor = parameters.get("backType");
            String accessories = parameters.get("accessory");

            SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
            snapsProductOption.set(SnapsProductOption.KEY_PHONE_CASE_CASE_CODE, caseCode);

            if (StringUtil.isNotEmptyAfterTrim(deviceColor)) {
                snapsProductOption.set(SnapsProductOption.KEY_PHONE_CASE_DEVICE_COLOR, deviceColor);
            }

            if (StringUtil.isNotEmptyAfterTrim(caseColor)) {
                snapsProductOption.set(SnapsProductOption.KEY_PHONE_CASE_CASE_COLOR_CODE, caseColor);
            }

            snapsProductOption.set(SnapsProductOption.KEY_ACCESSORIES, accessories);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return snapsTemplate;
    }
}
