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

public class KakaoDialogFragment extends DialogFragment {

	ImpKakaoDialogListener listener = null;
	public boolean isDulicationDlg = false;

	public static KakaoDialogFragment newInstance() {
		KakaoDialogFragment frag = new KakaoDialogFragment();
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View v = null;

		if (isDulicationDlg)
			v = inflater.inflate(R.layout.kakao_event_duplication_dlg, container, false);
		else
			v = inflater.inflate(R.layout.kakao_event_complete_dlg, container, false);

		v.findViewById(R.id.btn_invite).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (listener != null)
					listener.OnClickListenr(10);

			}
		});

		v.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

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
