package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

/**
 * Created by songhw on 2017. 1. 17..
 */

public class DateDisplayScrollBar extends FrameLayout {
    private final int ALPHA_ANIMATION_DURATION = 500;
    private final int DATE_VIEW_ANIMATION_DURATION = 50;
    private final int DUMMY_HEIGHT = UIUtil.convertDPtoPX( getContext(), 52 ); // 상단 더미 높이
    private final int SCROLL_BAR_TOP_MARGIN = UIUtil.convertDPtoPX( getContext(), 8 ); // 스크롤바 위 그림자 높이
    private final int SCROLL_BAR_BOTTOM_MARGIN = UIUtil.convertDPtoPX( getContext(), 8 ); // 스크롤바 아래 그림자 높이
    private final int SCROLL_BAR_HEIGHT = UIUtil.convertDPtoPX( getContext(), 56 ); // 스크롤바 높이

    private SnapsSuperRecyclerView currentRecyclerView;
    private ValueAnimator scrollBarAnimator, dateViewAnimator;
    private Handler handler;

    private boolean isActivePinchAnimation = false;

    private Runnable hideScrollRunnable = new Runnable() {
        @Override
        public void run() {
            hideScroll();
        }
    };

    private float animTargetValue = 0f, defaultY, defaultLayoutY, positionRate;
    private boolean isScrolling = false, isMoved = false, isSuspended = false;

    private MyTouchListener mMyTouchListener = null;
    private ImageView mImageViewScrollBar;
    private LinearLayout mDateScrollLayout;
    private RelativeLayout mDateTextLayout;
    private RelativeLayout.LayoutParams mDateTextLayoutParams;
    private int mDateTextMargin;
    private TextView mDateTextView;

    private volatile boolean mIsSetScrollBarTouchListener = false;

    public DateDisplayScrollBar(Context context) {
        super(context);
    }

    public DateDisplayScrollBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateDisplayScrollBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onRecieveListTouchEvent ( SnapsSuperRecyclerView snapsSuperRecyclerView ) {
        if( isActivePinchAnimation() || isScrolling || snapsSuperRecyclerView.getRecyclerView().computeVerticalScrollOffset() < 1 ) return;

        if (mMyTouchListener == null) {
            mImageViewScrollBar = findViewById(R.id.scroll_bar);
            mDateTextLayout = findViewById(R.id.date_text_layout);
            mDateTextLayoutParams = (RelativeLayout.LayoutParams)mDateTextLayout.getLayoutParams();
            mDateScrollLayout = findViewById( R.id.date_scroll_layout);
            mDateTextView = findViewById(R.id.date_text);
            mDateTextMargin = UIUtil.convertDPtoPX( getContext(), 12 );

            mMyTouchListener = new MyTouchListener();
        }

        setScrollPosition( snapsSuperRecyclerView );
        showScroll();
    }

    public void releaseInstance() {
        isSuspended = true;

        if (scrollBarAnimator != null) {
            scrollBarAnimator = null;
        }

        if (dateViewAnimator != null) {
            dateViewAnimator = null;
        }

        if (currentRecyclerView != null) {
            currentRecyclerView = null;
        }
    }


    class MyTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if( event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ) {
                toggleDateView(true);
                isScrolling = true;

                float eventRawY = event.getRawY();

                if( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    defaultY = eventRawY;

                    defaultLayoutY = mDateScrollLayout.getY();
                    isMoved = false;
                }
                else if( event.getAction() == MotionEvent.ACTION_MOVE ) {
                    if( defaultY - eventRawY != 0 )
                        isMoved = true;

                    if( isMoved )
                        setScrollPosition(eventRawY);
                }

