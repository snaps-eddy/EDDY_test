
package com.snaps.common.customui;

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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.snaps.common.customui.dragdrop.DragController;
import com.snaps.common.customui.dragdrop.DragController.OnForceMotionEvent;
import com.snaps.common.customui.dragdrop.DragSource;
import com.snaps.common.customui.dragdrop.DragView;
import com.snaps.common.utils.constant.Config;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 세로 스크롤 드래그 가능한 그리드뷰
 * 
 * @author crjung
 * 
 */
@SuppressLint("WrongCall")
public class HDraggableGridViewVertical extends HDraggableGridParent {
	private static final String TAG = HDraggableGridViewVertical.class.getSimpleName();
	// layout vars
	public static float childRatio = .9f;
	protected int colCount = 4, childSize, padding, dpi, scroll = 0;
	protected int smoothScrollTarget = -1;
	protected int childWidth = 0;
	protected float lastDelta = 0;
	protected Handler handler = new Handler();
	// dragging vars
	protected int dragged = -1, lastX = -1, lastY = -1, lastTarget = -1;
	protected boolean enabled = true, touching = false, enableLongClick = true;
	// item visible idx
	int firstIdx, lastIdx;
	// anim vars
	public static int animT = 200;
	protected ArrayList<Integer> newPositions = new ArrayList<Integer>();
	// listeners
	protected OnRearrangeListener onRearrangeListener;

	protected OnDragViewClickListener secondaryOnClickListener;


	public boolean mIsSimpleBook = false;
	int upIndex = 0;
	
	private boolean isLongClicked = false;

	DragController mDragController;

	int setchildSize;
	int titleHeight;
	int indexdown = 0;
	List<String> childDataList = new ArrayList<String>();
	
	int maxPage = 75; // 최대페이지
	int minPage = 15; // 최소페이지
	int spinePage = 37; // 책등추가 페이지 -1이면 하드커버인경우..

	OnDragScrollState mDragScrollStateListener;
	
	public void enabled(boolean isEnable) {
		enableLongClick = isEnable;
	}

	public void setChildDataList(List<String> childDataList) {
		this.childDataList = childDataList;
	}
	
	public void addChildDataList(int index, String pageIdx) {
		if(childDataList != null)
			childDataList.add(index, pageIdx);
	}
	
	public void removeChildDataList(int index) {
		if(childDataList != null)
			childDataList.remove(index);
	}

	public void setSetchildSize(int setchildSize) {
		this.setchildSize = setchildSize;
	}

	public void setSetPadding(int setPadding) {
		this.padding = setPadding;
	}

	// CONSTRUCTOR AND HELPERS
	public HDraggableGridViewVertical(Context context, AttributeSet attrs) {
		super(context, attrs);
		setListeners();
		handler.removeCallbacks(updateTask);
		handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 100);
		setChildrenDrawingOrderEnabled(true);

