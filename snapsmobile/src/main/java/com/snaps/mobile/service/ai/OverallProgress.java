package com.snaps.mobile.service.ai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class OverallProgress {
    private static final String TAG = OverallProgress.class.getSimpleName();
    private volatile Map<Part, Integer> mPartTotalMap;
    private volatile Map<Part, Integer> mPartCurrentMap;
    private volatile SyncPhotoServiceManager.ProgressListener mListener;
    private volatile boolean mIsActive;
    private volatile int mPreTotalProcess;
    public enum Part {
        CREATE_EXIF_INFO(50),
        UPLOAD_EXIF_INFO(10),
        UPLOAD_IMG_THUMB(40);

        public final int mRatio;
        Part(int ratio) {
            mRatio = ratio;
        }
    }

    private OverallProgress() {
        mIsActive = false;
        mListener = null;

        //썸네일 업로드는 멀티 스레드 환경이므로
        mPartTotalMap = new ConcurrentHashMap<Part, Integer>();
        for (Part part : Part.values()) {
            mPartTotalMap.put(part, 0);
        }

        //썸네일 업로드는 멀티 스레드 환경이므로
        mPartCurrentMap = new ConcurrentHashMap<Part, Integer>();
        for (Part part : Part.values()) {
            mPartCurrentMap.put(part, 0);
        }

        mPreTotalProcess = -1;
    }

    public static OverallProgress getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final OverallProgress INSTANCE = new OverallProgress();
    }

    public void setActive(boolean isActive) {
        mIsActive = isActive;
    }

    public void setListener(SyncPhotoServiceManager.ProgressListener listener) {
        mListener = listener;
    }

    public void reset() {
        for (Part part : Part.values()) {
            mPartTotalMap.put(part, 0);
        }

        for (Part part : Part.values()) {
            mPartCurrentMap.put(part, 0);
        }

        mPreTotalProcess = -1;
    }

    public void setPercent(Part part, int percent) {
        if (mIsActive == false) {
            return;
        }

        mPartTotalMap.put(part, 100);
        mPartCurrentMap.put(part, percent);

        calculatePercent();
    }

    public void setTotal(Part part, int total) {
        if (mIsActive == false) {
            return;
        }

        mPartTotalMap.put(part, total);
    }

    public void setValue(Part part, int value) {
        if (mIsActive == false) {
            return;
        }

        mPartCurrentMap.put(part, value);

        calculatePercent();
    }

    public void setIncrement(Part part, int amount) {
        if (mIsActive == false) {
            return;
        }

        int current = mPartCurrentMap.get(part);
        current += amount;
        mPartCurrentMap.put(part, current);

        calculatePercent();
    }

    private void calculatePercent() {
        int totalPercent = 0;
        for (Map.Entry<Part, Integer> entry : mPartTotalMap.entrySet()) {
            Part part = entry.getKey();
            int total = entry.getValue();
            int percent = 0;
            if (total > 0) {
                int current = mPartCurrentMap.get(part);
                percent = (current * 100) / total;
            }

            int ratioPercent = (percent == 0 ? 0 : (percent * part.mRatio) / 100);

            totalPercent += ratioPercent;
        }

        if (mPreTotalProcess == totalPercent) {
            return;
        }

        mPreTotalProcess = totalPercent;

        mListener.onChangeProgress(totalPercent);
    }
}
