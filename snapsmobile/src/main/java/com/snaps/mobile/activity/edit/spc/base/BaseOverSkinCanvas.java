package com.snaps.mobile.activity.edit.spc.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

/**
 * @Marko 새로운 폰케이스처럼 신 크기보다 큰 스킨을 사용해야할 때 사용.
 * 신보다 아래에 있는 스킨은 overBackgroundLayer 에
 * 신보다 위에 있는 스킨은 overForegroundLayer 에 추가한다.
 * 2020.09.03 -> 이 두 레이어에 있는 정보는 xml 에 기록되지 않는다.
 */
public abstract class BaseOverSkinCanvas extends SnapsPageCanvas {

    private static final String TAG = BaseOverSkinCanvas.class.getSimpleName();

    public BaseOverSkinCanvas(Context context) {
        super(context);
    }

    public BaseOverSkinCanvas(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void loadShadowLayer() {
    }

    @Override
    protected void loadPageLayer() {
    }

    @Override
    protected void loadBonusLayer() {
    }

    @Override
    protected void initMargin() {
    }

    @Override
    protected void loadBgLayer(String previewBgColor) {
    }

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        this._snapsPage = page;
        this._page = number;
        // SnapsPageCanvas를 하나만 사용할 경우.
        removeItems(this);

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());
        layout.setMargins(0, 0, 0, 0);
        // 페이지의 크기를 구한다.
        this.width = page.getWidth();
        this.height = Integer.parseInt(page.height);

        initMargin();

        layout.width = this.width + leftMargin + rightMargin;
        layout.height = this.height + topMargin + bottomMargin;

        edWidth = layout.width;
        edHeight = layout.height;

        this.setLayoutParams(new ARelativeLayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        // Shadow 초기화.
        RelativeLayout.LayoutParams shadowlayout = new RelativeLayout.LayoutParams(layout.width, layout.height);
        shadowLayer = new FrameLayout(this.getContext());
        shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        shadowLayer.setTag("Shadow");
        this.addView(shadowLayer);

        // Over layer를 위한 파라미터
        RelativeLayout.LayoutParams overLayerParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        overLayerParams.addRule(CENTER_IN_PARENT, RelativeLayout.TRUE);
        overBackgroundLayer = new FrameLayout(this.getContext());
        overBackgroundLayer.setLayoutParams(new ARelativeLayoutParams(overLayerParams));
        this.addView(overBackgroundLayer);

        ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);
        containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        containerLayer = new SnapsFrameLayout(this.getContext());
        containerLayer.setLayout(new RelativeLayout.LayoutParams(containerlayout));
        this.addView(containerLayer);

        overForegroundLayer = new FrameLayout(this.getContext());
        overForegroundLayer.setLayoutParams(new ARelativeLayoutParams(overLayerParams));
        this.addView(overForegroundLayer);

        bonusLayer = new FrameLayout(this.getContext());
        bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        this.addView(bonusLayer);

        // bgLayer 초기화.
        RelativeLayout.LayoutParams baseLayout = new RelativeLayout.LayoutParams(this.width, this.height);
        RelativeLayout.LayoutParams kakaobookLayout = null;

        bgLayer = new FrameLayout(this.getContext());
        bgLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));

        containerLayer.addView(bgLayer);

        // layoutLayer 초기화.
        layoutLayer = new FrameLayout(this.getContext());
        layoutLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(layoutLayer);

        // controllLayer 초기화. ppppoint
        controlLayer = new FrameLayout(this.getContext());
        controlLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(controlLayer);

        layoutLayer.setPadding(0, 0, 0, 0);
        controlLayer.setPadding(0, 0, 0, 0);

        // formLayer 초기화.
        formLayer = new FrameLayout(this.getContext());
        formLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(formLayer);

        // pageLayer 초기화.
        pageLayer = new FrameLayout(this.getContext());
        pageLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(pageLayer);

        //이미지 로딩 완료 체크 객체 생성
        initImageLoadCheckTask();

        // Back Ground 설정.
        loadBgLayer(previewBgColor);

        requestLoadAllLayerWithDelay(DELAY_TIME_FOR_LOAD_IMG_LAYER);

        setBackgroundColorIfSmartSnapsSearching();
    }

    @Override
    public void onDestroyCanvas() {
        if (pageLayer != null) {
            Drawable d = pageLayer.getBackground();
            if (d != null) {
                try {
                    d.setCallback(null);
                } catch (Exception ignore) {
                }
            }
        }
        if (shadowLayer != null) {
            Drawable d = shadowLayer.getBackground();
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
