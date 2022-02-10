package com.snaps.common.utils.imageloader.filters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.view.Display;
import android.view.WindowManager;

import com.snaps.common.R;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ContextUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import static com.snaps.common.utils.imageloader.ImageLoader.MAX_DOWN_SAMPLE_RATIO;

/**
 * Created by kpbird on 27/08/13.
 */

// https://github.com/kpbird/Android-Image-Filters/blob/master/ImageEffect/src/main/java/com/kpbird/imageeffect/ImageFilters.java

public class ImageFilters {
	private static final String TAG = ImageFilters.class.getSimpleName();
	public static final int PREVIEW_SAMPLE_SIZE = 260;

	public static int getImageEditPreviewBitmapSize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int screenWidth = displayWidth > displayHeight ? displayHeight : displayWidth;
		return Math.max((int) (screenWidth / 2.3f), PREVIEW_SAMPLE_SIZE);
	}

	public static String getExportFileName(Context context, String imgName, String imgSqn, String effectType) {

		String szFileName = imgName;

		String ext = ".jpg";

		if (szFileName != null && szFileName.contains(".")) {
			ext = szFileName.substring(szFileName.lastIndexOf("."));
		}

		if (szFileName != null && szFileName.contains(".")) {
			szFileName = szFileName.substring(0, szFileName.indexOf("."));
		}

		// 폴더 생성. TODO 겔러리에 안 나오게 하려고 캐시 폴더에 넣었는데, 문제가 된다면, 수정이 필요한 부분.
		com.snaps.common.utils.constant.Config.setEFFECT_APPLIED_IMG_SAVE_PATH(com.snaps.common.utils.constant.Config.getExternalCacheDir(context) + "/snaps/effect/");

		File tempSavePath = new File(com.snaps.common.utils.constant.Config.getEFFECT_APPLIED_IMG_SAVE_PATH());

		// thumb path 폴더가 없으면 만든다.
		if (!tempSavePath.exists()) {
			tempSavePath.mkdirs();
		}

		return com.snaps.common.utils.constant.Config.getEFFECT_APPLIED_IMG_SAVE_PATH() + szFileName + "_" + effectType + "_" + imgSqn + ext;
	}

	public static String getAppliedEffectImgFilePath(Context context, Bitmap bmp, String fileName) {
		return getAppliedEffectImgFilePath(context, bmp, fileName, true);
	}

	public static String getAppliedEffectImgFilePath(Context context, Bitmap bmp, String fileName, boolean isRecycle) {

		if (bmp == null || bmp.isRecycled()) {
			return "";
		}

		if (com.snaps.common.utils.constant.Config.isDevelopVersion()) {
			fileName = fileName.replace("com.snaps.mobile.kr/", "com.snaps.mobile.kr.develop/");
		} else {
			fileName = fileName.replace("com.snaps.mobile.kr.develop/", "com.snaps.mobile.kr/");
		}
		String resultFileName = fileName;

		try {

			if (context != null && (com.snaps.common.utils.constant.Config.getEFFECT_APPLIED_IMG_SAVE_PATH() == null || com.snaps.common.utils.constant.Config.getEFFECT_APPLIED_IMG_SAVE_PATH().length() < 1)) {
				com.snaps.common.utils.constant.Config.setEFFECT_APPLIED_IMG_SAVE_PATH(com.snaps.common.utils.constant.Config.getExternalCacheDir(context) + "/snaps/effect/");
			}

			File tempSavePath = new File(com.snaps.common.utils.constant.Config.getEFFECT_APPLIED_IMG_SAVE_PATH());

			// thumb path 폴더가 없으면 만든다.
			if (!tempSavePath.exists() && !tempSavePath.mkdirs()) {
				return "";
			}

			tempSavePath = new File(fileName);

			if (tempSavePath.exists()) {
				tempSavePath.delete();
			}

//            if (!tempSavePath.exists()) {
			tempSavePath.createNewFile();
			tempSavePath.setWritable(true);
			tempSavePath.setReadable(true);

			String ext = ".jpg";

			if (fileName != null && fileName.contains(".")) {
				ext = fileName.substring(fileName.lastIndexOf("."));
			}

			FileOutputStream fos = new FileOutputStream(tempSavePath);
			try {
				if (ext.equals(".png")) {
					bmp.compress(Bitmap.CompressFormat.PNG, 95, fos);
				} else {
					bmp.compress(Bitmap.CompressFormat.JPEG, 95, fos);
				}
			} catch (Exception e) {
				Dlog.e(TAG, e);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
//            }

			if (isRecycle) {
				bmp.recycle();
				bmp = null;
			}

		} catch (Exception e) {
			Dlog.e(TAG, e);
			resultFileName = "";
		}

		return resultFileName;
	}

	public static Bitmap getEffectAppliedBitmap(Context context, ImageEffectBitmap.EffectType type, Bitmap bmp) {

		if (bmp == null || bmp.isRecycled()) {
			return null;
		}

		if (type == null) {
			return bmp;
		}
		Config rgbConfig = null;
		Bitmap bmCopyied = null;
//        if(type == ImageEffectBitmap.EffectType.SHARPEN || type == ImageEffectBitmap.EffectType.GRAY_SCALE || type == ImageEffectBitmap.EffectType.SEPHIA) {
//            rgbConfig = Bitmap.Config.ARGB_8888;
//            bmCopyied = bmp.copy(rgbConfig, true); //565를 4444로 컨버팅할수 없다.
//        }  else
		if (type == ImageEffectBitmap.EffectType.SNOW || type == ImageEffectBitmap.EffectType.AURORA || type == ImageEffectBitmap.EffectType.WARM) {
			Bitmap newBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), rgbConfig);
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bmp, 0, 0, null);
			bmCopyied = newBitmap;
		} else {
			rgbConfig = Bitmap.Config.ARGB_8888;
//            rgbConfig = Config.RGB_565;
			bmCopyied = bmp.copy(rgbConfig, true); //565를 4444로 컨버팅할수 없다.

		}

