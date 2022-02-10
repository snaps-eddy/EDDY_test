package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsTemplateInfo.COVER_TYPE;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

import errorhandle.logger.Logg;
import font.FTextView;

public class ThemeBookCanvas extends SnapsPageCanvas {
    private static final String TAG = ThemeBookCanvas.class.getSimpleName();

    public ThemeBookCanvas(Context context) {
        super(context);
    }

    public ThemeBookCanvas(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void loadShadowLayer() {
        try {
            if (getSnapsPage().info.getCoverType() == COVER_TYPE.HARD_COVER) {
                // 투명이미지
                if (getSnapsPage().type.equalsIgnoreCase("cover")) {
                    shadowLayer.setBackgroundResource(R.drawable.book_hard_cover_bg);
                } else {
                    if (!getSnapsPage().info.F_PAPER_CODE.equals("160008"))
                        shadowLayer.setBackgroundResource(R.drawable.skin_a4_cover_);
                    else
                        shadowLayer.setBackgroundResource(R.drawable.skin_a4_rayflat_cover);

                }
            } else {
                shadowLayer.setBackgroundResource(R.drawable.book_soft_page_bg);
            }
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void loadPageLayer() {
        try {
            // TODO Auto-generated method stub
            if (getSnapsPage().type.equalsIgnoreCase("page") || getSnapsPage().type.equalsIgnoreCase("title")) {
                pageLayer.setBackgroundResource(R.drawable.skin_a4);// 내지
            } else if (getSnapsPage().type.equalsIgnoreCase("cover")) {
                if (getSnapsPage().info.getCoverType() == COVER_TYPE.HARD_COVER)
                    ;// 커버
            }
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void loadBonusLayer() {
        //if (!isRealPagerView()) return; //썸네일 뷰에는 안 그린다.

        if (getSnapsPage().type.equalsIgnoreCase("title")) {
            formLayer.addView(getDisablePrintViewBackground(getSnapsPage()));
            if (isRealPagerView()) {
                formLayer.addView(getDisablePrintViewText(getSnapsPage()));
            }
        }
    }

    @Override
    protected void initMargin() {
        if (getSnapsPage().info.getCoverType() == COVER_TYPE.HARD_COVER) {
            leftMargin = Config.THEMEBOOK_MARGIN_LIST[0];
            topMargin = Config.THEMEBOOK_MARGIN_LIST[1];
            rightMargin = Config.THEMEBOOK_MARGIN_LIST[2];
            bottomMargin = Config.THEMEBOOK_MARGIN_LIST[3];

            // 와이드 여부 판단. 와이드 보정
            int w = getSnapsPage().getWidth() / 2;
            int h = (int) Float.parseFloat(getSnapsPage().height);

            // 와이드
            int wide_x_margin = 0;
            int wide_y_margin = 0;
            if (w + 20 > h) {
                wide_x_margin = 1;
                wide_y_margin = -1;
            }

            cover_leftMargin = Config.THEMEBOOK_HARDCOVER_MARGIN_LIST[0] + wide_x_margin;
            cover_topMargin = Config.THEMEBOOK_HARDCOVER_MARGIN_LIST[1] + wide_y_margin;
            cover_rightMargin = Config.THEMEBOOK_HARDCOVER_MARGIN_LIST[2] + wide_x_margin;
            cover_bottomMargin = Config.THEMEBOOK_HARDCOVER_MARGIN_LIST[3] + wide_y_margin;

        } else {

            // 와이드 여부 판단. 와이드 보정
            int w = getSnapsPage().getWidth() / 2;
            int h = (int) Float.parseFloat(getSnapsPage().height);

            // 와이드
            int wide_x_margin = 0;
            int wide_y_margin = 0;
            if (w + 20 > h) {
                wide_x_margin = 2;
                wide_y_margin = -4;
            }

            leftMargin = Config.THEMEBOOK_SOFT_MARGIN_LIST[0] + wide_x_margin;
            topMargin = Config.THEMEBOOK_SOFT_MARGIN_LIST[1] + wide_y_margin;
            rightMargin = Config.THEMEBOOK_SOFT_MARGIN_LIST[2] + wide_x_margin;
            bottomMargin = Config.THEMEBOOK_SOFT_MARGIN_LIST[3] + wide_y_margin;
        }

        if (isThumbnailView()) {
            cover_leftMargin = 0;
            cover_rightMargin = 0;
            cover_topMargin = 0;
            cover_bottomMargin = 0;

            leftMargin = 0;
            rightMargin = 0;
            topMargin = 0;
            bottomMargin = 0;
        }
    }

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        this._snapsPage = page;
        if (Config.isSimpleMakingBook()) { // 151007 간편만들기 만들때 추가페이지에 인덱스가 안들어가서 추가해줌.
            this._snapsPage.setPageLayoutIDX(number);
            for (SnapsControl control : this._snapsPage.getLayoutList())
                control.setPageIndex(number);
        }

        this._page = number;
        // SnapsPageCanvas를 하나만 사용할 경우.
        removeItems(this);

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());
        layout.setMargins(0, 0, 0, 0);
        // 페이지의 크기를 구한다.
        this.width = page.getWidth();
        this.height = Integer.parseInt(page.height);

        initMargin();

        if (page.type.equals("cover") && page.info.getCoverType() == COVER_TYPE.HARD_COVER) {
            layout.width = this.width + cover_leftMargin + cover_rightMargin;
            layout.height = this.height + cover_topMargin + cover_bottomMargin;
        } else {
            layout.width = this.width + leftMargin + rightMargin;
            layout.height = this.height + topMargin + bottomMargin;
        }

        edWidth = layout.width;
        edHeight = layout.height;

        this.setLayoutParams(new ARelativeLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Shadow 초기화.
        RelativeLayout.LayoutParams shadowlayout = new RelativeLayout.LayoutParams(layout.width, layout.height);
        shadowLayer = new FrameLayout(this.getContext());

        shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
        this.addView(shadowLayer);

        ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);
        if (page.type.equals("cover") && page.info.getCoverType() == COVER_TYPE.HARD_COVER) {
            containerlayout.setMargins(cover_leftMargin, cover_topMargin, cover_rightMargin, cover_bottomMargin); //TODO  핀치 줌 뷰로 바뀌면서 그리는 방식이 바뀌어서 마진값을 다 적용 해 줘야 한다...다른 곳도 이런 게 있는 지 확인 필요..
        } else {
            containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        }

        containerLayer = new SnapsFrameLayout(this.getContext());
        containerLayer.setLayout(new RelativeLayout.LayoutParams(containerlayout));
        this.addView(containerLayer);

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
        layoutLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
        containerLayer.addView(layoutLayer);

        // controllLayer 초기화. ppppoint
        controlLayer = new FrameLayout(this.getContext());
        controlLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
        containerLayer.addView(controlLayer);

        layoutLayer.setPadding(0, 0, 0, 0);
        controlLayer.setPadding(0, 0, 0, 0);

        if (Config.isThemeBook() && page.getBgList().size() <= 0)
            layoutLayer.setBackgroundColor(Color.WHITE);

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

        Dlog.d("setSnapsPage() page number:" + number);
    }

    @Override
    protected void loadAllLayers() {
        if (isSuspendedLayerLoad()) {
            hideProgressOnCanvas();
            return;
        }

        // Layout 설정
        loadLayoutLayer();

        loadFormLayer();

        // Page 이미지 설정.
        loadPageLayer();

        // 추가 Layer 설정.
        loadBonusLayer();

        // Control 설정.
        loadControlLayer();

        setPinchZoomScaleLimit(_snapsPage);

        setScaleValue();

        // 이미지 로드 완료 설정.
        imageLoadCheck();
    }

    View getDisablePrintViewBackground(SnapsPage titlePage) {
        int pageWidth = Integer.parseInt((isThumbnailView() ? titlePage.info.F_THUMBNAIL_PAGE_PIXEL_WIDTH : titlePage.info.F_PAGE_PIXEL_WIDTH)) / 2;
        int pageHeight = Integer.parseInt((isThumbnailView() ? titlePage.info.F_THUMBNAIL_PAGE_PIXEL_HEIGHT : titlePage.info.F_PAGE_PIXEL_HEIGHT));

        View whiteBgView = new View(getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(pageWidth, pageHeight);
        whiteBgView.setLayoutParams(new FrameLayout.LayoutParams(params));
        whiteBgView.setBackgroundColor(Color.WHITE);

        return whiteBgView;
    }

    View getDisablePrintViewText(SnapsPage titlePage) {
        int pageWidth = Integer.parseInt((isThumbnailView() ? titlePage.info.F_THUMBNAIL_PAGE_PIXEL_WIDTH : titlePage.info.F_PAGE_PIXEL_WIDTH)) / 2;
        int pageHeight = Integer.parseInt((isThumbnailView() ? titlePage.info.F_THUMBNAIL_PAGE_PIXEL_HEIGHT : titlePage.info.F_PAGE_PIXEL_HEIGHT));

        TextView disableText = new FTextView(getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(pageWidth, pageHeight);
        disableText.setLayoutParams(new FrameLayout.LayoutParams(params));
        disableText.setGravity(Gravity.CENTER);
        disableText.setText(R.string.no_print_page);
        disableText.setTextColor(Color.argb(255, 142, 142, 142));
        disableText.setTextSize(UIUtil.convertPixelsToSp(getContext(), 14.f));

        return disableText;
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
