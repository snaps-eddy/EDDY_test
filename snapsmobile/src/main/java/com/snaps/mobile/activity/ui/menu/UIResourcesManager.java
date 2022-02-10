package com.snaps.mobile.activity.ui.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.data.img.ExifUtil;
import com.snaps.common.push.GCMContent;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.file.FlushedInputStream;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.json.SnapsPriceDetailResponse;
import com.snaps.mobile.activity.ui.menu.json.SnapsStoreProductResponse;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UIResourcesManager {
    private static final String TAG = UIResourcesManager.class.getSimpleName();

    private static String CACHE_PATH = "/snaps/main_ui/";
    //	private static String ASSET_MAIN_MENU_DIRECTORY = "drawable/main_menu/";
    private static String ASSET_SALE_DIRECTORY = "drawable/sale/";
    private static String ASSET_SALE_UPTO_DIRECTORY = "drawable/sale_upto/";
    private static String ASSET_SALE_IMG_DEFAULT_NAME = "ico_sale_";
    private static String ASSET_SALE_IMG_UPTO_NAME = "upto";

    private static int gDefaultCacheSize = 1000;

    public static void setMainStoreBG(final Context context, final ImageView imgView, final SnapsStoreProductResponse menuInfo) throws Exception {
        if (context == null || imgView == null || menuInfo == null) return;

        gDefaultCacheSize = UIUtil.getScreenWidth(context);

        SnapsImgResourceInfo imgRes = new SnapsImgResourceInfo();
        imgRes.setProductInfo(menuInfo);

        //우선은 파일 폴더를 바라본다.
        if (isExistCache(context, menuInfo.getImgUrl())) {
            imgRes.setLocal(true);
            setImageViewResourceFromUri(context, imgRes, imgView);
        } else {

            imgView.setImageResource(R.drawable.img_main_store_default_bg);

            //없으면 ImageLoader로 바로 로딩.
            imgRes.setLocal(false);
            setImageViewResourceFromUri(context, imgRes, imgView);

            //캐싱(일부러 Loader에서는 파일 캐싱은 생략한다.)
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverUrl = SnapsAPI.DOMAIN() + menuInfo.getImgUrl();
                        String cachePath = getCacheFileAbsolutePath(context, menuInfo.getImgUrl());
                        downloadBitmapAndMakeCache(serverUrl, cachePath);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            });
            thread.start();
        }
    }

    public static void setSaleImage(Context context, final ImageView imgView, final SnapsPriceDetailResponse priceInfo) {
        if (context == null || imgView == null || priceInfo == null) return;
        try {
            String path = getPriceImgAssetFullPath(priceInfo);
            InputStream is = context.getAssets().open(path);
            Drawable drawable = Drawable.createFromStream(is, null);
            imgView.setImageDrawable(drawable);
        } catch (IOException ex) {
            Dlog.e(TAG, ex);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        } catch (Exception e2) {
            Dlog.e(TAG, e2);
        }
    }

    public static void deleteAllCacheFile(Context context) {
        String path = getMainUICacheImgFilePath(context);
        File directory = new File(path);
        FileUtil.deleteFolderInFiles(directory);
    }

    public static String getMainUICacheImgFilePath(Context context) {
        try {
            String filePath = Config.getExternalCacheDir(context) + CACHE_PATH;

            File directory = new File(filePath);

            if (!directory.exists())
                directory.mkdirs();

            return filePath;
        } catch (Exception ex) {
            Dlog.e(TAG, ex);
        }

        return "";
    }

    public static boolean isExistCache(Context context, String url) {
        if (url == null || url.length() < 1 || !url.contains("/")) return false;

        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String localUri = getMainUICacheImgFilePath(context) + fileName;

        File file = new File(localUri);
        if (file.exists() && file.length() < 1) {
            file.delete();
            return false;
        }

        return file.exists();
    }

    public static String getCacheFileName(String url) {
        if (url == null || url.length() < 1 || !url.contains("/")) return "";
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static String getCacheFileAbsolutePath(Context context, String url) {
        if (url == null || url.length() < 1 || !url.contains("/")) return "";
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        return getMainUICacheImgFilePath(context) + fileName;
    }

    public static boolean isUpatedResourceVersion(Context context, String resVer) {
        if (resVer == null || resVer.equals("")) return false;

        String storageVersion = Setting.getString(context, Const_VALUE.KEY_UI_MENU_RES_VERSION);

        if (storageVersion == null || storageVersion.equals("")) {
            storageVersion = Config.ASSET_MAIN_UI_RESOURCE_VERSION;
            Setting.set(context, Const_VALUE.KEY_UI_MENU_RES_VERSION, storageVersion);
        }

        if (!storageVersion.equals(resVer)) {
            return true;
        }

        return false;
    }

    private static String getPriceImgAssetFullPath(SnapsPriceDetailResponse priceInfo) {
        if (priceInfo == null) return null;
        if (priceInfo.isUptoSaleImg()) {
            return ASSET_SALE_UPTO_DIRECTORY + ASSET_SALE_IMG_DEFAULT_NAME + ASSET_SALE_IMG_UPTO_NAME + priceInfo.getSaleImg() + ".png";
        } else {
            return ASSET_SALE_DIRECTORY + ASSET_SALE_IMG_DEFAULT_NAME + priceInfo.getSaleImg() + ".png";
        }
    }

    private static boolean downloadBitmapAndMakeCache(final String url, final String outputPath) {
        Bitmap bmp = null;
        try {
            bmp = downloadBitmapFromUrl(url);
            if (bmp != null && !bmp.isRecycled())
                BitmapUtil.saveImgFile(outputPath, bmp);
            File file = new File(outputPath);
            return file.exists() && file.length() > 0;
        } catch (IOException e) {
            Dlog.e(TAG, e);
        } catch (OutOfMemoryError e2) {
            Dlog.e(TAG, e2);
        } catch (Exception e3) {
            Dlog.e(TAG, e3);
        }
        return false;
    }

//	SnapsImageLoader imageLoader = SnapsImageLoader.getInstance();
//
//		imageLoader.setDiscCache(context, gDefaultCacheSize, gDefaultCacheSize);
//
//	DisplayImageOptions options = new DisplayImageOptions.Builder()
//			.resetViewBeforeLoading(false)
//			.cacheInMemory(false)
//			.cacheOnDisk(false)
//			.considerExifParams(false)
//			.imageScaleType(ImageScaleType.EXACTLY)
//			.bitmapConfig(SnapsImageLoader.DEFAULT_BITMAP_CONFIG)
//			.build();


    private static void setImageViewResourceFromUri(final Context context, SnapsImgResourceInfo imgResourceInfo, final ImageView imgView) {
        if (context == null || imgResourceInfo == null || imgView == null) return;

        String uri = "";
        final SnapsStoreProductResponse productInfo = imgResourceInfo.getProductInfo();
        if (productInfo != null) {
            if (imgResourceInfo.isLocal()) {
                uri = /*"file://" + */getCacheFileAbsolutePath(context, productInfo.getImgUrl());
            } else {
                uri = SnapsAPI.DOMAIN() + productInfo.getImgUrl();
            }

            final String URL = uri;
            SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(context, imgView) {
                @Override
                public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    super.onResourceReady(resource, transition);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    if (imgView != null)
                        imgView.setImageResource(R.drawable.img_main_store_default_bg);
                }
            };

            ImageLoader.asyncDisplayImage(context, URL, bitmapImageViewTarget);

        }
    }

    private static Bitmap downloadBitmapFromUrl(String imgUrl) throws IOException {
        Bitmap imgBitmap = null;
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        FlushedInputStream fis = new FlushedInputStream(conn.getInputStream());
        final BitmapFactory.Options option = new Options();

        try {
            imgBitmap = BitmapFactory.decodeStream(fis, new Rect(), option);
        } catch (OutOfMemoryError e) {
            return null;
        } finally {
            if (conn != null)
                conn.disconnect();

            if (fis != null)
                fis.close();
        }
        return imgBitmap;
    }

    public static class SnapsImgResourceInfo {
        private boolean isLocal = false;
        private SnapsStoreProductResponse productInfo = null;

        public boolean isLocal() {
            return isLocal;
        }

        public void setLocal(boolean isLocal) {
            this.isLocal = isLocal;
        }

        public SnapsStoreProductResponse getProductInfo() {
            return productInfo;
        }

        public void setProductInfo(SnapsStoreProductResponse productInfo) {
            this.productInfo = productInfo;
        }
    }

    public static Bitmap makeBitmapPushContent(GCMContent content) {
        if (content.getBigImgPath() == null || content.getBigImgPath().length() < 1) {
            return null;
        }

        Bitmap imgBitmap = null;
        FlushedInputStream fis = null;
        try {
            URL url = new URL(content.getBigImgPath());
            URLConnection conn = url.openConnection();
            conn.connect();
            fis = new FlushedInputStream(conn.getInputStream());
            BufferedInputStream bis = new BufferedInputStream(fis);

            imgBitmap = BitmapFactory.decodeStream(bis);
            bis.close();

            int imageOt = ExifUtil.parseOrientationToDegree(content.getImgOt());
            if (imageOt != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate((float) imageOt);
                imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);

        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }

        return imgBitmap;
    }
}
