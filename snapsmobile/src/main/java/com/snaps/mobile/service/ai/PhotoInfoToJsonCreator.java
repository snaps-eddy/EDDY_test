package com.snaps.mobile.service.ai;

import android.graphics.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


class PhotoInfoToJsonCreator {
    private static final String TAG = PhotoInfoToJsonCreator.class.getSimpleName();
    private static final SimpleDateFormat sSimpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private StringBuilder mStringBuilder;
    private Date mDate;
    private Point mPoint;
    private ImageUtils mImageUtils;

    public PhotoInfoToJsonCreator() {
        mStringBuilder = new StringBuilder();
        mDate = new Date();
        mPoint = new Point();
        mImageUtils = new ImageUtils();
    }

    /**
     * 변경된 사진 정보를 이용해서 서버에 전송할 EXIF 전체 정보 JSON을 만든다.
     * @param syncType
     * @param deviceId
     * @param txId
     * @param changePhotoInfo
     * @return
     */
    public JSONObject create(
            String syncType,
            String deviceId,
            String txId,
            SyncPhotoDB.ChangePhotoInfo changePhotoInfo)
    {
        if (txId == null || txId.length() == 0) {
            return null;
        }

        if (changePhotoInfo == null) {
            return null;
        }

        JSONObject jsonObj = new JSONObject();
        try {
            int imageCnt = changePhotoInfo.getAllCount();

            jsonObj.put("appType", "android");
            jsonObj.put("txId", txId);
            jsonObj.put("deviceId", deviceId);
            jsonObj.put("imageCnt", imageCnt);

            JSONArray jsonArray = new JSONArray();
            if (syncType.equals(NetClient.EXIF_SYNC_TYPE_INIT)) {
                addPhotosExifJson(jsonArray, changePhotoInfo.getNewList(), "I");
            }
            else if (syncType.equals(NetClient.EXIF_SYNC_TYPE_UPDATE)) {
                addPhotosExifJson(jsonArray, changePhotoInfo.getNewList(), "A");
                addPhotosExifJson(jsonArray, changePhotoInfo.getModifyList(), "U");
                addPhotosExifJson(jsonArray, changePhotoInfo.getDeleteList(), "D");
            }

            jsonObj.put("images", jsonArray);
        } catch (Exception e) {
            Loggg.e(TAG, e);
            return null;
        }

        return jsonObj;
    }

    /**
     * 각 사진의 상세 정보를 만들어서 추가한다.
     * @param jsonArray
     * @param photoInfoList
     * @param status
     * @return
     * @throws JSONException
     */
    private JSONArray addPhotosExifJson(JSONArray jsonArray, List<PhotoInfo> photoInfoList, String status) throws JSONException {
        for(PhotoInfo photoInfo : photoInfoList) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("status", status);
            jsonObj.put("index", jsonArray.length());
            jsonObj.put("uuid", photoInfo.getUUID());
            jsonObj.put("imageKey", photoInfo.getID());
            jsonObj.put("imageOriFile", photoInfo.getFilePath());
            jsonObj.put("oripqW", photoInfo.getWidth());
            jsonObj.put("oripqH", photoInfo.getHeight());
            jsonObj.put("createDate", convertTimeLongToFormattedString(photoInfo.getDateTakenLong()));
            jsonObj.put("modifyDate", convertTimeLongToFormattedString(photoInfo.getDateModifedLong()));

            long exifDateTimeLong = photoInfo.getExifDateTimeLong();
            if (exifDateTimeLong != PhotoInfo.NOT_EXIST_DATE_TIME_VALUE) {
                jsonObj.put("exifDate", convertTimeLongToFormattedString(exifDateTimeLong));
            }

            String gps = convertGPStoFormattedString(photoInfo.getExifLatitude(), photoInfo.getExifLongitude());
            if (gps.length() > 0) {
                jsonObj.put("gps", gps);
            }

            int ot = photoInfo.getExifOrientation();
            if (exifDateTimeLong != PhotoInfo.NOT_EXIST_ORIENTATION_VALUE) {
                jsonObj.put("ot", ot);
            }

            Point point = mImageUtils.getResizeWidthAndHeight(
                    photoInfo.getWidth(), photoInfo.getHeight(), ImageUtils.THUMBNAIL_PIXEL_SIZE, mPoint);
            jsonObj.put("thumbW", point.x);
            jsonObj.put("thumbH", point.y);

            jsonArray.put(jsonObj);
        }

        return jsonArray;
    }

    /**
     * 시간 정보 포맷화
     * @param time
     * @return
     */
    private String convertTimeLongToFormattedString(long time) {
        mDate.setTime(time);
        return sSimpleDateFormatDate.format(mDate);
    }

    /**
     * 위치 정보 포맷화
     * @param latitude
     * @param longitude
     * @return
     */
    private String convertGPStoFormattedString(float latitude, float longitude) {
        if (latitude == PhotoInfo.NOT_EXIST_GPS_VALUE) return "";
        if (longitude == PhotoInfo.NOT_EXIST_GPS_VALUE) return "";
        mStringBuilder.setLength(0);
        mStringBuilder.append(latitude).append(",").append(longitude);
        return mStringBuilder.toString();
    }

}
