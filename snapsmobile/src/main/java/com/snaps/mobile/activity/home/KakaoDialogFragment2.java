package com.snaps.mobile.activity.home;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.snaps.mobile.R;
import com.snaps.mobile.interfaces.ImpKakaoDialogListener;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class KakaoDialogFragment2 extends DialogFragment {

	ImpKakaoDialogListener listener = null;
	public boolean isDulicationDlg = false;

	public static KakaoDialogFragment2 newInstance() {
		KakaoDialogFragment2 frag = new KakaoDialogFragment2();
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View v = null;

		v = inflater.inflate(R.layout.kakao_event_complete_dlg2, container, false);

		v.findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (listener != null)
					listener.OnClickListenr(10);

			}
		});

		v.findViewById(R.id.btn_membership).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (listener != null)
					listener.OnClickListenr(20);

			}
		});

		return v;
	}
	public void setListener(ImpKakaoDialogListener listener) {
		this.listener = listener;
	}

}
