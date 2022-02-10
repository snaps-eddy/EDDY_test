package com.snaps.common.text;

import android.content.Context;
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

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import errorhandle.SnapsAssert;
import font.FTextView;

/**
 * Created by ysjeong on 2018. 3. 12..
 */

public class SnapsTextToImageViewDp extends SnapsTextToImageView {
    private static final String TAG = SnapsTextToImageViewDp.class.getSimpleName();

    public SnapsTextToImageViewDp(Context context) {
        super(context);
    }

    public SnapsTextToImageViewDp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnapsTextToImageViewDp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SnapsTextToImageViewDp(Context context, SnapsTextControl control) {
        super(context);
        init(control);
    }

    public SnapsTextToImageViewDp(Context context, SnapsTextControl control,boolean isThumb) {
        super(context);
        init(control,isThumb);
    }

    private ImageView outLineView = null;
    private ImageView textToImageView = null;
    private FTextView placeHolderTextView = null;
    private ImageView cautionView = null;

    private SnapsTextToImageAttribute attribute = null;

    public void init(SnapsTextControl control){
      init(control,false);
    }
    public void init(SnapsTextControl control,boolean isThumnb) {
        this.attribute = SnapsTextToImageAttribute.createAttribute(control);
        attribute.setThumbnail(isThumnb);
        try {
            if (!isValidTextControl(control))
                return;

            setLayoutParamsByControl(control);

            initControlsWithTextControl(control);
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
        int controlWidth = changeDp(getContext(),(int) Float.parseFloat(control.width));

        MarginLayoutParams baseParams = new MarginLayoutParams(controlWidth, MarginLayoutParams.MATCH_PARENT);
        baseParams.setMargins(changeDp(getContext(),(control.getX())), changeDp(getContext(),(int) Float.parseFloat(control.y)), 0, 0);

        this.setLayoutParams(new FrameLayout.LayoutParams(baseParams));
        this.setClipChildren(false);
    }

    private void initControlsWithTextControl(SnapsTextControl control) throws Exception {
        int controlWidth = changeDp(getContext(),(int) Float.parseFloat(control.width));
        int controlHeight = changeDp(getContext(),(int) Float.parseFloat(control.height));

        outLineView = new ImageView(getContext());
        outLineView.setLayoutParams(new ViewGroup.LayoutParams(controlWidth, controlHeight));
        outLineView.setVisibility(View.VISIBLE);

        textToImageView = new ImageView(getContext());
        textToImageView.setLayoutParams(new ViewGroup.LayoutParams(controlWidth, controlHeight));
        textToImageView.setScaleType(ImageView.ScaleType.MATRIX);
        textToImageView.setVisibility(View.GONE);

        cautionView = new ImageView(getContext());
        cautionView.setLayoutParams(new ViewGroup.LayoutParams(controlWidth, controlHeight));
        int padding = changeDp(getContext(), 6);
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

    private void setOutlineStateOverArea() {
        if (outLineView == null) return;
        outLineView.setBackgroundResource(R.drawable.shape_red_e36a63_border_trans_solid_rect);
    }

    private void initPlaceHolderWithTextControl(SnapsTextControl textControl) {
        if (placeHolderTextView == null || textControl == null) return;

        try {
            String placeHolder = getPlaceHolderTextWithTextControl(textControl);
            float fontSize = (float)changeDp(getContext(),(int)getFontSizeWithTextControl(textControl));
            int controlWidth = changeDp(getContext(),textControl.getIntWidth());
            setPlaceHolderTextSize(fontSize, controlWidth);

            placeHolderTextView.setText(placeHolder);
        } catch (Exception e) { Dlog.e(TAG, e); }
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
                //showPlaceHolder();
            }
        } catch (Exception e) { Dlog.e(TAG, e);}
    }

