package com.snaps.common.spc.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.imp.iSnapsPageCanvasInterface;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * com.snaps.spc.view SnapsTextView.java
 * 
 * @author JaeMyung Park
 * @Date : 2013. 6. 3.
 * @Version :
 */
public class SnapsPhotoCardTextView extends SnapsBaseCardTextView {
	private static final String TAG = SnapsPhotoCardTextView.class.getSimpleName();
	private final int MAX_PHOTO_CARD_TEXT_LINE_COUNT = 15;
	/**
	 * FIXME 단말기에서 보여지는 글씨 크기와 실제 랜더링 된 텍스트 크기가 다르므로 이 수치를 조절하여, 맞춰야 한다.(이걸 수정하면 FontUtil에 있는 customBreakText 수치도 변경해야 한다.)
	 */
	private final float FIX_VALUE_TEXT_SIZE_SCALE = .87f; //스냅스 윤고딕 700 기준으로 좀 더 작다..

	private float lineSpacing = 9; //행간...

	private SnapsTextControl data = null;
	private TextView textView;
	String pageType;
	String controlType;
	iSnapsPageCanvasInterface _callback = null;
	boolean isDummyText = false;
	Align textAlign = Align.CENTER;
	boolean isFocused = false;
	boolean isThumbnail = false;
	Context context;

	public SnapsPhotoCardTextView(Context context) {
		super(context);
	}

	public SnapsPhotoCardTextView(String pageType, Context context, SnapsTextControl control, iSnapsPageCanvasInterface callback) {
		super(context);
		this._callback = callback;
		this.pageType = pageType;
		init(context, control);
	}
	public SnapsPhotoCardTextView(String pageType, String controlType, Context context, SnapsTextControl control, iSnapsPageCanvasInterface callback) {
		super(context);
		this._callback = callback;
		this.pageType = pageType;
		this.controlType = controlType;
		init(context, control);
	}
	public SnapsPhotoCardTextView(Context context, SnapsTextControl control) {
		super(context);
		init(context, control);
	}

	private void setDefaultLayoutParams(SnapsTextControl control) {
		if (control == null || control.width.isEmpty() || control.width.compareTo("") == 0 || control.height.compareTo("") == 0)
			return;

		int width = (int) Float.parseFloat(control.width);

		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, (int) (Float.parseFloat(control.height))); //텍스트가 살짝 잘려서 조금 길이를 늘림....

		params.setMargins((control.getX()), (int) Float.parseFloat(control.y), 0, 0);