		titleHeight = UIUtil.convertDPtoPX(getContext(), 43);

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dpi = metrics.densityDpi;
	}

	public void stopHandler() {
		if(handler != null)
			handler.removeCallbacks(updateTask);
	}

	protected void setListeners() {
		setOnTouchListener(this);
		super.setOnClickListener(this);
		setOnLongClickListener(this);
	}

	public void setOnDragViewClickListener(OnDragViewClickListener l) {
		secondaryOnClickListener = l;
	}
	
	protected Runnable updateTask = new Runnable() {
		public void run() {
			if (dragged != -1)// 드래그모드
			{
				if (lastY < padding * 3 && scroll > 0) 
					scroll -= 20;
				else if (lastY > getBottom() - getTop() - (padding * 3) && scroll < getMaxScroll()) 
					scroll += 20;
			} 
			else if(smoothScrollTarget != -1) {
				if(touching) {
					smoothScrollTarget = -1;
					mDragScrollStateListener = null;
				} else {
					int delta = Math.abs(scroll - smoothScrollTarget);
					if(scroll < smoothScrollTarget) {
						if(delta > 30) {
							delta *= .6f;
							scroll += delta;
						} else {
							scroll += 10;
						}
							
						if(scroll >= smoothScrollTarget) {
							scroll = smoothScrollTarget;
							smoothScrollTarget = -1;
							if(mDragScrollStateListener != null) {
								mDragScrollStateListener.onDragScrollChanged(0);
								mDragScrollStateListener = null;
							}
						}
					} else {
						if(delta > 30) {
							delta *= .6f;
							scroll -= delta;
						} else {
							scroll -= 10;
						}
						
						if(scroll <= smoothScrollTarget) {
							scroll = smoothScrollTarget;
							smoothScrollTarget = -1;
							if(mDragScrollStateListener != null) {
								mDragScrollStateListener.onDragScrollChanged(0);
								mDragScrollStateListener = null;
							}
						}
					}
				}
			} else if (!isLongClicked && lastDelta != 0 && !touching) {// 스크롤모드
				scroll += lastDelta;
				lastDelta *= .85f;
				if (Math.abs(lastDelta) < .3)
					lastDelta = 0;
			}
			
			clampScroll();
			onLayout(true, getLeft(), getTop(), getRight(), getBottom());

			handler.postDelayed(this, 30);
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
					HDraggableGridViewVertical.super.removeViewAt(index);
					newPositions.remove(index);
				}
			});
			view.startAnimation(fadeOut);
		} else {
			super.removeViewAt(index);
			newPositions.remove(index);
		}
	};

	// LAYOUT
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(setchildSize != 0) {
			childWidth = setchildSize;
		} else {
			if(childWidth <= 0)
				childWidth = UIUtil.convertDPtoPX(getContext(), 85);//(int) r - l;d
		}
		
		if (childSize <= 0)
				childSize = UIUtil.convertDPtoPX(getContext(), 85);

		int firstStart = 0;
		int firstEnd = padding + childSize;
		int lastStart = getHeight() - firstEnd;
		int lastEnd = getHeight();

		for (int i = 0; i < getChildCount(); i++) {
			if (i != dragged) {
				Point xy = getCoorFromIndex(i);

				if (firstStart <= xy.y && xy.y <= firstEnd)
					firstIdx = i;
				else if (lastStart <= xy.y + childSize && xy.y + childSize <= lastEnd)
					lastIdx = i;

				int x = (getMeasuredWidth() - childWidth) / 2;
				getChildAt(i).layout(x, xy.y, x + childWidth, xy.y + childSize);
				getChildAt(i).measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY));
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
		int col = getColOrRowFromCoor(y + scroll), row = 0;//getColOrRowFromCoor(y);
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
		if (getColOrRowFromCoor(y + scroll) == -1) // touch is between rows
			return -1;

		int topPos = getIndexFromCoor(x, y  - (childSize / 2));
		int bottomPos = getIndexFromCoor(x, y + (childSize / 2));
		if (topPos == -1 && bottomPos == -1) // touch is in the middle of
												// nowhere
			return -1;
		if (topPos == bottomPos) // touch is in the middle of a visual
			return -1;

		int target = -1;
		if (bottomPos > -1)
			target = bottomPos;
		else if (topPos > -1)
			target = topPos + 1;
		if (dragged < target)
			return target - 1;

		return target;
	}

	protected Point getCoorFromIndex(int index) {
		int x = (getMeasuredWidth() - childWidth) / 2;
		return new Point(x, padding + (childSize + padding) * index - scroll);// 무조건
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
				secondaryOnClickListener.onDragViewClick(view, indexdown);
		}
	}

	public boolean onLongClick(View view) {
		if(Config.isNotCoverPhotoBook())
			return false;
		if (!enableLongClick)
			return false;
		if (!enabled)
			return false;
		
		//커버와 속지는 드래그 금지.
		if(indexdown == 0 || indexdown == 1)
			return false;
		
		isLongClicked = true;
		
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

		int downX = 0;
		int downY = 0;
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			isLongClicked = false;
			Dlog.d("onTouch() ACTION_DOWN x:" + (int)event.getX() + ", y:" + (int)event.getY());
			enabled = true;
			lastX = (int) event.getX();
			lastY = (int) event.getY();

			touching = true;

			downX = (int) event.getX();
			downY = (int) event.getY();

			indexdown = getIndexFromCoor(downX, downY);
			break;
		case MotionEvent.ACTION_MOVE:
			int delta = lastY - (int) event.getY();
			if (dragged != -1) {// long click 후 드래그 모드로 움직일때
				// change draw location of dragged visual
				int x = (int) event.getX(), y = (int) event.getY();
				int l = x - (3 * childWidth / 6), t = y - (3 * childSize / 6);
				getChildAt(dragged).layout(l, t, l + (childWidth), t + (childSize));

				// check for new target hover
				int target = getTargetFromCoor(x, y);
				//커버와 속지는 드래그 금지.
				if(target > 1) {
					if (lastTarget != target) {
						if (target != -1) {
							animateGap(target);
							lastTarget = target;
						}
					}
				}
			} else {// 스크롤될 때
				scroll += delta;
				clampScroll();
				if (Math.abs(delta) > 10)
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
					v.layout(xy.x, xy.y, xy.x + childWidth, xy.y + childSize);
					
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (onRearrangeListener != null)
								onRearrangeListener.onRearrange(indexdown, indexdown);
						}
					}, 30);
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
				ani.setDuration(10);

				ani.setFillAfter(true);

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
			int delta = lastY - (int) eventY;

			if (dragged != -1) {// long click 후 드래그 모드로 움직일때
				// change draw location of dragged visual
				int x = (int) eventX, y = (int) eventY;
				int l = x - (3 * childWidth / 6), t = y - (3 * childSize / 6);
				getChildAt(dragged).layout(5000, t, l + (childWidth), t + (childSize));

				// check for new target hover
				int target = getTargetFromCoor(x, y);
				if(target > 1) {
					if (lastTarget != target) {
						if (target != -1) {
							animateGap(target);
							lastTarget = target;
						}
					}
				}
			} else {// 스크롤될 때
				scroll += delta;
				clampScroll();
				if (Math.abs(delta) > 10)
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
					v.layout(xy.x, xy.y, xy.x + childWidth, xy.y + childSize);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (onRearrangeListener != null)
								onRearrangeListener.onRearrange(indexdown, indexdown);
						}
					}, 30);
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
		int x = getCoorFromIndex(dragged).x + childWidth / 2, y = getCoorFromIndex(dragged).y + childSize / 2;
		int l = x - (3 * childWidth / 6), t = y - (3 * childSize / 6);
		v.layout(l, t, l + (childWidth), t + (childSize));
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1, childWidth * 3 / 6, childSize * 3 / 6);
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
			if(newPositions.size() <= i)
				continue;
			
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
		final int tempDragged = dragged;
		final int tempLastTarget = lastTarget;
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

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (onRearrangeListener != null)
					onRearrangeListener.onRearrange(tempDragged, tempLastTarget);
			}
		}, 30);

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

	public void scrollToIdx(int moveType, int childIdx) {
		
		if(childIdx >= getChildCount()-1) {
			childIdx -= 1;
		}
		
		int targetIdx = 0;
		switch (moveType) {
		case EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NEXT:
			targetIdx = childIdx + 1;
			break;
		case EditActivityThumbnailUtils.PAGE_MOVE_TYPE_PREV:
			targetIdx = childIdx - 1;
			break;
		default:
			targetIdx = childIdx + 1;
			break;
		}

		Dlog.d("scrollToIdx() firstIdx:" + firstIdx + ", lastIdx:" + lastIdx);

		switch (moveType) {
		case EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NEXT:
			targetIdx += 1;
			smoothScrollTarget = targetIdx * childSize + (targetIdx + 1) * padding - getHeight();
			break;
		case EditActivityThumbnailUtils.PAGE_MOVE_TYPE_PREV:
			targetIdx += 4;
			smoothScrollTarget = targetIdx * childSize + (targetIdx + 1) * padding - getHeight();
			break;
		default:
			targetIdx += 1;
			smoothScrollTarget = targetIdx * childSize + (targetIdx + 1) * padding - getHeight();
			break;
		}
	}

	public void scrollToSimpleIdx(int childIdx) {

		if (childIdx == firstIdx || childIdx == firstIdx + 1 || childIdx == firstIdx + 2)
			return;

		++childIdx;
		scroll = childIdx * childSize + (childIdx + 1) * padding - getHeight();
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
		int max = getChildCount() * childSize + (getChildCount() + 1) * padding - getHeight();
		return max;
	}

	public int getLastIndex() {
		return getIndexFromCoor(lastX, lastY);
	}

	// OTHER METHODS
	public void setOnRearrangeListener(OnRearrangeListener l) {
		this.onRearrangeListener = l;
	}

	public void setDragController(DragController mDragController) {
		this.mDragController = mDragController;
		this.mDragController.setOnForceMEvent(onforceMEvent);
	}
	
	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public int getMinPage() {
		return minPage;
	}

	public void setMinPage(int minPage) {
		this.minPage = minPage;
	}

	public int getSpinePage() {
		return spinePage;
	}

	public void setSpinePage(int spinePage) {
		this.spinePage = spinePage;
	}
	
	public boolean isLimitPageCount() {
		return getChildCount() <= (minPage + 2); //TODO  차일드 갯수가 0으로 들어올 수 있음..
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
	
	@Override
	public void waitFinishScroll(OnDragScrollState listener) {
		
		if(mDragScrollStateListener != null)
			mDragScrollStateListener = null;
		
		mDragScrollStateListener = listener;
	}
}