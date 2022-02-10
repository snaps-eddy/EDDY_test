package com.snaps.mobile.order.order_v2.util.thumb_image_upload;

import android.content.Context;
import android.graphics.Bitmap;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2018. 2. 27..
 */

public class SnapsThumbnailMaker extends Thread {
    private static final String TAG = SnapsThumbnailMaker.class.getSimpleName();
    public static final int THUMBNAIL_SIZE_OFFSET = 800; //px
    private static final String SNAPS_THUMBNAIL_PATH = "/snaps/thumbCache/";

    private static final long MAX_THUMB_CACHE_SIZE = 100 * 1024 * 1024; //MB
    private static final long ORGANIZED_THUMB_CASH_SIZE = 50 * 1024 * 1024; // MAX_THUMB_CACHE_SIZE를 초과하면 ORGANIZED_EFFECT_CASH_SIZE만 남겨놓고 오래된 파일 삭제

    private Context context = null;
    private MyPhotoSelectImageData imageData = null;

    public static SnapsThumbnailMaker createThumbnailMakerWithImageData(Context context, MyPhotoSelectImageData imageData) {
        return new SnapsThumbnailMaker(context, imageData);
    }

    private SnapsThumbnailMaker(Context context, MyPhotoSelectImageData imageData) {
        this.setDaemon(true);
        this.context = context;
        this.imageData = imageData;
    }

