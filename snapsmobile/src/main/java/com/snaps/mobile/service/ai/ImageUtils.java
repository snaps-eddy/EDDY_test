package com.snaps.mobile.service.ai;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.snaps.mobile.order.order_v2.util.thumb_image_upload.SnapsThumbnailMaker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();
    public static final int THUMBNAIL_PIXEL_SIZE = SnapsThumbnailMaker.THUMBNAIL_SIZE_OFFSET;

    /**
     * 이미지를 저장한다.
     * @param bitmap
     * @param filePath
     * @return
     */
    public boolean saveBitmap(Bitmap bitmap, String filePath) {
        if (bitmap == null) {
            return false;
        }

        if (filePath == null || filePath.length() == 0) {
            return false;
        }

        boolean isSuccess;
        BufferedOutputStream bout = null;
        try {
            bout = new BufferedOutputStream(new FileOutputStream(filePath));

            if (bitmap.hasAlpha()) {
                isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, bout);
            }
            else {
                isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bout);  //원본 코드가 90
            }
            bout.flush();
        }catch (IOException e) {
            Loggg.e(TAG, e);
            return false;
        }finally {
            if (bout != null) {
                try {
                    bout.close();
                }catch (IOException e) {
                    Loggg.e(TAG, e);
                }
            }
        }

        return isSuccess;
    }

    /**
     * 이미지 가로, 세로 크기를 고려해서 가로/세로 중 최대 크기로... (설명을 못하겠네...)
     * 예를 들면 가로 1000, 세로 2000 일때 max가 500이라면 가로/세로 중 큰값인 세로 2000을 500으로하고 가로를 비율에 맞게 계산
     * @param width
     * @param height
     * @param maxWidthAndHeight
     * @return
     */
    public Point getResizeWidthAndHeight(int width, int height, int maxWidthAndHeight) {
        return getResizeWidthAndHeight(width, height, maxWidthAndHeight, null);
    }

    /**
     * 이미지 가로, 세로 크기를 고려해서 가로/세로 중 최대 크기로... (설명을 못하겠네...)
     * 예를 들면 가로 1000, 세로 2000 일때 max가 500이라면 가로/세로 중 큰값인 세로 2000을 500으로하고 가로를 비율에 맞게 계산
     * @param width
     * @param height
     * @param maxWidthAndHeight
     * @param point 메모리 절약을 위해 (new 안하려고)
     * @return
     */
    public Point getResizeWidthAndHeight(int width, int height, int maxWidthAndHeight, Point point) {
        int resizeWidth = width;
        int resizeHeight = height;
        if (width > maxWidthAndHeight || height > maxWidthAndHeight) {
            if (width > height) {
                //가로가 큰 경우
                resizeWidth = maxWidthAndHeight;
                resizeHeight = (int)((float)height * ((float)maxWidthAndHeight / (float)width));
            }
            else if (width < height) {
                //세로가 큰 경우
                resizeWidth = (int)((float)width * ((float)maxWidthAndHeight / (float)height));
                resizeHeight = maxWidthAndHeight;
            }
            else {
                //가로와 세로가 같은 경우
                resizeWidth = maxWidthAndHeight;
                resizeHeight = maxWidthAndHeight;
            }
        }

        if (point == null) {
            return new Point(resizeWidth, resizeHeight);
        }

        point.x = resizeWidth;
        point.y = resizeHeight;
        return point;
    }


    /**
     * 지정한 크기에 맞추어(???) 이미지 가로, 세로 비율을 유지하면서 이미지 객체를 만든다. (out of memory 방어 코드 포함)
     * @param filePath
     * @param maxWidthAndHeight
     * @return null을 리턴 할수 있다.
     */
    public Bitmap createResizedBitmap(String filePath, int maxWidthAndHeight) {
        if (filePath == null || filePath.length() == 0) {
            return null;
        }

        if (maxWidthAndHeight < 1) {
            return null;
        }

        //원본 이미지 크기가 아주 큰 경우 그냥 로딩 했다가 out of memory가 발생 할 수 있다.
        //그래서 아래와 같이 복잡하게 처리한다.

        //1. 원본 이미지의 가로, 세로 크기 구하기
        BufferedInputStream bin = null;
        BitmapFactory.Options bitmapOptions = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(filePath));
            bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;  //이게 중요! 실제 비트맵 이미지를 만들지 않는다. 일단 정보만 구함
            BitmapFactory.decodeStream(bin, null, bitmapOptions);
        }catch (IOException e) {
            Loggg.e(TAG, e);
            return null;
        }finally {
            if (bin != null) {
                try {
                    bin.close();
                }catch (IOException e) {
                    Loggg.e(TAG, e);
                }
            }
        }

        //2.내가 원하는 크기 계산
        Point point = getResizeWidthAndHeight(bitmapOptions.outWidth, bitmapOptions.outHeight, maxWidthAndHeight);
        int resizeWidth = point.x;
        int resizeHeight = point.y;


        //3.메모리 문제 안생기고 만들수 있는 이미지 축소 비율 계산
        bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions, resizeWidth, resizeHeight);

        //4.비트맵 축소하거나 원래크기대로 생성
        Bitmap bitmap = null;
        bin = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(filePath));
            bitmapOptions.inJustDecodeBounds = false;
            bitmapOptions.inDither = false;
            bitmap = BitmapFactory.decodeStream(bin, null, bitmapOptions);
            if (bitmap == null) {
                return null;
            }
        }catch (IOException e) {
            Loggg.e(TAG, e);
            return null;
        }finally {
            if (bin != null) {
                try {
                    bin.close();
                }catch (IOException e) {
                    Loggg.e(TAG, e);
                }
            }
        }


        //이제 마지막 단계
        //5.결과물 생성
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, false);

        //6.뒤처리
        if (bitmap != resizedBitmap) {
            bitmap.recycle();
        }

        return resizedBitmap;

    }

    /**
     * [aosp 코드인듯..]
     * 이미지를 로딩해야 할때 적당한 축소 비율을 계산한다.
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
