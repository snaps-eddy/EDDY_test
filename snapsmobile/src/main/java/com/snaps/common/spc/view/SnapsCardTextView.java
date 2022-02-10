package com.snaps.common.spc.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.imp.iSnapsPageCanvasInterface;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.CustomizeDialog;
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
public class SnapsCardTextView extends SnapsBaseCardTextView {
	private static final String TAG = SnapsCardTextView.class.getSimpleName();
	/**
	 * FIXME 단말기에서 보여지는 글씨 크기와 실제 랜더링 된 텍스트 크기가 다르므로 이 수치를 조절하여, 맞춰야 한다.(이걸 수정하면 FontUtil에 있는 customBreakText 수치도 변경해야 한다.)
	 */
	private final float FIX_VALUE_TEXT_SIZE_SCALE = .95f; //스냅스 윤고딕 700 기준으로 좀 더 작다..
//	private final float FIX_VALUE_TEXT_SIZE_SCALE = .99f; //스냅스 윤고딕 700 기준으로 좀 더 작다..
	private final int LINE_TEXT_HEIGHT = 11;
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
	boolean isVertical = false;
	Context context;

	public SnapsCardTextView(Context context) {
		super(context);
	}

	public SnapsCardTextView(String pageType, Context context, SnapsTextControl control, iSnapsPageCanvasInterface callback) {
		super(context);
		this._callback = callback;
		this.pageType = pageType;
		init(context, control);
	}
	public SnapsCardTextView(String pageType, String controlType, Context context, SnapsTextControl control, iSnapsPageCanvasInterface callback) {
		super(context);
		this._callback = callback;
		this.pageType = pageType;
		this.controlType = controlType;
		init(context, control);
	}
	public SnapsCardTextView(Context context, SnapsTextControl control) {
		super(context);
		init(context, control);
	}

	public void init(final Context context, SnapsTextControl control) {
		this.context = context;
		data = control;

		if (control == null || control.width.isEmpty() || control.width.compareTo("") == 0 || control.height.compareTo("") == 0)
			return;
		isVertical = (int) Float.parseFloat(control.width) > (int) (Float.parseFloat(control.height));
		int width = (int) Float.parseFloat(control.width);
		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, (int) (Float.parseFloat(control.height))); //텍스트가 살짝 잘려서 조금 길이를 늘림....

		params.setMargins((control.getX()), (int) Float.parseFloat(control.y), 0, 0);

		setLayoutParams(new FrameLayout.LayoutParams(params));

		this.setGravity(Gravity.CENTER_VERTICAL);

		float size = Float.parseFloat(control.format.fontSize) * FIX_VALUE_TEXT_SIZE_SCALE;// / context.getResources().getDisplayMetrics().density;
//		float size = Float.parseFloat("20") * FIX_VALUE_TEXT_SIZE_SCALE;// / context.getResources().getDisplayMetrics().density;

		textView = new CustomTextView(getContext());

		if (control.format.verticalView.equalsIgnoreCase("true")) {
			Dlog.d("init() control.format.verticalView.equalsIgnoreCase() true");

			// 책등은 회전에 의해 위치가 변경이 되므로 재조정...
			
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
		}

