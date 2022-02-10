package com.snaps.common.spc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.snaps.common.customui.RotateImageView;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.ISmartSnapImgDataAnimationState;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.imp.iSnapsPageCanvasInterface;
import com.snaps.common.snaps_image_proccesor.image_coordinate_processor.ImageCoordinateCalculator;
import com.snaps.common.snaps_image_proccesor.image_coordinate_processor.recoder.ImageCoordinateInfo;
import com.snaps.common.snaps_image_proccesor.image_load_checker.ImageLoadCheckTask;
import com.snaps.common.snaps_image_proccesor.image_load_checker.interfaces.IImageLoadCheckListener;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.spc.view.ImageLoadView;
import com.snaps.common.spc.view.SnapsImageView;
import com.snaps.common.spc.view.SnapsMovableImageView;
import com.snaps.common.spc.view.SnapsTextView;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsFormControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageUtil;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.component.ColorBorderView;
import com.snaps.mobile.utils.custom_layouts.AFrameLayoutParams;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;
import com.snaps.mobile.utils.custom_layouts.ZoomableRelativeLayout;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;
import com.snaps.mobile.utils.ui.SnapsImageViewTargetParams;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * com.snaps.kakao.activity.edit.spc SnapsPageCanvas.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 23.
 * @Version :
 */
public abstract class SnapsPageCanvas extends ZoomableRelativeLayout implements ISnapsHandler, IImageLoadCheckListener {
    private static final String TAG = SnapsPageCanvas.class.getSimpleName();
    public static final String TAG_IMAGEVIEW_NO_PRINT_IMAGE_ALERT = "NoPrintImage_Alert";

    public static final int SCALE_THUMBNAIL_MAX_OFFSET = 800; //일기 리스트에 나오는 썸네일은 EDSize를 SCALE_THUMBNAIL_MAX_OFFSET 기준으로 확대한다.

    protected static final int MSG_CALLBACK_POST_IMAGE_LOAD_COMLETED = 1; //로딩이 완료 되었을 때 호출 해야 함.
    protected static final int MSG_CALLBACK_HIDE_THUMBNAIL_PROGRESS = 2; //썸네일에 보이는 프로그레스를 숨김
    protected static final int MSG_CALLBACK_HIDE_IMAGE_LOAD_PROGRESS = 3; //중앙 페이저에 보이는 프로그레스를 숨김
    protected static final int MSG_CALLBACK_LOAD_LAYERS = 4; //레이어 로딩
    protected static final int MSG_NOTIFY_TEXT_TO_IMAGEVIEW = 5;
    protected static final int MSG_CHECK_LONG_CLICK = 6;
    protected static final int MSG_LOAD_TRANSPARENCY_PHOTO_CARD_BITMAP = 7;
    protected static final int MSG_LOAD_DIY_STICKER_BITMAP = 8;

    public static final int DELAY_TIME_FOR_LOAD_IMG_LAYER = 300;    //layerControl들을 로딩할 때 딜레이
    private static final int MAX_PROGRESS_TIME = 30000; //프로그래스는 최대 30초 까지만...
    protected static final int IMAGE_LOAD_PROGRESS_DIMENSION = 45; //페이저에 보여지는 프로그래스바의 크기
    protected static final int TIME_OF_LONG_PRESS = 500;

    public static final int MAX_CACHE_SIZE = 1200;
    public static final int MEDIUM_CACHE_SIZE = 720;
    public static final int DEFALUT_CACHE_SIZE = 440; //메모리 이슈때문에 좀 줄임. 480도 에러가 많이 남..
    public static final int DIARY_CACHE_SIZE = MAX_CACHE_SIZE;
    public static final int DIARY_SMALL_CACHE_SIZE = 200;
    public static final int THUMBNAIL_CACHE_SIZE = 150;
    public static final int CALENDAR_CACHE_SIZE = 0;
    public static final int PACKAGE_KIT_CACHE_SIZE = MEDIUM_CACHE_SIZE;

    protected int width;
    protected int height;
    /** 이미지 로드 Count */
    /**
     * 페이지 number
     */
    protected int _page;
    protected boolean _isBg;
    protected String _previewBgColor;
    /**
     * SnapsPage 데이터
     */
    protected SnapsPage _snapsPage;
    /** New Image Library */

    /**
     * Layer Layout
     **/
    protected FrameLayout shadowLayer;
    protected FrameLayout pageLayer;
    protected FrameLayout bonusLayer;
    protected FrameLayout shadowLayer2;
    protected FrameLayout multiFlyLayer;
    protected FrameLayout buttonLayer;
    protected ShimmerFrameLayout effectLayer;
    /**
     * 신 크기보다 크게 그려져야할 컨트롤이 있을 경우 ->
     */
    protected FrameLayout overBackgroundLayer;
    protected FrameLayout overForegroundLayer;

    protected SnapsFrameLayout containerLayer;

    protected FrameLayout bgLayer;
    protected FrameLayout layoutLayer;
    protected FrameLayout controlLayer;
    protected FrameLayout formLayer;

    protected View selectorView = null;
    protected boolean isClickAction = false;

    protected Bitmap bgLayerForegroundBitmap = null;
    protected BitmapDrawable bgLayerForegroundDrawable = null;
    protected SnapsImageView bgLayerForegroundImageView;

    protected int edWidth;
    protected int edHeight;

    protected iSnapsPageCanvasInterface _callback;

    protected float mScaleX = 1.f;
    protected float mScaleY = 1.f;

    // 테마북에서 + 버튼을 보일지 말지 설정하는 변
    private boolean isEnableButton = true; // false 보이지 않기 true 보이기..

    //장바구니에 저장할 때, 캡쳐를 하기 위해 생성된 Page인지
    private boolean isPageSaving = false;

    protected Context mContext = null;

    private ProgressBar thumbnailProgress = null;
    private ProgressBar imageLoadProgress = null;

    protected SnapsHandler mHandler = null;

    private boolean isCanvasDestroyed = false;

    private ImageLoadCheckTask mImageLoadCheckTask = null;
    private ImageCoordinateCalculator mImageCoordinateCalculator = null;

    private Set<CustomImageView> imageViews = null;

    private boolean isSuspendLayerLoad = false;
    protected boolean isLoadedShadowLayer = false;
    private boolean isScaledThumbnailMakeMode = false; // 썸네일 생성할때, 컨트롤들을 강제로 크게 해서 그린다.

    /**
     * 콜백 설정.
     *
     * @param callback
     */
    public void setCallBack(iSnapsPageCanvasInterface callback) {
        _callback = callback;
    }

    public void setThumbnailProgress(ProgressBar progress) {
        this.thumbnailProgress = progress;
    }

    public float getFitScaleX() {
        return mScaleX;
    }

    public float getFitScaleY() {
        return mScaleY;
    }

    public SnapsPageCanvas(Context context) {
        super(context);

        init(context);
    }