//        Bitmap bmCopyied = bmp.copy(rgbConfig, true); //565를 4444로 컨버팅할수 없다.

		ImageFilters imgFilter = new ImageFilters();

		try {
			switch (type) {
				case SHARPEN:
					return imgFilter.applySharpenEffect(bmCopyied, 12d);
				case GRAY_SCALE:
					return imgFilter.applyGrayscaleEffect(bmCopyied);
				case SEPHIA:
					return imgFilter.applySephiaEffect(bmCopyied);
				case AMERALD:
					// return imgFilter.applyEmeraldEffect(bmCopyied);
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_emeralde, PorterDuff.Mode.SCREEN, 100, bmCopyied);
				case WARM:
					// return imgFilter.applyWarmEffect(bmCopyied);
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_warm, PorterDuff.Mode.MULTIPLY, 100, bmCopyied);
				case BLACK_CAT:
					return imgFilter.applyBlackCatEffect(context, bmCopyied);
				case DAWN:
					return imgFilter.applyClamEffect(context, bmCopyied);
				// return imgFilter.applyDawnEffect(bmCopyied);
				case FILM:
					return imgFilter.applyFilmCameraEffect(context, bmCopyied);
				case VINTAGE:
					// return imgFilter.applyVintageEffect(bmCopyied);
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_vintage, PorterDuff.Mode.SCREEN, 100, bmCopyied);
				case SNOW:
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_snow, PorterDuff.Mode.SCREEN, 100, bmCopyied);
				case WATER:
					// return imgFilter.applyVintageEffect(bmCopyied);
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_water, PorterDuff.Mode.SCREEN, 100, bmCopyied);
				case WINTER:
					// return imgFilter.applyVintageEffect(bmCopyied);
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_winter, PorterDuff.Mode.SCREEN, 100, bmCopyied);
				case BOKE:
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_boke, PorterDuff.Mode.OVERLAY, 100, bmCopyied);
				case SHADY:
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_shady, PorterDuff.Mode.OVERLAY, 100, bmCopyied);
				case OLD_LIGHT:
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_old_light, PorterDuff.Mode.LIGHTEN, 100, bmCopyied);
				case SHINY:
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_shiny, PorterDuff.Mode.SCREEN, 100, bmCopyied);
				case AURORA:
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_aurora, PorterDuff.Mode.MULTIPLY, 65, bmCopyied);
				case MEMORY:
					return imgFilter.applyBlandEffect(context, R.drawable.img_filter_momory, PorterDuff.Mode.SCREEN, 100, bmCopyied);
				default:
					return bmCopyied;
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return bmCopyied;
	}

//	public Bitmap applyHighlightEffect(Bitmap src) {
//
//		// create new bitmap, which will be painted and becomes result image
//		Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + 96, src.getHeight() + 96, Config.ARGB_8888);
//		// setup canvas for painting
//		Canvas canvas = new Canvas(bmOut);
//		// setup default color
//		canvas.drawColor(0, Mode.CLEAR);
//
//		// create a blur paint for capturing alpha
//		Paint ptBlur = new Paint();
//		ptBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
//		int[] offsetXY = new int[2];
//		// capture alpha into a bitmap
//		Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
//		// create a color paint
//		Paint ptAlphaColor = new Paint();
//		ptAlphaColor.setColor(0xFFFFFFFF);
//		// paint color for captured alpha region (bitmap)
//		canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
//		// free memory
//		bmAlpha.recycle();
//
//		// paint the image source
//		canvas.drawBitmap(src, 0, 0, null);
//
//		// return out final image
//		return bmOut;
//	}

//	public Bitmap applyInvertEffect(Bitmap src) {
//		// create new bitmap with the same settings as source bitmap
//		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
//		// color info
//		int A, R, G, B;
//		int pixelColor;
//		// image size
//		int height = src.getHeight();
//		int width = src.getWidth();
//
//		// scan through every pixel
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				// get one pixel
//				pixelColor = src.getPixel(x, y);
//				// saving alpha channel
//				A = Color.alpha(pixelColor);
//				// inverting byte for each R/G/B channel
//				R = 255 - Color.red(pixelColor);
//				G = 255 - Color.green(pixelColor);
//				B = 255 - Color.blue(pixelColor);
//				// set newly-inverted pixel to output image
//				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//			}
//		}
//
//		// return final bitmap
//		return bmOut;
//	}

//	public Bitmap applySaturationValue(Bitmap src, float settingSat) {
//
//		int w = src.getWidth();
//		int h = src.getHeight();
//
//		Bitmap bitmapResult =
//				Bitmap.createBitmap(w, h, Config.ARGB_8888);
//		Canvas canvasResult = new Canvas(bitmapResult);
//		Paint paint = new Paint();
//		ColorMatrix colorMatrix = new ColorMatrix();
//		colorMatrix.setSaturation(settingSat);
//		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
//		paint.setColorFilter(filter);
//		canvasResult.drawBitmap(src, 0, 0, paint);
//
//		return bitmapResult;
//	}

//    public Bitmap applyGrayscaleEffect(Bitmap src) {
//    	if(src == null || src.isRecycled()) return null;
//    	try {
//    		  // constant factors
//            final double GS_RED = 11;
//            final double GS_GREEN = 16;
//            final double GS_BLUE = 5;
//
//            // create output bitmap
//            Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
//            // pixel information
//            int A, R, G, B;
//            int pixel;
//
//            // get image size
//            int width = src.getWidth();
//            int height = src.getHeight();
//
//            float[] HSV = new float[3];
//
//            // scan through every single pixel
//            for (int x = 0; x < width; ++x) {
//                for (int y = 0; y < height; ++y) {
//                    // get one pixel color
//                    pixel = src.getPixel(x, y);
//                    // retrieve color of all channels
//
//                    Color.colorToHSV(pixel, HSV);
//                    // increase Saturation level
//                    HSV[1] *= (1.0f - .3f);
//                    HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
//
//                    pixel = Color.HSVToColor(HSV);
//
//                    A = Color.alpha(pixel);
//                    R = Color.red(pixel);
//                    G = Color.green(pixel);
//                    B = Color.blue(pixel);
//                    // take conversion up to one single value
//                    R = G = B = (int) ((GS_RED * R + GS_GREEN * G + GS_BLUE * B)/32);
//                    // set new pixel color to output bitmap
//                    bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//                }
//            }
//
//            // return final image
//            return bmOut;
//		} catch (OutOfMemoryError e) {
//			Dlog.e(TAG, e);
//			return null;
//		}
//    }

	public Bitmap applyGrayscaleEffect(Bitmap src) {
		if (src == null || src.isRecycled()) {
			return null;
		}
		try {
			// create output bitmap
			Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

			Canvas c = new Canvas(bmOut);
			Paint paint = new Paint();
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
			paint.setColorFilter(f);
			c.drawBitmap(src, 0, 0, paint);

			return bmOut;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}
	}

	//Gamma Image (R, G, B) = (1.8, 1.8, 1.8)
