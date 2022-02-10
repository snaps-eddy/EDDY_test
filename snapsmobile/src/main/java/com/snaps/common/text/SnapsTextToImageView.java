package com.snaps.common.text;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import errorhandle.SnapsAssert;
import font.FTextView;

import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_MSG_WHAT;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_PAGE_INDEX;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.SNAPS_BROADCAST_BUNDLE_VALUE_TEXT_TO_IMAGE_SHOW_OVER_AREA_MSG;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.SNAPS_BROADCAST_BUNDLE_VALUE_TEXT_TO_IMAGE_SHOW_TEXT_SERVER_NETWORK_ERR_MSG;

/**
 * Created by ysjeong on 2018. 3. 12..
 */

public class SnapsTextToImageView extends RelativeLayout {
    private static final String TAG = SnapsTextToImageView.class.getSimpleName();

    //Ben
    //이미지 로딩 완료를 체크하려고
    private Listener listener = null;
    public interface Listener {
        void onLoadImage();
    }
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public SnapsTextToImageView(Context context) {
        super(context);
    }

    public SnapsTextToImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnapsTextToImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SnapsTextToImageView(Context context, SnapsTextControl control) {
        super(context);
        init(control);
    }

    public SnapsTextToImageView(Context context, SnapsTextControl control, int pageCanvasWidth, int pageCanvasHeight) {
        super(context);
        mPageCanvasWidth = pageCanvasWidth;
        mPageCanvasHeight = pageCanvasHeight;
        init(control);
    }

    private ImageView outLineView = null;
    private ImageView textToImageView = null;
    private FTextView placeHolderTextView = null;
    private ImageView cautionView = null;
    private int mPageCanvasWidth;
    private int mPageCanvasHeight;
//    private int mImageScale;

    private SnapsTextToImageAttribute attribute = null;

    private void init(SnapsTextControl control) {
        this.attribute = SnapsTextToImageAttribute.createAttribute(control);

        try {
            if (!isValidTextControl(control))
                return;

            setLayoutParamsByControl(control);

            initControlsWithTextControl(control);

            this.attribute.setImageScale(1);    // fontSize가 없는 경우 문제가 발생해서 기본값 설정

            int fontSize = Integer.parseInt(control.format.fontSize);
            //폰트 크기가 작은 경우 이미지 서버에서 전달해주는 이미지 해상도가 너무 낮아서 이상하게 보인다. 그래서 scale을 조정한다.
            //내 마음대로 12
            //현재 폰트 크기를 변경하는 UI가 없어서 mImageScale값을 여기서만 계산한다.
//            mImageScale = (fontSize > 12 ? 1 : 2);
//            if (mImageScale > 1) {
//                Matrix matrix = textToImageView.getMatrix();
//                matrix.setScale(1f / (float) mImageScale, 1f / (float) mImageScale);
//                textToImageView.setImageMatrix(matrix);
//            }
            // @Marko
            // 외부에서 해당 스케일 사용하기 때문에 attribute 로 저장 객체 변경.
            int mImageScale = fontSize > 12 ? 1 : 2;
            this.attribute.setImageScale(mImageScale);
            if (mImageScale > 1) {
                Matrix matrix = textToImageView.getMatrix();
                matrix.setScale(1f / (float) mImageScale, 1f / (float) mImageScale);
                textToImageView.setImageMatrix(matrix);
            }


        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(e);
        }
    }

    private boolean isValidTextControl(SnapsTextControl control) {
        return control != null && !StringUtil.isEmpty(control.width) && !StringUtil.isEmpty(control.height);
    }

    public boolean isWrittenText() {
        if (attribute == null) return false;
        SnapsTextControl textControl = attribute.getSnapsTextControl();
        return textControl != null && !StringUtil.isEmpty(textControl.text);
    }

    private void setLayoutParamsByControl(SnapsTextControl control) throws Exception {
        int controlWidth = (int) Float.parseFloat(control.width);
        int controlHeight = (int) Float.parseFloat(control.height);

        int x = control.getX();
        int y = (int) Float.parseFloat(control.y);
//        if (control.angle.equals("90") || control.angle.equals("270")) {ㅜ
            //control이 화면 영역안에 존재하지 않은 경우
            if (y + controlHeight > mPageCanvasHeight) {
                y = mPageCanvasHeight - controlHeight; //y값 보정
                int offsetY = (int) Float.parseFloat(control.y) - (mPageCanvasHeight - controlHeight);
                this.setTranslationY(offsetY);  //수평이동
            }
//        }

        MarginLayoutParams baseParams = new MarginLayoutParams(controlWidth, MarginLayoutParams.MATCH_PARENT);
        baseParams.setMargins(x, y, 0, 0);

        this.setLayoutParams(new FrameLayout.LayoutParams(baseParams));
        this.setClipChildren(false);
    }

