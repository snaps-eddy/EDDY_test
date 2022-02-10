package com.snaps.mobile.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.interfaces.ImpOnConfirmDel;

public class DelConfirmDialog extends Dialog {

	String mMessage;

	int mPositon;

	ImpOnConfirmDel mListener = null;

	public DelConfirmDialog(Context context, String message, int position) {
		super(context);
		mMessage = message;
		this.mPositon = position;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_cartdel_fragment_);

		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		TextView message = (TextView) findViewById(R.id.txtDiagTitle);
		message.setText(mMessage);
		TextView confirm = (TextView) findViewById(R.id.btnDelOk);
		TextView cancel = (TextView) findViewById(R.id.btnDelCancel);

		confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null)
					mListener.clickOnConfirm(mPositon);
				dismiss();

			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null)
					mListener.clickOnCancel(mPositon);
				dismiss();

			}
		});

	}

	public void setmListener(ImpOnConfirmDel mListener) {
		this.mListener = mListener;
	}

}
