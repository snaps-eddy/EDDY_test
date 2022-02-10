package com.snaps.common.spc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Shader.TileMode;
import android.graphics.RectF;
import android.widget.FrameLayout;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;

public class SnapsFrameLayout extends FrameLayout {
	private static final String TAG = SnapsFrameLayout.class.getSimpleName();

	private final Path path = new Path();
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Bitmap maskBitmap;

	boolean isBorderDraw = false;
	boolean isBackgroundDrow = false;

	public SnapsFrameLayout(Context context) {
		super(context);
	}

	/**
	 * 
	 * @param params
	 */
	public void setLayout(android.widget.RelativeLayout.LayoutParams params) {
		this.setLayoutParams(params);

		if (Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isMousePadProduct())
			path.addRoundRect(new RectF(0, 0, params.width, params.height), 10, 10, Direction.CW);

        if( Const_PRODUCT.isMetalFrame() || Const_PRODUCT.isMarvelFrame() )
            path.addRoundRect(new RectF(0, 0, params.width, params.height), 5, 5, Direction.CW);
	}

	public void setLayout(android.widget.FrameLayout.LayoutParams params) {
		this.setLayoutParams(params);

		if (Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isMousePadProduct())
			path.addRoundRect(new RectF(0, 0, params.width, params.height), 10, 10, Direction.CW);

        if( Const_PRODUCT.isMetalFrame() || Const_PRODUCT.isMarvelFrame() )
            path.addRoundRect(new RectF(0, 0, params.width, params.height), 5, 5, Direction.CW);
	}

	/**
	 * 
	 * 이미지 마스크 적용 비트맵.
	 * 
	 * @param bit
	 */
	public void setMaskBitmap(Bitmap bit) {
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		try {
			canvas.save();
			if ((!path.isEmpty() && maskBitmap != null && !maskBitmap.isRecycled()) || ((Const_PRODUCT.isSinglePageProduct() && !path.isEmpty()))) {
				// path 값으로 마스크 적용.
				Paint paint = mPaint;
				paint.setAntiAlias(true);

				canvas.clipPath(path);
				if (isBorderDraw)
					canvas.drawPath(path, paint);

				if (isBackgroundDrow) {
					canvas.drawPath(path, mBGPaint);
				}
			}

			super.dispatchDraw(canvas);
			canvas.restore();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public void addBorder(String sColor, String cColor, String eColor, int width) {
		isBorderDraw = true;
		mPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), new int[]{Color.parseColor(sColor), Color.parseColor(cColor), Color.parseColor(eColor)}, new float[]{0.f, 50.f, 100.f}, TileMode.MIRROR));
		mPaint.setStyle(Paint.Style.FILL);
		invalidate();
	}

	/**
	 * 뷰의 라운드를 한다.
	 */
	public void addRound(int width, int height) {
		path.addRoundRect(new RectF(0, 0, width, height), 15, 15, Direction.CW);
	}

	public void setBackgrounColor(String colorString) {
		mBGPaint.setColor(Color.parseColor(colorString));
		mBGPaint.setStyle(Style.FILL);
		isBackgroundDrow = true;
		invalidate();
	}
}