    public SnapsPageCanvas(Context context, AttributeSet attr) {
        super(context, attr);

        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mHandler = new SnapsHandler(this);

        isCanvasDestroyed = false;

        addPinchZoomListener();

        imageViews = new HashSet<>();

        if (shouldDisableHardwareAccelerate()) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private boolean shouldDisableHardwareAccelerate() {
        if (Config.isPhotobooks() || Config.isCalendar() || Config.isSnapsSticker() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isBabyNameStikerGroupProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct() || Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isSealStickerProduct()) {
            return false;
        }
        if (Const_PRODUCT.isDesignNoteProduct()) {
            return true;
        }
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        if (snapsTemplateManager != null) {
            SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
            return snapsTemplate != null && snapsTemplate.getPages() != null && snapsTemplate.getPages().size() < 3;
        }

        return false;
    }

    public void setBgColor(int color) {
        this.setBackgroundColor(color);
    }

    private void addPinchZoomListener() {
        setZoomViewTouchListener(ev -> {
            boolean handled = false;
            if (bonusLayer != null) {
                handled = bonusLayer.dispatchTouchEvent(ev);
            }
            //버튼레이아웃에 먼저 터치이벤트를 넘겨준다
            if (Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isSealStickerProduct()) {
                if (buttonLayer != null) {
                    handled = buttonLayer.dispatchTouchEvent(ev);
                }
            }
            if (containerLayer != null && !handled) {
                containerLayer.dispatchTouchEvent(ev);
            }
        });
    }

    protected void setPinchZoomScaleLimit(SnapsPage page) {
        float limitScaleRatio = LIMIT_SCALE_RATIO_SINGLE_PAGE_TYPE;
        if (Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook() || Const_PRODUCT.isLayFlatBook()
                || Const_PRODUCT.isSNSBook()) {
            limitScaleRatio = LIMIT_SCALE_RATIO_BOTH_PAGE_TYPE;
        }

        if (Const_PRODUCT.isCardShapeFolder() && page != null && page.type != null && page.type.equalsIgnoreCase(SnapsPage.PAGETYPE_PAGE)) {
            limitScaleRatio = LIMIT_SCALE_RATIO_BOTH_PAGE_TYPE;
        }

        setScaleLimit(limitScaleRatio);
    }

    public boolean isEnableButton() {
        return isEnableButton;
    }

    public void setEnableButton(boolean isEnableButton) {
        this.isEnableButton = isEnableButton;
    }

    public boolean isPageSaving() {
        return isPageSaving;
    }

    public void setIsPageSaving(boolean isPageSaving) {
        this.isPageSaving = isPageSaving;
    }

    public void suspendLayerLoad() {
        isSuspendLayerLoad = true;
    }

    public boolean isSuspendedLayerLoad() {
        return isSuspendLayerLoad;
    }

    /**
     * View 아이템 삭제.
     *
     * @param view
     */
    protected void removeItems(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                removeItems(((ViewGroup) view).getChildAt(i));
            }

            ((ViewGroup) view).removeAllViews();
        }
    }

    public int get_page() {
        return _page;
    }

    public void set_page(int _page) {
        this._page = _page;
    }

    /**
     * 배경 변경.
     */
    public void changeBgLayer() {

        removeItems(bgLayer);
        loadBgLayer();
    }

    public void changeBgLayer(String previewBgColor) {
        removeItems(bgLayer);
        loadBgLayer(previewBgColor);
    }

    /**
     * Layout 변경.
     */
    public void changeLayoutLayer() {

        removeItems(layoutLayer);
        loadLayoutLayer();
    }

    /**
     * Control 변경.
     */
    public void changeControlLayer() {

        removeItems(controlLayer);
        loadControlLayer();
    }

    /**
     * Bouns 변경.
     */
    public void changeBounsLayer() {

        removeItems(bonusLayer);
        loadBonusLayer();
    }

    /**
     * 현재 페이지 Number
     *
     * @return page
     */
    public int getPageNumber() {
        return _page;
    }

    /**
     * 캡쳐 컨테이터
     *
     * @return
     */
    public View getPageContainer() {
        return this.containerLayer;
    }

    public View getPageShadow() {
        return this.shadowLayer;
    }

    public View getPageMultiFlyLayer() {
        return this.multiFlyLayer;
    }

    /**
     * SnapsPage 데이터
     *
     * @return SnapsPage
     */
    public SnapsPage getSnapsPage() {
        return _snapsPage;
    }

    /**
     * Container width
     *
     * @return
     */
    public int getOrgWidth() {
        return this.width;
    }

    /**
     * Container height
     *
     * @return
     */
    public int getOrgHeight() {
        return this.height;
    }

    /**
     * 원본 ED width
     *
     * @return
     */
    public int getEDWidth() {
        return edWidth;
    }

    /**
     * 원본 ED height
     *
     * @return
     */
    public int getEDHeight() {
        return edHeight;
    }

    /**
     * 배경 데이터
     */
    protected void loadBgLayer() {
        loadBgLayer(null);
    }

    protected void loadBgLayer(String previewBgColor) {
        Dlog.d("loadBgLayer() napsPage.type:" + _snapsPage.type);
        for (SnapsControl bg : _snapsPage.getBgList()) {
            setBgLayer((SnapsBgControl) bg, previewBgColor);
        }
        // 테마북인 경우 무조건 배경에 흰색을 설정한다.
        if ((Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) && _snapsPage.getBgList().size() == 0) {
            setThemeBookBackground();
        }
    }

    /****
     * 테마북에 배경을 설정하는 함
     */
    private void setThemeBookBackground() {

        // 이미지뷰를 생성한다
        ImageView bgView = new ImageView(getContext());
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(new LayoutParams(this.width, this.height));
        bgView.setScaleType(ScaleType.FIT_XY);
        bgView.setLayoutParams(new AFrameLayoutParams(layout));
        bgView.setBackgroundColor(Color.WHITE);
        // 흰색 배경 이미지로드를 한다....어쩔수 없이..
        String url = SnapsAPI.DOMAIN(false) + "/Upload/Data1/Resource/design_ver4/edit/ed_TMSCB_u.jpg";
        loadImage(url, bgView, Const_VALUES.SELECT_CONTENT, 0, null);

        bgLayer.addView(bgView);
    }

    /**
     * 이미지 레이아웃 데이터
     */
    protected void loadLayoutLayer() {
        // Layout 설정
        ArrayList<SnapsControl> layoutList = _snapsPage.getLayoutList();
        if (layoutList != null) {
            for (SnapsControl layer : layoutList) {
                Dlog.d("loadLayoutLayer() layer.regName:" + layer.regName);
                if (layer instanceof SnapsLayoutControl) {
                    if (!_snapsPage.type.equalsIgnoreCase("hidden")) {
                        setLayout(_snapsPage.type, (SnapsLayoutControl) layer);
                    }
                }
            }
        }
    }

    /**
     * 텍스트 및 컨트롤 데이터
     */
    protected void loadControlLayer() {
        //clipart 로딩
        loadClipartControlList();    //ben: loadClipartControlList(), loadTextControlList() 이거 복사 했네..

        //Text Control 로딩
        loadTextControlList();
    }

