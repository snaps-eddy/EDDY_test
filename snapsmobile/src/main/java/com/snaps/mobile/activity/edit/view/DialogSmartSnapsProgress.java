package com.snaps.mobile.activity.edit.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.mobile.R;


public class DialogSmartSnapsProgress extends Dialog {

	public DialogSmartSnapsProgress(Context context) {
//		super(context, android.R.style.Theme_Translucent_NoTitleBar );
		super(context, OrientationManager.getInstance(context).isLandScapeMode() ? android.R.style.Theme_Translucent_NoTitleBar_Fullscreen : android.R.style.Theme_Translucent_NoTitleBar);
		setContentView(R.layout.dialog_smart_snaps_progress);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setCancelable(false);
	}

	public void setFirstLoadMode() {
		View firstLoadLayout = findViewById(R.id.dialog_smart_snaps_progress_first_load_ly);
		if (firstLoadLayout != null) {
			firstLoadLayout.setVisibility(View.VISIBLE);
		}

		View normalLoadLayout = findViewById(R.id.dialog_smart_snaps_progress_normal_load_ly);
		if (normalLoadLayout != null) {
			normalLoadLayout.setVisibility(View.GONE);
		}
	}

	public void setNormalLoadMode() {
		View firstLoadLayout = findViewById(R.id.dialog_smart_snaps_progress_first_load_ly);
		if (firstLoadLayout != null) {
			firstLoadLayout.setVisibility(View.GONE);
		}

		View normalLoadLayout = findViewById(R.id.dialog_smart_snaps_progress_normal_load_ly);
		if (normalLoadLayout != null) {
			normalLoadLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		// Back Key 방지.
	}
}