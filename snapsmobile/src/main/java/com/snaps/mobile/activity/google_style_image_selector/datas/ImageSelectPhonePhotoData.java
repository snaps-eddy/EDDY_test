package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.media.MediaImage;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.StoryBookStringUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ysjeong on 2017. 1. 3..
 */

public class ImageSelectPhonePhotoData {
    private static final String TAG = ImageSelectPhonePhotoData.class.getSimpleName();
    private ArrayList<IAlbumData> arrCursor = null;
    private Map<String, ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem>> mapPhonePhotoCursors = null;
    private Context context = null;
    private int heifImageCount;

    public ImageSelectPhonePhotoData(Context context) {
        this.context = context;
        heifImageCount = 0;
    }

    public int getHeifImageCount() {
        return heifImageCount;
    }

    public boolean isExistPhotoPhotoData() {
        return arrCursor != null && !arrCursor.isEmpty();
    }

    private boolean isMeasuredImagesDimension = false;

    public void releaseInstace() {
        if (mapPhonePhotoCursors != null) {
            mapPhonePhotoCursors.clear();
            mapPhonePhotoCursors = null;
        }

        if (arrCursor != null) {
            arrCursor.clear();
            arrCursor = null;
        }

        isMeasuredImagesDimension = false;

        context = null;
    }

    public void createAlbumDatas() {
        if (context == null) return;

        arrCursor = new ArrayList<>();
        final HashMap<Integer, GalleryCursorRecord.GalleryAlbumCursor> output = new HashMap<>();
        Cursor cursor = MediaImage.getBucketList(context);

        try {
            if (cursor.moveToFirst()) {
                do {
                    int phoneFolderId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));

                    GalleryCursorRecord.GalleryAlbumCursor cur = new GalleryCursorRecord.GalleryAlbumCursor();
                    if (!output.containsKey(phoneFolderId)) {
                        int imageID = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        String phoneFolderImgPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
                        String phoneFolderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                        int phoneDetailOrientation = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
                        cur.set(phoneFolderId, imageID, phoneFolderName, phoneDetailOrientation, 1, phoneFolderImgPath);
                        arrCursor.add(cur);
                        output.put(phoneFolderId, cur);
                    } else {
                        output.get(phoneFolderId).setPhoneFolderImgs(output.get(phoneFolderId).getPhoneFolderImgs() + 1);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public ArrayList<IAlbumData> getArrCursor() {
        return arrCursor;
    }

    public ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> getPhotoListByAlbumId(String id) {
        if (mapPhonePhotoCursors == null || !mapPhonePhotoCursors.containsKey(id)) return null;
        return mapPhonePhotoCursors.get(id);
    }

    //단말기내 사진을 mapPhonePhotoCursors에 모두 담는다.
    public boolean createAllPhotoDataOfCellPhone(Activity activity) {
        createAlbumDatas();

        if (arrCursor == null) return false;

        if (mapPhonePhotoCursors != null) return false;

        mapPhonePhotoCursors = new HashMap<>();

        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> allPhonePhotoList = new ArrayList<>();

        for (IAlbumData albumData : arrCursor) {
            if (albumData == null) continue;

            String albumId = albumData.getAlbumId();
            Cursor cursor = MediaImage.getBucketDetail(context, albumId);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoInAlbumList = new ArrayList<>();
                    do {
                        String phoneDetailImgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        String phoneThumbnailImgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));

                        long phoneDetailId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        String phoneDetailName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        int phoneDetailOrientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
                        long dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

                        dateTaken = StringUtil.fixValidTakenTime(dateTaken);

                        int outWidth = 0;
                        int outHeight = 0;

                        if (isSupportReadMediaDimensionInfo()) {
                            outWidth = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                            outHeight = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                        } else {
                            int[] bitmapSize = CropUtil.getBitmapFilesLength(phoneDetailImgPath);
                            if (bitmapSize != null && bitmapSize.length > 1) {
                                outWidth = bitmapSize[0];
                                outHeight = bitmapSize[1];
                            }
                        }

                        ImageSelectPhonePhotoInfo thumbnailPaths = new ImageSelectPhonePhotoInfo();
                        thumbnailPaths.setOrgImgPath(phoneDetailImgPath);
                        thumbnailPaths.setThumbnailPath(phoneThumbnailImgPath);
                        thumbnailPaths.setTakenTime(dateTaken);

                        thumbnailPaths.setDateByTakenTime();

                        GalleryCursorRecord.PhonePhotoFragmentItem photoItem = new GalleryCursorRecord.PhonePhotoFragmentItem();
                        photoItem.set(phoneDetailId, thumbnailPaths, phoneDetailName, phoneDetailOrientation);

                        photoItem.setImageDimension(outWidth, outHeight);
                        photoInAlbumList.add(photoItem);

                        if(Config.getAI_IS_SELFAI() && !Config.getAI_SELFAI_EDITTING()) {
                            if(((ImageSelectActivityV2)activity).isSelfAIImage(phoneDetailImgPath))
                                allPhonePhotoList.add(photoItem);
                        } else {
                            allPhonePhotoList.add(photoItem);
                        }
                    } while (cursor.moveToNext());
                    mapPhonePhotoCursors.put(albumId, photoInAlbumList);
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }

        //날짜 순 정렬
        sortPhotoListByTakenDate(allPhonePhotoList);

        mapPhonePhotoCursors.put(String.valueOf(ISnapsImageSelectConstants.PHONE_ALL_PHOTO_CURSOR_ID), allPhonePhotoList);

        return true;
    }

    public void createAllPhotoDataOfCellPhone2(ArrayList<IAlbumData> list, Activity activity) {
        if (list == null) return;

        if (mapPhonePhotoCursors != null) return;

        mapPhonePhotoCursors = new HashMap<>();

        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> allPhonePhotoList = new ArrayList<>();

        boolean isSupportReadMediaDimensionInfo = isSupportReadMediaDimensionInfo();

        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 1, 1,0,0,0);
        long minValidDateLong = calendar.getTimeInMillis(); //그냥 0대입 하면 되는데 혹시 변경에 대비

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 60);
        long maxValidDateLong = calendar.getTimeInMillis();

