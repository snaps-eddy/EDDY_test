package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.snaps.common.image.RxImageLoader;
import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.SkinComposer;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.activity.edit.spc.base.BaseOverSkinCanvas;
import com.snaps.mobile.activity.edit.spc.base.SceneCapturable;
import com.snaps.mobile.activity.edit.thumbnail_skin.SnapsThumbNailSkinConstants;
import com.snaps.mobile.activity.ui.menu.renewal.GlideApp;
import com.snaps.mobile.utils.custom_layouts.AFrameLayoutParams;
import com.snaps.mobile.utils.select_product_junction.PhoneCaseSkinUrlGenerator;
import com.snaps.mobile.utils.select_product_junction.SkinType;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NewPhoneCaseCanvas extends BaseOverSkinCanvas implements SceneCapturable {

    private final int PHONE_SKIN_WIDTH = 380;
    private final int PHONE_SKIN_HEIGHT = 650;

    private static final String TAG = NewPhoneCaseCanvas.class.getSimpleName();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final RxImageLoader rxImageLoader = new RxImageLoader();
    private final SkinComposer skinComposer = new SkinComposer();

    private String deviceSkinUrl;
    private String caseSkinUrl;
    private String topSkinUrl;

    private boolean isCartThumbnailCanvas = false;
    private boolean isUvProduct = false;

    private Bitmap phoneMaskBitmap;
    private Bitmap phoneBackgroundBitmap;

    public NewPhoneCaseCanvas(Context context) {
        super(context);
        initSkinUrl(false);
    }

    public NewPhoneCaseCanvas(Context context, boolean isCartThumbnailCanvas) {
        super(context);
        initSkinUrl(isCartThumbnailCanvas);
    }

    private void initSkinUrl(boolean isCartThumbnailCanvas) {

        SnapsTemplate snapsTemplate = SnapsTemplateManager.getInstance().getSnapsTemplate();
        SnapsProductOption snapsProductOption = snapsTemplate.getProductOption();

        String caseCode = snapsProductOption.get(SnapsProductOption.KEY_PHONE_CASE_CASE_CODE);
        String caseColorCode = snapsProductOption.get(SnapsProductOption.KEY_PHONE_CASE_CASE_COLOR_CODE);
        String deviceColorCode = snapsProductOption.get(SnapsProductOption.KEY_PHONE_CASE_DEVICE_COLOR);

        this.isCartThumbnailCanvas = isCartThumbnailCanvas;
        this.isUvProduct = StringUtil.isNotEmpty(caseCode) && StringUtil.isNotEmpty(deviceColorCode);

        String caseSkinOption = PhoneCaseSkinUrlGenerator.makeOptionCode(isCartThumbnailCanvas ? SkinType.CartThumnailCase : SkinType.Case, caseCode, caseColorCode, deviceColorCode);
        String caseSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(isCartThumbnailCanvas ? SkinType.CartThumnailCase : SkinType.Case, Config.getPROD_CODE(), caseSkinOption);
        this.caseSkinUrl = SnapsAPI.DOMAIN() + caseSkinUrl;

        String topSkinOption = PhoneCaseSkinUrlGenerator.makeOptionCode(isCartThumbnailCanvas ? SkinType.CartThumnailTopSkin : SkinType.TopSkin, caseCode, caseColorCode, deviceColorCode);
        String topSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(isCartThumbnailCanvas ? SkinType.CartThumnailTopSkin : SkinType.TopSkin, Config.getPROD_CODE(), topSkinOption);
        this.topSkinUrl = SnapsAPI.DOMAIN() + topSkinUrl;

        if (isUvProduct) {
            String deviceSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Device, Config.getPROD_CODE(), deviceColorCode);
            this.deviceSkinUrl = SnapsAPI.DOMAIN() + deviceSkinUrl;
        }
    }

    @Override
    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        super.setSnapsPage(page, number, isBg, previewBgColor);
        if (isUvProduct) {
            loadSkins(deviceSkinUrl, caseSkinUrl, topSkinUrl);

        } else {
            ImageView topSkinView = createForegroundSkinView();
            overForegroundLayer.removeAllViews();
            overForegroundLayer.addView(topSkinView);

            /**
             * @Marko overBackgroundLayer 는 부모 코드에서 WRAP 으로 설정되어있는데, 이게 정확한 값이 아니면 줌 할때 문제가 발생하는 듯하다.
             * 그래서 overBackgroundLayer 를 사용하지 않더라도 고정 값으로 설정해주는 코드 추가함.
             */
            ViewGroup.LayoutParams params = overBackgroundLayer.getLayoutParams();
            params.width = PHONE_SKIN_WIDTH;
            params.height = PHONE_SKIN_HEIGHT;
            overBackgroundLayer.setLayoutParams(params);

            GlideApp.with(getContext())
                    .load(caseSkinUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(false)
                    .into(topSkinView);
        }
    }

    private void loadSkins(final String phoneSkinUrl, final String caseSkinUrl, final String topSkinUrl) {
        Context context = getContext();
        if (context == null) {
            Dlog.e(TAG, "Context is null, can not get skins.");
            return;
        }

        compositeDisposable.add(
                Single.zip(rxImageLoader.loadImageRx(context, phoneSkinUrl, 0, 0),
                        rxImageLoader.loadImageRx(context, caseSkinUrl, 0, 0),
                        rxImageLoader.loadImageRx(context, topSkinUrl, 0, 0),
                        this::createSkins)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onCompleteLoadSkins, this::onErrorLoadSkins)
        );
    }

    private boolean createSkins(Bitmap bitmapPhoneSkin, Bitmap bitmapCaseSkin, Bitmap bitmapTopSkin) {
        this.phoneMaskBitmap = bitmapTopSkin;
        this.phoneBackgroundBitmap = skinComposer.composeBackground(bitmapPhoneSkin, bitmapCaseSkin);
        return this.phoneMaskBitmap != null && this.phoneBackgroundBitmap != null;
    }

    private void onCompleteLoadSkins(boolean isSkinsReady) {
        if (isSkinsReady) {
            addFinalMaskView();
            addDeviceAndCaseSkinView();
        } else {
            // close cavas or show dialog ... etc...
            Dlog.e(TAG, "Failed load skins");
        }
    }

    private void addFinalMaskView() {
        if (phoneMaskBitmap == null) {
            return;
        }

        ImageView finalMaskView = createForegroundSkinView();
        finalMaskView.setImageBitmap(phoneMaskBitmap);
        overForegroundLayer.removeAllViews();
        overForegroundLayer.addView(finalMaskView);
    }

    private ImageView createForegroundSkinView() {
        ImageView phoneContent = new ImageView(getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(PHONE_SKIN_WIDTH, PHONE_SKIN_HEIGHT);
        params.gravity = Gravity.CENTER;
        phoneContent.setLayoutParams(params);
        phoneContent.setScaleType(ImageView.ScaleType.FIT_XY);

        return phoneContent;
    }

    private void addDeviceAndCaseSkinView() {
        if (phoneBackgroundBitmap == null) {
            return;
        }

        ImageView phoneBackgroundSkinView = createDeviceAndCaseSkinView();
        phoneBackgroundSkinView.setImageBitmap(phoneBackgroundBitmap);
        overBackgroundLayer.removeAllViews();
        overBackgroundLayer.addView(phoneBackgroundSkinView);
    }

    private ImageView createDeviceAndCaseSkinView() {
        ImageView phoneBackground = new ImageView(getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(PHONE_SKIN_WIDTH, PHONE_SKIN_HEIGHT);
        params.gravity = Gravity.CENTER;
        phoneBackground.setLayoutParams(params);
        phoneBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        return phoneBackground;
    }

    private void onErrorLoadSkins(Throwable throwable) {
        Dlog.e(TAG, throwable);
    }

    /**
     * @Marko 기존 카트 섬네일은 외부에서 생성하였는데, 어차피 캔버스의 요소를 이용하여 만들기 때문에
     * 내부에서 처리하는 걸로 수정함.
     * 다른 캔버스도 이러한 방식으로 수정할 예정.
     * <p>
     * 섬네일을은 장바구니 캔버스에만 요청하므로, 이 함수 자체가 섬네일 캔버스라는 뜻인데
     * 혹시 몰라서 방어코드 추가함.
     */
    @Override
    public Bitmap getThumbnailBitmap() {

        int maxWidth = overForegroundLayer.getWidth();
        int maxHeight = overForegroundLayer.getHeight();

        Bitmap captureBitmap = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(captureBitmap);

        float dx = (maxWidth - containerLayer.getWidth()) / 2.0f;
        float dy = (maxHeight - containerLayer.getHeight()) / 2.0f;

        // uv 제품이라면 디바이스 스킨 -> 케이스 스킨 -> 사용자 컨텐츠 -> 파이널 마스크
        // 프린팅 제품이라면 사용자 컨텐츠 -> 케이스 스킨
        // 순으로 그린다.
        if (isUvProduct) {
            overBackgroundLayer.draw(canvas);
        }

        if (isCartThumbnailCanvas) {
            hideTextViewOutLineAndPlaceHolder();
        }

        canvas.translate(dx, dy);
        containerLayer.draw(canvas);
        canvas.translate(-dx, -dy); // overforeground 레이어의 크기는 overbackground 와 같기 때문에 캔버스의 위치를 다시 원복한다.

        overForegroundLayer.draw(canvas);
        return captureBitmap;
    }

    @Override
    protected void loadControlLayer() {
        super.loadControlLayer();
        if (isCartThumbnailCanvas) {
            hideTextViewOutLineAndPlaceHolder();
        }
    }

    private void hideTextViewOutLineAndPlaceHolder() {
        // 이런 걸 부모에서 제공해줘야지 ...
        int size = controlLayer.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = controlLayer.getChildAt(i);
            if (view instanceof SnapsTextToImageView) {
                SnapsTextToImageView textView = (SnapsTextToImageView) view;
                textView.setVisibleOutLine(false);
                textView.setVisiblePlaceHolder(false);
            }
        }
    }

    @Override
    protected void loadBgLayer(String previewBgColor) {
        if (isUvProduct) {
            return;
        }

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

        bgView.setScaleType(ImageView.ScaleType.CENTER);
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
    public void onDestroyCanvas() {
        super.onDestroyCanvas();
        compositeDisposable.clear();

//        if (phoneMaskBitmap != null && !phoneMaskBitmap.isRecycled()) {
//            phoneMaskBitmap.recycle();
//        }
//
//        if (phoneBackgroundBitmap != null && !phoneBackgroundBitmap.isRecycled()) {
//            phoneBackgroundBitmap.recycle();
//        }
    }
}
