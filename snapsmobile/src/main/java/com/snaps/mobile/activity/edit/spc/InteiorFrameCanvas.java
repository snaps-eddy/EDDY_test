package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.component.NewFrameBorderView;
import com.snaps.mobile.component.WoodFrameBorderView;

import java.util.HashMap;

public class InteiorFrameCanvas extends SnapsPageCanvas {
    private static final String TAG = InteiorFrameCanvas.class.getSimpleName();
    int addWidthByBorder = 0; // 보더로 인해 늘어나는 넓
    int frameBorderWidth = 0;


    int ovelapArea = 6;

    private HashMap<String, String> frameNameStringMap;
    private HashMap<String, Integer> frameBorderAdjustSizeMap;

    public InteiorFrameCanvas(Context context) {
        super(context);
        makeFrameData();
    }

    private void makeFrameData() {
        frameNameStringMap = new HashMap<String, String>();
        // 앤틱
        frameNameStringMap.put("045014000180", "antique_gold");
        frameNameStringMap.put("045014000179", "antique_ivory");
        frameNameStringMap.put("045014000181", "antique_purple");
        frameNameStringMap.put("045014000178", "antique_white");
        // classic
        frameNameStringMap.put("045014000182", "classic_black");
        frameNameStringMap.put("045014000184", "classic_brown");
        frameNameStringMap.put("045014000183", "classic_white");
        // royal
        frameNameStringMap.put("045014000177", "royal_brown");
        frameNameStringMap.put("045014000175", "royal_darkgold");
        frameNameStringMap.put("045014000176", "royal_silver");
        // simple
        frameNameStringMap.put("045014000172", "simple_black");
        frameNameStringMap.put("045014000174", "simple_gold");
        frameNameStringMap.put("045014000173", "simple_silver");
        // vintage
        frameNameStringMap.put("045014000185", "vintage_brown");
        frameNameStringMap.put("045014000187", "vintage_darkgray");
        frameNameStringMap.put("045014000186", "vintage_gold");

        frameBorderAdjustSizeMap = new HashMap<String, Integer>();
        // 앤틱
        frameBorderAdjustSizeMap.put("045014000180", 14);
        frameBorderAdjustSizeMap.put("045014000179", 14);
        frameBorderAdjustSizeMap.put("045014000181", 14);
        frameBorderAdjustSizeMap.put("045014000178", 14);
        // classic
        frameBorderAdjustSizeMap.put("045014000182", 25);
        frameBorderAdjustSizeMap.put("045014000184", 25);
        frameBorderAdjustSizeMap.put("045014000183", 25);
        // royal
        frameBorderAdjustSizeMap.put("045014000177", 15);
        frameBorderAdjustSizeMap.put("045014000175", 15);
        frameBorderAdjustSizeMap.put("045014000176", 15);
        // simple
        frameBorderAdjustSizeMap.put("045014000172", 10);
        frameBorderAdjustSizeMap.put("045014000174", 10);
        frameBorderAdjustSizeMap.put("045014000173", 10);
        // vintage
        frameBorderAdjustSizeMap.put("045014000185", 10);
        frameBorderAdjustSizeMap.put("045014000187", 10);
        frameBorderAdjustSizeMap.put("045014000186", 10);
    }

    @Override
    protected void loadShadowLayer() {
        // 매트 그리기.. 매트일때만 그린다.
        int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
        int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);

        float borderW = pxW / mmW * 2;

