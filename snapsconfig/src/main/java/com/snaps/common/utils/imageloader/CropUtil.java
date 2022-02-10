package com.snaps.common.utils.imageloader;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.opengl.GLES10;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;

import com.snaps.common.utils.constant.ISnapsConfigConstants;
import com.snaps.common.utils.file.FlushedInputStream;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo.CropImageRect;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFALUT_CACHE_SIZE;
import static com.snaps.common.utils.imageloader.ImageLoader.MAX_DOWN_SAMPLE_RATIO;


public class CropUtil {
    private static final String TAG = CropUtil.class.getSimpleName();

    static SimpleImageDownloader gSimpleImageDownloader = null;

    /**
     * Crop 정보를 바탕으로 Bitmap을 Crop한다.
     *
     * @param cropInfo
     * @param bitmap
     * @return
     */
    public static Bitmap cropBitmap(CropInfo cropInfo, Bitmap bitmap) {
        return cropBitmap(cropInfo, bitmap, 1);
    }

    public static Bitmap cropBitmap(CropInfo cropInfo, Bitmap bitmap, int sampleRat) {

        if (bitmap == null || bitmap.isRecycled())
            return null;

        Bitmap converted = null;
        try {

            if (sampleRat > 1) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() / sampleRat, bitmap.getHeight() / sampleRat, false);
                if (scaledBitmap != bitmap) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
                bitmap = scaledBitmap;
            }

            if (CropInfo.CORP_ORIENT.WIDTH == cropInfo.cropOrient) {
                int startX = (int) ((float) bitmap.getWidth() * ((float) cropInfo.startPercent / cropInfo.CROP_ACCURACY));
                int endX = (int) ((float) bitmap.getWidth() * ((float) cropInfo.endPercent / cropInfo.CROP_ACCURACY));
                converted = Bitmap.createBitmap(bitmap, startX, 0, endX
                        - startX, bitmap.getHeight());
            } else if (CropInfo.CORP_ORIENT.HEIGHT == cropInfo.cropOrient) {
                int startY = (int) ((float) bitmap.getHeight() * ((float) cropInfo.startPercent / cropInfo.CROP_ACCURACY));
                int endY = (int) ((float) bitmap.getHeight() * ((float) cropInfo.endPercent / cropInfo.CROP_ACCURACY));
                converted = Bitmap.createBitmap(bitmap, 0, startY,
                        bitmap.getWidth(), endY - startY);
            }

        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                cropBitmap(cropInfo, bitmap, sampleRat);
            else
                return null;
        }

        return converted;
    }

    //TODO  특이한 케이스로 아래 로직이 안 먹히는 고객이 있어서 반응을 보기 위해 전 코드로 롤백시켰다..만약, 반응이 잠잠하다면 다시 코드를 원복하자.
