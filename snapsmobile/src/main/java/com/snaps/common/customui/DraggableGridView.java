package com.snaps.common.customui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 세로스크롤 드래그 가능한 그리드뷰
 *
 * @author crjung
 */
public class DraggableGridView extends ViewGroup implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {
	// layout vars
	public static float childRatio = .9f;
	protected int colCount = 4, childSize, padding, dpi, scroll = 0;
	protected float lastDelta = 0;
	protected Handler handler = new Handler();
	// dragging vars
	protected int dragged = -1, lastX = -1, lastY = -1, lastTarget = -1;
	protected boolean enabled = true, touching = false, enableLongClick = true;
	// anim vars
	public static int animT = 150;
	protected ArrayList<Integer> newPositions = new ArrayList<Integer>();
	// listeners
	protected OnRearrangeListener onRearrangeListener;
	protected OnClickListener secondaryOnClickListener;
	private OnItemClickListener onItemClickListener;

	private Vibrator mVibrator;
	private static final int VIBRATE_DURATION = 35;

	int setchildSize;
	List<String> childDataList = new ArrayList<String>();

	public void enabled(boolean isEnable) {
		enableLongClick = isEnable;
	}

	public void setChildDataList(List<String> childDataList) {
		this.childDataList = childDataList;
	}

	public void setSetchildSize(int setchildSize) {
		this.setchildSize = setchildSize;
	}

	public void setSetPadding(int setPadding) {
		this.padding = setPadding;
	}

	public interface OnRearrangeListener {
		public abstract void onRearrange(int oldIndex, int newIndex);
	}