                refreshDateText();
                //showScroll();
            }
            else {
                toggleDateView(false);
                isScrolling = false;
            }
            return true;
        }
    }

    private void showScroll() {

        if (isActivePinchAnimation()) return;

        setHideScrollRunnable();

        if (!mIsSetScrollBarTouchListener) {
            mImageViewScrollBar.setOnTouchListener(mMyTouchListener);
            mIsSetScrollBarTouchListener = true;
        }

        doAlphaAnimation(0f, 1f );
    }

    public boolean isActivePinchAnimation() {
        return isActivePinchAnimation;
    }

    public void setActivePinchAnimation(boolean activePinchAnimation) {
        isActivePinchAnimation = activePinchAnimation;
    }

    public void requestUnlockPinchMotionAfterDelay() {
        if( handler == null )
            handler = new Handler();
        else
            handler.removeCallbacks( unlockPinchMotionRunnable );
        handler.postDelayed(unlockPinchMotionRunnable, 1500);
    }

    private Runnable unlockPinchMotionRunnable = new Runnable() {
        @Override
        public void run() {
            setActivePinchAnimation(false);
        }
    };

    private void setHideScrollRunnable() {
        if( handler == null )
            handler = new Handler();
        else
            handler.removeCallbacks( hideScrollRunnable );

        handler.postDelayed( hideScrollRunnable, 3000 );
    }

    private void hideScroll() {
        if( isScrolling || isSuspended ) return;

        mIsSetScrollBarTouchListener = false;
        mImageViewScrollBar.setOnTouchListener( null );
        doAlphaAnimation( 1f, 0f );
    }

    public void forceHideScroll() throws Exception {
        mIsSetScrollBarTouchListener = false;
        mImageViewScrollBar.setOnTouchListener( null );

//        if( scrollBarAnimator != null && scrollBarAnimator.isRunning() ) {
//            scrollBarAnimator.cancel();
//        }
//
//        animTargetValue = 0.f;
//
//        DateDisplayScrollBar.this.setVisibility( View.GONE );
//        DateDisplayScrollBar.this.findViewById( R.id.date_text_layout ).setVisibility( View.GONE );
        doAlphaAnimation( 1f, 0f );
    }

    private void toggleDateView(final boolean isShow ) {
        float currentAlpha = mDateTextLayout.getAlpha();
        final float targetAlpha = isShow ? 1f : 0f;

        if( dateViewAnimator != null && dateViewAnimator.isRunning() )
            dateViewAnimator.cancel();

        mDateTextLayout.setAlpha( currentAlpha );

        dateViewAnimator = ValueAnimator.ofFloat( currentAlpha, targetAlpha );
        dateViewAnimator.setDuration( (int)((float)DATE_VIEW_ANIMATION_DURATION * Math.abs((targetAlpha) - currentAlpha)) );
        dateViewAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if( targetAlpha == 1f ) {
                    mDateTextLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if( targetAlpha == 0f ) {
                    mDateTextLayout.setVisibility( View.GONE );
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        dateViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isSuspended) return;

                mDateTextLayout.setAlpha( (float)animation.getAnimatedValue() );
                mDateTextLayoutParams.leftMargin = mDateTextMargin - (int)( (float)animation.getAnimatedValue() * (float)mDateTextMargin );
                mDateTextLayout.setLayoutParams( mDateTextLayoutParams );
            }
        });
        dateViewAnimator.start();
    }

    private float getCurrentPos() {
        return ( (float) currentRecyclerView.getRecyclerView().computeVerticalScrollOffset() - DUMMY_HEIGHT ) / (float)( currentRecyclerView.getRecyclerView().computeVerticalScrollRange() - currentRecyclerView.getRecyclerView().computeVerticalScrollExtent() );
    }

    /**
     * RecyclerView scroll시 scrollBar 위치 셋팅
     */
    public void setScrollPosition( SnapsSuperRecyclerView currentRecyclerView ) {
        this.currentRecyclerView = currentRecyclerView;
        float currentPos = getCurrentPos();
        float topMargin = DUMMY_HEIGHT - SCROLL_BAR_TOP_MARGIN + ( (float)(this.currentRecyclerView.getRecyclerView().getHeight() - DUMMY_HEIGHT - SCROLL_BAR_HEIGHT + SCROLL_BAR_TOP_MARGIN + SCROLL_BAR_BOTTOM_MARGIN ) * currentPos );
        mDateScrollLayout.setY( topMargin );
    }

    /**
     * scrollBar 움직일 때 위치 셋팅 및 RecyclerView scroll
     */
    private void setScrollPosition(float yPos) {
        float topMargin = yPos - defaultY + defaultLayoutY;
        boolean resetBasePosition = false;
        if( topMargin > getHeight() - SCROLL_BAR_HEIGHT + SCROLL_BAR_BOTTOM_MARGIN ) {
            topMargin = getHeight() - SCROLL_BAR_HEIGHT + SCROLL_BAR_BOTTOM_MARGIN;
            resetBasePosition = true;
        }
        else if( topMargin < DUMMY_HEIGHT - SCROLL_BAR_TOP_MARGIN ) {
            topMargin = DUMMY_HEIGHT - SCROLL_BAR_TOP_MARGIN;
            resetBasePosition = true;
        }

        if( resetBasePosition ) {
            defaultY = yPos;
            defaultLayoutY = topMargin;
        }

        final float newPos = ( topMargin - DUMMY_HEIGHT + SCROLL_BAR_TOP_MARGIN ) / ( (float) currentRecyclerView.getRecyclerView().getHeight() - (float)SCROLL_BAR_HEIGHT - DUMMY_HEIGHT + SCROLL_BAR_TOP_MARGIN );
        positionRate = newPos > 1f ? 1f : newPos;
        LinearLayoutManager manager = (LinearLayoutManager) currentRecyclerView.getRecyclerView().getLayoutManager();
        manager.scrollToPositionWithOffset( (int)( positionRate * (manager.getItemCount() - 1)), 0 );

        mDateScrollLayout.setY( topMargin );
    }

    private void refreshDateText() {
        int dataScrollLayoutHeight = mDateScrollLayout.getHeight();
        float yPos = positionRate * ( currentRecyclerView.getRecyclerView().getHeight() - dataScrollLayoutHeight / 2 ) + dataScrollLayoutHeight / 2;

        View v = currentRecyclerView.getRecyclerView().findChildViewUnder( 0, yPos );
        if( v != null ) {
            if( v instanceof LinearLayout && v.getTag() != null ) {
                String[] dateStr = ( (String)v.getTag() ).split( "\\." );
                mDateTextView.setText( dateStr[0] + "." + dateStr[1] );
            }
            else if( v instanceof SquareRelativeLayout ){
                GalleryCursorRecord.PhonePhotoFragmentItem item = ( (SquareRelativeLayout)v).getHolder().getPhonePhotoItem();
                mDateTextView.setText( item.getPhotoTakenYear() + "." + item.getPhotoTakenMonth() );
            }
        }
    }

    private void doAlphaAnimation( float from, final float to ) {
        if (isActivePinchAnimation()) return;

        float currentAlpha = from;

        if( animTargetValue == to ) return;

        if( scrollBarAnimator != null && scrollBarAnimator.isRunning() ) {
            currentAlpha = (float)scrollBarAnimator.getAnimatedValue();
            scrollBarAnimator.cancel();
        }

        animTargetValue = to;
        setAlpha( currentAlpha );

        scrollBarAnimator = ValueAnimator.ofFloat( currentAlpha, to );
        scrollBarAnimator.setDuration( (int)((float)ALPHA_ANIMATION_DURATION * Math.abs(to - from)) );
        scrollBarAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isSuspended) return;

                if( to == 1f )
                    DateDisplayScrollBar.this.setVisibility( View.VISIBLE );
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isSuspended) return;

                if( to == 0f ) {
                    DateDisplayScrollBar.this.setVisibility( View.GONE );
                    mDateTextLayout.setVisibility( View.GONE );
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        scrollBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isSuspended) return;
                DateDisplayScrollBar.this.setAlpha( (float)animation.getAnimatedValue() );
            }
        });
        scrollBarAnimator.start();
    }
}
