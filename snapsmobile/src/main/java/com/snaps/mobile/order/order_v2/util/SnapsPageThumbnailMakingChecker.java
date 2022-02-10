package com.snaps.mobile.order.order_v2.util;

import android.os.Message;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2017. 4. 20..
 */

public class SnapsPageThumbnailMakingChecker implements ISnapsHandler {
    private static final String TAG = SnapsPageThumbnailMakingChecker.class.getSimpleName();
    public interface SnapsPageThumbnailMakeErrListener {
        void onOverWaitTime();
    }

    private static final int MAX_WAIT_SECONDS = 30; //SEC  메인 페이지 하나 만드는 데 최대 해당 초만큼 기다려 준다...넘어가면 실패다.
    private SnapsHandler snapsHandler = null;

    private SnapsPageThumbnailMakeErrListener snapsPageThumbnailMakeErrListener = null;

    private boolean isCompletedMakingThumbnail = false;
    private boolean isOverWaitTime = false;
    private boolean isSuspended = false;
    private int tryCount = 0;

    public static SnapsPageThumbnailMakingChecker newInstance() {
        return new SnapsPageThumbnailMakingChecker();
    }

    private SnapsPageThumbnailMakingChecker() {
        this.snapsHandler = new SnapsHandler(this);
    }

    public void startMainPageThumbnailMakingCheck(SnapsPageThumbnailMakeErrListener snapsPageThumbnailMakeErrListener) {
        setSnapsPageThumbnailMakeErrListener(snapsPageThumbnailMakeErrListener);
        setCompletedMakingThumbnail(false);
        setOverWaitTime(false);
        setTryCount(0);
        checkMakeSnapsPageThumbnail();
    }

    public void finalizeInstance() {
        setSuspended(true);
        finishCheck();
    }

    private void finishCheck() {
        snapsPageThumbnailMakeErrListener = null;
    }

    private void checkMakeSnapsPageThumbnail() {
        if (isCompletedMakingThumbnail()) {
            Dlog.d("checkMakeSnapsPageThumbnail() finished page making check.");
            finishCheck();
            return;
        }

        addTryCount();

        if (getTryCount() > MAX_WAIT_SECONDS) {
            Dlog.d("checkMakeSnapsPageThumbnail() over wait time for page_thumbnail to be made~!!");
            setOverWaitTime(true);
            if (getSnapsPageThumbnailMakeErrListener() != null)
                getSnapsPageThumbnailMakeErrListener().onOverWaitTime();
        } else {
            Dlog.d("checkMakeSnapsPageThumbnail() waiting for page_thumbnail to be made. (" + getTryCount() + "sec)");
            if (snapsHandler != null) snapsHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (isSuspended()) return;
        checkMakeSnapsPageThumbnail();
    }

    private boolean isCompletedMakingThumbnail() {
        return isCompletedMakingThumbnail;
    }

    public void setCompletedMakingThumbnail(boolean completedMakingThumbnail) {
        isCompletedMakingThumbnail = completedMakingThumbnail;
    }

    private int getTryCount() {
        return tryCount;
    }

    private void addTryCount() {
        this.tryCount++;
    }

    private void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    public SnapsPageThumbnailMakeErrListener getSnapsPageThumbnailMakeErrListener() {
        return snapsPageThumbnailMakeErrListener;
    }

    private void setSnapsPageThumbnailMakeErrListener(SnapsPageThumbnailMakeErrListener snapsPageThumbnailMakeErrListener) {
        this.snapsPageThumbnailMakeErrListener = snapsPageThumbnailMakeErrListener;
    }

    public boolean isOverWaitTime() {
        return isOverWaitTime;
    }

    private void setOverWaitTime(boolean overWaitTime) {
        isOverWaitTime = overWaitTime;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }
}
