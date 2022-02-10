package com.snaps.mobile.activity.themebook;

import java.util.LinkedList;
import java.util.Queue;
 
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;
 
public class HorizontalListView extends AdapterView<ListAdapter> {
    private static final String     TAG = HorizontalListView.class.getSimpleName();
 
    public boolean mAlwaysOverrideTouch = true;
    protected ListAdapter mAdapter;
    private int mLeftViewIndex = -1;
    private int mRightViewIndex = 0;
    protected int mCurrentX;
    protected int mNextX;
    private int mMaxX = Integer.MAX_VALUE;
    private int mDisplayOffset = 0;
    protected Scroller mScroller;
    private GestureDetector mGesture;
    private Queue<View> mRemovedViewQueue = new LinkedList<View>();
    private OnItemSelectedListener mOnItemSelected;
    private OnItemClickListener mOnItemClicked;
    private OnItemLongClickListener mOnItemLongClicked;
    private boolean mDataChanged = false;
     
    public HorizontalListView(Context context) {
        super(context);
        initView();
    }
     
    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
     
    private synchronized void initView() {
        mLeftViewIndex = -1;
        mRightViewIndex = 0;
        mDisplayOffset = 0;
        mCurrentX = 0;
        mNextX = 0;
        mMaxX = Integer.MAX_VALUE;
        mScroller = new Scroller(getContext());
        mGesture = new GestureDetector(getContext(), mOnGesture);
    }
     
