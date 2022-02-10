package com.snaps.mobile.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;

public class SnapsCommonPopupView extends RelativeLayout {
	private static final String TAG = SnapsCommonPopupView.class.getSimpleName();

	public interface EventPopupStateListener {
		void onClosed();
	}

	static final public String TUTORIAL_IMG_EFFECT = "tutorial_img_effect";

	Context mContext;
	View popupView = null;

	EventPopupStateListener popupStateListener = null;

	public SnapsCommonPopupView(Context context, int resourceId, EventPopupStateListener lis) {
		super(context);

		init(context, resourceId);

		popupStateListener = lis;
	}

	void init(Context context, int resourceId) {

		if (resourceId == 0) {
			return;
		}

		try {
			this.mContext = context;
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			popupView = layoutInflater.inflate(R.layout.snaps_common_popup_view, this);
			popupView.setOnClickListener(mClickListener);

			ImageView ivMain = (ImageView) popupView.findViewById(R.id.snaps_common_popup_main_img);
			ivMain.setImageResource(resourceId);

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			close();
		}
	};

	private void close() {
		try {
			((ViewGroup) popupView.getParent()).removeView(popupView);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		if (popupStateListener != null) {
			popupStateListener.onClosed();
		}
	}

}