//	public Bitmap applyGammaEffect(Bitmap src, double red, double green, double blue) {
//		// create output image
//		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
//		// get image size
//		int width = src.getWidth();
//		int height = src.getHeight();
//		// color information
//		int A, R, G, B;
//		int pixel;
//		// constant value curve
//		final int MAX_SIZE = 256;
//		final double MAX_VALUE_DBL = 255.0;
//		final int MAX_VALUE_INT = 255;
//		final double REVERSE = 1.0;
//
//		// gamma arrays
//		int[] gammaR = new int[MAX_SIZE];
//		int[] gammaG = new int[MAX_SIZE];
//		int[] gammaB = new int[MAX_SIZE];
//
//		// setting values for every gamma channels
//		for (int i = 0; i < MAX_SIZE; ++i) {
//			gammaR[i] = (int) Math.min(MAX_VALUE_INT,
//					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
//			gammaG[i] = (int) Math.min(MAX_VALUE_INT,
//					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
//			gammaB[i] = (int) Math.min(MAX_VALUE_INT,
//					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
//		}
//
//		// apply gamma table
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				// get pixel color
//				pixel = src.getPixel(x, y);
//				A = Color.alpha(pixel);
//				// look up gamma
//				R = gammaR[Color.red(pixel)];
//				G = gammaG[Color.green(pixel)];
//				B = gammaB[Color.blue(pixel)];
//				// set new color to output bitmap
//				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//			}
//		}
//
//		// return final image
//		return bmOut;
//	}

//	public Bitmap applyColorFilterEffect(Bitmap src, double red, double green, double blue) {
//		// image size
//		int width = src.getWidth();
//		int height = src.getHeight();
//		// create output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//		// color information
//		int A, R, G, B;
//		int pixel;
//
//		// scan through all pixels
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				// get pixel color
//				pixel = src.getPixel(x, y);
//				// apply filtering on each channel R, G, B
//				A = Color.alpha(pixel);
//				R = (int) (Color.red(pixel) * red);
//				G = (int) (Color.green(pixel) * green);
//				B = (int) (Color.blue(pixel) * blue);
//				// set new color pixel to output bitmap
//				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//			}
//		}
//
//		// return final image
//		return bmOut;
//	}

	/**
	 * public Bitmap applyGrayscaleEffect(Bitmap src) {
	 * <p>
	 * // constant factors
	 * final double GS_RED = 11;
	 * final double GS_GREEN = 16;
	 * final double GS_BLUE = 5;
	 * <p>
	 * // create output bitmap
	 * Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
	 * // pixel information
	 * int A, R, G, B;
	 * int pixel;
	 * <p>
	 * // get image size
	 * int width = src.getWidth();
	 * int height = src.getHeight();
	 * <p>
	 * float[] HSV = new float[3];
	 * <p>
	 * // scan through every single pixel
	 * for (int x = 0; x < width; ++x) {
	 * for (int y = 0; y < height; ++y) {
	 * // get one pixel color
	 * pixel = src.getPixel(x, y);
	 * // retrieve color of all channels
	 * <p>
	 * Color.colorToHSV(pixel, HSV);
	 * // increase Saturation level
	 * HSV[1] *= (1.0f - .3f);
	 * HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
	 * <p>
	 * pixel = Color.HSVToColor(HSV);
	 * <p>
	 * A = Color.alpha(pixel);
	 * R = Color.red(pixel);
	 * G = Color.green(pixel);
	 * B = Color.blue(pixel);
	 * // take conversion up to one single value
	 * R = G = B = (int) ((GS_RED * R + GS_GREEN * G + GS_BLUE * B)/32);
	 * // set new pixel color to output bitmap
	 * bmOut.setPixel(x, y, Color.argb(A, R, G, B));
	 * }
	 * }
	 * <p>
	 * // return final image
	 * return bmOut;
	 * }
	 *
	 * @return
	 */

//    public  Bitmap applySephiaEffect(Bitmap src) {
//    	if(src == null || src.isRecycled()) return null;
//
//    	try {
//    	    // constant factors
//            final double GS_RED = 11;
//            final double GS_GREEN = 16;
//            final double GS_BLUE = 5;
//
//        	// image size
//        	int width = src.getWidth();
//        	int height = src.getHeight();
//        	// create output bitmap
//        	Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//        	int A, R, G, B;
//        	int pixel;
//
//        	float[] HSV = new float[3];
//
//        	// scan through all pixels
//        	for(int x = 0; x < width; ++x) {
//        		for(int y = 0; y < height; ++y) {
//        			// get pixel color
//        			pixel = src.getPixel(x, y);
//
//        			Color.colorToHSV(pixel, HSV);
//
//        			// increase Saturation level
//        			HSV[1] *= (1.0f - .3f);
//        			HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
//
//        			pixel = Color.HSVToColor(HSV);
//
//        			A = Color.alpha(pixel);
//        			R = Color.red(pixel);
//        			G = Color.green(pixel);
//        			B = Color.blue(pixel);
//        			// take conversion up to one single value
//        			R = G = B = (int) ((GS_RED * R + GS_GREEN * G + GS_BLUE * B)/32);
//
//        			R += (R * .4);
//        			if(R > 255) { R = 255; }
//
//        			G += (G * .2f);
//        			if(G > 255) { G = 255; }
//
//        			B += (B * .1f);
//        			if(B > 255) { B = 255; }
//
//        			// set new pixel color to output image
//        			bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//        		}
//        	}
//
//        	// return final image
//        	return bmOut;
//		} catch (OutOfMemoryError e) {
//			Dlog.e(TAG, e);
//			return null;
//		}
//    }
	public Bitmap applySephiaEffect(Bitmap src) {
		if (src == null || src.isRecycled()) {
			return null;
		}

		final double GS_RED = 11;
		final double GS_GREEN = 16;
		final double GS_BLUE = 5;

		try {
			// image size
			int width = src.getWidth();
			int height = src.getHeight();
			// create output bitmap
			Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

			Canvas c = new Canvas(bmOut);
			Paint paint = new Paint();
			ColorMatrix colorMatrix_Sepia = new ColorMatrix();
			colorMatrix_Sepia.setSaturation(.3f);

			ColorMatrix sepiaMatrix = new ColorMatrix();

			float[] array = sepiaMatrix.getArray();

			float R = array[0];
			float G = array[6];
			float B = array[12];

			R = G = B = (int) ((GS_RED * R + GS_GREEN * G + GS_BLUE * B) / 32);

			R += (R * .4);
			if (R > 255) {
				R = 255;
			}

			G += (G * .2f);
			if (G > 255) {
				G = 255;
			}

			B += (B * .1f);
			if (B > 255) {
				B = 255;
			}

			array[0] = R;
			array[6] = G;
			array[12] = B;

			sepiaMatrix.set(array);

			colorMatrix_Sepia.postConcat(sepiaMatrix);

			ColorMatrixColorFilter f = new ColorMatrixColorFilter(colorMatrix_Sepia);
			paint.setColorFilter(f);
			c.drawBitmap(src, 0, 0, paint);

			return bmOut;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}
	}

