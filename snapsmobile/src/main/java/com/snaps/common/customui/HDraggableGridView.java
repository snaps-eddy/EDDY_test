package com.snaps.common.customui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.snaps.common.customui.dragdrop.DragController;
import com.snaps.common.customui.dragdrop.DragController.OnForceMotionEvent;
import com.snaps.common.customui.dragdrop.DragSource;
import com.snaps.common.customui.dragdrop.DragView;
import com.snaps.common.customui.dragdrop.DropTarget;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;

/**
 * 가로스크롤 드래그 가능한 그리드뷰
 * 
 * @author crjung
 * 
 */
@SuppressLint("WrongCall")
public class HDraggableGridView extends ViewGroup implements DropTarget, View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {
	private static final String TAG = HDraggableGridView.class.getSimpleName();
	// layout vars
	public static float childRatio = .9f;
	protected int colCount = 4, childSize, padding, dpi, scroll = 0;
	protected float lastDelta = 0;
	protected Handler handler = new Handler();
	// dragging vars
	protected int dragged = -1, lastX = -1, lastY = -1, lastTarget = -1;
	protected boolean enabled = true, touching = false, enableLongClick = true;
	// item visible idx
	int firstIdx, lastIdx;
	// anim vars
	public static int animT = 150;
	protected ArrayList<Integer> newPositions = new ArrayList<Integer>();
	// listeners
	protected OnRearrangeListener onRearrangeListener;
	protected OnClickListener secondaryOnClickListener;
	private OnItemClickListener onItemClickListener;

	public boolean mIsSimpleBook = false;
	int upIndex = 0;

	DragController mDragController;

