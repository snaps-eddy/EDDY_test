package com.snaps.common.spc.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.imp.iSnapsPageCanvasInterface;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.control.TextFormat;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.interfaces.ISnapsControl;

/**
 * com.snaps.spc.view SnapsTextView.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 6. 3.
 * @Version :
 */
public class SnapsTextView extends RelativeLayout implements ISnapsControl {
    private static final String TAG = SnapsTextView.class.getSimpleName();

    private SnapsTextControl data = null;
    private TextView textView;
    String pageType;
    String controlType;
    iSnapsPageCanvasInterface _callback = null;
    boolean day = false;
    boolean dayTitle = false;
    boolean monthTitle = false;
    boolean year = false;
    boolean month = false;
    boolean isThumbnail = false;
    boolean isPreview = false;

    SnapsControl mSnapsControl = null;

    public SnapsTextView(Context context) {
        super(context);
    }

    public SnapsTextView(String pageType, Context context, SnapsTextControl control, iSnapsPageCanvasInterface callback) {
        super(context);
        this._callback = callback;
        this.pageType = pageType;
        init(context, control);
    }

    public SnapsTextView(String pageType, String controlType, Context context, SnapsTextControl control, iSnapsPageCanvasInterface callback) {
        super(context);
        this._callback = callback;
        this.pageType = pageType;
        this.controlType = controlType;
        init(context, control);
    }

    public SnapsTextView(Context context, SnapsTextControl control) {
        super(context);
        init(context, control);
    }

    public void init(final Context context, final SnapsTextControl control) {
        data = control;

        if (control == null || control.width.isEmpty() || control.width.compareTo("") == 0 || control.height.compareTo("") == 0)
            return;

        boolean isVerticalText = data.format.orientation == TextFormat.TEXT_ORIENTAION_VERTICAL;

        if (isVerticalText) {
            if (!data.format.bVerticalViewValueChaneged) {
                data.format.bVerticalViewValueChaneged = true;
            }
        }

        int width = (int) Float.parseFloat(control.width);
        // 책등인 경우 width를 늘려준다.
        if ((Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook() || Const_PRODUCT.isSNSBook(Config.getPROD_CODE())) && (control.format.verticalView.equalsIgnoreCase("true"))) {
            width = width + Math.abs(control.getMaxPageX()) * 2;
        }

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, (int) (Float.parseFloat(control.height)));

        params.setMargins(control.getX(), (int) Float.parseFloat(control.y), 0, 0);

        setLayoutParams(new FrameLayout.LayoutParams(params));

        if (isVerticalText)
            this.setGravity(Gravity.CENTER_VERTICAL);
        else
            this.setGravity(Gravity.CENTER_VERTICAL);

        float scale = 1.33f;// TODO kakaobook, fb : 카카오북,페북북 text 폰트사이즈 조절

        if (Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook() || Config.isCalendar() || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isCardProduct())
            scale = 1.0f;

        if (Const_PRODUCT.isSnapsDiary(Config.getPROD_CODE()) && "totalpost_scale".equalsIgnoreCase(control.getSnsproperty())) {
            scale = 1.25f;
        } else if (Const_PRODUCT.isInstagramBook(Config.getPROD_CODE())) {
            scale = 0.98f;
        } else if (Const_PRODUCT.isNewKakaoBook(Config.getPROD_CODE()) || Const_PRODUCT.isFacebookPhotobook(Config.getPROD_CODE())) {
            scale = 1.0f;
            if (isVerticalText && control.format.fontFace != null && control.format.fontFace.startsWith("Roboto")) {
                scale = .975f;
            } else if (control.format.fontFace != null && control.format.fontFace.startsWith("Noto")) {
            }

            //TODO  ...폰트 적용 후에서 넘어가는지 확인 필요함...
            try {
                int fontSize = (int) Float.parseFloat(control.format.fontSize);
                if (fontSize > 60) scale = .8f;
            } catch (NumberFormatException e) {
                Dlog.e(TAG, e);
            }
        }

        //화면에 짤려 보여서 좀 작게 축소해서 보여 줌.
        /*
        if (control.controType.compareTo("daytitle") == 0) {
            scale = .8f;
        }
        */

        float size = 1.f;
        try {
            if (control.format != null && !StringUtil.isEmpty(control.format.fontSize)) {
                size = Float.parseFloat(control.format.fontSize) * scale;// / context.getResources().getDisplayMetrics().density;
            }
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }

        textView = new CustomTextView(getContext());