    @Override
    public void run() {
        super.run();

        try {
            File cacheFile = createThumbnailCacheWithImageData(context, imageData);
            if (cacheFile != null && cacheFile.exists() && !BitmapUtil.isValidThumbnailImage(cacheFile.getAbsolutePath())) {
                FileUtil.deleteFile(cacheFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static File getThumbnailCacheFileWithImageData(Context context, MyPhotoSelectImageData imageData) throws Exception {
        File orgFile = new File(imageData.PATH);
        if (!orgFile.exists()) return null;

//
        String imageName = imageData.F_IMG_NAME;
        String ext = ".jpg";
        if (imageName != null && imageName.contains(".")) {
            ext = imageName.substring(imageName.lastIndexOf(".")-1);
            imageName = imageName.substring(0, imageName.lastIndexOf("."));
        }

        final int LIMIT_FILE_NAME_LENGTH = 50;
        if (imageName != null && imageName.length() > LIMIT_FILE_NAME_LENGTH) {
            imageName = imageName.substring(0, LIMIT_FILE_NAME_LENGTH);
        }

        String cacheFileName = "thumbCache_" + Config.getAPP_VERSION() + "_" + imageName +  "_" + orgFile.lastModified() + ext;

        File cacheFile = new File(Config.getExternalCacheDir(context), SNAPS_THUMBNAIL_PATH);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }

        cacheFile = new File(Config.getExternalCacheDir(context), SNAPS_THUMBNAIL_PATH + cacheFileName);
        return cacheFile;
    }

    private static Bitmap getResizedBitmapByThumbnailSizeOffset(Bitmap imageBitmap) throws Exception {
        if (imageBitmap != null && (imageBitmap.getWidth() > THUMBNAIL_SIZE_OFFSET || imageBitmap.getHeight() > THUMBNAIL_SIZE_OFFSET)) {
            int bitmapWidth = imageBitmap.getWidth();
            int bitmapHeight = imageBitmap.getHeight();
            int resizeWidth = 0, resizeHeight = 0;
            float ratio = 1.f;

            if (bitmapWidth > bitmapHeight) {
                ratio = THUMBNAIL_SIZE_OFFSET / (float) bitmapWidth;
                resizeWidth = THUMBNAIL_SIZE_OFFSET;
                resizeHeight = (int) (bitmapHeight * ratio);
            } else if (bitmapWidth < bitmapHeight) {
                ratio = THUMBNAIL_SIZE_OFFSET / (float) bitmapHeight;
                resizeHeight = THUMBNAIL_SIZE_OFFSET;
                resizeWidth = (int) (bitmapWidth * ratio);
            } else {
                resizeWidth = THUMBNAIL_SIZE_OFFSET;
                resizeHeight = THUMBNAIL_SIZE_OFFSET;
            }

//            Bitmap resizedBitmap = CropUtil.getInSampledScaleBitmap(imageBitmap, resizeWidth, resizeHeight);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, resizeWidth, resizeHeight, false);

            if (resizedBitmap != null && imageBitmap != resizedBitmap && !imageBitmap.isRecycled()) {
                imageBitmap.recycle();
                return resizedBitmap;
            }
        }

        return imageBitmap;
    }

    private static Bitmap createThumbnailBitmapWithOrgFilePath(String orgFilePath) throws Exception {
        Bitmap imageBitmap = null;
        try {
            imageBitmap = CropUtil.getBitmapFromStreamSafetyOOM(orgFilePath);
            if (BitmapUtil.isUseAbleBitmap(imageBitmap))
                return getResizedBitmapByThumbnailSizeOffset(imageBitmap);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }

        return imageBitmap;
    }

    public static File createThumbnailCacheWithImageData(Context context, MyPhotoSelectImageData imageData) throws Exception {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()
                || imageData == null || imageData.KIND != Const_VALUES.SELECT_PHONE
                || context == null) return null;

        if (isExistThumbnailCache(context, imageData)) return getThumbnailCacheFileWithImageData(context, imageData);

        deleteOldCacheIfOverLimitCacheSize(context);

        File cacheFile = getThumbnailCacheFileWithImageData(context, imageData);
        if (cacheFile == null) return null;

        synchronized (cacheFile) {
            String savePath = cacheFile.getAbsolutePath();

            Bitmap thumbnailBitmap = createThumbnailBitmapWithOrgFilePath(imageData.PATH);
            if (!BitmapUtil.isUseAbleBitmap(thumbnailBitmap)) return null;

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(savePath);
                cacheFile.createNewFile();
                cacheFile.setWritable(true);
                cacheFile.setReadable(true);

                if (thumbnailBitmap.hasAlpha())
                    thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                else
                    thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                Dlog.d("createThumbnailCacheWithImageData() thumbnail cache made:" + savePath);
            } catch (OutOfMemoryError e) {
                Dlog.e(TAG, e);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            } finally {
                if(fos != null)
                    fos.close();

                if (thumbnailBitmap != null && !thumbnailBitmap.isRecycled()) {
                    thumbnailBitmap.recycle();
                    thumbnailBitmap = null;
                }
            }

            return cacheFile;
        }
    }

    private static void deleteOldCacheIfOverLimitCacheSize(Context context) {
        try {
            File file = getThumbnailCacheDirectoryWithImageData(context);
            if (file.isDirectory()) {
                if (getDirectorySize(file) > MAX_THUMB_CACHE_SIZE) {
                    deleteEffectCashDirectory(file);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private synchronized static void deleteEffectCashDirectory(File directoryOrFile) throws Exception {
        if (directoryOrFile == null)
            return;

        File[] files = directoryOrFile.listFiles();
        if (files == null) return;

        Arrays.sort(files, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }

        });

        for (int ii = files.length - 1; ii >= 0; ii--) {
            if (getDirectorySize(directoryOrFile) < ORGANIZED_THUMB_CASH_SIZE)
                break;
            files[ii].delete();
        }
    }

    private static long getDirectorySize(File directory) {
        long length = 0;
        if (directory == null) return length;
        File[] files = directory.listFiles();
        if (files == null) return length;

        for (File file : files) {
            if (file.isFile())
                length += file.length();
            else
                length += getDirectorySize(file);
        }
        return length;
    }

    private static boolean isExistThumbnailCache(Context context, MyPhotoSelectImageData imageData) throws Exception {
        File cacheFile = getThumbnailCacheFileWithImageData(context, imageData);
        if (cacheFile == null || !cacheFile.exists()) return false;

        if (cacheFile.length() < 1 || !BitmapUtil.isValidThumbnailImage(cacheFile.getAbsolutePath())) {
            cacheFile.delete();
            return false;
        }

        return true;
    }

    private static File getThumbnailCacheDirectoryWithImageData(Context context) {
        File cacheFile = new File(Config.getExternalCacheDir(context), SNAPS_THUMBNAIL_PATH);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile;
    }
}