//	public synchronized static Bitmap getRotateImage(Bitmap bmp, int rotate) {
//		if (bmp == null || bmp.isRecycled())
//			return null;
//
//		if (rotate == 0) return bmp;
//
//		WeakReferenceBitmap weakReferenceBitmap = new WeakReferenceBitmap();
//		BitmapRotateTask rotateTask = new BitmapRotateTask(bmp, rotate, weakReferenceBitmap);
//		rotateTask.start();
//
//		final int MAX_WAIT_COUNT = 200; //10SEC
//		int waitCnt = 0;
//		while (rotateTask.isRunning()) {
//			try {
//				Thread.sleep(50);
//				if (++waitCnt > MAX_WAIT_COUNT) {
//					break;
//				}
//			} catch (InterruptedException e) {
//				Dlog.e(TAG, e);
//			}
//		}
//
//		return weakReferenceBitmap.getBmp();
//	}

    public static Bitmap getRotateImage(Bitmap bmp, int rotate) {
        if (bmp == null || bmp.isRecycled())
            return null;
        Matrix matrix = new Matrix();
        matrix.setRotate(rotate);

        try {
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            return null;
        }

        return bmp;
    }

    /**
     * 편집화면에서 저장된 Matrix로 Bitmap 을 재현한다.
     */
    public static Bitmap cropBitmapByMatrix(Bitmap bitmap, String uri,
                                            AdjustableCropInfo cropInfo, int rotate) {
        //메모리 이슈 때문에 축소해서 재연한다.
        final float SAMPLE_RATIO = 1f;
        bitmap = getRecoveryEditedImg(bitmap, uri, cropInfo, rotate, SAMPLE_RATIO);

        return bitmap != null && !bitmap.isRecycled() ? bitmap : null;
    }

    private static Bitmap getRecoveryEditedImg(Bitmap bitmap,
                                               String uri,
                                               AdjustableCropInfo cropInfo,
                                               int rotate,
                                               float sampleRat) {
        try {
            CropImageRect imgRect = cropInfo.getImgRect();
            CropImageRect clipRect = cropInfo.getClipRect();

            if (imgRect == null || clipRect == null)
                return null;

            if (bitmap == null || bitmap.isRecycled()) {
                bitmap = sycnLoadImage(uri);

                if (rotate != 0) {
                    bitmap = getRotateImage(bitmap, rotate);
                }
            }

            if (bitmap == null || bitmap.isRecycled())
                return null;

            float preW = (float) imgRect.resWidth / sampleRat;
            float preH = (float) imgRect.resHeight / sampleRat;

            // 편집할 당시의 상황을 재현하기 위해 편집 화면에서의 크기로 변환한다.
            Bitmap bmScaled = Bitmap.createScaledBitmap(bitmap,
                    (int) preW, (int) preH, false);

            if (bmScaled == null || bmScaled.isRecycled()) return null;

            if (bitmap != bmScaled) {
                bitmap.recycle();
                bitmap = null;
            }

            // 편집화면에서 셋팅한 matrixValue를 가져와서 그대로 셋팅해 준다.
            float[] arSampledMatrix = new float[9];
            for (int ii = 0; ii < 9; ii++)
                arSampledMatrix[ii] = imgRect.matrixValue[ii] / sampleRat;

            Matrix matrix = new Matrix();
            matrix.setValues(arSampledMatrix);

            Bitmap bmAppliedMatrix = Bitmap.createBitmap(bmScaled, 0,
                    0, bmScaled.getWidth(), bmScaled.getHeight(),
                    matrix, true);

            if (bmAppliedMatrix == null || bmAppliedMatrix.isRecycled()) return null;

            if (bmScaled != bmAppliedMatrix) {
                bmScaled.recycle();
                bmScaled = null;
            }

            float[] center = new float[2];
            float[] offset = new float[2];

            center[0] = (bmAppliedMatrix.getWidth() / 2)
                    - (imgRect.movedX / sampleRat);
            center[1] = (bmAppliedMatrix.getHeight() / 2)
                    - (imgRect.movedY / sampleRat);

            offset[0] = Math.max(0,
                    (center[0] - (clipRect.width / (2 * sampleRat))));
            offset[1] = Math.max(0,
                    (center[1] - (clipRect.height / (2 * sampleRat))));

            try {
                bitmap = Bitmap
                        .createBitmap(bmAppliedMatrix,
                                (int) offset[0], (int) offset[1],
                                (int) (clipRect.width / sampleRat),
                                (int) (clipRect.height / sampleRat));
            } catch (IllegalArgumentException e) {
                Dlog.e(TAG, e);
                return null;
            }

            if (bitmap != bmAppliedMatrix) {
                if (bmAppliedMatrix != null && !bmAppliedMatrix.isRecycled()) {
                    bmAppliedMatrix.recycle();
                    bmAppliedMatrix = null;
                }
            }
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            //회전 문제가 발생할 수 있으므로, 비트맵을 지우고 새로 생성한다.
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e1) {
                Dlog.e(TAG, e1);
            }

            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getRecoveryEditedImg(bitmap, uri, cropInfo, rotate, sampleRat);
            else
                return null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }

        return bitmap;
    }

    static public Bitmap sycnLoadImage(String url) {
//		return sycnLoadImage(url, 512, 512, false, 0);
        return sycnLoadImage(url, DEFALUT_CACHE_SIZE, DEFALUT_CACHE_SIZE, false, 0);
    }

    @SuppressLint("NewApi")
    static public Bitmap sycnLoadImage(String url, int width, int height,
                                       boolean scale, int angle) {

        if (gSimpleImageDownloader != null
                && gSimpleImageDownloader.getStatus() == AsyncTask.Status.RUNNING) {
            while (gSimpleImageDownloader != null
                    && gSimpleImageDownloader.isDownloading) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
            }
        }

        CropUtilBitmap cBitmap = new CropUtilBitmap();

        if (url.startsWith("http://") || url.startsWith("https://")) {

            if (scale)
                gSimpleImageDownloader = new SimpleImageDownloader(url,
                        cBitmap, width, height, true);
            else
                gSimpleImageDownloader = new SimpleImageDownloader(url, cBitmap);

//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				gSimpleImageDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//			} else {
//				gSimpleImageDownloader.execute();
//			}

            while (gSimpleImageDownloader != null
                    && gSimpleImageDownloader.isDownloading) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { // 여러곳에서 호출하는 case가 있는가 보다. log에 "Cannot execute task: the task is already running." error 가 있어, 시작하기 전에 실행중인 download가 있으면 대기하도록 수정.
                gSimpleImageDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                gSimpleImageDownloader.execute();
            }

            while (gSimpleImageDownloader != null
                    && gSimpleImageDownloader.isDownloading) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
            }

        } else {
            cBitmap.bmp = loadImage(url, width, height, 1);
        }

        if (angle > 0) {
            cBitmap.bmp = getRotateImage(cBitmap.bmp, angle);
        }

        return cBitmap.bmp;

    }

    public static int getExifOrientationTag(String imageUri) throws IOException {
        int cacheExifOrientation = getExifOrientationCache(imageUri);
        if (cacheExifOrientation != Integer.MIN_VALUE) {
            return cacheExifOrientation;
        }

        if (isPNGFile(imageUri)) return 0;
        ExifInterface exif = new ExifInterface(imageUri);
        return exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
    }

    private static boolean isPNGFile(String imageUri) {
        if (imageUri != null) {
            String trimUri = imageUri.trim().toLowerCase();
            if (trimUri.endsWith("png"))
                return true;
        }
        return false;
    }

    //////////////////////////////////////////////////////////////////
    private static Map<String, Integer> sCacheExifOrientationMap = new HashMap<>();
    private static Map<String, Long> sCacheExifOrientationAddTimeMap = new HashMap<>();

    private static int getExifOrientationCache(String imageUri) {
        if (sCacheExifOrientationAddTimeMap.containsKey(imageUri)) {
            long timeDiff = System.currentTimeMillis() - sCacheExifOrientationAddTimeMap.get(imageUri);
            if (timeDiff < 1000 * 10) {
                //사용자가 외부 편집기를 이용해서 사진을 변경하는 경우가 있으므로 제한 Cache유지 시간을 10초로 한다.
                //10초가 너무 짧다고 생각할 수 있으나, 현재 구현된 코드를 보면 단 시간안에 같은 사진 파일에 대해서 ot값을 요청하고 있다.
                //사진 추가를 하면 몇번씩 ot를 요청하고 있는 상태
                try {
                    int exifOrientation = sCacheExifOrientationMap.get(imageUri);
                    return exifOrientation;
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    return Integer.MIN_VALUE;
                }
            }

            sCacheExifOrientationAddTimeMap.remove(imageUri);
            sCacheExifOrientationMap.remove(imageUri);
        }

        return Integer.MIN_VALUE;
    }

    private static void addExifOrientationCache(String imageUri, int exifOrientation) {
        sCacheExifOrientationAddTimeMap.put(imageUri, System.currentTimeMillis());
        sCacheExifOrientationMap.put(imageUri, exifOrientation);
    }
    //////////////////////////////////////////////////////////////////

    public static int getExifOrientation(String imageUri) {
        int cacheExifOrientation = getExifOrientationCache(imageUri);
        if (cacheExifOrientation != Integer.MIN_VALUE) {
            return covertExifOrientationtoAngle(cacheExifOrientation);
        }

        if (imageUri != null) { //png는 exif가 없다.
            String trimUri = imageUri.trim().toLowerCase();
            if (trimUri.endsWith("png"))
                return 0;
        }

        int rotation = 0;
        try {
            ExifInterface exif = new ExifInterface(imageUri);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            rotation = covertExifOrientationtoAngle(exifOrientation);

            addExifOrientationCache(imageUri, exifOrientation);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return rotation;
    }

    private static int covertExifOrientationtoAngle(int exifOrientation) {
        int rotation = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                rotation = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_TRANSPOSE:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
        }
        return rotation;
    }

    public static Bitmap getScaledBitmapFromUrl(String imageUrl, int requiredWidth, int requiredHeight, int sampleRat) {
        FlushedInputStream fis = null;
        try {
            URL url = new URL(imageUrl);
            BitmapFactory.Options options = new BitmapFactory.Options();

            if (sampleRat <= 1) {

                options.inJustDecodeBounds = true;
                fis = new FlushedInputStream(url.openConnection().getInputStream());
                BitmapFactory.decodeStream(fis, null, options);

                if (requiredWidth <= -1 || requiredHeight <= -1) {
                    requiredWidth = options.outWidth;
                    requiredHeight = options.outHeight;
                }

                options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
            } else {
                options.inSampleSize = sampleRat;
            }

            sampleRat = options.inSampleSize;
            options.inJustDecodeBounds = false;
            options.inDither = false;
            //don't use same inputstream object as in decodestream above. It will not work because
            //decode stream edit input stream. So if you create
            //InputStream is =url.openConnection().getInputStream(); and you use this in  decodeStream
            //above and bellow it will not work!
            return BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getScaledBitmapFromUrl(imageUrl, requiredWidth, requiredHeight, sampleRat);
            else
                return null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }
    }

    public static Bitmap getScaledBitmapFromStream(String filePath) {
        return getScaledBitmapFromStream(filePath, -1, -1, 1);
    }

    public static Bitmap getScaledBitmapFromStream(String filePath, int requiredWidth, int requiredHeight, int sampleRat) {
        BufferedInputStream is = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (sampleRat <= 1) {

                is = new BufferedInputStream(new FileInputStream(filePath));
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, options);

                if (requiredWidth <= -1 || requiredHeight <= -1) {
                    requiredWidth = options.outWidth;
                    requiredHeight = options.outHeight;
                }

                options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);

                is.close();
                is = null;
            } else {
                options.inSampleSize = sampleRat;
            }

            sampleRat = options.inSampleSize;
            options.inJustDecodeBounds = false;
            options.inDither = false;
            //don't use same inputstream object as in decodestream above. It will not work because
            //decode stream edit input stream. So if you create
            //InputStream is =url.openConnection().getInputStream(); and you use this in  decodeStream
            //above and bellow it will not work!
            is = new BufferedInputStream(new FileInputStream(filePath));
            return BitmapFactory.decodeStream(is, null, options);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getScaledBitmapFromStream(filePath, requiredWidth, requiredHeight, sampleRat);
            else
                return null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }
    }

    public static Bitmap getBitmapFromStreamSafetyOOM(String filePath) {
        BufferedInputStream is = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inDither = false;
            is = new BufferedInputStream(new FileInputStream(filePath));
            return BitmapFactory.decodeStream(is, null, options);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            return null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }
    }

    public static class SimpleImageDownloader extends
            AsyncTask<Void, Void, Void> {

        String imgUrl;
        CropUtilBitmap imgBitmap;
        boolean isDownloading = false;
        boolean scale = false;
        int width, height;

        public SimpleImageDownloader(String url, CropUtilBitmap bm) {
            imgUrl = url;
            imgBitmap = bm;
            isDownloading = true;
            scale = false;
        }

        public SimpleImageDownloader(String url, CropUtilBitmap bm, int width,
                                     int height, boolean scale) {
            imgUrl = url;
            imgBitmap = bm;
            isDownloading = true;
            this.width = width;
            this.height = height;
            this.scale = scale;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                isDownloading = true;

                if (scale) {
                    imgBitmap.bmp = getScaledBitmapFromUrl(imgUrl, width, height, 1);
//					Bitmap sampleBitmap = BitmapFactory.decodeStream(fis, new Rect(), option);
//					int sampleSize = calculateInSampleSize(option, width, height);
//					option.inSampleSize = sampleSize;
//					if(sampleBitmap != null) {
//
//						ByteArrayOutputStream bos = new ByteArrayOutputStream();
//						sampleBitmap.compress(CompressFormat.JPEG, 100, bos);
//						byte[] bitmapdata = bos.toByteArray();
//						ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
//
//						if(imgBitmap != null)
//							imgBitmap.bmp = BitmapFactory.decodeStream(bs, new Rect(), option);
//
//						sampleBitmap.recycle();
//						sampleBitmap = null;
//					}
                } else {
//					URL url = new URL(imgUrl);
//					URLConnection conn = url.openConnection();
//					conn.connect();
//					FlushedInputStream fis = new FlushedInputStream(conn.getInputStream());
//					final BitmapFactory.Options option = new Options();
//					option.inJustDecodeBounds = false;
//
//					//FIXME 그대로 가져오면 죽을텐데...
//					if(imgBitmap != null)
//					{
//						try {
//							imgBitmap.bmp = BitmapFactory.decodeStream(fis, new Rect(), option);
//						} catch (OutOfMemoryError e) {
//
//						}
//					}


                    imgBitmap.bmp = getInSampledBitmap(imgUrl, 1);
                }

                isDownloading = false;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            } finally {
                isDownloading = false;
            }
            return null;
        }
    }

    public static Bitmap getInSampledBitmap(String imgUrl, int sampleValue) throws IOException {
        Bitmap imgBitmap = null;
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        FlushedInputStream fis = new FlushedInputStream(conn.getInputStream());
        final BitmapFactory.Options option = new Options();
        option.inJustDecodeBounds = false;
        option.inSampleSize = sampleValue;

        try {
            imgBitmap = BitmapFactory.decodeStream(fis, new Rect(), option);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e1) {
                Dlog.e(TAG, e1);
            }
            sampleValue *= 2;
            if (sampleValue <= MAX_DOWN_SAMPLE_RATIO)
                return getInSampledBitmap(imgUrl, sampleValue * 2);
            else
                return null;
        } finally {
            if (conn != null)
                conn.disconnect();

            if (fis != null)
                fis.close();
        }
        return imgBitmap;
    }

    public static Bitmap getInSampledBitmapCopy(@NonNull Bitmap bitmap, @NonNull Bitmap.Config config) {
        return getInSampledBitmapCopy(bitmap, config, 1);
    }

    public static Bitmap getInSampledBitmapCopy(@NonNull Bitmap bitmap, @NonNull Bitmap.Config config, int sampleRat) {
        return getInSampledBitmapCopy(bitmap, config, sampleRat, 1);
    }

    public static Bitmap getInSampledBitmapCopy(@NonNull Bitmap bitmap, @NonNull Bitmap.Config config, int sampleRat, float scale) {
        if (bitmap.isRecycled()) {
            return null;
        }
        Bitmap copyiedBitmap = null;
        try {
            if (sampleRat < 1) {
                copyiedBitmap = bitmap.copy(config, true);
            } else {
                Bitmap scaledBitmap = getInSampledScaleBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), sampleRat);
                if (scaledBitmap != null && !scaledBitmap.isRecycled()) {
                    copyiedBitmap = scaledBitmap.copy(config, true);
                    if (bitmap != scaledBitmap && copyiedBitmap != scaledBitmap && !scaledBitmap.isRecycled()) {
                        scaledBitmap.recycle();
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getInSampledBitmapCopy(bitmap, config, sampleRat);
            else
                return null;
        }
        return copyiedBitmap;
    }

    public static Bitmap getInSampledBitmap(int w, int h, Config config) {
        return getInSampledBitmap(w, h, config, 1);
    }

    public static Bitmap getInSampledBitmap(int w, int h, Config config, int sampleRat) {
        Bitmap imgBitmap = null;

        try {
            imgBitmap = Bitmap.createBitmap(w / sampleRat, h / sampleRat, config);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e1) {
                Dlog.e(TAG, e1);
            }
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getInSampledBitmap(w, h, config, sampleRat);
            else
                return null;
        }
        return imgBitmap;
    }

    public static Bitmap getInSampledScaleBitmap(Bitmap orgBmp, int w, int h) {
        return getInSampledScaleBitmap(orgBmp, w, h, 1);
    }

    public static Bitmap getInSampledScaleBitmap(Bitmap orgBmp, int x, int y, int w, int h) {
        return getInSampledScaleBitmap(orgBmp, x, y, w, h, 1);
    }

    public static Bitmap getInSampledScaleBitmap(Bitmap orgBmp, int x, int y, int w, int h, int sampledSize) {
        Bitmap imgBitmap = null;

        try {
            imgBitmap = Bitmap.createBitmap(orgBmp, x, y, (w / sampledSize), (h / sampledSize));
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e1) {
                Dlog.e(TAG, e1);
            }
            sampledSize *= 2;
            if (sampledSize <= MAX_DOWN_SAMPLE_RATIO)
                return getInSampledScaleBitmap(orgBmp, x, y, w, h, sampledSize);
            else
                return null;
        }
        return imgBitmap;
    }

    public static Bitmap getInSampledScaleBitmap(Bitmap orgBmp, float scale) {
        if (orgBmp == null || orgBmp.isRecycled()) return orgBmp;
        return getInSampledScaleBitmap(orgBmp, (int) (orgBmp.getWidth() * scale), (int) (orgBmp.getHeight() * scale), 1);
    }

    public static Bitmap getInSampledScaleBitmap(Bitmap orgBmp, int w, int h, int sampledSize) {
        Bitmap imgBitmap = null;

        try {
            imgBitmap = Bitmap.createScaledBitmap(orgBmp, w / sampledSize, h / sampledSize, true);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e1) {
                Dlog.e(TAG, e1);
            }
            sampledSize *= 2;
            if (sampledSize <= MAX_DOWN_SAMPLE_RATIO)
                return getInSampledScaleBitmap(orgBmp, w, h, sampledSize);
            else
                return null;
        }
        return imgBitmap;
    }

    public static Bitmap getInSampledDecodeBitmapFromResource(Resources res,
                                                              int resId) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);
            int reqWidth = options.outWidth;
            int reqHeight = options.outHeight;
            return getInSampledDecodeBitmapFromResource(res, resId, reqWidth, reqHeight, 1);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            return null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static Bitmap getInSampledDecodeBitmapFromResource(Resources res,
                                                              int resId, int reqWidth, int reqHeight) {
        return getInSampledDecodeBitmapFromResource(res, resId, reqWidth, reqHeight, 1);
    }

    public static Bitmap getInSampledDecodeBitmapFromResource(Resources res,
                                                              int resId, int reqWidth, int reqHeight, int sampleRat) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            if (sampleRat <= 1) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(res, resId, options);
                options.inSampleSize = calculateInSampleSize(options, reqWidth,
                        reqHeight);
            } else {
                options.inSampleSize = sampleRat;
            }

            sampleRat = options.inSampleSize;
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (OutOfMemoryError e1) {
            Dlog.e(TAG, e1);
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getInSampledDecodeBitmapFromResource(res, resId, reqWidth, reqHeight, sampleRat);
            else
                return null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }
    }

    public static Bitmap loadImage(String filePath, int width, int height) {
        return loadImage(filePath, width, height, 1);
    }

    public static Bitmap loadImage(String filePath, int width, int height, int sampleRat) {
        try {
            final BitmapFactory.Options option = new Options();
            if (sampleRat <= 1) {
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, option);

                if (width <= -1 || height <= -1) {
                    width = option.outWidth;
                    height = option.outHeight;
                }

                option.inSampleSize = calculateInSampleSize(option, width, height);
            } else {
                option.inSampleSize = sampleRat;
            }

            sampleRat = option.inSampleSize;
            option.inJustDecodeBounds = false;
            option.inDither = false;
            return BitmapFactory.decodeFile(filePath, option);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return loadImage(filePath, width, height, sampleRat);
            else
                return null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }
    }

    public static Bitmap loadImage2(String fileUrl, int width, int height, int sampleRat) {
        FlushedInputStream fis = null;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
//	        InputStream input = connection.getInputStream();
            fis = new FlushedInputStream(connection.getInputStream());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            if (sampleRat > 1) {
                options.inSampleSize = sampleRat;
            }

            Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);

            if (width <= -1 || height <= -1) {
                if (width > 0)
                    height = (int) ((float) bitmap.getHeight() / (float) bitmap.getWidth() * (float) width);
                else if (height > 0)
                    width = (int) ((float) bitmap.getWidth() / (float) bitmap.getHeight() * (float) height);
                else {
                    width = bitmap.getWidth();
                    height = bitmap.getHeight();
                }
            }

            return getScaledBitmap(bitmap, width, height);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            sampleRat *= 2;
            if (sampleRat > MAX_DOWN_SAMPLE_RATIO) {
                return null;
            }

            return loadImage2(fileUrl, width, height, sampleRat);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
    }

    public static Bitmap getScaledBitmap(Bitmap origin, int width, int height) {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        if (maxSize[0] < 1) maxSize[0] = 2048;

        if (width > maxSize[0] || height > maxSize[0]) return getScaledBitmap(origin, width / 2, height / 2);
        else return Bitmap.createScaledBitmap(origin, width, height, true);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        if (reqWidth <= -1 || reqHeight <= -1) return 1;
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
                if (inSampleSize > 8) break;
            }
        }

        return inSampleSize;
    }

    // public static Bitmap getLocalPathBitmap(String filePath) {
    // Bitmap imgBitmap = null;
    // try {
    // imgBitmap = BitmapFactory.decodeFile(filePath);
    // } catch (Exception e) {
    // Dlog.e(TAG, e);
    // } catch (OutOfMemoryError e) {
    // Dlog.e(TAG, e);
    //
    // if (filePath != null) {
    // try {
    // BufferedInputStream in = new BufferedInputStream(
    // new FileInputStream(filePath));
    // // FIXME 임시로 512를 기준으로 하였는데, 퀄리티가 떨어진다면 수치 보정이 필요 해 보임.
    // return getArtworkSampled(in, 512, 512);
    // } catch (FileNotFoundException e1) {
    // Dlog.e(TAG, e1);
    // }
    // }
    // }
    // return imgBitmap;
    // }

    /**
     * Image Stream sampling Bitmap
     *
     * @return
     */