//	public Bitmap applyEmeraldEffect(Bitmap src) {
//		if (src == null || src.isRecycled()) {
//			return null;
//		}
//		try {
//			// image size
//			int width = src.getWidth();
//			int height = src.getHeight();
//			// create output bitmap
//			Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//			int A, R, G, B;
//			int pixel;
//
//			// scan through all pixels
//			for (int x = 0; x < width; ++x) {
//				for (int y = 0; y < height; ++y) {
//					// get pixel color
//					pixel = src.getPixel(x, y);
//					// get color on each channel
//					A = Color.alpha(pixel);
//					R = Color.red(pixel);
//					G = Color.green(pixel);
//					B = Color.blue(pixel);
//
//					R += (R * .5);
//					if (R > 255) {
//						R = 255;
//					}
//
//					G += (G * .7f);
//					if (G > 255) {
//						G = 255;
//					}
//
//					B += (B * .5f);
//					if (B > 255) {
//						B = 255;
//					}
//
//					// set new pixel color to output image
//					bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//				}
//			}
//
//			// return final image
//			return bmOut;
//		} catch (OutOfMemoryError e) {
//			Dlog.e(TAG, e);
//			return null;
//		}
//	}

//	public Bitmap applyWarmEffect(Bitmap src) {
//		if (src == null || src.isRecycled()) {
//			return null;
//		}
//		try {
//			// image size
//			int width = src.getWidth();
//			int height = src.getHeight();
//			// create output bitmap
//			Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//			int A, R, G, B;
//			int pixel;
//
//			// scan through all pixels
//			for (int x = 0; x < width; ++x) {
//				for (int y = 0; y < height; ++y) {
//					// get pixel color
//					pixel = src.getPixel(x, y);
//					// get color on each channel
//					A = Color.alpha(pixel);
//					R = Color.red(pixel);
//					G = Color.green(pixel);
//					B = Color.blue(pixel);
//
//					R += (R * .7f);
//					if (R > 255) {
//						R = 255;
//					}
//
//					G += (G * .5f);
//					if (G > 255) {
//						G = 255;
//					}
//
//					B += (B * .5f);
//					if (B > 255) {
//						B = 255;
//					}
//
//					// set new pixel color to output image
//					bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//				}
//			}
//
//			// return final image
//			return bmOut;
//		} catch (OutOfMemoryError e) {
//			Dlog.e(TAG, e);
//			return null;
//		}
//	}

	public Bitmap applySherpenIntensityEffect(Bitmap src) {
		if (src == null || src.isRecycled()) {
			return null;
		}
		try {
			// image size
			int width = src.getWidth();
			int height = src.getHeight();
			// create output bitmap
			Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
			int A, R, G, B;
			int pixel;

			// scan through all pixels
			for (int x = 0; x < width; ++x) {
				for (int y = 0; y < height; ++y) {
					// get pixel color
					pixel = src.getPixel(x, y);
					// get color on each channel
					A = Color.alpha(pixel);
					R = Color.red(pixel);
					G = Color.green(pixel);
					B = Color.blue(pixel);

					R += (R * .2f);
					if (R > 255) {
						R = 255;
					}

					G += (G * .2f);
					if (G > 255) {
						G = 255;
					}

					B += (B * .2f);
					if (B > 255) {
						B = 255;
					}

					// set new pixel color to output image
					bmOut.setPixel(x, y, Color.argb(A, R, G, B));
				}
			}

			// return final image
			return bmOut;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}
	}

//	public Bitmap applyDawnEffect(Bitmap src) {
//		if (src == null || src.isRecycled()) {
//			return null;
//		}
//		try {
//			// image size
//			int width = src.getWidth();
//			int height = src.getHeight();
//			// create output bitmap
//			Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//			int A, R, G, B;
//			int pixel;
//
//			// scan through all pixels
//			for (int x = 0; x < width; ++x) {
//				for (int y = 0; y < height; ++y) {
//					// get pixel color
//					pixel = src.getPixel(x, y);
//					// get color on each channel
//					A = Color.alpha(pixel);
//					R = Color.red(pixel);
//					G = Color.green(pixel);
//					B = Color.blue(pixel);
//
//					R += (R * .5f);
//					if (R > 255) {
//						R = 255;
//					}
//
//					G += (G * .5f);
//					if (G > 255) {
//						G = 255;
//					}
//
//					B += (B * .7f);
//					if (B > 255) {
//						B = 255;
//					}
//
//					// set new pixel color to output image
//					bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//				}
//			}
//
//			// return final image
//			return bmOut;
//		} catch (OutOfMemoryError e) {
//			Dlog.e(TAG, e);
//			return null;
//		}
//	}

//	public Bitmap applyVintageEffect(Bitmap src) {
//		if (src == null || src.isRecycled()) {
//			return null;
//		}
//		try {
//			// image size
//			int width = src.getWidth();
//			int height = src.getHeight();
//			// create output bitmap
//			Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//			int A, R, G, B;
//			int pixel;
//
//			// scan through all pixels
//			for (int x = 0; x < width; ++x) {
//				for (int y = 0; y < height; ++y) {
//					// get pixel color
//					pixel = src.getPixel(x, y);
//					// get color on each channel
//					A = Color.alpha(pixel);
//					R = Color.red(pixel);
//					G = Color.green(pixel);
//					B = Color.blue(pixel);
//
////        			R -= (R * .1f);
////        			if(R > 255) { R = 255; }
//
//					G -= (G * .1f);
//					if (G > 255) {
//						G = 255;
//					}
//
//					B -= (B * .1f);
//					if (B > 255) {
//						B = 255;
//					}
//
//					// set new pixel color to output image
//					bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//				}
//			}
//
//			bmOut = applyDisSaturationFilter(bmOut, .5f);
//
//			// return final image
//			return bmOut;
//		} catch (OutOfMemoryError e) {
//			Dlog.e(TAG, e);
//			return null;
//		}
//	}

//	public Bitmap applyDisSaturationFilter(Bitmap source, float level) {
//		// get image size
//		int width = source.getWidth();
//		int height = source.getHeight();
//		int[] pixels = new int[width * height];
//		float[] HSV = new float[3];
//		// get pixel array from source
//		source.getPixels(pixels, 0, width, 0, 0, width, height);
//
//		int index = 0;
//		// iteration through pixels
//		for (int y = 0; y < height; ++y) {
//			for (int x = 0; x < width; ++x) {
//				// get current index in 2D-matrix
//				index = y * width + x;
//				// convert to HSV
//				Color.colorToHSV(pixels[index], HSV);
//				// increase Saturation level
//				HSV[1] *= (1.0f - level);
//				HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
//
//				// take color back
//				pixels[index] = Color.HSVToColor(HSV);
////                pixels[index] |= Color.HSVToColor(HSV);
//			}
//		}
//		// output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bmOut;
//	}