		Typeface fontFace = null;
		// KR버전만 소망체 유효함. 닷컴버전은 meiryo로 전부설정
		if (control.format.alterFontFace.equalsIgnoreCase("스냅스 소망2 M") && Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_KOR)) {
			try {

				fontFace = Const_VALUE.SNAPS_TYPEFACE_SOMANG;
				textView.setTypeface(fontFace);

			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		} else {
			try {
				fontFace = Const_VALUE.SNAPS_TYPEFACE_YG033;
				textView.setTypeface(fontFace);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

		textView.setLayoutParams(new ViewGroup.LayoutParams(getLayoutParams().width, getLayoutParams().height));

		if (control.format.verticalView.equalsIgnoreCase("true")) {
			textView.setGravity(Gravity.CENTER);
			if (!Config.isThemeBook() && !Config.isSimplePhotoBook() && !Config.isSimpleMakingBook() && !Config.isCalendar() && !Const_PRODUCT.isSNSBook(Config.getPROD_CODE())
					&& !Const_PRODUCT.isPackageProduct() && !Const_PRODUCT.isCardProduct() ) {
				textView.setTextColor(Color.TRANSPARENT);
			}
		}

		if (control.format.align.equalsIgnoreCase("center")) {
			textAlign = Align.CENTER;
		} else if (control.format.align.equalsIgnoreCase("right")) {
			textAlign = Align.RIGHT;
		} else if (control.format.align.equalsIgnoreCase("left")) {
			textAlign = Align.LEFT;
		}
		
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (UIUtil.convertPixelsToSp(getContext(), size)));
		
		setTextColor(control);

		textView.setText(data.text);

		if (data.text.equals("")) {
			textView.setText(control.initialText);
		} else
			textView.setHint(control.initialText);

		addView(textView);
		textView.setIncludeFontPadding(false);

//		// TEST
//		if (data.priority.equals("test")) {
			setBackgroundColor(Color.argb(40, 255, 0, 0));
//		}
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
						//end = mPaint.breakText(textArr[i], true, mAvailableWidth, null); //만약, 문제가 생긴다면 이걸로 적용!

						end = FontUtil.customBreakText(textArr[i],isVertical ? FontUtil.TEXT_TYPE_CARD_TEXT_VERTICAL : FontUtil.TEXT_TYPE_CARD_TEXT,getTextSize() > 15 ? 20 : 10);
	
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
//				float height = getPaddingTop() + getLineHeight() + getCurViewCenterOffsetY();
				float height = getPaddingTop() + getLineHeight()+ getCurViewCenterOffsetY();
				
				int limitSize = measuredMaxTextSize(height, mTextLineList.size());
				
//				height = getPaddingTop() + getLineHeight() + getCurViewCenterOffsetY();
				height = getPaddingTop() + (getLineHeight() / 2)+ getCurViewCenterOffsetY();
				
				drawText(canvas, height, limitSize);
			}
		}
		
		private float getCurViewCenterOffsetY() {
			float offsetHeight = 
					(
						getHeight() - 
						(
							(mTextLineList.size() - 1) * (getLineHeight() + lineSpacing) + getPaddingTop()
						)
					) / 2.f;
			if (offsetHeight < 0.f)
				offsetHeight = 0.f;
			return offsetHeight;
		}
		
		private int measuredMaxTextSize(float offsetY, int size) {
			float height = offsetY;
			
			for (int ii = 0; ii < size; ii++) {
				// 자신의 영역을 넘어가면 텍스트를 뿌리지 않는다.
				if (getHeight() - (getPaddingBottom() + getLineHeight()) < height) {
					setDataString(ii);				

					return ii;

				}
				
				height += getLineHeight();
				
				if(ii > 0)
					height += lineSpacing;
			}
			
			return size;
		}
			
		
		private void drawText(Canvas canvas, float offsetY, int limit) {
			float height = offsetY;

			if(!isThumbnail()) {
				data.textList.clear();
			}
			
			final int TEXT_CONTROL_Y = getLineHeight()/2;
			
			for (int ii = 0; ii < limit; ii++) {

				if(mTextLineList.size() <= ii) break;

				String text = mTextLineList.get(ii);

					if(isDummyText) {
					} else {
						// 캔버스에 라인 높이 만큰 글자 그리기
						if (textAlign == Align.LEFT) {
							mPaint.setTextAlign(Align.LEFT);
							canvas.drawText(text, getPaddingLeft(), height, mPaint);
						} else if(textAlign == Align.RIGHT) {
							mPaint.setTextAlign(Align.RIGHT);
							canvas.drawText(text, mAvailableWidth + getPaddingLeft(), height, mPaint);
						} else {
							mPaint.setTextAlign(Align.CENTER);
							canvas.drawText(text, mAvailableWidth/2, height, mPaint);  
						}
					}
					
					LineText lineText = new LineText();

					
					lineText.x = data.getX() + "";
					lineText.y = data.getIntY() + (int) (height + TEXT_CONTROL_Y) + ""; //FIXME Control의 Y축 만큼 이동시킴..
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

			if(idx < mTextLineList.size()) {
				if (!isThumbnail()) {
					showOverTextMsg();
				}
			}

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