	// CONSTRUCTOR AND HELPERS
	public DraggableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setListeners();
		handler.removeCallbacks(updateTask);
		handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 100);
		setChildrenDrawingOrderEnabled(true);

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dpi = metrics.densityDpi;

		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void stopHandler() {
		handler.removeCallbacks(updateTask);
		handler = null;
	}

	protected void setListeners() {
		setOnTouchListener(this);
		super.setOnClickListener(this);
		setOnLongClickListener(this);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		secondaryOnClickListener = l;
	}

	protected Runnable updateTask = new Runnable() {
		public void run() {
			if (dragged != -1) {
				if (lastY < padding * 3 && scroll > 0) {
					scroll -= 20;
				} else if (lastY > getBottom() - getTop() - (padding * 3) && scroll < getMaxScroll()) {
					scroll += 20;
				}
			} else if (lastDelta != 0 && !touching) {
				scroll += lastDelta;
				lastDelta *= .9;
				if (Math.abs(lastDelta) < .25) {
					lastDelta = 0;
				}
			}
			clampScroll();
			onLayout(true, getLeft(), getTop(), getRight(), getBottom());

			handler.postDelayed(this, 25);
		}
	};

	// OVERRIDES
	@Override
	public void addView(View child) {
		super.addView(child);
		newPositions.add(-1);
	}

	;

	@Override
	public void removeViewAt(int index) {
		if (getChildCount() <= index)// 갯수보다 idx가 크면 패스
		{
			return;
		}
		super.removeViewAt(index);
		newPositions.remove(index);
	}

	;

	// LAYOUT
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (childSize <= 0) {
			childSize = (int) Math.round((double) ((r - l) - (padding * (colCount + 1))) / (double) colCount);
		}

		for (int i = 0; i < getChildCount(); i++) {
			if (i != dragged) {
				Point xy = getCoorFromIndex(i);
				getChildAt(i).layout(xy.x, xy.y, xy.x + childSize, xy.y + childSize);
				getChildAt(i).measure(MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY));
			}
		}
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (dragged == -1) {
			return i;
		} else if (i == childCount - 1) {
			return dragged;
		} else if (i >= dragged) {
			return i + 1;
		}
		return i;
	}

	public int getIndexFromCoor(int x, int y) {
		int col = getColOrRowFromCoor(x), row = getColOrRowFromCoor(y + scroll);
		if (col == -1 || row == -1) // touch is between columns or rows
		{
			return -1;
		}
		int index = row * colCount + col;
		if (index >= getChildCount()) {
			return -1;
		}
		return index;
	}

	protected int getColOrRowFromCoor(int coor) {
		coor -= padding;
		for (int i = 0; coor > 0; i++) {
			if (coor < childSize) {
				return i;
			}
			coor -= (childSize + padding);
		}
		return -1;
	}

	protected int getTargetFromCoor(int x, int y) {
		if (getColOrRowFromCoor(y + scroll) == -1) // touch is between rows
		{
			return -1;
		}

		int leftPos = getIndexFromCoor(x - (childSize / 4), y);
		int rightPos = getIndexFromCoor(x + (childSize / 4), y);
		if (leftPos == -1 && rightPos == -1) // touch is in the middle of
		// nowhere
		{
			return -1;
		}
		if (leftPos == rightPos) // touch is in the middle of a visual
		{
			return -1;
		}

		int target = -1;
		if (rightPos > -1) {
			target = rightPos;
		} else if (leftPos > -1) {
			target = leftPos + 1;
		}
		if (dragged < target) {
			return target - 1;
		}

		return target;
	}

	protected Point getCoorFromIndex(int index) {
		int col = index % colCount;
		int row = index / colCount;
		return new Point(padding + (childSize + padding) * col, padding + (childSize + padding) * row - scroll);
	}

	public int getIndexOf(View child) {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i) == child) {
				return i;
			}
		}
		return -1;
	}

	// EVENT HANDLERS
	public void onClick(View view) {
		if (enabled) {
			if (secondaryOnClickListener != null) {
				secondaryOnClickListener.onClick(view);
			}
			if (onItemClickListener != null && getLastIndex() != -1) {
				onItemClickListener.onItemClick(null, getChildAt(getLastIndex()), getLastIndex(), getLastIndex() / colCount);
			}
		}
	}

	public boolean onLongClick(View view) {
		if (!enableLongClick) {
			return false;
		}
		if (!enabled) {
			return false;
		}
		int index = getLastIndex();
		if (index != -1) {
			dragged = index;
			animateDragged();
			return true;
		}
		return false;
	}

	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				enabled = true;
				lastX = (int) event.getX();
				lastY = (int) event.getY();
				touching = true;
				break;
			case MotionEvent.ACTION_MOVE:
				int delta = lastY - (int) event.getY();
				if (dragged != -1) {
					// change draw location of dragged visual
					int x = (int) event.getX(), y = (int) event.getY();
					int l = x - (3 * childSize / 6), t = y - (3 * childSize / 6);
					getChildAt(dragged).layout(l, t, l + (childSize), t + (childSize));

					// check for new target hover
					int target = getTargetFromCoor(x, y);
					if (lastTarget != target) {
						if (target != -1) {
							animateGap(target);
							lastTarget = target;
						}
					}
				} else {
					scroll += delta;
					clampScroll();
					if (Math.abs(delta) > 2) {
						enabled = false;
					}
					onLayout(true, getLeft(), getTop(), getRight(), getBottom());
				}
				lastX = (int) event.getX();
				lastY = (int) event.getY();
				lastDelta = delta;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (dragged != -1) {
					View v = getChildAt(dragged);
					if (lastTarget != -1) {
						reorderChildren();
					} else {
						Point xy = getCoorFromIndex(dragged);
						v.layout(xy.x, xy.y, xy.x + childSize, xy.y + childSize);
					}
					v.clearAnimation();
					if (v instanceof ImageView) {
						((ImageView) v).setAlpha(255);
					}
					lastTarget = -1;
					dragged = -1;
				}
				touching = false;
				break;
		}
		if (dragged != -1) {
			return true;
		}
		return false;
	}

	// EVENT HELPERS
	protected void animateDragged() {
		View v = getChildAt(dragged);
		int x = getCoorFromIndex(dragged).x + childSize / 2, y = getCoorFromIndex(dragged).y + childSize / 2;
		int l = x - (3 * childSize / 6), t = y - (3 * childSize / 6);
		v.layout(l, t, l + (childSize), t + (childSize));
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1, childSize * 3 / 6, childSize * 3 / 6);
		scale.setDuration(animT);
		AlphaAnimation alpha = new AlphaAnimation(1, .5f);
		alpha.setDuration(animT);

		animSet.addAnimation(alpha);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);

		v.clearAnimation();
		v.startAnimation(animSet);

		mVibrator.vibrate(VIBRATE_DURATION);
	}

	protected void animateGap(int target) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (i == dragged) {
				continue;
			}
			int newPos = i;
			if (dragged < target && i >= dragged + 1 && i <= target) {
				newPos--;
			} else if (target < dragged && i >= target && i < dragged) {
				newPos++;
			}

			// animate
			int oldPos = i;
			if (newPositions.get(i) != -1) {
				oldPos = newPositions.get(i);
			}
			if (oldPos == newPos) {
				continue;
			}

			Point oldXY = getCoorFromIndex(oldPos);
			Point newXY = getCoorFromIndex(newPos);
			Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y - v.getTop());
			Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y - v.getTop());

			TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE, newOffset.x, Animation.ABSOLUTE, oldOffset.y, Animation.ABSOLUTE, newOffset.y);
			translate.setDuration(animT);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			v.clearAnimation();
			v.startAnimation(translate);

			newPositions.set(i, newPos);
		}
	}

	protected void reorderChildren() {
		// FIGURE OUT HOW TO REORDER CHILDREN WITHOUT REMOVING THEM ALL AND
		// RECONSTRUCTING THE LIST!!!
		ArrayList<View> children = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).clearAnimation();
			children.add(getChildAt(i));
		}
		int tempDragged = dragged;
		int tempLastTarget = lastTarget;
		removeAllViews();
		while (dragged != lastTarget) {
			if (lastTarget == children.size()) // dragged and dropped to the
			// right of the last element
			{
				children.add(children.remove(dragged));
				childDataList.add(childDataList.remove(dragged));
				dragged = lastTarget;
			} else if (dragged < lastTarget) // shift to the right
			{
				Collections.swap(children, dragged, dragged + 1);
				Collections.swap(childDataList, dragged, dragged + 1);
				dragged++;
			} else if (dragged > lastTarget) // shift to the left
			{
				Collections.swap(children, dragged, dragged - 1);
				Collections.swap(childDataList, dragged, dragged - 1);
				dragged--;
			}
		}

		if (onRearrangeListener != null) {
			onRearrangeListener.onRearrange(tempDragged, tempLastTarget);
		}

		for (int i = 0; i < children.size(); i++) {
			newPositions.set(i, -1);
			addView(children.get(i));
		}
		onLayout(true, getLeft(), getTop(), getRight(), getBottom());
	}

	public void recvReorder(int recvDragged, int recvLastTarget) {
		View v = getChildAt(recvDragged);
		if (recvLastTarget != -1) {
			reorderChildren(recvDragged, recvLastTarget);
		}
		v.clearAnimation();
		if (v instanceof ImageView) {
			((ImageView) v).setAlpha(255);
		}
		lastTarget = -1;
		dragged = -1;
	}

	public void reorderChildren(int recvDragged, int recvLastTarget) {
		// FIGURE OUT HOW TO REORDER CHILDREN WITHOUT REMOVING THEM ALL AND
		// RECONSTRUCTING THE LIST!!!
		ArrayList<View> children = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).clearAnimation();
			children.add(getChildAt(i));
		}
		dragged = recvDragged;
		lastTarget = recvLastTarget;
		removeAllViews();
		while (dragged != lastTarget) {
			if (lastTarget == children.size()) // dragged and dropped to the
			// right of the last element
			{
				children.add(children.remove(dragged));
				dragged = lastTarget;
			} else if (dragged < lastTarget) // shift to the right
			{
				Collections.swap(children, dragged, dragged + 1);
				dragged++;
			} else if (dragged > lastTarget) // shift to the left
			{
				Collections.swap(children, dragged, dragged - 1);
				dragged--;
			}
		}

		for (int i = 0; i < children.size(); i++) {
			newPositions.set(i, -1);
			addView(children.get(i));
		}
		onLayout(true, getLeft(), getTop(), getRight(), getBottom());
	}

	public void scrollToTop() {
		scroll = 0;
	}

	public void scrollToBottom() {
		scroll = Integer.MAX_VALUE;
		clampScroll();
	}

	protected void clampScroll() {
		int stretch = 3, overreach = getHeight() / 2;
		int max = getMaxScroll();
		max = Math.max(max, 0);

		overreach = 0;// 스크롤 튕기기 끔.

		if (scroll < -overreach) {
			scroll = -overreach;
			lastDelta = 0;
		} else if (scroll > max + overreach) {
			scroll = max + overreach;
			lastDelta = 0;
		} else if (scroll < 0) {
			if (scroll >= -stretch) {
				scroll = 0;
			} else if (!touching) {
				scroll -= scroll / stretch;
			}
		} else if (scroll > max) {
			if (scroll <= max + stretch) {
				scroll = max;
			} else if (!touching) {
				scroll += (max - scroll) / stretch;
			}
		}
	}

	protected int getMaxScroll() {
		int rowCount = (int) Math.ceil((double) getChildCount() / colCount), max = rowCount * childSize + (rowCount + 1) * padding - getHeight();
		return max;
	}

	public int getLastIndex() {
		return getIndexFromCoor(lastX, lastY);
	}

	// OTHER METHODS
	public void setOnRearrangeListener(OnRearrangeListener l) {
		this.onRearrangeListener = l;
	}

	public void setOnItemClickListener(OnItemClickListener l) {
		this.onItemClickListener = l;
	}

}