//	public static Bitmap getArtworkSampled(InputStream is, int w, int h) {
//		BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
//
//		w -= 1;
//		try {
//			int sampleSize = 2;
//
//			sBitmapOptionsCache.inJustDecodeBounds = true;
//			sBitmapOptionsCache.inDither = false;
//
//			int nextWidth = sBitmapOptionsCache.outWidth >> 1;
//			int nextHeight = sBitmapOptionsCache.outHeight >> 1;
//			while (nextWidth > w && nextHeight > h) {
//				sampleSize <<= 1;
//				nextWidth >>= 1;
//				nextHeight >>= 1;
//			}
//
//			sBitmapOptionsCache.inSampleSize = sampleSize;
//			sBitmapOptionsCache.inJustDecodeBounds = false;
//			Bitmap b = BitmapFactory
//					.decodeStream(is, null, sBitmapOptionsCache);
//
//			if (b != null) {
//				int scaledW = b.getWidth(), scaledH = b.getHeight();
//				if (scaledW <= scaledH) {
//					float ratio = (float) w / (float) scaledW;
//					scaledW = w;
//					scaledH = (int) ((double) scaledH * ratio);
//				} else {
//					float ratio = (float) h / (float) scaledH;
//					scaledW = (int) ((double) scaledW * ratio);
//					scaledH = h;
//				}
//
//				if (sBitmapOptionsCache.outWidth != w
//						|| sBitmapOptionsCache.outHeight != h) {
//					Bitmap tmp = Bitmap.createScaledBitmap(b, scaledW, scaledH,
//							true);
//					if (tmp != b) {
//						b.recycle();
//						b = null;
//					}
//					b = tmp;
//				}
//			}
//
//			return b;
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//		return null;
//	}

