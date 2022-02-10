package com.snaps.mobile.activity.event;

import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

import errorhandle.logger.Logg;
import com.snaps.mobile.component.SnapsEventView;
import com.snaps.mobile.interfaces.ImpSnapsEvent;

import errorhandle.CatchActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class LoginEventActivtiy extends CatchActivity implements ImpSnapsEvent {

	SnapsEventView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		view = new SnapsEventView(this);
		view.setEventListener(this);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

		setContentView(view, layoutParams);
	}

	@Override
	public void onEventClose() {
		// 액티비티를 닫고 결과코드를 999로 리턴한다.
		setResult(999);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(view != null)
			view.removeEventView();
	}
}
