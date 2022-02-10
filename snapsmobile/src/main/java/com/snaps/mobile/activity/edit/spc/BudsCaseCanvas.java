package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.customui.RotateImageView;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

import java.util.HashMap;
import java.util.Map;

public class BudsCaseCanvas extends SnapsPageCanvas {
    private static final String TAG = BudsCaseCanvas.class.getSimpleName();

    public BudsCaseCanvas(Context context) {
        super(context);
    }

    public BudsCaseCanvas(Context context, AttributeSet attr) {
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
                LayoutParams param = new LayoutParams(bonusLayer.getLayoutParams());
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

        Map<String, String> colorMap = new HashMap<>();
        colorMap.put(Const_VALUES.COLOR_CODE_BLACK, SnapsSkinConstants.BUDS_CASE_BLACK);
        colorMap.put(Const_VALUES.COLOR_CODE_LAVENDER, SnapsSkinConstants.BUDS_CASE_LAVENDAR);
        colorMap.put(Const_VALUES.COLOR_CODE_TRANSPARENCY, SnapsSkinConstants.BUDS_CASE_LIMPIDITY);
        colorMap.put(Const_VALUES.COLOR_CODE_MINT, SnapsSkinConstants.BUDS_CASE_MINT);
        colorMap.put(Const_VALUES.COLOR_CODE_NAVY, SnapsSkinConstants.BUDS_CASE_NAVY);
        colorMap.put(Const_VALUES.COLOR_CODE_PINK, SnapsSkinConstants.BUDS_CASE_PINK);
        colorMap.put(Const_VALUES.COLOR_CODE_SKYBLUE, SnapsSkinConstants.BUDS_CASE_SKYBLUE);
        colorMap.put(Const_VALUES.COLOR_CODE_WHITE, SnapsSkinConstants.BUDS_CASE_WHITE);
        colorMap.put(Const_VALUES.COLOR_CODE_YELLOW, SnapsSkinConstants.BUDS_CASE_YELLOW);
        String fileName = colorMap.get(caseColor);

        return fileName;
    }

    @Override
    protected void initMargin() {
        leftMargin = 160;
        topMargin = 160;
        rightMargin = 160;
        bottomMargin = 160;
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
}
