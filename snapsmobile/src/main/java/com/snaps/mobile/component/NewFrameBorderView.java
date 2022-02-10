package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.view.View;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;

import java.io.File;

//액자 테두리 border 뷰 
public class NewFrameBorderView extends View {

	Bitmap frameBitmap = null;

	int frameBorderPxWidth = 5;
	Path clipPath = new Path();
	Paint paint = new Paint();
	Rect srcRect = new Rect();
	Rect dstRect = new Rect();

	public void setFrameBitmap(Bitmap frameBitmap) {
		if (frameBitmap != null) {
			this.frameBitmap = frameBitmap;
			srcRect = new Rect(0, 0, frameBitmap.getWidth(), frameBitmap.getHeight());
		}
	}

	public void setFrameBorderPxWidth(int frameBorderPxWidth) {
		if (frameBitmap != null) {
			this.frameBorderPxWidth = frameBorderPxWidth;
			dstRect = new Rect(0, 0, frameBitmap.getWidth(), frameBorderPxWidth);
		}
	}

	public NewFrameBorderView(Context context, String url, String resID, int borderWidth) {
		super(context);
		setLayerType(LAYER_TYPE_SOFTWARE, null);
		paint.setAntiAlias(true);
		init(context, url, resID, borderWidth);
	}

	int angle = 0;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (frameBitmap != null) {

			for (int i = 0; i < 4; i++) {
				canvas.save();
				angle = 90 * i;
				canvas.rotate(angle, getWidth() / 2, getHeight() / 2);

				if (angle == 90 || angle == 270) {
					float dy = (getWidth() - getHeight()) / 2.f;
					canvas.translate(dy, -dy);
					setClipPath(canvas, 0, getHeight(), getWidth(), frameBorderPxWidth);
				} else
					setClipPath(canvas, 0, getWidth(), getHeight(), frameBorderPxWidth);
				canvas.drawBitmap(frameBitmap, srcRect, dstRect, paint);
				canvas.restore();
			}
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		// Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			// Must be this size
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			width = Math.min(0, widthSize);
		} else {
			// Be whatever you want
			width = 0;
		}
		// Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			// Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			height = Math.min(0, heightSize);
		} else {
			// Be whatever you want
			height = 0;
		}

		setMeasuredDimension(width, height);
	}

	void setClipPath(Canvas canvas, int type, int w, int h, int borderWidth) {
		Rect clipRect = new Rect(0, 0, w, borderWidth);

		clipPath.reset();
		clipPath.moveTo(0, 0);
		clipPath.lineTo(0, borderWidth);
		clipPath.lineTo(borderWidth, borderWidth);
		clipPath.lineTo(0, 0);
		clipPath.close();

		clipPath.moveTo(w, 0);
		clipPath.lineTo(w, borderWidth);
		clipPath.lineTo(w - borderWidth, borderWidth);
		clipPath.lineTo(w, 0);
		clipPath.close();

		canvas.clipRect(clipRect);
		canvas.clipPath(clipPath, Op.DIFFERENCE);
	}

	Bitmap getFrameBitmap(Context context, String url, String resID) {
		// 저장될 Path
		String savePath = Config.getExternalCacheDir(context) + "/frame_img/" + resID + ".dat";
		File frameImgFile = new File(savePath);

		if ((frameImgFile != null && frameImgFile.exists()) || HttpUtil.saveUrlToFile(url, savePath)) {
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = 4;

			return BitmapFactory.decodeFile(savePath, option);

		}
		return null;

	}

	void init(final Context context, final String url, final String resID, final int borderWidth) {
		ATask.executeVoid(new OnTask() {

			@Override
			public void onPre() {

			}

			@Override
			public void onPost() {
				invalidate();

			}

			@Override
			public void onBG() {
				setFrameBitmap(getFrameBitmap(context, url, resID));
				setFrameBorderPxWidth(borderWidth);

			}
		});
	}

}
