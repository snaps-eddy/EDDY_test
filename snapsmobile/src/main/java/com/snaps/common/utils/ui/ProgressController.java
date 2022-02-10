package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.snaps.mobile.R;

public class ProgressController {
	private Activity parent;
	private ProgressBar progressBar;
	
	public ProgressController(Activity activity ) {
		this.parent = activity;
	}

	public ProgressBar initialize() {
		Resources res = parent.getResources();

		progressBar = new ProgressBar(parent, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setBackgroundColor(res.getColor(android.R.color.transparent));
		progressBar.setMax(100);
		progressBar.setProgressDrawable(res.getDrawable(R.drawable.progressbar_webview));
		RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, res.getDisplayMetrics()) );
		rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		progressBar.setLayoutParams(rParams);
		progressBar.setVisibility(View.GONE);

		((RelativeLayout) parent.findViewById(R.id.root_layout)).addView(progressBar);
		return progressBar;
	}
}
