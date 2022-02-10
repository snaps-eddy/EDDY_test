package com.snaps.mobile.activity.google_style_image_selector.ui.sticky;

import android.content.Context;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectGridView;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;

/**
 * Created by ysjeong on 16. 7. 26..
 */
@Deprecated
public class ImageSelectStickyLayout extends LinearLayout {
    private static final String TAG = ImageSelectStickyLayout.class.getSimpleName();
    public interface ISnapsStickyClickListener {

        int STICKY_CONTROL_ID_TITLE_BAR_BACK_KEY    = 10;

        void onSnapsStickyViewClicked(int what, SnapsMenuManager.UIStickyInfo stickyInfo);
    }

    public interface ISnapsStickyScrollChangedListener {
        void onChangedRecyclerViewScroll();
    }

    private View mTrayView;
//    private LinearLayout m_lyImageArea;
    private RelativeLayout m_lyTitleBar;
    private TextView m_tvTitleBar;
    private ImageView m_ivTitleBackBtn;
    private TextView m_tvTitleBackBtn;
    private TextView m_tvNextBtn;

    private int m_iStripTopLastOffsetY = 0;
    private int m_iTitleBarHeight = 0; //타이틀바의 높이
    private int m_iTrayViewHeight = 0; //페이저 스트랩의 높이
    private int m_iDefaultTrayViewOffsetY = 0; //스트랩 초기 위치

    private View mHidenTopArea;
    private FrameLayout mFrameMainView;
    private int m_iTopViewHeight;
    private ViewGroup mInnerScrollView;

    private String m_szMainImagePath = "";

    private boolean m_hasFocusInnerScrollView = false;
    private boolean m_isHiddenTitleArea = false;
    private boolean m_isExistTrayView = false;

    private VelocityTracker mVelocityTracker;
    private int m_iTouchSlop;
    private int m_iMaximumVelocity, m_iMinimumVelocity;
    private float m_fLastTouchY;
    private boolean m_isDragging;
    private int m_iStickOffset;
    private int m_iViewPagerMaxHeight;
    private int m_iTopViewMaxHeight;
    private int m_iStickyOffsetY = 0;
    private int m_iInnerScrollViewId = 0;
    private boolean m_isInControl = false;
    private boolean isSticky;

    private Context mContext;

    private float m_fLastInnerScrollY = 0;
    private float m_fLastStickyScrollY = 0;

    protected Handler mHandler = new Handler();
    private float m_fLastDelta = 0.f;
    private boolean m_isSuspendedHandler = true;

    private int m_iTotalRecyclerViewScrollY = 0;

    private boolean m_isEnableScoll = true;
    private boolean m_isEnableInnerScoll = true;
    private boolean m_isEnableSticky = true;

    private ISnapsStickyClickListener mClickListener = null;
    private ISnapsStickyScrollChangedListener mScrollChangeListener = null;

    public void init(Context context) {
        this.mContext = context;
        this.m_isEnableScoll = true;
        this.m_isEnableInnerScoll = true;
        this.m_isEnableSticky = true;
        this.m_iInnerScrollViewId = R.id.custom_snaps_native_super_recycler_view;
    }

    public void setEnableSticky(boolean isEnableSticky) {
        this.m_isEnableSticky = isEnableSticky;
    }

    public void setEnableScroll(boolean flag) {
        this.m_isEnableScoll = flag;
    }

    public void setEnableInnerScoll(boolean m_isEnableInnerScoll) {
        this.m_isEnableInnerScoll = m_isEnableInnerScoll;
    }

    public void setOnStickyClickListener(ISnapsStickyClickListener l) {
        this.mClickListener = l;
    }

    public void setOnStickyScrollChangeListener(ISnapsStickyScrollChangedListener listener) {
        this.mScrollChangeListener = listener;
    }

