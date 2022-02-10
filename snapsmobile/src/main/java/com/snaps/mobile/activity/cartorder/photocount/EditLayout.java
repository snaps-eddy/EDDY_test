package com.snaps.mobile.activity.cartorder.photocount;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.mobile.R;

public class EditLayout extends RelativeLayout {

	public TextView mItemMinus;
	public TextView mItemPlus;
	public EditText mEditText;
	int mCount = 1;

	public boolean mClickBtn = true;

	public boolean mTouch = false;

	Context context;
	View view;

	photoEntity entity;

	public void setEntity(photoEntity entity) {
		this.entity = entity;
	}

	OnEditLayoutTextChanged mListener = null;

	public EditLayout(Context context) {
		super(context);
	}

	public EditLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public String getEditText() {

		return mEditText.getText().toString();
	}

	private void init(Context context) {
		this.context = context;
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.edit_widjet, this, true);

		mItemMinus = (TextView) view.findViewById(R.id.item_minusbtn);
		mItemPlus = (TextView) view.findViewById(R.id.item_plusbtn);

		mEditText = (EditText) view.findViewById(R.id.item_edit);
		mEditText.setText(Integer.toString(mCount));

		mItemMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mClickBtn == true) {
					mCount = Integer.parseInt(mEditText.getText().toString());

					mCount--;
					if (mCount <= 0) {
						mCount = 1;
					}
					setEditText(Integer.toString(mCount));

					mTouch = true;
				} else {
					mItemMinus.setClickable(false);
				}
			}
		});

		mItemPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mClickBtn == true) {
					mCount = Integer.parseInt(mEditText.getText().toString());

					mCount++;
					// 99가 넘어가면 무조건 99
					if (mCount > 99)
						mCount = 99;
					setEditText(Integer.toString(mCount));

				} else {
					mClickBtn = true;
					setEditText(Integer.toString(1));
				}

			}
		});
	}

	public void photoitemEdit(boolean photoedit) {
	}

	public void setOnEditLayoutTextChanged(OnEditLayoutTextChanged listener) {
		this.mListener = listener;

		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				if (mListener != null)
					mListener.onTextChanged(s.toString());
				// entity.count = s.toString();

				if (s.toString().equals("")) {
					mClickBtn = false;
				}

			}
		});
	}

	public void setEditText(String str) {
		if(str != null)
			mEditText.setText(str);
	}

}
