package com.snaps.mobile.activity.common.products.single_page_product;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.HashMap;

public class TinCaseEditor extends SnapsSinglePageEditor {
    private static final String TAG = TinCaseEditor.class.getSimpleName();

    public TinCaseEditor(FragmentActivity fragmentActivity) {
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

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();
    }

    @Override
    public void onFinishedFirstSmartSnapsAnimation() {
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }

    @Override
    public void handleScreenRotatedHook() {
        initControlVisibleStateOnActivityCreate();
    }


    @Override
    public SnapsTemplate loadTemplate(String url) {
        SnapsTemplate snapsTemplate = super.loadTemplate(url);

        if (Config.isFromCart()) {
            return snapsTemplate;
        }

        String caseColor;
        Intent intent = getActivity().getIntent();
        HashMap<String, String> parameters = (HashMap<String, String>)intent.getSerializableExtra(SnapsProductEditConstants.EXTRA_NAME_ALL_PARAM_MAP);
        caseColor = parameters.get("prmBackType");
        if (caseColor == null || caseColor.length() == 0) {
            Dlog.e(TAG, "[case color] prmBackType is invalid:" + caseColor);
            if (Config.isDevelopVersion()) {    //TODO::개발용 <- 삭제 해야 함
                caseColor = Const_VALUES.COLOR_CODE_TINCASE_SILVER;
            }
        }

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        snapsProductOption.set(SnapsProductOption.KEY_CASE_COLOR, caseColor);

        return snapsTemplate;
    }

}
