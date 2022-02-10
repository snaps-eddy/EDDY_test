package com.snaps.mobile.utils.ui;

import com.snaps.common.structure.control.SnapsTextControl;

import java.util.ArrayList;

public class MultiLineTextData {

	ArrayList<String> lineTexts = null;   // 라인별 텍스트
	float textHeight = 0; // 라인 높이..

	public MultiLineTextData(float textHeight, ArrayList<String> lineTexts) {
		this.textHeight = textHeight;
		this.lineTexts = lineTexts;
	}

	public int getExtractTextByHeight(int startLine, int height, SnapsTextControl textControl) {
		int tHeight = 0;
		String sText = "";
		int currentLineIndex = -1;
		ArrayList<String> linetexts = new ArrayList<String>();
		for (int i = startLine; i < lineTexts.size(); i++) {
			// 라인이 추가 되었을때 높이보다 작으면 진행 넘으면 리턴을 한다.
			if (height <= (tHeight + textHeight))
				break;
			currentLineIndex = i;
			tHeight += textHeight;
			if (sText.length() > 0) {
				sText += "\n";
			}
			String s = lineTexts.get(i);
			sText += s;
			linetexts.add(s);
		}
		CalcViewRectUtil.makeLineText(textControl, linetexts, textHeight);
		textControl.height = (int) (linetexts.size() * textHeight + ((linetexts.size() - 1) * textControl.lineSpcing)) + "";
		textControl.text = sText;

		return currentLineIndex + 1;// 다음시작행을 반환..
	}
	public ArrayList<String> getLineTexts() {
		return lineTexts;
	}

	public String getLineString() {
		StringBuilder sb = new StringBuilder();
		if( lineTexts != null && lineTexts.size() > 0 ) {
			for( int i = 0; i < lineTexts.size(); ++i ) {
				if( sb.length() > 0 ) sb.append( "\n" );
				sb.append( lineTexts.get(i) );
			}
		}
		return sb.toString();
	}

	public void setLineTexts( ArrayList<String> texts ) { lineTexts = texts; }

	public float getTextHeight() {
		return textHeight;
	}

	public int getTextTotalHeight(SnapsTextControl textControl) {
		return (int) (textHeight * lineTexts.size() + ((lineTexts.size() - 1) * textControl.lineSpcing));
	}

	/***
	 * 카카오스토리북에서 텍스트 3개 라인의 높이를 구하는 함수
	 * @param textControl
	 * @return
	 */
	public int getText3LineHeight(SnapsTextControl textControl) {
		if(lineTexts.size()>3)
			return (int) (textHeight * 3 + (2 * textControl.lineSpcing));
		else
			return (int) (textHeight * lineTexts.size() + ((lineTexts.size() - 1) * textControl.lineSpcing));
	}


	public int getTextLineHeight(SnapsTextControl textControl, int size) {
		return (int) (textHeight * size + ((size - 1) * textControl.lineSpcing));
	}
}
