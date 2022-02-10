package com.snaps.common.customui.sticky;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStripForSticky;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;

/**
 * Created by ysjeong on 16. 7. 26..
 */
@Deprecated
public class SnapsStickyLayout extends LinearLayout {
    private static final String TAG = SnapsStickyLayout.class.getSimpleName();

    public interface ISnapsStickyClickListener {

        int STICKY_CONTROL_ID_TITLE_BAR_BACK_KEY = 10;
        int STICKY_CONTROL_ID_TITLE_BAR_MENU = 11;
        int STICKY_CONTROL_ID_TITLE_BAR_INFO = 12;
        int STICKY_CONTROL_ID_STICKY_AREA_INFO = 13;

        void onSnapsStickyViewClicked(int what, SnapsMenuManager.UIStickyInfo stickyInfo);
    }

    private static final int HIDE_AREA_FREE_MARGIN_DP = 40; //위로 올라간 타이틀바를 조금 더 올라가게 보정한다.

    private PagerSlidingTabStripForSticky mPagerStrip;
    private LinearLayout m_lyImageArea;
    private RelativeLayout m_lyTitleBar;
    private TextView m_tvTitleBar;
    private TextView m_tvStickyTitle;
    private TextView m_tvStickyDesc;
    private TextView m_tvStickyInfo;
    private TextView m_tvStickyInfoFake;
    private ImageView m_ivTitleBackBtn;
    private ImageView m_ivTitleMenuBtn;
    private ImageView m_ivTitleInfoBtn;
    private ImageView m_ivMainImage;

    private int m_iStripTopLastOffsetY = 0;
    private int m_iTitleBarHeight = 0; //타이틀바의 높이
    private int m_iPagerStripHeight = 0; //페이저 스트랩의 높이
    private int m_iDefaultPagerStripOffsetY = 0; //스트랩 초기 위치
    private int m_iHideFreeMargin = 0;

    private View mHidenTopArea;
    private ViewPager mViewPager;
    private int m_iTopViewHeight;
    private ViewGroup mInnerScrollView;

    private String m_szMainImagePath = "";

    private boolean m_hasFocusInnerScrollView = false;
    private boolean m_isHiddenTitleArea = false;
    private boolean m_isExistPagerStrip = false;

    private VelocityTracker mVelocityTracker;
    private int m_iTouchSlop;
    private int m_iMaximumVelocity, m_iMinimumVelocity;
    private float m_fLastTouchY;
    private boolean m_isDragging;
    private int m_iStickOffset;
    private int m_iViewPagerMaxHeight;
    private int m_iTopViewMaxHeight;
    private int m_iStickyOffsetY = 0;
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

    private ISnapsStickyClickListener mClickListener = null;

    public void init(Context context) {
        this.mContext = context;
        this.m_iHideFreeMargin = UIUtil.convertDPtoPX(mContext, HIDE_AREA_FREE_MARGIN_DP);
        this.m_isEnableScoll = true;
    }

    public void setEnableScroll(boolean flag) {
        this.m_isEnableScoll = flag;
    }

    public void setOnStickyClickListener(ISnapsStickyClickListener l) {
        this.mClickListener = l;
    }