    private void initControlsWithTextControl(SnapsTextControl control) throws Exception {
        int controlWidth = (int) Float.parseFloat(control.width);
        int controlHeight = (int) Float.parseFloat(control.height);

        outLineView = new ImageView(getContext());
        outLineView.setLayoutParams(new ViewGroup.LayoutParams(controlWidth, controlHeight));
        outLineView.setVisibility(View.VISIBLE);

        textToImageView = new ImageView(getContext());
        textToImageView.setLayoutParams(new ViewGroup.LayoutParams(controlWidth, controlHeight));
        textToImageView.setScaleType(ImageView.ScaleType.MATRIX);
        textToImageView.setVisibility(View.GONE);

        cautionView = new ImageView(getContext());
        cautionView.setLayoutParams(new ViewGroup.LayoutParams(controlWidth, controlHeight));
        int padding = UIUtil.convertDPtoPX(getContext(), 6);
        cautionView.setPadding(padding, padding, padding, padding);
        cautionView.setImageResource(R.drawable.icon_text_server_caution);
        cautionView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        cautionView.setVisibility(View.GONE);

        placeHolderTextView = new FTextView(getContext());
        placeHolderTextView.setLayoutParams(new ViewGroup.LayoutParams(controlWidth, controlHeight));
        placeHolderTextView.setGravity(Gravity.CENTER);
        placeHolderTextView.setVisibility(View.GONE);
        initPlaceHolderWithTextControl(control);

        this.addView(placeHolderTextView);
        this.addView(textToImageView);
        this.addView(outLineView);
        this.addView(cautionView);

        this.setPivotX(0.0f);
        this.setPivotY(0.0f);
        this.setRotation(Float.parseFloat(control.angle));
    }

