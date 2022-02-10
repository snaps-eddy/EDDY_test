package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class SnapsEditText extends EditText {
	public SnapsEditText(Context context) {
		super(context);

	}

	public SnapsEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public SnapsEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	@Override
	public boolean onTextContextMenuItem(int id) {
		switch (id) {
			case android.R.id.cut:
				onTextCut();
				break;
			case android.R.id.paste:
				onTextPaste();
				break;
			case android.R.id.copy:
				onTextCopy();
		}

		return super.onTextContextMenuItem(id);
	}

	/**
	 * Text was cut from this EditText.
	 */
	public void onTextCut() {
	}

	/**
	 * Text was copied from this EditText.
	 */
	public void onTextCopy() {
	}

	/**
	 * Text was pasted into the EditText.
	 */
	public void onTextPaste() {
	}
}
