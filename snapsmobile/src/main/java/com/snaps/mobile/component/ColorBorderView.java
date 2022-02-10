package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.view.View;

import com.snaps.common.structure.control.SnapsLayoutControl;

public class ColorBorderView extends View {

	RectF rect = new RectF();
	RectF rect2 = new RectF();
	Paint paint = new Paint();

	float borderWidth = 0;
	int width = 0, height = 0;
	String colorString = "";

	float x = 0, y = 0, w = 0, h = 0;

	public ColorBorderView(Context context) {
		super(context);

	}

	void init() {
		paint.reset();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.parseColor(colorString));

		rect = new RectF(0, 0, (int) w, (int) h);
		rect2 = new RectF(rect);
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

	public void setBorderWidth(SnapsLayoutControl layout) {
		borderWidth = Float.parseFloat(layout.bordersinglethick);
		this.colorString = layout.bordersinglecolor;
		x = Float.parseFloat(layout.x);
		y = Float.parseFloat(layout.y);
		w = Float.parseFloat(layout.width);
		h = Float.parseFloat(layout.height);
		init();
	}

	public void setBorderWidth(SnapsLayoutControl layout, float _borderWidth, String borderColor) {
		borderWidth = _borderWidth;
		this.colorString = borderColor;
		x = Float.parseFloat(layout.x);
		y = Float.parseFloat(layout.y);
		w = Float.parseFloat(layout.width);
		h = Float.parseFloat(layout.height);
		init();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		init();
	}

}
