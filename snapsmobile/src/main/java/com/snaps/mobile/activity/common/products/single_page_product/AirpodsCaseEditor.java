package com.snaps.mobile.activity.common.products.single_page_product;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.HashMap;

public class AirpodsCaseEditor extends SnapsSinglePageEditor{
    private static final String TAG = AirpodsCaseEditor.class.getSimpleName();

    public AirpodsCaseEditor(FragmentActivity fragmentActivity) {
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
        }

        //투명 : T
        //불투명 : O
        String glossyType;
        glossyType = parameters.get("prmGlossyType");
        if (glossyType == null || glossyType.length() == 0) {
            Dlog.e(TAG, "[glossyType] prmGlossyType is invalid:" + glossyType);
        }

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        snapsProductOption.set(SnapsProductOption.KEY_CASE_COLOR, caseColor);
        snapsProductOption.set(SnapsProductOption.KEY_GLOSSY_TYPE, glossyType);
        return snapsTemplate;
    }
}
