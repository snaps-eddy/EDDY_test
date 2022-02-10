package com.snaps.mobile.activity.common.products.single_page_product;

import android.content.Intent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsSceneCutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 상식적으로,
 * 편집기 액티비티에서 하는 일들 중 개별로, 상품별로 처리해야할 일이 있을 땐 이곳.
 * 편집기 프래그먼트에서 하는 일들은 PageCanvasFragment 로,
 * 편집기 Real Page View 과 Thumb Page View 에서 각각 하는 일들은 PageCanvas로
 */
public class SealStickerProductEditor extends SnapsSinglePageEditor {
    public static volatile boolean isTransparentPaper = false;
    public static String glossyType = "";
    public static String sceneCutUrl = "";
    public static volatile boolean isShownToolTip = false;

    public SealStickerProductEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistThumbnailLayout();
    }

    // Auto save disabled.
    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setPROJ_NAME("");
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();

        setBackgroundImageControl();
    }

    private void setBackgroundImageControl() {
        final SnapsLayoutControl backgrouncControl = getTemplate().getStickerBackgroundLayoutControl();
        if (backgrouncControl == null || getEditControls().getBtnBackgroundToolChangeSource() == null || getEditControls().getBtnBackgroundToolEdit() == null
                || getEditControls().getBtnBackgroundToolDelete() == null || getEditControls().getToggleBtnBackgroundToolbox() == null
                || getEditControls().getViewOutsideBackgroundToolbox() == null) {
            return;
        }

        getEditControls().getToggleBtnBackgroundToolbox().setOnClickListener(v -> {
            View toolBox = getEditControls().getViewBackgroundToolbox();
            if (toolBox == null) {
                return;
            }

            if (toolBox.getVisibility() == View.VISIBLE) {
                Intent intent = new Intent(Const_VALUE.RESET_LAYOUT_ACTION);
                getActivity().sendBroadcast(intent);
                super.dismissBackgroundToolBox();

            } else {
                Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
                intent.putExtra("control_id", backgrouncControl.getControlId());
                intent.putExtra("isEdited", true);
                intent.putExtra("isShowPopup", false);
                getActivity().sendBroadcast(intent);
                setEnabledBackgroundTool();
                toolBox.setVisibility(View.VISIBLE);
            }
        });

        getEditControls().getViewOutsideBackgroundToolbox().setOnClickListener(v -> {
            View toolBox = getEditControls().getViewBackgroundToolbox();
            if (toolBox == null) {
                return;
            }
            Intent intent = new Intent(Const_VALUE.RESET_LAYOUT_ACTION);
            getActivity().sendBroadcast(intent);
            toolBox.setVisibility(View.GONE);
        });

        getEditControls().getBtnBackgroundToolChangeSource().setOnClickListener(v -> {
            onClickedImageChange();
        });

        getEditControls().getBtnBackgroundToolEdit().setOnClickListener(v -> {
            if (backgrouncControl.isEmptyImage()) {
                return;
            }
            onClickedImageEdit();
        });

        getEditControls().getBtnBackgroundToolDelete().setOnClickListener(v -> {
            if (backgrouncControl.isEmptyImage()) {
                return;
            }
            onClickedImageRemove();
        });
    }

    @Override
    public void setTemplateBaseInfo() {
        getTemplate().saveInfo.orderCount = Config.getCARD_QUANTITY();
        super.setTemplateBaseInfo();
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
        if (getEditInfo().IS_EDIT_MODE()) {
            Config.setFRAME_TYPE(template.info.F_FRAME_TYPE);
        } else {
            template.info.F_FRAME_TYPE = Config.getFRAME_TYPE();
        }
        return true;
    }

    @Override
    public void handleScreenRotatedHook() {
        initControlVisibleStateOnActivityCreate();
    }

    /**
     * SnapsEditActivity 레이아웃 변경하는 함수
     */
    @Override
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_seal_sticker);
    }


    @Override
    public void initImageRangeInfoOnLoadedTemplate(SnapsTemplate template) {
        //TODO:: 장바구니 저장되면 정상 동작 여부 확인 필요
        if (Config.isFromCart()) {
            super.initImageRangeInfoOnLoadedTemplate(template);
        } else {
            PhotobookCommonUtils.imageRangeForSealSticker(template, getEditInfo().getGalleryList());
        }
    }

    @Override
    public SnapsTemplate loadTemplate(String url) {
        SnapsTemplate snapsTemplate = super.loadTemplate(url);

        isShownToolTip = false;
        isTransparentPaper = false;
        sceneCutUrl = "";
        glossyType = "";

        List<SnapsControl> snapsControlList = snapsTemplate.getPages().get(0).getLayerControls();
        for (SnapsControl snapsControl : snapsControlList) {
            if (snapsControl != null && snapsControl instanceof SnapsSceneCutControl) {
                SnapsSceneCutControl snapsSceneCutControl = (SnapsSceneCutControl) snapsControl;
                sceneCutUrl = snapsSceneCutControl.resourceURL;
            }
        }

//        용지
//        160034 스탠다드
//        160035 투명
//        160036 리무버블
//
//        코팅
//        M 무광  / G 유광 / S 스파클 / A 오로라

        String prmPaperCode = "";
        String prmGlossyType = "";

        if (Config.isFromCart()) {
            prmPaperCode = snapsTemplate.info.F_PAPER_CODE;
            prmGlossyType  = snapsTemplate.info.F_GLOSSY_TYPE;
        } else {
            Intent intent = getActivity().getIntent();
            HashMap<String, String> parameters = (HashMap<String, String>) intent.getSerializableExtra(SnapsProductEditConstants.EXTRA_NAME_ALL_PARAM_MAP);
            prmPaperCode = parameters.get("prmPaperCode");
            prmGlossyType = parameters.get("prmGlossyType");
        }

        if (prmPaperCode == null || prmPaperCode.length() == 0) {
            isTransparentPaper = false; //오류인 경우 투명이 아닌것으로 처리한다.
        } else if (prmPaperCode.equals("160035")) {
            isTransparentPaper = true;
        } else {
            isTransparentPaper = false;
        }

        if (prmGlossyType == null || prmGlossyType.length() == 0) {
            glossyType = "M"; //오류인 경우 무광으로 처리한다.
        } else {
            glossyType = prmGlossyType;
        }

        return snapsTemplate;
    }

    @Override
    public void refreshSelectedNewImageDataHook(MyPhotoSelectImageData imageData) {
//        setEnabledBackgroundTool();
    }

    private void setEnabledBackgroundTool() {
        final SnapsLayoutControl backgroundControl = getTemplate().getStickerBackgroundLayoutControl();
        if (backgroundControl != null && backgroundControl.isNotEmptyImage()) {
            getEditControls().getBtnBackgroundToolDelete().setAlpha(1.0f);
            getEditControls().getBtnBackgroundToolEdit().setAlpha(1.0f);
        } else {
            getEditControls().getBtnBackgroundToolDelete().setAlpha(0.3f);
            getEditControls().getBtnBackgroundToolEdit().setAlpha(0.3f);
        }
    }

    @Override
    protected boolean isLockPortraitOrientation() {
        return true;
    }
}
