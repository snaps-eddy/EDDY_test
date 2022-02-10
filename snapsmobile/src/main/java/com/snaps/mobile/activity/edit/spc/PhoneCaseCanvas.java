package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.structure.SnapsFrameInfo;
import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsSceneMaskControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.utils.custom_layouts.AFrameLayoutParams;
import com.snaps.mobile.utils.select_product_junction.PhoneCaseSkinUrlGenerator;
import com.snaps.mobile.utils.select_product_junction.SkinType;

public class PhoneCaseCanvas extends ThemeBookCanvas {

    private static final String TAG = PhoneCaseCanvas.class.getSimpleName();
    private String caseSkin;

    public PhoneCaseCanvas(Context context) {
        super(context);
        initialize(false);
    }

    public PhoneCaseCanvas(Context context, boolean isCartThumbnailCanvas) {
        super(context);
        initialize(isCartThumbnailCanvas);
    }

    private void initialize(boolean isCartThumbnailCanvas) {
        SnapsTemplate snapsTemplate = SnapsTemplateManager.getInstance().getSnapsTemplate();
        SnapsProductOption snapsProductOption = snapsTemplate.getProductOption();
        
        String caseCode = snapsProductOption.get(SnapsProductOption.KEY_PHONE_CASE_CASE_CODE);
        caseSkin = SnapsAPI.DOMAIN() + PhoneCaseSkinUrlGenerator.generateSkinUrl(isCartThumbnailCanvas ? SkinType.CartThumnailCase : SkinType.Case, Config.getPROD_CODE(), caseCode);
    }

    @Override
    protected void loadPageLayer() {
        // 핸드폰 케이스 커버 추가.
        View coverView = getCoverView();
        if (coverView != null) {
            pageLayer.addView(coverView);
        }
    }

    private View getCoverView() {
        try {
            int width = getSnapsPage().getWidth();
            int height = Integer.parseInt(getSnapsPage().height);

            ImageView springV = new ImageView(getContext());

            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, height);
            springV.setLayoutParams(new FrameLayout.LayoutParams(params));
            springV.setScaleType(ScaleType.FIT_XY);

            SnapsTemplate snapsTemplate = ((SnapsEditActExternalConnectionBridge) getContext()).getTemplate();
            String url = "";
            SnapsFrameInfo info = snapsTemplate.info.frameInfo;

            if (info != null && info.getF_FRAME_IMG_URL() != null && info.getF_FRAME_IMG_URL().length() > 0) {
                url = info.getF_FRAME_IMG_URL();
                saveCaseImageFromUrl(SnapsAPI.DOMAIN() + url, springV);
            }

            return springV;

        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    private void saveCaseImageFromUrl(final String url, final ImageView iv) {
        ATask.executeVoidWithThreadPool(new ATask.OnTaskBitmap() {
            @Override
            public void onPre() {
            }

            @Override
            public void onPost(Bitmap bitmap) {
                if (bitmap == null) {
                    ImageLoader.with(getContext()).load(url).into(iv);
                } else iv.setImageBitmap(bitmap);
            }

            @Override
            public Bitmap onBG() {
                Bitmap bitmap = getCaseBitmapFromLocalFile(getFileNameFromUrl(url)); // 파일 저장된거 있나 한번 만들어 보고.
                if (bitmap == null) {
                    HttpUtil.saveUrlToFile(url, Const_VALUE.PATH_PACKAGE(getContext(), true) + "/skin/" + getFileNameFromUrl(url)); // 없으면 다운받아서 파일로 쓴 다음에.
                    bitmap = getCaseBitmapFromLocalFile(getFileNameFromUrl(url)); // 다시한번.
                }
                return bitmap;
            }
        });
    }

    private Bitmap getCaseBitmapFromLocalFile(String fileName) {
        if (fileName == null || fileName.length() < 1) return null;

        String filePath = Const_VALUE.PATH_PACKAGE(getContext(), true) + "/skin/" + fileName;
        Bitmap b = BitmapFactory.decodeFile(filePath);
        return b;
    }

    private String getFileNameFromUrl(String url) {
        String name = "";

        String[] temp = url.split("/");
        if (temp.length > 0) name = temp[temp.length - 1];

        return name;
    }

    @Override
    protected void loadBgLayer(String previewBgColor) {
        for (SnapsControl bg : _snapsPage.getBgList()) {
            setBackgroundLayer((SnapsBgControl) bg, previewBgColor);
        }
    }

    private void setBackgroundLayer(SnapsBgControl bgControl, String previewBgColor) {
        SnapsImageView bgView = new SnapsImageView(getContext());
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(new LayoutParams(this.width, this.height));

        bgLayerForegroundImageView = new SnapsImageView(getContext());
        bgLayerForegroundImageView.setLayoutParams(layout);

        if (isRealPagerView()) {
            bgView.setSnapsControl(bgControl);
            int generatedId = ViewIDGenerator.generateViewId(bgControl.getControlId());
            bgControl.setControlId(generatedId);
            bgView.setId(generatedId);
        }

        bgView.setScaleType(ScaleType.CENTER);
        bgView.setLayoutParams(new AFrameLayoutParams(layout));

        if (bgControl.type.equalsIgnoreCase("webitem")) {
            String url = "";

            if (bgControl.resourceURL.equals("")) {
                url = SnapsAPI.GET_API_RESOURCE_IMAGE() + "&rname=" + bgControl.srcTarget + "&rCode=" + bgControl.srcTarget;

            } else {
                if (bgControl.resourceURL.contains(SnapsAPI.DOMAIN(false))) {
                    url = bgControl.resourceURL;
                } else {
                    url = SnapsAPI.DOMAIN(false) + bgControl.resourceURL;
                }

                bgView.setBackgroundColor(Color.parseColor("#" + "ffffffff"));
            }
            loadImage(url, bgView, 3, Integer.parseInt(bgControl.angle), null);
        }

        if (!bgControl.bgColor.equalsIgnoreCase("")) {
            bgView.setBackgroundColor(Color.parseColor("#" + bgControl.bgColor));
        }

        float alpha = Float.parseFloat(bgControl.alpha);
        bgView.setAlpha(alpha);

        bgLayer.addView(bgView);
        bgLayer.addView(bgLayerForegroundImageView);

    }

    @Override
    protected void loadShadowLayer() {
        // Nothing to do
        // Parent 에서 아무것도 안하게 막을 용도 ;;;;
    }

    @Override
    protected void loadBonusLayer() {
        // Nothing to do
        // Parent 에서 아무것도 안하게 막을 용도 ;;;;
    }

    @Override
    protected void initMargin() {
        // Nothing to do
        // Parent 에서 아무것도 안하게 막을 용도 ;;;;
    }

}