//	public Bitmap applyDecreaseColorDepthEffect(Bitmap src, int bitOffset) {
//		// get image size
//		int width = src.getWidth();
//		int height = src.getHeight();
//		// create output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//		// color information
//		int A, R, G, B;
//		int pixel;
//
//		// scan through all pixels
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				// get pixel color
//				pixel = src.getPixel(x, y);
//				A = Color.alpha(pixel);
//				R = Color.red(pixel);
//				G = Color.green(pixel);
//				B = Color.blue(pixel);
//
//				// round-off color offset
//				R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
//				if (R < 0) {
//					R = 0;
//				}
//				G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
//				if (G < 0) {
//					G = 0;
//				}
//				B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
//				if (B < 0) {
//					B = 0;
//				}
//
//				// set pixel color to output bitmap
//				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//			}
//		}
//
//		// return final image
//		return bmOut;
//	}

//	public Bitmap applyContrastEffect(Bitmap src, double value) {
//		// image size
//		int width = src.getWidth();
//		int height = src.getHeight();
//		// create output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//		// color information
//		int A, R, G, B;
//		int pixel;
//		// get contrast value
//		double contrast = Math.pow((100 + value) / 100, 2);
//
//		// scan through all pixels
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				// get pixel color
//				pixel = src.getPixel(x, y);
//				A = Color.alpha(pixel);
//				// apply filter contrast for every channel R, G, B
//				R = Color.red(pixel);
//				R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//				if (R < 0) {
//					R = 0;
//				} else if (R > 255) {
//					R = 255;
//				}
//
//				G = Color.red(pixel);
//				G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//				if (G < 0) {
//					G = 0;
//				} else if (G > 255) {
//					G = 255;
//				}
//
//				B = Color.red(pixel);
//				B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//				if (B < 0) {
//					B = 0;
//				} else if (B > 255) {
//					B = 255;
//				}
//
//				// set new pixel color to output bitmap
//				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//			}
//		}
//
//		// return final image
//		return bmOut;
//	}

//	public Bitmap applyBrightnessEffect(Bitmap src, int value) {
//		// image size
//		int width = src.getWidth();
//		int height = src.getHeight();
//		// create output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//		// color information
//		int A, R, G, B;
//		int pixel;
//
//		// scan through all pixels
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				// get pixel color
//				pixel = src.getPixel(x, y);
//				A = Color.alpha(pixel);
//				R = Color.red(pixel);
//				G = Color.green(pixel);
//				B = Color.blue(pixel);
//
//				// increase/decrease each channel
//				R += value;
//				if (R > 255) {
//					R = 255;
//				} else if (R < 0) {
//					R = 0;
//				}
//
//				G += value;
//				if (G > 255) {
//					G = 255;
//				} else if (G < 0) {
//					G = 0;
//				}
//
//				B += value;
//				if (B > 255) {
//					B = 255;
//				} else if (B < 0) {
//					B = 0;
//				}
//
//				// apply new pixel color to output bitmap
//				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//			}
//		}
//
//		// return final image
//		return bmOut;
//	}

//	public Bitmap applyGaussianBlurEffect(Bitmap src) {
//		double[][] GaussianBlurConfig = new double[][]{
//				{1, 2, 1},
//				{2, 4, 2},
//				{1, 2, 1}
//		};
//		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
//		convMatrix.applyConfig(GaussianBlurConfig);
//		convMatrix.Factor = 16;
//		convMatrix.Offset = 0;
//		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
//	}

	public Bitmap applySharpenEffectLow(Bitmap src, double weight) {
		if (src == null || src.isRecycled()) {
			return null;
		}
		try {
			//느려서 빼버림..
//    		  double[][] SharpConfig = new double[][] {
//    	                { 0 , -2    , 0  },
//    	                { -2, weight, -2 },
//    	                { 0 , -2    , 0  }
//    	        };
//    	        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
//    	        convMatrix.applyConfig(SharpConfig);
//    	        convMatrix.Factor = weight - 8;
//    	        Bitmap sherpen = ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
			src = applySherpenIntensityEffect(src);

			return src;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}
	}

	public Bitmap applySharpenEffect(Bitmap original, double lev) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return applySharpenEffectLow(original, lev);
		}

		Bitmap bitmap = null;

		try {
			bitmap = Bitmap.createBitmap(
					original.getWidth(), original.getHeight(),
					original.getConfig());

			float sharpenValue = -0.13f; //낮을수록 밝아진다 높을수록 어두워 진다(선명해진다) 1.5 기준
			float sharpenPointValue = 2.0f; //낮을수록 어두워진다 높을수록 밝아진다 2.2 기준
			float[] sharp = {sharpenValue, sharpenValue, sharpenValue, sharpenValue, sharpenPointValue, sharpenValue, sharpenValue,
					sharpenValue, sharpenValue
			};

			RenderScript rs = RenderScript.create(ContextUtil.getContext());

			Allocation allocIn = Allocation.createFromBitmap(rs, original);
			Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

			ScriptIntrinsicConvolve3x3 convolution
					= ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
			convolution.setInput(allocIn);
			convolution.setCoefficients(sharp);
			convolution.forEach(allocOut);

			allocOut.copyTo(bitmap);
			rs.destroy();
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}

		return bitmap;
	}

//    public Bitmap applySharpenEffect(Bitmap original, double weight) {
//        float[] radius = { -0.60f, -0.60f, -0.60f, -0.60f, 5.81f, -0.60f,
//                -0.60f, -0.60f, -0.60f };
//
//        Bitmap bitmap = Bitmap.createBitmap(
//                original.getWidth(), original.getHeight(),
//                Bitmap.Config.ARGB_8888);
//
//        RenderScript rs = RenderScript.create(ContextUtil.getContext());
//
//        Allocation allocIn = Allocation.createFromBitmap(rs, original);
//        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);
//
//        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            ScriptIntrinsicConvolve3x3 convolution
//                    = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
//            convolution.setInput(allocIn);
//            convolution.setCoefficients(radius);
//            convolution.forEach(allocOut);
//        }
//
//        allocOut.copyTo(bitmap);
//        rs.destroy();
//
//        return bitmap;
//
//    }

//	public Bitmap applyMeanRemovalEffect(Bitmap src) {
//		double[][] MeanRemovalConfig = new double[][]{
//				{-1, -1, -1},
//				{-1, 9, -1},
//				{-1, -1, -1}
//		};
//		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
//		convMatrix.applyConfig(MeanRemovalConfig);
//		convMatrix.Factor = 1;
//		convMatrix.Offset = 0;
//		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
//	}

//	public Bitmap applySmoothEffect(Bitmap src, double value) {
//		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
//		convMatrix.setAll(1);
//		convMatrix.Matrix[1][1] = value;
//		convMatrix.Factor = value + 8;
//		convMatrix.Offset = 1;
//		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
//	}

//	public Bitmap applyEmbossEffect(Bitmap src) {
//		double[][] EmbossConfig = new double[][]{
//				{-1, 0, -1},
//				{0, 4, 0},
//				{-1, 0, -1}
//		};
//		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
//		convMatrix.applyConfig(EmbossConfig);
//		convMatrix.Factor = 1;
//		convMatrix.Offset = 127;
//		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
//	}

