package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.snaps.common.structure.control.SnapsControl;
import com.snaps.mobile.interfaces.ISnapsControl;
import com.snaps.mobile.interfaces.ImpMaskImageViewListener;

public class MaskImageView extends AppCompatImageView implements ISnapsControl {

	private ImpMaskImageViewListener imgListener = null;
	protected SnapsControl mLayoutControl = null;

	public MaskImageView(Context context) {
		super(context);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);

		if (imgListener != null) {
			imgListener.completeLoadBitmap(bm);
		}
	}

	public void setImgListener(ImpMaskImageViewListener imgListener) {
		this.imgListener = imgListener;
	}

	@Override
	public SnapsControl getSnapsControl() {
		return mLayoutControl;
	}

	@Override
	public void setSnapsControl(SnapsControl layoutControl) {
		mLayoutControl = layoutControl;
	}

	@Override
	public ImageView getView() {
		return this;
	}

	@Override
	public void setClickable(boolean clickable) {
		super.setClickable(clickable);
	}
}
