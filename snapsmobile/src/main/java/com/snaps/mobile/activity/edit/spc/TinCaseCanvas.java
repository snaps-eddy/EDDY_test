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
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.component.MaskImageView;

import java.util.HashMap;
import java.util.Map;

import static com.snaps.common.utils.constant.Const_PRODUCT.TIN_CASE_S_V;
import static com.snaps.common.utils.constant.Const_PRODUCT.TIN_CASE_M_V;
import static com.snaps.common.utils.constant.Const_PRODUCT.TIN_CASE_L_V;
import static com.snaps.common.utils.constant.Const_PRODUCT.TIN_CASE_S_H;
import static com.snaps.common.utils.constant.Const_PRODUCT.TIN_CASE_M_H;
import static com.snaps.common.utils.constant.Const_PRODUCT.TIN_CASE_L_H;

public class TinCaseCanvas extends SnapsPageCanvas {
    private static final String TAG = TinCaseCanvas.class.getSimpleName();

    public TinCaseCanvas(Context context) {
        super(context);
    }

    public TinCaseCanvas(Context context, AttributeSet attr) {
        super(context, attr);
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
        Map<String, Map<String, String>> thumbNailMap = new HashMap<>();

        Map<String, String> small_vertical = new HashMap<>();
        small_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsSkinConstants.TINCASE_WHITE_S_V);
        small_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsSkinConstants.TINCASE_BLACK_S_V);
        small_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsSkinConstants.TINCASE_SILVER_S_V);
        thumbNailMap.put(Const_PRODUCT.TIN_CASE_S_V, small_vertical);

        Map<String, String> middle_vertical = new HashMap<>();
        middle_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsSkinConstants.TINCASE_WHITE_M_V);
        middle_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsSkinConstants.TINCASE_BLACK_M_V);
        middle_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsSkinConstants.TINCASE_SILVER_M_V);
        thumbNailMap.put(Const_PRODUCT.TIN_CASE_M_V, middle_vertical);

        Map<String, String> larger_vertical = new HashMap<>();
        larger_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsSkinConstants.TINCASE_WHITE_L_V);
        larger_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsSkinConstants.TINCASE_BLACK_L_V);
        larger_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsSkinConstants.TINCASE_SILVER_L_V);
        thumbNailMap.put(Const_PRODUCT.TIN_CASE_L_V, larger_vertical);

        Map<String, String> small_horizontal = new HashMap<>();
        small_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsSkinConstants.TINCASE_WHITE_S_H);
        small_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsSkinConstants.TINCASE_BLACK_S_H);
        small_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsSkinConstants.TINCASE_SILVER_S_H);
        thumbNailMap.put(Const_PRODUCT.TIN_CASE_S_H, small_horizontal);

        Map<String, String> middle_horizontal = new HashMap<>();
        middle_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsSkinConstants.TINCASE_WHITE_M_H);
        middle_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsSkinConstants.TINCASE_BLACK_M_H);
        middle_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsSkinConstants.TINCASE_SILVER_M_H);
        thumbNailMap.put(Const_PRODUCT.TIN_CASE_M_H, middle_horizontal);

        Map<String, String> larger_horizontal = new HashMap<>();
        larger_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsSkinConstants.TINCASE_WHITE_L_H);
        larger_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsSkinConstants.TINCASE_BLACK_L_H);
        larger_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsSkinConstants.TINCASE_SILVER_L_H);
        thumbNailMap.put(Const_PRODUCT.TIN_CASE_L_H, larger_horizontal);

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        String caseColor = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);

        String fileName = thumbNailMap.get(Config.getPROD_CODE()).get(caseColor);
        return fileName;
    }

    @Override
    protected void initMargin() {
        switch (Config.getPROD_CODE()) {
            case TIN_CASE_S_V:
                leftMargin = 63;
                topMargin = 63;
                rightMargin = 63;
                bottomMargin = 63;
                break;
            case TIN_CASE_M_V:
                leftMargin = 85;
                topMargin = 85;
                rightMargin = 85;
                bottomMargin = 85;
                break;
            case TIN_CASE_L_V:
                leftMargin = 88;
                topMargin = 88;
                rightMargin = 88;
                bottomMargin = 88;
                break;
            case TIN_CASE_S_H:
                leftMargin = 63;
                topMargin = 63;
                rightMargin = 63;
                bottomMargin = 63;
                break;
            case TIN_CASE_M_H:
                leftMargin = 85;
                topMargin = 85;
                rightMargin = 85;
                bottomMargin = 85;
                break;
            case TIN_CASE_L_H:
                leftMargin = 88;
                topMargin = 88;
                rightMargin = 88;
                bottomMargin = 88;
                break;
        }
    }

    @Override
    protected void loadControlLayer() {
        super.loadControlLayer();

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        String caseColor = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);

        int count = layoutLayer.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = layoutLayer.getChildAt(i);
            if (view instanceof CustomImageView) {
                CustomImageView customImageView = (CustomImageView)view;
                if (customImageView.getLayoutControl().imgData == null) {
                    //사진 선택전에 사진 들어갈 영역을 표시
                    view.setAlpha(0.25f);
                    //틴 케이스가 흰색일때 사진 영역이 표시안되는 문제가 있어서 아래와 같이 추가
                    if (Const_VALUES.COLOR_CODE_TINCASE_WHITE.equals(caseColor)) {
                        MaskImageView maskImageView = (MaskImageView) customImageView.getImageView();
                        maskImageView.setColorFilter(Color.LTGRAY);
                    }
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
