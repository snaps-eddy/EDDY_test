package com.snaps.mobile.service.ai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Exif 정보 Cache DB
 * ExifInterface 클래스를 이용해서 exif정보를 구하면 속도가 느리다. 그래서 ExifInterface 클래스로 구한 데이터를 저장해서 추후 속도 향상 목적
 */
class ExifCacheDB extends SQLiteOpenHelper {
    private static final String TAG = ExifCacheDB.class.getSimpleName();
    private volatile boolean mIsForceStop;    //한번 true로 변경하면 reset하는 기능 없음
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sync_exif_cache_new.db";
    private static final String TABLE_NAME_EXIF_INFO = "exifInfo";
    private static final String COLUMN_NAME_FILE_PATH = "file_path";
    private static final String COLUMN_NAME_FILE_LAST_MODIFIED = "file_last_modified";
    private static final String COLUMN_NAME_LATITUDE = "latitude";
    private static final String COLUMN_NAME_LONGITUDE = "longitude";
    private static final String COLUMN_NAME_ORIENTATION = "oCOLUMN_NAME_FILE_LAST_MODIFIEDrientation";
    private static final String COLUMN_NAME_DATETIME = "datetime";

    private int mColumnIndex_FILE_PATH;
    private int mColumnIndex_FILE_LAST_MODIFIED;
    private int mColumnIndex_DATETIME;
    private int mColumnIndex_LATITUDE;
    private int mColumnIndex_LONGITUDE;
    private int mColumnIndex_ORIENTATION;

    public ExifCacheDB(Context context, String databaseDir) {
        super(context, databaseDir + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
        mIsForceStop = false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME_EXIF_INFO + "(" +
                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME_FILE_PATH + " TEXT, " +
                        COLUMN_NAME_FILE_LAST_MODIFIED + " INTEGER, " +
                        COLUMN_NAME_LATITUDE + " REAL, " +
                        COLUMN_NAME_LONGITUDE + " REAL, " +
                        COLUMN_NAME_ORIENTATION + " INTEGER, " +
                        COLUMN_NAME_DATETIME + " INTEGER" +
                        ");";
        db.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME_EXIF_INFO;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    /**
     * 긴급 정지
     */
    public void forceStop() {
        Loggg.d(TAG, "forceStop");
        mIsForceStop = true;
    }

    public boolean isForceStop() {
        return mIsForceStop;
    }

    /**
     * 파일 path와 마지막 수정시간 기준으로 DB에 데이터가 있는 경우 데이터를 채운다.
     * @param photoInfoMap (in/out)
     * @throws SQLException
     */
    public void processSetExifInfoWithCacheDB(Map<String, PhotoInfo> photoInfoMap) throws SQLException {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        try {
            String sql = "select * from " + TABLE_NAME_EXIF_INFO + " ;";
            cursor = db.rawQuery(sql, null);
            if (cursor == null) {
                throw new SQLException("cursor is null");
            }
        }catch (SQLException e) {
            try {
                db.close();
            }catch (SQLException e2) {
            }
            throw e;
        }

        Loggg.d(TAG, "row count : " + cursor.getCount());

        //index cache
        mColumnIndex_FILE_PATH = cursor.getColumnIndex(COLUMN_NAME_FILE_PATH);
        mColumnIndex_FILE_LAST_MODIFIED = cursor.getColumnIndex(COLUMN_NAME_FILE_LAST_MODIFIED);
        mColumnIndex_DATETIME = cursor.getColumnIndex(COLUMN_NAME_DATETIME);
        mColumnIndex_LATITUDE = cursor.getColumnIndex(COLUMN_NAME_LATITUDE);
        mColumnIndex_LONGITUDE = cursor.getColumnIndex(COLUMN_NAME_LONGITUDE);
        mColumnIndex_ORIENTATION = cursor.getColumnIndex(COLUMN_NAME_ORIENTATION);

        try {
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false) {
                if (mIsForceStop) {
                    break;
                }

                String filePath = cursor.getString(mColumnIndex_FILE_PATH);
                PhotoInfo photoInfo = photoInfoMap.get(filePath);
                if (photoInfo != null) {
                    long db_fileLastModified = cursor.getLong(mColumnIndex_FILE_LAST_MODIFIED);
                    //시간을 비교해서 수정이 있었는지 검사한다. 수정이 없었다면 최신 정보이다.
                    if (db_fileLastModified == photoInfo.getFileLastModifiedLong()) {
                        photoInfo.setExifDateTimeLong(cursor.getLong(mColumnIndex_DATETIME));
                        photoInfo.setExifLatitude(cursor.getLong(mColumnIndex_LATITUDE));
                        photoInfo.setExifLongitude(cursor.getLong(mColumnIndex_LONGITUDE));
                        photoInfo.setExifOrientation(cursor.getInt(mColumnIndex_ORIENTATION));

                        //정보가 설정된 것은 맵에서 지운다.
                        //결국 맵에 남는 것은 ExifInterface를 이용해서 정보를 구해야 한다.
                        photoInfoMap.remove(filePath);
                        if (photoInfoMap.size() == 0) {
                            break;
                        }
                    }
                }

                cursor.moveToNext();
            }
        }catch (SQLException e) {
            throw e;
        }finally {
            cursor.close();
            db.close();
        }
    }


    /**
     * Insert PhotoInfo
     * @param list
     * @return
     */
    public void insertExifInfoList(List<PhotoInfo> list) throws SQLException {
        if (list == null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (PhotoInfo photoInfo : list) {
                if (mIsForceStop) {
                    break;
                }
                values.put(COLUMN_NAME_FILE_PATH, photoInfo.getFilePath());
                values.put(COLUMN_NAME_FILE_LAST_MODIFIED, photoInfo.getFileLastModifiedLong());
                values.put(COLUMN_NAME_LATITUDE, photoInfo.getExifLatitude());
                values.put(COLUMN_NAME_LONGITUDE, photoInfo.getExifLongitude());
                values.put(COLUMN_NAME_ORIENTATION, photoInfo.getExifOrientation());
                values.put(COLUMN_NAME_DATETIME, photoInfo.getExifDateTimeLong());
                db.insert(TABLE_NAME_EXIF_INFO, null, values);
            }
            db.setTransactionSuccessful();
        }catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * delete PhotoInfo
     * @param list
     * @throws SQLException
     */
    public void delete(List<PhotoInfo> list) throws SQLException {
        if (list.size() == 0) return;

        SQLiteDatabase db = getWritableDatabase();

        String whereClause = COLUMN_NAME_FILE_PATH + " = ?";
        String[] whereArgs = new String[1];
        try {
            for(PhotoInfo photoInfo : list) {
                whereArgs[0] = photoInfo.getFilePath();
                db.delete(TABLE_NAME_EXIF_INFO, whereClause, whereArgs);
            }
        }catch (SQLException e) {
            throw e;
        } finally {
            db.close();
        }
    }
}
