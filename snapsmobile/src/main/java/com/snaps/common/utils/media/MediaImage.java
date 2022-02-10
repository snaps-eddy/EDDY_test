package com.snaps.common.utils.media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;

import com.snaps.common.utils.log.Dlog;

/**
 * MediaStore의 Image 관련 쿼리 담당 Class
 * 
 * @author crjung
 * 
 */
public class MediaImage {
	private static final String TAG = MediaImage.class.getSimpleName();
	
	static final boolean IS_SUPPORT_PNG = true;
	
	/** 이미지 폴더(bucket) 조회용 projection */
	static final String[] PROJECTION_BUCKET_IN_ONE_TABLE = { ImageColumns.BUCKET_ID, ImageColumns.DATE_TAKEN, ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.DATA, ImageColumns._ID, ImageColumns.ORIENTATION };

	/** 미디어스토어에서 jpg 파일만 가져오기 */
	static final String BUCKET_FILTER_JPG_N_PNG = 
			IS_SUPPORT_PNG ? "LOWER(" + ImageColumns.DATA + ") like '%.jpg' OR " + "LOWER(" + ImageColumns.DATA + ") like '%.jpeg' OR " + "LOWER(" + ImageColumns.DATA + ") like '%.png'"
					: "LOWER(" + ImageColumns.DATA + ") like '%.jpg' OR " + "LOWER(" + ImageColumns.DATA + ") like '%.jpeg'";// png도 적용.
	
	/** 이미지 폴더(bucket) 조회 시 Group by용 selection */
//	static final String BUCKET_GROUP_BY_IN_ONE_TABLE = BUCKET_FILTER_JPG_N_PNG + ") GROUP BY (1";// 1번째컬럼(BUCKET_ID)으로 Group By
	static final String BUCKET_ORDER_BY = ImageColumns.DATE_TAKEN + " DESC";

	/** 이미지 상세조회용 projection */
	static final String[] PROJECTION_BUCKET_DETAIL = { ImageColumns._ID, ImageColumns.DATA, ImageColumns.DISPLAY_NAME,
			ImageColumns.DATE_TAKEN, ImageColumns.ORIENTATION, ImageColumns.WIDTH, ImageColumns.HEIGHT };
	static final String[] PROJECTION_BUCKET_DETAIL2 = { ImageColumns.BUCKET_ID, ImageColumns._ID, ImageColumns.DATA, ImageColumns.DISPLAY_NAME,
			ImageColumns.DATE_TAKEN, ImageColumns.ORIENTATION, ImageColumns.WIDTH, ImageColumns.HEIGHT, ImageColumns.DATE_MODIFIED };
	static final String BUCKET_DETAIL_ORDER_BY = ImageColumns.DATE_TAKEN + " DESC";

	/**
	 * 이미지 폴더(bucket) 리스트 조회
	 * 
	 * @param context
	 * @return
	 */
	public static Cursor getBucketList(Context context) {
		return context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET_IN_ONE_TABLE, null, null, BUCKET_ORDER_BY);
	}

	//MediaStore.Images.Thumbnails.MINI_KIND
	public static String getThumbnailCursor(Context context, long uri, int kind) {
		Cursor cursor = null;
		try {
			cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
					context.getContentResolver(), uri,
					kind,
					PROJECTION_BUCKET_DETAIL );

			if( cursor != null && cursor.getCount() > 0 ) {
				cursor.moveToFirst();
				return cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return null;
	}

	/**
	 * 이미지 상세조회
	 * 
	 * @param context
	 * @param bucketId
	 * @return
	 */
	public static Cursor getBucketDetail(Context context, String bucketId) {
		return context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET_DETAIL, "(" + BUCKET_FILTER_JPG_N_PNG + ") AND " + ImageColumns.BUCKET_ID + " = " + bucketId, null, BUCKET_DETAIL_ORDER_BY);
	}

	public static Cursor getBucketDetail2(Context context) {
		return context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET_DETAIL2,
				null, null, null);
	}

	public static Cursor getBucketDetail(Context context, int bucketId) {
		return context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET_DETAIL, "(" + BUCKET_FILTER_JPG_N_PNG + ") AND " + ImageColumns.BUCKET_ID + " = " + bucketId, null, BUCKET_DETAIL_ORDER_BY);
	}

	public static Cursor getUseHomeImages(Context context, int imgCount) {
		return context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET_DETAIL, "(" + BUCKET_FILTER_JPG_N_PNG + ") ", null, BUCKET_DETAIL_ORDER_BY + " LIMIT " + imgCount);
	}

	public static String getImageThumbnailPath(Context context, long imageId) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Thumbnails.DATA }, MediaStore.Images.Thumbnails.IMAGE_ID + " = " + imageId, null, null);
			if (cursor != null && cursor.moveToFirst())
				return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/***
	 * half image 1/2
	 * 
	 * @param context
	 * @param imageId
	 * @return
	 */
	public static Bitmap getImageMiniThumbnailPath(Context context, long imageId) {
		return getImageMiniThumbnailPath(context, imageId, 1);
	}
	
	public static Bitmap getImageMiniThumbnailPath(Context context, long imageId, int sampleRat) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = sampleRat;
			return Images.Thumbnails.getThumbnail(context.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND, options);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return getImageMiniThumbnailPath(context, imageId, sampleRat * 2);
		} catch (Exception e) {
			Dlog.e(TAG, e);
			return null;
		} 
	}

	/***
	 * 쿼터 이미지 1/4
	 * 
	 * @param context
	 * @param imageId
	 * @return
	 */
	public static Bitmap getImageMicroThumbnailPath(Context context, long imageId) {
		try {
			return Images.Thumbnails.getThumbnail(context.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
		}
		return null;
	}
}
