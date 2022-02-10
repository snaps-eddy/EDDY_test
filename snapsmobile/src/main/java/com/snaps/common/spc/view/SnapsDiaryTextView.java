package com.snaps.common.spc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.imp.iSnapsPageCanvasInterface;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * com.snaps.spc.view SnapsTextView.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 6. 3.
 * @Version :
 */
public class SnapsDiaryTextView extends RelativeLayout {
	private static final String TAG = SnapsDiaryTextView.class.getSimpleName();
	public interface ISnapsDiaryTextControlListener {
		void onOverTextArea(String drawnText);
	}

	public static final int MAX_DIARY_PUBLISH_TEXT_LINE_COUNT = 12;

	/**
	 * FIXME 단말기에서 보여지는 글씨 크기와 실제 랜더링 된 텍스트 크기가 다르므로 이 수치를 조절하여, 맞춰야 한다.(이걸 수정하면 FontUtil에 있는 customBreakText 수치도 변경해야 한다.)
	 */
	private final float FIX_VALUE_TEXT_SIZE_SCALE = .95f; //스냅스 윤고딕 700 기준으로 좀 더 작다..
	private final float FIX_VALUE_TEXT_LINE_SPACING = 0; //행간...

	private ISnapsDiaryTextControlListener diaryTextControlListener = null;

	private SnapsTextControl data = null;
	private TextView textView;
	iSnapsPageCanvasInterface _callback = null;
	Align textAlign = Align.CENTER;
	Context context;

	public static final int DIARY_CONTENTS_WIDTH_OFFSET = 196;

	//노가다로 찍어 낸 결과물.
	private Map<Character, Float> mapCharDimens;
	{
		mapCharDimens = new HashMap<Character, Float>();
		mapCharDimens.put('가', 5.02f);
		mapCharDimens.put('A', 3.42f);
		mapCharDimens.put('B', 3.42f);
		mapCharDimens.put('C', 3.42f);
		mapCharDimens.put('D', 3.45f);
		mapCharDimens.put('E', 3.02f);
		mapCharDimens.put('F', 3.02f);
		mapCharDimens.put('G', 3.4f);
		mapCharDimens.put('H', 3.4f);
		mapCharDimens.put('I', 2f);
		mapCharDimens.put('J', 3.01f);
		mapCharDimens.put('K', 3.4f);
		mapCharDimens.put('L', 3f);
		mapCharDimens.put('M', 4.42f);
		mapCharDimens.put('N', 3.42f);
		mapCharDimens.put('O', 3.42f);
		mapCharDimens.put('P', 3.42f);
		mapCharDimens.put('Q', 3.42f);
		mapCharDimens.put('R', 3.42f);
		mapCharDimens.put('S', 3.42f);
		mapCharDimens.put('T', 3.42f);
		mapCharDimens.put('U', 3.42f);
		mapCharDimens.put('V', 3.42f);
		mapCharDimens.put('W', 4.42f);
		mapCharDimens.put('X', 3.42f);
		mapCharDimens.put('Y', 3.42f);
		mapCharDimens.put('Z', 3.42f);

		mapCharDimens.put('a', 3.1f);
		mapCharDimens.put('b', 3.1f);
		mapCharDimens.put('c', 3.1f);
		mapCharDimens.put('d', 3.1f);
		mapCharDimens.put('e', 3.1f);
		mapCharDimens.put('f', 2.2f);
		mapCharDimens.put('g', 3f);
		mapCharDimens.put('h', 3f);
		mapCharDimens.put('i', 1.3f);
		mapCharDimens.put('j', 1.3f);
		mapCharDimens.put('k', 3f);
		mapCharDimens.put('l', 1.3f);
		mapCharDimens.put('m', 4.8f);
		mapCharDimens.put('n', 3f);
		mapCharDimens.put('o', 3.1f);
		mapCharDimens.put('p', 3f);
		mapCharDimens.put('q', 3f);
		mapCharDimens.put('r', 2.3f);
		mapCharDimens.put('s', 3.1f);
		mapCharDimens.put('t', 2.3f);
		mapCharDimens.put('u', 3f);
		mapCharDimens.put('v', 3f);
		mapCharDimens.put('w', 4.8f);
		mapCharDimens.put('x', 3.1f);
		mapCharDimens.put('y', 3.1f);
		mapCharDimens.put('z', 3.1f);

		mapCharDimens.put('0', 3f);
		mapCharDimens.put('1', 3f);
		mapCharDimens.put('2', 3f);
		mapCharDimens.put('3', 3f);
		mapCharDimens.put('4', 3f);
		mapCharDimens.put('5', 3f);
		mapCharDimens.put('6', 3f);
		mapCharDimens.put('7', 3f);
		mapCharDimens.put('8', 3f);
		mapCharDimens.put('9', 3f);

		mapCharDimens.put(' ', 1.75f);
		mapCharDimens.put(',', 1.43f);
		mapCharDimens.put('.', 1.43f);
		mapCharDimens.put('\'', 1f);
		mapCharDimens.put('!', 2f);
		mapCharDimens.put('?', 3f);
		mapCharDimens.put('(', 3f);
		mapCharDimens.put(')', 3f);
		mapCharDimens.put('[', 3f);
		mapCharDimens.put(']', 3f);
	}

