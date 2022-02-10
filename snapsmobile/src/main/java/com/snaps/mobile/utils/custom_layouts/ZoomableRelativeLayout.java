package com.snaps.mobile.utils.custom_layouts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.snaps.common.data.img.BPoint;
import com.snaps.common.data.img.BRect;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.UIUtil;

import java.lang.reflect.InvocationTargetException;

public class ZoomableRelativeLayout extends RelativeLayout {
    private static final String TAG = ZoomableRelativeLayout.class.getSimpleName();

    public static final float LIMIT_SCALE_RATIO_BOTH_PAGE_TYPE = 3.6f; //양면으로 펼쳐지는 스타일
    public static final float LIMIT_SCALE_RATIO_SINGLE_PAGE_TYPE = 2f; //단면 스타일

    public enum ZOOMABLE_LAYOUT_SCROLL_DIRECTION {
        NONE,
        TO_LEFT,
        TO_RIGHT,
        TO_TOP,
        TO_BOTTOM
    }

    public interface IZoomViewTouchListener {
        void onZoomViewTouch(MotionEvent ev);
    }

    protected IZoomViewTouchListener zoomViewTouchListener = null;

    private final int PAGER_MARGIN_VALUE_SIDE = UIUtil.convertDPtoPX(
            getContext(), 20); // 양쪽을 합쳐서 계산 할 것.
    public final int LEFT_TRAY_SIZE = UIUtil.convertDPtoPX(
            getContext(), 112); // 양쪽을 합쳐서 계산 할 것.

    private final int PAGER_MARGIN_VALUE_TOP_N_BOTTOM = UIUtil.convertDPtoPX(
            getContext(), 100); // 상, 하를 합쳐서 계산 할 것.

    public final float PAGING_MARGIN_OFFSET = UIUtil.convertDPtoPX(
            getContext(), 4); // 확대 했을 때, 이 정도 공간이 남아 있으면 페이징 처리 한다.(한쪽 기준
    // 마진임.)
    private final int CLICK_ALLOW_PIXEL = UIUtil.convertDPtoPX( //하위 뷰에 클릭 이벤트를 보내 주기 위해 터치 이벤트로 클릭이벤트를 구현함..
            getContext(), 2);

    // 줌 아웃할 때, 원래 포지션으로 돌려 놓는 감도..(속도..) 숫자가 낮을 수록 느리다..
    private final float ZOOM_OUT_RECOVER_POSITION_SPEED = .011f;

    private final float ZOOM_OUT_RECOVER_LIMIT = 15f; // 확대 된 화면을 다시 축소할 때, 중심점
    // 이 정도 오차..까지...맞춰준다.

    private final float FIX_VALUE_TOUCH_MOVE_DIST = .35f; //너무 획획 지나가서 감도를 좀 낮춤..(수치가 낮을 수록 천천히 움직임..)

    private final float FIX_VALUE_TOUCH_SWIPE = .7f; //스와이핑  감도.. //수치가 높을수록 무뎌짐..

    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();

    private Handler mScrollingHandler = new Handler();

    private float mScaleFactor = 1;
    private float mScaleLimit = LIMIT_SCALE_RATIO_BOTH_PAGE_TYPE; //FIXME 최대 확대 배율 -> 단면 2, 양면 3.6
    private float mTranslateX;
    private float mTranslateY;
    private float mDefaultScaleFactor = 1;

    private Matrix mTranslateMatrix = new Matrix();
    private Matrix mTranslateMatrixInverse = new Matrix();

    private float mLastTouchX;
    private float mLastTouchY;

    private float mPrevSpacing = 0.f;

    private Rect mCanvasRect = new Rect();
    private Rect mContainerLayoutRect = new Rect();
    private ZoomViewCoordInfo mZoomViewCoordInfo = null;

    private Context mContext = null;