        // 텍스트뷰가 클릭이 되었을때 호출되는 함수
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isThumbnail() || isPreview()) return;
                Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
                intent.putExtra("control_id", v.getId());
                intent.putExtra("isEdit", false);

                getContext().sendBroadcast(intent);
            }
        });

        if (isVerticalText || control.format.verticalView.equalsIgnoreCase("true")) {
            Dlog.d("init() control.format.verticalView.equalsIgnoreCase() true");

            // 책등은 회전에 의해 위치가 변경이 되므로 재조정...
            if (isVerticalText) {
                params.setMargins((int) (control.getX() + (control.getIntWidth() / 2.f + control.getIntHeight() / 2.f)), (int) Float.parseFloat(control.y), 0, 0);
            } else
                params.setMargins((int) (control.getX() + Float.parseFloat(control.height)), (int) Float.parseFloat(control.y), 0, 0);

            setLayoutParams(new FrameLayout.LayoutParams(params));

            RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            rotate.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }
            });
            this.setAnimation(rotate);

            this.setBackgroundColor(Color.TRANSPARENT);
        }

//        Typeface fontFace = null;
//        // KR버전만 소망체 유효함. 닷컴버전은 meiryo로 전부설정
//        if (control.format.alterFontFace.equalsIgnoreCase("스냅스 소망2 M") && Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_KOR)) {
//            try {
//                textView.setTypeface(Const_VALUE.SNAPS_TYPEFACE_SOMANG);
//            } catch (Exception e) {
//                Dlog.e(TAG, e);
//            }
//        } else {
//            try {
//                textView.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
//            } catch (Exception e) {
//                Dlog.e(TAG, e);
//            }
//        }
//        if (control.type.compareTo("calendar") == 0) {
//            String uiFont = "true".equalsIgnoreCase(control.format.bold) ? (control.format.fontFace + " Bold") : control.format.fontFace;
//            Typeface typeface = Const_VALUE.sTypefaceMap.get(uiFont);
//            textView.setTypeface(typeface);
//        }

        if (Const_PRODUCT.isSNSBook(Config.getPROD_CODE())) {
            ATask.executeVoid(new ATask.OnTaskObject() {
                @Override
                public void onPre() {

                }

                @Override
                public Object onBG() {
                    return FontUtil.getFontTypeface(context, control.format.fontFace + control.format.italic + control.format.bold);
                }

                @Override
                public void onPost(Object typeface) {
                    if (typeface != null) textView.setTypeface((Typeface) typeface);
                }
            });


            textView.setLayoutParams(new ViewGroup.LayoutParams(getLayoutParams().width, getLayoutParams().height));
            if (isVerticalText) {
                textView.setSingleLine(true);
            }
        } else
            textView.setLayoutParams(new ViewGroup.LayoutParams(getLayoutParams().width, getLayoutParams().height));

        if (control.format.verticalView.equalsIgnoreCase("true")) {
            textView.setGravity(Gravity.CENTER);
            // textView.setVisibility(View.VISIBLE);
            if (!Config.isThemeBook() && !Config.isSimplePhotoBook() && !Config.isSimpleMakingBook() && !Config.isCalendar() && !Const_PRODUCT.isSNSBook(Config.getPROD_CODE()) && !Const_PRODUCT.isPackageProduct() && !Const_PRODUCT.isCardProduct()) {
                textView.setTextColor(Color.TRANSPARENT);
                textView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        if (isVerticalText) {
            textView.setGravity(Gravity.CENTER_VERTICAL);
        } else if (!isVerticalText && control.format.align.equalsIgnoreCase("center")) {
            if (!Config.isCalendar() && !Const_PRODUCT.isCardProduct() && !Config.isSnapsDiary()) {
                textView.setGravity(Gravity.CENTER);
                textView.setMaxLines(3);
                textView.setLayoutParams(new ViewGroup.LayoutParams(getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT));

            } else {

                textView.setGravity(Gravity.CENTER_HORIZONTAL);// | Gravity.TOP);
            }

            if (!Config.isCalendar())
                textView.setLineSpacing(0, size / 10);

        } else if (control.format.align.equalsIgnoreCase("right")) {
            textView.setGravity(Gravity.RIGHT);

            if (control.format.verticalView.equalsIgnoreCase("true")) {
                textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            }
        } else if (control.format.align.equalsIgnoreCase("left")) {
            textView.setGravity(Gravity.LEFT);

            if (control.format.verticalView.equalsIgnoreCase("true")) {
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }
        }
        if (Const_PRODUCT.isNewKakaoBook(Config.getPROD_CODE())) {
            textView.setLineSpacing(1, 1);
        } else if (Const_PRODUCT.isFacebookPhotobook(Config.getPROD_CODE()) || Const_PRODUCT.isInstagramBook(Config.getPROD_CODE()) || Const_PRODUCT.isSNSBook()) {
            textView.setLineSpacing(0, 1);
        }

        if (Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook() || Config.isCalendar()) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), size));
        } else if (Const_PRODUCT.isSnapsDiary(Config.getPROD_CODE())) { // 미리보기 텍스트 사이즈가 렌더보다 크게 나와서 수정. 다른 sns북에도 적용할지는 추후 테스트 후 결정.
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), (size * .76f)));
        } else if (Const_PRODUCT.isSNSBook(Config.getPROD_CODE())) {
            //자꾸 글씨가 잘려 보인다는 QA 이슈 때문에 큰 글씨는 좀 작게 줄여서 보여줌..
            if (size > 30)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), (size * .76f)));
            else if (size == 5)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), (size * .78f)));
            else
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), size));
        } else
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), (size * 2)));

        setTextColor(control);

        if (!control.format.verticalView.equalsIgnoreCase("true")) {
            if (Config.PRODUCT_THEMEBOOK_A5.equalsIgnoreCase(Config.getPROD_CODE())) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), size));
            } else if (Config.PRODUCT_THEMEBOOK_A6.equalsIgnoreCase(Config.getPROD_CODE())) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), size));
            }
        }