//	public Bitmap applyEngraveEffect(Bitmap src) {
//		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
//		convMatrix.setAll(0);
//		convMatrix.Matrix[0][0] = -2;
//		convMatrix.Matrix[1][1] = 2;
//		convMatrix.Factor = 1;
//		convMatrix.Offset = 95;
//		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
//	}

//	public Bitmap applyBoostEffect(Bitmap src, int type, float percent) {
//		int width = src.getWidth();
//		int height = src.getHeight();
//		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
//
//		int A, R, G, B;
//		int pixel;
//
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				pixel = src.getPixel(x, y);
//				A = Color.alpha(pixel);
//				R = Color.red(pixel);
//				G = Color.green(pixel);
//				B = Color.blue(pixel);
//				if (type == 1) {
//					R = (int) (R * (1 + percent));
//					if (R > 255) {
//						R = 255;
//					}
//				} else if (type == 2) {
//					G = (int) (G * (1 + percent));
//					if (G > 255) {
//						G = 255;
//					}
//				} else if (type == 3) {
//					B = (int) (B * (1 + percent));
//					if (B > 255) {
//						B = 255;
//					}
//				}
//				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
//			}
//		}
//		return bmOut;
//	}

//	public Bitmap applyRoundCornerEffect(Bitmap src, float round) {
//		// image size
//		int width = src.getWidth();
//		int height = src.getHeight();
//		// create bitmap output
//		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		// set canvas for painting
//		Canvas canvas = new Canvas(result);
//		canvas.drawARGB(0, 0, 0, 0);
//
//		// config paint
//		final Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		paint.setColor(Color.BLACK);
//
//		// config rectangle for embedding
//		final Rect rect = new Rect(0, 0, width, height);
//		final RectF rectF = new RectF(rect);
//
//		// draw rect to canvas
//		canvas.drawRoundRect(rectF, round, round, paint);
//
//		// create Xfer mode
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		// draw source image to canvas
//		canvas.drawBitmap(src, rect, rect, paint);
//
//		// return final image
//		return result;
//	}

//	public Bitmap applyWaterMarkEffect(Bitmap src, String watermark, int x, int y, int color, int alpha, int size, boolean underline) {
//		int w = src.getWidth();
//		int h = src.getHeight();
//		Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
//
//		Canvas canvas = new Canvas(result);
//		canvas.drawBitmap(src, 0, 0, null);
//
//		Paint paint = new Paint();
//		paint.setColor(color);
//		paint.setAlpha(alpha);
//		paint.setTextSize(size);
//		paint.setAntiAlias(true);
//		paint.setUnderlineText(underline);
//		canvas.drawText(watermark, x, y, paint);
//
//		return result;
//	}

//	public static final double PI = 3.14159d;
//	public static final double FULL_CIRCLE_DEGREE = 360d;
//	public static final double HALF_CIRCLE_DEGREE = 180d;
//	public static final double RANGE = 256d;
//
//	public Bitmap applyTintEffect(Bitmap src, int degree) {
//
//		int width = src.getWidth();
//		int height = src.getHeight();
//
//		int[] pix = new int[width * height];
//		src.getPixels(pix, 0, width, 0, 0, width, height);
//
//		int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
//		double angle = (PI * (double) degree) / HALF_CIRCLE_DEGREE;
//
//		int S = (int) (RANGE * Math.sin(angle));
//		int C = (int) (RANGE * Math.cos(angle));
//
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				int index = y * width + x;
//				int r = (pix[index] >> 16) & 0xff;
//				int g = (pix[index] >> 8) & 0xff;
//				int b = pix[index] & 0xff;
//				RY = (70 * r - 59 * g - 11 * b) / 100;
//				GY = (-30 * r + 41 * g - 11 * b) / 100;
//				BY = (-30 * r - 59 * g + 89 * b) / 100;
//				Y = (30 * r + 59 * g + 11 * b) / 100;
//				RYY = (S * BY + C * RY) / 256;
//				BYY = (C * BY - S * RY) / 256;
//				GYY = (-51 * RYY - 19 * BYY) / 100;
//				R = Y + RYY;
//				R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
//				G = Y + GYY;
//				G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
//				B = Y + BYY;
//				B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
//				pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
//			}
//		}
//
//		Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());
//		outBitmap.setPixels(pix, 0, width, 0, 0, width, height);
//
//		pix = null;
//
//		return outBitmap;
//	}

//	public static final int COLOR_MIN = 0x00;
//	public static final int COLOR_MAX = 0xFF;
//
//	public Bitmap applyFleaEffect(Bitmap source) {
//		// get image size
//		int width = source.getWidth();
//		int height = source.getHeight();
//		int[] pixels = new int[width * height];
//		// get pixel array from source
//		source.getPixels(pixels, 0, width, 0, 0, width, height);
//		// a random object
//		Random random = new Random();
//
//		int index = 0;
//		// iteration through pixels
//		for (int y = 0; y < height; ++y) {
//			for (int x = 0; x < width; ++x) {
//				// get current index in 2D-matrix
//				index = y * width + x;
//				// get random color
//				int randColor = Color.rgb(random.nextInt(COLOR_MAX),
//						random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
//				// OR
//				pixels[index] |= randColor;
//			}
//		}
//		// output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
//		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bmOut;
//	}

//	public Bitmap applyBlackFilter(Bitmap source) {
//		// get image size
//		int width = source.getWidth();
//		int height = source.getHeight();
//		int[] pixels = new int[width * height];
//		// get pixel array from source
//		source.getPixels(pixels, 0, width, 0, 0, width, height);
//		// random object
//		Random random = new Random();
//
//		int R, G, B, index = 0, thresHold = 0;
//		// iteration through pixels
//		for (int y = 0; y < height; ++y) {
//			for (int x = 0; x < width; ++x) {
//				// get current index in 2D-matrix
//				index = y * width + x;
//				// get color
//				R = Color.red(pixels[index]);
//				G = Color.green(pixels[index]);
//				B = Color.blue(pixels[index]);
//				// generate threshold
//				thresHold = random.nextInt(COLOR_MAX);
//				if (R < thresHold && G < thresHold && B < thresHold) {
//					pixels[index] = Color.rgb(COLOR_MIN, COLOR_MIN, COLOR_MIN);
//				}
//			}
//		}
//		// output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bmOut;
//	}

