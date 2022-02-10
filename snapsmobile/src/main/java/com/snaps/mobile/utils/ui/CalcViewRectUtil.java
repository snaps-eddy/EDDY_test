package com.snaps.mobile.utils.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.UIUtil;

import java.util.ArrayList;

public class CalcViewRectUtil {
	private static final String TAG = CalcViewRectUtil.class.getSimpleName();
	static public Rect getLayoutControlRect(String limit_Width, String limit_Height, String imgWidth, String imgHeight) {
		return getLayoutControlRect(Float.parseFloat(limit_Width), Float.parseFloat(limit_Height), Float.parseFloat(imgWidth), Float.parseFloat(imgHeight));
	}
	/***
	 * 이미지 레이아웃의 영역을 구하는 함수..
	 * 
	 * @param limit_Width
	 * @param limit_Height
	 * @param imgWidth
	 * @param imgHeight
	 * @return
	 */
	static public Rect getLayoutControlRect(float limit_Width, float limit_Height, float imgWidth, float imgHeight) {
		// 이미지를 width에 맞게 맞춘다.
		float width = 0;
		float height = limit_Width * imgHeight / imgWidth;

		// 만약에 이미지가 기준 height가 넘어가면 height 기준으로 마춘다.
		// 재계산...
		if (height > limit_Height) {
			width = limit_Height * imgWidth / imgHeight;
			height = limit_Height;
		} else {
			width = limit_Width;
		}

		Rect rect = new Rect(0, 0, (int) width, (int) height);
		return rect;
	}

	/***
	 * 텍스트 레이아웃의 영역을 구하는 함수..
	 * 
	 * @param con
	 * @param font
	 * @param fontSize
	 * @param text
	 * @return
	 */
	static public Rect getTextControlRect(Context con, String font, String fontSize, int limitWidth, String text, float scale, SnapsTextControl textControl, int textType) {

		TextView textView = new TextView(con);

		textView.setTypeface(FontUtil.getFontTypeface(con, font));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(con, Float.parseFloat(fontSize) * scale));

		Rect bounds = new Rect();
		Paint p = textView.getPaint();
		p.getTextBounds(text, 0, text.length(), bounds);

		FontMetrics fm = p.getFontMetrics();
		ArrayList<String> texts = new ArrayList<String>();

		// 텍스트 라인 높이를 구한다.
		int height = 4;// + fm.ascent - fm.descent);
		if (limitWidth > 0) {
			texts.clear();
			int end = 0;

			String[] textArr = text.split("\n");

			for (int i = 0; i < textArr.length; i++) {
				if (textArr[i].length() == 0) {
					texts.add(textArr[i]);
					continue;
				}
				do {
					// 글자가 width 보다 넘어가는지 체크
					end = FontUtil.customBreakText(textArr[i], textType);

					if (end > 0) {
						String s = textArr[i].substring(0, end);
						texts.add(s);
						textArr[i] = textArr[i].substring(end);

					}
				} while (end > 0);
			}
		}

		makeLineText(textControl, texts, height);
		int lines = texts.size();
		height = (int) (height * lines + ((lines - 1) * textControl.lineSpcing));
		Rect result = new Rect(0, 0, bounds.width(), height);
		textControl.height = result.height() + "";

		String reCreateString = "";
		for (String s : texts) {
			if (reCreateString.length() > 0)
				reCreateString += "\n";
			reCreateString += s;
		}
		textControl.text = reCreateString;

