package com.snaps.mobile.activity.diary.customview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 16. 4. 1..
 */
public class SnapsDiaryRecyclerView extends SuperRecyclerView {
    private static final String TAG = SnapsDiaryRecyclerView.class.getSimpleName();

    private Context context;

    public SnapsDiaryRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public SnapsDiaryRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnapsDiaryRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        RecyclerView recylerView = getRecyclerView();
        recylerView.setVerticalScrollBarEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch( IllegalArgumentException e ) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch( IllegalArgumentException e ) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    public void scrollByY(int y) {
        RecyclerView recylerView = getRecyclerView();
        recylerView.scrollBy(0, y);
        if (!isShown()) {
            recylerView.post(new Runnable() {
                @Override
                public void run() {
                    SnapsDiaryRecyclerView.this.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        RecyclerView recylerView = getRecyclerView();
        recylerView.setItemAnimator(animator);
    }

    public int getComputeVerticalScrollOffset() {
        RecyclerView recylerView = getRecyclerView();
        return recylerView.computeVerticalScrollOffset();
    }
}