    @Override
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelected = listener;
    }
     
    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClicked = listener;
    }
     
    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mOnItemLongClicked = listener;
    }
 
    private DataSetObserver mDataObserver = new DataSetObserver() {
 
        @Override
        public void onChanged() {
            synchronized (HorizontalListView.this) {
                mDataChanged = true;
            }
            invalidate();
            requestLayout();
        }
 
        @Override
        public void onInvalidated() {
            reset();
            invalidate();
            requestLayout();
        }
         
    };
 
    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }
 
    @Override
    public View getSelectedView() {
        //TODO : implement
        return null;
    }
 
    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataObserver);
        reset();
    }
 
    private synchronized void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }
 
    @Override
    public void setSelection(int position) {
        //TODO : implement
    }
     
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            int     height = getMaxHeightInChilds();
             
            if (height > 0) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            }
        }
         
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
     
    private int getMaxHeightInChilds() {
        int     count = this.getChildCount();
        int     index;
        int     height = 0;
        View    child;
         
        for (index = 0; index < count; ++index) {
            child = this.getChildAt(index);
             
            if (height < child.getMeasuredHeight()) {
                height = child.getMeasuredHeight();
            }
        }
         
        return height;
    }
 
    private void addAndMeasureChild(final View child, int viewPos) {
        LayoutParams params = child.getLayoutParams();
        int     childWidth;
        int     childWidthMeasureSpec;
        int     childHeight;
        int     childHeightMeasureSpec;
         
        if (params == null) {
            params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }
 
        childWidth = getWidth();
        childWidthMeasureSpec = MeasureSpec.AT_MOST;
         
        if (params.height == LayoutParams.WRAP_CONTENT) {
            childHeight = getHeight();
            childHeightMeasureSpec = MeasureSpec.AT_MOST;
             
        } else if (params.height == LayoutParams.FILL_PARENT) {
            childHeight = getHeight();
            childHeightMeasureSpec = MeasureSpec.EXACTLY;
             
        } else {
            childHeight = params.height;
            childHeightMeasureSpec = MeasureSpec.EXACTLY;
        }
 
        /**
         *  현재는 자식 뷰의 크기는 자식 뷰별로 다 다를 수 있다.
         *  Gallery 처럼 자식 뷰의 크기(높이)를 통일 시키길 원할 경우에는
         *  그에 대한 처리가 들어가야 한다. 
         */
        addViewInLayout(child, viewPos, params, true);
        child.measure(MeasureSpec.makeMeasureSpec(childWidth, childWidthMeasureSpec),
                MeasureSpec.makeMeasureSpec(childHeight, childHeightMeasureSpec));
    }
 
    @Override
    protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
         
        if (mAdapter == null) {
            return;
        }
 
        if (mDataChanged) {
            int oldCurrentX = mCurrentX;
            initView();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }
 
        if (mScroller.computeScrollOffset()) {
            int scrollx = mScroller.getCurrX();
            mNextX = scrollx;
        }
 
        if (mNextX <= 0) {
            mNextX = 0;
            mScroller.forceFinished(true);
        }
        if (mNextX >= mMaxX) {
            mNextX = mMaxX;
            mScroller.forceFinished(true);
        }
 
        int dx = mCurrentX - mNextX;
         
        removeNonVisibleItems(dx);
        fillList(dx);
        positionItems(dx);
         
        mCurrentX = mNextX;
         
        if (!mScroller.isFinished()) {
            post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            });
 
        }
    }
     
    private void fillList(final int dx) {
        int edge = 0;
        View child = getChildAt(getChildCount() - 1);
        if (child != null) {
            edge = child.getRight();
        }
        fillListRight(edge, dx);
 
        edge = 0;
        child = getChildAt(0);
        if (child != null) {
            edge = child.getLeft();
        }
        fillListLeft(edge, dx);
    }
     
    private void fillListRight(int rightEdge, final int dx) {
        while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
 
            View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, -1);
            rightEdge += child.getMeasuredWidth();
 
            if (mRightViewIndex == mAdapter.getCount() - 1) {
                mMaxX = mCurrentX + rightEdge - getWidth();
            }
 
            if (mMaxX < 0) {
                mMaxX = 0;
            }
            mRightViewIndex++;
        }
         
    }
     
    private void fillListLeft(int leftEdge, final int dx) {
        while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
            View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, 0);
            leftEdge -= child.getMeasuredWidth();
            mLeftViewIndex--;
            mDisplayOffset -= child.getMeasuredWidth();
        }
    }
     
    private void removeNonVisibleItems(final int dx) {
        View child = getChildAt(0);
        while (child != null && child.getRight() + dx <= 0) {
            mDisplayOffset += child.getMeasuredWidth();
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mLeftViewIndex++;
            child = getChildAt(0);
             
        }
         
        child = getChildAt(getChildCount() - 1);
        while (child != null && child.getLeft() + dx >= getWidth()) {
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mRightViewIndex--;
            child = getChildAt(getChildCount() - 1);
        }
    }
     
    private void positionItems(final int dx) {
        if (getChildCount() > 0) {
            mDisplayOffset += dx;
            int left = mDisplayOffset;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int childWidth = child.getMeasuredWidth();
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
                left += childWidth;
            }
        }
    }
     
    public synchronized void scrollTo(int nX) {
        mScroller.startScroll(mNextX, 0, nX - mNextX, 0);
        requestLayout();
    }
     
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = super.dispatchTouchEvent(ev);
        handled |= mGesture.onTouchEvent(ev);
        return handled;
    }
     
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }
 
    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        synchronized (HorizontalListView.this) {
            mScroller.fling(mNextX, 0, (int)-(velocityX / 1), 0, 0, mMaxX, 0, 0);
        }
        requestLayout();
         
        return true;
    }
     
    protected boolean onDown(MotionEvent event) {
        mScroller.forceFinished(true);
        return true;
    }
     
    private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
 
        @Override
        public boolean onDown(MotionEvent event) {
            return HorizontalListView.this.onDown(event);
        }
 
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
        }
 
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
             
            synchronized (HorizontalListView.this) {
                mNextX += (int) distanceX;
            }
            requestLayout();
             
            return true;
        }
 
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (isEventWithinView(event, child)) {
                    if (mOnItemClicked != null) {
                        mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
                    }
                    if (mOnItemSelected != null) {
                        mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
                    }
                    break;
                }
                 
            }
            return true;
        }
         
        @Override
        public void onLongPress(MotionEvent event) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (isEventWithinView(event, child)) {
                    if (mOnItemLongClicked != null) {
                        mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
                    }
                    break;
                }
 
            }
        }
 
        private boolean isEventWithinView(MotionEvent event, View child) {
            Rect viewRect = new Rect();
            int[] childPosition = new int[2];
            child.getLocationOnScreen(childPosition);
            int left = childPosition[0];
            int right = left + child.getWidth();
            int top = childPosition[1];
            int bottom = top + child.getHeight();
            viewRect.set(left, top, right, bottom);
            return viewRect.contains((int) event.getRawX(), (int) event.getRawY());
        }
    };
}