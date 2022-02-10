//package com.snaps.common.spc.view;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.snaps.common.imp.iSnapsPageCanvasInterface;
//import com.snaps.common.structure.control.LineText;
//import com.snaps.common.utils.constant.Config;
//import com.snaps.common.utils.constant.Const_VALUE;
//import com.snaps.common.utils.ui.UIUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// *
// * com.snaps.spc.view SnapsTextView.java
// *
// * @author JaeMyung Park
// * @Date : 2013. 6. 3.
// * @Version :
// */
//public class SnapsThemeTextView extends RelativeLayout {
//
//	private TextView textView;
//	String pageType;
//	iSnapsPageCanvasInterface _callback = null;
//	boolean isThumbnail = false;
//
//	public SnapsThemeTextView(Context context) {
//		super(context);
//	}
//
//	public SnapsThemeTextView(String pageType, Context context, SnapsTControl control, iSnapsPageCanvasInterface callback) {
//		super(context);
//		this._callback = callback;
//		this.pageType = pageType;
//		init(context, control);
//	}
//
//	public SnapsThemeTextView(Context context, SnapsTControl control) {
//		super(context);
//		init(context, control);
//	}
//
//	public void init(Context context, SnapsTControl control) {
//		data = control;
//
//		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		setLayoutParams(new FrameLayout.LayoutParams(params));
//
//		this.setGravity(Gravity.CENTER_VERTICAL);
//
//		// 연구대상..
//		float size = 11.f;
//
//		if (Config.PRODUCT_THEMEBOOK_A5.equalsIgnoreCase(Config.getPROD_CODE())) {
//		}
//
//		if (Config.PRODUCT_THEMEBOOK_A6.equalsIgnoreCase(Config.getPROD_CODE()))
//
//		textView = new CustomTextView(getContext());
//
//		// 폰트설정...
//		textView.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
//
//		textView.setLayoutParams(new ViewGroup.LayoutParams(getLayoutParams().width, LayoutParams.WRAP_CONTENT));
//
//		textView.setGravity(Gravity.CENTER_VERTICAL);
//
//		textView.setIncludeFontPadding(false);
//
//		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(getContext(), size));
//
//		float space = 15.6f;
//
//		textView.setLineSpacing(space, 0.f);
//
//		textView.setTextColor(Color.BLACK);
//		textView.setText(data.text);
//
//		addView(textView);
//
//	}
//
//	public TextView getTextView() {
//		return textView;
//	}
//
//	/**
//	 * @param text
//	 */
//	public void setText(String text) {
//		this.textView.setText(text);
//		if (!isThumbnail()) {
//			data.text = text;
//		}
//	}
//
//	/**
//	 *
//	 * com.snaps.kakao.activity.edit.view SnapsTextView.java
//	 *
//	 * @author JaeMyung Park
//	 * @Date : 2013. 6. 3.
//	 * @Version :
//	 */
//	class CustomTextView extends TextView {
//		private int mAvailableWidth = 0;
//		private Paint mPaint;
//		private List<String> mCutStr = new ArrayList<String>();
//
//		public CustomTextView(Context context) {
//			super(context);
//		}
//
//		private int getNumberCharCount(String str) {
//			if (str == null || str.length() < 1)
//				return 0;
//
//			final char[] nums = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ' '};
//
//			int iNumCharCnt = 0;
//			for (int ii = 0; ii < str.length(); ii++) {
//				char c = str.charAt(ii);
//				for (char n : nums) {
//					if (c == n) {
//						iNumCharCnt++;
//						break;
//					}
//				}
//			}
//
//			return iNumCharCnt;
//		}
//
//		private int adjustNumberCharWidth(String str, int measuredOneLineCharCnt) {
//			if (str == null || str.length() < 1 || str.length() < measuredOneLineCharCnt)
//				return measuredOneLineCharCnt;
//
//			str = str.substring(0, measuredOneLineCharCnt);
//
//			// 글자 1개의 width를 구한다.
//			float eachCharWidth = mAvailableWidth / (float) measuredOneLineCharCnt;
//			float eachNumCharBlank = eachCharWidth / 21.5f; // 테스트 결과, 숫자가 일반 문자보다 대략 1/21.5 정도 더 작다.
//			int numberCharCnt = getNumberCharCount(str);
//			int fixWidth = (int) (mAvailableWidth - (eachCharWidth * 2)); // 한글을 길게 쓰면 1~2자 정도 오버 되어서, 2자 정도의 여유를 둔다.
//			float availableWidth = (float) (fixWidth - (numberCharCnt * eachNumCharBlank));
//
//			return mPaint.breakText(str, true, availableWidth, null);
//		}
//
//		private int setTextInfo(String text, int tw, int th) {
//
//			// 그릴 페인트 세팅
//			mPaint = getPaint();
//			mPaint.setColor(getTextColors().getDefaultColor());
//			mPaint.setTextSize(getTextSize());
//
//			int mTextHeight = th;
//
//			if (tw > 0) {
//				int textWidth = tw;
//				int textHeight = th;
//
//				// 값 세팅
//				mAvailableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
//
//				mCutStr.clear();
//				int end = 0;
//				String[] textArr = text.split("\n");
//
//				for (int i = 0; i < textArr.length; i++) {
//					if (textArr[i].length() == 0) {
//						mCutStr.add(textArr[i]);
//						continue;
//					}
//					do {
//						// 글자가 width 보다 넘어가는지 체크
//						end = mPaint.breakText(textArr[i], true, mAvailableWidth, null);
//						// 숫자 또는 공백문자는 일반 문자보다 작기 때문에 예외 처리를 추가적으로 진행 함.
//						if(textArr[i].length() > 3) {
//							end = adjustNumberCharWidth(textArr[i], end);
//						}
//
//						if (end > 0) {
//							// 자른 문자열을 문자열 배열에 담아 놓는다.
//							String s = textArr[i].substring(0, end);
//							mCutStr.add(s);
//							// 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
//							textArr[i] = textArr[i].substring(end);
//							// 다음라인 높이 지정
//							if (textHeight == 0)
//								mTextHeight += getLineHeight();
//						}
//					} while (end > 0);
//				}
//			}
//			mTextHeight += getPaddingTop() + getPaddingBottom();
//			return mTextHeight;
//		}
//
//		@Override
//		protected void onDraw(Canvas canvas) {
//
//			int gravity = getGravity();
//			int lineHeight = getLineHeight();
//			if (isThumbnail())
//				lineHeight = (int) (getLineHeight() * .3f);
//
//			float offsetHeight = 0.f;
//			if (true || gravity == Gravity.CENTER_VERTICAL) {
//				offsetHeight = (getHeight() - (mCutStr.size() * lineHeight + getPaddingTop())) / 2.f;
//				if (offsetHeight < 0.f)
//					offsetHeight = 0.f;
//			} else {
//				offsetHeight = 0.f;
//			}
//
//			// 글자 높이 지정
//			float height = getPaddingTop() + lineHeight + offsetHeight;
//
//			for (String text : mCutStr) {
//
//				// 자신의 영역을 넘어가면 텍스트를 뿌리지 않는다.
//				if (getHeight() - getPaddingBottom() < height)
//					break;
//
//				// 캔버스에 라인 높이 만큰 글자 그리기
//				canvas.drawText(text, getPaddingLeft(), height, mPaint);
//					height += lineHeight;
//			}
//
//			if (!isThumbnail()) {
//				// xml 데이터 writing
//				data.textList.clear();
//
//				height = getPaddingTop() + getLineHeight() + offsetHeight;
//				int idx = 0;
//				for (String t : mCutStr) {
//					// 자신의 영역을 넘어가면 텍스트를 뿌리지 않는다.
//					if (getHeight() - getPaddingBottom() < height) {
//						setDataString(idx);
//						break;
//					}
//
//					LineText lineText = new LineText();
//
//					lineText.x = data.getX() + "";
//					lineText.y = (int) height + "";
//					lineText.height = getLineHeight() + "";
//					lineText.width = getUserSelectWidth() + "";
//					lineText.text = t;
//
//					data.textList.add(lineText);
//					height += getLineHeight();
//					idx++;
//				}
//			}
//		}
//
//		void setDataString(int idx) {
//			StringBuffer buffer = new StringBuffer();
//			for (int i = 0; i < idx; i++) {
//				buffer.append(mCutStr.get(i));
//				if (i != idx)
//					buffer.append("\n");
//			}
//
//			data.text = buffer.toString();
//		}
//
//		@Override
//		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//			int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
//			int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
//			int height = setTextInfo(this.getText().toString(), parentWidth, parentHeight);
//			// 부모 높이가 0인경우 실제 그려줄 높이만큼 사이즈를 놀려줌...
//			if (parentHeight == 0)
//				parentHeight = height;
//			this.setMeasuredDimension(parentWidth, parentHeight);
//		}
//
//		@Override
//		protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
//			// 글자가 변경되었을때 다시 세팅
//			setTextInfo(text.toString(), this.getUserSelectWidth(), this.getHeight());
//		}
//
//		@Override
//		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//			// 사이즈가 변경되었을때 다시 세팅(가로 사이즈만...)
//			if (w != oldw) {
//				setTextInfo(this.getText().toString(), w, h);
//			}
//		}
//	}
//
//	public boolean isThumbnail() {
//		return isThumbnail;
//	}
//
//	public void setThumbnail() {
//		this.isThumbnail = true;
//	}
//}
