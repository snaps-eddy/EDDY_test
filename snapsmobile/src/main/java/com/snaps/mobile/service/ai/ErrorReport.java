package com.snaps.mobile.service.ai;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * Crashlytics 보고서
 */
class ErrorReport {
    private static final String TAG = DeviceManager.class.getSimpleName();

    private ErrorReport() {
    }

    public static ErrorReport getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final ErrorReport INSTANCE = new ErrorReport();
    }

    /**
     * milisecond 시간을 적절히 일,시,분,초 문자열로 변환한다.
     * @param diff
     * @return
     */
    private String covertLongTimeToDHMS(long diff) {
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDay = diff / (60 * 60 * 1000) / 24;

        String DHMS = String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);
        if (diffDay > 0) {
            DHMS = String.valueOf(diffDay) + "D " + DHMS;
        }

        return DHMS;
    }

    /**
     * 리포트를 생성한다.
     * @param context
     * @param userNo
     * @return
     */
    public String create(Context context, String userNo) {
        StringBuilder sb = new StringBuilder();

        Monitoring monitoring = Monitoring.getInstance();

        try {
            long startTime = monitoring.getStartTime();
            if (startTime > 0) {
                long currentRunTime = monitoring.getCurrentRunningTime();
                Monitoring.getInstance().setInfo("Run time - current", covertLongTimeToDHMS(currentRunTime));

                long totalRunTime = monitoring.getTotalRunningTime();
                Monitoring.getInstance().setInfo("Run time - total", covertLongTimeToDHMS(totalRunTime));
            }

            AppConfigClone appConfigClone = AppConfigClone.getInstance();
            monitoring.setInfo("Error Time - UserNo", userNo);
            monitoring.setInfo("Error Time - IsAllowUploadMobileNetwork", appConfigClone.isAllowUploadMobileNetwork());

            sb.append("== Environment ==").append("\n");
            sb.append(getEnvInfo(context));
            sb.append("\n");

            sb.append("== service values ==").append("\n");
            sb.append(monitoring.getInfoListText());
            sb.append("\n");

            String errorTexts = monitoring.getErrorTexts();
            if (errorTexts.length() > 0) {
                sb.append("== ERROR MSG ==").append("\n");
                sb.append(errorTexts);
                sb.append("\n");
            }
        }catch (Throwable t) {
        }

        return sb.toString();
    }

    /**
     * 단말기 실행 환경 정보를 구한다.
     * @param context
     * @return
     */
    private String getEnvInfo(Context context) {
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("Device Model : ").append(Build.MODEL).append("\n");
            sb.append("Android OS : ").append(Build.VERSION.RELEASE).append("\n");

            String inStorageTotal = humanReadableByteCount(getStorageTotal(true));
            String inStorageAvailable = humanReadableByteCount(getStorageAvailable(true));
            sb.append("Internal Storage (free): ").append(inStorageAvailable).append(" / ").append(inStorageTotal).append("\n");
            if (isExternalMemoryAvailable()) {
                String exStorageTotal = humanReadableByteCount(getStorageTotal(false));
                String exStorageAvailable = humanReadableByteCount(getStorageAvailable(false));
                sb.append("External Storage (free): ").append(exStorageAvailable).append(" / ").append(exStorageTotal).append("\n");
            }

            DeviceManager deviceManager = new DeviceManager(context);
            sb.append("WiFi : ").append(deviceManager.isWiFiConnected()).append("\n");
            sb.append("Mobile Network : ").append(deviceManager.isMobileNetworkConnected()).append("\n");
            sb.append("Power Save Mode : ").append(deviceManager.isPowerSaveMode()).append("\n");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.getMemoryInfo(mi);
                String availableMemory = humanReadableByteCount(mi.availMem);
                String totalMemory = humanReadableByteCount(mi.totalMem);

                double percent = (double)mi.availMem / (double)mi.totalMem * (double)100;
                sb.append("Memory Info (free): ").append(availableMemory).append(" / ").append(totalMemory);
                sb.append(" <- ").append(String.format("%.2f", percent)).append("%").append("\n");
            }

            sb.append("App version : ").append(getAppVersion(context)).append("\n");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean isReadPermission = deviceManager.isGantedPermissionReadExternalStorage();
                boolean isWritePermission = deviceManager.isGantedPermissionWriteExternalStorage();
                sb.append("READ_EXTERNAL_STORAGE permission : ").append(isReadPermission).append("\n");
                sb.append("WRITE_EXTERNAL_STORAGE permission : ").append(isWritePermission).append("\n");
            }

        }catch (Throwable t) {
        }

        return sb.toString();
    }


    private boolean isExternalMemoryAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 외장/내장 메모리 전체 용량을 구한다.
     * @param isInternal
     * @return
     */
    private long getStorageTotal(boolean isInternal) {
        String path = (isInternal ?
                Environment.getDataDirectory().getPath() :
                Environment.getExternalStorageDirectory().getPath());

        StatFs stat = new StatFs(path);
        long bytesAvailable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
        } else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount();
        }

        return bytesAvailable;
    }

    /**
     * 외장/내장 메모리 가용 용량을 구한다.
     * @param isInternal
     * @return
     */
    private long getStorageAvailable(boolean isInternal) {
        String path = (isInternal ?
                Environment.getDataDirectory().getPath() :
                Environment.getExternalStorageDirectory().getPath());

        StatFs stat = new StatFs(path);
        long bytesAvailable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        } else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }

        return bytesAvailable;
    }

    /**
     * byte를 사람이 읽기 편한 단위로 변환한다.
     * @param bytes
     * @return
     */
    //https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    private String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "" + ("KMGTPE").charAt(exp-1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * 앱의 버전 정보를 구한다.
     * @param context
     * @return
     */
    public String getAppVersion(Context context) {
        if(context == null) return "";
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
        }
        return "";
    }
}



