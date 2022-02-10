package com.snaps.mobile.utils.custom_layouts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.View;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.UIUtil;

import static com.snaps.common.utils.ui.UIUtil.getStatusBarHeight;

public class ZoomViewCoordInfo {
	private static final String TAG = ZoomViewCoordInfo.class.getSimpleName();

	private Matrix mTranslateMatrixInverse = new Matrix();
	private Matrix mScaleMatrixInverse = new Matrix();
	private float scaleFactor;
	private float defualtScaleFactor;
	private float translateX = 0.f;
	private float translateY = 0.f;

	public ZoomViewCoordInfo(Context context) {
	}

	public float getScaleFactor() {
		return scaleFactor;
	}

	public float getDefualtScaleFactor() {
		return defualtScaleFactor;
	}

	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public void setDefualtScaleFactor(float defualtScaleFactor) {
		this.defualtScaleFactor = defualtScaleFactor;
	}

	public void convertPopupOverRect(Rect rect) {
		DataTransManager transMan = DataTransManager.getInstance();
		if (transMan != null) {
			float rectCenterX = rect.left + (rect.width() / 2);
			float rectCenterY = rect.top + (rect.height() / 2);

			float[] rectCoord = {rectCenterX, rectCenterY};

			rectCoord = screenPointsToScaledPoints(rectCoord);

			float rectWidth = rect.width() * getScaleFactor();
			float rectHeight = rect.height() * getScaleFactor();

			int rectLeft = (int) (rectCoord[0] - (rectWidth / 2));
			int rectTop = (int) (rectCoord[1] - (rectHeight / 2));
			int rectRight = (int) (rectCoord[0] + (rectWidth / 2));
			int rectBottom = (int) (rectCoord[1] + (rectHeight / 2));

			rect.set(rectLeft, rectTop, rectRight, rectBottom);
		}
	}

	public void convertTestRect(Rect rect, boolean isLandScapeMode) {
		DataTransManager transMan = DataTransManager.getInstance();
		if (transMan != null) {
			float rectCenterX = rect.left;
			float rectCenterY = rect.top;

			float[] rectCoord = {rectCenterX, rectCenterY};

			rectCoord = screenPointsToScaledPoints(rectCoord);

			float rectWidth = rect.width() * getDefualtScaleFactor();
			float rectHeight = rect.height() * getDefualtScaleFactor();

			int rectLeft = 0;
			int rectTop = 0;
			int rectRight = 0;
			int rectBottom = 0;

			if (isLandScapeMode) {
				rectLeft = (int) rectCoord[0] - UIUtil.convertDPtoPX(ContextUtil.getContext(), 130);
				rectTop = (int) rectCoord[1] - UIUtil.convertDPtoPX(ContextUtil.getContext(), 52);
				rectRight = (int) (rectCoord[0] + rectWidth) - UIUtil.convertDPtoPX(ContextUtil.getContext(), 130);
				rectBottom = (int) (rectCoord[1] + rectHeight) - UIUtil.convertDPtoPX(ContextUtil.getContext(), 52);
			} else {
				rectLeft = (int) rectCoord[0];
				rectTop = (int) rectCoord[1] - UIUtil.convertDPtoPX(ContextUtil.getContext(), 48);
				rectRight = (int) (rectCoord[0] + rectWidth);
				rectBottom = (int) (rectCoord[1] + rectHeight) - UIUtil.convertDPtoPX(ContextUtil.getContext(), 48);
			}

			rect.set(rectLeft, rectTop, rectRight, rectBottom);
		}
	}

