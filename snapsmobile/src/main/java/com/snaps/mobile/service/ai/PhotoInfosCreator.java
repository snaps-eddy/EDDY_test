package com.snaps.mobile.service.ai;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 단말에 저장된 사진의 Exif 정보를 제외한 나머지 정보를 구한다.
 */
class PhotoInfosCreator {
    private static final String TAG = PhotoInfosCreator.class.getSimpleName();
    private final boolean mIsSupportImageWHAndroidVerion;
    private List<String> mScreenShotsDirList;
    private Point mPoint;
    private BitmapFactory.Options mBitmapFactoryOptions;
    private volatile boolean mIsForceStop;  //한번 true로 변경하면 reset하는 기능 없음
    private long mMinValidDateLong;
    private long mMaxValidDateLong;
    private int mColumnIndex_ID;
    private int mColumnIndex_DATA;
    private int mColumnIndex_DATE_TAKEN;
    private int mColumnIndex_DATE_MODIFIED;
    private int mColumnIndex_DATE_WIDTH;
    private int mColumnIndex_DATE_HEIGHT;

    //미디어 스토어에서 가져올 데이터
    private static final String[] PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
    };

    public PhotoInfosCreator() {
        mIsForceStop = false;

        File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String screenShotDir1 = dcimDir.getAbsolutePath() + File.separator + "Screenshots";

        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String screenShotDir2 = pictureDir.getAbsolutePath() + File.separator + "Screenshots";

        mScreenShotsDirList = new ArrayList<String>();
        mScreenShotsDirList.add(screenShotDir1.toLowerCase());
        mScreenShotsDirList.add(screenShotDir2.toLowerCase());

        mPoint = new Point();
        mBitmapFactoryOptions = new BitmapFactory.Options();
        mBitmapFactoryOptions.inJustDecodeBounds = true;

        // 2019년 5월 기준 거의 단말이 젤리빈 이상이지만
        mIsSupportImageWHAndroidVerion = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 강제 정지
     */
    public void forceStop() {
        Loggg.d(TAG, "forceStop");
        mIsForceStop = true;
    }

    /**
     *
     * @return
     */
    public boolean isForceStop() {
        return mIsForceStop;
    }

    /**
     * 분석 대상 사진 숫자를 리턴한다.
     * @param context
     * @return
     */
    public int getPhotoCount(Context context) throws SQLException {
        String[] projection = {
                MediaStore.Images.Media.DATA,
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        );
        if (cursor == null) {
            Loggg.e(TAG, "cursor is null");
            throw new SQLException("cursor is null");
        }

        int columnIndex_DATA = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

        int count = 0;
        try {
            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                if (mIsForceStop) {
                    count = 0;
                    break;
                }

                String filePath = cursor.getString(columnIndex_DATA);
                if (isTargetFileType(filePath)) {
                    count++;
                }

                cursor.moveToNext();
            }
        }catch (SQLException e) {
            Loggg.e(TAG, e);
            throw e;
        } finally {
            cursor.close();
        }

        return count;
    }


    /**
     * 미디어 스토어에서 Exif 정보를 제외한 사진 정보를 만든다.
     * @param context
     * @return
     */
    public Map<String, PhotoInfo> createPhotoInfoMapExceptForExif(Context context) throws SQLException {
        Map<String, PhotoInfo> photoInfoMap = new HashMap<String, PhotoInfo>();

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        );
        if (cursor == null) {
            Loggg.e(TAG, "cursor is null");
            throw new SQLException("cursor is null");
        }


        //index cache
        mColumnIndex_ID = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        mColumnIndex_DATA = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        mColumnIndex_DATE_TAKEN = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
        mColumnIndex_DATE_MODIFIED = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
        mColumnIndex_DATE_WIDTH = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH);
        mColumnIndex_DATE_HEIGHT = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT);

        initValidDateRange();
        int count = 0;

        try {
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false) {
                if (mIsForceStop) {
                    photoInfoMap.clear();
                    break;
                }

                PhotoInfo photoInfo = createPhotoInfo(cursor);
                if (photoInfo != null) {
                    photoInfoMap.put(photoInfo.getFilePath(), photoInfo);
                }

                count++;
                if (count % 10000 == 0) {
                    //사진 15만장 가지고 있는 사람이 있어서 로그 추가
                    Loggg.d(TAG, "createPhotoInfoMapExceptForExif: " + count);
                }

                //FOR TEST(개발 할때 기다리는게 싫어서)
                //if (photoInfoMap.size() == 100) break;

                cursor.moveToNext();
            }
        }catch (SQLException e) {
            Loggg.e(TAG, e);
            throw e;
        } finally {
            cursor.close();
        }

        return photoInfoMap;
    }

    /**
     * 미디어 스토어 정보를 이용해서 PhotoInfo를 생성한다.
     * @param cursor
     * @return
     */
    @Nullable
    private PhotoInfo createPhotoInfo(Cursor cursor) {
        long id = cursor.getLong(mColumnIndex_ID);

        String filePath = cursor.getString(mColumnIndex_DATA);
        if (isTargetFileType(filePath) == false) {
            return null;
        }

        long lastModifiedLong = 0;
        File file = new File(filePath);
        if (file.isFile() == false) {
            Loggg.e(TAG, "file.isFile() is false : " + filePath);
            return null;
        }

        lastModifiedLong = file.lastModified();

        PhotoInfo photoInfo = new PhotoInfo(id, filePath, lastModifiedLong);

        long dateTakenLong = cursor.getLong(mColumnIndex_DATE_TAKEN);
        dateTakenLong = fixInvalidDate(dateTakenLong);
        photoInfo.setDateTakenLong(dateTakenLong);

        long dateModifiedLong = cursor.getLong(mColumnIndex_DATE_MODIFIED);
        dateModifiedLong = fixInvalidDate(dateModifiedLong);
        photoInfo.setDateModifedLong(dateModifiedLong);

        if (mIsSupportImageWHAndroidVerion) {
            photoInfo.setWidth(cursor.getInt(mColumnIndex_DATE_WIDTH));
            photoInfo.setHeight(cursor.getInt(mColumnIndex_DATE_HEIGHT));
        } else {
            Point point = getImageWidthAndHeight(filePath);
            photoInfo.setWidth(point.x);
            photoInfo.setHeight(point.y);
        }

        return photoInfo;
    }

    /**
     * 이미지 파일의 정보를 구한다.
     * @param filePath
     * @return
     */
    private Point getImageWidthAndHeight(String filePath) {
        mPoint.x = 0;
        mPoint.y = 0;
        try {
            BitmapFactory.decodeFile(filePath, mBitmapFactoryOptions);
        }
        catch (Exception e) {
            Loggg.e(TAG, e);
            return mPoint;
        }

        mPoint.x = mBitmapFactoryOptions.outWidth;
        mPoint.y = mBitmapFactoryOptions.outHeight;
        return mPoint;
    }

    /**
     * 대상 파일인지 검사한다.
     * @param filePath
     * @return
     */
    private boolean isTargetFileType(String filePath) {
        String filePathLowerCase = filePath.toLowerCase();
        for(String screenShotsDir : mScreenShotsDirList) {
            if (filePathLowerCase.startsWith(screenShotsDir)) {
                return false;
            }
        }

        String fileName = filePathLowerCase.substring(filePath.lastIndexOf("\\") + 1);

        //스크린 샷 이미지 필터링
        if (fileName.startsWith("screenshot_")) {
            return false;
        }

        if (fileName.endsWith(".jpg")) return true;
        if (fileName.endsWith(".jpeg")) return true;
        return false;
    }

    /**
     * 정상시간 값의 범위를 초기화 한다.
     */
    private void initValidDateRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 1, 1,0,0,0);
        mMinValidDateLong = calendar.getTimeInMillis(); //그냥 0대입 하면 되는데 혹시 변경에 대비

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
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
