package com.snaps.mobile.utils.ui;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class RotateUtil {

	/***
	 * 좌상단 회전 좌표를 중심 회전 좌표로 변화해주는 함수..
	 * 
	 * @param r
	 * @param angle
	 * @return
	 */
	static public Rect convertCenterRotateRect(Rect r, float angle) {

		Point a = new Point();// 이동한 점.
		Point b = new Point();// 이동된 점 (각도)
		Point s = new Point();// 회전 축
		s.x = r.left;
		s.y = r.top;

		a.x = r.left + r.width() / 2;
		a.y = r.top + r.height() / 2;

		float angles = angle;

		b.x = (int) ((a.x - s.x) * Math.cos(Math.toRadians(angles)) - (a.y - s.y) * Math.sin(Math.toRadians(angles)) + s.x);
		b.y = (int) ((a.x - s.x) * Math.sin(Math.toRadians(angles)) + (a.y - s.y) * Math.cos(Math.toRadians(angles)) + s.y);

		int dx = b.x - a.x;
		int dy = b.y - a.y;

		Rect r2 = new Rect(r);

		r2.offset(dx, dy);

		return r2;
	}

	static public RectF convertCenterRotateRect2(RectF r, float angle, PointF p) {

		// P' P(알고있는 위치) S(회전점)
		PointF P = new PointF(); // 이동되기 전 점..
		PointF P_ = new PointF(); // 이동된 위치 점..
		PointF s = new PointF();// 회전 중심점

		s = p; // 회전축...
		P.x = r.left + r.width() / 2;
		P.y = r.top + r.height() / 2;


		float angles = angle;

		P_.x = (float) ((P.x - s.x) * Math.cos(Math.toRadians(angles)) - (P.y - s.y) * Math.sin(Math.toRadians(angles)) + s.x);
		P_.y = (float) ((P.x - s.x) * Math.sin(Math.toRadians(angles)) + (P.y - s.y) * Math.cos(Math.toRadians(angles)) + s.y);

		float dx = P_.x - P.x;
		float dy = P_.y - P.y;

		RectF r2 = new RectF(r);

		r2.offset(dx, dy);

		return r2;
	}

}
