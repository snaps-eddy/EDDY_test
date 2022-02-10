package com.snaps.mobile.service.ai;

import android.media.ExifInterface;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoInfo {
    private static final String TAG = PhotoInfo.class.getSimpleName();
    private static final SimpleDateFormat AI_SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final float NOT_EXIST_GPS_VALUE = 0;
    public static final int NOT_EXIST_ORIENTATION_VALUE = ExifInterface.ORIENTATION_UNDEFINED;
    public static final long NOT_EXIST_DATE_TIME_VALUE = 0;
    private final long mID; //Android MediaStore ID
    private final String mFilePath;
    private final long mFileLastModifiedLong;
    private int mWidth;
    private int mHeight;
    private long mDateTakenLong;
    private long mDateModifedLong;
    private int mExifOrientation;
    private float mExifLatitude;
    private float mExifLongitude;
    private long mExifDateTimeLong;
    private String mUUID;


    public PhotoInfo(long id, String filePath, long fileLastModifiedLong) {
        mID = id;
        mFilePath = filePath;
        mFileLastModifiedLong = fileLastModifiedLong;
        mWidth = 0;
        mHeight = 0;
        mDateTakenLong = NOT_EXIST_DATE_TIME_VALUE;
        mDateModifedLong = NOT_EXIST_DATE_TIME_VALUE;
        mExifOrientation = NOT_EXIST_ORIENTATION_VALUE;
        mExifLatitude = NOT_EXIST_GPS_VALUE;
        mExifLongitude = NOT_EXIST_GPS_VALUE;
        mExifDateTimeLong = NOT_EXIST_DATE_TIME_VALUE;
        mUUID = "";
    }

    public long getID() {
        return mID;
    }

    public void setUUID(String uuid) {
        mUUID = uuid;
    }

    public String getUUID() {
        return mUUID;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public long getFileLastModifiedLong() {
        return mFileLastModifiedLong;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public long getDateTakenLong() {
        return mDateTakenLong;
    }

    public void setDateTakenLong(long dateTakenLong) {
        mDateTakenLong = dateTakenLong;
    }

    public long getDateModifedLong() {
        return mDateModifedLong;
    }

    public void setDateModifedLong(long dateModifedLong) {
        mDateModifedLong = dateModifedLong;
    }

    public int getExifOrientation() {
        return mExifOrientation;
    }

    public void setExifOrientation(int orientation) {
        mExifOrientation = orientation;
    }

    public float getExifLatitude() {
        return mExifLatitude;
    }

    public void setExifLatitude(float latitude) {
        mExifLatitude = latitude;
    }

    public float getExifLongitude() {
        return mExifLongitude;
    }

    public void setExifLongitude(float longitude) {
        mExifLongitude = longitude;
    }

    public long getExifDateTimeLong() {
        return mExifDateTimeLong;
    }

    public void setExifDateTimeLong(long exifDateTimeLong) {
        mExifDateTimeLong = exifDateTimeLong;
    }

    private StringBuffer appendDateFormatString(StringBuffer sb, String tag, long dateLong) {
        SimpleDateFormat sf = (SimpleDateFormat)AI_SimpleDateFormat.clone();
        sb.append(tag).append(":").append(dateLong);
        sb.append( "[");
        sb.append(sf.format(new Date(dateLong)));
        sb.append( "]");
        return sb;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("mID:").append(mID).append(", ");
        sb.append("mFilePath:").append(mFilePath).append(", ");
        appendDateFormatString(sb, "mFileLastModifiedLong", mFileLastModifiedLong).append(", ");
        sb.append("mWidth:").append(mWidth).append(", ");
        sb.append("mHeight:").append(mHeight).append(", ");
        appendDateFormatString(sb, "mDateTakenLong", mDateTakenLong).append(", ");
        appendDateFormatString(sb, "mDateModifedLong", mDateModifedLong).append(", ");
        sb.append("mExifOrientation:").append(mExifOrientation).append(", ");
        sb.append("mExifLatitude:").append(mExifLatitude).append(", ");
        sb.append("mExifLongitude:").append(mExifLongitude).append(", ");
        appendDateFormatString(sb, "mExifDateTimeLong", mExifDateTimeLong).append(", ");
        sb.append("mUUID:").append(mUUID);
        return sb.toString();
    }
}
