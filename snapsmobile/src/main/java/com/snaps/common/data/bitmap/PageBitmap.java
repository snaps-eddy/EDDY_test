package com.snaps.common.data.bitmap;

import android.graphics.Bitmap;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;



public class PageBitmap {
	private static final String TAG = PageBitmap.class.getSimpleName();
	public static final int ORIENT_LEFT = 0;
	public static final int ORIENT_RIGHT = 1;
	
	public Bitmap all;
	public Bitmap left;
	public Bitmap right;

	public PageBitmap() {}
	public PageBitmap(Bitmap left, Bitmap right) {
		if (left == null && right != null)
			all = BitmapUtil.drawHalf(right, ORIENT_RIGHT);
		else if (right == null && left != null)
			all = BitmapUtil.drawHalf(left, ORIENT_LEFT);
		else {
			this.left = left;
			this.right = right;
		}
	}
	public PageBitmap(Bitmap left, Bitmap right, boolean isTwoPage) {
		if (isTwoPage)
			all = BitmapUtil.drawAll(left, right);
		else {
			this.left = left;
			this.right = right;
		}
	}
	public PageBitmap(Bitmap left, Bitmap right, int orient) {
		all = BitmapUtil.drawAll(left, right, orient);
	}
	
	public Bitmap getBitmap(int orient) {
		return orient == ORIENT_LEFT ? left : right;
	}
	public void merge() {
		if (left != null && right != null)
			all = BitmapUtil.drawAll(left, right);
	}

	public void close() {
		try {
			if (all != null)
				all.recycle();
			if (left != null)
				left.recycle();
			if (right != null)
				right.recycle();
			all = null;
			left = null;
			right = null;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
}