package com.snaps.mobile.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.BaseCropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.thumbnail_skin.SnapsThumbNailUtil;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;

public class SnapsBitmapUtil {

	/***
	 * 사진인화 장바구니 리스트에 나오는 썸네일 만드는 함수..
	 * 
	 * @param cropBitmap
	 * @return
	 */
	static public Bitmap makeCartBitmap(Context context, Bitmap cropBitmap , String prodcode, PhotoPrintData data) {
		if (cropBitmap == null)
			return null;

		int bgID = 0;
		int w = 0;
		int h = 0;
		if(prodcode.equals("00800100010023"))
		{
			bgID = R.drawable.pt_44;
		}else
		{
			if (cropBitmap.getWidth() >= cropBitmap.getHeight()) {
				w = data.getSize()[1];
				h = data.getSize()[0];
			} else if(cropBitmap.getWidth() < cropBitmap.getHeight()){
				w = data.getSize()[0];
				h = data.getSize()[1];
			}
		}

		// 빈 캔버스를 만든
		Bitmap tempBmp = Bitmap.createBitmap(480, 480, android.graphics.Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(tempBmp);
		canvas.drawRGB(250,250,250);
		
		Bitmap temp = cropBitmap;
		Bitmap scaleTempBitmap = Bitmap.createScaledBitmap(temp, w, h, true);
		if(scaleTempBitmap != temp) {
			if(temp != null && !temp.isRecycled()) {
				temp.recycle();
				temp = null;
			}
		}
	
		temp = scaleTempBitmap;
		String [] productData = SnapsThumbNailUtil.getThumbNailData(prodcode);
		final float scale = Float.parseFloat(productData[1]);
		temp = CropUtil.getInSampledBitmapCopy(temp, DEFAULT_BITMAP_CONFIG,1,scale);
//		temp = addShadow(temp,temp.getHeight(),temp.getUserSelectWidth(),Color.BLACK,3,3,3);
//		temp = addShadow2(context,temp,temp.getUserSelectWidth(),temp.getHeight());
		temp= doHighlightImage2(temp);
		canvas.drawBitmap(temp, (tempBmp.getWidth() / 2) - (temp.getWidth() / 2),(tempBmp.getHeight() / 2) - temp.getHeight()/2, null);

		cropBitmap.recycle();
		temp.recycle();
		cropBitmap = null;
		temp = null;
		return tempBmp;
	}
//	public static Bitmap addShadow(final Bitmap bm, final int dstHeight, final int dstWidth, int color, int size, float dx, float dy) {
//		final Bitmap mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8);
//
//		final Matrix scaleToFit = new Matrix();
//		final RectF src = new RectF(0, 0, bm.getUserSelectWidth(), bm.getHeight());
//		final RectF dst = new RectF(0, 0, dstWidth - dx, dstHeight - dy);
//		scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
//
//		final Matrix dropShadow = new Matrix(scaleToFit);
//		dropShadow.postTranslate(dx, dy);
//
//		final Canvas maskCanvas = new Canvas(mask);
//		final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		maskCanvas.drawBitmap(bm, scaleToFit, paint);
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
//		maskCanvas.drawBitmap(bm, dropShadow, paint);
//
//		final BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
//		paint.reset();
//		paint.setAntiAlias(true);
//		paint.setColor(color);
//		paint.setMaskFilter(filter);
//		paint.setFilterBitmap(true);
//		final Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
//		final Canvas retCanvas = new Canvas(ret);
//		retCanvas.drawBitmap(mask, 0,  0, paint);
//		retCanvas.drawBitmap(bm, scaleToFit, null);
//		mask.recycle();
//		return ret;
//	}
//	public static Bitmap addShadow2(Context context,final Bitmap bm,int dstWidth,int dstHeight) {
//		final Bitmap mask = Bitmap.createBitmap(dstWidth+20, dstHeight + 20, Bitmap.Config.ARGB_8888);
//		NinePatchDrawable shadow= (NinePatchDrawable)ImageUtil.getNinePatchDrawableFromResourceId(R.drawable.thumb_nail_shadow_,context);
//
//		shadow.setBounds(new Rect(0,0,dstWidth + 20,dstHeight + 20));
//
//		Bitmap frame = CropUtil.getInSampledDecodeBitmapFromResource(context.getResources(), R.drawable.thumb_nail_shadow_,dstWidth+10,dstHeight+10);//BitmapFactory.decodeResource();
////		Bitmap shadow = frame.copy(Bitmap.Config.ARGB_8888,true);
//
//		final Canvas maskCanvas = new Canvas(mask);
//		shadow.draw(maskCanvas);
//		//maskCanvas.drawBitmap(bm,(mask.getUserSelectWidth() / 2) - (bm.getUserSelectWidth() / 2),(mask.getHeight() / 2) - bm.getHeight()/2,null);
//		return mask;
//	}
	public static Bitmap doHighlightImage(Bitmap src) {
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + 30, src.getHeight() +30, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmOut);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		Paint ptBlur = new Paint();
		ptBlur.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
		int[] offsetXY = new int[2];
		Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
		Paint ptAlphaColor = new Paint();
		ptAlphaColor.setColor(Color.BLACK);
		canvas.drawBitmap(bmAlpha, (bmOut.getWidth() / 2) - (src.getWidth() / 2)+offsetXY[0] , (src.getHeight() / 2) - bmOut.getHeight()/2+offsetXY[1] , ptAlphaColor);
		bmAlpha.recycle();
		canvas.drawBitmap(src, (bmOut.getWidth() / 2) - (src.getWidth() / 2),(src.getHeight() / 2) - bmOut.getHeight()/2, null);
		return bmOut;
	}

