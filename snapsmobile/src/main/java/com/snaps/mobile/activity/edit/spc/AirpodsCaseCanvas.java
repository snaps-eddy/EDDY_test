package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.customui.RotateImageView;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsProductInfoManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

import java.util.HashMap;
import java.util.Map;

public class AirpodsCaseCanvas extends SnapsPageCanvas {
    private static final String TAG = AirpodsCaseCanvas.class.getSimpleName();
    private final boolean isAirpodsPro;

    public AirpodsCaseCanvas(Context context) {
        super(context);

        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        isAirpodsPro = pdCode.equals(Const_PRODUCT.AIRPODS_PRO_CASE);
    }

    public AirpodsCaseCanvas(Context context, AttributeSet attr) {
        super(context, attr);

        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        isAirpodsPro = pdCode.equals(Const_PRODUCT.AIRPODS_PRO_CASE);
    }

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        super.setSnapsPage(page, number, isBg, previewBgColor);
    }

    @Override
    protected void loadShadowLayer() {

    }

    @Override
    protected void loadPageLayer() {

    }

    @Override
    protected void loadBonusLayer() {
        try {
            if (getSnapsPage().type.equalsIgnoreCase("page")) {
                ImageView skin = new ImageView(getContext());
                LayoutParams param = new LayoutParams(pageLayer.getLayoutParams());
                param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
                param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
                skin.setLayoutParams( param );

                SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                        .setContext(getContext())
                        .setResourceFileName(getSkinName())
                        .setSkinBackgroundView(skin).create());
                bonusLayer.addView( skin );
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        containerLayer.bringToFront();
    }

    private String getSkinName() {
        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        String caseColor = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);

        String fileName = "";

        if (isAirpodsPro) {
            Map<String, String> colorMap = new HashMap<>();
            colorMap.put(Const_VALUES.COLOR_CODE_BLACK, SnapsSkinConstants.AIRPODS_PRO_CASE_BLACK);
            colorMap.put(Const_VALUES.COLOR_CODE_LAVENDER, SnapsSkinConstants.AIRPODS_PRO_CASE_LAVENDAR);
            colorMap.put(Const_VALUES.COLOR_CODE_TRANSPARENCY, SnapsSkinConstants.AIRPODS_PRO_CASE_LIMPIDITY);
            colorMap.put(Const_VALUES.COLOR_CODE_MINT, SnapsSkinConstants.AIRPODS_PRO_CASE_MINT);
            colorMap.put(Const_VALUES.COLOR_CODE_NAVY, SnapsSkinConstants.AIRPODS_PRO_CASE_NAVY);
            colorMap.put(Const_VALUES.COLOR_CODE_PINK, SnapsSkinConstants.AIRPODS_PRO_CASE_PINK);
            colorMap.put(Const_VALUES.COLOR_CODE_SKYBLUE, SnapsSkinConstants.AIRPODS_PRO_CASE_SKYBLUE);
            colorMap.put(Const_VALUES.COLOR_CODE_WHITE, SnapsSkinConstants.AIRPODS_PRO_CASE_WHITE);
            colorMap.put(Const_VALUES.COLOR_CODE_YELLOW, SnapsSkinConstants.AIRPODS_PRO_CASE_YELLOW);
            fileName = colorMap.get(caseColor);
        }
        else {
            Map<String, String> colorMap = new HashMap<>();
            colorMap.put(Const_VALUES.COLOR_CODE_BLACK, SnapsSkinConstants.AIRPODS_CASE_BLACK);
            colorMap.put(Const_VALUES.COLOR_CODE_LAVENDER, SnapsSkinConstants.AIRPODS_CASE_LAVENDAR);
            colorMap.put(Const_VALUES.COLOR_CODE_TRANSPARENCY, SnapsSkinConstants.AIRPODS_CASE_LIMPIDITY);
            colorMap.put(Const_VALUES.COLOR_CODE_MINT, SnapsSkinConstants.AIRPODS_CASE_MINT);
            colorMap.put(Const_VALUES.COLOR_CODE_NAVY, SnapsSkinConstants.AIRPODS_CASE_NAVY);
            colorMap.put(Const_VALUES.COLOR_CODE_PINK, SnapsSkinConstants.AIRPODS_CASE_PINK);
            colorMap.put(Const_VALUES.COLOR_CODE_SKYBLUE, SnapsSkinConstants.AIRPODS_CASE_SKYBLUE);
            colorMap.put(Const_VALUES.COLOR_CODE_WHITE, SnapsSkinConstants.AIRPODS_CASE_WHITE);
            colorMap.put(Const_VALUES.COLOR_CODE_YELLOW, SnapsSkinConstants.AIRPODS_CASE_YELLOW);
            fileName = colorMap.get(caseColor);
        }

        return fileName;
    }

    @Override
    protected void initMargin() {
        if (isAirpodsPro) {
            SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
            String caseColor = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);

            if (caseColor.equals(Const_VALUES.COLOR_CODE_TRANSPARENCY)) {
                int offsetY = 10;
                leftMargin = 225;
                topMargin = 225 - offsetY;
                rightMargin = 225;
                bottomMargin = 225 + offsetY;
            }
            else {
                int offsetY = 15;
                leftMargin = 180;
                topMargin = 180 - offsetY;
                rightMargin = 180;
                bottomMargin = 180 + offsetY;
            }
        }
        else {
            int offsetY = 21;
            leftMargin = 160;
            topMargin = 160 - offsetY;
            rightMargin = 160;
            bottomMargin = 160 + offsetY;
        }
    }

    @Override
    protected void loadControlLayer() {
        super.loadControlLayer();

        int count = layoutLayer.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = layoutLayer.getChildAt(i);
            if (view instanceof RotateImageView) {
                //확대되서 +버튼이 너무 크게 나와서 축소
                view.setScaleX(0.5f);
                view.setScaleY(0.5f);
            }
            else if (view instanceof CustomImageView) {
                CustomImageView customImageView = (CustomImageView)view;
                if (customImageView.getLayoutControl().imgData == null) {
                    //사진 선택전에 사진 들어갈 영역을 표시
                    view.setAlpha(0.25f);
                }
            }
        }

        //배경 안그리기 <- 템플릿에서 투명으로 색을 설정해주면 이런짓 할 필요없는데...
        //원래 코드는 SnapsPageCanvas 클래스 안의 setBgLayer(..) 메소드 안의 bgView.setBackgroundColor(Color.TRANSPARENT); 를 조건에 따라 호출하는 것인데
        //안쪽이 점점 복잡해 지는 것 같아서 여기서 구현.. 일관성이....
        count = bgLayer.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = bgLayer.getChildAt(i);
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onDestroyCanvas() {
        if(bonusLayer != null) {
            Drawable d = bonusLayer.getBackground();
            if (d != null) {
                try {
                    d.setCallback(null);
                } catch (Exception ignore) {
                }
            }
        }
        super.onDestroyCanvas();
    }
}