//	public static Bitmap noneBitmap(Bitmap bitmap, float ratio) {
//		try {
//			Bitmap converted = null;
//
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true;
//			options.inDither = false;
//			// options.inSampleSize = 4;
//
//			int oriWidth = bitmap.getWidth();
//			int oriHeight = bitmap.getHeight();
//
//			float oriWRatio = (float) oriWidth / (float) oriHeight;
//			float oriHRatio = (float) oriWidth / (float) oriHeight;
//
//			int ratioWidth = oriWidth;
//			int ratioHeight = oriHeight;
//
//			int thumbX = 0;
//			int thumbY = 0;
//
//			if (oriWidth >= oriHeight) // 가로가 세로 보다 긴 직사각형
//			{
//				if (oriWRatio > ratio) {
//
//					ratioWidth = (int) (oriHeight * ratio);
//					ratioHeight = oriHeight;
//
//					thumbX = (oriWidth - ratioWidth) / 2;
//					thumbY = 0;
//
//				} else {
//
//					ratioWidth = oriWidth;
//					ratioHeight = (int) (oriWidth / ratio);
//
//					thumbX = 0;
//					thumbY = (oriHeight - ratioHeight) / 2;
//
//				}
//
//			} else // 세로가 가로 보다 긴 직사각형
//			{
//
//				if (oriHRatio > ratio) {
//
//					ratioWidth = (int) (oriHeight * ratio);
//					ratioHeight = oriHeight;
//
//					thumbX = (oriWidth - ratioWidth) / 2;
//					thumbY = 0;
//
//				} else {
//
//					ratioWidth = oriWidth;
//					ratioHeight = (int) (oriWidth / ratio);
//
//					thumbX = 0;
//					thumbY = (oriHeight - ratioHeight) / 2;
//				}
//			}
//
//			converted = Bitmap.createBitmap(bitmap, thumbX, thumbY, ratioWidth,
//					ratioHeight);
//
//			if (bitmap != converted) {
//				bitmap = converted;
//			}
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//		return bitmap;
//	}
//
    public static int[] getBitmapFilesLength(String fileName) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);
            return new int[]{options.outWidth, options.outHeight};
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static int[] getBitmapURLLength(String imgUrl) throws IOException {
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        FlushedInputStream fis = new FlushedInputStream(conn.getInputStream());
        final BitmapFactory.Options option = new Options();
        option.inJustDecodeBounds = true;

        try {
            BitmapFactory.decodeStream(fis, new Rect(), option);
            return new int[]{option.outWidth, option.outHeight};
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            return null;
        } finally {
            if (conn != null)
                conn.disconnect();

            if (fis != null)
                fis.close();
        }
    }

    public static class CropUtilBitmap {
        Bitmap bmp;
    }

    public static String createThumbnailFile(String orgFilePath) throws IOException {
        String thumbnailPath = "";
        FileOutputStream fos = null;
        try {
            Bitmap imageBitmap = getScaledBitmapFromStream(orgFilePath, -1, -1, 1);
            if (imageBitmap == null) return null;

            imageBitmap = getScaledBitmapOffsetLength(imageBitmap, ISnapsConfigConstants.PRODUCT_THUMBNAIL_OFFSET_LENGTH);

            int extIndex = orgFilePath.lastIndexOf(".");
            thumbnailPath = orgFilePath.substring(0, extIndex) + "_thumbnail" + orgFilePath.substring(extIndex);

            File thumbnailFile = new File(thumbnailPath);
            fos = new FileOutputStream(thumbnailFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            imageBitmap.recycle();
            imageBitmap = null;
        } catch (IOException e) {
            Dlog.e(TAG, e);
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }

        return thumbnailPath;
    }

    public static Bitmap getScaledBitmapOffsetLength(Bitmap orgBitmap, int offsetLength) {
        if (orgBitmap == null || orgBitmap.isRecycled()) return orgBitmap;

        int bitmapWidth = orgBitmap.getWidth();
        int bitmapHeight = orgBitmap.getHeight();

        if (bitmapWidth < ISnapsConfigConstants.PRODUCT_THUMBNAIL_OFFSET_LENGTH && bitmapHeight < ISnapsConfigConstants.PRODUCT_THUMBNAIL_OFFSET_LENGTH) return orgBitmap;

        float scaleValue = bitmapWidth > bitmapHeight ? offsetLength / (float) bitmapWidth : offsetLength / (float) bitmapHeight;

        try {
            float scaledWidth = orgBitmap.getWidth() * scaleValue;
            float scaledheight = orgBitmap.getHeight() * scaleValue;

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(orgBitmap,
                    (int) scaledWidth, (int) scaledheight, false);

            if (scaledBitmap != orgBitmap) {
                if (!orgBitmap.isRecycled()) {
                    orgBitmap.recycle();
                    orgBitmap = null;
                }
            }
            orgBitmap = scaledBitmap;
        } catch (OutOfMemoryError e1) {
            Dlog.e(TAG, e1);
            return orgBitmap;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return orgBitmap;
    }

    public static boolean isFlippedOrientationImage(String uri) {
        int orientationTag = 0;
        try {
            orientationTag = getExifOrientationTag(uri);
        } catch (IOException e) {
            Dlog.e(TAG, e);
        }
        return orientationTag == ExifInterface.ORIENTATION_FLIP_HORIZONTAL
                || orientationTag == ExifInterface.ORIENTATION_FLIP_VERTICAL
                || orientationTag == ExifInterface.ORIENTATION_TRANSPOSE
                || orientationTag == ExifInterface.ORIENTATION_TRANSVERSE;
    }


    public static Bitmap getFlippedBitmap(String imagePath, Bitmap resource) {
        if (StringUtil.isEmpty(imagePath) || imagePath.startsWith("http")) return resource;

        int orientationTag = 0;
        try {
            orientationTag = CropUtil.getExifOrientationTag(imagePath);
        } catch (IOException e) {
            Dlog.e(TAG, e);
            return resource;
        }

        if (orientationTag == 0 || resource == null || resource.isRecycled() || resource.getWidth() == 0 || resource.getHeight() == 0) return resource;

        int centerX = resource.getWidth() / 2;
        int centerY = resource.getHeight() / 2;

        Matrix matrix = new Matrix();
        switch (orientationTag) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(-1, 1, centerX, centerY);
                break;
            default:
                return resource;
        }

        return createBitmapWithMatrix(resource, matrix);
    }


    private static Bitmap createBitmapWithMatrix(Bitmap bitmap, Matrix matrix) {
        if (bitmap == null || bitmap.isRecycled())
            return bitmap;

        Bitmap converted = null;
        Bitmap copiedBitmap = getInSampledBitmapCopy(bitmap, Bitmap.Config.ARGB_8888);
        try {
            converted = Bitmap.createBitmap(copiedBitmap, 0, 0, copiedBitmap.getWidth(), copiedBitmap.getHeight(), matrix, true);
            if (converted != null && copiedBitmap != converted) {
                copiedBitmap.recycle();
                copiedBitmap = null;
            }
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            return bitmap;
        }
        return converted;
    }
}