	public SnapsDiaryTextView(Context context) {
		super(context);
	}

	public SnapsDiaryTextView(Context context, SnapsTextControl control, iSnapsPageCanvasInterface callback) {
		super(context);
		this._callback = callback;
		init(context, control);
	}

	public SnapsDiaryTextView(Context context, SnapsTextControl control) {
		super(context);
		init(context, control);
	}

	public void setTextLoadedListener(ISnapsDiaryTextControlListener listener) {
		diaryTextControlListener = listener;
	}

	public void init(final Context context, SnapsTextControl control) {
		this.context = context;
		data = control;

		if (control == null || control.width.isEmpty() || control.width.compareTo("") == 0 || control.height.compareTo("") == 0)
			return;

		int width = (int) Float.parseFloat(control.width);

		MarginLayoutParams params = new MarginLayoutParams(width, (int) (Float.parseFloat(control.height))); //텍스트가 살짝 잘려서 조금 길이를 늘림....
		params.setMargins(0, 0, 0, 0); //좌상단에 위치 시키고 숨겨 놓는다...(꽁수임..)

		setLayoutParams(new FrameLayout.LayoutParams(params));

		float size = Float.parseFloat(control.format.fontSize) * FIX_VALUE_TEXT_SIZE_SCALE;

		textView = new CustomTextView(getContext());

		Typeface fontFace = null;
		try {
			fontFace = Const_VALUE.SNAPS_TYPEFACE_YG033;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		textView.setLayoutParams(new ViewGroup.LayoutParams(getLayoutParams().width, getLayoutParams().height));

		textAlign = Align.LEFT;

		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (UIUtil.convertPixelsToSp(getContext(), size)));

		textView.setText(data.text);

		addView(textView);
		textView.setIncludeFontPadding(false);
		// TEST
		if (SnapsDiaryConstants.DESIGN_TEST_FUNCTION) {
			setBackgroundColor(Color.argb(40, 255, 0, 0));
		}
	}

	public TextView getTextView() {
		return textView;
	}

