package com.snaps.mobile.service.ai;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1. AI 사진 업로드 과정에서 필요한 정보를 수집한다.
 *
 * 추가로 최초 사진 업로드가 되는지 감시하는 역할도 하는데.. (on/off 가능하다.)
 */
class Monitoring {
    private static final String TAG = Monitoring.class.getSimpleName();
    private static final String SP_NAME = "aiPhotoSyncErrorReport";
    private static final String SP_KEY_TOTAL_RUNNING_TIME = "TOTAL_RUNNING_TIME";
    private static final String SP_KEY_RUNNING_COUNT = "RUNNING_COUNT";
    private static final String SP_KEY_LAST_CHECK_SYNC_INIT_SECOND = "LAST_CHECK_SYNC_INIT_SECOND";
    private static final String SP_KEY_IS_SENT_NOT_AVAILABLE_PHOTO = "IS_SENT_NOT_AVAILABLE_PHOTO";
    private static final long INTERVAL_CHECK_SYNC_INIT_COMPLETE_TIME_SECOND = 60 * 10;
    private static final long MAX_CHECK_SYNC_INIT_COMPLETE_TIME_SECOND = INTERVAL_CHECK_SYNC_INIT_COMPLETE_TIME_SECOND * 3;
    private static final int MAX_ERROR_MSG_SIZE = 16;
    private List<String> mErrorMsgList;
    private Map<String, String> mInfoMap;
    private long mStartTime;
    private long mFinishTime;
    private String mUserNo;
    private Context mContext;
    private volatile SyncInitCompleteCheckThread mSyncInitCompleteCheckThread;
    private EventListener mEventListener;

    public interface EventListener {
        void onUncatchedException(String msg, Throwable throwable);
    }

    private Monitoring() {
        mStartTime = 0;
        mFinishTime = 0;
        mContext = null;
        mErrorMsgList = new ArrayList<String>();
        mInfoMap = new HashMap<String, String>();
        mEventListener = null;
        mSyncInitCompleteCheckThread = null;
    }

