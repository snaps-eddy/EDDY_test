package com.snaps.common.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.recoders.EffectFilerMaker;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.interfaces.ISnapsControl;

import java.io.File;
import java.util.ArrayList;

public class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();

    public static Bitmap applyShadowOnImage(Bitmap src) {
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + 30, src.getHeight() + 30, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOut);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        Paint ptBlur = new Paint();
//		ptBlur.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];
        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        //Bitmap bmAlpha = Bitmap.createBitmap(src.getUserSelectWidth() +10 , src.getHeight() +10, Bitmap.Config.ARGB_8888);
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setShadowLayer(5.0f, 3.0f, 3.0f, Color.parseColor("#33000000"));
        canvas.drawBitmap(bmAlpha, (bmOut.getWidth() / 2) - (bmAlpha.getWidth() / 2) + offsetXY[0], (bmOut.getWidth() / 2) - (bmAlpha.getWidth() / 2) + offsetXY[1], ptAlphaColor);
        bmAlpha.recycle();
        canvas.drawBitmap(src, (bmOut.getWidth() / 2) - (src.getWidth() / 2), (bmOut.getHeight() / 2) - src.getHeight() / 2, null);
        return bmOut;
    }

    public static Drawable getNinePatchDrawableFromResourceId(int id, Context context) throws OutOfMemoryError {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        byte[] chunk = bitmap.getNinePatchChunk();
        if (NinePatch.isNinePatchChunk(chunk)) {
            return new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
        } else {
            return new BitmapDrawable(bitmap);
        }
    }

    public static void recycleBitmap(ImageView iv) {
        try {
            if (iv != null) {
                iv.setImageBitmap(null);

                if (iv instanceof ISnapsControl) { //Glide??? ??????????????? ??????
                    return;
                }

                Drawable d = iv.getDrawable();
                if (d != null) {
                    d.setCallback(null);
                }

                if (d != null && d instanceof BitmapDrawable) {
                    Bitmap b = ((BitmapDrawable) d).getBitmap();
                    if (b != null && !b.isRecycled()) {
                        b.recycle();
                    }
                } // ??????????????? BitmapDrawable ????????? drawable ?????? ?????? ???????????? ????????? ????????? ???????????????.
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * ??????????????? Page??? Control imageData ????????? ?????? ??? ????????????. (????????? true)
     */
    public static boolean isExistLayerImageCache(Context context, ArrayList<SnapsControl> layerControls) {
        if (context == null || layerControls == null) {
            return false;
        }

        try {
            for (SnapsControl layer : layerControls) {
                if (!(layer instanceof SnapsLayoutControl)) {
                    continue;
                }

                MyPhotoSelectImageData imageData = ((SnapsLayoutControl) layer).imgData;
                if (imageData == null) {
                    continue;
                }

                String uri = ImageUtil.getImagePath(context, imageData);
                if (!isExistImageLoaderCache(context, uri)) {
                    return false;
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return true;
    }

    private static boolean isExistImageLoaderCache(Context context, String cachePath) {
        File cacheFile = Glide.getPhotoCacheDir(context, cachePath);
        return cacheFile != null && cacheFile.exists();
    }

    public static String getImagePathForSnapsDiary(Context context, MyPhotoSelectImageData data) {
        String baseImgPath = data.PATH != null ? data.PATH : data.ORIGINAL_PATH;

        if (data.isApplyEffect) {
            String path = data.EFFECT_PATH;
            if ((new File(path).exists())) {
                return path;
            } else {
                // ???????????? ????????? ?????? ?????? ?????????, ????????? ???????????? ?????? ??????.
                if (Config.isSNSPhoto(data.KIND)) {
                    baseImgPath = data.ORIGINAL_PATH;
                } else {
                    if (!StringUtil.isEmpty(data.getSafetyThumbnailPath())) {
                        baseImgPath = SnapsAPI.DOMAIN(false) + data.getSafetyThumbnailPath();
                    } else if (data.ORIGINAL_PATH != null && data.ORIGINAL_PATH.length() > 0) {
                        baseImgPath = SnapsAPI.DOMAIN(false) + data.ORIGINAL_PATH;
                    }
                }

                data.ADJ_CROP_INFO.setShouldCreateFilter(!data.isTriedRecoveryEffectFilterFile());
                data.ADJ_CROP_INFO.setEffectFilerMaker(getEffectFilterMaker(context, data));
                return baseImgPath;
            }
        } else {
            boolean isWebUrl = baseImgPath != null && baseImgPath.startsWith("http");

            if (!isWebUrl) {
                if (baseImgPath != null && new File(baseImgPath).exists()) {
                    data.KIND = Const_VALUES.SELECT_PHONE;
                } else {
                    if (Config.isSNSPhoto(data.KIND)) {
                        baseImgPath = data.ORIGINAL_PATH;
                    } else {
                        if (!StringUtil.isEmpty(data.getSafetyThumbnailPath())) {
                            if (!data.getSafetyThumbnailPath().startsWith("http")) {
                                baseImgPath = SnapsAPI.DOMAIN(false) + data.getSafetyThumbnailPath();
                            } else {
                                baseImgPath = data.getSafetyThumbnailPath();
                            }
                        } else if (data.ORIGINAL_PATH != null && data.ORIGINAL_PATH.length() > 0 && !data.ORIGINAL_PATH.contains("/snaps/effect")) {
                            baseImgPath = SnapsAPI.DOMAIN(false) + data.ORIGINAL_PATH;
                        }
                    }
                }
            } else {
                if (Config.isSNSPhoto(data.KIND)) {
                    baseImgPath = data.PATH;
                    if (Const_PRODUCT.isSNSBook()) {

                        String thumb = data.ORIGINAL_PATH;
                        if (thumb != null && thumb.startsWith("http")) {
                            baseImgPath = data.ORIGINAL_PATH;
                        }
                    }
                } else {
                    if (!StringUtil.isEmpty(data.getSafetyThumbnailPath())) {
                        if (!data.getSafetyThumbnailPath().startsWith("http")) {
                            baseImgPath = SnapsAPI.DOMAIN(false) + data.getSafetyThumbnailPath();
                        } else {
                            baseImgPath = data.getSafetyThumbnailPath();
                        }
                    } else if (data.ORIGINAL_PATH != null && data.ORIGINAL_PATH.length() > 0 && !data.ORIGINAL_PATH.contains("/snaps/effect")) {
                        if (!data.ORIGINAL_PATH.startsWith("http")) {
                            baseImgPath = SnapsAPI.DOMAIN(false) + data.PATH;
                        } else {
                            baseImgPath = data.PATH;
                        }
                    }
                }
            }
        }

        return baseImgPath;
    }

    private static EffectFilerMaker getEffectFilterMaker(Context context, MyPhotoSelectImageData data) {
        if (context == null || data == null) {
            return null;
        }
        EffectFilerMaker filerMaker = new EffectFilerMaker();
        filerMaker.setContext(context);
        filerMaker.setImageData(data);
        return filerMaker;
    }

    public static String getImagePath(Context context, MyPhotoSelectImageData data) {
        if (data == null) {
            return "";
        }

        boolean isUseThumbnail = data.KIND == Const_VALUES.SELECT_UPLOAD;

        if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
            return getImagePathForSnapsDiary(context, data);
        }

        final String safetyThumbnailPath = data.getSafetyThumbnailPath();

        String baseImgPath = isUseThumbnail ? safetyThumbnailPath : data.PATH != null ? data.PATH : data.ORIGINAL_PATH;

        if (data.isApplyEffect) {
            String path = isUseThumbnail ? data.EFFECT_THUMBNAIL_PATH : data.EFFECT_PATH;

            if ((new File(path).exists())) {
                return path;
            } else {
                // ???????????? ????????? ?????? ?????? ?????????, ????????? ???????????? ?????? ??????.
                if (Config.isSNSPhoto(data.KIND)) {
                    baseImgPath = safetyThumbnailPath;
                    if (baseImgPath != null && baseImgPath.startsWith("/Upload")) {
                        baseImgPath = SnapsAPI.DOMAIN(false) + baseImgPath;
                    }
                } else {
                    if (safetyThumbnailPath != null && safetyThumbnailPath.length() > 0) {
                        baseImgPath = data.getSafetyThumbnailPath();
                        if (baseImgPath != null && baseImgPath.startsWith("/Upload")) {
                            baseImgPath = SnapsAPI.DOMAIN(false) + baseImgPath;
                        }
                    }
                }

                data.ADJ_CROP_INFO.setShouldCreateFilter(!data.isTriedRecoveryEffectFilterFile());
                data.ADJ_CROP_INFO.setEffectFilerMaker(getEffectFilterMaker(context, data));
                return baseImgPath;
            }
        } else {
            boolean isWebUrl = baseImgPath != null && baseImgPath.startsWith("http");

            if (!isWebUrl) {
                if (baseImgPath != null && new File(baseImgPath).exists()) {
                    data.KIND = Const_VALUES.SELECT_PHONE;
                } else {
                    if (Config.isSNSPhoto(data.KIND)) {
                        baseImgPath = safetyThumbnailPath;
                        if (baseImgPath != null && baseImgPath.startsWith("/Upload")) {
                            baseImgPath = SnapsAPI.DOMAIN(false) + baseImgPath;
                        }
                    } else {
                        if (safetyThumbnailPath != null && safetyThumbnailPath.length() > 0 && !safetyThumbnailPath.contains("/snaps/effect")) { //FIXME... ????????? ?????? ????????? ???????????? ????????? ??????.
                            baseImgPath = SnapsAPI.DOMAIN(false) + safetyThumbnailPath;
                        }
                    }
                }
            } else {
                if (Config.isSNSPhoto(data.KIND)) {
                    baseImgPath = data.PATH;
                    if (Const_PRODUCT.isSNSBook()) {
                        String thumb = safetyThumbnailPath;
                        if (thumb != null && thumb.startsWith("http")) {
                            baseImgPath = safetyThumbnailPath;
                        }
                    }
                } else {
                    if (safetyThumbnailPath != null && safetyThumbnailPath.length() > 0 && !safetyThumbnailPath.contains("/snaps/effect")) {
                        if (!safetyThumbnailPath.startsWith("http")) {
                            baseImgPath = SnapsAPI.DOMAIN(false) + safetyThumbnailPath;
                        } else {
                            baseImgPath = safetyThumbnailPath;
                        }
                    }
                }
            }
        }

        baseImgPath = getGooglePhotoUrl(baseImgPath, data);
        return baseImgPath;
    }

    //ben:??????!!!!!
    //??????????????? ?????????????????? ????????? ?????? ??????;;;
    //???????????? : ?????? ????????? ????????? ???????????? ???????????? ??????????????? ?????? ??? ?????? ?????? ?????? ???????????? ????????? ???????????? ????????? ????????? ?????? ?????? ??????
    //???????????? : ?????? ????????? ????????? ???????????? ????????? ?????? ????????? ?????? ????????? ???????????? ?????? ????????? ???????????????. ????????? ?????? ????????? ????????? ?????? ????????? ????????? ??????.
    //?????? ????????? : ???????????? ????????? ???????????? ?????? ????????? ????????? ????????? ????????????. ??? ?????? ?????? ?????? ????????? ???????????? ?????? ???????????? ???????????? ????????????.
    public static String getGooglePhotoUrl(String baseImgPath, MyPhotoSelectImageData data) {
        if (baseImgPath == null || baseImgPath.length() == 0) {
            return baseImgPath;
        }

        //?????? ?????? URL?????? ??????
        if (isGooglePhotoURL(baseImgPath) == false) {
            return baseImgPath;
        }

        //????????? ?????? ???????????? ????????? ????????? ?????? <- ??????????????? ?????? ?????? ?????? ???????????? ????????? ????????? ??????.
        if (data.ORIGINAL_PATH == null || data.ORIGINAL_PATH.length() == 0) {
            return baseImgPath;
        }

        //????????? ?????? ??????
        if (baseImgPath.equals(data.THUMBNAIL_PATH)) {
            return baseImgPath;
        }

        //?????? ?????? ???????????? ????????? ???????????? ????????? ???????????? ????????? ???????????? ?????????.
        String newBaseImgPath = SnapsAPI.DOMAIN(false) + data.THUMBNAIL_PATH;
        return newBaseImgPath;
    }

    public static boolean isGooglePhotoURL(String imageURL) {
        return imageURL.startsWith("https") && imageURL.contains("google");
    }

//	public static String createAppiedEffectImgPath(Context context, MyPhotoSelectImageData data, String basePath) {
//		if (data == null)
//			return null;
//
//		try {
//			Bitmap bmp = null;
//
//			if (basePath != null) {
//				if (basePath.startsWith("http"))
//					bmp = CropUtil.sycnLoadImage(basePath, ImageFilters.PREVIEW_SAMPLE_SIZE, ImageFilters.PREVIEW_SAMPLE_SIZE, true, data.ROTATE_ANGLE);
//				else
//					bmp = ImageLoader.syncLoadBitmap(context, basePath, ImageFilters.PREVIEW_SAMPLE_SIZE, ImageFilters.PREVIEW_SAMPLE_SIZE, data.ROTATE_ANGLE);
//			}
//
//			if (bmp != null) {
//				Bitmap bmEffect = ImageFilters.getEffectAppliedBitmap(context, ImageEffectBitmap.convertEffectStrToEnumType(data.EFFECT_TYPE), bmp);
//
//				if (bmEffect != null && !bmEffect.isRecycled()) {
//
//					String effectPath = data.EFFECT_PATH;
//					if (effectPath == null || effectPath.length() < 1)
//						effectPath = ImageFilters.getExportFileName(context, data.F_IMG_NAME, data.F_IMG_SQNC, data.EFFECT_TYPE);
//
//					String result = ImageFilters.getAppliedEffectImgFilePath(context, bmEffect, effectPath);
//					if (result != null) {
//						return result;
//					}
//				}
//			}
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//
//		return null;
//	}
}