    public void setEnablePagerStrip(boolean isExistPagerStrip) {
        m_iPagerStripHeight = (int) mContext.getResources().getDimension(R.dimen.snaps_sticky_title_height);  //페이저 스트랩의 높이
        m_iTitleBarHeight = (int) mContext.getResources().getDimension(R.dimen.snaps_sticky_viewpager_strip_height); //타이틀바의 높이
        m_iDefaultPagerStripOffsetY = (int) mContext.getResources().getDimension(R.dimen.snaps_sticky_strip_offset_y); //초기에 스탭의 위치 그림영역 - 스트랩
        m_isExistPagerStrip = isExistPagerStrip;

        if (mPagerStrip != null) {
            mPagerStrip.setVisibility(m_isExistPagerStrip ? View.VISIBLE : View.INVISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPagerStrip.getLayoutParams();
            layoutParams.topMargin = m_iDefaultPagerStripOffsetY;
        }

        if (mHidenTopArea != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mHidenTopArea.getLayoutParams();
            layoutParams.height = m_isExistPagerStrip ? (m_iDefaultPagerStripOffsetY + m_iPagerStripHeight) : m_iDefaultPagerStripOffsetY;
            mHidenTopArea.setLayoutParams(layoutParams);
        }

    }

    public SnapsStickyLayout(Context context) {
        this(context, null);
        init(context);
    }

    public SnapsStickyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public SnapsStickyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void initLastOffset(int prevPos, int curPos) {

        try {
            RelativeLayout.LayoutParams lpStrip = (RelativeLayout.LayoutParams) mPagerStrip.getLayoutParams();
            if (lpStrip.topMargin <= -m_iPagerStripHeight) {
                m_fLastInnerScrollY = 0;

                lpStrip.topMargin = 0;

                RelativeLayout.LayoutParams lpTitleBar = (RelativeLayout.LayoutParams) m_lyTitleBar.getLayoutParams();
                lpTitleBar.topMargin = lpStrip.topMargin - m_iTitleBarHeight;

                mPagerStrip.setLayoutParams(lpStrip);
                m_lyTitleBar.setLayoutParams(lpTitleBar);

                m_fLastStickyScrollY = getScrollY() - m_iPagerStripHeight;

                scrollTo(0, m_iTopViewHeight - m_iPagerStripHeight);

                m_fLastStickyScrollY = getScrollY();

                checkScrollState();

                if (mInnerScrollView != null && mInnerScrollView instanceof SnapsSuperRecyclerView) {
                    m_iTotalRecyclerViewScrollY = 0;
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void releaseInstances() {
        ImageUtil.recycleBitmap(m_ivMainImage);
        ImageUtil.recycleBitmap(m_ivTitleBackBtn);
        ImageUtil.recycleBitmap(m_ivTitleMenuBtn);
        ImageUtil.recycleBitmap(m_ivTitleInfoBtn);
    }

    private SnapsStickyLayout.onStickStateChangeListener onStickStateChangeListener = new SnapsStickyLayout.onStickStateChangeListener() {
        @Override
        public void isStick(boolean isStick) {
        }

        /**
         * @param percent : 완전히 열려 있을 때, 0 위로 올렸을때 1
         */
        @Override
        public void scrollPercent(float percent) {

            View[] arrStickyAreaControls = {
                    m_tvStickyTitle, m_tvStickyDesc, m_tvStickyInfo
            };

            View[] arrTitleControls = {
                    m_ivTitleBackBtn, m_ivTitleMenuBtn
            };

            //타이틀 바 배경 처리
            if (m_lyTitleBar != null) {
                int per = (int) (255 * percent);
                per = per >= 255 ? 255 : 0;
                m_lyTitleBar.setBackgroundColor(Color.argb(per, 255, 255, 255));
            }

            //이미지 영역 배경 처리
            if (m_lyImageArea != null) {
                int per = (int) (255 * percent);
                m_lyImageArea.setBackgroundColor(Color.argb(per, 255, 255, 255));
            }

            boolean isVisibilityTitleText = mPagerStrip.getY() < m_tvStickyInfo.getY(); //Info 버튼을 기준으로 서서히 알파값을 올린다.
            if (m_tvTitleBar != null) {
                float titleBarPercent = percent;
                if (isVisibilityTitleText) {
                    titleBarPercent -= .5f;
                    titleBarPercent = Math.min(1, Math.max(0, titleBarPercent * 2.f));
                } else {
                    titleBarPercent = 0.f;
                }

                int color = (int) (255 * (1 - titleBarPercent));

                m_tvTitleBar.setTextColor(Color.rgb(color, color, color));

                m_tvTitleBar.setAlpha(titleBarPercent);
            }

            //타이틀 영역의 i버튼
            boolean isVisibilityTitleInfoButton = mPagerStrip.getY() < m_tvStickyInfo.getY();
            if (m_ivTitleInfoBtn != null) {
                float titleInfoPercent = percent;
                if (isVisibilityTitleInfoButton) {
                    titleInfoPercent -= .5f;
                    titleInfoPercent = Math.min(1, Math.max(0, titleInfoPercent * 2.f));
                } else {
                    titleInfoPercent = 0.f;
                }

                m_ivTitleInfoBtn.setAlpha(titleInfoPercent);
                int color = (int) (255 * (1 - titleInfoPercent));
                m_ivTitleInfoBtn.setColorFilter(Color.rgb(color, color, color));
            }

            //이미지 영역의 텍스트들 처리
            for (View view : arrStickyAreaControls) {
                if (view == null) {
                    continue;
                }
                view.setAlpha(1 - percent);
            }

            //타이틀 영역의 버튼들 처리
            for (View view : arrTitleControls) {
                if (view == null || !(view instanceof ImageView)) {
                    continue;
                }
                int color = (int) (255 * (1 - percent));
                ((ImageView) view).setColorFilter(Color.rgb(color, color, color));
            }

        }

        @Override
        public void onInnerScrollChanged(float y) {
            int move = (int) (y - m_fLastInnerScrollY);

            RelativeLayout.LayoutParams lpStrip = (RelativeLayout.LayoutParams) mPagerStrip.getLayoutParams();
            lpStrip.topMargin -= move;

            if (lpStrip.topMargin < 0) {
                lpStrip.topMargin = 0;
            }

            if (lpStrip.topMargin >= m_iTitleBarHeight) {
                lpStrip.topMargin = m_iTitleBarHeight;
            }

            mPagerStrip.setLayoutParams(lpStrip);

            m_iStripTopLastOffsetY = lpStrip.topMargin;

            RelativeLayout.LayoutParams lpTitleBar = (RelativeLayout.LayoutParams) m_lyTitleBar.getLayoutParams();
            lpTitleBar.topMargin -= move;

            if (lpTitleBar.topMargin < -m_iTitleBarHeight) {
                lpTitleBar.topMargin = -m_iTitleBarHeight;
            }

            if (lpTitleBar.topMargin > 0) {
                lpTitleBar.topMargin = 0;
            }

            m_lyTitleBar.setLayoutParams(lpTitleBar);

            m_fLastInnerScrollY = y;

            m_isHiddenTitleArea = true;
        }

        @Override
        public void onStickyScrollChanged(float y) {
            RelativeLayout.LayoutParams lpStrip = (RelativeLayout.LayoutParams) mPagerStrip.getLayoutParams();
            RelativeLayout.LayoutParams lpTitle = (RelativeLayout.LayoutParams) m_lyTitleBar.getLayoutParams();

            int move = (int) (y - m_fLastStickyScrollY);
            int movedOffsetY = lpStrip.topMargin - move;

            if (m_isHiddenTitleArea) { //타이틀바가 완전히 가려진 상태에서 내려올 때
                if (m_iDefaultPagerStripOffsetY - y > m_iStripTopLastOffsetY) {
                    lpStrip.topMargin = movedOffsetY;
                    m_isHiddenTitleArea = false;
                }
            } else {
                lpStrip.topMargin = movedOffsetY;
            }

            if (lpStrip.topMargin < 0) {
                lpStrip.topMargin = 0;
            }

            if (lpStrip.topMargin > m_iDefaultPagerStripOffsetY) {
                lpStrip.topMargin = m_iDefaultPagerStripOffsetY;
            }

            lpTitle.topMargin = lpStrip.topMargin - m_iTitleBarHeight;

            if (lpTitle.topMargin < -m_iTitleBarHeight) {
                lpTitle.topMargin = -m_iTitleBarHeight;
            }

            if (lpTitle.topMargin > 0) {
                lpTitle.topMargin = 0;
            }

            m_lyTitleBar.setLayoutParams(lpTitle);
            mPagerStrip.setLayoutParams(lpStrip);
            m_fLastStickyScrollY = y;
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHidenTopArea = findViewById(R.id.snpas_sticky_id_topview);
        View view = findViewById(R.id.snpas_sticky_id_viewpager);
        if (!(view instanceof ViewPager)) {
            throw new RuntimeException(
                    "id_stickynavlayout_viewpager show used by ViewPager !");
        } else if (mHidenTopArea instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) mHidenTopArea;
            if (viewGroup.getChildCount() >= 2) {
                throw new RuntimeException(
                        "if the TopView(android:id=\"R.id.id_stickynavlayout_topview\") is a ViewGroup(ScrollView,LinearLayout,FrameLayout, ....) ,the children count should be one  !");
            }
        }
        mViewPager = (ViewPager) view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        int height = getMeasuredHeight();
        m_iViewPagerMaxHeight = (height >= m_iViewPagerMaxHeight ? height : m_iViewPagerMaxHeight);
        params.height = /*m_iViewPagerMaxHeight - m_iStickOffset*/height;
        mViewPager.setLayoutParams(params);

        int topHeight = mHidenTopArea.getMeasuredHeight();
        ViewGroup.LayoutParams topParams = mHidenTopArea.getLayoutParams();

        m_iTopViewMaxHeight = (topHeight >= m_iTopViewMaxHeight ? topHeight : m_iTopViewMaxHeight);
        topParams.height = /*m_iTopViewMaxHeight*/topHeight;
        mHidenTopArea.setLayoutParams(topParams);

        m_iTopViewHeight = topParams.height - m_iStickOffset;

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
                    int height = viewGroup.getChildAt(0).getHeight();
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
                    } else if (mInnerScrollView instanceof SnapsSuperRecyclerView) {

                        RecyclerView rv = ((SnapsSuperRecyclerView) mInnerScrollView).getRecyclerView();

                        //스크롤이 내려가 있는데, 안에 스크롤이 최상단이 아니면, 안에 있는 스크롤한테 포커싱을 준다.
                        if (!m_hasFocusInnerScrollView && dy > 0) {
                            if (androidx.core.view.ViewCompat.canScrollVertically(rv, -1)) {
                                return false;
                            }
                        }

                        if (!m_hasFocusInnerScrollView || (rv.computeVerticalScrollOffset() == getTopOffset() && dy > 0)) {
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

    private void getCurrentScrollView() {
        int currentItem = mViewPager.getCurrentItem();
        PagerAdapter a = mViewPager.getAdapter();
        if (a == null) {
            return;
        }

        int innerScrollViewId = R.id.custom_snaps_native_super_recycler_view; //FIXME

        if (a instanceof FragmentPagerAdapter) {
            FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
            Fragment item = fadapter.getItem(currentItem);
            View v = item.getView();
            if (v != null) {
                mInnerScrollView = (ViewGroup) (v
                        .findViewById(innerScrollViewId));
            }
        } else if (a instanceof FragmentStatePagerAdapter) {
            FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
            Fragment item = fsAdapter.getItem(currentItem);
            View v = item.getView();
            if (v != null) {
                mInnerScrollView = (ViewGroup) (v
                        .findViewById(innerScrollViewId));
            }
        } else {
            throw new RuntimeException(
                    "mViewPager  should be  used  FragmentPagerAdapter or  FragmentStatePagerAdapter  !");
        }

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
                                } else {
                                    onStickStateChangeListener.onInnerScrollChanged(mInnerScrollView.getScrollY());
                                }
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
        if (!m_isEnableScoll) {
            return false;
        }

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
            if (m_isSuspendedHandler) {
                return;
            }

            if (m_fLastDelta != 0) {
                m_fLastDelta *= .92f;
                if (Math.abs(m_fLastDelta) < 1) {
                    m_fLastDelta = 0;
                }

                scrollBy(0, (int) m_fLastDelta);
            }

            if (m_fLastDelta != 0) {
                mHandler.postDelayed(this, 5);
            }
        }
    };

    /**
     * public void fling(int startX, int startY, int velocityX, int velocityY,
     * int minX, int maxX, int minY, int maxY) {
     *
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
            if (onStickStateChangeListener != null) {
                onStickStateChangeListener.onStickyScrollChanged(y);
            }

            super.scrollTo(x, y);
        }

        if (onStickStateChangeListener != null) {
            onStickStateChangeListener.isStick(m_hasFocusInnerScrollView);

            int topOffset = m_iStickyOffsetY - m_iTitleBarHeight - m_iPagerStripHeight;

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

    public void setStickyControls(StickyControls stickyControls) {
        if (stickyControls == null) {
            return;
        }
        this.mPagerStrip = stickyControls.getPagerSlidingTabStripForSticky();
        this.m_lyTitleBar = stickyControls.getTitleLayout();
        this.m_tvTitleBar = stickyControls.getTitleText();
        this.m_tvStickyTitle = stickyControls.getStickyTitle();
        this.m_tvStickyDesc = stickyControls.getStickyDesc();
        this.m_tvStickyInfo = stickyControls.getStickyInfo();
        this.m_tvStickyInfoFake = stickyControls.getStickyInfoFake();
        this.m_ivTitleInfoBtn = stickyControls.getInfoIcon();
        this.m_ivTitleBackBtn = stickyControls.getBackKey();
        this.m_ivTitleMenuBtn = stickyControls.getMenuKey();
        this.m_ivMainImage = stickyControls.getMainImage();
        this.m_lyImageArea = stickyControls.getImageLayout();

        //font
        FontUtil.applyTextViewTypeface(m_tvStickyTitle, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
        FontUtil.applyTextViewTypeface(m_tvStickyDesc, FontUtil.eSnapsFonts.YOON_GOTHIC_720);
        FontUtil.applyTextViewTypeface(m_tvStickyInfo, FontUtil.eSnapsFonts.YOON_GOTHIC_740);
        FontUtil.applyTextViewTypeface(m_tvTitleBar, FontUtil.eSnapsFonts.YOON_GOTHIC_760);
    }

    public void initStickyControls(final SnapsMenuManager.UIStickyInfo stickyInfo) {
        if (stickyInfo == null) {
            return;
        }
        if (m_tvStickyTitle != null) {
            m_tvStickyTitle.setText(stickyInfo.getTitle());
        }
        if (m_tvStickyDesc != null) {
            m_tvStickyDesc.setText(stickyInfo.getTopic());
        }
        if (m_tvStickyInfo != null) {
            if (!Config.useKorean()) {
                m_tvStickyInfo.setVisibility(View.GONE);
            } else {
                m_tvStickyInfo.setText(mContext.getResources().getString(R.string.product_comment));
                if (m_tvStickyInfoFake != null) {
                    m_tvStickyInfoFake.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (m_ivTitleInfoBtn != null && m_ivTitleInfoBtn.getAlpha() <= 0.f) {
                                if (mClickListener != null) {
                                    mClickListener.onSnapsStickyViewClicked(ISnapsStickyClickListener.STICKY_CONTROL_ID_STICKY_AREA_INFO, stickyInfo);
                                }
                            }
                        }
                    });
                }
            }
        }

        if (m_ivTitleInfoBtn != null) {
            if (!Config.useKorean()) {
                m_ivTitleInfoBtn.setVisibility(View.GONE);
            } else {
                m_ivTitleInfoBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (m_ivTitleInfoBtn.getAlpha() > .1f) {
                            if (mClickListener != null) {
                                mClickListener.onSnapsStickyViewClicked(ISnapsStickyClickListener.STICKY_CONTROL_ID_TITLE_BAR_INFO, stickyInfo);
                            }
                        }
                    }
                });
            }
        }

        if (m_ivTitleMenuBtn != null) {
            m_ivTitleMenuBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onSnapsStickyViewClicked(ISnapsStickyClickListener.STICKY_CONTROL_ID_TITLE_BAR_MENU, stickyInfo);
                    }
                }
            });
        }

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

        loadMainImage();

        if (onStickStateChangeListener != null) {
            onStickStateChangeListener.scrollPercent(0);
        }
    }

    public void releaseMainImage() {
        ImageUtil.recycleBitmap(m_ivMainImage);
    }

    public void loadMainImage() {
        if (m_ivMainImage == null || m_szMainImagePath == null || m_szMainImagePath.length() < 1) {
            return;
        }
        String imagePath = m_szMainImagePath;
        if (!imagePath.startsWith("http"))
            imagePath = SnapsAPI.DOMAIN() + imagePath;

        ImageLoader.with(mContext).load(imagePath).fitCenter().into(m_ivMainImage);
    }
}
