package com.snaps.common.customui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;

//import com.snaps.common.R;

public class RotateImageView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = RotateImageView.class.getSimpleName();

    boolean isRotate = false;
    RotateImageViewReceiver receiver = null;
    Context mContext = null;
    View mTouchedView = null;
    boolean isThumbnail = false;
    boolean isPreview = false;

    private PointF ptActionDown = new PointF();
    private boolean isActiveAnimation = false;

    public RotateImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotateImageView(Context context) {
        super(context);
        init(context);
    }

    void init(Context context) {

        mContext = context;

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        ptActionDown.set(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        if (checkClickAction(event)) {
                            if (isActiveAnimation) {
                                return true;
                            }
                            mTouchedView = v;
                            processRotation(true);
                        }
                        break;
                }
                return true;
            }
        });

        IntentFilter filter = new IntentFilter(Const_VALUE.RESET_LAYOUT_ACTION);
        receiver = new RotateImageViewReceiver();
        getContext().registerReceiver(receiver, filter);
    }

    private boolean checkClickAction(MotionEvent event) {
        if (event == null || ptActionDown == null) {
            return false;
        }
        float moveX = Math.abs(ptActionDown.x - event.getX());
        float moveY = Math.abs(ptActionDown.y - event.getY());
        return moveX < 20 && moveY < 20;
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public void setIsThumbnail(boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public void setIsPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }

    void sendClickEvent() {
        if (mTouchedView == null || isThumbnail() || isPreview()) {
            return;
        }
        // action 보내기..
        Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
        intent.putExtra("control_id", mTouchedView.getId());
        intent.putExtra("isEdit", false);

        SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(mTouchedView);
        if (snapsControl != null && snapsControl instanceof SnapsLayoutControl) {
            SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;
            if (control.isSnsBookCover/*|| control.isFacebookPhotobookCover*/) {
                intent.putExtra("isEditableKaKaoImg", true);
                intent.putExtra("isEditableImg", true);
            }
        }

        mContext.sendBroadcast(intent);
    }

    void processRotation(boolean isRotation) {
        if (isRotation) {
            if (isRotate) {
                processRotation(false);
                return;
            }
            Animation ro = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_45);
            ro.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    isActiveAnimation = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    sendClickEvent();
                }
            });

            this.startAnimation(ro);
            isRotate = true;
        } else {
            if (!isRotate) {
                return;
            }
            Animation ro = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_0);
            ro.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isActiveAnimation = false;
                }
            });
            this.startAnimation(ro);

            isRotate = false;
        }
    }

    public class RotateImageViewReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            processRotation(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            getContext().unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Dlog.e(TAG, e);
        }
        super.onDetachedFromWindow();
    }
}