//	protected boolean isCoverPageAndExistTitleText() {
//		if (_page != 0 || _snapsPage == null || _snapsPage.getTextControlList() == null) return false;
//		for (SnapsControl control : _snapsPage.getTextControlList()) {
//			if (control != null && control._controlType == SnapsControl.CONTROLTYPE_TEXT && control instanceof SnapsTextControl) {
//				SnapsTextControl textControl = (SnapsTextControl) control;
//				return !StringUtil.isEmpty(textControl.text);
//			}
//		}
//		return false;
//	}

    protected void loadTextControlList() {
        for (SnapsControl control : _snapsPage.getTextControlList()) {

            switch (control._controlType) {
                case SnapsControl.CONTROLTYPE_IMAGE:
                    // 이미지
                    break;

                case SnapsControl.CONTROLTYPE_STICKER: // 스티커..
                    ImageLoadView view = new ImageLoadView(this.getContext(), (SnapsClipartControl) control);
                    view.setSnapsControl(control);

                    String url = SnapsAPI.DOMAIN() + ((SnapsClipartControl) control).resourceURL;
                    loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);
                    // angleclip적용
                    if (!control.angle.isEmpty()) {
                        // view.setPivotX(0);
                        // view.setPivotY(0);
                        view.setRotation(Float.parseFloat(control.angle));
                    }
                    SnapsClipartControl clipart = (SnapsClipartControl) control;
                    float alpha = Float.parseFloat(clipart.alpha);
                    view.setAlpha(alpha);

                    controlLayer.addView(view);

                    break;

                case SnapsControl.CONTROLTYPE_BALLOON:
                    // 말풍선.
                    break;

                case SnapsControl.CONTROLTYPE_TEXT:
                    boolean isImmutable = isImmutableEditTextControl(control);
                    if (isImmutable) {
                        setImmutableTextControl(control);
                    } else {
                        setMutableTextControl(control);
                    }
                    break;
                case SnapsControl.CONTROLTYPE_GRID:
                    setImmutableTextControl(control);
                    break;
            }
        }
    }

    protected boolean shouldDrawSpineText() {
        //KT 북 - 책등 편집기에서 숨기기
        if (Config.isKTBook()) {
            return false;
        }
        return true;
    }

    protected void setImmutableTextControl(SnapsControl control) {
        SnapsTextControl textControl = (SnapsTextControl) control;
        if (isSpineTextControl(control) && !shouldDrawSpineText()) {
            return;
        }

        if (textControl.text == null) {
            textControl.text = "";
        }

        SnapsTextView text = new SnapsTextView(_snapsPage.type, textControl.controType, this.getContext(), textControl, this._callback);
        if (isRealPagerView()) {
            text.setSnapsControl(textControl);

            int generatedId = ViewIDGenerator.generateViewId(textControl.getControlId());
            textControl.setControlId(generatedId);
            text.setId(generatedId);
        } else {
            text.setThumbnail(isThumbnailView());
            text.setPreview(isPreview());
            TextView texView = text.getTextView();
            if (texView != null && isThumbnailView()) {
                texView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1);
            }
        }

        controlLayer.addView(text);
        if (Config.isCalendar(Config.getPROD_CODE())) {
            controlLayer.bringToFront();
        }
    }

    private boolean isImmutableEditTextControl(SnapsControl control) {
        if (!SnapsTextToImageUtil.isSupportEditTextProduct() || control == null || !(control instanceof SnapsTextControl)) {
            return true;
        }
        return isSpineTextControl(control);
    }

    private boolean isSpineTextControl(SnapsControl control) {
        if (control == null || !(control instanceof SnapsTextControl)) {
            return false;
        }
        SnapsTextControl textControl = (SnapsTextControl) control;
        return textControl.format != null && textControl.format.verticalView != null && textControl.format.verticalView.equalsIgnoreCase("true");
    }

    protected void setMutableTextControl(SnapsControl control) {
        if (control == null || !(control instanceof SnapsTextControl)) {
            return;
        }

        SnapsTextControl textControl = (SnapsTextControl) control;
        if (textControl.text == null) {
            textControl.text = "";
        }

        //KT 북 - 텍스트 입력이 된 경우 점선 안 보이게 (장바구니, 오토 세이브...)
        if (Config.isKTBook()) {
            String text = textControl.getText();
            if (text != null && text.length() > 0) {
                textControl.isEditedText = true;
            }
        }
        final SnapsTextToImageView snapsTextToImageView = new SnapsTextToImageView(getContext(), textControl, width, height);
        snapsTextToImageView.setTag(textControl);
        snapsTextToImageView.getPlaceHolderTextView().setTag(textControl);

        if (isRealPagerView()) {
            int generatedId = ViewIDGenerator.generateViewId(textControl.getControlId());
            textControl.setControlId(generatedId);
            snapsTextToImageView.setId(generatedId);

            snapsTextToImageView.addClickEventListener(v -> {
                UIUtil.blockClickEvent(v);
                Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
                intent.putExtra("control_id", snapsTextToImageView.getId());
                intent.putExtra("dummy_control_id", v.getId());
                intent.putExtra("isEdit", false);
                intent.putExtra("viewInCover", _snapsPage.isCover() && _page == 0);

                getContext().sendBroadcast(intent);
            });

        } else if (isThumbnailView()) {
            snapsTextToImageView.setThumbnail(getThumbnailRatioX(), getThumbnailRatioY());
            TextView texView = snapsTextToImageView.getPlaceHolderTextView();
            if (texView != null) {
                texView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1);
            }
        }

        if (mHandler != null) {
            Message message = new Message();
            message.obj = snapsTextToImageView;
            message.what = MSG_NOTIFY_TEXT_TO_IMAGEVIEW;
            mHandler.sendMessageDelayed(message, 100);
        }