    public void setEnableTrayView(boolean isExistTrayView) {
        m_iTrayViewHeight = (int) mContext.getResources().getDimension(R.dimen.snaps_sticky_tray_view_offset_y);  //트레이 높이
        m_iTitleBarHeight = (int) mContext.getResources().getDimension(R.dimen.snaps_sticky_viewpager_strip_height); //타이틀바의 높이
        m_iDefaultTrayViewOffsetY = m_iTitleBarHeight;//(int) mContext.getResources().getDimension(R.dimen.snaps_sticky_strip_offset_y); //초기에 스탭의 위치 그림영역 - 스트랩
        m_isExistTrayView = isExistTrayView;

        if (mTrayView != null) {
            mTrayView.setVisibility(m_isExistTrayView ? View.VISIBLE : View.INVISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTrayView.getLayoutParams();
            layoutParams.topMargin = m_iDefaultTrayViewOffsetY;
        }

        if (mHidenTopArea != null) {

            int trayBottomMargin = (int) mContext.getResources().getDimension(R.dimen.snaps_sticky_tray_bottom_margin); ;

            LayoutParams layoutParams = (LayoutParams) mHidenTopArea.getLayoutParams();
            layoutParams.height = m_isExistTrayView ? (m_iDefaultTrayViewOffsetY + m_iTrayViewHeight + trayBottomMargin) : m_iDefaultTrayViewOffsetY;
            mHidenTopArea.setLayoutParams(layoutParams);
        }
    }

    public ImageSelectStickyLayout(Context context) {
        this(context, null);
        init(context);
    }

    public ImageSelectStickyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public ImageSelectStickyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);

