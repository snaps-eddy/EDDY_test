package com.snaps.common.utils.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Message;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;

public class SnapsViewVisibilityByScrollHandler implements ISnapsHandler {
    private static final String TAG = SnapsViewVisibilityByScrollHandler.class.getSimpleName();

    private static final long ANIMATION_DURING_TIME = 100;
    private static final long AUTO_SHOW_WAIT_DELAY_ON_NOT_EVENT = 4000; //실제 IDEL되는 시간이 1초정도 소요되서 좀 줄였다.

    enum eScrollDirection {
        IDLE,
        SCROLLED_UP,
        SCROLLED_DOWN,
        SCROLLED_BOTTOM
    }

    enum eViewState {
        HIDDEN,
        VISIBLE,
        ANIMATION
    }

    private Context context;
    private RecyclerView recyclerView;
    private View visibilityControlTargetView;
    private eScrollDirection currentScrollDirection = eScrollDirection.IDLE;
    private eViewState currentViewState = eViewState.HIDDEN;
    private SnapsHandler snapsHandler = null;
    private boolean isActive = false;

    public static SnapsViewVisibilityByScrollHandler createHandler(Context context, RecyclerView recyclerView, View targetView) {
        SnapsViewVisibilityByScrollHandler handler = new SnapsViewVisibilityByScrollHandler();
        handler.context = context;
        handler.recyclerView = recyclerView;
        handler.visibilityControlTargetView = targetView;
        return handler;
    }

    public void start() {
        if (recyclerView == null || context == null || isActive) return;
        isActive = true;
        recyclerView.addOnScrollListener(new OnVerticalScrollWithPagingSlopListener(context ) {
            @Override
            public void onScrolledUp() {
                super.onScrolledUp();
                updateViewState(eScrollDirection.SCROLLED_UP);
            }

            @Override
            public void onScrolledDown() {
                super.onScrolledDown();
                updateViewState(eScrollDirection.SCROLLED_DOWN);
            }

            @Override
            public void onScrollStop() {
                super.onScrollStop();
                updateViewState(eScrollDirection.IDLE);
            }

            @Override
            public void onScrolledToBottom() {
                super.onScrolledToBottom();
                updateViewState(eScrollDirection.SCROLLED_BOTTOM);
            }
        });

        registerAutoShowViewEvent();
    }

    public void stop() {
        isActive = false;

        removeAutoShowViewHandleMsg();
    }

    private void updateViewState(eScrollDirection scrollDirection) {
        switch (scrollDirection) {
            case SCROLLED_UP:  //UP에 보인다.
                if (currentScrollDirection == eScrollDirection.SCROLLED_UP) return;
                showView();
                break;
            case SCROLLED_BOTTOM: //가장 아래로 내리면 보여준다.
                if (currentScrollDirection == eScrollDirection.SCROLLED_BOTTOM) return;
                forceShowView();
                break;
            case SCROLLED_DOWN:  //Down에 사라진다.
                removeAutoShowViewHandleMsg();
                if (currentScrollDirection == eScrollDirection.SCROLLED_DOWN
                        || currentScrollDirection == eScrollDirection.SCROLLED_BOTTOM) return;
                hideView();
                break;
            case IDLE: //안보이는 상태에서 오랫동안 이벤트가 없다면 보이도록 한다.
                if (currentScrollDirection == eScrollDirection.IDLE) return;
                registerAutoShowViewEvent();
                break;
        }

        currentScrollDirection = scrollDirection;
    }

    private void showView() {
        if (currentViewState != eViewState.HIDDEN) return;

        try {
            startShowVerticalTranslateAnimation();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void forceShowView() {
        try {
            startShowVerticalTranslateAnimation();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void hideView() {
        if (isInvalidState() || currentViewState != eViewState.VISIBLE) return;

        try {
            startHideVerticalTranslateAnimation();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void removeAutoShowViewHandleMsg() {
        if (snapsHandler != null)
            snapsHandler.removeMessages(HANDLE_MSG_SHOW_VIEW);
    }

    private void registerAutoShowViewEvent() {
        if (isInvalidState() || currentViewState != eViewState.HIDDEN) return;

        if (snapsHandler == null) snapsHandler = new SnapsHandler(this);

        removeAutoShowViewHandleMsg();

        snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_SHOW_VIEW, AUTO_SHOW_WAIT_DELAY_ON_NOT_EVENT);
    }

    private void startShowVerticalTranslateAnimation() throws Exception {
        if (isInvalidState() || visibilityControlTargetView.isShown()) return;
        startTranslateAnimation(true);
    }

    private void startHideVerticalTranslateAnimation() throws Exception {
        if (isInvalidState() || !visibilityControlTargetView.isShown()) return;
        startTranslateAnimation(false);
    }

    private void startTranslateAnimation(final boolean show) throws Exception {
        if (isInvalidState() || currentViewState == eViewState.ANIMATION) return;

        currentViewState = eViewState.ANIMATION;

        final float FROM = show ? 1.f : 0.f;
        final float TO = show ? 0.f : 1.f;

        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(FROM, TO);
        viewParamsAnimator.setDuration(ANIMATION_DURING_TIME);
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    updateAnimationView(animation, show);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
        viewParamsAnimator.start();
    }

    private void updateAnimationView(ValueAnimator animation, boolean show) throws Exception {
        if (isInvalidState() || animation == null) return;

        float animatedValue = (float) animation.getAnimatedValue();

        final int VIEW_HEIGHT = (int) context.getResources().getDimension(R.dimen.smart_recommend_book_main_act_bottom_layout_height);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) visibilityControlTargetView.getLayoutParams();
        layoutParams.bottomMargin = (int) -(VIEW_HEIGHT * animatedValue);
        visibilityControlTargetView.setLayoutParams(layoutParams);

        if (animatedValue == 1.f) {
            if (show) {
                visibilityControlTargetView.setVisibility(View.VISIBLE);
                currentViewState = eViewState.VISIBLE;
            } else {
                visibilityControlTargetView.clearAnimation();
                visibilityControlTargetView.setVisibility(View.INVISIBLE);
                currentViewState = eViewState.HIDDEN;
            }
        }
    }

    private boolean isInvalidState() {
        return !isActive || visibilityControlTargetView == null || context == null;
    }

    private static final int HANDLE_MSG_SHOW_VIEW = 0;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_MSG_SHOW_VIEW:
                try {
                    startShowVerticalTranslateAnimation();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                break;
        }
    }
}