//		snapsTextToImageView.notifyChildrenControlState();

        controlLayer.addView(snapsTextToImageView);
    }

    protected void loadClipartControlList() {
        for (SnapsControl control : _snapsPage.getClipartControlList()) {

            switch (control._controlType) {
                case SnapsControl.CONTROLTYPE_IMAGE:
                    // 이미지
                    break;

                case SnapsControl.CONTROLTYPE_STICKER: // 스티커..
                    setClipartControl((SnapsClipartControl) control);

                    break;

                case SnapsControl.CONTROLTYPE_BALLOON:
                    // 말풍선.
                    break;

                case SnapsControl.CONTROLTYPE_TEXT:
                    if (isImmutableEditTextControl(control)) {
                        setImmutableTextControl(control);
                    } else {
                        setMutableTextControl(control);
                    }
                    break;

                case SnapsControl.CONTROLTYPE_GRID:
                    // 텍스트.
                    SnapsTextControl textControl = (SnapsTextControl) control;

                    SnapsTextView text = new SnapsTextView(_snapsPage.type, textControl.controType, this.getContext(), textControl, this._callback);

                    if (isThumbnailView() || isPreview()) {
                        text.setThumbnail(isThumbnailView());
                        text.setPreview(isPreview());
                        TextView texView = text.getTextView();
                        if (texView != null) {
                            float fontSize = texView.getTextSize();
                            texView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1);
                        }
                    } else {
                        text.setSnapsControl(textControl);

                        AutoSaveManager saveMan = AutoSaveManager.getInstance();
                        if (saveMan != null && saveMan.isRecoveryMode()) {
//							textControl.setControlId(-1);
                        }
                        int generatedId = ViewIDGenerator.generateViewId(textControl.getControlId());
                        textControl.setControlId(generatedId);
                        text.setId(generatedId);
                    }

                    controlLayer.addView(text);
                    if (Config.isCalendar(Config.getPROD_CODE())) {
                        controlLayer.bringToFront();
                    }

                    break;

                case SnapsControl.CONTROLTYPE_MOVABLE:
                    SnapsMovableImageView snapsMovableImageView = new SnapsMovableImageView(getContext(), control);
                    controlLayer.addView(snapsMovableImageView);
                    break;
            }
        }
    }

    protected void setClipartControl(SnapsClipartControl clipartControl) {
        ImageLoadView view = new ImageLoadView(this.getContext(), clipartControl);
        view.setSnapsControl(clipartControl);

        String url = SnapsAPI.DOMAIN(false) + clipartControl.resourceURL;

        loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);
        Dlog.d("loadClipartControlList() CONTROLTYPE_STICKER:" + clipartControl);
        // angleclip적용
        if (!clipartControl.angle.isEmpty()) {
            view.setRotation(Float.parseFloat(clipartControl.angle));
        }

        float alpha = Float.parseFloat(clipartControl.alpha);
        view.setAlpha(alpha);

        controlLayer.addView(view);
    }

    /**
     * 폼 데이터
     */
    protected void loadFormLayer() {
        // Form 설정.
        for (SnapsControl form : _snapsPage.getFormList()) {
            setFormLayer((SnapsFormControl) form);
        }
    }

    protected int topMargin = 0;
    protected int leftMargin = 0;
    protected int rightMargin = 0;
    protected int bottomMargin = 0;

    protected int cover_topMargin = 0;
    protected int cover_leftMargin = 0;
    protected int cover_rightMargin = 0;
    protected int cover_bottomMargin = 0;

    public void refresh() {
        if (this._snapsPage == null) {
            return;
        }
        setSnapsPage(_snapsPage, _page, _isBg, _previewBgColor);
    }

    /**
     * Snaps Page 설정.
     *
     * @param page   Setting SnapsPage
     * @param number Page Number
     */
    public void setSnapsPage(SnapsPage page, int number) {
        setSnapsPage(page, number, true, null);
    }

    public void setSnapsPage(SnapsPage page, int number, boolean isBg) {
        setSnapsPage(page, number, isBg, null);
    }

    public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
        this._snapsPage = page;
        this._page = number;

        removeItems(this);

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());

        this.width = page.getWidth();
        this.height = Integer.parseInt(page.height);

        initMargin();

        if (page.type.equals("cover")) {
            layout.width = this.width;
            layout.height = this.height;
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
        if (page.type.equals("cover")) {
            containerlayout.setMargins(0, 0, 0, 0);
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

        if (isBg || previewBgColor != null) {
            containerLayer.addView(bgLayer);
        }

        // layoutLayer 초기화.
        layoutLayer = new FrameLayout(this.getContext());
        layoutLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
        containerLayer.addView(layoutLayer);

        // controllLayer 초기화. ppppoint
        controlLayer = new FrameLayout(this.getContext());
        controlLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
        containerLayer.addView(controlLayer);

        // formLayer 초기화.
        formLayer = new FrameLayout(this.getContext());
        formLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(formLayer);

        // pageLayer 초기화.
        pageLayer = new FrameLayout(this.getContext());
        pageLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(pageLayer);

        // effectLayer 초기화
        effectLayer = new ShimmerFrameLayout(this.getContext());
        effectLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
        containerLayer.addView(effectLayer);

        //이미지 로딩 완료 체크 객체 생성
        initImageLoadCheckTask();

        // Back Ground 설정.
        loadBgLayer(previewBgColor); //형태는 갖추고 있어야 하니까 우선 BG 만 로딩한다.

        requestLoadAllLayerWithDelay(DELAY_TIME_FOR_LOAD_IMG_LAYER);
        Dlog.d("setSnapsPag() page number:" + number);

        setBackgroundColorIfSmartSnapsSearching();
    }

    protected void setBackgroundColorIfSmartSnapsSearching() {
        boolean isSmartSearchingColor = false;
        if (SmartSnapsManager.isSupportSmartSnapsProduct() && !SmartSnapsUtil.isOmitDimUIProduct()) {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            if (smartSnapsManager.isFirstSmartSearching() && SmartSnapsManager.isSmartAreaSearching()) {
                setBackgroundColor(Color.argb(255, 180, 180, 180));
                isSmartSearchingColor = true;
            }
        }

        if (!isSmartSearchingColor) {
            if (isThumbnailView()) {
                setBackgroundColor(Color.argb(255, 255, 255, 255));
            } else {
                setBackgroundColor(Color.argb(255, 238, 238, 238));
            }
        }
    }

    protected void requestLoadAllLayerWithDelay(long delay) { //딜레를 약간이라도 주는 이유는 화면에 그려지기 전에 로딩이 될 경우, 컨트롤 크기 측정에 실패(0)하여 안 그려질 가능성도 있기 때문이다
        lockPageCanvasImageLoadSyncLockForSmartSnaps();
        mHandler.sendEmptyMessageDelayed(MSG_CALLBACK_LOAD_LAYERS, delay);
    }

    protected void loadAllLayers() {
        if (isSuspendedLayerLoad()) {
            hideProgressOnCanvas();
            return;
        }

        // Layout 설정
        loadLayoutLayer();

        // Control 설정.
        loadControlLayer();

        // Form 설정.
        loadFormLayer();

        // Page 이미지 설정.
        loadPageLayer();

        // 추가 Layer 설정.
        loadBonusLayer();

        // 이미지 로드 완료 설정.
        setPinchZoomScaleLimit(_snapsPage);

        setScaleValue();

        // 이미지 로딩이 완료 되었는 지 체크 하기 시작한다.
        imageLoadCheck();
    }

    protected void setScaleValue() {
        boolean isPortraitScreen = PhotobookCommonUtils.isPortraitScreenProduct();

        int screenWidth = UIUtil.getScreenHeight(mContext) - (int) getResources().getDimension(R.dimen.snaps_preview_landscape_margin);
        int screenHeight = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.home_title_bar_height) - (int) getResources().getDimension(R.dimen.snaps_preview_margin);

        if (isPortraitScreen) {
            if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                screenWidth = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.snaps_diary_list_margin);
            } else {
                screenWidth = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.snaps_preview_margin);
            }
        }

        //썸네일을 확대해서 생성한다.
        if (isScaledThumbnailMakeMode()) {
            screenWidth = SCALE_THUMBNAIL_MAX_OFFSET;
        }

        float ratioCanvasWH = this.width / (float) this.height;
        int fixedCanvasWidth = (int) (screenWidth / ratioCanvasWH);

        mScaleX = screenWidth / (float) this.width;
        mScaleY = fixedCanvasWidth / (float) this.height;

        if (!isPortraitScreen && (this.height * mScaleY) > screenHeight) {
            mScaleY = screenHeight / (float) this.height;
            mScaleX = mScaleY;
        }

        if (isPreview()) {
            setScaleX(mScaleX);
            setScaleY(mScaleY);
        }
    }

    /**
     * 이미지 로드 완료 체크.
     */
    protected void imageLoadCheck() {
        //이미 돌고 있다면, 중단하고 다시 실행한다.
        if (mImageLoadCheckTask != null && mImageLoadCheckTask.getState() == Thread.State.RUNNABLE) {
            suspendCheckLoadImageTasks();
            try {
                mImageLoadCheckTask.interrupt();
                mImageLoadCheckTask.join();
            } catch (InterruptedException e) {
                Dlog.e(TAG, e);
            }

            mImageLoadCheckTask = new ImageLoadCheckTask(this);
            mImageLoadCheckTask.setSnapsPageIndex(_page);
            mImageLoadCheckTask.setMakeThumbnail(isPageSaving());
            mImageLoadCheckTask.start();
        } else {
            if (mImageLoadCheckTask == null || mImageLoadCheckTask.getState() == Thread.State.TERMINATED) {
                mImageLoadCheckTask = new ImageLoadCheckTask(this);
                mImageLoadCheckTask.setSnapsPageIndex(_page);
                mImageLoadCheckTask.setMakeThumbnail(isPageSaving());
            }

            mImageLoadCheckTask.start();
        }
    }

    /**
     * 이미지 로드.
     *
     * @param url       Image URL
     * @param imageView ImageView
     */
    protected void loadImage(String url, final ImageView imageView, final int loadType, final int rotate, final MyPhotoSelectImageData imgData, SnapsLayoutControl layoutControl) {
        addImageLoadCheckCount();

        int totalRotate = (rotate == -1 ? 0 : rotate) % 360;

        final String URL = url;

        SnapsImageViewTargetParams snapsImageViewTargetParams = new SnapsImageViewTargetParams.Builder().setLayoutControl(layoutControl).setView(imageView)
                .setImageData(imgData).setRotate(totalRotate).setUri(url).setRealPagerView(isRealPagerView()).setLoadType(loadType).create();
        SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(getContext(), snapsImageViewTargetParams) {
            @Override
            public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);

                try {
                    if (isRealPagerView()) {
                        calculateImageCoordinate(resource, view, loadType, URL, rotate);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (view != null) { //bitmap이 완전히 그려지는 지 체크하기 위해 post..
                        view.post(() -> subImageLoadCheckCount());
                    } else {
                        subImageLoadCheckCount();
                    }
                }

                isLoadedShadowLayer = true;
                // Shadow 설정.
                loadShadowLayer();
            }
        };

        if (isRealPagerView()) {
            if (imgData != null) {
                addSmartSnapsAnimationViewTargetListener(imgData, bitmapImageViewTarget);
            }
        } else {
            if (SmartSnapsManager.isFirstSmartAreaSearching()) {
                if (imgData != null) {
                    addSmartSnapsAnimationThumbViewTargetListener(imgData, bitmapImageViewTarget);
                }
            }
        }
        int width = 0;
        int height = 0;
        if (imageView instanceof ImageLoadView) {
            ImageLoadView imageLoadView = (ImageLoadView) imageView;
            SnapsControl snapsControl = imageLoadView.getSnapsControl();
            if (snapsControl != null) {
                width = snapsControl.getIntWidth();
                height = snapsControl.getIntHeight();
            }
        }

        ImageLoader.asyncDisplayImage(mContext, imgData, URL, bitmapImageViewTarget, getRequestImageSize(), width, height);
    }

    protected void loadImage(String url, ImageView view, final int loadType, final int rotate, final MyPhotoSelectImageData imgData) {
        loadImage(url, view, loadType, rotate, imgData, null);
    }

    protected void loadImage(String url, ImageView view, SnapsLayoutControl layoutControl, final int loadType, final int rotate) {
        loadImage(url, view, loadType, rotate, null, layoutControl);
    }

    protected void loadImage(String url, ImageView view, SnapsLayoutControl layoutControl, final int rotate) {
        if (layoutControl == null) {
            return;
        }
        loadImage(url, view, layoutControl.imgData.KIND, rotate, layoutControl.imgData, layoutControl);
    }

    protected int getRequestImageSize() {
        if (isThumbnailView()) {
            return THUMBNAIL_CACHE_SIZE;
        } else if (Config.isCalendar()) {
            return CALENDAR_CACHE_SIZE;
        } else if (Const_PRODUCT.isPackageProduct()) {
            return PACKAGE_KIT_CACHE_SIZE;
        } else if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
            return DIARY_CACHE_SIZE;
        } else if (Const_PRODUCT.isBabyNameStikerGroupProduct()) {
            return MAX_CACHE_SIZE;
        } else if (Const_PRODUCT.isNewKakaoBook()) {
            return 0;
        }
