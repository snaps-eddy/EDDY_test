package com.snaps.mobile.activity.diary.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

/**
 * Created by ysjeong on 16. 4. 6..
 */
public class SnapsDiaryLimitLineTextView extends TextView {

    private int MAX_LINE_COUNT;
    private Context context = null;
    private boolean isForceMoreText = false;
    private String moreLabel;

    public SnapsDiaryLimitLineTextView(Context context) {
        super(context);
        init(context, null);
   }

    public SnapsDiaryLimitLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SnapsDiaryLimitLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SnapsDiaryLimitLineTextViewStyle);
            MAX_LINE_COUNT = typedArray.getInt(0, R.styleable.SnapsDiaryLimitLineTextViewStyle_maxLineCount);
            typedArray.recycle();
        }

        moreLabel = "  " + getContext().getResources().getString(R.string.diary_list_more_text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setLabelAfterEllipsis(MAX_LINE_COUNT);
    }

    public void setIsForceMoreText(boolean isForceMoreText) {
        this.isForceMoreText = isForceMoreText;
    }

    private void setLabelAfterEllipsis(int maxLines) {
        if (getText() == null) return;
        String displayText = getText().toString();
        if (displayText.endsWith(moreLabel)) return;

        if (getLayout().getLineCount() <= maxLines) {
            if (isForceMoreText) {
                addMoreText(displayText, "");
            }
            return;
        }

        String lastText;

        int start = getLayout().getLineStart(maxLines - 1);
        int end = getLayout().getLineEnd(maxLines-1);
        lastText = displayText.substring(start, end);

        displayText = displayText.substring(0, displayText.lastIndexOf(lastText));
        addMoreText(displayText, lastText);
    }
    
    private void addMoreText(String allText, String lastText) {
        int maxWidth = (int)(UIUtil.getScreenWidth(context)
                - context.getResources().getDimension(R.dimen.snaps_diary_list_margin)
                - context.getResources().getDimension(R.dimen.snaps_diary_limit_line_margin)); //더보기가 삐져 나가면 이 값을 조절 해야 함.

        if (lastText != null) {
            lastText = lastText.trim();
            if (lastText.length() > 0) {
                int textWidth;
                textWidth = getTextWidth(lastText + moreLabel, getTextSize());

                while (textWidth > maxWidth) {
                    if(lastText.trim().length() < 1) break;
                    int nextIdx = lastText.length() > 0 ? lastText.length() - 1 :  0;
                    lastText = lastText.substring(0, nextIdx).trim();
                    textWidth = getTextWidth(lastText + moreLabel, getTextSize());
                }

                lastText = lastText.trim();

                if (lastText.length() < 2) //더 보기가 자동으로 줄바꿈 되는 이상한 증상이 있어서, 차라리 날려 버린다.
                    lastText = "";
            }
        }

        allText = (allText + lastText).trim();

        setSpannableAppliedText(allText, moreLabel);
    }

    private void setSpannableAppliedText(String orgText, String elipsizeText) {
        setText(orgText);

        final SpannableStringBuilder sp = new SpannableStringBuilder(elipsizeText);
        sp.setSpan(new ForegroundColorSpan(Color.argb(255, 153, 153, 153)), 0, elipsizeText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new RelativeSizeSpan(0.8f), 0, elipsizeText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        append(sp);
    }

    private int getTextWidth(String text, float textSize) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        return (int) Math.ceil( bounds.width());
    }
}