	public static Bitmap doHighlightImage2(Bitmap src) {
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth() +30 , src.getHeight() +30, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmOut);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		Paint ptBlur = new Paint();
//		ptBlur.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
		int[] offsetXY = new int[2];
		Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
		//Bitmap bmAlpha = Bitmap.createBitmap(src.getUserSelectWidth() +10 , src.getHeight() +10, Bitmap.Config.ARGB_8888);
		Paint ptAlphaColor = new Paint();
		ptAlphaColor.setShadowLayer(5.0f, 3.0f, 3.0f, Color.parseColor("#33000000"));
		canvas.drawBitmap(bmAlpha, (bmOut.getWidth() / 2) - (bmAlpha.getWidth() / 2) +offsetXY[0], (bmOut.getWidth() / 2) - (bmAlpha.getWidth() / 2) + offsetXY[1] , ptAlphaColor);
		bmAlpha.recycle();
		canvas.drawBitmap(src, (bmOut.getWidth() / 2) - (src.getWidth() / 2) ,(bmOut.getHeight() / 2) - src.getHeight()/2, null);
		return bmOut;
	}

	public static Bitmap processCropInfo(Bitmap bitmap, int rotate, BaseCropInfo cropInfo, String uri) throws OutOfMemoryError {
		if(bitmap == null || bitmap.isRecycled()) return null;

		if(rotate > 0)  {
			Bitmap appliedRotaionBitmap = CropUtil.getRotateImage(bitmap, rotate);
			if(appliedRotaionBitmap != bitmap && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
			bitmap = appliedRotaionBitmap;
		}

		if(cropInfo != null && cropInfo.isCropped()) {
			Bitmap appliedCropBitmap = null;
			if(cropInfo.isAdjustableCropSize()) {
				appliedCropBitmap = CropUtil.cropBitmapByMatrix(bitmap, uri,
						(AdjustableCropInfo) cropInfo, rotate);
			} else {
				appliedCropBitmap = CropUtil.cropBitmap((CropInfo) cropInfo, bitmap);
			}

			if(appliedCropBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
			bitmap = appliedCropBitmap;
		}

		return bitmap;
	}

	public static Bitmap createEmptyGrayBitmap(int width, int height) {
		Rect rect = new Rect(0, 0, width, height);
		Bitmap image = CropUtil.getInSampledBitmap(rect.width(), rect.height(), Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(image);
		int color = Color.argb(255, 238, 238, 238);

		Paint paint = new Paint();
		paint.setColor(color);
		canvas.drawRect(rect, paint);
		return image;
	}
}