        boolean isEnglish = !Config.useKorean();
        boolean isSelfAIAndNotEditting = Config.getAI_IS_SELFAI() && !Config.getAI_SELFAI_EDITTING();

        //속도 향상을 위해 요일 문자열을 만들어둔다.
        Map<Integer, String> dayOfWeekTextMap = new HashMap<Integer, String>();
        dayOfWeekTextMap.put(Calendar.SUNDAY, StoryBookStringUtil.getDayOfWeekString(Calendar.SUNDAY, isEnglish));
        dayOfWeekTextMap.put(Calendar.MONDAY, StoryBookStringUtil.getDayOfWeekString(Calendar.MONDAY, isEnglish));
        dayOfWeekTextMap.put(Calendar.TUESDAY, StoryBookStringUtil.getDayOfWeekString(Calendar.TUESDAY, isEnglish));
        dayOfWeekTextMap.put(Calendar.WEDNESDAY, StoryBookStringUtil.getDayOfWeekString(Calendar.WEDNESDAY, isEnglish));
        dayOfWeekTextMap.put(Calendar.THURSDAY, StoryBookStringUtil.getDayOfWeekString(Calendar.THURSDAY, isEnglish));
        dayOfWeekTextMap.put(Calendar.FRIDAY, StoryBookStringUtil.getDayOfWeekString(Calendar.FRIDAY, isEnglish));
        dayOfWeekTextMap.put(Calendar.SATURDAY, StoryBookStringUtil.getDayOfWeekString(Calendar.SATURDAY, isEnglish));

        for (IAlbumData albumData : list) {
            if (albumData == null) continue;

            String albumId = albumData.getAlbumId();
            mapPhonePhotoCursors.put(albumId, new ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem>());
        }

