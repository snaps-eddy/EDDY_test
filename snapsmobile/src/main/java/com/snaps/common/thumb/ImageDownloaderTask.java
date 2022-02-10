package com.snaps.common.thumb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.snaps.common.thumb.ImageDownloader.DownloadedDrawable;
import com.snaps.common.thumb.ImageDownloader.ImageDownloaderListener;
import com.snaps.common.utils.media.MediaImage;

import java.lang.ref.WeakReference;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	public String url;
	public String targetUrl;
	private boolean thumbnailFlag;
	private WeakReference<ImageView> imageViewReference;
	private ImageDownloaderListener mListener;
	private Context context;

	public ImageDownloaderTask(String url, ImageView imageView, boolean thumbnailFlag, Context context) {
		this.targetUrl = url;
		this.imageViewReference = new WeakReference<ImageView>(imageView);
		this.thumbnailFlag = thumbnailFlag;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mListener != null) {
			mListener.startDownload();
		}
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		return downloadBitmap(context, params[0], thumbnailFlag);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (bitmap == null) {
			if (mListener != null) {
				mListener.completeDownload(null, null);
			}
			return;
		}

		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			ImageDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

			if (this == bitmapDownloaderTask) {
				ImageDownloader.mImageCache.put(targetUrl, bitmap);
				imageView.setImageBitmap(bitmap);
				if (mListener != null) {
					mListener.completeDownload(imageView, bitmap);
				}
			}
		}
	}

	private ImageDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	static Bitmap downloadBitmap(Context context, String url, boolean thumbnailFlag) {
		Bitmap cacheDirImage = MediaImage.getImageMiniThumbnailPath(context, Long.parseLong(url));

		return cacheDirImage;
	}
}
