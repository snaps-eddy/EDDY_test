package com.snaps.mobile.service.ai;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 디버그 용도 성능 및 기타 등등 측정
 */
class PerformanceMeasurementTool {
    private static final String TAG = PerformanceMeasurementTool.class.getSimpleName();
    private static DecimalFormat sDecimalFormat = new DecimalFormat(".###");
    private static Map<String, Long> mTimeTagMap = new HashMap<String, Long>();

    /**
     * 시간을 측정한다.
     * @param tag
     */
    public static void measure(String tag) {
        if (Loggg.IS_DEBUG == false) return;

        if (mTimeTagMap.containsKey(tag)) {
            long time2 = System.currentTimeMillis();
            long time1 = mTimeTagMap.get(tag);
            float result = (float)(time2 - time1) / 1000f;
            Loggg.i(TAG, tag + " Time : " + result + " second");
            mTimeTagMap.remove(tag);
        }
        else {
            mTimeTagMap.put(tag, System.currentTimeMillis());
        }
    }

    /**
     * 시간 측정 정보를 초기화 한다.
     * @param tag
     */
    public static void reset(String tag) {
        if (mTimeTagMap.containsKey(tag)) {
            mTimeTagMap.remove(tag);
        }
    }


    /**
     * 전체 시간 측정 정보를 초기화 한다.
     */
    public static void resetAll() {
        mTimeTagMap.clear();
    }

    /**
     * byte를 사람이 읽기 쉬운 단위로 변환
     * @param bytes
     * @return
     */
    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * 시간을 사람이 읽기 쉬운 단위
     * @param milsec
     * @return
     */
    public static String getFormatedTimeString(long milsec) {
        if (milsec < 1000) {
            return "0" + sDecimalFormat.format((float)milsec / (float)1000);
        }

        long timeSecondCap = milsec / 1000;
        if (timeSecondCap < 60) {
            return sDecimalFormat.format((float)milsec / (float)1000);
        }

        long minute = timeSecondCap / 60;
        long second = timeSecondCap % 60;
        if (minute >= 60) {
            long hour = minute / 60;
            minute = minute % 60;
            return "" + hour + "h " + minute + "m " + second + "s";
        }

        return "" + minute + "m " + second + "s";
    }

    /**
     * 시간 차이를 사람이 읽기 쉬운 단위
     * @param milsec1
     * @param milsec2
     * @return
     */
    public static String getFormatedTimeString(long milsec1, long milsec2) {
        long timeMilSecondCap = milsec2 - milsec1;
        return getFormatedTimeString(timeMilSecondCap);
    }


}
