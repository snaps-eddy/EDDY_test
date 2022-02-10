package com.snaps.common.utils.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaps.common.utils.file.FlushedInputStream;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.log.Dlog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SnapsImageDownloader {
	private static final String TAG = SnapsImageDownloader.class.getSimpleName();
	private static final String URI_AND_SIZE_SEPARATOR = "_";
	private static final int MEMORY_CACHE_MAX_SIZE = 1024 * 1024 * 3;

	public static final int INVALID_ANGLE = -999;
	
	private CLruCache<String, Bitmap> mImageCache = new CLruCache<String, Bitmap>(MEMORY_CACHE_MAX_SIZE);

	public static File cacheDir;
	final Object mDiskCacheLock = new Object();

	int mWidth;
	int mHeight;
	
	
	public void clean(){
		mImageCache.evictAll();
		mImageCache = null;
	}

	public interface OnLoadComplete {
		public void onComplete(int width, int height);
		public void onFailedLoad();
	}

	public SnapsImageDownloader(int width, int height) {
		this.mWidth = width;
		this.mHeight = height;

	}

	static public Bitmap sycnLoadImage(String url, int width, int height, int angle) {
		Bitmap bitmap = null;
		if (url.startsWith("http://") || url.startsWith("https://")) {
			bitmap = CropUtil.getScaledBitmapFromUrl(url, width, height, 1);
		} else
			bitmap = loadImage(url, width, height, 1);

		if (angle != INVALID_ANGLE && angle != 0) {
			bitmap = rotate(bitmap, angle);
		}

		return bitmap;
	}

//	public static Bitmap getFlippedBitmap(String uri, Bitmap bitmap) {
//		int orientationTag = 0;
//		try {
//			orientationTag = CropUtil.getExifOrientationTag(uri);
//		} catch (IOException e) {
//			Dlog.e(TAG, e);
//		}
//
//		if (orientationTag == 0 || bitmap == null || bitmap.isRecycled() || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) return bitmap;
//
//		int centerX = bitmap.getWidth()/2;
//		int centerY = bitmap.getHeight()/2;
//
//		Matrix matrix = new Matrix();
//		switch (orientationTag) {
//			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//				matrix.postScale(-1, 1, centerX, centerY);
//				break;
//			case ExifInterface.ORIENTATION_TRANSPOSE:
//			case ExifInterface.ORIENTATION_TRANSVERSE:
//				matrix.postScale(1, -1, centerX, centerY);
//				break;
//		}
//
//		return createBitmapWithMatrix(bitmap, matrix);
//	}

	private synchronized static Bitmap decodeCacheFile(File f) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			return BitmapFactory.decodeStream(new FlushedInputStream(new FileInputStream(f)));
		} catch (FileNotFoundException e) {
			Dlog.e(TAG, e);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		}

		return null;
	}

	public static Bitmap getBitmapCacheDir(String key) {
		String filename = key;
		File f = null;

		f = new File(SnapsImageDownloader.cacheDir, filename);

		if (!f.exists())
			return null;

		// from SD mCache
		Bitmap b = decodeCacheFile(f);

		if (b != null)
			return b;
		return null;
	}

	/***
	 * 비트맵을 파일로 저장하는 함수...
	 * 
	 * @param key
	 * @param bitmap
	 */
	@SuppressWarnings("unused")
	private synchronized void saveBitmap(String key, Bitmap bitmap) {
		FileOutputStream stream;
		try {
			File file = new File(SnapsImageDownloader.cacheDir, key);
			try {
				if (!file.exists())
					file.createNewFile();
			} catch (Exception e) {
//				Logg.d("Exception", "setViewBitmapToFile");
			}
			stream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
			
			stream.close();

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

	}

	/***
	 * 키를 만드는 함
	 * 
	 * @param url
	 * @param angle
	 * @return
	 */
	public String getgenerationKey(String url, int angle) {
		String key = new StringBuilder(url).append(URI_AND_SIZE_SEPARATOR).append(mWidth).append(URI_AND_SIZE_SEPARATOR).append(mHeight).append(URI_AND_SIZE_SEPARATOR).append(angle).toString();
		return String.valueOf(key.hashCode());
	}

	/***
	 * 이미지를 로드하는 함수
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, ImageView imageView) {
		loadBitmap(url, imageView, null, 0, null);
	}

	/***
	 * 이미지 로드하는 함수..
	 * 
	 * @param url
	 * @param imageView
	 * @param progressbar
	 */
	public void loadBitmap(String url, ImageView imageView, ProgressBar progressbar, int mAngle) {
		loadBitmap(url, imageView, progressbar, mAngle, null);
	}

	public void loadBitmap(String url, ImageView imageView, ProgressBar progressbar, int mAngle, OnLoadComplete listener) {
		Bitmap bitmap = null;
		bitmap = mImageCache.get(getgenerationKey(url, mAngle));

		if (bitmap != null) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			imageView.setImageBitmap(bitmap);
			if (listener != null) {
				listener.onComplete(w, h);
			}

			if (progressbar != null)
				progressbar.setVisibility(View.GONE);
		}
		if (cancelPotentialWork(url, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(url, imageView, progressbar, mWidth, mHeight, mAngle, listener);
			final AsyncDrawable drawble = new AsyncDrawable(task);
			imageView.setImageDrawable(drawble);
			task.execute(url);
		}
	}

	public void loadCropBitmap(String url, ImageView imageView, ProgressBar progressbar, int mAngle, CropInfo crop) {
		loadCropBitmap(url, imageView, progressbar, mAngle, null, crop);
	}

	public void loadCropBitmap(String url, ImageView imageView, ProgressBar progressbar, int mAngle, OnLoadComplete listener, CropInfo cropInfo) {

		Bitmap bitmap = null;
		bitmap = mImageCache.get(getgenerationKey(url, mAngle));

		if (bitmap != null) {

			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			if (cropInfo != null && CropInfo.CORP_ORIENT.NONE != cropInfo.cropOrient) {
				bitmap = CropUtil.cropBitmap(cropInfo, bitmap);
			}

			imageView.setImageBitmap(bitmap);
			if (listener != null) {
				listener.onComplete(w, h);
			}

			if (progressbar != null)
				progressbar.setVisibility(View.GONE);
		}
		if (cancelPotentialWork(url, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(url, imageView, progressbar, mWidth, mHeight, mAngle, cropInfo, listener);
			final AsyncDrawable drawble = new AsyncDrawable(task);
			imageView.setImageDrawable(drawble);
			task.execute(url);
		}
	}

	private boolean cancelPotentialWork(String url, ImageView imageView) {
		final BitmapWorkerTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.mUrl;
			if (!url.equals(bitmapUrl)) {
				bitmapDownloaderTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	private BitmapWorkerTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				AsyncDrawable downloadedDrawable = (AsyncDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	static Bitmap loadImageUrl(String imageUrl, int width, int height) {
		return CropUtil.getScaledBitmapFromUrl(imageUrl, width, height, 1);
	}
	
	static Bitmap loadImage(String filePath, int width, int height, int sampleRat) {
		Bitmap result = null;
		try {
			final Options option = new Options();
			if(sampleRat <= 1) {
				option.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filePath, option);
				option.inSampleSize = CropUtil.calculateInSampleSize(option, width, height);
			} else {
				option.inSampleSize = sampleRat;
			}

			sampleRat = option.inSampleSize;

			option.inJustDecodeBounds = false;
			option.inDither = false;
			result = BitmapFactory.decodeFile(filePath, option);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			System.gc();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				Dlog.e(TAG, e1);
			}
			return loadImage(filePath, width, height, sampleRat * 2);
		}
		return result;
	}

	/***
	 * 이미지를 회전시키는 함수..
	 *
	 * @param bitmap
	 * @param rotate
	 * @return
	 */
	synchronized public static Bitmap rotate(Bitmap bitmap, int rotate) {
		if (bitmap == null || bitmap.isRecycled())
			return bitmap;

		Matrix matrix = new Matrix();
		matrix.setRotate(rotate);

		return createBitmapWithMatrix(bitmap, matrix);
	}

	private static Bitmap createBitmapWithMatrix(Bitmap bitmap, Matrix matrix) {
		if (bitmap == null || bitmap.isRecycled())
			return bitmap;

		Bitmap converted = null;
		try {
			converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			if (converted != null && bitmap != converted) {
				bitmap.recycle();
				bitmap = null;
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
		return converted;
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> mImageViewReference;
		private final WeakReference<ProgressBar> mProgressBarReference;
		private int mWidth;
		private int mHeight;
		private CropInfo mCropinfo;
		public String mUrl;
		public int mAngle;
		private final OnLoadComplete mListener;

		public BitmapWorkerTask(String url, ImageView imageView, ProgressBar progressbar, int width, int height, int angle, OnLoadComplete listener) {
			this.mImageViewReference = new WeakReference<ImageView>(imageView);
			this.mWidth = width;
			this.mHeight = height;
			this.mUrl = url;
			this.mProgressBarReference = new WeakReference<ProgressBar>(progressbar);
			this.mAngle = angle;
			this.mListener = listener;
			this.mCropinfo = null;
		}

		public BitmapWorkerTask(String url, ImageView imageView, ProgressBar progressbar, int width, int height, int angle, CropInfo cropinfo, OnLoadComplete listener) {
			this.mImageViewReference = new WeakReference<ImageView>(imageView);
			this.mWidth = width;
			this.mHeight = height;
			this.mUrl = url;
			this.mProgressBarReference = new WeakReference<ProgressBar>(progressbar);
			this.mAngle = angle;
			this.mCropinfo = cropinfo;
			this.mListener = listener;
		}

		public BitmapWorkerTask(String url, ImageView imageView, ProgressBar progressbar, int width, int height, int angle) {
			this.mImageViewReference = new WeakReference<ImageView>(imageView);
			this.mWidth = width;
			this.mHeight = height;
			this.mUrl = url;
			this.mProgressBarReference = new WeakReference<ProgressBar>(progressbar);
			this.mAngle = angle;
			this.mListener = null;
			this.mCropinfo = null;
		}

		public BitmapWorkerTask(String url, ImageView imageView, int width, int height) {
			this.mImageViewReference = new WeakReference<ImageView>(imageView);
			this.mWidth = width;
			this.mHeight = height;
			this.mUrl = url;
			this.mProgressBarReference = null;
			this.mAngle = 0;
			this.mListener = null;
			this.mCropinfo = null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mProgressBarReference != null) {
				ProgressBar pb = mProgressBarReference.get();
				if (pb != null)
					pb.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bp = null;

			// 로컬 이미지, 원결 이미지
			if (params[0].startsWith("http://") || params[0].startsWith("https://")) {
				bp = loadImageUrl(params[0], mWidth, mHeight);
			} else {
				// return loadImage(params[0], mWidth, mHeight);
				bp = loadImage(params[0], mWidth, mHeight, 1);
			}

			return bp;

		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
				return;
			}

			if (bitmap != null && mImageViewReference != null) {
				final ImageView iv = mImageViewReference.get();
				final BitmapWorkerTask bTask = getBitmapDownloaderTask(iv);
				if (this == bTask && iv != null) {

					iv.setImageBitmap(null);

					if (mCropinfo != null && CropInfo.CORP_ORIENT.NONE != mCropinfo.cropOrient) {
						bitmap = CropUtil.cropBitmap(mCropinfo, bitmap);
					}

					if (mAngle > 0) {
						Bitmap rotatedBitmap = rotate(bitmap, mAngle);
						if(bitmap != rotatedBitmap) {
							if(bitmap != null && !bitmap.isRecycled()) {
								bitmap.recycle();
								bitmap = null;
							}
						}
						bitmap = rotatedBitmap;

//						if (bTask != null && !StringUtil.isEmpty(bTask.mUrl) && !bTask.mUrl.startsWith("http"))
//							bitmap = getFlippedBitmap(bTask.mUrl, bitmap);
					}

					if(bitmap !=  null && !bitmap.isRecycled()) {
						String genKey = getgenerationKey(bTask.mUrl, bTask.mAngle);
						if(genKey != null)
							mImageCache.put(genKey, bitmap);

						int w = bitmap.getWidth();
						int h = bitmap.getHeight();
						iv.setImageBitmap(bitmap);
						if (this.mListener != null)
							mListener.onComplete(w, h);
					} else {
						if (this.mListener != null)
							mListener.onFailedLoad();
					}
				}
			}

			if (mProgressBarReference != null) {
				ProgressBar pb = mProgressBarReference.get();
				if (pb != null)
					pb.setVisibility(View.GONE);
			}
		}

	}

	static class AsyncDrawable extends ColorDrawable {
		private final WeakReference<BitmapWorkerTask> mBitmapWorkerTaskReference;

		public AsyncDrawable(BitmapWorkerTask task) {
			this.mBitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(task);
		}

		public BitmapWorkerTask getBitmapDownloaderTask() {
			return mBitmapWorkerTaskReference.get();
		}
	}
}