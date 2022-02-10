package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone.pinch_handler;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IGoogleStylePinchGesture;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectDragItemListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IUIPinchMotionCallback;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectPhonePhotoAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies.GooglePhotoStyleAdapterStrategyBase;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.CustomGridLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.GooglePhotoStyleAnimationView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.GooglePhotoStyleFrontView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SquareRelativeLayout;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone.GooglePhotoStylePhoneFragmentProcessor;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ysjeong on 2017. 1. 3..
 */
public abstract class UIPinchMotionDetector implements IUIPinchMotionCallback, ISnapsImageSelectConstants, IGoogleStylePinchGesture, ISnapsHandler {
    private static final String TAG = UIPinchMotionDetector.class.getSimpleName();
    private static final long LIMIT_SUCCESSIVE_CLICK_TIME = 200; //연속 클릭을 막기 위함.(사진 선택 후 해당 시간동안 다른 아이템 클릭을 못한다.)

    protected static final float VALID_OFFSET_INTERVAL = 200; //핀치 동작을 했을 때, 타겟 OFFSET과 이 정도 픽셀 차이 내여야 나야한다.

    protected static final long SCROLL_TO_OFFSET_DELAY_TIME = 50; //리사이클러뷰가 OFFSET을 맞출 때 UI THREAD가 동작할 시간이 필요하다.

    protected static final int ROW_TYPE_CHECK_RESULT_SECTION    = 1;
    protected static final int ROW_TYPE_CHECK_RESULT_THUMBNAIL  = 0;
    protected static final int ROW_TYPE_CHECK_RESULT_NONE       = -1;

    private static final int TOUCH_CHECK_RECT_SIZE = 150; //양손가락이 내려왔을때, 손가락의 사이의 중앙을 기점으로 RECT를 범위를 체크한다.

    private static final int INFLATER_VIEW_COUNT = 120; //뷰 전개에 많은 시간이 소비되니, 미리 만들어 놓는다. (최대 150개 정도까지 생성되는 것 같다.)

    private float CLICK_CHECK_VALID_PIXEL;

    protected ImageSelectActivityV2 activity = null;

    private SparseArray<ImageSelectPhonePhotoAdapter> arPhotoAdpaters = null;
    private SparseArray<SnapsSuperRecyclerView> arRecyclerViews = null;

    private SnapsSuperRecyclerView nextTargetRecyclerView = null;
    private SnapsSuperRecyclerView prevTargetRecyclerView = null;
    private ImageSelectPhonePhotoAdapter nextTargetAdapter = null;
    private ImageSelectPhonePhotoAdapter prevTargetAdapter = null;

    private GooglePhotoStyleFrontView frontView = null;
    private FrameLayout animationLayout = null;

    private eGOOGLE_STYLE_DEPTH currentDapth = null;
    private eGOOGLE_STYLE_DEPTH nextTargetDepth = null;
    private eGOOGLE_STYLE_DEPTH prevTargetDepth = null;

    private GooglePhotoStylePhoneFragmentProcessor googlePhotoStyleUIProcessor = null;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private eMOTION_STATE motionState = eMOTION_STATE.IDLE;
    private eMOTION_STATE lastMotionHistory = eMOTION_STATE.IDLE; //연속을 확대 또는 축소 되는 걸 막기 위해

    private Point ptFirstEvent = new Point();
    private Point ptSecondEvent = new Point();

    private SquareRelativeLayout currentAnimationOffsetView = null;
    private SquareRelativeLayout nextTargetAnimationOffsetView = null;
    private SquareRelativeLayout prevTargetAnimationOffsetView = null;
    private GalleryCursorRecord.PhonePhotoFragmentItem currentAnimationOffsetItem = null;
    private GalleryCursorRecord.PhonePhotoFragmentItem nextTargetAnimationOffsetItem = null;
    private GalleryCursorRecord.PhonePhotoFragmentItem prevTargetAnimationOffsetItem = null;

    private IImageSelectFragmentItemClickListener fragmentItemClickListener = null;

    private IImageSelectDragItemListener dragItemListener = null;

    private LinkedList<View> queInflaterView = null;

    protected SnapsHandler snapsHandler = null;

    protected long lastScrollToPositionWithOffsetTime = 0l;
    protected long lastTryFindFirstVisibleTargetViewTime = 0l;

    private long lPrevClickedTime = 0l;

    private SquareRelativeLayout lastFindFirstVisibleTargetView = null;

    private int recyclerViewBaseOffsetY = -1;
    private int currentRecyclerViewComputeVerticalScrollOffset = 0;

    private boolean isActionPointerDown = false;
    private boolean isLockPinchMotion = false; //애니메이션이 연속적으로 동작하지 않게 하기 위해, 애니메이션이 진행 중일때는 잠근다.
    private PointF ptActionDown = null;
    private LayoutInflater inflater = null;
    private Timer dragTimer = null;
    private TimerTask dragTimerTask = null;
    private boolean isDrag = false;

    public UIPinchMotionDetector(ImageSelectActivityV2 activity, GooglePhotoStylePhoneFragmentProcessor processor) {
        this.activity = activity;
        this.googlePhotoStyleUIProcessor = processor;
        if (processor != null) {
            this.currentDapth = processor.getCurrentDepth();
        }

        init();
    }

    protected abstract void clearAnimations();

