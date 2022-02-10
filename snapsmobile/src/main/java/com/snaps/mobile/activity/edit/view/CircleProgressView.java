package com.snaps.mobile.activity.edit.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.component.SnapsCircularProgressBar;
import com.snaps.mobile.component.SnapsCircularProgressBar.ProgressAnimationListener;

/**
 * @Marko
 * 네이티브 프로그레스바가 없기 때문에 아마 참조 안할듯.
 */
public class CircleProgressView extends Dialog implements DialogInterface.OnKeyListener, Runnable, android.view.View.OnClickListener {
    private static final String TAG = CircleProgressView.class.getSimpleName();
    TextView title_text;
    TextView desc_text;

    private SnapsCircularProgressBar progress;

    int bar_value = 0;
    String bar_count = "";

    static CircleProgressView _instance;
    Thread progress_thread;

    public static final String VIEW_PROGRESS = "view_progress";
    public static final String VIEW_LOADING = "view_loading";

    final int duration = 100;
    private boolean m_isActiveAnim = false;

    /**
     * Get Instance
     *
     * @param context
     * @return
     */
    public static CircleProgressView getInstance(Activity context) {
        if (_instance == null) {
            _instance = new CircleProgressView(context);
        }
        return _instance;
    }

    public CircleProgressView(Activity context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        setOwnerActivity(context);

        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(false);

        /** Design the dialog in main.xml file */
        setContentView(R.layout.snaps_circular_progress);

        title_text = this.findViewById(R.id.snaps_circluar_progress_title_tv);
        desc_text = this.findViewById(R.id.snaps_circluar_progress_desc_tv);
        progress = this.findViewById(R.id.snaps_circluar_progress_bar);
    }

    /**
     * Instance 제거
     */
    public static void destroy() {
        _instance = null;
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Dlog.e(TAG, e);
            }

            try {
                desc_text.post(progress_run);
            } catch (OutOfMemoryError e) {
                Dlog.e(TAG, e);
            }
        }
    }

    Runnable progress_run = new Runnable() {
        public void run() {
            // 현재 표시할 값이 최대값을 넘으면 최대값으로 대체한다.
            if (bar_value > 100) bar_value = 100;

            int curProgress = progress.getProgress();
            if (curProgress < bar_value && !m_isActiveAnim) {
                progress.animateProgressTo(curProgress, bar_value, new ProgressAnimationListener() {

                    @Override
                    public void onAnimationStart() {
                        m_isActiveAnim = true;
                    }

                    @Override
                    public void onAnimationProgress(int progress) {
                    }

                    @Override
                    public void onAnimationFinish() {
                        progress.setTitle(String.valueOf(bar_value));
                        m_isActiveAnim = false;
                    }
                });
            }
        }
    };


    /**
     * Object Animation 설정.
     *
     * @param obj
     * @param propertyName
     * @param value
     */
    void objectAnimation(Object obj, String propertyName, int value) {
        ObjectAnimator animation = ObjectAnimator.ofInt(obj, propertyName, value);
        animation.setDuration(duration);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    /**
     * 팝업 로드
     *
     * @param mode
     */
    public void load(String mode) {
        if (mode.equalsIgnoreCase(VIEW_PROGRESS)) {

            if (progress_thread == null) {
                progress_thread = new Thread(this);
                progress_thread.start();
            }

        } else if (mode.equalsIgnoreCase(VIEW_LOADING)) {
        }

        if (getOwnerActivity() != null && !getOwnerActivity().isFinishing() && !this.isShowing()) {
            this.show();
        }


        bar_value = 0;
    }

    /**
     * Progress Bar value 설정.
     *
     * @param value
     */
    public void setValue(final int value) {
        if (bar_value < value)
            bar_value = value;
    }

    public int getValue() {
        return bar_value;
    }

    public void closeProgress(int value) {

        progress.setVisibility(View.GONE);
    }

    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    /**
     * Title Message 설정.
     *
     * @param message
     */
    public void setMessage(String message) {
        title_text.setText(message);
    }

    /**
     * Page Count 설정.
     */
    public void setPageCount() {
        progress.setVisibility(View.VISIBLE);
    }

    public void setPageCount(String count) {
        bar_count = count;
    }

    /**
     * 팝업 Unload
     */
    public void Unload() {
        try {
            dismiss();
            progress_thread = null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onBackPressed() {
        // Back Key 방지.
    }

    @Override
    public void onClick(View arg0) {
        try {
            dismiss();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