		return result;
	}

	static public void makeLineText(SnapsTextControl textControl, ArrayList<String> texts, float height) {
		for (String s : texts) {
			LineText line = new LineText();
			line.width = textControl.width;
			line.height = height + "";
			line.text = s;

			textControl.textList.add(line);
		}
	}
	
	public static String[] splitTextByArea( Context context, SnapsTextControl control, float height ) {
		if( fitInside(context, control, control.text, height) ) return new String[]{ control.text, "" }; // 한방에 들어가면 그대로 토함.
		
		Rect r = CalcViewRectUtil.getTextControlRect2( context, control.text, control.format.fontSize, control.getIntWidth(), control.format.fontFace, 1f);
		
		int textLength = control.text.length();
		int point = (int)( (float)textLength / (float)r.height() * height ); // 영역과 전체 예상 사이즈의 비율로 대략적인 구분 위치를 구함.
		boolean fitAtPoint = fitInside( context, control, control.text.substring(0, point), height ); // 그 위치에서 텍스트가 들어가는지 확인, 저장.
		boolean currentFlag;
		int index = fitAtPoint ? point + 1 : point - 1; // 텍스트가 들어갔으면 인덱스를 크게, 아니면 작게.
		int lastIndex;
		while( index > -1 && index < textLength ) { // 텍스트가 들어갔으면 안들어갈때까지 돌리고, 안들어갔으면 들어갈때까지 반복.
			currentFlag = fitInside( context, control, control.text.substring(0, index), height );
			if( currentFlag != fitAtPoint ) {
				lastIndex = fitAtPoint ? point : index;
				return new String[]{ control.text.substring(0, lastIndex), control.text.substring(lastIndex, textLength) };
			}
			index = fitAtPoint ? index + 1 : index - 1;
		}
		return null;    // 여기까지 오면 뭔가 오류가 있는거.
	}
	
	public static boolean fitInside( Context context, SnapsTextControl control, String text, float height ) {
		Rect r = CalcViewRectUtil.getTextControlRect2( context, text, control.format.fontSize, control.getIntWidth(), control.format.fontFace, 1f);
		return r.height() < height + 1;
	}
	
	public static Rect getTextControlRect2(Context context, CharSequence text, String fontSize, int limitWidth, String font, float scale) {
		TextView textView = new TextView(context);
		textView.setPadding(0, 0, 0, 0);
		textView.setTypeface(FontUtil.getFontTypeface(context, font));
		textView.setText(text, TextView.BufferType.SPANNABLE);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(context, Float.parseFloat(fontSize) * scale));

		int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(limitWidth, View.MeasureSpec.AT_MOST);
		int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		textView.measure(widthMeasureSpec, heightMeasureSpec);
		textView.getMeasuredWidth();
		Rect result = new Rect(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
		return result;
	}

    public static Rect getTextControlRect3(Context context, CharSequence text, String fontSize, int limitWidth, String font, float scale) {
        TextView textView = new TextView(context);
        textView.setPadding(0, 0, 0, 0);
        textView.setTypeface(FontUtil.getFontTypeface(context, font));
        textView.setText(text, TextView.BufferType.SPANNABLE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (Float.parseFloat(fontSize) * scale));

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(limitWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        textView.getMeasuredWidth();
        Rect result = new Rect(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        return result;
    }

	/***
	 * 텍스트 레이아웃의 영역을 구하는 함수..(카카오 텍스트인 경우만 사용합...)
	 * 
	 * @param con
	 * @param font
	 * @param fontSize
	 * @param text
	 * @return
	 */
	static public MultiLineTextData getTextControlRect(Context con, String font, String fontSize, int limitWidth, String text, float scale, int textType) {
		return getTextControlRect( con, font, fontSize, limitWidth, text, scale, textType, 6 );
	}
	static public MultiLineTextData getTextControlRect(Context con, String font, String fontSize, int limitWidth, String text, float scale, int textType, float lineHeight ) {
		TextView textView = new TextView(con);
		if( text == null ) text = ""; // 방어코드.

		// textView.setLineSpacing(0, 1.5f);
		Typeface typeFace = FontUtil.getFontTypeface(con, font);
		textView.setTypeface(typeFace);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(con, Float.parseFloat(fontSize) * scale));
		Dlog.d("getTextControlRect() fontSize = " + textView.getTextSize());

		Rect bounds = new Rect();
		Paint p = textView.getPaint();
		p.setSubpixelText(true);
		p.getTextBounds(text, 0, text.length(), bounds);

		ArrayList<String> texts = new ArrayList<String>();

		// 텍스트 라인 높이를 구한다.
		float height = lineHeight;
		
		if (limitWidth > 0) {
			texts.clear();
			int end = 0;

			String[] textArr = text.split("\n");

			for (int i = 0; i < textArr.length; i++) {
				if (textArr[i].length() == 0) {
					texts.add(textArr[i]);
					continue;
				}
				do {
					// 글자가 width 보다 넘어가는지 체크
					if( textType / 100 == 2 ) end = FontUtil.customBreakText(textArr[i], textType);
					else end = FontUtil.customBreakText(textArr[i], textType);

					if (end > 0) {
						String s = textArr[i].substring(0, end);
						texts.add(s);
						textArr[i] = textArr[i].substring(end).trim();
					}
				} while (end > 0);
			}
		}

		MultiLineTextData textData = new MultiLineTextData(height, texts);

		return textData;
	}

	static public String getEllipticalSingLineString(Context con, String font, String fontSize, String text, float scale, int textType ) {
		TextView textView = new TextView(con);
		if( text == null ) text = ""; // 방어코드.

		Typeface typeFace = FontUtil.getFontTypeface(con, font);
		textView.setTypeface(typeFace);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, UIUtil.convertPixelsToSp(con, Float.parseFloat(fontSize) * scale));

		Rect bounds = new Rect();
		Paint p = textView.getPaint();
		p.setSubpixelText(true);
		p.getTextBounds(text, 0, text.length(), bounds);

		String result = text;
		int end = 0;
		if (textType / 100 == 2) end = FontUtil.customBreakText(text, textType);
		else end = FontUtil.customBreakText(text, textType);

		result = text.substring(0, end);
		final String ellipsizeStr = "...";
		String temp;

		do {
			temp = result + ellipsizeStr;
			temp = temp.substring( 0, FontUtil.customBreakText(temp, textType) );
			if (temp.equals(result + ellipsizeStr)) break;
			else {
				if (result.length() > 0) result = result.substring(0, result.length() - 1);
				else break;
			}
		} while (!temp.equals(result + ellipsizeStr));

		if( text.length() < result.length() + 1 ) return result;
		return result.length() > 0 && " ".equalsIgnoreCase( result.substring(result.length() -1, result.length()) ) ? result.substring(0, result.length() - 1) + ellipsizeStr : result + ellipsizeStr ;
	}
}