//		String text = null;
//		if(data.text !=null) {
//			text = data.text.replace("","\u00A0");
//		} else {
//			text = "";
//		}
        textView.setText(data.text);

        if (data.text.equals("")) {
            textView.setText(control.initialText);
        } else
            textView.setHint(control.initialText);

        addView(textView);
        textView.setIncludeFontPadding(false);

        /**
         * FIXME 달력은 onDraw에서 텍스트를 그리는 게 아니라, 생성 단계에서 처리 하고 있다. 썸네일 생성 도중, 앱이 포커스를 잃었을 때, onDraw가 호출되지 않기 때문이다.. 다른 제품군도 추가 작업이 필요 해 보인다.
         */
        if (Config.isCalendar())
            setLineText(textView);

        if (data.priority.equals("test")) {
            setBackgroundColor(Color.argb(40, 255, 0, 0));
        }
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public void setThumbnail(boolean flag) {
        this.isThumbnail = flag;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean flag) {
        this.isPreview = flag;
    }

    public TextView getTextView() {
        return textView;
    }

    void setTextColor(SnapsTextControl control) {
        String color = "ff000000";
        if (control.format.fontColor.compareTo("0") == 0)
            color = "ff000000";
        else
            color = control.format.fontColor.length() == 8 ? control.format.fontColor : "ff" + control.format.fontColor;

        try {
            textView.setTextColor(Color.parseColor("#" + color));
        } catch (IllegalArgumentException e) {
            Dlog.e(TAG, e);
        }
    }

    private void setLineText(TextView view) {
        setLineText(view, Config.isCalendar());
    }

    private void setLineText(TextView view, boolean isCalendar) {
        if (isThumbnail()) return;

        int end;
        int start;
        LineText lineText;

        int textHeight = -1;


        if (data.textList.size() == 0) {

            int lineCount = view.getLineCount();
            if (isCalendar) {
                String str = view.getText().toString().trim();
                if (str != null && view.getText().toString().length() > 0)
                    lineCount = 1;
            }

            for (int i = 0; i < lineCount; i++) {

                lineText = new LineText();

                if (isCalendar) {
                    lineText.text = view.getText() != null ? view.getText().toString() : "";
                    lineText.width = data.width;
                    lineText.height = data.height;
                    lineText.x = data.getX() + "";
                    lineText.y = data.y + "";
                } else {
                    end = view.getLayout().getLineEnd(i);
                    start = view.getLayout().getLineStart(i);

                    lineText.x = data.getX() + "";
                    if (textHeight > -1)
                        lineText.y = String.valueOf(((int) Float.parseFloat(data.y) + view.getTop() + (i * textHeight)));
                    else
                        lineText.y = String.valueOf(((int) Float.parseFloat(data.y) + view.getTop() + view.getLayout().getLineTop(i)));
                    lineText.width = data.width;
                    if (textHeight > -1)
                        lineText.height = String.valueOf(textHeight);
                    else
                        lineText.height = String.valueOf(view.getLayout().getLineBottom(i) - view.getLayout().getLineTop(i));

                    lineText.text = view.getText().toString().substring(start, end);
                }

                data.textList.add(lineText);
            }
        }
    }

    /**
     * @param text
     */
    public void text(String text) {
        if (!isThumbnail()) {
            data.text = text;
        }
        this.textView.setText(text);

        if (!isThumbnail()) {
            // 데이터가 설정이 되었을때는 다시 lineText를 만들기 위해 초기화...
            data.textList.clear();
        }
    }

    @Override
    public SnapsControl getSnapsControl() {
        return mSnapsControl;
    }

    @Override
    public void setSnapsControl(SnapsControl snapsControl) {
        this.mSnapsControl = snapsControl;
    }

    @Override
    public View getView() {
        return this;
    }

    /**
     * com.snaps.kakao.activity.edit.view SnapsTextView.java
     */
    class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {

        public CustomTextView(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!Config.isCalendar() && !Const_PRODUCT.isNewKakaoBook(Config.getPROD_CODE()) && !Const_PRODUCT.isFacebookPhotobook(Config.getPROD_CODE()) && !Const_PRODUCT.isInstagramBook(Config.getPROD_CODE())) {
                setLineText(this);
            }
        }
    }
}
