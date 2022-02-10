package com.snaps.mobile.activity.common.products.single_page_product;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsStickControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.HashMap;

public class AcrylicStandEditor extends SnapsSinglePageEditor {
    private static final String TAG = AcrylicStandEditor.class.getSimpleName();

    public static volatile boolean sIsShowPrice;    //사용자가 사진 선택후 최초 한번 가격 정보를 보여주기 위한 목적

    public AcrylicStandEditor(FragmentActivity fragmentActivity) {
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

        sIsShowPrice = !Config.isFromCart();

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

    private static final int MM_SCENE_MAGIN = 2;
    private static final int MM_KNIFELINE_MAGIN = 2;
    private static final int MM_STICK_WIDTH = 14;
    private static final int MM_STICK_HEIGHT = 3;
    private static final int MM_HELPER_MIN_WIDTH = 20;
    private static final int MM_MINIMUM_IMAGE_SIZE = 20;

    @Override
    public SnapsTemplate loadTemplate(String url) {
        SnapsTemplate snapsTemplate = super.loadTemplate(url);

        if (Config.isFromCart()) {
            return snapsTemplate;
        }

        int height = 200;
        int width = 200;
        Intent intent = getActivity().getIntent();
        HashMap<String, String> parameters = (HashMap<String, String>)intent.getSerializableExtra(SnapsProductEditConstants.EXTRA_NAME_ALL_PARAM_MAP);
        try {
            width = (int)Float.parseFloat(parameters.get("millimeterWidth"));
            height = (int)Float.parseFloat(parameters.get("millimeterHeight"));
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }

        int longSide = Math.max(width, height);

        int zoomLevel;

        if (longSide < 50) {
            // 20 ~ 49
            zoomLevel = 6;
//            knifeLine = 12;
//            stickHeight = 18;
//            stickWidth = 84;
//            margin = 18;
//            helperMinWidth = 120;
//            minimumImageSize = 120;
        } else if (longSide < 100) {
            // 50 ~ 99
            zoomLevel = 5;
        } else if (longSide < 150) {
            // 100 ~ 149
            zoomLevel = 4;
        } else {
            // 150 ~ 200
            zoomLevel = 3;
        }

        int knifeLine = MM_KNIFELINE_MAGIN * zoomLevel;
        int stickWidth = MM_STICK_WIDTH * zoomLevel;
        int stickHeight = MM_STICK_HEIGHT * zoomLevel;
        int margin = MM_SCENE_MAGIN * zoomLevel;
        int helperMinWidth = MM_HELPER_MIN_WIDTH * zoomLevel;
        int minimumImageSize = MM_MINIMUM_IMAGE_SIZE * zoomLevel;

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        snapsProductOption.set(SnapsProductOption.KEY_USER_SELECTED_MM_WIDTH, Integer.toString(width));
        snapsProductOption.set(SnapsProductOption.KEY_USER_SELECTED_MM_HEIGHT, Integer.toString(height));
        snapsProductOption.set(SnapsProductOption.KEY_ZOOM_LEVEL, Integer.toString(zoomLevel));
        snapsProductOption.set(SnapsProductOption.KEY_KNIFE_LINE_PX, Integer.toString(knifeLine));
        snapsProductOption.set(SnapsProductOption.KEY_STICK_WIDTH_PX, Integer.toString(stickWidth));
        snapsProductOption.set(SnapsProductOption.KEY_STICK_HEIGHT_PX, Integer.toString(stickHeight));
        snapsProductOption.set(SnapsProductOption.KEY_MARGIN_PX, Integer.toString(margin));
        snapsProductOption.set(SnapsProductOption.KEY_HELPER_MIN_WIDTH_PX, Integer.toString(helperMinWidth));
        snapsProductOption.set(SnapsProductOption.KEY_MINIMUM_IMAGE_SIZE_PX, Integer.toString(minimumImageSize));

//        디폴트 값으로 사용자가 입력한 width, height를 셋팅한다.
        snapsProductOption.set(SnapsProductOption.KEY_MM_WIDTH, Integer.toString(width));
        snapsProductOption.set(SnapsProductOption.KEY_MM_HEIGHT, Integer.toString(height));

        width *= zoomLevel;
        height *= zoomLevel;

        SnapsPage snapsPage = SnapsTemplateManager.getInstance().getSnapsTemplate().getPages().get(0);

        int marginWidth = (margin + knifeLine) * 2;
        int marginHeight = (margin + knifeLine) * 2 + stickHeight;
        snapsPage.width = String.valueOf(marginWidth + width);
        snapsPage.height = String.valueOf(marginHeight + height);

        for (SnapsControl snapsControl : snapsPage.getLayoutList()) {
            switch (snapsControl._controlType) {
                case SnapsControl.CONTROLTYPE_IMAGE:
                    snapsControl.x = Integer.toString(margin + knifeLine);
                    snapsControl.y = Integer.toString(margin + knifeLine);
                    snapsControl.width = Integer.toString(width);
                    snapsControl.height = Integer.toString(height);
                    break;

                case SnapsControl.CONTROLTYPE_MOVABLE:
                    if (snapsControl instanceof SnapsStickControl) {
                        snapsControl.width = Integer.toString(stickWidth);
                        snapsControl.height = Integer.toString(stickHeight);
                    }
                    break;
            }
        }

        return snapsTemplate;
    }


}
