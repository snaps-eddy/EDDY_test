package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Shader.TileMode;
import android.view.View;

public class WoodFrameBorderView extends View {

	Rect rect = new Rect();
	Rect rect2 = new Rect();
	Paint paint = new Paint();

	int borderWidth = 0;
	int width = 0, height = 0;

	public WoodFrameBorderView(Context context) {
		super(context);
	}

	void init() {
		paint.reset();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setShader(new LinearGradient(0, 0, width / 2, height / 2, Color.parseColor("#d0d0d0"), Color.parseColor("#ffffff"), TileMode.MIRROR));

		rect = new Rect(0, 0, width, height);
		rect2 = new Rect(rect);
		rect2.inset(borderWidth, borderWidth);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.clipRect(rect);
		canvas.clipRect(rect2, Op.DIFFERENCE);
		canvas.drawRect(rect, paint);
		super.onDraw(canvas);
	}

	public void setBorderWidth(int pxWidth) {
		borderWidth = pxWidth;
		init();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		init();
	}

}
