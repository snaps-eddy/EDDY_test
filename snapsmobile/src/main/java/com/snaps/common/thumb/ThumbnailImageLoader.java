package com.snaps.common.thumb;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.media.MediaImage;
import com.snaps.mobile.R;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThumbnailImageLoader {
	private static final String TAG = ThumbnailImageLoader.class.getSimpleName();
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Handler handler = new Handler();// handler to display images in UI thread

	Context context = null;
	Bitmap defaultBitmap = null;

	public ThumbnailImageLoader(Context context) {
		executorService = Executors.newFixedThreadPool(5);
		this.context = context;
		final int stub_id = R.drawable.img_default_pic_bottom;
		defaultBitmap = CropUtil.getInSampledDecodeBitmapFromResource(context.getResources(),
				stub_id, 150, 150);
	}

	public void destroy() {
		if (defaultBitmap != null && !defaultBitmap.isRecycled()) {
			defaultBitmap.recycle();
			defaultBitmap = null;
		}
	}

	public void displayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = ImageDownloader.mImageCache.get(url);
		if (bitmap != null && !bitmap.isRecycled()) {
			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(url, imageView);

			if (imageView != null && context != null) {
				imageView.setVisibility(View.INVISIBLE);

				if (defaultBitmap != null && !defaultBitmap.isRecycled()) {
					imageView.setImageBitmap(defaultBitmap);
				}

				imageView.setVisibility(View.VISIBLE);
			}
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {

		return MediaImage.getImageMiniThumbnailPath(context, Long.parseLong(url));
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad)) {
					return;
				}
				Bitmap bmp = getBitmap(photoToLoad.url);
				ImageDownloader.mImageCache.put(photoToLoad.url, bmp);
				if (imageViewReused(photoToLoad)) {
					return;
				}
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				Dlog.e(TAG, th);
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url)) {
			return true;
		}
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad)) {
				return;
			}
			if (bitmap != null && !bitmap.isRecycled()) {
				photoToLoad.imageView.setImageBitmap(bitmap);
			} else {
				if (defaultBitmap != null && !defaultBitmap.isRecycled()) {
					photoToLoad.imageView.setImageBitmap(defaultBitmap);
				}
			}
		}
	}

	public void clearCache() {
	}

}