    //year depth에서 썸네일을 클릭할 경우 다음 UI step으로 넘어가야 한다.
    public void switchUIForNextDepth(SquareRelativeLayout offsetView) {
        if (offsetView == null) return;

        setCurrentAnimationOffsetView(offsetView);

        findCurrentAnimationOffsetItem();

        scrollToPositionWithTargetOffset(eMOTION_STATE.SCALE_NEXT_DEPTH);

        lockViewsScroll();

        if (snapsHandler != null) {
            snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_SWITCH_NEXT_UI, SCROLL_TO_OFFSET_DELAY_TIME); //스크롤 offset이 동작할 시간이 필요하니, 약간의 딜레이를 준다.
        }
    }

    public boolean isLockPinchMotion() {
        return isLockPinchMotion;
    }

    public void setLockPinchMotion(boolean lockPinchMotion) {
        isLockPinchMotion = lockPinchMotion;
    }

    public void setDataScrollBarLock() {
        if (googlePhotoStyleUIProcessor != null)
            googlePhotoStyleUIProcessor.setDataScrollBarLock();
    }

    public void requestUnlockPinchMotionAfterDelay() {
        if (googlePhotoStyleUIProcessor != null)
            googlePhotoStyleUIProcessor.requestUnlockPinchMotionAfterDelay();
    }

    public int getRecyclerViewBaseOffsetY() {
        return recyclerViewBaseOffsetY;
    }

    public void setFragmentItemClickListener(IImageSelectFragmentItemClickListener fragmentItemClickListener) {
        this.fragmentItemClickListener = fragmentItemClickListener;
    }

    public void setDragItemListener(IImageSelectDragItemListener dragItemListener) {
        this.dragItemListener = dragItemListener;
    }

    public GalleryCursorRecord.PhonePhotoFragmentItem getCurrentAnimationOffsetItem() {
        return currentAnimationOffsetItem;
    }

    public void setCurrentAnimationOffsetItem(GalleryCursorRecord.PhonePhotoFragmentItem currentAnimationOffsetItem) {
        this.currentAnimationOffsetItem = currentAnimationOffsetItem;
    }

    public GalleryCursorRecord.PhonePhotoFragmentItem getPrevTargetAnimationOffsetItem() {
        return prevTargetAnimationOffsetItem;
    }

    public void setPrevTargetAnimationOffsetItem(GalleryCursorRecord.PhonePhotoFragmentItem prevTargetAnimationOffsetItem) {
        this.prevTargetAnimationOffsetItem = prevTargetAnimationOffsetItem;
    }

    public GalleryCursorRecord.PhonePhotoFragmentItem getNextTargetAnimationOffsetItem() {
        return nextTargetAnimationOffsetItem;
    }

    public void setNextTargetAnimationOffsetItem(GalleryCursorRecord.PhonePhotoFragmentItem nextTargetAnimationOffsetItem) {
        this.nextTargetAnimationOffsetItem = nextTargetAnimationOffsetItem;
    }

    public eGOOGLE_STYLE_DEPTH getCurrentDapth() {
        return currentDapth;
    }

    public void setCurrentDapth(eGOOGLE_STYLE_DEPTH currentDapth) {
        this.currentDapth = currentDapth;
    }

    public FrameLayout getAnimationLayout() {
        return animationLayout;
    }

    public void setAnimationLayout(FrameLayout animationLayout) {
        this.animationLayout = animationLayout;
    }

    public GooglePhotoStyleFrontView getFrontView() {
        return frontView;
    }

    public void setFrontView(GooglePhotoStyleFrontView frontView) {
        this.frontView = frontView;
    }

    public eGOOGLE_STYLE_DEPTH getNextTargetDepth() {
        return nextTargetDepth;
    }

    public void setNextTargetDepth(eGOOGLE_STYLE_DEPTH nextTargetDepth) {
        this.nextTargetDepth = nextTargetDepth;
    }

    public GooglePhotoStylePhoneFragmentProcessor getGooglePhotoStyleUIProcessor() {
        return googlePhotoStyleUIProcessor;
    }

    public eMOTION_STATE getLastMotionHistory() {
        return lastMotionHistory;
    }

    public void setLastMotionHistory(eMOTION_STATE lastMotionHistory) {
        this.lastMotionHistory = lastMotionHistory;
    }

    public SnapsSuperRecyclerView getNextTargetRecyclerView() {
        return nextTargetRecyclerView;
    }

    public SnapsSuperRecyclerView getPrevTargetRecyclerView() {
        return prevTargetRecyclerView;
    }

    public void setPrevTargetRecyclerView(SnapsSuperRecyclerView prevTargetRecyclerView) {
        this.prevTargetRecyclerView = prevTargetRecyclerView;
    }

    public ImageSelectPhonePhotoAdapter getPrevTargetAdapter() {
        return prevTargetAdapter;
    }

    public void setPrevTargetAdapter(ImageSelectPhonePhotoAdapter prevTargetAdapter) {
        this.prevTargetAdapter = prevTargetAdapter;
    }

    public eGOOGLE_STYLE_DEPTH getPrevTargetDepth() {
        return prevTargetDepth;
    }

    public void setPrevTargetDepth(eGOOGLE_STYLE_DEPTH prevTargetDepth) {
        this.prevTargetDepth = prevTargetDepth;
    }

    public void setNextTargetRecyclerView(SnapsSuperRecyclerView nextTargetRecyclerView) {
        this.nextTargetRecyclerView = nextTargetRecyclerView;
    }

    public ImageSelectPhonePhotoAdapter getNextTargetAdapter() {
        return nextTargetAdapter;
    }

    public void setNextTargetAdapter(ImageSelectPhonePhotoAdapter nextTargetAdapter) {
        this.nextTargetAdapter = nextTargetAdapter;
    }

    public SquareRelativeLayout getCurrentAnimationOffsetView() {
        return currentAnimationOffsetView;
    }

    public void setCurrentAnimationOffsetView(SquareRelativeLayout currentAnimationOffsetView) {
        this.currentAnimationOffsetView = currentAnimationOffsetView;
    }

    public SquareRelativeLayout getNextTargetAnimationOffsetView() {
        return nextTargetAnimationOffsetView;
    }

    public void setNextTargetAnimationOffsetView(SquareRelativeLayout nextTargetAnimationOffsetView) {
        this.nextTargetAnimationOffsetView = nextTargetAnimationOffsetView;
    }

    public SquareRelativeLayout getPrevTargetAnimationOffsetView() {
        return prevTargetAnimationOffsetView;
    }

    public void setPrevTargetAnimationOffsetView(SquareRelativeLayout prevTargetAnimationOffsetView) {
        this.prevTargetAnimationOffsetView = prevTargetAnimationOffsetView;
    }

    public eMOTION_STATE getMotionState() {
        return motionState;
    }

    public void setMotionState(eMOTION_STATE motionState) {
        this.motionState = motionState;
    }

    public void releaseInstance() {

        if (arRecyclerViews != null) {
            arRecyclerViews.clear();
            arRecyclerViews = null;
        }

        if (nextTargetRecyclerView != null) {
            nextTargetRecyclerView = null;
        }

        if (prevTargetRecyclerView != null) {
            prevTargetRecyclerView = null;
        }

        if (nextTargetAdapter != null) {
            nextTargetAdapter = null;
        }

        if (prevTargetAdapter != null) {
            prevTargetAdapter = null;
        }

        if (frontView != null) {
            frontView.releaseInstance();
            frontView = null;
        }

        if (animationLayout != null) {
            animationLayout = null;
        }

        if (googlePhotoStyleUIProcessor != null) {
            googlePhotoStyleUIProcessor = null;
        }

        if (currentAnimationOffsetView != null) {
            currentAnimationOffsetView = null;
        }

        if (nextTargetAnimationOffsetItem != null) {
            nextTargetAnimationOffsetItem = null;
        }

        if (prevTargetAnimationOffsetItem != null) {
            prevTargetAnimationOffsetItem = null;
        }

        if (inflater != null) {
            inflater = null;
        }

        if (activity != null) {
            activity = null;
        }

        if (queInflaterView != null) {
            for (View view : queInflaterView) {
                try {
                    if (view != null) {
                        ImageLoader.clear(activity, view);
                        ViewUnbindHelper.unbindReferences(view, null, false);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            queInflaterView.clear();
            queInflaterView = null;
        }
    }

    protected boolean isFindScaleOffsetView() {
        return currentAnimationOffsetView != null;
    }

    protected void lockViewsScroll() {
        if (arRecyclerViews != null) {
            UIPinchMotionDetector.eMOTION_STATE[] arDepths = UIPinchMotionDetector.eMOTION_STATE.values();
            for (UIPinchMotionDetector.eMOTION_STATE depth : arDepths) {
                if (depth == null) continue;
                SnapsSuperRecyclerView currentRecyclerView = arRecyclerViews.get(depth.ordinal());
                if (currentRecyclerView != null) {
                    CustomGridLayoutManager gridLayoutManager = (CustomGridLayoutManager) currentRecyclerView.getLayoutManager();
                    gridLayoutManager.setScrollEnabled(false);
                }
            }
        }
    }

    protected void lockViewsDragScroll() {
        if (arRecyclerViews != null) {
            ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH[] arDepths = ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.values();
            for (ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH depth : arDepths) {
                if (depth == null) continue;
                SnapsSuperRecyclerView currentRecyclerView = arRecyclerViews.get(depth.ordinal());
                if (currentRecyclerView != null) {
                    CustomGridLayoutManager gridLayoutManager = (CustomGridLayoutManager) currentRecyclerView.getLayoutManager();
                    gridLayoutManager.setScrollEnabled(false);
                }
            }
        }
    }

    protected void smoothDragScrollHandler(final int direction) {
        if(dragTimer == null) {
            dragTimer = new Timer();
            dragTimerTask = new TimerTask() {
                @Override
                public void run() {
                    smoothDragScroll(direction);
                }
            };
        }
        if(direction != 2) {
            dragTimer.schedule(dragTimerTask,0,100);
        }else {
            dragTimer.cancel();
            dragTimer = null;
            dragTimerTask = null;
        }
    }

    protected void smoothDragScroll(final int direction) {
        if (arRecyclerViews != null) {
            ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH[] arDepths = ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.values();
            for (ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH depth : arDepths) {
                if (depth == null) continue;
                final SnapsSuperRecyclerView currentRecyclerView = arRecyclerViews.get(depth.ordinal());
                if (currentRecyclerView != null) {
                    CustomGridLayoutManager gridLayoutManager = (CustomGridLayoutManager) currentRecyclerView.getLayoutManager();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int scroll = 0;
                            if(direction == 1) {
                                scroll = 200;
                            } else {
                                scroll = -200;
                            }
                            currentRecyclerView.smoothScrollBy(0,scroll);
                        }
                    });

                }
            }
        }
    }

    protected void unLockViewsScroll() {
        if (arRecyclerViews != null) {
            UIPinchMotionDetector.eMOTION_STATE[] arDepths = UIPinchMotionDetector.eMOTION_STATE.values();
            for (UIPinchMotionDetector.eMOTION_STATE depth : arDepths) {
                if (depth == null) continue;
                SnapsSuperRecyclerView currentRecyclerView = arRecyclerViews.get(depth.ordinal());
                if (currentRecyclerView != null) {
                    CustomGridLayoutManager gridLayoutManager = (CustomGridLayoutManager) currentRecyclerView.getLayoutManager();
                    gridLayoutManager.setScrollEnabled(true);
                }
            }
        }
    }

    protected void unLockViewsDragScroll() {
        if (arRecyclerViews != null) {
            ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH[] arDepths = ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.values();
            for (ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH depth : arDepths) {
                if (depth == null) continue;
                SnapsSuperRecyclerView currentRecyclerView = arRecyclerViews.get(depth.ordinal());
                if (currentRecyclerView != null) {
                    CustomGridLayoutManager gridLayoutManager = (CustomGridLayoutManager) currentRecyclerView.getLayoutManager();
                    gridLayoutManager.setScrollEnabled(true);
                }
            }
        }
    }

    protected SnapsSuperRecyclerView findTargerRecyclerView(ePINCH_TARGET_TYPE targetType) {
        if (arRecyclerViews == null || currentDapth == null) return null;
        switch (targetType) {
            case PREV:
                if (currentDapth == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR)
                    return null;
                return arRecyclerViews.get(currentDapth.ordinal() - 1);
            case NEXT:
                if (currentDapth == eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED)
                    return null;
                return arRecyclerViews.get(currentDapth.ordinal() + 1);
        }
        return null;
    }

    protected eGOOGLE_STYLE_DEPTH findTargetDepth(ePINCH_TARGET_TYPE targetType) {
        if (currentDapth == null) return null;

        eGOOGLE_STYLE_DEPTH[]  arDepth = eGOOGLE_STYLE_DEPTH.values();
        switch (targetType) {
            case PREV:
                if (currentDapth == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR)
                    return null;
                return arDepth[currentDapth.ordinal() - 1];
            case NEXT:
                if (currentDapth == eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED)
                    return null;
                return arDepth[currentDapth.ordinal() + 1];
        }
        return null;
    }


    protected ImageSelectPhonePhotoAdapter findTargetAdapter(ePINCH_TARGET_TYPE targetType) {
        if (arRecyclerViews == null || currentDapth == null) return null;
        switch (targetType) {
            case PREV:
                if (currentDapth == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR)
                    return null;
                return arPhotoAdpaters.get(currentDapth.ordinal() - 1);
            case NEXT:
                if (currentDapth == eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED)
                    return null;
                return arPhotoAdpaters.get(currentDapth.ordinal() + 1);
        }
        return null;
    }

    public boolean isActiveAnimation() {
        return motionState != null && motionState != eMOTION_STATE.IDLE;
    }

    protected boolean isIdleState() {
        return motionState != null && motionState == eMOTION_STATE.IDLE;
    }

    protected SnapsSuperRecyclerView getCurrentRecyclerView() {
        return googlePhotoStyleUIProcessor != null ? googlePhotoStyleUIProcessor.getCurrentPhotoRecyclerView() : null;
    }

    protected ImageSelectPhonePhotoAdapter getCurrentAdapter() {
        return googlePhotoStyleUIProcessor != null ? googlePhotoStyleUIProcessor.getCurrentPhotoAdapter() : null;
    }

    public void reset() {
        SnapsSuperRecyclerView currentRecyclerView = getCurrentRecyclerView();
        if (currentRecyclerView == null) return;

        if (recyclerViewTouchListener != null)
            currentRecyclerView.removeOnItemTouchListener(recyclerViewTouchListener);

        currentRecyclerView.addOnItemTouchListener(recyclerViewTouchListener);

        this.currentDapth = googlePhotoStyleUIProcessor.getCurrentDepth();
        this.nextTargetDepth = currentDapth;
        this.nextTargetRecyclerView = null;
        this.nextTargetAdapter = null;
        this.motionState = eMOTION_STATE.IDLE;

        initTargetControls();
    }

    private RecyclerView.OnItemTouchListener recyclerViewTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            if (activity != null)
                activity.onChangedRecyclerViewScroll();

            if (mScaleGestureDetector != null)
                mScaleGestureDetector.onTouchEvent(e);
            onRecyclerViewItemTouchEvent(rv, e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    };

    private void init() {
        if (googlePhotoStyleUIProcessor == null) return;

        ptActionDown = new PointF();

        CLICK_CHECK_VALID_PIXEL = UIUtil.convertDPtoPX(activity, 10);

        arPhotoAdpaters = googlePhotoStyleUIProcessor.getPhotoAdapters();

        arRecyclerViews = googlePhotoStyleUIProcessor.getRecyclerViews();

        frontView = googlePhotoStyleUIProcessor.getFrontView();

        queInflaterView = new LinkedList<>();

        snapsHandler = new SnapsHandler(this);

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initInflaterViews();

        if (frontView != null) {
            setAnimationLayout(frontView.getAnimationLayout());
        }

        mScaleGestureDetector = new ScaleGestureDetector(activity, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return processUIByScaleGesture(detector);
            }
        });
    }

    @Override
    public boolean processUIByScaleGesture(ScaleGestureDetector detector) {
        if (detector == null || isActiveAnimation() || isLockPinchMotion()) return false;

        if (currentAnimationOffsetView != null) {
            boolean isScaleNextDepth = detector.getScaleFactor() > 1.f;
            boolean isScalePrevDepth = detector.getScaleFactor() < 1.f;

            if (isScaleNextDepth) {
                if (lastMotionHistory == eMOTION_STATE.SCALE_NEXT_DEPTH) return false; //연속으로 다음 스케일로 변하는 건 막는다.
                if (getNextTargetRecyclerView() != null && getNextTargetAdapter() != null && getNextTargetDepth() != null) {
                    scaleNextDepth(detector.getScaleFactor());
                }
            } else if (isScalePrevDepth) {
                if (lastMotionHistory == eMOTION_STATE.SCALE_PREV_DEPTH) return false; //연속으로 다음 스케일로 변하는 건 막는다.
                if (getPrevTargetRecyclerView() != null && getPrevTargetAdapter() != null && getPrevTargetDepth() != null) {
                    scalePrevDepth(detector.getScaleFactor());
                }
            }
        }

        return false;
    }

    protected SquareRelativeLayout findTargetViewByRecyclerViewFirstVisiblePosition(eMOTION_STATE motionState) {
        if (System.currentTimeMillis() - lastTryFindFirstVisibleTargetViewTime < (SCROLL_TO_OFFSET_DELAY_TIME * 2)) return lastFindFirstVisibleTargetView;
        lastTryFindFirstVisibleTargetViewTime = System.currentTimeMillis();

        SnapsSuperRecyclerView recyclerView = getCurrentRecyclerView();
        ImageSelectPhonePhotoAdapter currentAdapter = getCurrentAdapter();

        if (recyclerView == null || currentAdapter == null) return null;

        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        int firstVisiblePosition = Math.max(0, layoutManager.findFirstVisibleItemPosition() - 1);
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition() - 1;

        GalleryCursorRecord.PhonePhotoFragmentItem offsetItem = null;

        for (int ii = firstVisiblePosition; ii <= lastVisiblePosition; ii++)  {
            offsetItem = currentAdapter.getPhotoItem(ii);
            if (offsetItem != null
                    && offsetItem.getHolderType() == eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL
                    && offsetItem.getViewHolder() instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) {
                break;
            }
        }

        if (offsetItem == null) return null;

        ImageSelectAdapterHolders.PhotoFragmentItemHolder photoFragmentItemHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) offsetItem.getViewHolder();
        SquareRelativeLayout offsetHolderView = photoFragmentItemHolder.getParentView();

        setCurrentAnimationOffsetView(offsetHolderView);

        findCurrentAnimationOffsetItem();

        scrollToPositionWithTargetOffset(motionState);

        lastFindFirstVisibleTargetView = motionState == eMOTION_STATE.SCALE_NEXT_DEPTH ? getNextTargetAnimationOffsetView() : getPrevTargetAnimationOffsetView();

        return lastFindFirstVisibleTargetView;
    }

    private SquareRelativeLayout findTargetView(RecyclerView currentRecyclerView, Object object, float eventY) {
        return findTargetView(currentRecyclerView, object, eventY, false);
    }

    private SquareRelativeLayout findTargetView(RecyclerView currentRecyclerView, Object object, float eventY, boolean retry) {
        if (object == null) return null;

        if( object instanceof SquareRelativeLayout) {
            return (SquareRelativeLayout) object;
        } else {
            if (retry) return null;

            //섹션일 가능성이 높다. 약간 아래쪽 또는 위쪽을 검색 해 본다.
            Object targetObj = currentRecyclerView.findChildViewUnder(10, eventY + UIUtil.convertDPtoPX(activity, 37)); //섹션 높이를 감안해서
            if (targetObj == null) {
                targetObj = currentRecyclerView.findChildViewUnder(10, eventY - UIUtil.convertDPtoPX(activity, 37)); //섹션 높이
            }

            return findTargetView(currentRecyclerView, targetObj, eventY, true);
        }
    }

    private Object findTargetViewByOffsetRect(RecyclerView currentRecyclerView, int centerX, int centerY) {
        Rect checkRect  = new Rect();
        checkRect.set(centerX - (TOUCH_CHECK_RECT_SIZE / 2), centerY - (TOUCH_CHECK_RECT_SIZE / 2), centerX + (TOUCH_CHECK_RECT_SIZE / 2), centerY + (TOUCH_CHECK_RECT_SIZE / 2));

        Object targetObj = currentRecyclerView.findChildViewUnder(Math.max(0, checkRect.left), Math.max(0, checkRect.top));
        if (targetObj != null) {
            return targetObj;
        } else {
            targetObj = currentRecyclerView.findChildViewUnder(checkRect.right, Math.max(0, checkRect.top));
            if (targetObj != null) {
                return targetObj;
            } else {
                targetObj = currentRecyclerView.findChildViewUnder(Math.max(0, checkRect.left), checkRect.bottom);
                if (targetObj != null) {
                    return targetObj;
                } else {
                    targetObj = currentRecyclerView.findChildViewUnder(checkRect.right, checkRect.bottom);
                    if (targetObj != null) {
                        return targetObj;
                    } else {
                        return null;
                    }
                }
            }
        }
    }


    //손가락이 2개 내려왔을 때, 애니메이션의 주체가 될 뷰를 찾는다.
    private SquareRelativeLayout findTargetView(RecyclerView currentRecyclerView, MotionEvent event) {
        ptFirstEvent.set((int)event.getX(0), (int)event.getY(0));

        if (event.getPointerCount() > 1) {
            ptSecondEvent.set((int)event.getX(1), (int)event.getY(1));
        } else {
            ptSecondEvent.set((int)event.getX(0), (int)event.getY(0));
        }

        //두 손가락 가운데 있는 뷰를 기준으로 한다.
        int maxX = Math.max(ptFirstEvent.x, ptSecondEvent.x);
        int minX = Math.min(ptFirstEvent.x, ptSecondEvent.x);
        int maxY = Math.max(ptFirstEvent.y, ptSecondEvent.y);
        int minY = Math.min(ptFirstEvent.y, ptSecondEvent.y);
        int centerX =  minX + (maxX - minX);
        int centerY =  minY + (maxY - minY);

        SquareRelativeLayout targetView = null;

        if (currentRecyclerView != null) {
            //우선 양 손가락 중앙을 기점으로 탐색해 본다.
            Object targetObj = currentRecyclerView.findChildViewUnder(centerX, centerY);
            if (targetObj != null) {
                targetView = findTargetView(currentRecyclerView, targetObj, centerY);
                if (targetView != null) return targetView;
            }

            //손가락 중앙을 기준으로 Rect에 해당하는 지점을 검사 해 본다.
            targetObj = findTargetViewByOffsetRect(currentRecyclerView, centerX, centerY);
            if (targetObj != null) {
                targetView = findTargetView(currentRecyclerView, targetObj, centerY);
                if (targetView != null) return targetView;
            }

            //비어 있는 공간 (오른쪽일 수 있으니, 가장 좌측에 있는 뷰를 찾아 본다.)
            targetObj = currentRecyclerView.findChildViewUnder(10, centerY); //좌측 마진이 들어갈지도 모를 것을 고려해서 10
            if (targetObj != null) {
                targetView = findTargetView(currentRecyclerView, targetObj, centerY);
                if (targetView != null) return targetView;
            }

            //그래도 없다면 처음에 닿은 손가락에 위치한 뷰를 찾아 본다.
            targetObj = currentRecyclerView.findChildViewUnder(ptFirstEvent.x, ptFirstEvent.y); //좌측 마진을 고려해서 10
            if (targetObj != null) {
                targetView = findTargetView(currentRecyclerView, targetObj, centerY);
                if (targetView != null) return targetView;
            }

            //그것 마저 없다면 두번 째로 닿은 손가락에 위치한 뷰도 찾아 본다..
            targetObj = currentRecyclerView.findChildViewUnder(ptSecondEvent.x, ptSecondEvent.y); //좌측 마진을 고려해서 10
            if (targetObj != null) {
                targetView = findTargetView(currentRecyclerView, targetObj, centerY);
                if (targetView != null) return targetView;
            }

            //아이템이 상단에 위치해서 못 찾았다고 본다...첫번째 사진 아이템을 기준으로 해 버리자. 그것마저 없으면 정말 없는거다.
            GridLayoutManager gridLayoutManager = (GridLayoutManager) currentRecyclerView.getLayoutManager();
            if (gridLayoutManager != null) {
                int firstItem = Math.max(0, gridLayoutManager.findFirstVisibleItemPosition() - 1);
                int lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition() - 1;

                for (int ii = firstItem; ii <= lastVisiblePosition; ii++) {
                    View view = gridLayoutManager.findViewByPosition(ii);
                    if (view != null) {
                        targetView = findTargetView(currentRecyclerView, view, centerY);
                        if (targetView != null) return targetView;
                    }
                }
            }
        }

        return null;
    }

    private void findCurrentAnimationOffsetItem() {
        SquareRelativeLayout offsetView = getCurrentAnimationOffsetView();
        if (offsetView != null && offsetView.getHolder() != null) {
            String mapKey = offsetView.getHolder().getMapKey();
            if (mapKey != null) {
                ImageSelectPhonePhotoAdapter adapter = getCurrentAdapter();
                if (adapter != null) {
                    GalleryCursorRecord.PhonePhotoFragmentItem photoFragmentItem = adapter.findItemByImageKey(mapKey);
                    setCurrentAnimationOffsetItem(photoFragmentItem);
                }
            }
        }
    }

    private void initTargetControls() {
        setNextTargetRecyclerView(findTargerRecyclerView(ePINCH_TARGET_TYPE.NEXT));

        setNextTargetAdapter(findTargetAdapter(ePINCH_TARGET_TYPE.NEXT));

        setNextTargetDepth(findTargetDepth(ePINCH_TARGET_TYPE.NEXT));

        setPrevTargetRecyclerView(findTargerRecyclerView(ePINCH_TARGET_TYPE.PREV));

        setPrevTargetAdapter(findTargetAdapter(ePINCH_TARGET_TYPE.PREV));

        setPrevTargetDepth(findTargetDepth(ePINCH_TARGET_TYPE.PREV));
    }

    private void tryFindScaleOffsetView(RecyclerView currentRecyclerView, MotionEvent event) {
        if (currentAnimationOffsetView != null || !isIdleState()) return;

        SquareRelativeLayout targetView = findTargetView(currentRecyclerView, event);
        if (targetView != null) { //찾았다.
            setCurrentAnimationOffsetView(targetView);

            findCurrentAnimationOffsetItem();

            scrollToPositionWithTargetOffset(eMOTION_STATE.IDLE);

            setCurrentRecyclerViewComputeVerticalScrollOffset();

            lockViewsScroll();
        }
    }

    protected void removeDateScrollBar() {
        if (googlePhotoStyleUIProcessor != null)
            googlePhotoStyleUIProcessor.removeDateScrollBar();
    }

    public boolean isEnableClick() {
        //hidden상태에서는 클릭 되지 않도록
        ImageSelectPhonePhotoAdapter currentPhotoAdapter = getCurrentAdapter();
        if (currentPhotoAdapter != null) {
            GooglePhotoStyleAdapterStrategyBase.AdapterAttribute attribute = currentPhotoAdapter.getAttribute();
            if (attribute != null && attribute.isHidden()) return false;
        }

        //연속 클릭을 막는다.
        return System.currentTimeMillis() - lPrevClickedTime > LIMIT_SUCCESSIVE_CLICK_TIME;
    }

    private boolean checkClickAction(MotionEvent event) {
        if (event == null || ptActionDown == null || !isEnableClick()) return false;
        float moveX = Math.abs(ptActionDown.x - event.getX());
        float moveY = Math.abs(ptActionDown.y - event.getY());
        return moveX < CLICK_CHECK_VALID_PIXEL && moveY < CLICK_CHECK_VALID_PIXEL;
    }

    private boolean checkMovingAction(MotionEvent event) {
        if (event == null || ptActionDown == null || !isEnableClick()) return false;
        float moveX = Math.abs(ptActionDown.x - event.getX());
        float moveY = Math.abs(ptActionDown.y - event.getY());
        return moveX > CLICK_CHECK_VALID_PIXEL && moveY > CLICK_CHECK_VALID_PIXEL;
    }

    private void setRecyclerViewBaseOffsetY() {
        if (recyclerViewBaseOffsetY > -1) return;
        int[] recyclerViewLocation = new int[2];
        getCurrentRecyclerView().getLocationOnScreen(recyclerViewLocation);
        recyclerViewBaseOffsetY = recyclerViewLocation[1];
    }
    boolean isDragMode = false;
    Handler handler = null;
    RecyclerView currentRecyclerViewTest;
    private void longClickCheck(int type) {
        longClickCheck(null,null,type);
    }
    private void longClickCheck(final RecyclerView currentRecyclerView, final MotionEvent event,int type) {
        if(activity.isSingleChooseType() || !Const_PRODUCT.isMultiImageSelectProduct()) return;
        if(handler == null) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    float x = bundle.getFloat("x");
                    float y = bundle.getFloat("y");
                    RecyclerView recyclerView = (RecyclerView)msg.obj;
                    isDragMode = true;
                    lockViewsDragScroll();
                    selectDragItem(recyclerView, null, x, y, IImageSelectDragItemListener.FIRST_ITEM);
                }
            };
        }
        if(type == 0) {
            Bundle bundle = new Bundle();
            bundle.putFloat("x",event.getX());
            bundle.putFloat("y",event.getY());
            Message message = new Message();
            message.setData(bundle);
            message.obj = currentRecyclerView;
            handler.sendMessageDelayed(message,1000);
        } else {
            handler.removeMessages(0);
        }
    }

    @Override
    public boolean onRecyclerViewItemTouchEvent(RecyclerView currentRecyclerView, MotionEvent event) {
        currentRecyclerViewTest = currentRecyclerView;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                    longClickCheck(currentRecyclerView, event, 0);
                    setRecyclerViewBaseOffsetY();
                    initInflaterViews();
                    currentAnimationOffsetView = null;
                    lastMotionHistory = eMOTION_STATE.IDLE;
                    motionState = eMOTION_STATE.IDLE;
                    isActionPointerDown = false;
                    ptActionDown.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //2번쨰 손가락이 내려왔을 때
                if(!isDragMode) {
                    longClickCheck(-1);
                    isActionPointerDown = true;
                    tryFindScaleOffsetView(currentRecyclerView, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDragMode) {
                    if(checkMovingAction(event)) {
                        selectDragItem(currentRecyclerView,event,IImageSelectDragItemListener.DRAG_ITEM);
                        int bottom = currentRecyclerView.getBottom();
                        int top = currentRecyclerView.getTop();
                        if(event.getY() >= bottom - 300.f) {
                            if(!isDrag) {
                                smoothDragScrollHandler(1);
                                unLockViewsDragScroll();
                                isDrag = true;
                            }
                        } else if(event.getY() <= top + 300.f){
                            if(!isDrag) {
                                smoothDragScrollHandler(0);
                                unLockViewsDragScroll();
                                isDrag = true;
                            }
                        } else  {
                            if(isDrag) {
                                smoothDragScrollHandler(2);
                                lockViewsDragScroll();
                                isDrag =false;
                            }
                        }

                    }
                }else {
                    if(checkMovingAction(event)) {
                        longClickCheck(-1);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                longClickCheck(-1);
                if (isActionPointerDown){ //핀치 동작 종료
                    lastMotionHistory = eMOTION_STATE.IDLE;
                    motionState = eMOTION_STATE.IDLE;

                    setCurrentAnimationOffsetView(null);

                    setCurrentAnimationOffsetItem(null);

                    setNextTargetAnimationOffsetItem(null);

                    setNextTargetAnimationOffsetView(null);

                    setPrevTargetAnimationOffsetItem(null);

                    setPrevTargetAnimationOffsetView(null);

                    lastFindFirstVisibleTargetView = null;

                } else { //ONClick
                    if(isDragMode) {
                        isDragMode = false;
                        if(isDrag) {
                            smoothDragScrollHandler(2);
                            lockViewsDragScroll();
                            isDrag =false;
                        }

                        selectDragItem(currentRecyclerView,event,IImageSelectDragItemListener.LAST_ITEM);

                        requestUnlockPinchMotionAfterDelay();
                    } else {
                        if (checkClickAction(event)) {
                            View view = currentRecyclerView.findChildViewUnder(event.getX(), event.getY());
                            if (view != null && view instanceof SquareRelativeLayout) {
                                SquareRelativeLayout squareRelativeLayout = (SquareRelativeLayout) view;

                                ImageSelectAdapterHolders.PhotoFragmentItemHolder holder = squareRelativeLayout.getHolder();
                                if (holder != null) {

                                    if (isEnableClick()) {
                                        if (fragmentItemClickListener != null) {
                                            fragmentItemClickListener.onClickFragmentItem(holder);
                                        }
                                    }

                                    lPrevClickedTime = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                }

                unLockViewsScroll();

                if (snapsHandler != null) {
                    snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_INIT_INFLATER_VIEWS, (GooglePhotoStyleAnimationView.ANIM_TIME + 50));
                }

                break;
                default:
                    longClickCheck(-1);
                    break;
        }

        return false;
    }
    private void selectDragItem(RecyclerView currentRecyclerView,MotionEvent event,int type) {
        selectDragItem(currentRecyclerView,event,-1,-1,type);
    }
    private void selectDragItem(RecyclerView currentRecyclerView,MotionEvent event,float x,float y,int type) {
        removeDateScrollBar();
        setDataScrollBarLock();

        View view = null;
        if(x != -1) {
            view = currentRecyclerView.findChildViewUnder(x, y);

        } else {
            view = currentRecyclerView.findChildViewUnder(event.getX(), event.getY());
        }
        if (view != null && view instanceof SquareRelativeLayout) {
            SquareRelativeLayout squareRelativeLayout = (SquareRelativeLayout) view;
            ImageSelectAdapterHolders.PhotoFragmentItemHolder holder = squareRelativeLayout.getHolder();
            if (holder != null) {

                if (dragItemListener != null) {
                    dragItemListener.onDragItem(holder,type);
                }
            }
        } else {
            if (IImageSelectDragItemListener.LAST_ITEM == type) {
                if (dragItemListener != null) {
                    dragItemListener.onDragItemEmpty(type);
                }
            }
        }
    }

    private void setCurrentRecyclerViewComputeVerticalScrollOffset() {
        SnapsSuperRecyclerView recyclerView = getCurrentRecyclerView();
        if (recyclerView != null) {
            currentRecyclerViewComputeVerticalScrollOffset = recyclerView.getComputeVerticalScrollOffset();
        }
    }

    protected int getCurrentRecyclerViewComputeVerticalScrollOffset() {
        return currentRecyclerViewComputeVerticalScrollOffset;
    }

    protected View getInflaterView() {
        if (queInflaterView == null || queInflaterView.isEmpty()) return LayoutInflater.from(activity).inflate(R.layout.fragment_imagedetail_item_for_google_style, null, false);
        return queInflaterView.poll();
    }

    private void initInflaterViews() {
        if (queInflaterView == null || inflater == null) return;

        int usedInflaterViewCount = INFLATER_VIEW_COUNT - queInflaterView.size();
        for (int ii = 0; ii < usedInflaterViewCount; ii++) {
            View view = inflater.inflate(R.layout.fragment_imagedetail_item_for_google_style, null);
            this.queInflaterView.offer(view);
        }
    }

    protected static final int HANDLER_MSG_INIT_INFLATER_VIEWS      = 0;
    protected static final int HANDLER_MSG_SWITCH_NEXT_UI           = 1;
    protected static final int HANDLER_MSG_CLEAR_ANIMATIONS         = 2;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_MSG_INIT_INFLATER_VIEWS:
                initInflaterViews();
                break;
            case HANDLER_MSG_SWITCH_NEXT_UI:
                boolean isSuccess = scaleNextDepth(2); //현재 2는 의미가 없다..나중에 실시간 핀치 모션을 적용한다면 적절히 숫자를 넣자.
                if (!isSuccess && msg.arg1 != INVALID_VALUE) { //firstVisibleTargetView로 동작할 수 있으니, 재 시도
                    if (snapsHandler != null) {
                        Message postMsg = new Message();
                        postMsg.arg1 = INVALID_VALUE;
                        postMsg.what = HANDLER_MSG_SWITCH_NEXT_UI;
                        snapsHandler.sendMessageDelayed(postMsg, SCROLL_TO_OFFSET_DELAY_TIME);
                    }
                }
                break;
            case HANDLER_MSG_CLEAR_ANIMATIONS:
                clearAnimations();
                break;
        }
    }
}