//	public Bitmap applySnowEffect(Bitmap source) {
//		// get image size
//		int width = source.getWidth();
//		int height = source.getHeight();
//		int[] pixels = new int[width * height];
//		// get pixel array from source
//		source.getPixels(pixels, 0, width, 0, 0, width, height);
//		// random object
//		Random random = new Random();
//
//		int R, G, B, index = 0, thresHold = 50;
//		// iteration through pixels
//		for (int y = 0; y < height; ++y) {
//			for (int x = 0; x < width; ++x) {
//				// get current index in 2D-matrix
//				index = y * width + x;
//				// get color
//				R = Color.red(pixels[index]);
//				G = Color.green(pixels[index]);
//				B = Color.blue(pixels[index]);
//				// generate threshold
//				thresHold = random.nextInt(COLOR_MAX);
//				if (R > thresHold && G > thresHold && B > thresHold) {
//					pixels[index] = Color.rgb(COLOR_MAX, COLOR_MAX, COLOR_MAX);
//				}
//			}
//		}
//		// output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bmOut;
//	}

//	public Bitmap applyShadingFilter(Bitmap source, int shadingColor) {
//		// get image size
//		int width = source.getWidth();
//		int height = source.getHeight();
//		int[] pixels = new int[width * height];
//		// get pixel array from source
//		source.getPixels(pixels, 0, width, 0, 0, width, height);
//
//		int index = 0;
//		// iteration through pixels
//		for (int y = 0; y < height; ++y) {
//			for (int x = 0; x < width; ++x) {
//				// get current index in 2D-matrix
//				index = y * width + x;
//				// AND
//				pixels[index] &= shadingColor;
//			}
//		}
//		// output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bmOut;
//	}

//	public Bitmap applySaturationFilter(Bitmap source, int level) {
//		// get image size
//		int width = source.getWidth();
//		int height = source.getHeight();
//		int[] pixels = new int[width * height];
//		float[] HSV = new float[3];
//		// get pixel array from source
//		source.getPixels(pixels, 0, width, 0, 0, width, height);
//
//		int index = 0;
//		// iteration through pixels
//		for (int y = 0; y < height; ++y) {
//			for (int x = 0; x < width; ++x) {
//				// get current index in 2D-matrix
//				index = y * width + x;
//				// convert to HSV
//				Color.colorToHSV(pixels[index], HSV);
//				// increase Saturation level
//				HSV[1] *= level;
//				HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
//				// take color back
//				pixels[index] |= Color.HSVToColor(HSV);
//			}
//		}
//		// output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bmOut;
//	}

//	public Bitmap applyHueFilter(Bitmap source, int level) {
//		// get image size
//		int width = source.getWidth();
//		int height = source.getHeight();
//		int[] pixels = new int[width * height];
//		float[] HSV = new float[3];
//		// get pixel array from source
//		source.getPixels(pixels, 0, width, 0, 0, width, height);
//
//		int index = 0;
//		// iteration through pixels
//		for (int y = 0; y < height; ++y) {
//			for (int x = 0; x < width; ++x) {
//				// get current index in 2D-matrix
//				index = y * width + x;
//				// convert to HSV
//				Color.colorToHSV(pixels[index], HSV);
//				// increase Saturation level
//				HSV[0] *= level;
//				HSV[0] = (float) Math.max(0.0, Math.min(HSV[0], 360.0));
//				// take color back
//				pixels[index] |= Color.HSVToColor(HSV);
//			}
//		}
//		// output bitmap
//		Bitmap bmOut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bmOut;
//	}