        int columnIndex_Media_BUCKET_ID;
        int columnIndex_Media_DATA;
        int columnIndex_Media_ID;
        int columnIndex_Media_DISPLAY_NAME;
        int columnIndex_Media_ORIENTATION;
        int columnIndex_Media_DATE_TAKEN;
        int columnIndex_Media_DATE_MODIFIED;
        int columnIndex_Media_DATE_WIDTH;
        int columnIndex_Media_DATE_HEIGHT;

        Cursor cursor = null;
        try {
            cursor = MediaImage.getBucketDetail2(context);
            if (cursor != null && cursor.moveToFirst()) {
                columnIndex_Media_BUCKET_ID = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                columnIndex_Media_DATA = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                columnIndex_Media_ID = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                columnIndex_Media_DISPLAY_NAME = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                columnIndex_Media_ORIENTATION = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
                columnIndex_Media_DATE_TAKEN = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                columnIndex_Media_DATE_MODIFIED = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
                columnIndex_Media_DATE_WIDTH = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH);
                columnIndex_Media_DATE_HEIGHT = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT);

                do {
                    String phoneDetailImgPath = cursor.getString(columnIndex_Media_DATA);
                    String lowerCaseImgPath = phoneDetailImgPath.toLowerCase();
                    boolean isSupportFormat = (lowerCaseImgPath.endsWith(".jpg") || lowerCaseImgPath.endsWith(".jpeg")
                            || lowerCaseImgPath.endsWith(".png"));
                    if (isSupportFormat == false) {
                        //HEIF 파일 존재 유무 확인
                        if (lowerCaseImgPath.endsWith(".heic")) {
                            Dlog.i(TAG, "HEIF (.heic) file exists : " + phoneDetailImgPath);
                            heifImageCount++;
                        }
                        continue;
                    }

                    String phoneFolderId = String.valueOf(cursor.getInt(columnIndex_Media_BUCKET_ID));
                    String phoneThumbnailImgPath = phoneDetailImgPath;
                    long phoneDetailId = cursor.getLong(columnIndex_Media_ID);
                    String phoneDetailName = cursor.getString(columnIndex_Media_DISPLAY_NAME);
                    int phoneDetailOrientation = cursor.getInt(columnIndex_Media_ORIENTATION);

                    long dateTaken = cursor.getLong(columnIndex_Media_DATE_TAKEN);
                    boolean isValidDateTaken = (minValidDateLong <= dateTaken && dateTaken <= maxValidDateLong);
                    if (isValidDateTaken == false) {
                        long dateModified = cursor.getLong(columnIndex_Media_DATE_MODIFIED);
                        dateTaken = fixInvalidDateTaken(minValidDateLong, maxValidDateLong, dateTaken, dateModified, phoneDetailImgPath);
                    }

                    int outWidth = 0;
                    int outHeight = 0;
                    if (isSupportReadMediaDimensionInfo) {
                        outWidth = cursor.getInt(columnIndex_Media_DATE_WIDTH);
                        outHeight = cursor.getInt(columnIndex_Media_DATE_HEIGHT);
                    } else {
                        int[] bitmapSize = CropUtil.getBitmapFilesLength(phoneDetailImgPath);
                        if (bitmapSize != null && bitmapSize.length > 1) {
                            outWidth = bitmapSize[0];
                            outHeight = bitmapSize[1];
                        }
                    }

                    calendar.setTimeInMillis(dateTaken);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    String dayOfWeekText = dayOfWeekTextMap.get(dayOfWeek);

                    ImageSelectPhonePhotoInfo thumbnailPaths = new ImageSelectPhonePhotoInfo();
                    thumbnailPaths.setOrgImgPath(phoneDetailImgPath);
                    thumbnailPaths.setThumbnailPath(phoneThumbnailImgPath);
                    thumbnailPaths.setTakenTime(dateTaken);
                    thumbnailPaths.setYear(year);
                    thumbnailPaths.setMonth(month);
                    thumbnailPaths.setDay(day);
                    thumbnailPaths.setDayOfWeek(dayOfWeekText);

                    GalleryCursorRecord.PhonePhotoFragmentItem photoItem = new GalleryCursorRecord.PhonePhotoFragmentItem();
                    photoItem.set(phoneDetailId, thumbnailPaths, phoneDetailName, phoneDetailOrientation);

                    photoItem.setImageDimension(outWidth, outHeight);

                    List<GalleryCursorRecord.PhonePhotoFragmentItem> photoItemList = mapPhonePhotoCursors.get(phoneFolderId);
                    if (photoItemList == null) continue;
                    photoItemList.add(photoItem);
                    if (isSelfAIAndNotEditting) {
                        if(((ImageSelectActivityV2)activity).isSelfAIImage(phoneDetailImgPath))
                            allPhonePhotoList.add(photoItem);
                    } else {
                        allPhonePhotoList.add(photoItem);
                    }

                } while(cursor.moveToNext());
            }
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }finally {
            if (cursor != null) {
                try {
                    cursor.close();
                }catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }

        mapPhonePhotoCursors.put(String.valueOf(ISnapsImageSelectConstants.PHONE_ALL_PHOTO_CURSOR_ID), allPhonePhotoList);

        //날짜 순 정렬
        for( Map.Entry<String, ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem>> elem : mapPhonePhotoCursors.entrySet() ){
            sortPhotoListByTakenDate(elem.getValue());
        }
    }

    //단말기 내에 모든 사진을 담고 있는 앨범도 만든다.
    public void createAllPhotoCotainedAlbum() {
        ArrayList<IAlbumData> cursorList = getArrCursor();
        if (cursorList == null || context == null) return;

        GalleryCursorRecord.GalleryAlbumCursor allPhonePhotoCusor = new GalleryCursorRecord.GalleryAlbumCursor();
        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> arrAllPhonePhotoCusorList = getPhotoListByAlbumId(String.valueOf(ISnapsImageSelectConstants.PHONE_ALL_PHOTO_CURSOR_ID));
        if (arrAllPhonePhotoCusorList != null && !arrAllPhonePhotoCusorList.isEmpty()) {
            ImageSelectPhonePhotoInfo thumbnailInfo = arrAllPhonePhotoCusorList.get(0).getPhotoInfo();
            String thumbnailPath = thumbnailInfo != null ? thumbnailInfo.getThumbnailPath() : "";
            allPhonePhotoCusor.set(ISnapsImageSelectConstants.PHONE_ALL_PHOTO_CURSOR_ID, 0, context.getString(R.string.phone_all_photos), 0, arrAllPhonePhotoCusorList.size(), thumbnailPath);
            cursorList.add(0, allPhonePhotoCusor);
        }
    }

    private long fixInvalidDateTaken(long minValidDateLong, long maxValidDateLong, long dateTaken, long dateModified, String imgPath) {
        //sns 사진 중 sec으로 기록 되어 있는 날짜가 있다.
        long fixDate = dateTaken * 1000;
        if (minValidDateLong <= fixDate && fixDate <= maxValidDateLong) {
            return fixDate;
        }

        if (minValidDateLong <= dateModified && dateModified <= maxValidDateLong) {
            return dateModified;
        }

        fixDate = dateModified * 1000;
        if (minValidDateLong <= fixDate && fixDate <= maxValidDateLong) {
            return fixDate;
        }

        //DATE_TAKEN이 없으면 파일 저장된 날짜
        try {
            File file = new File(imgPath);
            if (file.isFile()) {
                return file.lastModified();
            }
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return 0;
    }


    private void sortPhotoListByTakenDate(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> allPhotoList) {
        if (allPhotoList == null) return;

        Collections.sort(allPhotoList, new Comparator<GalleryCursorRecord.PhonePhotoFragmentItem>() {
            @Override
            public int compare(GalleryCursorRecord.PhonePhotoFragmentItem prev,
                               GalleryCursorRecord.PhonePhotoFragmentItem cur) {
                if (prev == null || cur == null) return 0;

                long prev_time = prev.getPhotoTakenTime();
                long cur_time = cur.getPhotoTakenTime();
                return prev_time < cur_time ? 1 : (prev_time > cur_time ? -1 : 0);
            }
        });
     }

    private boolean isSupportReadMediaDimensionInfo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