	/**
	 * @param text
	 */
	public void text(String text) {
		data.text = text;
		this.textView.setText(text);

		// 데이터가 설정이 되었을때는 다시 lineText를 만들기 위해 초기화...
		data.textList.clear();
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
		private Paint mPaint;
		private List<String> mTextLineList = new ArrayList<String>();

		public CustomTextView(Context context) {
			super(context);
		}

		private int customDetailBreakText(String text) {
			if(text == null || text.length() < 1 ) return 0;

			float length = 0f;
			int result = 0;
			boolean bOverLength = false;

			for(int j = 0; j < text.length(); j++) {
				char c = text.charAt(j);
				byte charType = FontUtil.getCharType(c);
				if(charType == FontUtil.CHAR_TYPE_HANGUL)
					c = '가';

				if (mapCharDimens != null && mapCharDimens.containsKey(c)) {
					length += mapCharDimens.get(c);
				} else {
					length += FontUtil.getBreakTextSize(FontUtil.TEXT_TYPE_DIARY, charType);
				}

				if(length >= DIARY_CONTENTS_WIDTH_OFFSET) {
					bOverLength = true;
					result = j;
					break;
				}
			}

			return bOverLength ? result : text.length();
		}

		private int setTextInfo(String text, int tw, int th) {

			// 그릴 페인트 세팅
			mPaint = getPaint();
			mPaint.setColor(getTextColors().getDefaultColor());
			mPaint.setTextSize(getTextSize());

			int totalTextHeight = th;

			if (tw > 0) {
				int textWidth = tw;
				int textHeight = th;
				// 값 세팅
				mTextLineList.clear();
				int end = 0;

				if (text != null && text.endsWith("\n"))
					text += " ";

				String[] textArr = text.split("\n");

				final int layoutLineHeight = getLineHeight();
				for (int i = 0; i < textArr.length; i++) {
					if (textArr[i].length() == 0) {
						mTextLineList.add(textArr[i]);
						continue;
					}
					do {
						if(SnapsDiaryConstants.DESIGN_TEST_FUNCTION) {
							end = customDetailBreakText(textArr[i]);
						} else {
							// 글자가 width 보다 넘어가는지 체크
							end = customDetailBreakText(textArr[i]);
						}

						if (end > 0) {
							String s = textArr[i].substring(0, end);
							mTextLineList.add(s);
							textArr[i] = textArr[i].substring(end);

							// 다음라인 높이 지정
							if (textHeight == 0)
								totalTextHeight += layoutLineHeight;
							if(i > 0) {
								totalTextHeight += FIX_VALUE_TEXT_LINE_SPACING;
							}
						}

					} while (end > 0);
				}
			}

			totalTextHeight += getPaddingTop() + getPaddingBottom();

			return totalTextHeight;
		}

		private void postOverTextMsg(String filterText) {
			try {
				if(diaryTextControlListener != null){
					diaryTextControlListener.onOverTextArea(filterText);
				}
			} catch (BadTokenException e) {
				Dlog.e(TAG, e);
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if(mTextLineList != null) {
				float height = getLineHeight();

				int limitSize = measuredMaxTextSize(height, mTextLineList.size());

				drawText(canvas, height, limitSize);
			}
		}

		private int measuredMaxTextSize(float offsetY, int size) {
			float height = offsetY;

			final int layoutHeight = getHeight();
			final int layoutPaddingBottom = getPaddingBottom();
			final int layoutLineHeight = getLineHeight();

			for (int ii = 0; ii < size; ii++) {
				// 자신의 영역을 넘어가면 텍스트를 뿌리지 않는다.
				boolean isOverArea = layoutHeight - (layoutPaddingBottom + layoutLineHeight) < height;
				if (isOverArea || ii >= MAX_DIARY_PUBLISH_TEXT_LINE_COUNT) { //출판물을 기준으로 최대 12줄까지만 출력할 수 있다.
					setDataString(ii);

					return ii;

				}

				height += layoutLineHeight;

				if(ii > 0)
					height += FIX_VALUE_TEXT_LINE_SPACING;
			}

			return size;
		}


		private void drawText(Canvas canvas, float offsetY, int limit) {
			float height = offsetY;

			final int layoutPaddingLeft = getPaddingLeft();
			final int layoutWidth = getWidth();
			final int layoutLineHeight = getLineHeight();

			data.textList.clear();

			final int TEXT_CONTROL_Y = (data.getIntY()/2);

			for (int ii = 0; ii < limit; ii++) {

				if(mTextLineList.size() <= ii) break;

				String text = mTextLineList.get(ii);

				// 캔버스에 라인 높이 만큰 글자 그리기
				mPaint.setTextAlign(Align.LEFT);
				canvas.drawText(text, layoutPaddingLeft, height, mPaint);

				LineText lineText = new LineText();

				lineText.x = data.getX() + "";
				lineText.y = (int) (height + TEXT_CONTROL_Y) + "";
				lineText.height = (layoutLineHeight + FIX_VALUE_TEXT_LINE_SPACING) + "";
				lineText.width = layoutWidth + "";
				lineText.text = text;

				data.textList.add(lineText);

				height += layoutLineHeight + FIX_VALUE_TEXT_LINE_SPACING;
			}
		}

		void setDataString(int idx) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < idx; i++) {
				sb.append(mTextLineList.get(i));
				if (i != idx)
					sb.append("\n");
			}

			String result = sb.toString();
			if(idx < mTextLineList.size()) {
				postOverTextMsg(result);
			}

			text(result);
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
}