    private float[] mInvalidateWorkingArray = new float[6];
    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];

    private View.OnClickListener snapsPageClickListener = null;
    private View.OnClickListener snapsPageLongClickListener = null;
    private InterceptTouchableViewPager mViewPager = null;
    private int mTouchSlop = 0;

    private int m_iTouchDownX = 0;
    private int m_iTouchDownY = 0;

    private int m_iDefaultWidth = 0;
    private int m_iDefaultHeight = 0;
    private int m_iDefaultTopMargin = 0;
    private int m_iDefaultLeftMargin = 0;

    private boolean m_isPreview = false;
    private boolean m_isAllowPaging = false;
    private boolean m_isLandscapeMode = false;
    private boolean m_isSupportZoomProduct = false;
    private boolean m_isZoomOut = false;
    private boolean m_isInitialize = false;
    private boolean m_isOverScreenWidth = false;
    private boolean m_isOverScreenHeight = false;
    private boolean m_isOverPagingLimitLeft = false;
    private boolean m_isOverPagingLimitRight = false;
    private boolean m_isOverPagingLimitTop = false;
    private boolean m_isOverPagingLimitBottom = false;
    private boolean m_isLongClicked = false;

    private ZOOMABLE_LAYOUT_SCROLL_DIRECTION mScrollDirect = ZOOMABLE_LAYOUT_SCROLL_DIRECTION.NONE;
    private float m_fScrollDelta = 0;

    private boolean m_isZoomable = false;
    private boolean m_isThumbnailView = false;
    private boolean m_isDefaultScaleFactorInit = false;
    private float m_fThumbnailRatioX = 1.f, m_fThumbnailRatioY = 1.f;

    public ZoomableRelativeLayout(Context context) {
        super(context);
        init(context);
    }

    public ZoomableRelativeLayout(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public ZoomableRelativeLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        m_isZoomable = isZoomable();
        m_isSupportZoomProduct = isSupportZoomFunction();

        this.mContext = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = (int) (vc.getScaledTouchSlop() * FIX_VALUE_TOUCH_SWIPE); // 뷰 페이저에서 슬라이딩으로 인식하는 픽셀 정도..
        m_isInitialize = false;
        m_isDefaultScaleFactorInit = false;
        mZoomViewCoordInfo = new ZoomViewCoordInfo(context);

        mTranslateX = 0.f;
        mTranslateY = 0.f;
    }

    protected boolean isZoomable() {
        return true;
    }

    protected boolean isSupportZoomFunction() {
        return !Config.isSnapsSticker() && !Const_PRODUCT.isSNSBook();
    }

    //줌 이후 무빙을 할 수 있도록..
    protected boolean shouldMoveAfterZoom() {
        return isSupportZoomFunction();
    }

    protected boolean shouldBeCheckScaleGesture() {
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!m_isZoomable) {
            return;
        }

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                LayoutParams params = (LayoutParams) child.getLayoutParams();

                int width = getPaddingLeft() + getPaddingRight()
                        + child.getMeasuredWidth() + params.leftMargin
                        + params.rightMargin;
                int height = getPaddingTop() + getPaddingBottom()
                        + child.getMeasuredHeight() + params.topMargin
                        + params.bottomMargin;

                int shadowPosition = 0;
                if (Config.isCalendar()) {
                    shadowPosition = childCount - 1;
                }

                if (i == shadowPosition) {
                    m_iDefaultWidth = width;
                    m_iDefaultHeight = height;
                }

                if (params.topMargin != 0) {
                    m_iDefaultTopMargin = params.topMargin;
                }

                if (params.leftMargin != 0) {
                    m_iDefaultLeftMargin = params.leftMargin;
                }

                int left = (getWidth() / 2 - width / 2) + params.leftMargin
                        + getPaddingLeft();// + params.leftMargin;
                int top = (getHeight() / 2 - height / 2) + params.topMargin
                        + getPaddingTop();// + params.topMargin;
                child.layout(left, top, left + width, top + height);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!m_isZoomable) {
            return;
        }

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!m_isZoomable) {
            return super.onInterceptTouchEvent(e);
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!m_isZoomable) {
            return super.dispatchTouchEvent(ev);
        }

        // Logg.y("dispatchTouchEvent :" + ev.getAction());
        mDispatchTouchEventWorkingArray[0] = ev.getX();
        mDispatchTouchEventWorkingArray[1] = ev.getY();
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        ev.setLocation(mDispatchTouchEventWorkingArray[0],
                mDispatchTouchEventWorkingArray[1]);

        DataTransManager transMan = DataTransManager.getInstance();
        if (transMan != null) {
            mZoomViewCoordInfo.setScaleFactor(mScaleFactor);
            mZoomViewCoordInfo.setTranslateX(mTranslateX);
            mZoomViewCoordInfo.setTranslateY(mTranslateY);
            mZoomViewCoordInfo.setTranslateMatrixInverse(mTranslateMatrix);
            mZoomViewCoordInfo.setScaleMatrixInverse(mScaleMatrix);
            transMan.setZoomViewCoordInfo(mZoomViewCoordInfo);

            sendOnClickEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    private void sendOnClickEvent(MotionEvent ev) {
        if (zoomViewTouchListener == null || ev.getAction() == MotionEvent.ACTION_MOVE) {
            return;
        }
        MotionEvent performEvent = MotionEvent.obtain(ev);

        int offsetX = -((getWidth() / 2 - m_iDefaultWidth / 2)) - m_iDefaultLeftMargin; //중앙으로 위치 시켰기 때문에..
        int offsetY = -((getHeight() / 2 - m_iDefaultHeight / 2)) - m_iDefaultTopMargin;
        performEvent.offsetLocation(offsetX, offsetY);

        onPostTouchEvent(performEvent);

        //줌 동작은 클릭이 아니다.
        if (ev.getPointerCount() > 1) {
            performEvent.setAction(MotionEvent.ACTION_CANCEL);
            zoomViewTouchListener.onZoomViewTouch(performEvent);
            return;
        }

        switch (performEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:

                int[] dist = calculateDistance(performEvent);
                if (performEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (dist[0] < CLICK_ALLOW_PIXEL && dist[1] < CLICK_ALLOW_PIXEL) {
                        performEvent.setLocation(m_iTouchDownX, m_iTouchDownY); //클릭으로 동작시키기 위해 터치 다운된 좌표로 셋팅해 줌.
                    } else {
                        performEvent.setAction(MotionEvent.ACTION_CANCEL);
                    }
                }

                zoomViewTouchListener.onZoomViewTouch(performEvent);
                break;
            default:
                break;
        }
    }

    protected void onPostTouchEvent(MotionEvent e) {
    }

    private int[] calculateDistance(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                m_iTouchDownX = (int) e.getX();
                m_iTouchDownY = (int) e.getY();
                break;
            case MotionEvent.ACTION_UP:
                int dx = (int) Math.abs(e.getX() - m_iTouchDownX);
                int dy = (int) Math.abs(e.getY() - m_iTouchDownY);
                return new int[]{dx, dy};
            default:
                break;
        }
        return new int[]{0, 0};
    }

    protected float getBestRatio() { //FIXME... 리펙토링 필요해 보인다.
        if (Config.isSnapsSticker()) {
            return 1.f;
        } else if (Const_PRODUCT.isStikerGroupProduct()) {
            return getStickerBestRatio();
        } else if (Const_PRODUCT.isBabyNameStikerGroupProduct()) {
            return getBabyNameStickerBestRatio();
        } else if (Const_PRODUCT.isMiniBannerProduct()) {
            return getMiniBannerBestRatio();
        } else if (Const_PRODUCT.isNewPolaroidPackProduct()) {
            return getMiniBannerBestRatio();  //Ben:: 미니 배너는 아니지만 크게 보여 달라고 해서 미니 배너 처럼 하니까 크게 보여서 미니 배너를 사용.. ㅡㅡ;;
        }

        float fRatio = 1.f;
        float fFixMarginRat = 1.f;
        float fFixMarginRatioIncreaseAmount = getFixMarginRatioIncreaseAmount();
        while (true) {
            if (isLandscapeMode()) {
                if (m_iDefaultWidth > m_iDefaultHeight) {
                    fRatio = (getWidth() - LEFT_TRAY_SIZE)
                            / (float) (m_iDefaultWidth + ((float) PAGER_MARGIN_VALUE_SIDE * fFixMarginRat));
                } else {
                    fRatio = getHeight()
                            / (float) (m_iDefaultHeight + ((float) PAGER_MARGIN_VALUE_SIDE * fFixMarginRat));
                }
            } else {
                if (m_iDefaultWidth > m_iDefaultHeight) {
                    fRatio = getWidth()
                            / (float) (m_iDefaultWidth + ((float) PAGER_MARGIN_VALUE_SIDE * fFixMarginRat));
                } else {
                    fRatio = getHeight()
                            / (float) (m_iDefaultHeight + ((float) PAGER_MARGIN_VALUE_TOP_N_BOTTOM * fFixMarginRat));
                }
            }

            float scaledWidth = (m_iDefaultWidth * fRatio) + (PAGER_MARGIN_VALUE_SIDE);
            float scaledHeight = (m_iDefaultHeight * fRatio) + (isLandscapeMode() ?
                    PAGER_MARGIN_VALUE_SIDE : PAGER_MARGIN_VALUE_TOP_N_BOTTOM);
            if (scaledWidth > getWidth() || scaledHeight > getHeight()) {
                fFixMarginRat += fFixMarginRatioIncreaseAmount;
            } else {
                break;
            }
        }

        return fRatio;
    }

    private float getFixMarginRatioIncreaseAmount() {
        // !!!! 아놔!!!!!!
        // 기존 코드가 iFixMarginRat++ 이렇게 되어있었음..
        // PAGER_MARGIN_VALUE_SIDE, PAGER_MARGIN_VALUE_SIDE, 기타 등등이 하드 코딩된 크기를 잡고 있었는데
        // 아무튼 iFixMarginRat++ 이렇게 하면 증가 폭이 너무 커서 갑자기 크기가 확 줄어듬 ㅡㅡ;;;;

        if (Const_PRODUCT.isAcrylicKeyringProduct() || Const_PRODUCT.isAcrylicStandProduct() ||
                Const_PRODUCT.isAirpodsCaseProduct() || Const_PRODUCT.isBudsCaseProduct() ||
                Const_PRODUCT.isButtonProduct() || Const_PRODUCT.isSealStickerProduct()) {
            return 0.05f;
        }

        return 1.0f;    //기존 상품 사이드 예측이 불가능하므로 기존값 유지
    }

    protected int getDefaultWidth() {
        return m_iDefaultWidth;
    }

    protected int getDefaultHeight() {
        return m_iDefaultHeight;
    }

    //임시로 크기 조정 나중에 전상품 적용해야 한다.
    private float getStickerBestRatio() {
        float fRatio = 1.f;
        int iFixMarginRat = 1;
        while (true) {
            if (isLandscapeMode()) {
                if (m_iDefaultWidth > m_iDefaultHeight) {
                    fRatio = (getWidth() - LEFT_TRAY_SIZE)
                            / (float) (m_iDefaultWidth + (UIUtil.convertDPtoPX(
                            getContext(), 8) * iFixMarginRat));
                } else {
                    fRatio = getHeight()
                            / (float) (m_iDefaultHeight + (UIUtil.convertDPtoPX(
                            getContext(), 8) * iFixMarginRat));
                }
            } else {
                if (m_iDefaultWidth > m_iDefaultHeight) {
                    fRatio = getWidth()
                            / (float) (m_iDefaultWidth + (UIUtil.convertDPtoPX(
                            getContext(), 8) * iFixMarginRat));
                } else {
                    fRatio = getHeight()
                            / (float) (m_iDefaultHeight + (UIUtil.convertDPtoPX(
                            getContext(), 8) * iFixMarginRat));
                }
            }

            float scaledWidth = (m_iDefaultWidth * fRatio) + ((UIUtil.convertDPtoPX(
                    getContext(), 8)));
            float scaledHeight = (m_iDefaultHeight * fRatio) + ((UIUtil.convertDPtoPX(
                    getContext(), 8)));
            if (scaledWidth > getWidth() || scaledHeight > getHeight()) {
                iFixMarginRat++;
            } else {
                break;
            }
        }
        return fRatio;
    }

    private float getBabyNameStickerBestRatio() {
        float fRatio = 1.f;
        int iFixMarginRat = 1;
        while (true) {
            if (isLandscapeMode()) {
                if (m_iDefaultWidth > m_iDefaultHeight) {
                    fRatio = (getWidth() - LEFT_TRAY_SIZE)
                            / (float) (m_iDefaultWidth + (UIUtil.convertDPtoPX(
                            getContext(), 32) * iFixMarginRat));
                } else {
                    fRatio = getHeight()
                            / (float) (m_iDefaultHeight + (UIUtil.convertDPtoPX(
                            getContext(), 32) * iFixMarginRat));
                }
            } else {
                if (m_iDefaultWidth > m_iDefaultHeight) {
                    fRatio = getWidth()
                            / (float) (m_iDefaultWidth + (UIUtil.convertDPtoPX(
                            getContext(), 32) * iFixMarginRat));
                } else {
                    fRatio = getHeight()
                            / (float) (m_iDefaultHeight + (UIUtil.convertDPtoPX(
                            getContext(), 32) * iFixMarginRat));
                }
            }

            float scaledWidth = (m_iDefaultWidth * fRatio) + ((UIUtil.convertDPtoPX(
                    getContext(), 8)));
            float scaledHeight = (m_iDefaultHeight * fRatio) + ((UIUtil.convertDPtoPX(
                    getContext(), 8)));
            if (scaledWidth > getWidth() || scaledHeight > getHeight()) {
                iFixMarginRat++;
            } else {
                break;
            }
        }
        return fRatio;
    }

    private float getMiniBannerBestRatio() {
        float fRatio = 1.f;
        int iFixMarginRat = 1;
        while (true) {
            if (isLandscapeMode()) {
                if (m_iDefaultWidth > m_iDefaultHeight) {
                    fRatio = (getWidth() - LEFT_TRAY_SIZE)
                            / (float) (m_iDefaultWidth + (UIUtil.convertDPtoPX(
                            getContext(), 32) * iFixMarginRat));
                } else {
                    fRatio = getHeight()
                            / (float) (m_iDefaultHeight + (UIUtil.convertDPtoPX(
                            getContext(), 32) * iFixMarginRat));
                }
            } else {
                if (m_iDefaultWidth > m_iDefaultHeight) {
                    fRatio = getWidth()
                            / (float) (m_iDefaultWidth + (UIUtil.convertDPtoPX(
                            getContext(), 32) * iFixMarginRat));
                } else {
                    fRatio = getHeight()
                            / (float) (m_iDefaultHeight + (UIUtil.convertDPtoPX(
                            getContext(), 32) * iFixMarginRat));
                }
            }

            float scaledWidth = (m_iDefaultWidth * fRatio) + ((UIUtil.convertDPtoPX(
                    getContext(), 8)));
            float scaledHeight = (m_iDefaultHeight * fRatio) + ((UIUtil.convertDPtoPX(
                    getContext(), 8)));
            if (scaledWidth > getWidth() || scaledHeight > getHeight()) {
                iFixMarginRat++;
            } else {
                break;
            }
        }
        return fRatio;
    }

    public void initLocation() {
        initLocation(false);
    }

    public void initLocation(boolean isForceSetDefaultScaleFactor) {
        if (m_iDefaultWidth == 0 || m_iDefaultHeight == 0) {
            requestLayout();
            return;
        }

        mScaleFactor = getBestRatio();
        if (!m_isDefaultScaleFactorInit) {
            mDefaultScaleFactor = mScaleFactor;
            m_isDefaultScaleFactorInit = true;
        }

        //ben 추가함
        //기본 크기에서 줌 아웃이 되는 문제가 있어서 추가 (정확히는 기본 크기가 보다 줄어들려고 하다가 다시 기본 크기가 되려고 하고 반복되면서 스케일이 울렁거림)
        //아크릴 캔버스에서 initLocation를 호출하는데 m_isDefaultScaleFactorInit 플래그 때문에 mDefaultScaleFactor가 변경되지 않는다.
        //즉 현재 줌 값과 디폴트 줌 값이 불일치하는 현상이 발생한다는 뜻이다.
        //불일치가 발생하면 이 소스 제일 마지막에 있는 ScaleGestureDetector.SimpleOnScaleGestureListener 안을 보면 min, max 뭐뭐 해서 계산하는 부분이 있는데.. (주석쓰기 귀찬아서 생략)
        if (isForceSetDefaultScaleFactor) {
            mDefaultScaleFactor = mScaleFactor;
        }

        mZoomViewCoordInfo.setDefualtScaleFactor(mDefaultScaleFactor);
        mZoomViewCoordInfo.setScaleFactor(mScaleFactor);
        mZoomViewCoordInfo.setTranslateX(0);
        mZoomViewCoordInfo.setTranslateY(0);

        mScaleMatrix.setScale(mScaleFactor, mScaleFactor, getWidth() / 2,
                getHeight() / 2);
        mScaleMatrix.invert(mScaleMatrixInverse);

        mTranslateX = 0;
        mTranslateY = 0;

        m_isInitialize = true;
        DataTransManager transMan = DataTransManager.getInstance();
        mTranslateMatrix.setTranslate(0, 0);
        mTranslateMatrix.invert(mTranslateMatrixInverse);

        mZoomViewCoordInfo.setTranslateMatrixInverse(mTranslateMatrix);
        mZoomViewCoordInfo.setScaleMatrixInverse(mScaleMatrix);
        if (transMan != null && !m_isThumbnailView) {
            transMan.setZoomViewCoordInfo(mZoomViewCoordInfo);
        }

        stopHandler();
        if (mContext != null && mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
        }
    }

    public void initLocationWithOffsetWidth(final int canvasWidth, final int offsetWidth, final int sideMargin) {
        if (canvasWidth == 0) {
            return;
        }

        if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            setVisibility(View.INVISIBLE);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initLocationWithOffsetWidth(canvasWidth, offsetWidth, sideMargin);
                }
            }, 50);
            return;
        }

        setVisibility(View.VISIBLE);

        mScaleFactor = offsetWidth / (float) (canvasWidth + sideMargin);
        if (!m_isDefaultScaleFactorInit) {
            mDefaultScaleFactor = mScaleFactor;
            m_isDefaultScaleFactorInit = true;
        }
        mZoomViewCoordInfo.setDefualtScaleFactor(mDefaultScaleFactor);
        mZoomViewCoordInfo.setScaleFactor(mScaleFactor);
        mScaleMatrix.setScale(mScaleFactor, mScaleFactor, getWidth() / 2,
                getHeight() / 2);
        mScaleMatrix.invert(mScaleMatrixInverse);

        mTranslateX = 0;
        mTranslateY = 0;

        m_isInitialize = true;
        DataTransManager transMan = DataTransManager.getInstance();
        mTranslateMatrix.setTranslate(0, 0);
        mTranslateMatrix.invert(mTranslateMatrixInverse);

        mZoomViewCoordInfo.setTranslateMatrixInverse(mTranslateMatrix);
        mZoomViewCoordInfo.setScaleMatrixInverse(mScaleMatrix);
        if (transMan != null && !m_isThumbnailView) {
            transMan.setZoomViewCoordInfo(mZoomViewCoordInfo);
        }

        stopHandler();
        if (mContext != null && mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
        }
    }

    public void initLocationWithOffsetHeight(int canvasHeight, int offsetHeight, int margin) {
        if (canvasHeight == 0) {
            return;
        }

        mScaleFactor = offsetHeight / (float) (canvasHeight + margin);
        if (!m_isDefaultScaleFactorInit) {
            mDefaultScaleFactor = mScaleFactor;
            m_isDefaultScaleFactorInit = true;
        }
        mZoomViewCoordInfo.setDefualtScaleFactor(mDefaultScaleFactor);
        mZoomViewCoordInfo.setScaleFactor(mScaleFactor);
        mScaleMatrix.setScale(mScaleFactor, mScaleFactor, getWidth() / 2,
                getHeight() / 2);
        mScaleMatrix.invert(mScaleMatrixInverse);

        mTranslateX = 0;
        mTranslateY = 0;

        m_isInitialize = true;
        DataTransManager transMan = DataTransManager.getInstance();
        mTranslateMatrix.setTranslate(0, 0);
        mTranslateMatrix.invert(mTranslateMatrixInverse);

        mZoomViewCoordInfo.setTranslateMatrixInverse(mTranslateMatrix);
        mZoomViewCoordInfo.setScaleMatrixInverse(mScaleMatrix);
        if (transMan != null && !m_isThumbnailView) {
            transMan.setZoomViewCoordInfo(mZoomViewCoordInfo);
        }

        stopHandler();
        if (mContext != null && mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
        }
    }

    //컨트롤이 화면 밖으로 나가지 못하게 체크...
    private void checkCanvasBoundary(Canvas canvas) {
        if (!isPinchZoom(canvas)) {
            return;
        }

        //Canvas가 단말기 화면에서 위치하는 초기 위치.
        BPoint canvasOffsetPoint = getCanvasOffsetPoint();

        //좌표 수치가 100% 정확하게 맞지 않아서, 보정 코드..
        BRect canvasLimitOffsetRect = getCanvasLimitOffsetRect(canvas, canvasOffsetPoint.getY());

        //핀치 동작으로 확대되거나, 이동된 container의 위치를 구한다.
        setContainerRectWithOffset(canvasOffsetPoint.getX(), canvasOffsetPoint.getY());

        //컨트롤이 화면 밖으로 나가지 못하게 체크한다.
        setOverScreenOffset(canvas, canvasLimitOffsetRect);

        //스와이프 동작을 했을때, 끝단까지 스르륵 흘러가게 하는 동작.
        smoothScrollBySwipeTouch(canvasLimitOffsetRect);
    }

    private void setOverScreenOffset(Canvas canvas, BRect canvasLimitOffsetRect) {
        m_isOverScreenWidth = mContainerLayoutRect.left < 0
                || mContainerLayoutRect.right > getScreenWdith();
        m_isOverScreenHeight = mContainerLayoutRect.top < 0
                || mContainerLayoutRect.bottom > canvas.getHeight();

        //Container가 화면 밖으로 나가지 못 하도록 한다..
        m_isOverPagingLimitLeft = m_isOverScreenWidth
                && (mContainerLayoutRect.left > canvasLimitOffsetRect.left);
        m_isOverPagingLimitRight = m_isOverScreenWidth
                && (mContainerLayoutRect.right < canvasLimitOffsetRect.right); // 오른쪽이 공간이 더 좁아보여서..

        m_isOverPagingLimitTop = m_isOverScreenHeight
                && (mContainerLayoutRect.top > canvasLimitOffsetRect.top); //위에가 더 넓어 보인다..수치상으로는 맞는데...
        m_isOverPagingLimitBottom = m_isOverScreenHeight
                && (mContainerLayoutRect.bottom < canvasLimitOffsetRect.bottom); //아랫쪽 공간이 더 좁아 보여서...
    }

    /*
     * Canvas가 단말기 화면에서 위치하는 초기 위치.
     * 타이틀바와 썸네일 영역 때문에 좌표를 보정 해 줘야 한다..
     * 하단 및 좌측에 썸네일이 있는 상품군은 Config.isExistThumbnailEditView()를 수정 해 주어야 한다..
     */
    protected BPoint getCanvasOffsetPoint() {
        int x = 0, y = 0;
        boolean isExistThumbnailView = Config.isExistThumbnailEditView();
        if (isExistThumbnailView) {
            if (isLandscapeMode()) {
                y = UIUtil.convertDPtoPX(mContext, 48);
                x = UIUtil.convertDPtoPX(mContext, 106);
                if (Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
                    x = UIUtil.convertDPtoPX(mContext, 148);
                } else if (Const_PRODUCT.isAccordionCardProduct() || Const_PRODUCT.isSloganProduct()) {
                    x = UIUtil.convertDPtoPX(mContext, 112);
                    // 이 수치는 Controller2에서 정한 수치.
                }
            } else {
                y = UIUtil.convertDPtoPX(mContext, 48);
            }
        } else {
            x = 0;
            y = UIUtil.convertDPtoPX(mContext, 48);
        }

        return new BPoint(x, y);
    }

