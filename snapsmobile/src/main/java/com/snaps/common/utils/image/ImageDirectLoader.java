package com.snaps.common.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.UIUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDirectLoader {
	private static final String TAG = ImageDirectLoader.class.getSimpleName();

	public static void loadImageFitScreenWidth(final Context context, final String imgUrl, final ImageView imageView) {
		int screenWidth = UIUtil.getScreenWidth(context);
		loadImageFitWidth(context, imgUrl, imageView, screenWidth);
	}

	public static void loadImageFitWidth(final Context context, final String imgUrl, final ImageView imageView, final int fitWidth) {
		ATask.executeVoid(new ATask.OnTaskBitmap() {
			@Override
			public void onPre() {
			}

			@Override
			public Bitmap onBG() {
				Bitmap resBitmap = getUrlBitmap(imgUrl, -1, null);
				return resBitmap;
			}

			@Override
			public void onPost(Bitmap resBitmap) {
				if (resBitmap != null) {
					float width = resBitmap.getWidth();
					float height = resBitmap.getHeight();
					if (width < fitWidth) {
						float percente = (float) (width / 100);
						float scale = (float) (fitWidth / percente);
						width *= (scale / 100);
						height *= (scale / 100);
					}
					Bitmap tmp = Bitmap.createScaledBitmap(resBitmap, (int) width, (int) height, true);
					if (tmp != resBitmap) {
						if(resBitmap != null && !resBitmap.isRecycled()) {
							resBitmap.recycle();
							resBitmap = null;
						}
					}
					
					resBitmap = tmp;
					
					if(resBitmap != null && !resBitmap.isRecycled())
						imageView.setImageBitmap(resBitmap);
				}
			}
		});
	}

	public static void loadImage(final String imgUrl, final ImageView imageView) {
		loadImage(imgUrl, imageView, -1, null, -1, null);
	}

	public static void loadImage(final String imgUrl, final ImageView imageView, final int thumbSize) {
		loadImage(imgUrl, imageView, thumbSize, null, -1, null);
	}

	public static void loadImage(final String imgUrl, final ImageView imageView, final int thumbSize, final String prefix) {
		loadImage(imgUrl, imageView, thumbSize, prefix, -1, null);
	}

	public static void loadImage(final String imgUrl, final ImageView imageView, final int thumbSize, final String prefix, int rotation) {
		loadImage(imgUrl, imageView, thumbSize, prefix, rotation, null);
	}

	public static void loadImage(final String imgUrl, final ImageView imageView, final int thumbSize, final String prefix, ProgressBar prog) {
		loadImage(imgUrl, imageView, thumbSize, prefix, -1, prog);
	}

	public static void loadImage(final String imgUrl, final ImageView imageView, final int thumbSize, final String prefix, final int rotation, final ProgressBar prog) {
		loadImage(imgUrl, imageView, thumbSize, prefix, rotation, prog, null);
	}

	public static void loadImage(final String imgUrl, final ImageView imageView, final int thumbSize, final String prefix, final int rotation, final ProgressBar prog, final OnLoadComplete onLoadComp) {
		ATask.executeVoid(new ATask.OnTaskBitmap() {
			@Override
			public void onPre() {
				if (prog != null)
					prog.setVisibility(View.VISIBLE);
			}

			@Override
			public Bitmap onBG() {
				Bitmap resBitmap = null;
				if (prefix == null) {
					resBitmap = getUrlBitmap(imgUrl, thumbSize, null);
				} else {
					String localPath = prefix + Integer.toString(imgUrl.hashCode());//Uri.parse(imgUrl).getLastPathSegment();
					if (new File(localPath).exists())
						resBitmap = getLocalPathBitmapLoad(localPath, thumbSize);
					else
						resBitmap = getUrlBitmap(imgUrl, thumbSize, localPath);
				}
				return resBitmap;
			}

			@Override
			public void onPost(Bitmap resBitmap) {
				if (prog != null)
					prog.setVisibility(View.GONE);

				if (resBitmap != null && !resBitmap.isRecycled()) {
					if (rotation > -1)
						resBitmap = rotate(resBitmap, rotation);

					if(resBitmap != null && !resBitmap.isRecycled()) {
						imageView.setImageBitmap(resBitmap);
						
						if (onLoadComp != null)
							onLoadComp.onComplete(resBitmap.getWidth(), resBitmap.getHeight());
					} else {
						if (onLoadComp != null)
							onLoadComp.onFailedLoad();
					}
				} else {
					if (onLoadComp != null)
						onLoadComp.onFailedLoad();
				}
			}
		});
	}

	public static Bitmap getImageBitmap(String imgUrl, int thumbSize, String prefix) {
		Bitmap resBitmap = null;
		String localPath = prefix + Integer.toString(imgUrl.hashCode());//Uri.parse(imgUrl).getLastPathSegment();
		if (new File(localPath).exists())
			resBitmap = getLocalPathBitmapLoad(localPath, thumbSize);
		else
			resBitmap = getUrlBitmap(imgUrl, thumbSize, localPath);
		return resBitmap;
	}

	public interface OnLoadComplete {
		public void onComplete(int width, int height);
		public void onFailedLoad();
	}

	public static void loadLocalImage(final String imgUrl, final ImageView imageView, final int thumbSize, final int rotation, final ProgressBar prog, final OnLoadComplete onLoadComp) {
		ATask.executeVoid(new ATask.OnTaskBitmap() {
			@Override
			public void onPre() {
				if (prog != null)
					prog.setVisibility(View.VISIBLE);
			}

			@Override
			public Bitmap onBG() {
				Bitmap resBitmap = null;
				if (new File(imgUrl).exists())
					resBitmap = getLocalPathBitmap(imageView.getContext(), imgUrl, thumbSize);

				return resBitmap;
			}

			@Override
			public void onPost(Bitmap resBitmap) {
				if (prog != null)
					prog.setVisibility(View.GONE);

				if (resBitmap != null && !resBitmap.isRecycled()) {
					if (rotation > 0)
						resBitmap = rotate(resBitmap, rotation);

					if(resBitmap != null && !resBitmap.isRecycled()) {
						imageView.setImageBitmap(resBitmap);
						if (onLoadComp != null)
							onLoadComp.onComplete(resBitmap.getWidth(), resBitmap.getHeight());
					} else {
						if (onLoadComp != null)
							onLoadComp.onFailedLoad();
					}
				} else {
					if (onLoadComp != null)
						onLoadComp.onFailedLoad();
				}
			}
		});
	}

	public static void loadLocalImageAndCrop(final String imgUrl, final ImageView imageView, final Rect cropRect, final int thumbWidth, final int thumbHeight, final String thumbPath) {
		ATask.executeVoid(new ATask.OnTaskBitmap() {
			@Override
			public void onPre() {

			}

			@Override
			public Bitmap onBG() {
				Bitmap resBitmap = null;

				// 먼저 파일이 있는지 확인을 한다. 없으면 로직을 태운다.
				if (new File(thumbPath).exists()) {
					resBitmap = getLocalPathBitmap(imageView.getContext(), thumbPath, -1);
					return resBitmap;
				}

				if (new File(imgUrl).exists()) {
					resBitmap = getLocalPathBitmap(imageView.getContext(), imgUrl, -1);
					// return resBitmap;
				}
				// 크롭영역만큼 잘라낸다.
				resBitmap = Bitmap.createBitmap(resBitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());
				// 리스트에 맞게 리사이즈를 한다.
				resBitmap = Bitmap.createScaledBitmap(resBitmap, thumbWidth, thumbHeight, false);

				// 캐쉬를 하기 위해 파일로 저장을 한다.
				saveBitmap(resBitmap, thumbPath);

				return resBitmap;
			}

			@Override
			public void onPost(Bitmap resBitmap) {
				if (resBitmap != null) {
					// if (rotation > 0)
					// resBitmap = rotate(resBitmap, rotation);

					imageView.setImageBitmap(resBitmap);
				}
			}
		});
	}

	public static void loadAllImage(Context context, int rotation, String imgUrl, final ImageView imageView, final int thumbSize, final ProgressBar prog, final OnLoadComplete onLoadComp) {
		String prefix = null;
		if (imgUrl.startsWith("http://") || imgUrl.startsWith("https://")) {// url
																			// 파일
			prefix = Const_VALUE.PATH_IMAGESELECT_DETAIL(context);
			loadImage(imgUrl, imageView, thumbSize, prefix, rotation, prog, onLoadComp);
		} else {// local 파일

			loadLocalImage(imgUrl, imageView, thumbSize, rotation, prog, onLoadComp);

		}
	}

	public static Bitmap rotate(Bitmap curBitmap, int setRotate) {
		if (curBitmap == null)
			return null;

		Matrix matrix = new Matrix();
		matrix.setRotate(setRotate);// 첫 이미지 로딩 시 회전된 이미지라면 회전함.

		try {
			Bitmap converted = Bitmap.createBitmap(curBitmap, 0, 0, curBitmap.getWidth(), curBitmap.getHeight(), matrix, true);
			if (curBitmap != converted && curBitmap != null && !curBitmap.isRecycled()) {
				curBitmap.recycle();
				curBitmap = converted;
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
		return curBitmap;
	}

	/**
	 * local 파일을 caching 없이 직접로딩
	 * 
	 * @param context
	 * @param filePath
	 * @param thumbSize
	 * @return
	 */
	public static Bitmap getLocalPathBitmap(Context context, String filePath, int thumbSize) {
		return getLocalPathBitmap(context, filePath, thumbSize, null);
	}

	public static Bitmap getLocalPathBitmap(Context context, String filePath, int thumbSize, String saveFilePath) {
		Bitmap imgBitmap = null;
		try {
			if (thumbSize > -1) {
				return getArtworkSampled(filePath, thumbSize, thumbSize);
			} else
				imgBitmap = BitmapFactory.decodeFile(filePath);

			if (imgBitmap != null && saveFilePath != null)
				saveBitmap(imgBitmap, saveFilePath);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);

			if (filePath != null) {
				try {
//					// 메모리 에러가 날 정도의 이미지라면 샘플링한다.
					return getArtworkSampled(filePath, thumbSize, thumbSize);
				} catch (Exception e1) {
					Dlog.e(TAG, e1);
				}
			}
		}
		return imgBitmap;
	}

	public static Bitmap getLocalPathBitmapLoad(String filePath, int thumbSize) {
		return getLocalPathBitmapLoad(filePath, thumbSize, null);
	}

	public static Bitmap getLocalPathBitmapLoad(String filePath, int thumbSize, String saveFilePath) {
		Bitmap imgBitmap = null;
		try {
			if (thumbSize > -1) {
				imgBitmap = getArtworkSampled(filePath, thumbSize, thumbSize);
			} else
				imgBitmap = BitmapFactory.decodeFile(filePath);

			if (imgBitmap != null && saveFilePath != null)
				saveBitmap(imgBitmap, saveFilePath);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);

			if (filePath != null) {
				try {
					// 메모리 에러가 날 정도의 이미지라면 샘플링한다.
					return getArtworkSampled(filePath, thumbSize, thumbSize);
				} catch (Exception e1) {
					Dlog.e(TAG, e1);
				}
			}
		}
		return imgBitmap;
	}

	public static Bitmap getLocalPathBitmapSampled(Context context, String filePath, int thumbSize, String saveFilePath) {
		Bitmap imgBitmap = null;
		try {
			if (thumbSize > -1) {
				imgBitmap = getArtworkSampled(filePath, thumbSize, thumbSize);
			} else
				imgBitmap = BitmapFactory.decodeFile(filePath);

			if (imgBitmap != null && saveFilePath != null)
				saveBitmap(imgBitmap, saveFilePath);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return imgBitmap;
	}

	/**
	 * network url 파일을 caching 없이 직접로딩
	 * 
	 * @param imgurl
	 * @param thumbSize
	 * @return
	 */
	public static Bitmap getUrlBitmap(String imgurl, int thumbSize, String saveFilePath) {
		Bitmap imgBitmap = null;
		FlushedInputStream fis = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(imgurl);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			fis = new FlushedInputStream(conn.getInputStream());

			if (thumbSize > -1)
				imgBitmap = CropUtil.getScaledBitmapFromUrl(imgurl, thumbSize, thumbSize, 1);
			else
				imgBitmap = BitmapFactory.decodeStream(fis);

			if (imgBitmap != null && saveFilePath != null)
				saveBitmap(imgBitmap, saveFilePath);

			fis.close();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);

			if (fis != null) {
				// 메모리 에러가 날 정도의 이미지라면 샘플링한다.
				return CropUtil.getScaledBitmapFromUrl(imgurl, thumbSize, thumbSize, 1);
			}
		} finally {
			try {
				if(conn != null)
					conn.disconnect();
				
				if(fis != null)
					fis.close();
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		}
		
		return imgBitmap;
	}

	static void saveBitmap(Bitmap bm, String filePath) {
		try {
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(file);
			bm.compress(CompressFormat.PNG, 95, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public static boolean saveBitmapBGWhiteForJPG(Bitmap bm, String filePath) {
		try {
			Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bm, 0, 0, null);

			File file = new File(filePath);
			file.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(file);
			newBitmap.compress(CompressFormat.JPEG, 95, fos);
			newBitmap.recycle();
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return false;
	}

	/**
	 * Image Stream sampling Bitmap
	 * 
	 * @param w
	 * @param h
	 * @return
	 */

	public static Bitmap getArtworkSampled(String filePath, int w, int h) {
		return CropUtil.getScaledBitmapFromStream(filePath, w, h, 2);
	}

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}
