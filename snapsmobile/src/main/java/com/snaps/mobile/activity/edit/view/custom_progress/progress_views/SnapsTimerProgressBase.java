package com.snaps.mobile.activity.edit.view.custom_progress.progress_views;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate.SnapsTimerProgressBaseCalculator;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public abstract class SnapsTimerProgressBase extends Dialog implements Runnable, SnapsProgressViewAPI {
    private static final String TAG = SnapsTimerProgressBase.class.getSimpleName();
    enum eProgressBarShape {
        CIRCLE,
        BAR
    }

    private static final int PROGRESS_TICK_TIME = 1000;
    private static final int PROGRESS_DURING_TIME = 1000;

    private static final int PROGRESS_TEXT_REFRESH_TICK = 5; //PROGRESS_TICK_TIME가 해당 횟수만큼 진행될때마다 텍스트를 갱신한다.

    private double progressCount = 0;

    private Activity activity = null;

    private ProgressBar barShapeProgressBar;
    private ProgressBar circleShapeProgressBar;

    private ObjectAnimator animation;

    private SnapsTimerProgressViewFactory.eTimerProgressType progressType;

    private TextView title_text;
    private TextView tvUploadProgressText;

    private Thread progressThread;

    private SnapsTimerProgressBaseCalculator progressCalculator = null;

    private int progressValue;
    private int prevProgressValue;

    protected abstract void updateProgressBar() throws Exception;

    protected abstract void updateProgressText() throws Exception;

    protected abstract void initHook();

    protected abstract void showProgressHook();

    protected SnapsTimerProgressBase(Activity activity, SnapsTimerProgressViewFactory.eTimerProgressType progressType) {
        super(activity, R.style.TransparentProgressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_timer_progress_custom_bar);

        init(activity, progressType);
    }

    private void init(Activity activity, SnapsTimerProgressViewFactory.eTimerProgressType progressType) {
        this.setCancelable( false );
        this.setActivity(activity);
        this.setProgressType(progressType);

        title_text = (TextView) this.findViewById( R.id.progress_title );
        barShapeProgressBar = ( ProgressBar ) this.findViewById( R.id.progress );
        circleShapeProgressBar = ( ProgressBar ) this.findViewById( R.id.loading );
        tvUploadProgressText = ( TextView ) this.findViewById( R.id.page_text );

        initHook();
    }

    @Override
    public void releaseInstance() {
        if (progressCalculator != null)
            progressCalculator.releaseData();

        progressCount = 0;

        hideProgress();
    }

    @Override
    public void run() {
        if (getProgressType() == SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING) return;
        try {
            do {
                try {
                    Thread.sleep(PROGRESS_TICK_TIME);
                } catch ( InterruptedException e ) {
                    Dlog.e(TAG, e);
                }

                try {
                    if(getActivity() != null && !getActivity().isFinishing()) {
                        getActivity().runOnUiThread(progressBarRunnable);

                        if (++progressCount % PROGRESS_TEXT_REFRESH_TICK == 0)
                            getActivity().runOnUiThread(progressTextRunnable);
                    }
                } catch (OutOfMemoryError | Exception e) {
                    Dlog.e(TAG, e);
                }
            } while (!isSuspended());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void setMessage(String message) {
        if (title_text != null)
            title_text.setText(message);
    }

    @Override
    public void showProgress() {
        try {
            initProgressValue();

            showProgressHook();

            if ( ! this.isShowing() ) {
                this.show();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void initProgressValue() {
        cancelAnimation();

        initProgressBarValue();

        hideTimerText();
    }

    private void hideTimerText() {
        if (tvUploadProgressText != null)
            tvUploadProgressText.setVisibility(View.GONE);
    }

    private void cancelAnimation() {
        if (animation != null && animation.isRunning())
            animation.cancel();
    }

    private void initProgressBarValue() {
        if (barShapeProgressBar != null)
            barShapeProgressBar.setProgress(0);

        prevProgressValue = 0;
        progressValue = 0;
    }

    @Override
    public void hideProgress() {
        try {
            if (isShowing())
                dismiss();

            if (progressThread != null && progressThread.getState() == Thread.State.RUNNABLE)
                progressThread.interrupt();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private boolean isSuspended() {
        return Thread.interrupted() || activity == null || activity.isFinishing() || !isShowing();
    }

    @Override
    public SnapsTimerProgressBaseCalculator getProgressCalculator() {
        return progressCalculator;
    }

    public void setProgressCalculator(SnapsTimerProgressBaseCalculator progressCalculator) {
        this.progressCalculator = progressCalculator;
    }

    protected Activity getActivity() {
        return activity;
    }

    public ProgressBar getBarShapeProgressBar() {
        return barShapeProgressBar;
    }

    public void setBarShapeProgressBar(ProgressBar barShapeProgressBar) {
        this.barShapeProgressBar = barShapeProgressBar;
    }

    public ProgressBar getCircleShapeProgressBar() {
        return circleShapeProgressBar;
    }

    public void setCircleShapeProgressBar(ProgressBar circleShapeProgressBar) {
        this.circleShapeProgressBar = circleShapeProgressBar;
    }

    public TextView getTitle_text() {
        return title_text;
    }

    public void setTitle_text(TextView title_text) {
        this.title_text = title_text;
    }

    public TextView getTvUploadProgressText() {
        return tvUploadProgressText;
    }

    public void setTvUploadProgressText(TextView tvUploadProgressText) {
        this.tvUploadProgressText = tvUploadProgressText;
    }

    public SnapsTimerProgressViewFactory.eTimerProgressType getProgressType() {
        return progressType;
    }

    public void setProgressType(SnapsTimerProgressViewFactory.eTimerProgressType progressType) {
        this.progressType = progressType;
    }

    private int getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }

    private Thread getProgressThread() {
        return progressThread;
    }

    public void setProgressThread(Thread progressThread) {
        this.progressThread = progressThread;
    }

    private void objectAnimation (Object obj , String propertyName , int value ) {
        animation = ObjectAnimator.ofInt( obj , propertyName , value );
        animation.setDuration( PROGRESS_DURING_TIME );
        animation.setInterpolator( new DecelerateInterpolator() );
        animation.start();
    }

    protected void setProgressBarShape(eProgressBarShape shape) {
        switch (shape) {
            case BAR:
                setProgressShapeBar();
                startProgressThread();
                break;
            case CIRCLE:
                setProgressShapeCircle();
                break;
        }
    }

    private void setProgressShapeCircle() {
        getBarShapeProgressBar().setVisibility( View.GONE );
        getCircleShapeProgressBar().setVisibility( View.VISIBLE );
    }

    private void setProgressShapeBar() {
        getCircleShapeProgressBar().setVisibility( View.GONE );
        getBarShapeProgressBar().setVisibility( View.VISIBLE );
    }

    private void startProgressThread() {
        if (getProgressThread() == null) {
            setProgressThread(new Thread( this ));
            getProgressThread().start();
        }
    }

    protected String convertSecToMin(long time) {
        int sec = (int) (time / 1000);
        int min = sec/60;
        int hour = min/60;
        StringBuilder sbTime = new StringBuilder();
        if(hour > 0) {
            sbTime.append("0").append(hour).append(":");
            min = min % 60;
        }

        if(min > 9) {
            sbTime.append(String.valueOf(min)).append(":");
        } else if(min > 0) {
            sbTime.append("0").append(String.valueOf(min)).append(":");
        } else {
            sbTime.append("00").append(":");
        }

        int modSec = sec % 60;
        if(modSec < 10)
            sbTime.append("0");
        sbTime.append(String.valueOf(modSec));
        return sbTime.toString();
    }

    protected void calculateRemainTimeValue() {
        long estimatedTime = getProgressCalculator().getAllTaskRemainEstimatedTime();

        long maxTime = 7199000;

        estimatedTime = Math.min(maxTime, estimatedTime);
        estimatedTime = Math.max(1, estimatedTime);

        getProgressCalculator().setUploadRemainExpectSec(estimatedTime);
    }

    private boolean checkProgressTimeVisibility() {
        if (getTvUploadProgressText().isShown())
                getTvUploadProgressText().setVisibility(View.GONE);
        return false;
//        if (isAllowShowRemainTime()) {
//            if (!getTvUploadProgressText().isShown())
//                getTvUploadProgressText().setVisibility(View.VISIBLE);
//            return true;
//        } else {
//            if (getTvUploadProgressText().isShown())
//                getTvUploadProgressText().setVisibility(View.GONE);
//            return false;
//        }
    }

    protected void updateRemainTimeText() throws Exception {
        calculateRemainTimeValue();

        if (checkProgressTimeVisibility()) {
            String remainTimeText = convertSecToMin(getProgressCalculator().getUploadRemainExpectTime()) + (" " + ContextUtil.getString(R.string.remain_text, "남음"));
            getTvUploadProgressText().setText(remainTimeText);
        }
    }

    protected void updateProgressView() throws Exception {
        setProgressValue(getProgressCalculator().getCurrentProgressValue());

        if ( getProgressValue() == 0 ) {
            barShapeProgressBar.setProgress(getProgressValue());
        } else {
            if (getProgressValue() > prevProgressValue) {
                prevProgressValue = getProgressValue();
                objectAnimation(barShapeProgressBar, "progress" , getProgressValue() );
            }
        }
    }

    private boolean isAllowShowRemainTime() {
        return getProgressCalculator() != null && getProgressCalculator().getUploadRemainExpectTime() > 4000; //너무 작은 수치는 보여주나 마나..
    }

    private Runnable progressBarRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                updateProgressBar();
            } catch (Exception e) { Dlog.e(TAG, e); }
        }
    };

    private Runnable progressTextRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                updateProgressText();
            } catch (Exception e) { Dlog.e(TAG, e); }
        }
    };
}