//	public Bitmap applyReflection(Bitmap originalImage) {
//		// gap space between original and reflected
//		final int reflectionGap = 4;
//		// get image size
//		int width = originalImage.getWidth();
//		int height = originalImage.getHeight();
//
//		// this will not scale but will flip on the Y axis
//		Matrix matrix = new Matrix();
//		matrix.preScale(1, -1);
//
//		// create a Bitmap with the flip matrix applied to it.
//		// we only want the bottom half of the image
//		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);
//
//		// create a new bitmap with same width but taller to fit reflection
//		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
//
//		// create a new Canvas with the bitmap that's big enough for
//		// the image plus gap plus reflection
//		Canvas canvas = new Canvas(bitmapWithReflection);
//		// draw in the original image
//		canvas.drawBitmap(originalImage, 0, 0, null);
//		// draw in the gap
//		Paint defaultPaint = new Paint();
//		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
//		// draw in the reflection
//		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
//
//		// create a shader that is a linear gradient that covers the reflection
//		Paint paint = new Paint();
//		LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
//				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
//				TileMode.CLAMP);
//		// set the paint to use this shader (linear gradient)
//		paint.setShader(shader);
//		// set the Transfer mode to be porter duff and destination in
//		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
//		// draw a rectangle using the paint with our linear gradient
//		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
//
//		return bitmapWithReflection;
//	}

	public Bitmap applyBlandEffect(Context context,
			int filterDrawableRes,
			Mode blandMode,
			int alphaPer,
			Bitmap originalImage) {

		if (originalImage == null || originalImage.isRecycled()) {
			return null;
		}

		Bitmap blend = getEffectBlandedBitmap(context, filterDrawableRes, blandMode, alphaPer, originalImage);

		if (blandMode != Mode.MULTIPLY) {
			return blend;
		}

		if (blend != null) {
			Paint p = new Paint();
			p.setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
			p.setShader(new BitmapShader(blend, TileMode.CLAMP, TileMode.CLAMP));

			Canvas c = new Canvas();
			c.setBitmap(originalImage);
			c.drawBitmap(originalImage, 0, 0, null);
			c.drawRect(0, 0, originalImage.getWidth(), originalImage.getHeight(), p);
		}

		return originalImage;
	}

	private Bitmap getEffectBlandedBitmap(Context context,
			int filterDrawableRes,
			Mode blandMode,
			int alphaPer,
			Bitmap originalImage) {
		return getEffectBlandedBitmap(context, filterDrawableRes, blandMode, alphaPer, originalImage, 1);
	}

	private Bitmap getEffectBlandedBitmap(Context context,
			int filterDrawableRes,
			Mode blandMode,
			int alphaPer,
			Bitmap originalImage,
			int sampleRat) {

		if (originalImage == null || originalImage.isRecycled()) {
			return null;
		}

		try {
			Resources res = context.getResources();
			BitmapFactory.Options options = new BitmapFactory.Options();
			if (sampleRat <= 1) {
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(res, filterDrawableRes, options);

				int offsetSize = originalImage.getWidth() > originalImage.getHeight() ? originalImage.getWidth() : originalImage.getHeight();

				options.inSampleSize = CropUtil.calculateInSampleSize(options, offsetSize, offsetSize);
				options.inPreferredConfig = Config.ARGB_8888;
//        		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
			} else {
				options.inSampleSize = sampleRat;
			}

			sampleRat = options.inSampleSize;
			options.inDither = false;
			options.inJustDecodeBounds = false;

			Bitmap orgBlendRes = BitmapFactory.decodeResource(res, filterDrawableRes, options);

			Bitmap scaleBitmap = Bitmap.createScaledBitmap(orgBlendRes, originalImage.getWidth(), originalImage.getHeight(), false);
			if (scaleBitmap != orgBlendRes) {
				if (orgBlendRes != null && !orgBlendRes.isRecycled()) {
					orgBlendRes.recycle();
					orgBlendRes = null;
				}
			}
			orgBlendRes = scaleBitmap;

			Paint p = new Paint();
			p.setAlpha(convertAlphaPer(alphaPer));
			p.setXfermode(new PorterDuffXfermode(blandMode));
			p.setShader(new BitmapShader(orgBlendRes, TileMode.CLAMP, TileMode.CLAMP));

			Canvas c = new Canvas();
			c.setBitmap(originalImage);
			c.drawBitmap(originalImage, 0, 0, null);
			c.drawRect(0, 0, originalImage.getWidth(), originalImage.getHeight(), p);
			return originalImage;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			sampleRat *= 2;
			if (sampleRat <= MAX_DOWN_SAMPLE_RATIO) {
				return getEffectBlandedBitmap(context, filterDrawableRes, blandMode, alphaPer, originalImage, sampleRat);
			} else {
				return null;
			}
		}
	}

	public Bitmap applyOpacityEffect(Bitmap originalImage, int opacity, Mode mode) {
		if (originalImage == null || originalImage.isRecycled()) {
			return null;
		}
		try {
			Bitmap blend = originalImage.copy(Config.ARGB_8888, true);//BitmapFactory.decodeResource(res, R.drawable.img_effect_filter_emerald, options);
			Bitmap result = originalImage.copy(Config.ARGB_8888, true);

			Paint p = new Paint();
			p.setAlpha(opacity);
			p.setXfermode(new PorterDuffXfermode(mode));
			p.setShader(new BitmapShader(blend, TileMode.CLAMP, TileMode.CLAMP));

			Canvas c = new Canvas();
			c.setBitmap(result);
			c.drawBitmap(originalImage, 0, 0, null);
			c.drawRect(0, 0, originalImage.getWidth(), originalImage.getHeight(), p);

			return result;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}
	}

	private int convertAlphaPer(int per) {
		return (int) (255 * (per / 100.f));
	}

	public Bitmap applyFilmCameraEffect(Context context, Bitmap originalImage) {
		if (originalImage == null || originalImage.isRecycled()) {
			return null;
		}
		try {
			Resources res = context.getResources();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap blend = BitmapFactory.decodeResource(res, R.drawable.img_effect_filter_film, options);

			options.inSampleSize = CropUtil.calculateInSampleSize(options, originalImage.getWidth(), originalImage.getHeight());
			options.inPreferredConfig = Config.ARGB_8888;
			options.inJustDecodeBounds = false;
			options.inDither = false;

			blend = BitmapFactory.decodeResource(res, R.drawable.img_effect_filter_film, options);

			Bitmap result = applyOpacityEffect(originalImage, convertAlphaPer(80), Mode.OVERLAY);

			Paint p = new Paint();
			p.setAlpha(convertAlphaPer(15));

			p.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
			p.setShader(new BitmapShader(blend, TileMode.CLAMP, TileMode.CLAMP));

			Canvas c = new Canvas();
			c.setBitmap(result);
			c.drawBitmap(originalImage, 0, 0, null);
			c.drawRect(0, 0, originalImage.getWidth(), originalImage.getHeight(), p);
			return result;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}
	}

	public Bitmap applyClamEffect(Context context, Bitmap originalImage) {
		if (originalImage == null || originalImage.isRecycled()) {
			return null;
		}
		try {
			Resources res = context.getResources();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap blend = BitmapFactory.decodeResource(res, R.drawable.img_effect_filter_dawn, options);

			options.inSampleSize = CropUtil.calculateInSampleSize(options, originalImage.getWidth(), originalImage.getHeight());
			options.inPreferredConfig = Config.ARGB_8888;
			options.inJustDecodeBounds = false;
			options.inDither = false;

			blend = BitmapFactory.decodeResource(res, R.drawable.img_effect_filter_dawn, options);

			Bitmap result = applyOpacityEffect(originalImage, convertAlphaPer(100), Mode.SCREEN);

			Paint p = new Paint();
			p.setAlpha(convertAlphaPer(50));
			p.setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
			p.setShader(new BitmapShader(blend, TileMode.CLAMP, TileMode.CLAMP));

			Canvas c = new Canvas();
			c.setBitmap(result);
			c.drawBitmap(originalImage, 0, 0, null);
			c.drawRect(0, 0, originalImage.getWidth(), originalImage.getHeight(), p);

			return result;

		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}
	}

	public Bitmap applyBlackCatEffect(Context context, Bitmap originalImage) {
		if (originalImage == null || originalImage.isRecycled()) {
			return null;
		}
		try {
			Resources res = context.getResources();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap blend = BitmapFactory.decodeResource(res, R.drawable.img_effect_filter_blackcat, options);

			options.inSampleSize = CropUtil.calculateInSampleSize(options, originalImage.getWidth(), originalImage.getHeight());
			options.inPreferredConfig = Config.ARGB_8888;
			options.inJustDecodeBounds = false;
			options.inDither = false;

			blend = BitmapFactory.decodeResource(res, R.drawable.img_effect_filter_blackcat, options);

			Bitmap result = applyOpacityEffect(originalImage, convertAlphaPer(70), Mode.OVERLAY);

			Paint p = new Paint();
			p.setAlpha(convertAlphaPer(70));
			p.setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
			p.setShader(new BitmapShader(blend, TileMode.CLAMP, TileMode.CLAMP));

			Canvas c = new Canvas();
			c.setBitmap(result);
			c.drawBitmap(originalImage, 0, 0, null);
			c.drawRect(0, 0, originalImage.getWidth(), originalImage.getHeight(), p);
			return result;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}
	}

	public static boolean updateEffectImageToOrgAngle(Activity activity, MyPhotoSelectImageData imageData) {
		if (activity == null || imageData == null) {
			return true;
		}
		try {
			String newEffectPath = imageData.EFFECT_PATH;
			String ext = ".jpg";

			if (newEffectPath != null && newEffectPath.contains(".")) {
				ext = newEffectPath.substring(newEffectPath.lastIndexOf("."));
			}

			String fileName = newEffectPath.substring(0, newEffectPath.lastIndexOf("."));
			newEffectPath = fileName + "_new" + ext;

			Bitmap effectBitmap = ImageLoader.syncLoadBitmap(imageData.EFFECT_PATH, -1, -1, -imageData.ROTATE_ANGLE_THUMB);
			ImageFilters.getAppliedEffectImgFilePath(activity, effectBitmap, newEffectPath);

			imageData.EFFECT_PATH = newEffectPath;
			imageData.EFFECT_THUMBNAIL_PATH = newEffectPath;
			return true;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return false;
	}
}
