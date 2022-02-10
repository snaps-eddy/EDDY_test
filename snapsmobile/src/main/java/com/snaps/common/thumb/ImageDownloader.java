package com.snaps.common.thumb;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class ImageDownloader {
	public static MemoryCache mImageCache = new MemoryCache();

	/**
	 * 이미지 다운로더
	 *
	 * @param url
	 * @param imageView
	 * @param defaultImage
	 * @param thumbnailFlag
	 */
	public static void download(Context context, String url, ImageView imageView, int defaultImage, boolean thumbnailFlag) {
		Bitmap cachedImage = null;
		cachedImage = mImageCache.get(url);

		if (cachedImage != null) {
			imageView.setImageBitmap(cachedImage);
		} else if (cancelPotentialDownload(url, imageView)) {
			ImageDownloaderTask task = new ImageDownloaderTask(url, imageView, thumbnailFlag, context);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task, context.getResources(), BitmapFactory.decodeResource(context.getResources(), defaultImage));

			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}

	private static boolean cancelPotentialDownload(String url, ImageView imageView) {
		ImageDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if (!url.equals(bitmapUrl)) {
				bitmapDownloaderTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	private static ImageDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	static class DownloadedDrawable extends BitmapDrawable {
		private final WeakReference<ImageDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(ImageDownloaderTask bitmapDownloaderTask, Resources res, Bitmap bitmap) {
			super(res, bitmap);
			bitmapDownloaderTaskReference = new WeakReference<ImageDownloaderTask>(bitmapDownloaderTask);
		}

		public ImageDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	public interface ImageDownloaderListener {
		void startDownload();

		void completeDownload(ImageView imageView, Bitmap bitmap);
	}
}