    public void addClickEventListener(OnClickListener onClickListener) {
        if (!SnapsTextToImageUtil.isSupportEditTextProduct()) return;

        try {
            outLineView.setOnClickListener(onClickListener);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private void showTextToImageView() throws Exception {
        placeHolderTextView.setVisibility(View.GONE);
        textToImageView.setVisibility(View.VISIBLE);
    }

    private void showPlaceHolder() throws Exception {
        textToImageView.setVisibility(View.GONE);
        placeHolderTextView.setVisibility(View.VISIBLE);

        setOutlineStateNormal();
    }

    private void handleNetworkError() {
        try {
            textToImageView.setVisibility(View.GONE);
            placeHolderTextView.setVisibility(View.GONE);

            Dlog.e(TAG, "handleNetworkError()");
            setOutlineStateNetworkError();

            showCautionIcon();

            showCautionMsg();
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private void showOverAreaCautionMsg() {
        if (!isShownOnCurrentScreen()) return;
        try {
            MessageUtil.toast(getContext(), R.string.text_server_over_area_caution_msg);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private void showCautionMsg() {
        if (!isShownOnCurrentScreen()) return;
        try {
            MessageUtil.toast(getContext(), R.string.text_server_network_err_msg);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    //뷰페이저 상에서 좌측 혹은 우측에 가려져 있는 뷰의 경우, 메시지가 노출 되지 않도록 한다.
    private boolean isShownOnCurrentScreen() {
        try {
            int[] arr = new int[2];
            getLocationOnScreen(arr);
            return arr[0] >= 0 && arr[0] <= UIUtil.getScreenWidth(getContext());
        } catch (Exception e) { Dlog.e(TAG, e); }
        return false;
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

        final String requestUrl;
                   requestUrl =  SnapsTextToImageUtil.createTextToImageUrlWithAttributeDp(getContext(),attribute);
        ImageLoader.with(getContext()).load(requestUrl).setListener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                Dlog.e(TAG,  "loadTextImageAndCheckArea() onLoadFailed()");
                handleNetworkError();
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource != null && resource instanceof BitmapDrawable) {
                    try {
                        handleOnResourceReady((BitmapDrawable) resource);
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

            int textDrawableWidth = textDrawable.getIntrinsicWidth();
            int textDrawableHeight = textDrawable.getIntrinsicHeight();
            setTextDrawableDimensions(textDrawableWidth, textDrawableHeight);

            if (isOverTextAreaWithImageRect(textDrawableHeight)&& !Config.isBabyNameStickerEditScreen()) {
                setOutlineStateOverArea();

                showOverAreaCautionMsg();
            } else {
                if (Config.isBabyNameStickerEditScreen()) {
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
        ImageLoader.with(getContext()).load(requestUrl).setListener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                Dlog.e(TAG, "loadTextImage() onLoadFailed()");
                handleNetworkError();
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource != null && resource instanceof BitmapDrawable) {
                    try {
                        BitmapDrawable textDrawable = (BitmapDrawable) resource;
                        int textDrawableWidth = textDrawable.getIntrinsicWidth();
                        int textDrawableHeight = textDrawable.getIntrinsicHeight();
                        setTextDrawableThumbNailDimensions(textDrawableWidth,textDrawableHeight);
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

    private void setTextDrawableDimensions(int w, int h) throws Exception {
        SnapsTextControl textControl = attribute.getSnapsTextControl();
        textControl.textDrawableWidth = String.valueOf(w);
        textControl.textDrawableHeight = String.valueOf(h);

    }

    private void setTextDrawableThumbNailDimensions(int w, int h) throws Exception {
        SnapsTextControl textControl = attribute.getSnapsTextControl();
        textControl.textDrawableWidth = String.valueOf(w);
        textControl.textDrawableHeight = String.valueOf(h);

    }

    private boolean isOverTextAreaWithImageRect(int textDrawableHeight) throws Exception {
        return textDrawableHeight > changeDp(getContext(),attribute.getSnapsTextControl().getIntHeight());
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

    private int changeDp(Context context,int dp) {
        if(isThumbnail()) {
            return dp;
        } else {
            return UIUtil.convertDPtoPXBabyNameSticker(context,dp);
        }
    }
}
