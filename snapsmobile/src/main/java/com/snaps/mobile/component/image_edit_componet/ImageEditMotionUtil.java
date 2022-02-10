package com.snaps.mobile.component.image_edit_componet;

import android.view.MotionEvent;

/**
 * Created by ysjeong on 2017. 6. 7..
 */

public class ImageEditMotionUtil {

	/**
	 * Determine the space between the first two fingers
	 */
	public static float getPinchSpacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Calculate the degree to be rotated by.
	 *
	 * @param event
	 * @return Degrees
	 */
	public static float getRotationDegree(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}
}