    public static Monitoring getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final Monitoring INSTANCE = new Monitoring();
    }

    public void init(Context context, String userNo) {
        mContext = context;
        mUserNo = userNo;
        mStartTime = 0;
        mFinishTime = 0;
        mErrorMsgList.clear();
        mInfoMap.clear();
        mEventListener = null;
        mSyncInitCompleteCheckThread = null;
    }

    public void setListener(EventListener listener) {
        mEventListener = listener;
    }

    /**
     * 최초 사진 동기화 완료 시간 모니터링 시작
     */
    public void startSyncInitCompleateCheck() {
        if (SyncPhotoServiceManager.IS_CHECK_SYNC_INIT_COMPLETE == false) return;
        if (mSyncInitCompleteCheckThread != null) return;

        mSyncInitCompleteCheckThread = new SyncInitCompleteCheckThread();
        mSyncInitCompleteCheckThread.start();
    }

    /**
     * 최초 사진 동기화 완료 시간 모니터링 종료
     */
    public void stopSyncInitCompleateCheck() {
        if (SyncPhotoServiceManager.IS_CHECK_SYNC_INIT_COMPLETE == false) return;
        if (mSyncInitCompleteCheckThread == null) return;

        mSyncInitCompleteCheckThread.forceStop();
        mSyncInitCompleteCheckThread.interrupt();
        mSyncInitCompleteCheckThread = null;
    }

    public void setInfo(String key, long value) {
        mInfoMap.put(key, String.valueOf(value));
    }

    public void setInfo(String key, boolean value) {
        mInfoMap.put(key, String.valueOf(value));
    }

    public void setInfo(String key, String value) {
        mInfoMap.put(key, value);
    }

    public void addInfo(String key, boolean value) {
        addInfo(key, String.valueOf(value));
    }

    public void addInfo(String key, long value) {
        addInfo(key, String.valueOf(value));
    }

    public void addInfo(String key, String value) {
        String fixedKey = key;
        if (mInfoMap.containsKey(key)) {
            fixedKey = fixedKey + "#";
        }
        mInfoMap.put(fixedKey, value);
    }

    public String getInfoListText() {
        StringBuilder sb = new StringBuilder();

        List<String> infoMapKeyList = new ArrayList<String>(mInfoMap.keySet());
        Collections.sort(infoMapKeyList);
        for(String key : infoMapKeyList) {
            sb.append(key).append(" : ").append(mInfoMap.get(key)).append("\n");
        }

        return sb.toString();
    }

    public void addError(String errorMsg) {
        if (mErrorMsgList.size() > MAX_ERROR_MSG_SIZE) {
            mErrorMsgList.remove(0);
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        String nowTime = sdf.format(date);
        mErrorMsgList.add(nowTime + " " + errorMsg + "\n");
    }

    public String getErrorTexts() {
        StringBuilder sb = new StringBuilder();

        List<String> errorMsgList = new ArrayList<String>(mErrorMsgList);
        for(String errorMsg : errorMsgList) {
            sb.append(errorMsg).append("\n");
        }

        return sb.toString();
    }

    public void setStartTime() {
        mStartTime = System.currentTimeMillis();

        Date date = new Date(mStartTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        setInfo("Start time", sdf.format(date));

        long runningCount = getSP(getSPKey(SP_KEY_RUNNING_COUNT), 0) + 1;
        setInfo("Run count", runningCount);
        putSP(getSPKey(SP_KEY_RUNNING_COUNT), runningCount);
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setFinishTime() {
        mFinishTime = System.currentTimeMillis();
        addTotalRunningTime(mFinishTime - mStartTime);
    }

    public long getCurrentRunningTime() {
        if (mStartTime == 0) return 0;
        return System.currentTimeMillis() - mStartTime;
    }

    private String getSPKey(String key) {
        String key2 = key + mUserNo;
        return key2;
    }

    public long getTotalRunningTime() {
        long totalRunningTime = getSP(getSPKey(SP_KEY_TOTAL_RUNNING_TIME), 0);
        totalRunningTime += getCurrentRunningTime();
        return totalRunningTime;
    }

    private void addTotalRunningTime(long time) {
        long totalRunningTime = getSP(getSPKey(SP_KEY_TOTAL_RUNNING_TIME), 0);
        totalRunningTime += time;
        putSP(getSPKey(SP_KEY_TOTAL_RUNNING_TIME), totalRunningTime);
    }

    public void setSentNotAvailablePhoto(boolean isSend) {
        putSP(getSPKey(SP_KEY_IS_SENT_NOT_AVAILABLE_PHOTO), isSend);
    }

    public boolean isSentNotAvailablePhoto() {
        return getSP(getSPKey(SP_KEY_IS_SENT_NOT_AVAILABLE_PHOTO), false);
    }

    private void putSP(String key, long value) {
        if (mContext == null) return;
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(key, value).commit();
    }

    private long getSP(String key, long defaultValue) {
        if (mContext == null) return 0;
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    private void putSP(String key, boolean value) {
        if (mContext == null) return;
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    private boolean getSP(String key, boolean defaultValue) {
        if (mContext == null) return defaultValue;
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }


    class SyncInitCompleteCheckThread extends Thread {
        private final String TAG = Monitoring.TAG + "." + SyncInitCompleteCheckThread.class.getSimpleName();
        private volatile boolean mIsRunning;
        private long mLastCheckSyncInitSecond;

        public SyncInitCompleteCheckThread() {
            Loggg.d(TAG, "create instance()");
            mIsRunning = true;
            mLastCheckSyncInitSecond = getSP(getSPKey(SP_KEY_LAST_CHECK_SYNC_INIT_SECOND),
                    INTERVAL_CHECK_SYNC_INIT_COMPLETE_TIME_SECOND);
        }

        public void forceStop() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            Loggg.d(TAG, "start run()");
            while(isInterrupted() == false && mIsRunning) {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    return;
                }

                if (mContext == null) continue;
                if (mEventListener == null) continue;

                long totalRunningTimeMiliSecond = getTotalRunningTime();
                int totalRunningTimeSecond = (int)(totalRunningTimeMiliSecond / (long)1000);

                if (totalRunningTimeSecond > mLastCheckSyncInitSecond) {
                    //한번 전송이 되면 지정한 시간 이후에 다시 전송한다.
                    mLastCheckSyncInitSecond += INTERVAL_CHECK_SYNC_INIT_COMPLETE_TIME_SECOND;
                    putSP(getSPKey(SP_KEY_LAST_CHECK_SYNC_INIT_SECOND), mLastCheckSyncInitSecond);

                    if (mLastCheckSyncInitSecond > MAX_CHECK_SYNC_INIT_COMPLETE_TIME_SECOND) {
                        //더 이상 보내지 않는다.
                        putSP(getSPKey(SP_KEY_LAST_CHECK_SYNC_INIT_SECOND), Long.MAX_VALUE);
                    }

                    String report = ErrorReport.getInstance().create(mContext, mUserNo);
                    mEventListener.onUncatchedException(
                            report, new RuntimeException("Initial photo sync creation timeout"));
                }
            }
            Loggg.d(TAG, "stop run()");
        }
    }
}