    //장바구니 스킨 이미지 만들기 위한 용도
    //텍스트 편집을 하지 않고 장바구니에 저장하면 장바구니 이미지에 outline이 표시되는 문제가 있다.
    public void setVisibleOutLine(boolean isShow) {
        if (outLineView == null) return;
        outLineView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    private void removeOutline() {
        if (outLineView == null) return;
        outLineView.setBackgroundResource(0);
    }

    private void setOutlineStateNormal() {
        if (outLineView == null) return;
        outLineView.setBackgroundResource(R.drawable.shape_gray_cccccc_dash_border_trans_solid_rect);
    }

    private void setOutlineStateNetworkError() {
        if (outLineView == null) return;
        outLineView.setBackgroundResource(R.drawable.shape_red_e36a63_dash_border_trans_solid_rect);
    }

    /**
     * @Marko 스마트톡 상품은 글자가 영역을 넘어가도 빨간 영역 표시 안하도록 막는다.
     */
    private void setOutlineStateOverArea() {
        if (outLineView == null) return;
        if (Const_PRODUCT.isSmartTalkProduct()) {
            return;
        }
        outLineView.setBackgroundResource(R.drawable.shape_red_e36a63_border_trans_solid_rect);
    }

    private void initPlaceHolderWithTextControl(SnapsTextControl textControl) {
        if (placeHolderTextView == null || textControl == null) return;

        try {
            String placeHolder = getPlaceHolderTextWithTextControl(textControl);
            float fontSize = getFontSizeWithTextControl(textControl);
            int controlWidth = textControl.getIntWidth();
            setPlaceHolderTextSize(fontSize, controlWidth);

            placeHolderTextView.setText(placeHolder);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private String getPlaceHolderTextWithTextControl(SnapsTextControl textControl) {
        //KT 북
        if (Config.isKTBook()) {
            return Const_VALUES.KT_BOOK_INPUT_TEXT_HINT_TEXT;
        }
        return textControl != null && !StringUtil.isEmpty(textControl.initialText) ? textControl.initialText : getContext().getString(R.string.text_control_empty_hint);
    }

    private float getFontSizeWithTextControl(SnapsTextControl textControl) {
        return textControl != null && textControl.format != null && !StringUtil.isEmpty(textControl.format.fontSize) ? Float.parseFloat(textControl.format.fontSize) : 10.f;
    }

    private void setPlaceHolderTextSize(float fontSize, int controlWidth) throws Exception {
        //KT 북 -  텍스트 입력 힌트 글자 크게
        if (Config.isKTBook()) {
            fontSize = fontSize * 2;
        }
        placeHolderTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), (Math.max(11, fontSize))));
        float sampleRat = 1.f;
        String text = getContext().getString(R.string.text_control_empty_hint);
        Rect bounds = new Rect();

        while (sampleRat > .2f) {
            Paint textPaint = placeHolderTextView.getPaint();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            if (controlWidth * .9f < bounds.width()) {
                sampleRat *= .8f;
                placeHolderTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), (Math.max(8, fontSize * sampleRat))));
            } else {
                break;
            }
        }

        //KT 북 -  텍스트 입력 힌트 글자 크게
        if (Config.isKTBook()) {
            placeHolderTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), 20.f));
        }
    }

    public void notifyChildrenControlState() {
        try {
            if (hasTextContents()) {
                showTextToImageView();

                if (isThumbnail()) {
                    loadTextImage();
                } else {
                    loadTextImageAndCheckArea();
                }
            } else {
                showPlaceHolder();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void addClickEventListener(View.OnClickListener onClickListener) {
        if (!SnapsTextToImageUtil.isSupportEditTextProduct()) return;

        try {
            outLineView.setOnClickListener(onClickListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void showTextToImageView() throws Exception {
        setVisiblePlaceHolder(false);
        textToImageView.setVisibility(View.VISIBLE);
    }

    private void showPlaceHolder() {
        textToImageView.setVisibility(View.GONE);
        setVisiblePlaceHolder(true);

        setOutlineStateNormal();
    }

    /**
     * @Marko 장바구니용 에디터 화면에서는 placeholder 텍스트를 보여주지 않아도 되므로, 외부에서 숨길 수 있는 함수 추가함.
     */
    public void setVisiblePlaceHolder(boolean isShow) {
        if (placeHolderTextView == null) {
            return;
        }
        placeHolderTextView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void handleNetworkError() {
        try {
            textToImageView.setVisibility(View.GONE);
            setVisiblePlaceHolder(false);

            Dlog.e(TAG, "handleNetworkError()");
            setOutlineStateNetworkError();

            showCautionIcon();

            showCautionMsg();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void showOverAreaCautionMsg() {
        if (getContext() == null) return;
        try {
            Intent intent = new Intent(Const_VALUE.TEXT_TO_IMAGE_ACTION);
            intent.putExtra(SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_MSG_WHAT, SNAPS_BROADCAST_BUNDLE_VALUE_TEXT_TO_IMAGE_SHOW_OVER_AREA_MSG);
            intent.putExtra(SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_PAGE_INDEX, getPageIndex());

            getContext().sendBroadcast(intent);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void showCautionMsg() {
        if (getContext() == null) return;
        try {
            Intent intent = new Intent(Const_VALUE.TEXT_TO_IMAGE_ACTION);
            intent.putExtra(SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_MSG_WHAT, SNAPS_BROADCAST_BUNDLE_VALUE_TEXT_TO_IMAGE_SHOW_TEXT_SERVER_NETWORK_ERR_MSG);
            intent.putExtra(SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_PAGE_INDEX, getPageIndex());

            getContext().sendBroadcast(intent);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private int getPageIndex() {
        if (attribute == null) return -1;
        SnapsTextControl snapsTextControl = attribute.getSnapsTextControl();
        if (snapsTextControl == null) return -1;
        return snapsTextControl.getPageIndex();
    }

    private void showCautionIcon() {
        if (cautionView != null) {
            cautionView.setVisibility(View.VISIBLE);
        }
    }

    private void hideCautionIcon() {
        if (cautionView != null && cautionView.isShown()) {
            cautionView.setVisibility(View.GONE);
        }
    }

    private void loadTextImageAndCheckArea() {
        final String requestUrl = SnapsTextToImageUtil.createTextToImageUrlWithAttribute(attribute);
        Dlog.d("Request Image URL : " + requestUrl);

        ImageLoader imageLoader = ImageLoader.with(getContext()).load(requestUrl);
        int mImageScale = attribute.getImageScale();

        if (mImageScale > 1) {
            imageLoader = imageLoader.override(
                    attribute.getSnapsTextControl().getIntWidth() * mImageScale,
                    attribute.getSnapsTextControl().getIntHeight() * mImageScale);
        }

        imageLoader.setListener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                Dlog.e(TAG, "loadTextImageAndCheckArea() onLoadFailed()");
                handleNetworkError();
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof BitmapDrawable) {
                    try {
                        handleOnResourceReady((BitmapDrawable) resource);
                        if (listener != null) {
                            listener.onLoadImage();
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        handleNetworkError();
                    }
                } else {
                    handleNetworkError();
                }
                return false;
            }
        }).into(textToImageView);
    }

    private void handleOnResourceReady(BitmapDrawable textDrawable) {
        try {
            hideCautionIcon();

            int mImageScale = attribute.getImageScale();

            int textDrawableWidth = textDrawable.getIntrinsicWidth() / mImageScale;
            int textDrawableHeight = textDrawable.getIntrinsicHeight() / mImageScale;
            setTextDrawableDimensions(textDrawableWidth, textDrawableHeight);

            if (isOverTextAreaWithImageRect(textDrawableHeight) && !Const_PRODUCT.isBabyNameStikerGroupProduct()) {
                setOutlineStateOverArea();

                showOverAreaCautionMsg();
            } else {
                if (isEdited()) {
                    removeOutline();
                } else {
                    setOutlineStateNormal();
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            handleNetworkError();
        }
    }

    public boolean isEdited() {
        SnapsTextControl textControl = attribute != null ? attribute.getSnapsTextControl() : null;
        return textControl != null && textControl.isEditedText;
    }

    private void loadTextImage() {
        final String requestUrl = SnapsTextToImageUtil.createTextToImageUrlWithAttribute(attribute);
        ImageLoader.with(getContext()).load(requestUrl).into(textToImageView);
    }

    private void setTextDrawableDimensions(int w, int h) throws Exception {
        SnapsTextControl textControl = attribute.getSnapsTextControl();
        textControl.textDrawableWidth = String.valueOf(w);
        textControl.textDrawableHeight = String.valueOf(h);

    }

    /**
     * @Marko 폰트의 특성상 텍스트가 한줄을 넘어가지 않지만 원본 틀보다 크게 내려올 수 있으므로,
     * 반사슬로건 한정으로 overtext 체크를 변경한다.
     * 추후 다른 제품에서도 같은 로직을 태웠을 때 문제가 없다면 다 같이 적용하자.
     * @Marko 2020.06.10
     * 스마트톡에 들어간 텍스트 이미지 체크 안하도록 한다. 토스트도 안띄워야 하는건가?>
     */
    private boolean isOverTextAreaWithImageRect(int textDrawableHeight) throws Exception {
        if (Const_PRODUCT.isMagicalReflectiveSloganProduct() || Const_PRODUCT.isReflectiveSloganProduct() || Const_PRODUCT.isHolographySloganProduct()) {
            return (float) textDrawableHeight > attribute.getSnapsTextControl().getIntHeight() * 1.6f;
        }

        return textDrawableHeight > attribute.getSnapsTextControl().getIntHeight();
    }

    private boolean hasTextContents() {
        return attribute != null && attribute.getSnapsTextControl() != null && !StringUtil.isEmptyAfterTrim(attribute.getSnapsTextControl().text);
    }

    public ImageView getOutLineView() {
        return outLineView;
    }

    public ImageView getTextToImageView() {
        return textToImageView;
    }

    public FTextView getPlaceHolderTextView() {
        return placeHolderTextView;
    }

    public SnapsTextToImageAttribute getAttribute() {
        return attribute;
    }

    public boolean isThumbnail() {
        return getAttribute() != null && getAttribute().isThumbnail();
    }

    public void setThumbnail(float ratioX, float ratioY) {
        if (getAttribute() != null) {
            getAttribute().setThumbnail(true);
            getAttribute().setThumbnailRatioX(ratioX);
            getAttribute().setThumbnailRatioY(ratioY);
        }
    }

    /**
     * 외부에서 이미지 로드 해서 넣어주는 방식
     *
     * @param bitmap
     */
    public void loadTextImage(Bitmap bitmap) {
        try {
            if (hasTextContents()) {
                showTextToImageView();

                textToImageView.setImageBitmap(bitmap);

                if (!isThumbnail()) {
                    handleOnResourceReady(bitmap);
                }

            } else {
                showPlaceHolder();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void handleOnResourceReady(Bitmap resource) {
        try {
            hideCautionIcon();

            int mImageScale = attribute.getImageScale();

            int textDrawableWidth = resource.getWidth() / mImageScale;
            int textDrawableHeight = resource.getHeight() / mImageScale;
            setTextDrawableDimensions(textDrawableWidth, textDrawableHeight);

            if (isOverTextAreaWithImageRect(textDrawableHeight) && !Const_PRODUCT.isBabyNameStikerGroupProduct()) {
                setOutlineStateOverArea();

                showOverAreaCautionMsg();
            } else {
                if (isEdited()) {
                    removeOutline();
                } else {
                    setOutlineStateNormal();
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            handleNetworkError();
        }
    }

    public void showEmptyTextView() {
        showPlaceHolder();
    }

}