	public Rect covertItemRect(View view) throws Exception {
		Rect viewRect = new Rect();
		view.getGlobalVisibleRect(viewRect);

		Rect parentLayoutRect = new Rect();
		View containerView = (View) view.getParent().getParent();
		if (containerView != null) {
			containerView.getGlobalVisibleRect(parentLayoutRect);
		}

		Matrix calculatedMatrix = new Matrix();
		calculatedMatrix.setScale(defualtScaleFactor, defualtScaleFactor, parentLayoutRect.centerX(), parentLayoutRect.centerY());
		float[] targetViewPoint = {viewRect.left, viewRect.top};
		calculatedMatrix.mapPoints(targetViewPoint);

		float x = targetViewPoint[0];
		float y = targetViewPoint[1];

		float width = viewRect.width() * defualtScaleFactor;
		float height = (viewRect.height() * defualtScaleFactor);// -getStatusBarHeight();

		try {
			y -= getStatusBarHeight();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		viewRect.set((int) x, (int) y, (int) (x + width), (int) (y + height));

		return viewRect;
	}

	public Rect covertItemRectForRecommendBookEdit(View view) throws Exception {
		Context context = ContextUtil.getContext();
		if (context == null) {
			return null;
		}

		Rect viewRect = new Rect();
		view.getGlobalVisibleRect(viewRect);

		Rect parentLayoutRect = new Rect();
		View containerView = (View) view.getParent().getParent();
		if (containerView != null) {
			containerView.getGlobalVisibleRect(parentLayoutRect);
		}

		Matrix calculatedMatrix = new Matrix();
		calculatedMatrix.setScale(scaleFactor, scaleFactor, parentLayoutRect.centerX(), parentLayoutRect.centerY());
		float[] targetViewPoint = {viewRect.left, viewRect.top};
		calculatedMatrix.mapPoints(targetViewPoint);

		float x = targetViewPoint[0];
		float y = targetViewPoint[1];

		x += (translateX * scaleFactor);
		y += (translateY * scaleFactor);

		float width = viewRect.width() * scaleFactor;
		float height = (viewRect.height() * scaleFactor);// -getStatusBarHeight();

		try {
			y -= getStatusBarHeight();

			if (scaleFactor - defualtScaleFactor > 0.3f) {
				y -= UIUtil.convertDPtoPX(context, 4); //핀치로 확대하면 안 맞아서 넣은 값
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		int titleBarOffsetY = UIUtil.convertDPtoPX(context, 48);
		if (y < titleBarOffsetY) {
			height -= (titleBarOffsetY - y);
			y = titleBarOffsetY;
		}

		int bottomAreaOffsetY = UIUtil.getScreenHeight(context) - UIUtil.convertDPtoPX(ContextUtil.getContext(), 164) - getStatusBarHeight();
		int bottom = (int) (y + height);
		if (bottom > bottomAreaOffsetY) {
			bottom = bottomAreaOffsetY;
		}

		viewRect.set((int) x, (int) y, (int) (x + width), bottom);

		return viewRect;
	}

	public Rect covertItemRectForTutorialTooltip(View view, boolean isLandScapeMode, boolean isSmartRecommendBookMainPage) throws Exception {
		Rect viewRect = new Rect();
		view.getGlobalVisibleRect(viewRect);

		Rect parentLayoutRect = new Rect();
		if (isSmartRecommendBookMainPage) {
			parentLayoutRect.set(viewRect);
		} else {
			View containerView = (View) view.getParent().getParent();
			if (containerView != null) {
				containerView.getGlobalVisibleRect(parentLayoutRect);
			}
		}

		Matrix calculatedMatrix = new Matrix();
		calculatedMatrix.setScale(defualtScaleFactor, defualtScaleFactor, parentLayoutRect.centerX(), parentLayoutRect.centerY());
		float[] targetViewPoint = {viewRect.left, viewRect.top};
		calculatedMatrix.mapPoints(targetViewPoint);

		float x = targetViewPoint[0];
		float y = targetViewPoint[1];

		float width = viewRect.width() * defualtScaleFactor;
		float height = (viewRect.height() * defualtScaleFactor);// -getStatusBarHeight();

		if (!isLandScapeMode) {
			try {
				if (isSmartRecommendBookMainPage) {
					y += UIUtil.convertDPtoPX(ContextUtil.getContext(), 200); //FIME...Canvas가 200dp 아래에서 부터 시작되기 때문에....
				}

				y -= getStatusBarHeight();
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
		viewRect.set((int) x, (int) y, (int) (x + width), (int) (y + height));

		return viewRect;
	}

	public Rect covertItemRectForSmartSnaps(Activity activity, View view) throws Exception {
		Rect viewRect = new Rect();
		view.getGlobalVisibleRect(viewRect);

		Rect parentLayoutRect = new Rect();
		View containerView = (View) view.getParent().getParent();

		if (containerView != null) {
			containerView.getGlobalVisibleRect(parentLayoutRect);
		}

		Matrix calculatedMatrix = new Matrix();
		calculatedMatrix.setScale(defualtScaleFactor, defualtScaleFactor, parentLayoutRect.centerX(), parentLayoutRect.centerY());
		float[] targetViewPoint = {viewRect.left, viewRect.top};
		calculatedMatrix.mapPoints(targetViewPoint);

		float x = targetViewPoint[0];
		float y = targetViewPoint[1];

		float width = viewRect.width() * defualtScaleFactor;
		float height = (viewRect.height() * defualtScaleFactor);// -getStatusBarHeight();

		viewRect.set((int) x, (int) y, (int) (x + width), (int) (y + height));

		viewRect.offset(-parentLayoutRect.left, -parentLayoutRect.top);

		viewRect.offset(UIUtil.convertDPtoPX(activity, 110), -UIUtil.convertDPtoPX(activity, 4));

//		viewRect.inset(-UIUtil.convertDPtoPX(activity, 4), -UIUtil.convertDPtoPX(activity, 4));

		return viewRect;
	}

	public void covertItemRectPopmenu(Rect viewRect, View view, boolean isLandScapeMode) {
		Rect parentLayoutRect = new Rect();

		View containerView = null;

		try {
			containerView = (View) view.getParent().getParent();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		if (containerView != null) {
			containerView.getGlobalVisibleRect(parentLayoutRect);
		}

		Matrix calculatedMatrix = new Matrix();
		calculatedMatrix.setScale(defualtScaleFactor, defualtScaleFactor, parentLayoutRect.centerX(), parentLayoutRect.centerY());
		float[] targetViewPoint = {viewRect.left, viewRect.top};
		calculatedMatrix.mapPoints(targetViewPoint);

		float x = targetViewPoint[0];
		float y = targetViewPoint[1];

		float width = viewRect.width() * defualtScaleFactor;
		float height = (viewRect.height() * defualtScaleFactor);// -getStatusBarHeight();

		if (!isLandScapeMode) {
			try {
				y -= getStatusBarHeight();
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
		viewRect.set((int) x, (int) y, (int) (x + width), (int) (y + height));
	}

//	public boolean convertPopupOverRect(Rect rect, View rootView, boolean isLandscapeMode) {
//		if(getScaleFactor() > defualtScaleFactor) {
//			rect.set(rootView.getLeft(), rootView.getTop(), rootView.getLeft() + rootView.getMeasuredWidth(),  rootView.getTop() + rootView.getMeasuredHeight());
//			// plus 버튼의 절반위치 이동...
//			rect.offset((isLandscapeMode ? Const_VALUE.PLUS_BUTTON_HEIGHT : 0), -(Const_VALUE.PLUS_BUTTON_HEIGHT));
//			return true;
//		} else {
//			covertItemRectPopmenu(rect,rootView,isLandscapeMode);
//			return false;
//		}
//	}

	public boolean convertPopupOverRect(Rect rect, View view, View rootView, boolean isLandscapeMode) {
		if (getScaleFactor() > defualtScaleFactor) {
			rect.set(rootView.getLeft(), rootView.getTop(), rootView.getLeft() + rootView.getMeasuredWidth(), rootView.getTop() + rootView.getMeasuredHeight());
			// plus 버튼의 절반위치 이동...
			rect.offset((isLandscapeMode ? Const_VALUE.PLUS_BUTTON_HEIGHT : 0), -(Const_VALUE.PLUS_BUTTON_HEIGHT));
			return true;
		} else {
			covertItemRectPopmenu(rect, view, isLandscapeMode);
			return false;
		}
	}

	public Matrix getScaleMatrixInverse() {
		return mScaleMatrixInverse;
	}

	public void setScaleMatrixInverse(Matrix mScaleMatrixInverse) {
		this.mScaleMatrixInverse = mScaleMatrixInverse;
	}

	public Matrix getTranslateMatrixInverse() {
		return mTranslateMatrixInverse;
	}

	public void setTranslateMatrixInverse(Matrix mTranslateMatrixInverse) {
		this.mTranslateMatrixInverse = mTranslateMatrixInverse;
	}

	public float getTranslateY() {
		return translateY;
	}

	public void setTranslateY(float translateY) {
		this.translateY = translateY;
	}

	public float getTranslateX() {
		return translateX;
	}

	public void setTranslateX(float translateX) {
		this.translateX = translateX;
	}

	private float[] screenPointsToScaledPoints(float[] a) {
		mTranslateMatrixInverse.mapPoints(a);
		mScaleMatrixInverse.mapPoints(a);
		return a;
	}
}