//	protected BRect getCanvasLimitOffsetRect(Canvas canvas, int offsetY) {
//		//좌표 수치가 100% 정확하게 맞지 않아서, 보정 코드..
//		int l = (int) PAGING_MARGIN_OFFSET;
//		int t = (int) (isLandscapeMode() ?  PAGING_MARGIN_OFFSET : PAGING_MARGIN_OFFSET/2);
//		int r = (int) (canvas.getUserSelectWidth() - (PAGING_MARGIN_OFFSET * 1.2f));
//		int b = (int)(isLandscapeMode() ? (canvas.getHeight() - PAGING_MARGIN_OFFSET) : (canvas.getHeight() - offsetY) - (PAGING_MARGIN_OFFSET * 1.5f));
//
//		if (Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isPhotoCardProduct()) {
//			if (!isLandscapeMode()) { //썸네일 영역이 타 상품 대비 높다
//				b -= UIUtil.convertDPtoPX(mContext, 131);
//			}
//		}
//		return new BRect(l, t, r, b);
//	}

    //Rect의 크기가 커 질수록 화면과 뷰 안쪽 사이의 마진이 커진다.
    protected BRect getCanvasLimitOffsetRect(Canvas canvas, int offsetY) {
        int l = (int) PAGING_MARGIN_OFFSET;
        int t = (int) PAGING_MARGIN_OFFSET;
        int r = (int) (canvas.getWidth() - PAGING_MARGIN_OFFSET);
        int b = (int) (canvas.getHeight() - PAGING_MARGIN_OFFSET);

        return new BRect(l, t, r, b);
    }

    private boolean isPinchZoom(Canvas canvas) {
        if (getScale() == 1) {
            m_isOverScreenWidth = false;
            return false;
        }

        mCanvasRect = canvas.getClipBounds();
        if (mCanvasRect.width() < 1 || mCanvasRect.height() < 1) {
            super.dispatchDraw(canvas);
            canvas.restore();
            return false;
        }

        return true;
    }

    private void setContainerRectWithOffset(int canvasOffsetX, int canvasOffsetY) {
        View containerLayoutView = getChildCount() > 1 ? getChildAt(1)
                : getChildAt(0);

        containerLayoutView.getGlobalVisibleRect(mContainerLayoutRect);

        if (isLandscapeMode()) {
            mContainerLayoutRect.offset(-(mCanvasRect.left + canvasOffsetX), -(mCanvasRect.top + canvasOffsetY));
        } else {
            mContainerLayoutRect.offset(-(mCanvasRect.left + canvasOffsetX), -((mCanvasRect.top + canvasOffsetY / 2) + canvasOffsetY)); //애초에 센터 정렬이기 때문에 타이틀 바의 반만큼만 올려준다.
        }

        mContainerLayoutRect.set(mContainerLayoutRect.left,
                mContainerLayoutRect.top,
                (int) (mContainerLayoutRect.right * mScaleFactor),
                (int) (mContainerLayoutRect.bottom * mScaleFactor));
    }

    protected void smoothScrollBySwipeTouch(BRect canvasLimitOffsetRect) {
        switch (mScrollDirect) {
            case TO_LEFT:
                m_fScrollDelta = Math.abs(mContainerLayoutRect.left - canvasLimitOffsetRect.left);
                if (m_isOverPagingLimitLeft) {
                    stopHandler();
                }
                break;
            case TO_RIGHT:
                m_fScrollDelta = Math.abs(canvasLimitOffsetRect.right - mContainerLayoutRect.right);
                if (m_isOverPagingLimitRight) {
                    stopHandler();
                }
                break;
            case TO_TOP:
                m_fScrollDelta = Math.abs(canvasLimitOffsetRect.top - mContainerLayoutRect.top);
                if (m_isOverPagingLimitTop) {
                    stopHandler();
                }
                break;
            case TO_BOTTOM:
                m_fScrollDelta = Math.abs(mContainerLayoutRect.bottom - canvasLimitOffsetRect.bottom);
                if (m_isOverPagingLimitBottom) {
                    stopHandler();
                }
                break;
            default:
                break;
        }
    }

    protected void smoothScrollBySwipeTouch(final int LIMIT_OFFSET_LEFT, final int LIMIT_OFFSET_TOP, final int LIMIT_OFFSET_RIGHT, final int LIMIT_OFFSET_BOTTOM) {
        //스와이프 동작을 했을때, 끝단까지 스르륵 흘러가게 하는 동작.
        switch (mScrollDirect) {
            case TO_LEFT:
                m_fScrollDelta = Math.abs(mContainerLayoutRect.left - LIMIT_OFFSET_LEFT);
                if (m_isOverPagingLimitLeft) {
                    stopHandler();
                }
                break;
            case TO_RIGHT:
                m_fScrollDelta = Math.abs(LIMIT_OFFSET_RIGHT - mContainerLayoutRect.right);
                if (m_isOverPagingLimitRight) {
                    stopHandler();
                }
                break;
            case TO_TOP:
                m_fScrollDelta = Math.abs(LIMIT_OFFSET_TOP - mContainerLayoutRect.top);
                if (m_isOverPagingLimitTop) {
                    stopHandler();
                }
                break;
            case TO_BOTTOM:
                m_fScrollDelta = Math.abs(mContainerLayoutRect.bottom - LIMIT_OFFSET_BOTTOM);
                if (m_isOverPagingLimitBottom) {
                    stopHandler();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!m_isZoomable) {
            super.dispatchDraw(canvas);
            return;
        }

        if (getChildCount() < 1) {
            return;
        }

        if (!m_isInitialize) {
            initLocation();
            return;
        }

        canvas.save();

        float offsetX = getPositionX();//mTranslateX * mScaleFactor;
        float offsetY = getPositionY();//mTranslateY * mScaleFactor;

        if (getScale() != 1) {
            canvas.translate(offsetX, offsetY);
        }

        canvas.scale(mScaleFactor, mScaleFactor, getWidth() / 2, getHeight() / 2);

        checkCanvasBoundary(canvas);

        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        canvas.restore();
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void stopHandler() {
        mScrollDirect = ZOOMABLE_LAYOUT_SCROLL_DIRECTION.NONE;

        if (mScrollingHandler != null) {
            mScrollingHandler.removeCallbacks(mScrollingUpdateHandler);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!m_isZoomable) {
            return super.onTouchEvent(ev);
        }

        if (!m_isSupportZoomProduct && !shouldMoveAfterZoom()) {
            if (shouldBeCheckScaleGesture()) {
                mScaleDetector.onTouchEvent(ev);
            }
            return true;
        }

        mOnTouchEventWorkingArray[0] = ev.getX();
        mOnTouchEventWorkingArray[1] = ev.getY();

        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);

        ev.setLocation(mOnTouchEventWorkingArray[0],
                mOnTouchEventWorkingArray[1]);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = ev.getPointerId(0);

                stopHandler();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex == INVALID_POINTER_ID || pointerIndex == -1) {
                    break;
                }

                float x = mLastTouchX;
                float y = mLastTouchY;
                try {
                    x = ev.getX(pointerIndex);
                    y = ev.getY(pointerIndex);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    break;
                }

                float dx = (x - mLastTouchX) * FIX_VALUE_TOUCH_MOVE_DIST;
                float dy = (y - mLastTouchY) * FIX_VALUE_TOUCH_MOVE_DIST; // 너무 휙휙 지나가서 보정함..

                //편집화면을 확대 했을 때, 확대 화면과 뷰 페이져 간의 터치 이벤트 선점 처리..
                //mTouchSlop는 스와이핑을 하는 동작인지 체크하기 위함이다.(빠르게 슉 문지르면..컨트롤의 끝까지 이동시키기 위함..)
                if (getViewPager() != null) {
                    if (m_isSupportZoomProduct && ev.getPointerCount() > 1) {
                        getViewPager().setPaging(false);
                    } else {
                        if (m_isOverScreenWidth) { //확대된 컨테이너가 단말기화면보다 커 졌을때,
                            boolean isSwipeAction = Math.abs(dx) > mTouchSlop; //스와프 동작(빠르게 그었을때,)
                            if (m_isOverPagingLimitLeft) { //왼쪽이 초과된 상태
                                getViewPager().setPaging(m_isAllowPaging && dx > 0);
                                if (dx > 0) {
                                    dx = 0;
                                }
                            } else if (m_isOverPagingLimitRight) { //오른쪽이 초과된 상태
                                getViewPager().setPaging(m_isAllowPaging && dx < 0);
                                if (dx < 0) {
                                    dx = 0;
                                }
                            } else {
                                getViewPager().setPaging(false);
                                m_isAllowPaging = false;
                            }

                            if (isSwipeAction && dx != 0) {
                                if (dx > 0) {
                                    mScrollDirect = ZOOMABLE_LAYOUT_SCROLL_DIRECTION.TO_LEFT;
                                } else {
                                    mScrollDirect = ZOOMABLE_LAYOUT_SCROLL_DIRECTION.TO_RIGHT;
                                }

                                mScrollingHandler.post(mScrollingUpdateHandler);
                            }
                        } else {
                            getViewPager().setPaging(true);
                            dx = 0;
                        }
                    }
                }

                if (m_isOverScreenHeight) {
                    boolean isSwipeAction = Math.abs(dy) > mTouchSlop; //스와프 동작(빠르게 그었을때,)
                    if (m_isOverPagingLimitTop) {
                        if (dy > 0) {
                            dy = 0;
                        }
                    } else if (m_isOverPagingLimitBottom) {
                        if (dy < 0) {
                            dy = 0;
                        }
                    }

                    //화면 끝으로 보내는 처리
                    if (isSwipeAction && dy != 0) {
                        if (dy > 0) {
                            mScrollDirect = ZOOMABLE_LAYOUT_SCROLL_DIRECTION.TO_TOP;
                        } else {
                            mScrollDirect = ZOOMABLE_LAYOUT_SCROLL_DIRECTION.TO_BOTTOM;
                        }

                        mScrollingHandler.post(mScrollingUpdateHandler);
                    }
                } else {
                    dy = 0;
                }

                // 편집 화면을 확대 한 이후, 다시 축소할 때, 원래 자리로 돌려 놓는 동작.
                if (m_isSupportZoomProduct && ev.getPointerCount() > 1) {
                    float curSpacing = spacing(ev);
                    m_isZoomOut = curSpacing < mPrevSpacing;
                    mPrevSpacing = curSpacing;

                    if (m_isZoomOut) {

//					if(getScale() < 1.1f) {
                        if (mScaleFactor <= mDefaultScaleFactor) {
                            initLocation();
                            return false;
                        } else {
                            dx -= (getPositionX() * ZOOM_OUT_RECOVER_POSITION_SPEED);
                            dy -= (getPositionY() * ZOOM_OUT_RECOVER_POSITION_SPEED);
                        }
                    } else {
                        dx = 0;
                        dy = 0;
                    }
                }

                if (getScale() != 1) {
                    mTranslateX += dx;
                    mTranslateY += dy;

                    mTranslateMatrix.setTranslate(getPositionX(), getPositionY());
                    mTranslateMatrix.invert(mTranslateMatrixInverse);
                }

                mLastTouchX = x;
                mLastTouchY = y;

                invalidate();

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                if (getViewPager() != null) {
                    if (ev.getPointerCount() > 1) {
                        getViewPager().setPaging(false);
                    } else {
                        if (m_isOverScreenWidth) {
                            if (m_isOverPagingLimitLeft || m_isOverPagingLimitRight) { //확대된 컨테이너가 단말기화면보다 커 졌을때,
                                getViewPager().setPaging(true);
                                m_isAllowPaging = true;
                            } else {
                                getViewPager().setPaging(false);
                            }
                        } else {
                            getViewPager().setPaging(true);
                        }
                    }
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        if (m_isSupportZoomProduct) {
            mScaleDetector.onTouchEvent(ev);
        }

        return true;
    }

    private Runnable mScrollingUpdateHandler = new Runnable() {

        public void run() {
            boolean isSuspend = Math.abs(m_fScrollDelta) < 1;
            if (isSuspend) {
                stopHandler();
                return;
            }

            float scroll = m_fScrollDelta * .08f;
            switch (mScrollDirect) {
                case TO_LEFT:
                    mTranslateX += scroll;
                    break;
                case TO_RIGHT:
                    mTranslateX -= scroll;
                    break;
                case TO_TOP:
                    mTranslateY += scroll;
                    break;
                case TO_BOTTOM:
                    mTranslateY -= scroll;
                    break;
                default:
                    m_fScrollDelta = 0;
                    break;
            }

            if (getScale() != 1) {
                mTranslateMatrix.setTranslate(getPositionX(), getPositionY());
                mTranslateMatrix.invert(mTranslateMatrixInverse);
            }
            mScrollingHandler.postDelayed(this, 30);
            invalidate();
        }
    };

    /**
     * 줌 아웃했을 때, 원래 자리로 돌려 놓기 위해 이동한 거리만큼 원상 복구를 해 주고 나서 줌 아웃을 실시한다.
     */
    private boolean isEnableZoomOut() {
        if (!m_isZoomOut || Math.abs(getPositionX()) > ZOOM_OUT_RECOVER_LIMIT
                || Math.abs(getPositionY()) > ZOOM_OUT_RECOVER_LIMIT) {
            return true;
        }
        return false;
    }

    /**
     * Although the docs say that you shouldn't override this, I decided to do
     * so because it offers me an easy way to change the invalidated area to my
     * likening.
     */
    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        if (!m_isZoomable) {
            return super.invalidateChildInParent(location, dirty);
        }

        mInvalidateWorkingArray[0] = dirty.left;
        mInvalidateWorkingArray[1] = dirty.top;
        mInvalidateWorkingArray[2] = dirty.right;
        mInvalidateWorkingArray[3] = dirty.bottom;

        mInvalidateWorkingArray = scaledPointsToScreenPoints(mInvalidateWorkingArray);
        dirty.set(Math.round(mInvalidateWorkingArray[0]),
                Math.round(mInvalidateWorkingArray[1]),
                Math.round(mInvalidateWorkingArray[2]),
                Math.round(mInvalidateWorkingArray[3]));

        location[0] *= mScaleFactor;
        location[1] *= mScaleFactor;
        return super.invalidateChildInParent(location, dirty);
    }

    private float[] scaledPointsToScreenPoints(float[] a) {
        mScaleMatrix.mapPoints(a);
        mTranslateMatrix.mapPoints(a);
        return a;
    }

