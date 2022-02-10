package com.snaps.mobile.activity.common.products.single_page_product;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.HashMap;


public class AcrylicKeyringEditor extends SnapsSinglePageEditor {
    private static final String TAG = AcrylicKeyringEditor.class.getSimpleName();

    public static volatile boolean sIsShowPrice;    //사용자가 사진 선택후 최초 한번 가격 정보를 보여주기 위한 목적

    public AcrylicKeyringEditor(FragmentActivity fragmentActivity) {
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
        showEditActivityTutorial();
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
    private static final int KEY_HOLE_DIAMETER = 7;
    private static final int ZOOM_LEVEL = 6;
    private static final int KEY_MM_MINIMUM_SIZE = 10;
    private static final int KEY_KNIFE_LINE_THICKNESS_PX = 12;

    @Override
    public SnapsTemplate loadTemplate(String url) {
        SnapsTemplate snapsTemplate = super.loadTemplate(url);

        if (Config.isFromCart()) {
            return snapsTemplate;
        }

        int height = 100;
        int width = 100;
        String keyingType = "";
        String paperCode = "";
        String accessories = null;

        Intent intent = getActivity().getIntent();
        HashMap<String, String> parameters = (HashMap<String, String>) intent.getSerializableExtra(SnapsProductEditConstants.EXTRA_NAME_ALL_PARAM_MAP);
        try {
            width = (int) Float.parseFloat(parameters.get("millimeterWidth"));
            height = (int) Float.parseFloat(parameters.get("millimeterHeight"));
            keyingType = parameters.get("prmBackType");
            paperCode = parameters.get("paperCode");
            accessories = parameters.get("accessory");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        Dlog.d("Hello ? " + accessories);

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        snapsProductOption.set(SnapsProductOption.KEY_KEYING_TYPE, keyingType);
        snapsProductOption.set(SnapsProductOption.KEY_USER_SELECTED_MM_HEIGHT, Integer.toString(height));
        snapsProductOption.set(SnapsProductOption.KEY_USER_SELECTED_MM_WIDTH, Integer.toString(width));
        snapsProductOption.set(SnapsProductOption.KEY_ZOOM_LEVEL, String.valueOf(ZOOM_LEVEL));
        snapsProductOption.set(SnapsProductOption.KEY_MARGIN_PX, String.valueOf(MM_SCENE_MAGIN * ZOOM_LEVEL));
        snapsProductOption.set(SnapsProductOption.KEY_KEY_HOLE_DIAMETER, String.valueOf(KEY_HOLE_DIAMETER));
        snapsProductOption.set(SnapsProductOption.KEY_KNIFE_LINE_PX, Integer.toString(KEY_KNIFE_LINE_THICKNESS_PX));
        snapsProductOption.set(SnapsProductOption.KEY_KEYRING_TEXTURE_TYPE, paperCode);
        snapsProductOption.set(SnapsProductOption.KEY_ACCESSORIES, accessories);

//        디폴트 값으로 사용자가 입력한 width, height를 셋팅한다.
        snapsProductOption.set(SnapsProductOption.KEY_MM_WIDTH, Integer.toString(width));
        snapsProductOption.set(SnapsProductOption.KEY_MM_HEIGHT, Integer.toString(height));

        snapsProductOption.set(SnapsProductOption.KEY_MINIMUM_IMAGE_SIZE_PX, String.valueOf(KEY_MM_MINIMUM_SIZE * ZOOM_LEVEL));

        width *= ZOOM_LEVEL;
        height *= ZOOM_LEVEL;

        SnapsPage snapsPage = SnapsTemplateManager.getInstance().getSnapsTemplate().getPages().get(0);

        int marginWidth = (KEY_HOLE_DIAMETER + MM_SCENE_MAGIN) * ZOOM_LEVEL * 2;
        int marginHeight = (KEY_HOLE_DIAMETER + MM_SCENE_MAGIN) * ZOOM_LEVEL * 2;
        snapsPage.width = String.valueOf(marginWidth + width);
        snapsPage.height = String.valueOf(marginHeight + height);

        for (SnapsControl snapsControl : snapsPage.getLayoutList()) {
            if (snapsControl._controlType == SnapsControl.CONTROLTYPE_IMAGE) {
                snapsControl.width = Integer.toString(width);
                snapsControl.height = Integer.toString(height);
                break;
            }
        }

        return snapsTemplate;
    }
}
