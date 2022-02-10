package com.snaps.common.spc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.snaps.common.structure.control.SnapsControl;
import com.snaps.mobile.interfaces.ISnapsControl;

public class SnapsImageView extends androidx.appcompat.widget.AppCompatImageView implements ISnapsControl {
	SnapsControl snapsControl = null;
	public SnapsImageView(Context context) {
		super(context);
	}

	public SnapsImageView(Context context, AttributeSet attribute) {
		super(context, attribute);
	}

	@Override
	public SnapsControl getSnapsControl() {
		return snapsControl;
	}

	@Override
	public void setSnapsControl(SnapsControl layoutControl) {
		this.snapsControl = layoutControl;
	}

	@Override
	public View getView() {
		return this;
	}
}