//	private float[] scaledPointsToScreenPoints(float[] a) {
//		Rect parentLayoutRect = new Rect();
//		View containerView = (View) getParent().getParent();
//		containerView.getGlobalVisibleRect(parentLayoutRect);
//		Matrix calculatedMatrix = new Matrix();
//		calculatedMatrix.setScale(mDefaultScaleFactor, mDefaultScaleFactor, parentLayoutRect.centerX(), parentLayoutRect.centerY());
//		calculatedMatrix.mapPoints(a);
//		return a;
//	}

    private float[] screenPointsToScaledPoints(float[] a) {
        mTranslateMatrixInverse.mapPoints(a);
        mScaleMatrixInverse.mapPoints(a);
        return a;
    }

    private int getScreenWdith() {
        if (Build.VERSION.SDK_INT >= 14) {
            android.view.Display display = ((WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE)).getDefaultDisplay();
            Point realSize = new Point();
            try {
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
            } catch (IllegalArgumentException e) {
                Dlog.e(TAG, e);
            } catch (IllegalAccessException e) {
                Dlog.e(TAG, e);
            } catch (InvocationTargetException e) {
                Dlog.e(TAG, e);
            } catch (NoSuchMethodException e) {
                Dlog.e(TAG, e);
            }
            return realSize.x;

        } else {
            DisplayMetrics dmath = getContext().getResources().getDisplayMetrics();    // 화면의 가로,세로 길이를 구할 때 사용합니다.
            return dmath.widthPixels;
        }
    }

    public float getScaleLimit() {
        return mScaleLimit * mDefaultScaleFactor; //처음 셋팅되는 화면의 **배까지..
    }

    public void setScaleLimit(float mScaleLimit) {
        this.mScaleLimit = mScaleLimit;
    }

    public float getScale() {
        return 1 + (mScaleFactor - mDefaultScaleFactor);
    }

    public float getCurrentScaleFactor() {
        return mScaleFactor;
    }

    /**
     * position은 중심점에서 얼마나 이동 했는 가 수치이다..
     *
     * @return
     */
    public float getPositionX() {
        return mTranslateX * mScaleFactor;
    }

    public float getPositionY() {
        return mTranslateY * mScaleFactor;
    }

    public InterceptTouchableViewPager getViewPager() {
        return mViewPager;
    }

    public void setViewPager(InterceptTouchableViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

    public boolean isLandscapeMode() {
        return m_isLandscapeMode;
    }

    public void setLandscapeMode(boolean m_isLandscapeMode) {
        this.m_isLandscapeMode = m_isLandscapeMode;
    }

    //미리 보기에서 생성되는 뷰
    public boolean isPreview() {
        return m_isPreview;
    }

    public void setIsPreview(boolean m_isPreview) {
        this.m_isPreview = m_isPreview;
    }

    public void setZoomable(boolean m_isZoomable) {
        this.m_isZoomable = m_isZoomable;
    }

    //편집 화면에 보여지는 페이저 뷰
    public boolean isRealPagerView() {
        return !isThumbnailView() && !isPreview();
    }

    //하단 썸네일에 그려지는 뷰
    public boolean isThumbnailView() {
        return m_isThumbnailView;
    }

    public void setThumbnailView(float ratioX, float ratioY) {
        this.m_fThumbnailRatioX = ratioX;
        this.m_fThumbnailRatioY = ratioY;
        this.m_isThumbnailView = true;
        setZoomable(false);
    }

    public boolean isLongClicked() {
        return m_isLongClicked;
    }

    public void setLongClicked(boolean isLongClicked) {
        this.m_isLongClicked = isLongClicked;
    }

    public float getThumbnailRatioX() {
        return m_fThumbnailRatioX;
    }

    public float getThumbnailRatioY() {
        return m_fThumbnailRatioY;
    }

    public OnClickListener getSnapsPageClickListener() {
        return snapsPageClickListener;
    }

    public void setSnapsPageClickListener(OnClickListener snapsPageClickListener) {
        this.snapsPageClickListener = snapsPageClickListener;
    }

    public OnClickListener getSnapsPageLongClickListener() {
        return snapsPageLongClickListener;
    }

    public void setSnapsPageLongClickListener(OnClickListener snapsPageLongClickListener) {
        this.snapsPageLongClickListener = snapsPageLongClickListener;
    }

    public IZoomViewTouchListener getZoomViewTouchListener() {
        return zoomViewTouchListener;
    }

    public void setZoomViewTouchListener(
            IZoomViewTouchListener zoomViewTouchListener) {
        this.zoomViewTouchListener = zoomViewTouchListener;
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
//			if (!isEnableZoomOut())
//				return false;

            if (m_isSupportZoomProduct) {
                mScaleFactor *= detector.getScaleFactor();
                mScaleFactor = Math.max(mDefaultScaleFactor, Math.min(mScaleFactor, getScaleLimit()));
                mScaleMatrix.setScale(mScaleFactor, mScaleFactor, getWidth() / 2, getHeight() / 2); //캔버스의 중심을 기준으로 확대 한다..
                mScaleMatrix.invert(mScaleMatrixInverse);

                invalidate();
                requestLayout();
            }

            return true;
        }
    }
}