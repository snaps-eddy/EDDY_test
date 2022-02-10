package com.snaps.mobile.utils.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskedTextWatcher implements TextWatcher {

	static final String TAG = "MaskedTextWatcher";

	// * : 영문,숫자
	// # : 숫자
	// sample : ****-*****-*****
	String _mask;
	String _result = "";
	EditText _editText;
	ArrayList<String> arMask = new ArrayList<String>();

	public MaskedTextWatcher(String mask, EditText editText) {
		_mask = mask;
		this._editText = editText;

		for (int i = 0; i < mask.length(); i++) {
			arMask.add(mask.substring(i, i + 1));
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		String ss = s.toString();
		if (ss.equals(_result) || ss.equals(""))
			return;

		boolean isDeleteHyphon = false;
		// (-)하이픈에 back key를 눌렀을때...
		if (ss.length() < _result.length()) {
			String[] v = ss.split("-");
			String[] rV = _result.split("-");
			// 하이픈이 지워졌는지 확인
			if (v.length != rV.length) {
				for (int i = 0; i < ss.length(); i++) {
					String maskChar = arMask.get(i);// _mask.substring(i, i + 1);
					if (maskChar.equals("-")) {
						String valueChar = ss.substring(i, i + 1);
						if (!maskChar.equals(valueChar)) {
							ss = ss.substring(0, start - 1) + ss.substring(start);
							isDeleteHyphon = true;
							break;
						}
					}
				}
			}
		}

		String a = removeSpecialChar(ss);
		// mask index
		int mIDX = 0;
		String result = "";
		int addCursor = 0;
		for (int i = 0; i < a.length(); i++) {
			String value = a.substring(i, i + 1);
			for (int j = mIDX; j < arMask.size(); j++) {
				String maskValue = arMask.get(j);// _mask.substring(j, j + 1);
				if (maskValue.equals("*") || maskValue.equals("#")) {
					result += value;
					mIDX++;
					break;
				} else {
					result += maskValue;
					mIDX++;
				}
			}
		}

		if (ss.length() < result.length())
			addCursor = 1;

		_result = result;
		_editText.setText(result);
		_editText.setSelection(result.length() < start + count + addCursor ? result.length() : (start + count + addCursor + (isDeleteHyphon ? -2 : 0)));

	}

	public String removeSpecialChar(String str) {
		String regText = "[^a-zA-Z0-9]";
		Pattern pattern = Pattern.compile(regText);
		Matcher matcher = pattern.matcher(str);
		return matcher.replaceAll("");
	}

}