//		return 0;
        else {
            return DEFALUT_CACHE_SIZE;
        }
    }

    private boolean isSkipSmartAnimationImageData(MyPhotoSelectImageData imageData) {
        if (imageData == null || imageData.getSmartSnapsImgInfo() == null) {
            return true;
        }
        SmartSnapsConstants.eSmartSnapsImgState state = imageData.getSmartSnapsImgInfo().getSmartSnapsImgState();
        return state == null || state == SmartSnapsConstants.eSmartSnapsImgState.NONE || (isRealPagerView() && state == SmartSnapsConstants.eSmartSnapsImgState.FINISH_ANIMATION);
    }

    protected void addSmartSnapsAnimationViewTargetListener(MyPhotoSelectImageData imageData, ISmartSnapImgDataAnimationState animationListener) {
        if (!SmartSnapsManager.isSmartAreaSearching() || imageData == null || animationListener == null || !imageData.isSmartSnapsSupport()) {
            return;
        }

        if (isSkipSmartAnimationImageData(imageData)) {
            return;
        }

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.addSmartAnimationViewTargetListener(imageData, animationListener);
    }

    protected void addSmartSnapsAnimationThumbViewTargetListener(MyPhotoSelectImageData imageData, ISmartSnapImgDataAnimationState animationListener) {
        if (!SmartSnapsManager.isSmartAreaSearching() || imageData == null || animationListener == null || !imageData.isSmartSnapsSupport()) {
            return;
        }

        if (isSkipSmartAnimationImageData(imageData)) {
            return;
        }

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.addSmartAnimationThumbViewTargetListener(imageData, animationListener);
    }

    /**
     * 로딩 된 이미지의 정보(좌표)를 xml에 작성한다.
     * 장바구니에 저장할 때, 다시 한번 계산을 하지만 UI에 보여질 때 ImageRatio 보정 코드를 타서 문제가 생길 수 있으므로
     * 바로 바로 계산하는 로직을 걷어 내진 않았음.
     */
    protected void calculateImageCoordinate(Bitmap loadedImage, View view, int loadType, String url, int rotate) {
        ImageCoordinateInfo coordinateInfo = new ImageCoordinateInfo();
        coordinateInfo.setLoadedImage(loadedImage);
        coordinateInfo.setView(view);
        coordinateInfo.setLoadType(loadType);
        coordinateInfo.setUrl(url);
        coordinateInfo.setRotate(rotate);

        if (mImageCoordinateCalculator == null) {
            mImageCoordinateCalculator = new ImageCoordinateCalculator();
        }

        mImageCoordinateCalculator.start(coordinateInfo);
    }

    /**
     * Image Layout 설정.
     *
     * @param layout
     */
    private void setLayout(String pageType, SnapsLayoutControl layout) {

        boolean isPlusButtonEnable = true;

        // 로컬 리소스 인경우 // 바코드, 책등..
        if (layout.type.equals("local_resource")) {
            ImageLoadView view = new ImageLoadView(this.getContext(), layout);
//			view.setTag(layout);
            view.setSnapsControl(layout);

            int resId = UIUtil.string2DrawableResID(getContext(), layout.resourceURL);
            if (layout.imageLoadType == Const_VALUES.SPINE_TYPE) {
                ;
            } else if (layout.imageLoadType == Const_VALUES.SPINE_IMAGE_TYPE) {
                view.setBackgroundResource(resId);
            } else {
                view.setImageResource(resId);
            }

            view.invalidate();

            if (!isEnableButton && (layout.imageLoadType == Const_VALUES.SPINE_TYPE)) {
                ;
            } else {
                layoutLayer.addView(view);
            }
        } else if (layout.type.equals("border")) {
            // facebook용 border
            ColorBorderView border = new ColorBorderView(getContext());

            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(Integer.parseInt(layout.width), Integer.parseInt(layout.height));
            border.setLayoutParams(new FrameLayout.LayoutParams(params));

            border.setX(layout.getX());
            border.setY(Integer.parseInt(layout.y));
            border.setBorderWidth(layout, 0.5f, layout.getFacebookBordColor(true));

            layoutLayer.addView(border);

        } else if (layout.type.equals("browse_file")) {
            CustomImageView imgView = new CustomImageView(pageType, SnapsPageCanvas.this.getContext(), layout, isEnableButton ? _callback : null);
            imgView.setIsThumbnail(isThumbnailView());
            imgView.setIsPreview(isPreview());
            imgView.getImageView().setSnapsControl(layout);
            imgView.setBackgroundResource(R.color.transparent);

            // angleclip적용
            if (!layout.angleClip.isEmpty()) {
                imgView.setRotation(Float.parseFloat(layout.angleClip));
            }

            if (layout.imgData != null) {
                String path = ImageUtil.getImagePath(mContext, layout.imgData);

                int angle = 0;
                if (layout.imgData.isApplyEffect) {
                    if (!layout.imgData.isAdjustableCropMode) {
                        angle = layout.imgData.ROTATE_ANGLE_THUMB;
                    }
                } else {
                    if (path != null && path.startsWith("http")) {
                        if (path.contains("/oripq/")) {
                            angle = 0;
                        } else {
                            angle = layout.imgData.ROTATE_ANGLE;
                        }
                    } else {
                        angle = layout.imgData.ROTATE_ANGLE_THUMB;
                    }
                }

                int alpha = layout.imgData.imgAlpha;
                if (alpha != 100) {
                    imgView.getImageView().getView().setAlpha(alpha / 100.f);
                }

                loadImage(path, (ImageView) imgView.getImageView(), layout, angle);
            } else if (!layout.imagePath.equals("")) {
                loadImage(layout.imagePath, (ImageView) imgView.getImageView(), layout.imageLoadType, Integer.parseInt(layout.angle), layout.imgData);
            } else if (layout.isClick.equalsIgnoreCase("true") || Const_PRODUCT.isShowPlusButton()) {
                boolean shouldBeFillMaskArea = isRealPagerView() && !layout.mask.isEmpty();
                if (shouldBeFillMaskArea) {
                    String url = "";
                    if (Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isSealStickerProduct()) {
                        url = SnapsAPI.DOMAIN(false) + "/Upload/Data1/mobile/android/resource/skin/empty_gray_bg.png";
                    } else {
                        url = SnapsAPI.DOMAIN(false) + "/Upload/Data1/mobile/android/resource/skin/empty_white_bg.png";
                    }
                    loadImage(url, (ImageView) imgView.getImageView().getView(), layout, -1, 0);
                } else if (isEnableButton && layout.mask.isEmpty()) { // 테마북인 경우..
                    //Todo: @Marko -> 사용자 배경 설정 SceneObjectImage 가 이곳을 타기 때문에 보더가 생긴다. 템플릿 데이터가 온전해지면 조건에 따라 분기해야한다.
                    imgView.setBackgroundResource(R.drawable.border_snapscolor);
                } else {
                    if (isThumbnailView() && !Const_PRODUCT.isNewPolaroidPackProduct()) {
                        imgView.setBackgroundResource(R.drawable.border_snapscolor_light);
                    } else {
                        imgView.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            } else {
                if (!layout.tempImageColor.equalsIgnoreCase("")) {
                    imgView.setBackgroundColor(Color.parseColor("#" + layout.tempImageColor));
                    imgView.invalidate();
                }
            }

            Dlog.d("setLayout() browse_file-img");
            layoutLayer.addView(imgView);

            if (imageViews != null) {
                imageViews.add(imgView);
            }
        } else if (layout.type.equals("webitem")) {
            if ((layout.regName.equals("background") || layout.regName.equals("line"))) {
                CustomImageView imgView = new CustomImageView(pageType, this.getContext(), layout, isEnableButton ? _callback : null);
                imgView.setIsThumbnail(isThumbnailView());
                imgView.setIsPreview(isPreview());
//				imgView.getImageView().setTag(layout);
                imgView.getImageView().setSnapsControl(layout);

                if (!layout.tempImageColor.equalsIgnoreCase("")) {
                    imgView.setBackgroundColor(Color.parseColor("#" + layout.tempImageColor));
                    imgView.invalidate();
                }
                Dlog.d("setLayout() webitem-background");
                layoutLayer.addView(imgView);

                if (imageViews != null) {
                    imageViews.add(imgView);
                }
            } else if ((layout.regName.equals("like") || layout.regName.equals("more"))) {
                CustomImageView imgView = new CustomImageView(pageType, this.getContext(), layout, isEnableButton ? _callback : null);
                imgView.setIsThumbnail(isThumbnailView());
                imgView.setIsPreview(isPreview());
//				imgView.getImageView().setTag(layout);
                imgView.getImageView().setSnapsControl(layout);

                String url = "";
                Dlog.d("setLayout() layout.resourceURL:" + layout.resourceURL);
                if (!layout.resourceURL.equals("")) {
                    url = SnapsAPI.DOMAIN(false) + layout.resourceURL;
                    Dlog.d("setLayout() url:" + url);
                    if (!url.equalsIgnoreCase("")) {
                        loadImage(url, (ImageView) imgView.getImageView(), layout.imageLoadType, 0, null);
                    }
                }

                layoutLayer.addView(imgView);

                if (imageViews != null) {
                    imageViews.add(imgView);
                }
            } else {
                ImageLoadView view = new ImageLoadView(this.getContext(), layout);
                view.setSnapsControl(layout);
                String url = "";

                if (!layout.srcTarget.equalsIgnoreCase("")) {
                    url = SnapsAPI.GET_API_RESOURCE_IMAGE() + "&rname=" + layout.srcTarget + "&rCode=" + layout.srcTarget;

                }

                if (!url.equalsIgnoreCase("") && !"".equals(layout.angle)) {
                    loadImage(url, view, layout.imageLoadType, Integer.parseInt(layout.angle), null);
                } else if (!url.equalsIgnoreCase("")) {
                    isPlusButtonEnable = false;
                    loadImage(url, view, layout.imageLoadType, -1, null);
                }

                layoutLayer.addView(view);
            }
        }

        if (layout.border != null && !layout.border.equalsIgnoreCase("") && !layout.border.equals("false")) {
            // border가 있는 경우..
            //
            // ben이 작성함 (2020년 1월 9일)
            // PC 편집기 기준, 사진틀 - 디자인틀 처리 관련
            // 4년전에 스펙인데 미구현 상태
            // 히스토리를 확인해보니 4년전에 개발하기 어렵다고 미구현 한 상태로 확인됨
            // 구현해야 했던 기능은 다음과 같다.
            // 1. border이미지를 다운로드 받는다.
            // 2. border이미지에 해당하는 메타정보 파일을 다운로드 받는다.(확장명이 .ini)
            // 3. ini 파일을 파싱해서 정보를 추출....
            // 4. 추출된 정보를 이용해서 다운로드 받은 이미지를 9조각으로 나누어서 정가운데 영역만 확대하고 나머지는 유지 <- 쉽게 말해서 나인 패치를 노가다로 구현
            // 아무튼 결론은 아래 코드 주석 처리
            /*
            ImageLoadView borderview = new ImageLoadView(this.getContext(), layout);
            String url = SnapsAPI.GET_API_RESOURCE_IMAGE() + "&rname=" + layout.border + "&rCode=" + layout.border;

            if (layout.angle.isEmpty()) {
                layout.angle = "0";
            }

            loadImage(url, borderview, layout.imageLoadType, (int) Float.parseFloat(layout.angle), null);
            layoutLayer.addView(borderview);
            */
        }

        // 경고아이콘과 추가 아이콘 넣는 로
        if (!layout.type.equals("local_resource") && layout.type.equals("browse_file") && (Const_PRODUCT.isShowPlusButton() || SnapsDiaryDataManager.isAliveSnapsDiaryService())) {
            if (layout.imgData != null && isEnableButton) {

                boolean isVisibleIcon = true;

                if (Const_PRODUCT.isSNSBook())// 카카오북은 커버만 표시한다.
                {
                    isVisibleIcon = layout.isSnsBookCover;
                }

                ImageView ivNoticeIcon = null;
                if (isVisibleIcon) {
                    // 경고 이미지가 이미지보다 큰경우..
                    ivNoticeIcon = new ImageView(this.getContext());
                    ivNoticeIcon.setTag(TAG_IMAGEVIEW_NO_PRINT_IMAGE_ALERT);
                    if (layout.isUploadFailedOrgImg) { //업로드 실패된 원본 이미지
                        imgW = 54;
                        imgH = 46;
                        ivNoticeIcon.setImageResource(R.drawable.alert_for_upload_failed_org_img);
                    } else if (layout.isNoPrintImage) { // 해상도에 맞지않는 이미지인 경우 경고 이미지 표시.. 센터에 추가..
                        ivNoticeIcon.setImageResource(R.drawable.alert_01);
                    }

                    int noticeIconWidth = imgW;
                    int noticeIconHeight = imgH;
                    if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                        noticeIconWidth *= Math.min(1.5f, getFitScaleX());
                        noticeIconHeight *= Math.min(1.5f, getFitScaleY());
                    }


                    /**
                     * 일기는 편집 화면이 스케일되어서 보인다..´
                     */
                    if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                        ViewGroup.MarginLayoutParams params = new FrameLayout.LayoutParams(new LayoutParams(noticeIconWidth, noticeIconHeight));
                        params.leftMargin = layout.getScaledX() + (Integer.parseInt(layout.getScaledWidth()) - noticeIconWidth) / 2;
                        params.topMargin = layout.getScaledY() + ((int) Float.parseFloat(layout.getScaledHeight()) - noticeIconHeight) / 2;
                        ivNoticeIcon.setLayoutParams(params);

                    } else if (Const_PRODUCT.isFreeSizeProduct()) {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(noticeIconWidth, noticeIconHeight);
                        params.gravity = Gravity.CENTER;
                        ivNoticeIcon.setLayoutParams(params);

                    } else {
                        ViewGroup.MarginLayoutParams params = new FrameLayout.LayoutParams(new LayoutParams(noticeIconWidth, noticeIconHeight));
                        params.leftMargin = layout.getX() + (Integer.parseInt(layout.width) - noticeIconWidth) / 2;
                        params.topMargin = Integer.parseInt(layout.y) + ((int) Float.parseFloat(layout.height) - noticeIconHeight) / 2;
                        ivNoticeIcon.setLayoutParams(params);
                    }

                    containerLayer.addView(ivNoticeIcon);
                }

            } else if (isEnableButton && isPlusButtonEnable) { // 이미지가 없는 경우 플러스 버튼을 추가한다.

                boolean bAddPlusBtn = (!Const_PRODUCT.isNewKakaoBook(Config.getPROD_CODE()) && !Const_PRODUCT.isFacebookPhotobook(Config.getPROD_CODE()) && !Const_PRODUCT.isInstagramBook(Config.getPROD_CODE()) && !Const_PRODUCT.isSnapsDiary()) || layout.isSnsBookCover;

                if (bAddPlusBtn) {
                    RotateImageView iv = new RotateImageView(this.getContext());
                    iv.setIsThumbnail(isThumbnailView());
                    iv.setIsPreview(isPreview());
                    ViewGroup.MarginLayoutParams params = new FrameLayout.LayoutParams(new LayoutParams(imgW, imgH));
                    params.leftMargin = layout.getX() + (Integer.parseInt(layout.width) - imgW) / 2;
                    params.topMargin = Integer.parseInt(layout.y) + ((int) Float.parseFloat(layout.height) - imgH) / 2;

                    iv.setLayoutParams(params);
                    iv.setBackgroundResource(R.drawable.btn_page_add_01);

                    if (isRealPagerView()) {
                        iv.setTag(layout);
                        AutoSaveManager saveMan = AutoSaveManager.getInstance();
                        if (saveMan != null && saveMan.isRecoveryMode()) {
//							layout.setControlId(-1);
                        }

                        int generatedId = ViewIDGenerator.generateViewId(layout.getControlId());
                        layout.setControlId(generatedId);
                        iv.setId(generatedId);
                    }
                    //나중에 전상품 적용해야한다
                    if (Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isSealStickerProduct()) {
                        buttonLayer.addView(iv);
                    } else {
                        layoutLayer.addView(iv);
                    }
                }
            }
        }
    }

    // 경고 이미지 와 플러스 이미지 크기 설정값..
    int imgW = Const_VALUE.PLUS_BUTTON_WIDTH;// UIUtil.convertDPtoPX(this.getContext(),
    // Const_VALUE.PLUS_BUTTON_WIDTH);
    int imgH = Const_VALUE.PLUS_BUTTON_HEIGHT;// UIUtil.convertDPtoPX(this.getContext(),
    // Const_VALUE.PLUS_BUTTON_HEIGHT);

    /**
     * 폼 설정
     *
     * @param form
     */
    private void setFormLayer(SnapsFormControl form) {
        ImageView formView = new ImageView(getContext());
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(new LayoutParams(this.width, this.height));
        formView.setScaleType(ScaleType.FIT_XY);
        formView.setLayoutParams(new AFrameLayoutParams(layout));

        Dlog.d("setFormLayer() form.type:" + form.type);
        if (form.type.equalsIgnoreCase("webitem")) {
            String url;

            if (form.resourceURL.equalsIgnoreCase("")) {
                url = SnapsAPI.GET_API_RESOURCE_IMAGE() + "&rname=" + form.srcTarget + "&rCode=" + form.srcTarget;
            } else {
                if (form.resourceURL.contains(SnapsAPI.DOMAIN(false))) {
                    url = form.resourceURL;
                } else {
                    url = SnapsAPI.DOMAIN(false) + form.resourceURL;
                }
            }

            url = url.replace(" ", "%20");
            Dlog.d("setFormLayer() url:" + url);
            loadImage(url, formView, 4, Integer.parseInt(form.angle), null);
        } else {
        }

        // 스냅스 명함일 경우는 Form을 마스크로 사용한다..
        formLayer.addView(formView);
    }

    /**
     * 배경 설정.
     *
     * @param bg
     */
    private void setBgLayer(final SnapsBgControl bg, String previewBgColor) {

        // 이미지뷰를 생성한다
        SnapsImageView bgView = new SnapsImageView(getContext());
        // 배경레이아웃에 크기를 설정한다.
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(new LayoutParams(this.width, this.height));

        bgLayerForegroundImageView = new SnapsImageView(getContext());
        // 배경레이아웃에 크기를 설정한다.
        FrameLayout.LayoutParams testLayoutParams = new FrameLayout.LayoutParams(new LayoutParams(this.width, this.height));
        bgLayerForegroundImageView.setLayoutParams(testLayoutParams);

        // 테마북에서만 사용하려고 만들었음... 테마북에서는 배경은 커버에 배경만 존재한다는 가정에서 코딩함...

        if (isRealPagerView()) {
            bgView.setSnapsControl(bg);
            AutoSaveManager saveMan = AutoSaveManager.getInstance();
            if (saveMan != null && saveMan.isRecoveryMode()) {
//				bg.setControlId(-1);
            }

            int generatedId = ViewIDGenerator.generateViewId(bg.getControlId());
            bg.setControlId(generatedId);
            bgView.setId(generatedId);
        }

        if (bg.isClick.equalsIgnoreCase("true")) {
            bgView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isRealPagerView()) {
                        return;
                    }

                    if (bg.isClick.equalsIgnoreCase("true") && _callback != null) {
                        // _callback.onViewPagerItemClickView(v, 0, -1, -1);

                        Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
                        intent.putExtra("control_id", v.getId());
                        intent.putExtra("isEdit", false);

                        SnapsPageCanvas.this.getContext().sendBroadcast(intent);

                    }
                }
            });
        }

        bgView.setScaleType(ScaleType.FIT_XY);
        bgView.setLayoutParams(new AFrameLayoutParams(layout));

        if (previewBgColor != null) {// 카카오북 7인치 내지의 경우 커버컬러를 배경색으로 지정(preview만
            // 적용, xml에는 비적용)
            ColorDrawable cd = new ColorDrawable(Color.parseColor(previewBgColor));
            // bgView.setBackgroundColor( Color.parseColor( previewBgColor ));
            bgView.setImageDrawable(cd);
        } else {

            if (bg.type.equalsIgnoreCase("webitem")) {
                String url = "";

                if (bg.resourceURL.equals("")) {
                    url = SnapsAPI.GET_API_RESOURCE_IMAGE() + "&rname=" + bg.srcTarget + "&rCode=" + bg.srcTarget;

                } else {
                    if (bg.resourceURL.contains(SnapsAPI.DOMAIN(false))) {
                        url = bg.resourceURL;
                    } else {
                        url = SnapsAPI.DOMAIN(false) + bg.resourceURL;
                    }

                    bgView.setBackgroundColor(Color.parseColor("#" + "ffffffff"));
                }

                if (Config.isSnapsSticker()) {
                    if (_page == 0) {
                        loadImage(url, bgView, 3, Integer.parseInt(bg.angle), null);
                    }
                } else {
                    Dlog.d("setBgLayer() url:" + url);
                    loadImage(url, bgView, 3, Integer.parseInt(bg.angle), null);
                }
            }
            if (!bg.bgColor.equalsIgnoreCase("")) {
                if ((Const_PRODUCT.isTransparencyPhotoCardProduct() && isRealPagerView()) || Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isFreeSizeProduct()) {
                    bgView.setBackgroundColor(Color.TRANSPARENT);

                } else if (Const_PRODUCT.PRODUCT_MINI_BANNER_CLEAR.equals(Config.getPROD_CODE())) {
                    Drawable transparencyImage = ContextCompat.getDrawable(getContext(), R.drawable.transparency_image);
                    bgLayer.setBackground(transparencyImage);

                } else {
                    bgView.setBackgroundColor(Color.parseColor("#" + bg.bgColor));
                }
            }
        }

        if (Config.isSnapsSticker()) {
            if (!bg.coverColor.equalsIgnoreCase("")) {
                bgView.setBackgroundColor(Color.parseColor("#" + bg.coverColor));
            }
        }

        float alpha = Float.parseFloat(bg.alpha);
        bgView.setAlpha(alpha);

        bgLayer.addView(bgView);
        bgLayer.addView(bgLayerForegroundImageView);
    }

    /**
     * 그림자 로드
     */
    protected abstract void loadShadowLayer();

    /**
     * 페이지 이미지
     */
    protected abstract void loadPageLayer();

    /**
     * Bonus Layer
     */
    protected abstract void loadBonusLayer();

    /**
     * 페이지 Margin 설정.
     */
    protected abstract void initMargin();

    /**
     * 메모리 해제 용도..
     */
    public void onDestroyCanvas() {
        isCanvasDestroyed = true;

        if (mImageLoadCheckTask != null) {
            mImageLoadCheckTask.setIsStop(true);
            mImageLoadCheckTask.releaseInstance();
        }

        if (mImageCoordinateCalculator != null) {
            mImageCoordinateCalculator.stop();
        }

        if (mHandler != null) {
            mHandler.destroy();
            mHandler = null;
        }

        releaseReferences();
    }

    public void releaseReferences() {
        suspendLayerLoad();
        if (imageViews != null) {
            for (CustomImageView imageView : imageViews) {
                if (imageView != null) {
                    imageView.releaseInstance();
                }
            }

            imageViews.clear();
            imageViews = null;
        }

        //TODO  만약 리사이클 비트맵 사용으로 인해 오류가 발생한다면 아래 코드 삭제..
        try {
            ViewUnbindHelper.unbindReferences(containerLayer, null, false);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void postImageLoadComplateCallback(int pageNum) {
        if (!isThumbnailView()) {
            if (_callback != null) {
                if (containerLayer != null) {
                    containerLayer.invalidate();
                }
                _callback.onImageLoadComplete(pageNum);
            }
        }

        hideProgressOnCanvas();
    }

    protected void showProgressOnCanvas() {
        showThumbanilProgress();
        addImageLoadProgress();
    }

    protected void hideProgressOnCanvas() {
        hideThumbnailProgress();
        hideImageLoadProgress();
    }

    private void hideThumbnailProgress() {
        if (!isThumbnailView()) {
            return;
        }
        if (thumbnailProgress != null) {
            thumbnailProgress.setVisibility(View.GONE);
        }
    }

    private boolean isAllowShowImageLoadProgress() {
        if (imageLoadProgress == null) {
            return true;
        }

        if (bonusLayer == null) {
            return false;
        }

        //이미 프로그래스가 돌고 있다면...
        for (int ii = 0; ii < bonusLayer.getChildCount(); ii++) {
            View view = bonusLayer.getChildAt(ii);
            if (view == imageLoadProgress) {
                return false;
            }
        }

        return true;
    }

    /**
     * 편집 화면의 Canvas 중앙에 로딩 프로그래스를 그린다.
     */
    private void addImageLoadProgress() {
        if (isThumbnailView() || !isAllowShowImageLoadProgress()) {
            return;
        }

        imageLoadProgress = new ProgressBar(mContext);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.rotate_progress);

        ViewGroup.MarginLayoutParams progressParams = new FrameLayout.LayoutParams(IMAGE_LOAD_PROGRESS_DIMENSION, IMAGE_LOAD_PROGRESS_DIMENSION);
        progressParams.leftMargin = (int) (((this.edWidth) / 2) - (IMAGE_LOAD_PROGRESS_DIMENSION / 2));
        progressParams.topMargin = (int) (((this.edHeight) / 2) - (IMAGE_LOAD_PROGRESS_DIMENSION / 2));
        imageLoadProgress.setLayoutParams(progressParams);
        imageLoadProgress.setIndeterminateDrawable(drawable);
        imageLoadProgress.requestLayout();

        if (bonusLayer != null) {
            bonusLayer.addView(imageLoadProgress);
        }

        //가끔 프로그래스가 안 멈추는 경우가 있어서 강제로 일정 시간 후에 hide처리 한다.
        mHandler.sendEmptyMessageDelayed(MSG_CALLBACK_HIDE_IMAGE_LOAD_PROGRESS, MAX_PROGRESS_TIME);
    }

    /**
     * 썸네일뷰 중앙에 로딩 프로그래스를 그린다.
     */
    private void showThumbanilProgress() {
        if (!isThumbnailView()) {
            return;
        }
        if (thumbnailProgress != null) {
            thumbnailProgress.setVisibility(View.VISIBLE);
        }

        //가끔 프로그래스가 안 멈추는 경우가 있어서 강제로 일정 시간 후에 hide처리 한다.
        mHandler.sendEmptyMessageDelayed(MSG_CALLBACK_HIDE_THUMBNAIL_PROGRESS, MAX_PROGRESS_TIME);
    }

    protected void hideImageLoadProgress() {
        if (isThumbnailView()) {
            return;
        }

        if (imageLoadProgress != null) {
            imageLoadProgress.setVisibility(View.GONE);
            if (bonusLayer != null) {
                bonusLayer.removeView(imageLoadProgress);
            }
            imageLoadProgress = null;
        }
    }

    /**
     * Canvas의 모든 이미지 로딩이 끝났을 때 호출 됨.
     */
    @Override
    public void onFinishImageLoad() {
        if (isCanvasDestroyed) {
            return;
        }

        if (mHandler != null) {
            int delay = 200;
            if (_page == 0) //커버는 대표 썸네일을 따니까 확실히 이미지가 로딩될때까지 기다려 준다.
            {
                delay = 3000;
            }

            mHandler.sendEmptyMessageDelayed(MSG_CALLBACK_POST_IMAGE_LOAD_COMLETED, delay); // 바로 부르면 체크가 잘 안되는 현상이 확인 되어서 약간의 딜레이 후 다시 체크 한다.
        }
    }

    private void suspendCheckLoadImageTasks() {
        if (mImageLoadCheckTask != null && mImageLoadCheckTask.getState() == Thread.State.RUNNABLE) {
            mImageLoadCheckTask.setIsStop(true);
        }
    }

    private void addImageLoadCheckCount() {
        if (mImageLoadCheckTask == null) {
            return;
        }
        mImageLoadCheckTask.addImageLoadCheckCount();
    }

    protected void subImageLoadCheckCount() {
        if (mImageLoadCheckTask == null) {
            return;
        }
        mImageLoadCheckTask.subImageLoadCheckCount();
    }

    public boolean isScaledThumbnailMakeMode() {
        return isScaledThumbnailMakeMode;
    }

    public void setScaledThumbnailMakeMode(boolean scaledThumbnailMakeMode) {
        isScaledThumbnailMakeMode = scaledThumbnailMakeMode;
    }

    protected void initImageLoadCheckTask() {
        mImageLoadCheckTask = new ImageLoadCheckTask(this);
        mImageLoadCheckTask.setSnapsPageIndex(_page);
        mImageLoadCheckTask.setMakeThumbnail(isPageSaving());
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg == null) {
            return;
        }
        try {
            switch (msg.what) {
                case MSG_CALLBACK_LOAD_LAYERS:
                    loadAllLayers();
                    break;
                case MSG_CALLBACK_POST_IMAGE_LOAD_COMLETED:
                    postImageLoadComplateCallback(_page);

                    if (!isLoadedShadowLayer) {
                        loadShadowLayer();
                    }

                    onAllLayerImageLoaded();
                    break;
                case MSG_CALLBACK_HIDE_IMAGE_LOAD_PROGRESS:
                    hideImageLoadProgress();
                    break;
                case MSG_CALLBACK_HIDE_THUMBNAIL_PROGRESS:
                    hideThumbnailProgress();
                    break;
                case MSG_NOTIFY_TEXT_TO_IMAGEVIEW:
                    if (msg == null) {
                        return;
                    }
                    Object obj = msg.obj;
                    if (obj != null && obj instanceof SnapsTextToImageView) {
                        ((SnapsTextToImageView) obj).notifyChildrenControlState();
                    }
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected void onAllLayerImageLoaded() {
        notifyPageCanvasImageLoadSyncLockForSmartSnaps();
    }

    private void lockPageCanvasImageLoadSyncLockForSmartSnaps() { //이미지 로딩이 완료 된 시점에서 스마트 스냅스 애니메이션이 동작하도록 한다
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || !SmartSnapsManager.isSmartAreaSearching() || !isRealPagerView()) {
            return;
        }
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.lockPageCanvasImageLoadSyncLock(_page);
    }

    private void notifyPageCanvasImageLoadSyncLockForSmartSnaps() {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || !SmartSnapsManager.isSmartAreaSearching() || !isRealPagerView()) {
            return;
        }
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.notifyPageCanvasImageLoadSyncLocker(_page);
    }

    public FrameLayout getLayoutLayer() {
        return layoutLayer;
    }

    public FrameLayout getBonusLayer() {
        return bonusLayer;
    }

    public FrameLayout getBackGroundLayer() {
        return bgLayer;
    }

    public void setSelectorViewVisibility(int viewVisibility) {
        if (selectorView != null) {
            selectorView.setVisibility(viewVisibility);
        }
    }

    protected int changeDpToPx(Context context, int dp) {
        if (isThumbnailView()) {
            return dp;
        } else {
            return UIUtil.convertDPtoPX(context, dp);
        }
    }

    public boolean isPreventViewPagerScroll() {
        return false;
    }
}