        for (SnapsControl control : getSnapsPage().getLayoutList()) {
            if (control instanceof SnapsLayoutControl) {

                if (control.regName.equals("") || control.regName.equals("background"))
                    continue;

                addBorderView(control, pageLayer, borderW);
            }
        }
    }

    @Override
    protected void loadPageLayer() {

    }

    @Override
    protected void loadBonusLayer() {
        try {
            SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
                    .setContext(getContext())
                    .setResourceFileName(getSkinResourceName())
                    .setSkinBackgroundView(bonusLayer)
                    .setSkinLoadListener(new SnapsSkinUtil.SnapsSkinLoadListener() {
                        @Override
                        public void onSkinLoaded() {
                            loadInteriorFrameShadow();
                        }
                    })
                    .create());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void loadInteriorFrameShadow() {
        int coverSkinRes = isHorizontalFrame() ? R.drawable.inteior_frame_horizontal_shadow : R.drawable.inteior_frame_vertical_shadow;
        shadowLayer.setBackgroundResource(coverSkinRes);

        shadowLayer.setScaleX(isHorizontalFrame() ? 1.21f : 1.31f);
        shadowLayer.setScaleY(isHorizontalFrame() ? 1.21f : 1.31f);

        containerLayer.setScaleX(0.9f);
        containerLayer.setScaleY(0.9f);

        bonusLayer.setScaleX(0.9f);
        bonusLayer.setScaleY(0.9f);
    }

    @Override
    protected void initMargin() {

        int pxW = Integer.parseInt(getSnapsPage().info.F_PAGE_PIXEL_WIDTH);
        int mmW = Integer.parseInt(getSnapsPage().info.F_PAGE_MM_WIDTH);
        ovelapArea = getSnapsPage().info.frameInfo.getFrameDugWidth();

        frameBorderWidth = pxW / mmW * (getSnapsPage().info.frameInfo.getFrameWidthByMM());
        addWidthByBorder = (int) (pxW / mmW * (getSnapsPage().info.frameInfo.getFrameWidthByMM() - ovelapArea));

        addWidthByBorder += getAdjustSize(); // 신스킨 적용중 추가.

        leftMargin = addWidthByBorder;
        topMargin = addWidthByBorder;
        rightMargin = addWidthByBorder;
        bottomMargin = addWidthByBorder;

        if (isThumbnailView()) {
            leftMargin = 0;
            rightMargin = 0;
            topMargin = 0;
            bottomMargin = 0;
        }
    }

    View addFrameBorder() {
        String imgUrl = SnapsAPI.DOMAIN() + getSnapsPage().info.frameInfo.getF_FRAME_IMG_URL();
        String resID = getSnapsPage().info.frameInfo.getRESOURCECODE();

        NewFrameBorderView borderView = new NewFrameBorderView(getContext(), imgUrl, resID, frameBorderWidth);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(getLayoutParams());// new ViewGroup.MarginLayoutParams(400, 400);
        borderView.setLayoutParams(new FrameLayout.LayoutParams(params));

        return borderView;
    }

    private String getSkinResourceName() {
        StringBuilder sb = new StringBuilder();
        sb.append("frame_interior_").append(getFrameTypeString()).append(getOrientationString()).append("thumb.png");

        return sb.toString();
    }

    private String getFrameTypeString() {
        String name = "";
        if (frameNameStringMap != null)
            name = frameNameStringMap.get(getSnapsPage().info.F_FRAME_ID);
        return name;
    }

    private int getAdjustSize() {
        int adjust = 0;
        if (frameBorderAdjustSizeMap != null)
            adjust = frameBorderAdjustSizeMap.get(getSnapsPage().info.F_FRAME_ID);
        return adjust;
    }

    private String getOrientationString() {
        return isHorizontalFrame() ? "_horizontal_" : "_vertical_";
    }

    private boolean isHorizontalFrame() {
        try {
            int width, height;
            width = Integer.parseInt(getSnapsPage().width);
            height = Integer.parseInt(getSnapsPage().height);
            return width > height;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    void addBorderView(SnapsControl control, FrameLayout pageLayer, float borderW) {
        WoodFrameBorderView border = new WoodFrameBorderView(getContext());
        // 오차로 인해 +2 적용
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(Integer.parseInt(control.width) + 2, Integer.parseInt(control.height) + 2);
        border.setLayoutParams(new FrameLayout.LayoutParams(params));

        border.setX(control.getX() - 1);
        border.setY(Integer.parseInt(control.y) - 1);
        border.setBorderWidth((int) borderW);

        pageLayer.addView(border);
    }

    @Override
    public void onDestroyCanvas() {
        super.onDestroyCanvas();
    }
}