		setLayoutParams(new FrameLayout.LayoutParams(params));
	}

	private void createCustomTextView() {
		textView = new SnapsPhotoCardTextView.CustomTextView(getContext());
		textView.setLayoutParams(new ViewGroup.LayoutParams(getLayoutParams().width, getLayoutParams().height));

		addView(textView);
	}

	private void setDefaultTextViewConfig(SnapsTextControl control) {
		if (control == null)
			return;
		float size = Float.parseFloat(control.format.fontSize) * FIX_VALUE_TEXT_SIZE_SCALE;// / context.getResources().getDisplayMetrics().density;
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (UIUtil.convertPixelsToSp(getContext(), size)));

		this.setGravity(Gravity.CENTER_VERTICAL);

		//현재 무조건 센터다.
		control.format.align = "center";

		try {
			Typeface fontFace = Const_VALUE.SNAPS_TYPEFACE_YG033;
			textView.setTypeface(fontFace);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		textView.setText(data.text);

		if (data.text.equals("")) {
			textView.setText(control.initialText);
		} else
			textView.setHint(control.initialText);

		textView.setIncludeFontPadding(false);

		setTextColor(control);
	}

	public void init(final Context context, SnapsTextControl control) {
		this.context = context;
		data = control;

		setDefaultLayoutParams(control);

		createCustomTextView();

		setDefaultTextViewConfig(control);

		// TEST
		if (data.priority.equals("test")) {
			setBackgroundColor(Color.argb(40, 255, 0, 0));
		}
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

		textView.setTextColor(Color.parseColor("#" + color));
	}

	/**
	 * @param text
	 */
	public void text(String text) {
		if(!isThumbnail()) {
			data.text = text;
		}
		this.textView.setText(text);

		if(!isThumbnail()) {
			// 데이터가 설정이 되었을때는 다시 lineText를 만들기 위해 초기화...
			data.textList.clear();
		}
	}

	/**
	 * 화면에 보여지지만 실제로 order에 기록하지 않기 위함.
	 */
	public boolean isDummyText() {
		return isDummyText;
	}

	public void setHintState(boolean isDummyText) {
		this.isDummyText = isDummyText;
	}

	public boolean isThumbnail() {
		return isThumbnail;
	}

	public void setThumbnail() {
		this.isThumbnail = true;
		lineSpacing = 0;
	}

	/**
	 * 
	 * com.snaps.kakao.activity.edit.view SnapsTextView.java
	 * 
	 * @author JaeMyung Park
	 * @Date : 2013. 6. 3.
	 * @Version :
	 */
	class CustomTextView extends TextView {
		private int mAvailableWidth = 0;
		private Paint mPaint;
		private List<String> mTextLineList = new ArrayList<String>();

		public CustomTextView(Context context) {
			super(context);
		}

		private int getNumberCharCount(String str) {
			if (str == null || str.length() < 1)
				return 0;

			final char[] nums = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ' '};

			int iNumCharCnt = 0;
			for (int ii = 0; ii < str.length(); ii++) {
				char c = str.charAt(ii);
				for (char n : nums) {
					if (c == n) {
						iNumCharCnt++;
						break;
					}
				}
			}

			return iNumCharCnt;
		}

		private int setTextInfo(String text, int tw, int th) {

			if(isDummyText) return 0;

			// 그릴 페인트 세팅
			mPaint = getPaint();
			mPaint.setColor(getTextColors().getDefaultColor());
			mPaint.setTextSize(getTextSize());

			int totalTextHeight = th;

			if (tw > 0) {
				int textWidth = tw;
				int textHeight = th;

				// 값 세팅
				mAvailableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

				mTextLineList.clear();
				int end = 0;
				String[] textArr = text.split("\n");

				for (int i = 0; i < textArr.length; i++) {
					if (textArr[i].length() == 0) {
						mTextLineList.add(textArr[i]);
						continue;
					}
					do {
						// 글자가 width 보다 넘어가는지 체크
						end = FontUtil.customBreakText(textArr[i], FontUtil.TEXT_TYPE_PHOTO_CARD_TEXT);
	
						if (end > 0) {
							String s = textArr[i].substring(0, end);
							mTextLineList.add(s);
							textArr[i] = textArr[i].substring(end);
							
							// 다음라인 높이 지정
							if (textHeight == 0)
								totalTextHeight += getLineHeight();
							if(i > 0) {
								totalTextHeight += lineSpacing;
							}
						}
						
					} while (end > 0);
				}
			}
			
			totalTextHeight += getPaddingTop() + getPaddingBottom();
			
			return totalTextHeight;
		}
		
		private void showOverTextMsg() {
			if(Config.IS_OVER_LENTH_CARD_MSG()) return;
			Config.setIS_OVER_LENTH_CARD_MSG(true);
			try {
				MessageUtil.alertnoTitleOneBtn((Activity) context, context.getString(R.string.maximumnumber_characters_exceeds), new ICustomDialogListener() {
					@Override
					public void onClick(byte clickedOk) {
						if(clickedOk == ICustomDialogListener.OK) {
							Config.setIS_OVER_LENTH_CARD_MSG(false);
						}
					}
				});
			} catch (BadTokenException e) {
				Dlog.e(TAG, e);
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if(isDummyText) return;
			
			if(mTextLineList != null) {
				int limitSize = measuredMaxTextSize(mTextLineList.size());
				
				float height = (getLineHeight()) + getCurViewCenterOffsetY();

				drawText(canvas, height, limitSize);
			}
		}
		
		private float getCurViewCenterOffsetY() {
			float halfHeight = getHeight()/2;
			float totalTextHeight = (getLineHeight() * mTextLineList.size()) + (lineSpacing*mTextLineList.size());
			float halfTotalTextHeight = totalTextHeight/2;

			float offsetHeight = halfHeight - halfTotalTextHeight;

			if (offsetHeight < 0.f)
				offsetHeight = 0.f;
			return offsetHeight;
		}
		
		private int measuredMaxTextSize(int size) {
			if (size >= MAX_PHOTO_CARD_TEXT_LINE_COUNT) {
				setDataString(MAX_PHOTO_CARD_TEXT_LINE_COUNT);
				return MAX_PHOTO_CARD_TEXT_LINE_COUNT;
			}

			return size;
		}
			
		private void drawText(Canvas canvas, float offsetY, int limit) {
			float height = offsetY;

			if(!isThumbnail()) {
				data.textList.clear();
			}
			final int FIX_VALUE_DIFF_RENDER_TEXT = 10;
			final int TEXT_CONTROL_Y = (int) (data.getIntY()/2) + FIX_VALUE_DIFF_RENDER_TEXT;

			for (int ii = 0; ii < limit; ii++) {

				if(mTextLineList.size() <= ii) break;

				String text = mTextLineList.get(ii);

					if(isDummyText) {
						;
					} else {
						// 캔버스에 라인 높이 만큰 글자 그리기
							mPaint.setTextAlign(Align.CENTER);
							canvas.drawText(text, mAvailableWidth/2, height, mPaint);
					}
					
					LineText lineText = new LineText();

					lineText.x = data.getX() + "";
					lineText.y = (int) (height + TEXT_CONTROL_Y) + ""; //FIXME Control의 Y축 만큼 이동시킴..
					lineText.height = (getLineHeight() + lineSpacing) + "";
					lineText.width = getWidth() + "";
					lineText.text = text;

					if(!isThumbnail()) {
						data.textList.add(lineText);
					}

				height += getLineHeight() + lineSpacing;
			}
		}

		void setDataString(int idx) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < idx; i++) {
				buffer.append(mTextLineList.get(i));
				if (i != idx)
					buffer.append("\n");
			}

			if(!isThumbnail() && idx < mTextLineList.size())
				showOverTextMsg();

			text(buffer.toString());
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
			int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
			int height = setTextInfo(this.getText().toString(), parentWidth, parentHeight);
			// 부모 높이가 0인경우 실제 그려줄 높이만큼 사이즈를 놀려줌...
			if (parentHeight == 0)
				parentHeight = height;
			
			this.setMeasuredDimension(parentWidth, parentHeight);
		}

		@Override
		protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
			// 글자가 변경되었을때 다시 세팅
			setTextInfo(text.toString(), this.getWidth(), this.getHeight());
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			// 사이즈가 변경되었을때 다시 세팅(가로 사이즈만...)
			if (w != oldw) {
				setTextInfo(this.getText().toString(), w, h);
			}
		}
	}

	public boolean isFocused() {
		return isFocused;
	}

	public void setFocused(boolean isFocused) {
		this.isFocused = isFocused;
	}
}
