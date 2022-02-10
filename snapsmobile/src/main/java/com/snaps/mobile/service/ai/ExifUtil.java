package com.snaps.mobile.service.ai;

import android.media.ExifInterface;

import com.snaps.common.utils.log.Dlog;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 파일에서 Exif 정보를 구한다.
 */
class ExifUtil {
    private static final String TAG = ExifUtil.class.getSimpleName();
    //GPS 시간은 왜 단일 포맷이 없는지...
    private static final List<SimpleDateFormat> GPS_SimpleDateFormatList = Arrays.asList(
            new SimpleDateFormat("yyyy:MM:dd HH:mm:ss"),    //이게 경우의 수가 제일 많다.
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("dd/MM/yyyy HH:mm"),
            new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            );

    private long mMinValidDateLong;
    private long mMaxValidDateLong;

    public ExifUtil() {
    }

    public void init() {
        initValidDate();
    }

    /**
     * Exif 정보를 구한다.
     * @param photoInfo
     */
    public void setInfoOnPhotoInfo(PhotoInfo photoInfo) {
        //일단 기본값 설정
        photoInfo.setExifOrientation(PhotoInfo.NOT_EXIST_ORIENTATION_VALUE);
        photoInfo.setExifLatitude(PhotoInfo.NOT_EXIST_GPS_VALUE);
        photoInfo.setExifLongitude(PhotoInfo.NOT_EXIST_GPS_VALUE);
        photoInfo.setExifDateTimeLong(PhotoInfo.NOT_EXIST_DATE_TIME_VALUE);

        try {
            ExifInterface exifInterface = new ExifInterface(photoInfo.getFilePath());
            int orientation = getOrientation(exifInterface);
            float latitude = getLatitude(exifInterface);
            float longitude = getLongitude(exifInterface);
            long dateTimeLong = getDateTimeLong(exifInterface);

            photoInfo.setExifOrientation(orientation);
            photoInfo.setExifLatitude(latitude);
            photoInfo.setExifLongitude(longitude);
            photoInfo.setExifDateTimeLong(dateTimeLong);

        }catch(Exception e) {
            Loggg.e(TAG, e);
        }
    }

    /**
     * 위도 값을 구한다.
     * @param exifInterface
     * @return
     */
    private float getLatitude(ExifInterface exifInterface) {
        try {
            String attrLATITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            if (attrLATITUDE == null || attrLATITUDE.length() == 0) return PhotoInfo.NOT_EXIST_GPS_VALUE;

            String attrLATITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            if (attrLATITUDE_REF == null || attrLATITUDE_REF.length() == 0) return PhotoInfo.NOT_EXIST_GPS_VALUE;

            float latitude = convertRationalLatLonToFloat(attrLATITUDE, attrLATITUDE_REF);
            return latitude;
        }catch(Exception e) {
            Loggg.e(TAG, e);
            return PhotoInfo.NOT_EXIST_GPS_VALUE;
        }
    }

    /**
     * 경도 값을 구한다.
     * @param exifInterface
     * @return
     */
    private float getLongitude(ExifInterface exifInterface) {
        try {
            String attrLONGITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            if (attrLONGITUDE == null || attrLONGITUDE.length() == 0) return PhotoInfo.NOT_EXIST_GPS_VALUE;

            String attrLONGITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            if (attrLONGITUDE_REF == null ||attrLONGITUDE_REF.length() == 0) return PhotoInfo.NOT_EXIST_GPS_VALUE;

            float longitude = convertRationalLatLonToFloat(attrLONGITUDE, attrLONGITUDE_REF);
            return longitude;
        }catch(Exception e) {
            Loggg.e(TAG, e);
            return PhotoInfo.NOT_EXIST_GPS_VALUE;
        }
    }

    /**
     * 도분초 단위를 도단위로 변경한다.
     * @param rationalString
     * @param ref
     * @return
     */
    private float convertRationalLatLonToFloat(String rationalString, String ref) {
        //https://android.googlesource.com/platform/frameworks/base/+/android-cts-4.4_r1/media/java/android/media/ExifInterface.java
        try {
            String[] parts = rationalString.split(",");
            String[] pair;

            pair = parts[0].split("/");
            double degrees = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());

            pair = parts[1].split("/");
            double minutes = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());

            pair = parts[2].split("/");
            double seconds = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());

            double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
            if ((ref.equals("S") || ref.equals("W"))) {
                return (float) -result;
            }
            return (float)result;
        } catch (Exception e) {
            Loggg.e(TAG, e);
            return PhotoInfo.NOT_EXIST_GPS_VALUE;
        }
    }

    /**
     * 회전 값을 구한다.
     * @param exifInterface
     * @return
     */
    private int getOrientation(ExifInterface exifInterface) {
        try {
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            return orientation;
        } catch (Exception e) {
            Loggg.e(TAG, e);
            return PhotoInfo.NOT_EXIST_ORIENTATION_VALUE;
        }
    }

    /**
     * 시간 정보를 구한다.
     * @param exifInterface
     * @return
     */
    private long getDateTimeLong(ExifInterface exifInterface) {
        try {
            String dateTimeString = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            if (dateTimeString == null || dateTimeString.length() == 0) return PhotoInfo.NOT_EXIST_DATE_TIME_VALUE;

            Date date = null;
            for(SimpleDateFormat sf : GPS_SimpleDateFormatList) {
                try {
                    date = sf.parse(dateTimeString);
                    break;
                }catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
            if (date == null) {
                return PhotoInfo.NOT_EXIST_DATE_TIME_VALUE;
            }
            long dateLong = date.getTime();
            dateLong = fixInvalidDate(dateLong);
            return dateLong;
        } catch (Exception e) {
            Loggg.e(TAG, e);
            return PhotoInfo.NOT_EXIST_DATE_TIME_VALUE;
        }
    }


    /**
     * 정상시간 값의 범위를 초기화 한다.
     */
    private void initValidDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 1, 1,0,0,0);
        mMinValidDateLong = calendar.getTimeInMillis(); //그냥 0대입 하면 되는데 혹시 변경에 대비

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 60);
        mMaxValidDateLong = calendar.getTimeInMillis();
    }

    /**
     * 시간이 이상한 경우 한번 수정해보고 그래도 이상하면 0을 리턴
     * @param time
     * @return
     */
    private long fixInvalidDate(long time) {
        //원본 코드 여기 저기 카피 해서 수정
        if (time == 0) return PhotoInfo.NOT_EXIST_DATE_TIME_VALUE;

        if (isValidDate(time)) return time;

        long tmpTime = time * 1000; //sns 사진 중 second로 기록 되어 있는 날짜가 있다.
        if (isValidDate(tmpTime)) return tmpTime;

        return PhotoInfo.NOT_EXIST_DATE_TIME_VALUE;
    }

    /**
     * 시간이 정상인지 검사한다.
     * @param time
     * @return
     */
    private boolean isValidDate(long time) {
        return (mMinValidDateLong <= time && time <= mMaxValidDateLong);
    }
}