        mVelocityTracker = VelocityTracker.obtain();
        m_iTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        m_iMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        m_iMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
        init(context);
    }

    public void initOffset() {
        try {
            RelativeLayout.LayoutParams lpTrayView = (RelativeLayout.LayoutParams) mTrayView.getLayoutParams();
            m_fLastInnerScrollY = 0;
            lpTrayView.topMargin = m_iTitleBarHeight;

            RelativeLayout.LayoutParams lpTitleBar = (RelativeLayout.LayoutParams) m_lyTitleBar.getLayoutParams();
            lpTitleBar.topMargin = 0;

            mTrayView.setLayoutParams(lpTrayView);
            m_lyTitleBar.setLayoutParams(lpTitleBar);

            scrollTo(0, 0);

            checkScrollState();

            if (mInnerScrollView != null && mInnerScrollView instanceof SnapsSuperRecyclerView) {
                m_iTotalRecyclerViewScrollY = 0;
            }
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    public void releaseInstances() {
        ImageUtil.recycleBitmap(m_ivTitleBackBtn);
    }

    private ImageSelectStickyLayout.onStickStateChangeListener onStickStateChangeListener = new ImageSelectStickyLayout.onStickStateChangeListener() {
        @Override
        public void isStick(boolean isStick) {}

        /**
         * @param percent : 완전히 열려 있을 때, 0 위로 올렸을때 1
         */
        @Override
        public void scrollPercent(float percent) {}

        @Override
        public void onInnerScrollChanged(float y) {
            if (!m_isEnableSticky) return;

            RelativeLayout.LayoutParams lpTrayView = (RelativeLayout.LayoutParams) mTrayView.getLayoutParams();
            RelativeLayout.LayoutParams lpTitleBar = (RelativeLayout.LayoutParams) m_lyTitleBar.getLayoutParams();

            int move = (int) (y - m_fLastInnerScrollY);
            int movedOffsetY = lpTrayView.topMargin - move;
            int movedOffsetYY = lpTitleBar.topMargin - move;
            int topMargin = UIUtil.convertDPtoPX(mContext, 15);

            lpTrayView.topMargin = movedOffsetY;
            lpTitleBar.topMargin = movedOffsetYY;

            if (lpTrayView.topMargin < topMargin) {
                lpTrayView.topMargin = topMargin;
            }

            if (lpTrayView.topMargin >= m_iDefaultTrayViewOffsetY) {
                lpTrayView.topMargin = m_iDefaultTrayViewOffsetY;
            }

            if (lpTitleBar.topMargin < -(m_iTitleBarHeight - topMargin)) {
                lpTitleBar.topMargin = -((m_iTitleBarHeight - topMargin));
            }

            if (lpTitleBar.topMargin > 0) {
                lpTitleBar.topMargin = 0;
            }

            m_lyTitleBar.setLayoutParams(lpTitleBar);
            mTrayView.setLayoutParams(lpTrayView);

            m_fLastInnerScrollY = y;

            if (mScrollChangeListener != null) {
                mScrollChangeListener.onChangedRecyclerViewScroll();
            }
        }

        @Override
        public void onStickyScrollChanged(float y) {
            if (!m_isEnableSticky || mTrayView == null || m_lyTitleBar == null) return;

            RelativeLayout.LayoutParams lpTrayView = (RelativeLayout.LayoutParams) mTrayView.getLayoutParams();
            RelativeLayout.LayoutParams lpTitle= (RelativeLayout.LayoutParams) m_lyTitleBar.getLayoutParams();

            int move = (int) (y - m_fLastStickyScrollY);
            int movedOffsetY = lpTrayView.topMargin - move;
            int movedOffsetYY = lpTitle.topMargin - move;
            int topMargin = UIUtil.convertDPtoPX(mContext, 15);

                lpTrayView.topMargin = movedOffsetY;
                lpTitle.topMargin = movedOffsetYY;

            if (lpTrayView.topMargin < topMargin) {
                lpTrayView.topMargin = topMargin;
            }

            if (lpTrayView.topMargin > m_iDefaultTrayViewOffsetY) {
                lpTrayView.topMargin = m_iDefaultTrayViewOffsetY;
            }

            if (lpTitle.topMargin < -(m_iTitleBarHeight - topMargin)) {
                lpTitle.topMargin = -((m_iTitleBarHeight - topMargin));
            }

            if (lpTitle.topMargin > 0) {
                lpTitle.topMargin = 0;
            }

            m_lyTitleBar.setLayoutParams(lpTitle);
            mTrayView.setLayoutParams(lpTrayView);
            m_fLastStickyScrollY = y;

            if (mScrollChangeListener != null) {
                mScrollChangeListener.onChangedRecyclerViewScroll();
            }
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

//        mHidenTopArea = findViewById(R.id.google_photo_style_sticky_topview);
        View view = findViewById(R.id.google_photo_style_image_select_frame_main_ly);
        if (!(view instanceof FrameLayout)) {
            throw new RuntimeException(
                    "show used by FramgLayout !");
        }
        mFrameMainView = (FrameLayout) view;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams params = mFrameMainView.getLayoutParams();
        int height = getMeasuredHeight();
        m_iViewPagerMaxHeight = (height >= m_iViewPagerMaxHeight ? height : m_iViewPagerMaxHeight);
        params.height = /*m_iViewPagerMaxHeight - m_iStickOffset*/height;
        mFrameMainView.setLayoutParams(params);

        int topHeight = mHidenTopArea.getMeasuredHeight();
        ViewGroup.LayoutParams topParams = mHidenTopArea.getLayoutParams();

        m_iTopViewMaxHeight = (topHeight >= m_iTopViewMaxHeight ? topHeight : m_iTopViewMaxHeight);
        topParams.height = /*m_iTopViewMaxHeight*/topHeight;
        mHidenTopArea.setLayoutParams(topParams);

        m_iTopViewHeight = topParams.height- m_iStickOffset;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final ViewGroup.LayoutParams params = mHidenTopArea.getLayoutParams();
        mHidenTopArea.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mHidenTopArea instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) mHidenTopArea;
                    if (viewGroup == null || viewGroup.getChildCount() < 1) return;

                    View child = viewGroup.getChildAt(0);
                    if (child == null) return;

                    int height = child.getHeight();
                    m_iTopViewHeight = height - m_iStickOffset;
                    params.height = height;
                    mHidenTopArea.setLayoutParams(params);
                    mHidenTopArea.requestLayout();
                } else {
                    m_iTopViewHeight = mHidenTopArea.getMeasuredHeight() - m_iStickOffset;
                }
            }
        }, 200);
    }

    private int getTopOffset() {
        return 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                m_fLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - m_fLastTouchY;
                getCurrentScrollView();

                if (mInnerScrollView instanceof ScrollView || mInnerScrollView instanceof WebView) {
                    //다시 내릴 때..
                    if (mInnerScrollView.getScrollY() == getTopOffset() && m_hasFocusInnerScrollView && dy > getTopOffset()
                            && !m_isInControl) {
                        m_isInControl = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        isSticky = true;
                        return dispatchTouchEvent(ev2);
                    }
                } else if (mInnerScrollView instanceof ImageSelectGridView) {
                    ImageSelectGridView gridView = (ImageSelectGridView) mInnerScrollView;
                    if (!m_isInControl && gridView.computeVerticalScrollOffset() == getTopOffset() && m_hasFocusInnerScrollView
                            && dy > getTopOffset()) {
                        m_isInControl = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        isSticky = true;
                        return dispatchTouchEvent(ev2);
                    }
                } else if (mInnerScrollView instanceof SnapsRecyclerView) {

                    RecyclerView rv = ((SnapsRecyclerView) mInnerScrollView);
                    if (!m_isInControl && rv.computeVerticalScrollOffset() == getTopOffset() && m_hasFocusInnerScrollView
                            && dy > getTopOffset()) {
                        m_isInControl = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        isSticky = true;
                        return dispatchTouchEvent(ev2);
                    }
                } else if (mInnerScrollView instanceof SnapsSuperRecyclerView) {

                    RecyclerView rv = ((SnapsSuperRecyclerView) mInnerScrollView).getRecyclerView();
                    if (!m_isInControl && rv.computeVerticalScrollOffset() == getTopOffset() && m_hasFocusInnerScrollView
                            && dy > getTopOffset()) {
                        m_isInControl = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        isSticky = true;
                        return dispatchTouchEvent(ev2);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float distance = y - m_fLastTouchY;
                if (isSticky && /*distance==0.0f*/Math.abs(distance) <= m_iTouchSlop) {
                    isSticky = false;
                    return true;
                } else {
                    isSticky = false;
                    return super.dispatchTouchEvent(ev);
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                m_fLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - m_fLastTouchY;

                getCurrentScrollView();

                if (Math.abs(dy) > m_iTouchSlop) {
                    m_isDragging = true;
                    if (mInnerScrollView instanceof ScrollView || mInnerScrollView instanceof WebView) {
                        //스크롤이 내려가 있는데, 안에 스크롤이 최상단이 아니면, 안에 있는 스크롤한테 포커싱을 준다.
                        if (!m_hasFocusInnerScrollView && dy > 0) {
                            if (mInnerScrollView.getScrollY() > 0) {
                                return false;
                            }
                        }

                        //올라가는 시점 (스크롤이 걸리는 시점)
                        if (!m_hasFocusInnerScrollView
                                || (mInnerScrollView.getScrollY() == getTopOffset() && dy > getTopOffset())) {
                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            m_fLastTouchY = y;
                            return true;
                        }
                    } else if (mInnerScrollView instanceof ImageSelectGridView) {
                        ImageSelectGridView gridView = (ImageSelectGridView) mInnerScrollView;

                        //스크롤이 내려가 있는데, 안에 스크롤이 최상단이 아니면, 안에 있는 스크롤한테 포커싱을 준다.
                        if (!m_hasFocusInnerScrollView && dy > 0) {
                            if (gridView.computeVerticalScrollOffset() > 0) {
                                return false;
                            }
                        }

                        if (!m_hasFocusInnerScrollView || (gridView.computeVerticalScrollOffset() == getTopOffset() && dy > 0)) {
                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            m_fLastTouchY = y;
                            return true;
                        }
                    } else if (mInnerScrollView instanceof SnapsRecyclerView) {

                        RecyclerView rv = ((SnapsRecyclerView) mInnerScrollView);

                        //스크롤이 내려가 있는데, 안에 스크롤이 최상단이 아니면, 안에 있는 스크롤한테 포커싱을 준다.
                        if (!m_hasFocusInnerScrollView && dy > 0) {
                            if (androidx.core.view.ViewCompat.canScrollVertically(rv, -1)) {
                                return false;
                            }
                        }

                        if (!m_hasFocusInnerScrollView || (rv.computeVerticalScrollOffset() == getTopOffset()  && dy > 0)) {
                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            m_fLastTouchY = y;
                            return true;
                        }
                    } else if (mInnerScrollView instanceof SnapsSuperRecyclerView) {

                        RecyclerView rv = ((SnapsSuperRecyclerView) mInnerScrollView).getRecyclerView();

                        //스크롤이 내려가 있는데, 안에 스크롤이 최상단이 아니면, 안에 있는 스크롤한테 포커싱을 준다.
                        if (!m_hasFocusInnerScrollView && dy > 0) {
                            if (androidx.core.view.ViewCompat.canScrollVertically(rv, -1)) {
                                return false;
                            }
                        }

                        if (!m_hasFocusInnerScrollView || (rv.computeVerticalScrollOffset() == getTopOffset()  && dy > 0)) {
                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            m_fLastTouchY = y;
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                m_isDragging = false;
                recycleVelocityTracker();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private int getInnerScrollViewId() {
        return m_iInnerScrollViewId;
    }

    public void setInnerScrollViewId(int innerScrollViewId) {
        this.m_iInnerScrollViewId = innerScrollViewId;

        if (mFrameMainView == null) return;

        mInnerScrollView = (ViewGroup) mFrameMainView.findViewById(innerScrollViewId);
        if (mInnerScrollView != null && mInnerScrollView instanceof SnapsSuperRecyclerView) {
            ((SnapsSuperRecyclerView) mInnerScrollView).setIsAddedScrollListener(false);
        }
    }

    private void getCurrentScrollView() {
        int innerScrollViewId = getInnerScrollViewId();
        mInnerScrollView = (ViewGroup) mFrameMainView.findViewById(innerScrollViewId);

        if (mInnerScrollView != null) {

            boolean isAddedViewTreeObserver = false;
            SnapsSuperRecyclerView superRecyclerView = null;
            if (mInnerScrollView instanceof SnapsSuperRecyclerView) {
                superRecyclerView = ((SnapsSuperRecyclerView) mInnerScrollView);
                isAddedViewTreeObserver = superRecyclerView.isAddedViewTreeObserver();
            }

            if (!isAddedViewTreeObserver) {
                mInnerScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (m_hasFocusInnerScrollView) {
                            if (onStickStateChangeListener != null && mInnerScrollView != null) {
                                if (mInnerScrollView instanceof SnapsSuperRecyclerView) {
                                    RecyclerView rv = ((SnapsSuperRecyclerView) mInnerScrollView).getRecyclerView();
                                    if (rv != null) {
                                        onStickStateChangeListener.onInnerScrollChanged(m_iTotalRecyclerViewScrollY);
                                    }
                                } else
                                    onStickStateChangeListener.onInnerScrollChanged(mInnerScrollView.getScrollY());
                            }
                        }
                    }
                });

                if (superRecyclerView != null) {
                    superRecyclerView.setIsAddedViewTreeObserver(true);
                }
            }

            if (superRecyclerView != null) {
                if (!superRecyclerView.isAddedScrollListener()) {
                    RecyclerView rv = superRecyclerView.getRecyclerView();
                    if (rv != null) {
                        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                m_iTotalRecyclerViewScrollY += dy;
                            }
                        });
                        superRecyclerView.setIsAddedScrollListener(true);
                    }
                }
            }
        }

    }

    private void suspendHandler() {
        m_fLastDelta = 0;
        m_isSuspendedHandler = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!m_isEnableScoll)
            return false;

        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                suspendHandler();
                m_fLastTouchY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - m_fLastTouchY;

                if (!m_isDragging && Math.abs(dy) > m_iTouchSlop) {
                    m_isDragging = true;
                }
                if (m_isDragging) {
                    scrollBy(0, (int) -dy);

                    if (getScrollY() == m_iStickyOffsetY && dy < getTopOffset()) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        m_isInControl = false;
                        isSticky = true;
                    } else {
                        isSticky = false;
                    }

                    m_fLastDelta = m_fLastTouchY - (int) event.getY();
                }
                m_fLastTouchY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
                m_isDragging = false;
                recycleVelocityTracker();
                suspendHandler();
                break;
            case MotionEvent.ACTION_UP:

                m_isDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, m_iMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > m_iMinimumVelocity) {
                    fling(-velocityY);
                }
                recycleVelocityTracker();

                break;
        }

        return super.onTouchEvent(event);
    }

    private Runnable mUpdateTask = new Runnable() {
        public void run() {
            if (m_isSuspendedHandler) return;

            if (m_fLastDelta != 0) {
                m_fLastDelta *= .92f;
                if (Math.abs(m_fLastDelta) < 1)
                    m_fLastDelta = 0;

                scrollBy(0, (int) m_fLastDelta);
            }

            if (m_fLastDelta != 0)
                mHandler.postDelayed(this, 5);
        }
    };

    /**
     *    public void fling(int startX, int startY, int velocityX, int velocityY,
     int minX, int maxX, int minY, int maxY) {
     * @param velocityY
     */
    public void fling(int velocityY) {
        if (m_fLastDelta != 0) {

            //가중치를 둔다.
            float weight = velocityY / 10000.f;
            weight = Math.max(1, Math.min(3, 1 + weight));

            m_fLastDelta *= weight;

            m_isSuspendedHandler = false;
            mHandler.post(mUpdateTask);
        }

        invalidate();
    }

    private void checkScrollState() {
        /**
         * m_iTopViewHeight :  페이져 위에 그림 영역+스트랩 높이로 만들어놓은 뷰
         * m_iStickyOffsetY : 스크롤의 포커스 주인이 전환되는 시점.
         * m_isHiddenTopPhotoArea : 사진 영역이 가려진 상태
         * m_isHiddenTopTitleArea : 타이틀 영역이 가려진 상태
         */
        m_iStickyOffsetY = m_iTopViewHeight;

        m_hasFocusInnerScrollView = getScrollY() >= m_iStickyOffsetY;
    }

    @Override
    public void scrollTo(int x, int y) {

        checkScrollState();

        if (y < 0) {
            y = 0;
        }

        if (y > m_iStickyOffsetY) {
            y = m_iStickyOffsetY;
        }

        if (y != getScrollY()) {
            if (onStickStateChangeListener != null)
                onStickStateChangeListener.onStickyScrollChanged(y);

            super.scrollTo(x, y);
        }

        if (onStickStateChangeListener != null) {
            onStickStateChangeListener.isStick(m_hasFocusInnerScrollView);

            int topOffset = m_iStickyOffsetY - m_iTitleBarHeight - m_iTrayViewHeight;

            onStickStateChangeListener.scrollPercent(Math.min(1, getScrollY() / (float) topOffset));
        }
    }

    @Override
    public void computeScroll() {
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public interface onStickStateChangeListener {
        void isStick(boolean isStick);

        void scrollPercent(float percent);

        void onStickyScrollChanged(float y);

        void onInnerScrollChanged(float y);
    }

    public void setStickyControls(ImageSelectStickyControls stickyControls ) {
        if (stickyControls == null) return;

        this.mTrayView = stickyControls.getTrayView();
        this.m_lyTitleBar = stickyControls.getLyTitleBar();
        this.m_tvTitleBar = stickyControls.getTvTitleBarText();
        this.m_ivTitleBackBtn = stickyControls.getIvTitleBackBtn();
        this. m_tvTitleBackBtn = stickyControls.getTvTitleBackBtn();
        this. m_tvNextBtn = stickyControls.getTvNextBtn();
    }

    public void initStickyControls(final SnapsMenuManager.UIStickyInfo stickyInfo) {
        if (m_ivTitleBackBtn != null) {
            m_ivTitleBackBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onSnapsStickyViewClicked(ISnapsStickyClickListener.STICKY_CONTROL_ID_TITLE_BAR_BACK_KEY, stickyInfo);
                    }
                }
            });
        }

        m_szMainImagePath = stickyInfo.getStickyImage();

        if (onStickStateChangeListener != null)
            onStickStateChangeListener.scrollPercent(0);
    }
}
