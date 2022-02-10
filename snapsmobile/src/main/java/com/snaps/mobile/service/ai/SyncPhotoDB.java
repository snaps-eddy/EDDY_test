package com.snaps.mobile.service.ai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SyncPhotoDB extends SQLiteOpenHelper {
    private static final String TAG = SyncPhotoDB.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sync_photo.db";
    private static final String TABLE_NAME_FILE_INFO = "file_info";  //단말의 사진 정보를 가지고 있는 테이블
    private static final String TABLE_NAME_MUST_SEND_EXIF_FILE_INFO = "must_send_exif_file_info";  //서버에 전송할 exif 정보 압축 파일 정보 테이블
    public static final String COLUMN_NAME_FILE_PATH = "file_path";
    public static final String COLUMN_NAME_FILE_LAST_MODIFIED = "file_last_modified";
    public static final String COLUMN_NAME_IMAGE_ID = "image_id";
    public static final String COLUMN_NAME_UUID = "uuid";
    public static final String COLUMN_NAME_WIDTH = "width";
    public static final String COLUMN_NAME_HEIGHT = "height";
    public static final String COLUMN_NAME_DATE_TAKEN = "date_taken";
    public static final String COLUMN_NAME_DATE_MODIFIED = "date_modified";
    public static final String COLUMN_NAME_EXIF_DATETIME = "exif_datetime";
    public static final String COLUMN_NAME_EXIF_LATITUDE = "exif_latitude";
    public static final String COLUMN_NAME_EXIF_LONGITUDE = "exif_longitude";
    public static final String COLUMN_NAME_EXIF_ORIENTATION = "exif_orientation";
    public static final String COLUMN_NAME_SYNC_TYPE = "sync_type";
    public static final String COLUMN_NAME_TXID = "txid";

    private volatile boolean mIsForceStop;  //한번 true로 변경하면 reset하는 기능 없음
    private int mFileInfo_ColumnIndex_FILE_PATH;
    private int mFileInfo_ColumnIndex_FILE_LAST_MODIFIED;
    private int mFileInfo_ColumnIndex_IMAGE_ID;
    private int mFileInfo_ColumnIndex_UUID;
    private int mFileInfo_ColumnIndex_WIDTH;
    private int mFileInfo_ColumnIndex_HEIGHT;
    private int mFileInfo_ColumnIndex_DATE_TAKEN;
    private int mFileInfo_ColumnIndex_DATE_MODIFIED;
    private int mFileInfo_ColumnIndex_EXIF_DATETIME;
    private int mFileInfo_ColumnIndex_EXIF_LATITUDE;
    private int mFileInfo_ColumnIndex_EXIF_LONGITUDE;
    private int mFileInfo_ColumnIndex_EXIF_ORIENTATION;
    private volatile ExifCacheDB mExifCacheDB;
    private ExifUtil mExifUtil;

    static class ChangePhotoInfo {
        private List<PhotoInfo> mNewList;
        private List<PhotoInfo> mModifyList;
        private List<PhotoInfo> mDeleteList;

        public ChangePhotoInfo() {
            mNewList = new ArrayList<PhotoInfo>();
            mModifyList = new ArrayList<PhotoInfo>();
            mDeleteList = new ArrayList<PhotoInfo>();
        }

        public List<PhotoInfo> getNewList() {
            return mNewList;
        }

        public List<PhotoInfo> getModifyList() {
            return mModifyList;
        }

        public List<PhotoInfo> getDeleteList() {
            return mDeleteList;
        }

        public boolean isChanged() {
            if (mNewList.size() > 0) return true;
            if (mModifyList.size() > 0) return true;
            if (mDeleteList.size() > 0) return true;
            return false;
        }

        public int getAllCount() {
            return mNewList.size() + mModifyList.size() + mDeleteList.size();
        }

        public void clear() {
            mNewList.clear();
            mModifyList.clear();
            mDeleteList.clear();
        }

        public String getSummaryText() {
            StringBuilder sb = new StringBuilder();
            sb.append("New:").append(mNewList.size()).append(", ");
            sb.append("Modified:").append(mModifyList.size()).append(", ");
            sb.append("Deleted:").append(mDeleteList.size());

            return sb.toString();
        }
    }

    public SyncPhotoDB(Context context, String syncRoot, String userDir) {
        super(context, userDir + File.separator + DATABASE_NAME, null, DATABASE_VERSION);

        mIsForceStop = false;
        mExifCacheDB = new ExifCacheDB(context, syncRoot);
        mExifUtil = new ExifUtil();

        mFileInfo_ColumnIndex_FILE_PATH = Integer.MIN_VALUE;     // set invalid value [꼼수]
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_FILE_INFO =
                "CREATE TABLE " + TABLE_NAME_FILE_INFO + "(" +
                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME_FILE_PATH + " TEXT, " +
                        COLUMN_NAME_FILE_LAST_MODIFIED + " INTEGER, " +
                        COLUMN_NAME_IMAGE_ID + " INTEGER, " +
                        COLUMN_NAME_UUID + " TEXT, " +
                        COLUMN_NAME_WIDTH + " INTEGER, " +
                        COLUMN_NAME_HEIGHT + " INTEGER, " +
                        COLUMN_NAME_DATE_TAKEN + " INTEGER, " +
                        COLUMN_NAME_DATE_MODIFIED + " INTEGER, " +
                        COLUMN_NAME_EXIF_DATETIME + " INTEGER, " +
                        COLUMN_NAME_EXIF_LATITUDE + " REAL, " +
                        COLUMN_NAME_EXIF_LONGITUDE + " REAL, " +
                        COLUMN_NAME_EXIF_ORIENTATION + " INTEGER" +
                        ");";
        db.execSQL(CREATE_TABLE_FILE_INFO);

        final String CREATE_TABLE_MUST_SEND_EXIF_FILE_INFO =
                "CREATE TABLE " + TABLE_NAME_MUST_SEND_EXIF_FILE_INFO + "(" +
                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME_FILE_PATH + " TEXT, " +
                        COLUMN_NAME_TXID + " TEXT, " +
                        COLUMN_NAME_SYNC_TYPE + " TEXT" +
                        ");";
        db.execSQL(CREATE_TABLE_MUST_SEND_EXIF_FILE_INFO);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE_FILE_INFO = "DROP TABLE IF EXISTS " + TABLE_NAME_FILE_INFO;
        db.execSQL(DROP_TABLE_FILE_INFO);

        String DROP_TABLE_MUST_SEND_EXIF_FILE_INFO = "DROP TABLE IF EXISTS " + TABLE_NAME_MUST_SEND_EXIF_FILE_INFO;
        db.execSQL(DROP_TABLE_MUST_SEND_EXIF_FILE_INFO);

        onCreate(db);
    }

    /**
     * 강제 정지
     */
    public void forceStop() {
        Loggg.d(TAG, "forceStop");
        mIsForceStop = true;
        mExifCacheDB.forceStop();
    }

    /**
     *
     * @return
     */
    public boolean isForceStop() {
        return mIsForceStop;
    }

    /**
     * 현재 row count를 리턴한다.
     * @return
     */
    public long getRowCount() throws SQLException {
        SQLiteDatabase db = getReadableDatabase();

        long count;
        try {
            count = DatabaseUtils.queryNumEntries(db, TABLE_NAME_FILE_INFO);
        }catch (SQLException e) {
            throw e;
        }finally {
            db.close();
        }

        return count;
    }

    /**
     * 조건에 맞는 값으로 PhotoInfo를 생성한다.
     * @param columnName
     * @param value
     * @return
     */
    public List<PhotoInfo> selectPhotoInfoList(String columnName, List<String> value) throws SQLException {
        List<PhotoInfo> photoInfoList = new ArrayList<PhotoInfo>();

        SQLiteDatabase db = getReadableDatabase();

        final String sql = "select * from " + TABLE_NAME_FILE_INFO + " " + "where " + columnName + " = ?";
        String[] args = {""};
        for(String queryValue : value) {
            if (mIsForceStop) {
                break;
            }

            Cursor cursor;
            try {
                args[0] = queryValue;
                cursor = db.rawQuery(sql, args);
                if (cursor == null) {
                    throw new SQLException("cursor is null");
                }
            }catch (SQLException e) {
                db.close();
                throw e;
            }

            if (mFileInfo_ColumnIndex_FILE_PATH == Integer.MIN_VALUE) {
                mFileInfo_ColumnIndex_FILE_PATH = cursor.getColumnIndex(COLUMN_NAME_FILE_PATH);
                mFileInfo_ColumnIndex_FILE_LAST_MODIFIED = cursor.getColumnIndex(COLUMN_NAME_FILE_LAST_MODIFIED);
                mFileInfo_ColumnIndex_IMAGE_ID = cursor.getColumnIndex(COLUMN_NAME_IMAGE_ID);
                mFileInfo_ColumnIndex_UUID = cursor.getColumnIndex(COLUMN_NAME_UUID);
                mFileInfo_ColumnIndex_WIDTH = cursor.getColumnIndex(COLUMN_NAME_WIDTH);
                mFileInfo_ColumnIndex_HEIGHT = cursor.getColumnIndex(COLUMN_NAME_HEIGHT);
                mFileInfo_ColumnIndex_DATE_TAKEN = cursor.getColumnIndex(COLUMN_NAME_DATE_TAKEN);
                mFileInfo_ColumnIndex_DATE_MODIFIED = cursor.getColumnIndex(COLUMN_NAME_DATE_MODIFIED);
                mFileInfo_ColumnIndex_EXIF_DATETIME = cursor.getColumnIndex(COLUMN_NAME_EXIF_DATETIME);
                mFileInfo_ColumnIndex_EXIF_LATITUDE = cursor.getColumnIndex(COLUMN_NAME_EXIF_LATITUDE);
                mFileInfo_ColumnIndex_EXIF_LONGITUDE = cursor.getColumnIndex(COLUMN_NAME_EXIF_LONGITUDE);
                mFileInfo_ColumnIndex_EXIF_ORIENTATION = cursor.getColumnIndex(COLUMN_NAME_EXIF_ORIENTATION);
            }

            try {
                if (cursor.moveToFirst()) {
                    long imageID = cursor.getLong(mFileInfo_ColumnIndex_IMAGE_ID);
                    String filePath = cursor.getString(mFileInfo_ColumnIndex_FILE_PATH);
                    long fileLastModified = cursor.getLong(mFileInfo_ColumnIndex_FILE_LAST_MODIFIED);

                    PhotoInfo photoInfo = new PhotoInfo(imageID, filePath, fileLastModified);

                    photoInfo.setUUID(cursor.getString(mFileInfo_ColumnIndex_UUID));
                    photoInfo.setWidth(cursor.getInt(mFileInfo_ColumnIndex_WIDTH));
                    photoInfo.setHeight(cursor.getInt(mFileInfo_ColumnIndex_HEIGHT));
                    photoInfo.setDateTakenLong(cursor.getLong(mFileInfo_ColumnIndex_DATE_TAKEN));
                    photoInfo.setDateModifedLong(cursor.getLong(mFileInfo_ColumnIndex_DATE_MODIFIED));
                    photoInfo.setExifDateTimeLong(cursor.getLong(mFileInfo_ColumnIndex_EXIF_DATETIME));
                    photoInfo.setExifLatitude(cursor.getFloat(mFileInfo_ColumnIndex_EXIF_LATITUDE));
                    photoInfo.setExifLongitude(cursor.getFloat(mFileInfo_ColumnIndex_EXIF_LONGITUDE));
                    photoInfo.setExifOrientation(cursor.getInt(mFileInfo_ColumnIndex_EXIF_ORIENTATION));

                    photoInfoList.add(photoInfo);
                }
            }catch (SQLException e) {
                try {
                    db.close();
                }catch (SQLException e2) {
                }
                throw e;
            }finally {
                cursor.close();
            }
        }

        db.close();

        return photoInfoList;
    }

    /**
     * 전송해야 하는 exif 전체 정보 파일 정보를 삭제한다.
     * @return
     */
    public void deleteMustSendExifInfoFile() throws SQLException {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.delete(TABLE_NAME_MUST_SEND_EXIF_FILE_INFO, null, null);
        }catch (SQLException e) {
            throw e;
        } finally {
            db.close();
        }
    }

    static class MustSendExifFileInfo {
        public final String mFilePath;
        public final String mTxId;
        public final String mSyncType;

        MustSendExifFileInfo(String filePath, String txId, String syncType) {
            mFilePath = filePath;
            mTxId = txId;
            mSyncType = syncType;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("mSyncType:").append(mSyncType).append(", ");
            sb.append("mTxId:").append(mTxId).append(", ");
            sb.append("mFilePath:").append(mFilePath);
            return sb.toString();
        }
    }

    /**
     * 전송확인이 필요한 exif 전체 정보 파일을 구한다.
     * @return 없으면 null을 리턴
     */
    @Nullable
    public MustSendExifFileInfo getMustSendExifFileInfo() throws SQLException {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        try {
            String sql = "select * from " + TABLE_NAME_MUST_SEND_EXIF_FILE_INFO + " ;";
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

        MustSendExifFileInfo mustSendExifFileInfo = null;
        try {
            if (cursor.moveToFirst()) {
                String filePath = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FILE_PATH));
                String txId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TXID));
                String syncType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SYNC_TYPE));
                mustSendExifFileInfo = new MustSendExifFileInfo(filePath, txId, syncType);
            }
        }catch (SQLException e) {
            throw e;
        }finally {
            cursor.close();
            db.close();
        }

        return mustSendExifFileInfo;
    }

    /**
     * DB에 변경된 내용에 반영한다.
     * @param changePhotoInfo
     * @return
     */
    public void sync(
            ChangePhotoInfo changePhotoInfo,
            String exifInfoFilePath,
            String txId,
            String exifInfoSyncType) throws SQLException
    {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            //insert
            List<PhotoInfo> newPhotoInfoList = changePhotoInfo.getNewList();
            ContentValues values = new ContentValues();
            for (PhotoInfo photoInfo : newPhotoInfoList) {
                values.put(COLUMN_NAME_FILE_PATH, photoInfo.getFilePath());
                values.put(COLUMN_NAME_FILE_LAST_MODIFIED, photoInfo.getFileLastModifiedLong());
                values.put(COLUMN_NAME_IMAGE_ID, photoInfo.getID());
                values.put(COLUMN_NAME_UUID, photoInfo.getUUID());
                values.put(COLUMN_NAME_WIDTH, photoInfo.getWidth());
                values.put(COLUMN_NAME_HEIGHT, photoInfo.getHeight());
                values.put(COLUMN_NAME_DATE_TAKEN, photoInfo.getDateTakenLong());
                values.put(COLUMN_NAME_DATE_MODIFIED, photoInfo.getDateModifedLong());
                values.put(COLUMN_NAME_EXIF_DATETIME, photoInfo.getExifDateTimeLong());
                values.put(COLUMN_NAME_EXIF_LATITUDE, photoInfo.getExifLatitude());
                values.put(COLUMN_NAME_EXIF_LONGITUDE, photoInfo.getExifLongitude());
                values.put(COLUMN_NAME_EXIF_ORIENTATION, photoInfo.getExifOrientation());
                db.insert(TABLE_NAME_FILE_INFO, null, values);
            }

            //update
            String whereClause = COLUMN_NAME_FILE_PATH + " = ?";
            String[] whereArgs = new String[1];
            List<PhotoInfo> modifiedPhotoInfoList = changePhotoInfo.getModifyList();
            values.clear();
            for (PhotoInfo photoInfo : modifiedPhotoInfoList) {
                values.put(COLUMN_NAME_FILE_LAST_MODIFIED, photoInfo.getFileLastModifiedLong());
                values.put(COLUMN_NAME_IMAGE_ID, photoInfo.getID());
                values.put(COLUMN_NAME_UUID, photoInfo.getUUID());
                values.put(COLUMN_NAME_WIDTH, photoInfo.getWidth());
                values.put(COLUMN_NAME_HEIGHT, photoInfo.getHeight());
                values.put(COLUMN_NAME_DATE_TAKEN, photoInfo.getDateTakenLong());
                values.put(COLUMN_NAME_DATE_MODIFIED, photoInfo.getDateModifedLong());
                values.put(COLUMN_NAME_EXIF_DATETIME, photoInfo.getExifDateTimeLong());
                values.put(COLUMN_NAME_EXIF_LATITUDE, photoInfo.getExifLatitude());
                values.put(COLUMN_NAME_EXIF_LONGITUDE, photoInfo.getExifLongitude());
                values.put(COLUMN_NAME_EXIF_ORIENTATION, photoInfo.getExifOrientation());
                whereArgs[0] = photoInfo.getFilePath();
                db.update(TABLE_NAME_FILE_INFO, values, whereClause, whereArgs);
            }

            //delete
            List<PhotoInfo> deletedPhotoInfoList = changePhotoInfo.getDeleteList();
            for (PhotoInfo photoInfo : deletedPhotoInfoList) {
                whereArgs[0] = photoInfo.getFilePath();
                db.delete(TABLE_NAME_FILE_INFO, whereClause, whereArgs);
            }

            //exif file
            //혹시 모르니 일단 전부 삭제 (실제 테이블에 데이터가 없어야 정상)
            db.delete(TABLE_NAME_MUST_SEND_EXIF_FILE_INFO, null, null);
            //insert
            values.clear();
            values.put(COLUMN_NAME_FILE_PATH, exifInfoFilePath);
            values.put(COLUMN_NAME_TXID, txId);
            values.put(COLUMN_NAME_SYNC_TYPE, exifInfoSyncType);
            db.insert(TABLE_NAME_MUST_SEND_EXIF_FILE_INFO, null, values);

            db.setTransactionSuccessful();
        }catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
            db.close();
        }

        //삭제된 파일은 ExifCache DB에서 지운다. 추후 속도 향샹 목적
        mExifCacheDB.delete(changePhotoInfo.getDeleteList());
    }

    /**
     * DB와 미디어 스토어를 기반으로 만든 정보를 비교해서 변경된 정보를 찾는다. (Exif정보 포함)
     * @param photoInfoMap
     * @return null을 리턴 할 수 있다.
     */
    @Nullable
    public ChangePhotoInfo compare(Map<String, PhotoInfo> photoInfoMap) {
        //DB와 현재 미디어 정보를 비교한다.
        ChangePhotoInfo changePhotoInfo = compareDBandFileSystem(photoInfoMap);

        //신규 또는 수정은 어차피 Exif정보를 구하는 것은 동일하므로 map하나에 합친다.
        Map<String, PhotoInfo> updateExifPhotoInfoMap = new HashMap<String, PhotoInfo>();
        List<PhotoInfo> newPhotoList = changePhotoInfo.getNewList();
        for(PhotoInfo photoInfo : newPhotoList) {
            updateExifPhotoInfoMap.put(photoInfo.getFilePath(), photoInfo);
        }

        List<PhotoInfo> modifiedPhotoList = changePhotoInfo.getModifyList();
        for(PhotoInfo photoInfo : modifiedPhotoList) {
            updateExifPhotoInfoMap.put(photoInfo.getFilePath(), photoInfo);
        }

        if (updateExifPhotoInfoMap.size() > 0) {
            //Exif 정보를 일단 cache DB에 있는 값으로 채워본다.
            Loggg.d(TAG, "before processSetExifInfoWithCacheDB():" + updateExifPhotoInfoMap.size());
            mExifCacheDB.processSetExifInfoWithCacheDB(updateExifPhotoInfoMap);
            if (isForceStop()) {
                changePhotoInfo.clear();
                return changePhotoInfo;
            }
            Loggg.d(TAG, "after processSetExifInfoWithCacheDB():" + updateExifPhotoInfoMap.size());

            //채우지 못한 나머지
            if (updateExifPhotoInfoMap.size() > 0) {
                int totalProgress = changePhotoInfo.getNewList().size() + changePhotoInfo.getModifyList().size();
                //cache DB에 데이터가 없으면 ExifUtil을 이용한다. (이게 시간이 오래 걸린다.)

                int currentProgress = totalProgress - photoInfoMap.size();
                OverallProgress.getInstance().setTotal(OverallProgress.Part.CREATE_EXIF_INFO, totalProgress);
                OverallProgress.getInstance().setValue(OverallProgress.Part.CREATE_EXIF_INFO, currentProgress);

                progressSetExifInfoAndInsertCacheDB(updateExifPhotoInfoMap, currentProgress);
                if (isForceStop()){
                    changePhotoInfo.clear();
                    return changePhotoInfo;
                }
            }
        }

        //프로그래스바 처리
        OverallProgress.getInstance().setPercent(OverallProgress.Part.CREATE_EXIF_INFO, 100);

        return changePhotoInfo;
    }

    private String createUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void progressSetExifInfoAndInsertCacheDB(Map<String, PhotoInfo> photoInfoMap, int currentProgress) {
        mExifUtil.init();

        int totalProgress = photoInfoMap.size();
        int count = 0;  //Exif cache DB에 중간중간 저장해야하므로 카운트, 저장안하면 처음부터 다시해야 하므로

        List<PhotoInfo> insertPhotoList = new ArrayList<PhotoInfo>();
        for (Map.Entry<String, PhotoInfo> entry : photoInfoMap.entrySet()) {
            if (mIsForceStop) {
                return;
            }
            PhotoInfo photoInfo = entry.getValue();
            mExifUtil.setInfoOnPhotoInfo(photoInfo); //이건 속도가 느림
            insertPhotoList.add(photoInfo);

            count++;
            if (count % 100 == 0) { //하드 코딩
                mExifCacheDB.insertExifInfoList(insertPhotoList);
                insertPhotoList.clear();
                Loggg.d(TAG,"ExifUtil:" + count + "/" + totalProgress);
                Monitoring.getInstance().setInfo("ExifUtil progress", count + "/" + totalProgress);
            }

            currentProgress++;
            if (count % 10 == 0) { //하드 코딩
                OverallProgress.getInstance().setValue(OverallProgress.Part.CREATE_EXIF_INFO, currentProgress);
            }
        }

        Loggg.d(TAG,"ExifUtil:" + count + "/" + totalProgress);
        Monitoring.getInstance().setInfo("ExifUtil progress", count + "/" + totalProgress);
    }

    /**
     * DB와 미디어 스토어를 기반으로 만든 정보를 비교해서 변경된 정보를 찾는다.
     * @param photoInfoMap
     * @return
     */
    private ChangePhotoInfo compareDBandFileSystem(Map<String, PhotoInfo> photoInfoMap) throws SQLException {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        try {
            String sql = "select * from " + TABLE_NAME_FILE_INFO + " ;";
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

        //index cache
        mFileInfo_ColumnIndex_FILE_PATH = cursor.getColumnIndex(COLUMN_NAME_FILE_PATH);
        mFileInfo_ColumnIndex_FILE_LAST_MODIFIED = cursor.getColumnIndex(COLUMN_NAME_FILE_LAST_MODIFIED);
        mFileInfo_ColumnIndex_IMAGE_ID = cursor.getColumnIndex(COLUMN_NAME_IMAGE_ID);
        mFileInfo_ColumnIndex_UUID = cursor.getColumnIndex(COLUMN_NAME_UUID);
        mFileInfo_ColumnIndex_WIDTH = cursor.getColumnIndex(COLUMN_NAME_WIDTH);
        mFileInfo_ColumnIndex_HEIGHT = cursor.getColumnIndex(COLUMN_NAME_HEIGHT);
        mFileInfo_ColumnIndex_DATE_TAKEN = cursor.getColumnIndex(COLUMN_NAME_DATE_TAKEN);
        mFileInfo_ColumnIndex_DATE_MODIFIED = cursor.getColumnIndex(COLUMN_NAME_DATE_MODIFIED);
        mFileInfo_ColumnIndex_EXIF_DATETIME = cursor.getColumnIndex(COLUMN_NAME_EXIF_DATETIME);
        mFileInfo_ColumnIndex_EXIF_LATITUDE = cursor.getColumnIndex(COLUMN_NAME_EXIF_LATITUDE);
        mFileInfo_ColumnIndex_EXIF_LONGITUDE = cursor.getColumnIndex(COLUMN_NAME_EXIF_LONGITUDE);
        mFileInfo_ColumnIndex_EXIF_ORIENTATION = cursor.getColumnIndex(COLUMN_NAME_EXIF_ORIENTATION);

        ChangePhotoInfo changePhotoInfo = new ChangePhotoInfo();

        try {
            cursor.moveToFirst();

            List<PhotoInfo> deletedPhotoList = changePhotoInfo.getDeleteList();
            List<PhotoInfo> modifiedPhotoList = changePhotoInfo.getModifyList();

            while(cursor.isAfterLast() == false) {
                if (mIsForceStop) {
                    changePhotoInfo.clear();
                    break;
                }

                String db_filePath = cursor.getString(mFileInfo_ColumnIndex_FILE_PATH);
                long db_fileLastModified = cursor.getLong(mFileInfo_ColumnIndex_FILE_LAST_MODIFIED);

                PhotoInfo photoInfo = photoInfoMap.get(db_filePath);
                if (photoInfo == null) {
                    //DB에 있고 현재 파일 시스템에 없는 경우 --> 삭제됨
                    //과거 정보를 읽어옴 (AI팀에서 필요하다고 함)
                    PhotoInfo deletedPhotoInfo = createDeletedPhotoInfo(cursor, db_filePath, db_fileLastModified);
                    deletedPhotoList.add(deletedPhotoInfo);
                }
                else {
                    //DB, file system 양쪽에 있으면 과거에 이미 동기를 맞춘것이다.
                    //동기를 맞춘것응 아래와 같이 map에서 제외한다.
                    //이런식으로 처리하면 결국 map에는 신규만 남게된다.
                    photoInfoMap.remove(db_filePath);

                    if (db_fileLastModified != photoInfo.getFileLastModifiedLong()) {
                        //DB에 있고 현재 파일 시스템에 있는데 수정 시간이 다른 경우 --> 수정됨
                        //수정된 경우 UUID는 변경되면 안됨
                        String db_uuid = cursor.getString(mFileInfo_ColumnIndex_UUID);
                        photoInfo.setUUID(db_uuid);
                        modifiedPhotoList.add(photoInfo);
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

        //결국 나머지는 신규이다.
        List<PhotoInfo> newPhotoList = changePhotoInfo.getNewList();
        for (Map.Entry<String, PhotoInfo> entry : photoInfoMap.entrySet()) {
            PhotoInfo photoInfo = entry.getValue();
            photoInfo.setUUID(createUUID());  //신규 파일 UUID 생성
            newPhotoList.add(photoInfo);
        }

        return changePhotoInfo;
    }

    /**
     * 현재 삭제된 사진의 정보를 DB에 기록된 마지막 정보를 이용해서 만든다.
     * @param cursor
     * @param filePath
     * @param fileLastModified
     * @return
     */
    private PhotoInfo createDeletedPhotoInfo(Cursor cursor, String filePath, long fileLastModified) {
        long imageID = cursor.getLong(mFileInfo_ColumnIndex_IMAGE_ID);
        PhotoInfo deletedPhotoInfo = new PhotoInfo(imageID, filePath, fileLastModified);
        deletedPhotoInfo.setUUID(cursor.getString(mFileInfo_ColumnIndex_UUID));
        deletedPhotoInfo.setWidth(cursor.getInt(mFileInfo_ColumnIndex_WIDTH));
        deletedPhotoInfo.setHeight(cursor.getInt(mFileInfo_ColumnIndex_HEIGHT));
        deletedPhotoInfo.setDateTakenLong(cursor.getLong(mFileInfo_ColumnIndex_DATE_TAKEN));
        deletedPhotoInfo.setDateModifedLong(cursor.getLong(mFileInfo_ColumnIndex_DATE_MODIFIED));
        deletedPhotoInfo.setExifDateTimeLong(cursor.getLong(mFileInfo_ColumnIndex_EXIF_DATETIME));
        deletedPhotoInfo.setExifLatitude(cursor.getFloat(mFileInfo_ColumnIndex_EXIF_LATITUDE));
        deletedPhotoInfo.setExifLongitude(cursor.getFloat(mFileInfo_ColumnIndex_EXIF_LONGITUDE));
        deletedPhotoInfo.setExifOrientation(cursor.getInt(mFileInfo_ColumnIndex_EXIF_ORIENTATION));
        return deletedPhotoInfo;
    }
}
