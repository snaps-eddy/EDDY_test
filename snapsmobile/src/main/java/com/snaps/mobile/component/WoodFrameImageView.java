package com.snaps.mobile.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

public class WoodFrameImageView extends ImageView {

	// view전체크기
	int type = 0;// left,right,top,bottom

	Path clipPathLeft, clipPathRight;
	Rect bodyRect;

	public WoodFrameImageView(Context context) {
		super(context);
	}

	public WoodFrameImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public WoodFrameImageView(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		init(context, attrs);
	}

	void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WoodFrameImageView);
		if (a != null) {
			type = a.getInt(R.styleable.WoodFrameImageView_frameType, 0);
			// 쓴후에는 없앤다.. ㅋ
			a.recycle();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 클립영역을 설정한다.
		canvas.clipRect(bodyRect);
		UIUtil.clipPathSupportICS(this, canvas, clipPathLeft, Op.DIFFERENCE);
		UIUtil.clipPathSupportICS(this, canvas, clipPathLeft, Op.DIFFERENCE);
		super.onDraw(canvas);
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

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		bodyRect = new Rect(0, 0, w, h);

		clipPathLeft = new Path();
		clipPathRight = new Path();

		if (type == 0) {// top
			clipPathLeft.moveTo(0, 0);
			clipPathLeft.lineTo(0, h);
			clipPathLeft.lineTo(h, h);
			clipPathLeft.lineTo(0, 0);
			clipPathLeft.close();
			clipPathRight.moveTo(w, 0);
			clipPathRight.lineTo(w, h);
			clipPathRight.lineTo(w - h, h);
			clipPathRight.lineTo(w, 0);
			clipPathRight.close();
		} else if (type == 1) {// bottom
			clipPathLeft.moveTo(0, 0);
			clipPathLeft.lineTo(0, h);
			clipPathLeft.lineTo(h, 0);
			clipPathLeft.lineTo(0, 0);
			clipPathLeft.close();
			clipPathRight.moveTo(w, 0);
			clipPathRight.lineTo(w, h);
			clipPathRight.lineTo(w - h, 0);
			clipPathRight.lineTo(w, 0);
			clipPathRight.close();
		} else if (type == 2) {// left
			clipPathLeft.moveTo(0, 0);
			clipPathLeft.lineTo(w, 0);
			clipPathLeft.lineTo(w, w);
			clipPathLeft.lineTo(0, 0);
			clipPathLeft.close();
			clipPathRight.moveTo(0, h);
			clipPathRight.lineTo(w, h - w);
			clipPathRight.lineTo(w, h);
			clipPathRight.lineTo(0, h);
			clipPathRight.close();
		} else if (type == 3) {// right
			clipPathLeft.moveTo(0, 0);
			clipPathLeft.lineTo(w, 0);
			clipPathLeft.lineTo(0, w);
			clipPathLeft.lineTo(0, 0);
			clipPathLeft.close();

			clipPathRight.moveTo(0, h - w);
			clipPathRight.lineTo(0, h);
			clipPathRight.lineTo(w, h);
			clipPathRight.lineTo(0, h - w);
			clipPathRight.close();
		}
	}
}