	int setchildSize;
	int titleHeight;
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
	public HDraggableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setListeners();
		handler.removeCallbacks(updateTask);
		handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 100);
		setChildrenDrawingOrderEnabled(true);

		titleHeight = UIUtil.convertDPtoPX(getContext(), 48);

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dpi = metrics.densityDpi;
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
			if (dragged != -1)// 드래그모드
			{

				if (lastX < padding * 3 && scroll > 0)
					scroll -= 20;
				else if (lastX > getRight() - getLeft() - (padding * 3) && scroll < getMaxScroll())
					scroll += 20;
			} else if (lastDelta != 0 && !touching)// 스크롤모드
			{
				scroll += lastDelta;
				lastDelta *= .9;
				if (Math.abs(lastDelta) < .25)
					lastDelta = 0;
			}
			clampScroll();
			onLayout(true, getLeft(), getTop(), getRight(), getBottom());

			handler.postDelayed(this, 25);
		}
	};

	// OVERRIDES
	@Override
	public void addView(View child) {
		addView(child, false);
	};

	public void addView(View child, boolean isScrolltoEnd) {
		super.addView(child);
		newPositions.add(-1);

		if (isScrolltoEnd)
			scrollToRight();
	};

	@Override
	public void removeViewAt(int index) {
		if (getChildCount() == 0 || getChildCount() <= index)// 갯수보다 idx가 크면 패스
			return;
		super.removeViewAt(index);
		if(newPositions != null && newPositions.size() > index)
			newPositions.remove(index);
	};

	public void removeViewAt(final int index, boolean isAnimation) {
		if (Build.VERSION.SDK_INT < 16)// 삭제 시 애니메이션 처리는 젤리빈 버전 이하는 허용하지 않음
			isAnimation = false;

		if (getChildCount() == 0 || getChildCount() <= index)// 갯수보다 idx가 크면 패스
			return;
		if (isAnimation) {
			View view = getChildAt(index);
			view.clearAnimation();
			Animation fadeOut = new AlphaAnimation(1, 0);
			fadeOut.setInterpolator(new AccelerateInterpolator());
			fadeOut.setDuration(200);
			fadeOut.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					ViewGroup viewGroup = HDraggableGridView.this;
					if(viewGroup != null && viewGroup.getChildCount() > index) {
						viewGroup.removeViewAt(index);
					}
					if(newPositions != null && newPositions.size() > index)
						newPositions.remove(index);
				}
			});
			view.startAnimation(fadeOut);
		} else {
			super.removeViewAt(index);
			if(newPositions != null && newPositions.size() > index)
				newPositions.remove(index);
		}
	};

	// LAYOUT
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (childSize <= 0)
			childSize = (int) Math.round((double) ((r - l) - (padding * (colCount + 1))) / (double) colCount);
		int firstStart = 0;
		int firstEnd = padding + childSize;
		int lastStart = getWidth() - firstEnd;
		int lastEnd = getWidth();

		for (int i = 0; i < getChildCount(); i++) {
			if (i != dragged) {
				Point xy = getCoorFromIndex(i);

				if (firstStart <= xy.x && xy.x <= firstEnd)
					firstIdx = i;
				else if (lastStart <= xy.x + childSize && xy.x + childSize <= lastEnd)
					lastIdx = i;

				getChildAt(i).layout(xy.x, xy.y, xy.x + childSize, xy.y + childSize);
				getChildAt(i).measure(MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY));
			}
		}
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (dragged == -1)
			return i;
		else if (i == childCount - 1)
			return dragged;
		else if (i >= dragged)
			return i + 1;
		return i;
	}

	public int getIndexFromCoor(int x, int y) {
		int col = getColOrRowFromCoor(x + scroll), row = getColOrRowFromCoor(y);
		if (col == -1 || row == -1) // touch is between columns or rows
			return -1;
		int index = row * colCount + col;
		if (index >= getChildCount())
			return -1;
		return index;
	}

	protected int getColOrRowFromCoor(int coor) {
		coor -= padding;
		for (int i = 0; coor > 0; i++) {
			if (coor < childSize)
				return i;
			coor -= (childSize + padding);
		}
		return -1;
	}

	protected int getTargetFromCoor(int x, int y) {
		if (getColOrRowFromCoor(x + scroll) == -1) // touch is between rows
			return -1;

		int leftPos = getIndexFromCoor(x - (childSize / 4), y);
		int rightPos = getIndexFromCoor(x + (childSize / 4), y);
		if (leftPos == -1 && rightPos == -1) // touch is in the middle of
												// nowhere
			return -1;
		if (leftPos == rightPos) // touch is in the middle of a visual
			return -1;

		int target = -1;
		if (rightPos > -1)
			target = rightPos;
		else if (leftPos > -1)
			target = leftPos + 1;
		if (dragged < target)
			return target - 1;

		return target;
	}

	protected Point getCoorFromIndex(int index) {
		return new Point(padding + (childSize + padding) * index - scroll, padding);// 무조건
																					// 1줄
	}

	public int getIndexOf(View child) {
		for (int i = 0; i < getChildCount(); i++)
			if (getChildAt(i) == child)
				return i;
		return -1;
	}

	// EVENT HANDLERS
	public void onClick(View view) {
		if (enabled) {
			if (secondaryOnClickListener != null)
				secondaryOnClickListener.onClick(view);
			if (onItemClickListener != null && getLastIndex() != -1)
				onItemClickListener.onItemClick(null, getChildAt(getLastIndex()), getLastIndex(), getLastIndex() / colCount);
		}
	}

	public boolean onLongClick(View view) {
		if (!enableLongClick)
			return false;
		if (!enabled)
			return false;
		int index = getLastIndex();
		if (index != -1) {
			dragged = index;
			View v = getChildAt(dragged);
			return startDrag(v);
		}
		return false;
	}

	boolean startDrag(View v) {
		DragSource dragSource = (DragSource) v;
		mDragController.startDrag(v, dragSource, dragSource, DragController.DRAG_ACTION_MOVE);
		onforceMEvent.onActionMove(lastX, lastY);
		return true;
	}

	boolean isDownAnimation = false;

	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();

		int indexdown = 0;
		int downX = 0;
		int downY = 0;
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			Dlog.d("onTouch() ACTION_DOWN x:" + (int)event.getX() + ", y:" + (int)event.getY());
			enabled = true;
			lastX = (int) event.getX();
			lastY = (int) event.getY();

			touching = true;

			downX = (int) event.getX();
			downY = (int) event.getY();

			indexdown = getIndexFromCoor(downX, downY);

			if (mIsSimpleBook) {

				if (getChildAt(indexdown) != null) {
					TranslateAnimation ani = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.05f);
					ani.setFillAfter(true);
					ani.setDuration(50);
					ani.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.accelerate_interpolator));

					upIndex = indexdown;

					getChildAt(indexdown).startAnimation(ani);
				}

				isDownAnimation = true;

			}

			break;
		case MotionEvent.ACTION_MOVE:
			int delta = lastX - (int) event.getX();
			if (dragged != -1) {// long click 후 드래그 모드로 움직일때
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
			} else {// 스크롤될 때
				scroll += delta;
				clampScroll();
				if (Math.abs(delta) > 2)
					enabled = false;
				onLayout(true, getLeft(), getTop(), getRight(), getBottom());
			}
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			lastDelta = delta;
			break;
		case MotionEvent.ACTION_UP:
			Dlog.d("onTouch() ACTION_UP x:" + (int)event.getX() + ", y:" + (int)event.getY());
			if (dragged != -1) {
				View v = getChildAt(dragged);
				if (lastTarget != -1)
					reorderChildren();
				else {
					Point xy = getCoorFromIndex(dragged);
					v.layout(xy.x, xy.y, xy.x + childSize, xy.y + childSize);
				}
				v.clearAnimation();
				if (v instanceof ImageView)
					((ImageView) v).setAlpha(255);
				lastTarget = -1;
				dragged = -1;
			}

			if (mIsSimpleBook && isDownAnimation) {
				isDownAnimation = false;
				TranslateAnimation ani = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.05f, Animation.RELATIVE_TO_SELF, 0.00f);
				ani.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.accelerate_interpolator));
				ani.setDuration(50);

				ani.setFillAfter(true);

				if(getChildAt(upIndex) != null)
					getChildAt(upIndex).startAnimation(ani);
			}

			touching = false;
			break;
		}
		if (dragged != -1)
			return true;
		return false;
	}

	OnForceMotionEvent onforceMEvent = new OnForceMotionEvent() {
		@Override
		public void onActionMove(int eventX, int eventY) {
			eventY -= titleHeight;
			int delta = lastX - (int) eventX;

			if (dragged != -1) {// long click 후 드래그 모드로 움직일때
				// change draw location of dragged visual
				int x = (int) eventX, y = (int) eventY;
				int l = x - (3 * childSize / 6), t = y - (3 * childSize / 6);
				getChildAt(dragged).layout(l, 5000, l + (childSize), t + (childSize));

				// check for new target hover
				int target = getTargetFromCoor(x, y);
				if (lastTarget != target) {
					if (target != -1) {
						animateGap(target);
						lastTarget = target;
					}
				}
			} else {// 스크롤될 때
				scroll += delta;
				clampScroll();
				if (Math.abs(delta) > 2)
					enabled = false;
				onLayout(true, getLeft(), getTop(), getRight(), getBottom());
			}
			lastX = (int) eventX;
			lastY = (int) eventY;
			lastDelta = delta;
		}

		@Override
		public void onActionUp() {
			if (dragged != -1) {
				View v = getChildAt(dragged);
				if (lastTarget != -1)
					reorderChildren();
				else {
					Point xy = getCoorFromIndex(dragged);
					v.layout(xy.x, xy.y, xy.x + childSize, xy.y + childSize);
				}
				v.clearAnimation();
				if (v instanceof ImageView)
					((ImageView) v).setAlpha(255);
				lastTarget = -1;
				dragged = -1;
			}
			touching = false;
		}
	};

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

		animSet.addAnimation(scale);
		animSet.addAnimation(alpha);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);

		v.clearAnimation();
		v.startAnimation(animSet);
	}

	protected void animateGap(int target) {
		if(newPositions == null || newPositions.size() < getChildCount()) 
			return;
		
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (i == dragged)
				continue;
			int newPos = i;
			if (dragged < target && i >= dragged + 1 && i <= target)
				newPos--;
			else if (target < dragged && i >= target && i < dragged)
				newPos++;

			// animate
			int oldPos = i;
			if (newPositions.get(i) != -1)
				oldPos = newPositions.get(i);
			if (oldPos == newPos)
				continue;

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

		if (onRearrangeListener != null)
			onRearrangeListener.onRearrange(tempDragged, tempLastTarget);

		for (int i = 0; i < children.size(); i++) {
			newPositions.set(i, -1);
			addView(children.get(i));
		}
		onLayout(true, getLeft(), getTop(), getRight(), getBottom());
	}

	public void recvReorder(int recvDragged, int recvLastTarget) {
		View v = getChildAt(recvDragged);
		if (recvLastTarget != -1)
			reorderChildren(recvDragged, recvLastTarget);
		v.clearAnimation();
		if (v instanceof ImageView)
			((ImageView) v).setAlpha(255);
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

	public void scrollToLeft() {
		scroll = 0;
	}

	public void scrollToRight() {
		scroll = Integer.MAX_VALUE;
		clampScroll();
	}

	public void scrollTo(int moveScroll) {
		scroll = moveScroll;
	}

	public void scrollToIdx(int childIdx) {

		// child가 4개 이하이거나, scroll 해야 될 값이 현재 스크롤 된 영역 안에 있으면 스크롤 안함.
		if (colCount >= getChildCount() || firstIdx <= childIdx && childIdx <= lastIdx)
			return;

		++childIdx;
		scroll = childIdx * childSize + (childIdx + 1) * padding - getWidth();
	}

	public void scrollToSimpleIdx(int childIdx) {
		// child가 4개 이하이거나, scroll 해야 될 값이 현재 스크롤 된 영역 안에 있으면 스크롤 안함.
		// if (colCount >= getChildCount() || firstIdx < childIdx && childIdx < lastIdx)
		// return;

		if (childIdx == firstIdx || childIdx == firstIdx + 1 || childIdx == firstIdx + 2)
			return;

		++childIdx;
		scroll = childIdx * childSize + (childIdx + 1) * padding - getWidth();
	}

	public int getCurrentScroll() {
		return scroll;
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
			if (scroll >= -stretch)
				scroll = 0;
			else if (!touching)
				scroll -= scroll / stretch;
		} else if (scroll > max) {
			if (scroll <= max + stretch)
				scroll = max;
			else if (!touching)
				scroll += (max - scroll) / stretch;
		}
	}

	protected int getMaxScroll() {
		int max = getChildCount() * childSize + (getChildCount() + 1) * padding - getWidth();
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

	public void setDragController(DragController mDragController) {
		this.mDragController = mDragController;
		this.mDragController.setOnForceMEvent(onforceMEvent);
	}

	@Override
	public boolean onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		return false;
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		return false;
	}

	@Override
	public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo, Rect recycle) {
		return null;
	}
}