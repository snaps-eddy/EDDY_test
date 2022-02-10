package com.snaps.mobile.activity.edit.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.UIUtil;

public class SnapsClippingDimLayout extends AppCompatImageView {

	public static int DIMMED_LAYOUT_COLOR = Color.argb(80, 54, 54, 54);

	private Rect dimmedClipRect = null;
	private Rect subDimmedClipRect = null;
	private Paint dimmedClipPaint;

	private boolean isShowDimmedClip = false;

	public SnapsClippingDimLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SnapsClippingDimLayout(Context context) {
		this(context, null);
		init();
	}

	public SnapsClippingDimLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init();
	}

	private void init() {
		dimmedClipPaint = new Paint();
		dimmedClipPaint.setColor(DIMMED_LAYOUT_COLOR);
		dimmedClipPaint.setStyle(Paint.Style.FILL);
		dimmedClipPaint.setAntiAlias(true);

		setShowDimmedClip(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();

		if (isShowDimmedClip()) {
			if (dimmedClipRect != null) {
				canvas.drawRect(dimmedClipRect, dimmedClipPaint);
			}
			if (subDimmedClipRect != null) {
				canvas.drawRect(subDimmedClipRect, dimmedClipPaint);
			}
		}

		canvas.restore();
	}

	public boolean isShowDimmedClip() {
		return isShowDimmedClip;
	}

	private void setShowDimmedClip(boolean showDimmedClip) {
		isShowDimmedClip = showDimmedClip;
	}

	public void setDimmedAreaRect(Activity activity) {
		setShowDimmedClip(true);

		if (Config.isWQHDResolutionDevice() && OrientationManager.getInstance(activity).isLandScapeMode()) { //겔스8 해상도가 일반적이지 않아 뒤에 여백이 보여 꽁수로 메꿈..
			if (Config.isExistThumbnailEditView()) {
				int x = UIUtil.getScreenHeight(getContext()) + UIUtil.convertDPtoPX(getContext(), 27);
				subDimmedClipRect = new Rect(x, 0, x + UIUtil.convertDPtoPX(getContext(), 27), UIUtil.getScreenWidth(getContext()) - UIUtil.convertDPtoPX(getContext(), 48));

				x = UIUtil.convertDPtoPX(getContext(), 112);
				dimmedClipRect = new Rect(x, 0, x + UIUtil.convertDPtoPX(getContext(), 21), UIUtil.getScreenWidth(getContext()) - UIUtil.convertDPtoPX(getContext(), 48));
			} else {
				int x = UIUtil.getScreenHeight(getContext()) + UIUtil.convertDPtoPX(getContext(), 21);
				subDimmedClipRect = new Rect(x, 0, x + UIUtil.convertDPtoPX(getContext(), 21), UIUtil.getScreenWidth(getContext()) - UIUtil.convertDPtoPX(getContext(), 48));
			}
		}

		invalidate();
	}
}
