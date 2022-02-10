package com.snaps.common.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

/**
 * Created by ysjeong on 16. 8. 9..
 */
public class SnapsUnderlineTextView extends RelativeLayout {

	private boolean isDrawUnderline;
	private Paint rectPaint;
	private Context context;
	private TextView textView;
	private String text;
	private int textSize;
	private int textColor;

	public SnapsUnderlineTextView(Context context) {
		super(context);
		init(context, null);
		FontUtil.applyTextViewTypeface(textView, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
	}

	public SnapsUnderlineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SnapsUnderlineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public boolean isDrawUnderline() {
		return isDrawUnderline;
	}

	public void drawUnderline(boolean isDrawUnderline) {
		this.isDrawUnderline = isDrawUnderline;
	}

	private void init(Context context, AttributeSet attrs) {
		this.context = context;

		setWillNotDraw(false);

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					com.snaps.mobile.R.styleable.SnapsUnderlineTextView);
			text = a.getText(R.styleable.SnapsUnderlineTextView_text).toString();
			textSize = a.getInteger(R.styleable.SnapsUnderlineTextView_textSize, 15);
			textColor = a.getColor(R.styleable.SnapsUnderlineTextView_textColor, 0xffffff);

			a.recycle();
		}

		setMinimumWidth(UIUtil.convertDPtoPX(context, 40));

		textView = new TextView(context);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		lp.setMargins(UIUtil.convertDPtoPX(context, 4), 0, UIUtil.convertDPtoPX(context, 4), 0);
		textView.setLayoutParams(lp);
		textView.setText(text);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
		textView.setTextColor(textColor);

		//apply typeFace
		if (attrs != null) {
			TypedArray fTextViewAttr = context.obtainStyledAttributes(attrs, R.styleable.FTextViewAttrs);
			if (fTextViewAttr == null) {
				return;
			}

			String fontString = fTextViewAttr.getString(R.styleable.FTextViewAttrs_customFont);
			if (fontString == null) {
				return;
			}

			if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_760))) {
				FontUtil.applyTextViewTypeface(textView, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
			} else if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_740))) {
				FontUtil.applyTextViewTypeface(textView, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
			} else if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_720))) {
				FontUtil.applyTextViewTypeface(textView, FontUtil.eSnapsFonts.YOON_GOTHIC_720);
			} else if (fontString.equalsIgnoreCase(context.getString(R.string.font_name_ygt_330))) {
				FontUtil.applyTextViewTypeface(textView, FontUtil.eSnapsFonts.YOON_GOTHIC_330);
			} else {
				FontUtil.applyTextViewTypeface(textView, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
			}

			fTextViewAttr.recycle();
		}

		addView(textView);

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Paint.Style.FILL);
		rectPaint.setColor(Color.parseColor("#e8625a"));

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isDrawUnderline()) {
			float left = textView.getLeft();
			float top = textView.getTop();
			float right = textView.getRight();
			float bottom = textView.getBottom();

			top += textView.getMeasuredHeight() / 2;
			left -= UIUtil.convertDPtoPX(context, 2);
			right += UIUtil.convertDPtoPX(context, 2);

			canvas.drawRect(left, top, right, bottom, rectPaint);
		}
	}
}
