package com.snaps.common.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.snaps.common.data.bitmap.PageBitmap;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FlushedInputStream;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo.CropImageRect;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo.CORP_ORIENT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import errorhandle.logger.Logg;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;
import static com.snaps.common.utils.imageloader.ImageLoader.MAX_DOWN_SAMPLE_RATIO;

public class BitmapUtil {
	private static final String TAG = BitmapUtil.class.getSimpleName();

	public static final String MODE_VERTICAL = "mode_vertical";
	public static final String MODE_HORIZONTAL = "mode_horizontal";

	public static String[] getImagePosition(int loadType, SnapsLayoutControl layout, float imageWidth, float imageHeight) {
		String temp = null;
		if (loadType == 2 || layout.maskType.equalsIgnoreCase("image") || (Config.isSimpleMakingBook() && layout.getPageIndex() > 0) || layout.isImageFull) {
			temp = BitmapUtil.getImageFullPosition(Integer.valueOf(layout.width), Integer.valueOf(layout.height), imageWidth, imageHeight, layout.imgData);
		} else {
			temp = BitmapUtil.getPageFullPosition(Float.parseFloat(layout.width), Float.parseFloat(layout.height), imageWidth, imageHeight, layout.imgData);
		}
		return temp.replace(" ", "|").split("\\|");
	}

	public static boolean setImageDimensionInfo(SnapsControl c) {
		if (c instanceof SnapsLayoutControl) {
			SnapsLayoutControl layout = (SnapsLayoutControl) c;
			String[] temp = null;

			float imageWidth = 0.f;
			float imageHeight = 0.f;

			try {
				if (layout.srcTargetType != null && layout.srcTargetType.equalsIgnoreCase("content") || layout.imageLoadType == 2) {
					imageWidth = Integer.parseInt(layout.width);
					imageHeight = Integer.parseInt(layout.height);
				} else {
					if (layout.imgData != null && layout.imgData.F_IMG_WIDTH != null && layout.imgData.F_IMG_WIDTH.length() > 0 && layout.imgData.F_IMG_HEIGHT != null
							&& layout.imgData.F_IMG_HEIGHT.length() > 0) {
						imageWidth = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
						imageHeight = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
					}

					if (imageWidth <= 0 || imageHeight <= 0) {
						return false;
					}
				}
			} catch (Exception e) {
				return false;
			}

			//이미지의 F_IMAGE_WIDTH, HEIGHT 값은 exif의 orientaion value가 적용되지 않았기 때문에 90도와 270도는 swap해 주어야 한다.
			int rotate = StringUtil.isEmpty(layout.angle) ? 0 : Integer.parseInt(layout.angle);
			int totalRotate = rotate == -1 ? 0 : rotate;

			String imagePath = null;

			if (layout.imgData != null) {
				imagePath = ImageUtil.getImagePath(ContextUtil.getContext(), layout.imgData);
			}

			if (imagePath == null || imagePath.length() < 1) {
				imagePath = layout.imagePath;
			}

			if (imagePath != null && !imagePath.startsWith("http")) {
				int ro = CropUtil.getExifOrientation(imagePath);
				totalRotate = (ro + totalRotate) % 360;
			}

			if (totalRotate == 90 || totalRotate == 270) {
				float tempWidth = imageWidth;
				imageWidth = imageHeight;
				imageHeight = tempWidth;
			}

			temp = BitmapUtil.getImagePosition(layout.imageLoadType, layout, imageWidth, imageHeight);

			if (layout.imgData != null) {
				layout.freeAngle = layout.imgData.FREE_ANGLE;
			} else {
				layout.freeAngle = 0;
			}

			layout.angle = String.valueOf(totalRotate);

			if (temp != null && temp.length >= 4) {
				layout.img_x = temp[0];
				layout.img_y = temp[1];
				layout.img_width = temp[2];
				layout.img_height = temp[3];

				if (layout.img_width == null || layout.img_width.equalsIgnoreCase("0") || layout.img_height == null || layout.img_height.equalsIgnoreCase("0")) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	public static boolean setImageDimensionInfo(ImageView view, final int loadType, final String uri, final int rotate) {
		SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(view);
		if (snapsControl != null && snapsControl instanceof SnapsLayoutControl) {
			SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;
			control.imageLoadType = loadType;
			control.imagePath = uri;
			control.angle = rotate + "";
			return setImageDimensionInfo(control);
		}
		return false;
	}

	public static boolean setImageDimensionInfo(SnapsLayoutControl layoutControl, final int loadType, final String uri, final int rotate) {
		if (layoutControl != null) {
			layoutControl.imageLoadType = loadType;
			layoutControl.imagePath = uri;
			layoutControl.angle = rotate + "";
			return setImageDimensionInfo(layoutControl);
		}
		return false;
	}

	/**
	 * 이미지 마스크 타입이 image일 경우.
	 *
	 * @param viewW
	 * @param viewH
	 * @param imgW
	 * @param imgH
	 * @return
	 */
	static String getImageFullPosition(float viewW, float viewH, float imgW, float imgH, MyPhotoSelectImageData imgData) {

		float tempW = (imgW / viewW);
		float tempH = (imgH / viewH);

		String x = "";
		String y = "";
		String w = "";
		String h = "";

		if (tempW > tempH) {
			// width 비율이 클 경우.

			w = String.valueOf(((int) viewW));
			h = String.valueOf(((int) (imgH * (viewW / imgW))));
		} else {
			// height 비율이 클 경우.

			w = String.valueOf(((int) (imgW * (viewH / imgH))));
			h = String.valueOf(((int) viewH));
		}

		if (imgData != null) {
			if (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) {
				tempW = Float.parseFloat(w);
				tempH = Float.parseFloat(h);

				w = String.valueOf((int) tempH);
				h = String.valueOf((int) tempW);
			}
		}

		x = String.valueOf((int) ((viewW - Double.parseDouble(w)) / 2));
		y = String.valueOf((int) ((viewH - Double.parseDouble(h)) / 2));

		return x + " " + y + " " + w + " " + h;
	}

	/**
	 * 이미지 마스크 타입이 page이거나 기본 이미지 로드
	 *
	 * @param viewW
	 * @param viewH
	 * @param imgW
	 * @param imgH
	 * @return
	 */
	public static String getPageFullPosition(float viewW, float viewH, float imgW, float imgH, MyPhotoSelectImageData imgData) {
		// Logg.d("viewW:"+viewW+",viewH:"+viewH+" imgW:"+imgW+",imgH:"+imgH);

		float tempW = (imgW / viewW);
		float tempH = (imgH / viewH);

		int x, y, w, h;

		if (tempW == tempH) { // imgW와 imgH가 모두 0일때 w=viewW, h=0이 나오는 문제 수
			w = (int) viewW;
			h = (int) viewH;
		} else if (tempW > tempH) {
			// width 비율이 클 경우.

			w = ((int) (imgW * (viewH / imgH)));
			h = ((int) viewH);
		} else {
			// height 비율이 클 경우.

			w = ((int) viewW);
			h = ((int) (imgH * (viewW / imgW)));
		}

		if (imgData != null) {
			if (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) {
				//효과가 적용된 사진인데, 페이지를 디자인을 변경한 경우 swap을 하지 않는다.
				tempW = w;
				tempH = h;

				w = (int) tempH;
				h = (int) tempW;
			}
		}

		x = (int) ((viewW - (double) w) / 2);
		y = (int) ((viewH - (double) h) / 2);

		// 크롭이 있는경우 크롭을 적용한다.

		if (imgData != null) {
			/**
			 * FIXME 크롭 방식 관련 기획 내용이 확정 되면, 아래와 같은 방식으로 변경해야 한다.
			 */
			if (imgData.isAdjustableCropMode && imgData.ADJ_CROP_INFO != null && imgData.ADJ_CROP_INFO.getClipRect() != null && imgData.ADJ_CROP_INFO.getImgRect() != null) {
				boolean isRotate = (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) ? true : false;

				CropImageRect clipRect = imgData.ADJ_CROP_INFO.getClipRect();
				CropImageRect imgRect = imgData.ADJ_CROP_INFO.getImgRect();

				// Canvas와의 기본 배율
				float fScaleW = viewW / clipRect.width;
				float fScaleH = viewH / clipRect.height;

				if (isRotate) {
					w = (int) (imgRect.height * fScaleW);
					h = (int) (imgRect.width * fScaleH);
				} else {
					w = (int) (imgRect.width * fScaleW);
					h = (int) (imgRect.height * fScaleH);
				}

				//이상한 비율이라면 편집 정보를 초기화 시킨다.
				boolean isInit = false;

				try {
					float fOrgImgW = Float.parseFloat(imgData.F_IMG_WIDTH);
					float fOrgImgH = Float.parseFloat(imgData.F_IMG_HEIGHT);

					if (w > h
							&& fOrgImgW < fOrgImgH) {
						isInit = true;
					}

					if (h > w
							&& fOrgImgH < fOrgImgW) {
						isInit = true;
					}

					float fOrgRat = fOrgImgW / fOrgImgH;
					float fRectRet = (float) w / (float) h;

					if (Math.abs(fOrgRat - fRectRet) > .1f) {
						isInit = true;
					}
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}

				if (isInit) {
					imgData.isAdjustableCropMode = false;
					imgData.ADJ_CROP_INFO = new AdjustableCropInfo();
					imgData.FREE_ANGLE = 0;
					imgData.RESTORE_ANGLE = SnapsImageDownloader.INVALID_ANGLE;
					return getPageFullPosition(viewW, viewH, imgW, imgH, imgData);
				} else {
					// 크롭영역과 이미지의 오프셋 설정
					x = (int) ((viewW - (double) w) / 2);
					y = (int) ((viewH - (double) h) / 2);

					x += (imgRect.movedX * fScaleW); // TODO Rotate 됨에 따라, W와 H를
					// 바꾸어 줘야 할지도 모르겠음...
					y += (imgRect.movedY * fScaleH);

					imgData.ROTATE_ANGLE = (int) imgRect.rotate;
					imgData.FREE_ANGLE = (int) imgRect.angle;
				}
			} else {
				if (imgData.CROP_INFO != null && imgData.CROP_INFO.cropOrient != CORP_ORIENT.NONE) {
					boolean isRotate = (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) ? true : false;
					if (imgData.CROP_INFO.cropOrient == CORP_ORIENT.WIDTH) {
						x += (isRotate ? h : w) * imgData.CROP_INFO.movePercent;
					} else {
						y += (isRotate ? w : h) * imgData.CROP_INFO.movePercent;
					}
				}
			}
		}

		return x + " " + y + " " + w + " " + h;
	}

	/**
	 * 이미지 마스크 타입이 page이거나 기본 이미지 로드
	 *
	 * @param viewW
	 * @param viewH
	 * @param imgW
	 * @param imgH
	 * @return
	 */
	static String getPageFullPosition2(float viewW, float viewH, float imgW, float imgH, int angle, CropInfo cropInfo) {

		float tempW = (imgW / viewW);
		float tempH = (imgH / viewH);

		int x, y, w, h;

		if (tempW > tempH) {
			// width 비율이 클 경우.

			w = ((int) (imgW * (viewH / imgH)));
			h = ((int) viewH);
		} else {
			// height 비율이 클 경우.

			w = ((int) viewW);
			h = ((int) (imgH * (viewW / imgW)));
		}

		if (angle == 90 || angle == 270) {
			tempW = w;
			tempH = h;

			w = (int) tempH;
			h = (int) tempW;
		}

		x = (int) ((viewW - (double) w) / 2);
		y = (int) ((viewH - (double) h) / 2);

		// 크롭이 있는경우 크롭을 적용한다.
		if (cropInfo != null && cropInfo.cropOrient != CORP_ORIENT.NONE) {

			boolean isRotate = (angle == 90 || angle == 270) ? true : false;
			if (cropInfo.cropOrient == CORP_ORIENT.WIDTH) {
				x += (isRotate ? h : w) * cropInfo.movePercent;
			} else {
				y += (isRotate ? w : h) * cropInfo.movePercent;
			}
		}

		return x + " " + y + " " + w + " " + h;
	}

	/**
	 * 비트맵 라운드 처리.
	 *
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 대표 썸네일 로컬 저장.
	 *
	 * @param context
	 * @param orgBmp  썸네일 Bitmap
	 * @param width   썸네일 width
	 * @param height  썸네일 height
	 */
	public static Bitmap saveLocalThumbnail(Context context, Bitmap orgBmp, int width, int height) {
		try {
			Bitmap bmp;

			// 스케일 조정..
			float tempScaleW = 1.f;
			float tempScaleH = 1.f;
			if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
				tempScaleW = ((float) Config.THUMB_SIZE_FOR_SNAPS_DIARY) / ((float) width);
				tempScaleH = ((float) Config.THUMB_SIZE_FOR_SNAPS_DIARY) / ((float) height);
			} else {
				tempScaleW = ((float) Config.THUMB_SIZE) / ((float) width);
				tempScaleH = ((float) Config.THUMB_SIZE) / ((float) height);
			}

			float tempW;
			float tempH;

			// TODO 대표썸네일 생성 변경 이미지풀로~ 200X200

			if (width < height) {// 카스북의 경우
				tempW = width * tempScaleH;
				tempH = height * tempScaleH;

				bmp = Bitmap.createScaledBitmap(orgBmp, (int) tempW, (int) tempH, true);
				if (orgBmp != bmp) {
					if (bmp != null && !bmp.isRecycled()) {
						orgBmp.recycle();
						orgBmp = null;
					}
				}
			} else if (width > height) {// 스티커,명함의 경우
				tempW = width * tempScaleW;
				tempH = height * tempScaleW;

				bmp = Bitmap.createScaledBitmap(orgBmp, (int) tempW, (int) tempH, true);
				if (orgBmp != bmp) {
					if (orgBmp != null && !orgBmp.isRecycled()) {
						orgBmp.recycle();
						orgBmp = null;
					}
				}
			} else {
				if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
					bmp = Bitmap.createScaledBitmap(orgBmp, Config.THUMB_SIZE_FOR_SNAPS_DIARY, Config.THUMB_SIZE_FOR_SNAPS_DIARY, true);
				} else {
					bmp = Bitmap.createScaledBitmap(orgBmp, Config.THUMB_SIZE, Config.THUMB_SIZE, true);
				}

				if (orgBmp != bmp) {
					if (orgBmp != null && !orgBmp.isRecycled()) {
						orgBmp.recycle();
						orgBmp = null;
					}
				}
			}

			return bmp != null && !bmp.isRecycled() ? bmp : null;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
		return null;
	}

	/**
	 * 이미지 로테이션.
	 *
	 * @param bmp    비트맵.
	 * @param rotate 로테이션 정보.
	 * @return
	 */
	public static Bitmap getRotateImage(Bitmap bmp, int rotate) {

		Matrix matrix = new Matrix();
		matrix.setRotate(rotate);

		try {
			Bitmap converted = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			bmp = converted;

		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return null;
		}

		return bmp;
	}

	/**
	 * 이미지의 좌우를 로딩한다.
	 *
	 * @param index
	 * @return
	 */
	public static Bitmap loadBitmap(Context context, int index, int[] mBitmapIds) {
		BitmapDrawable d = (BitmapDrawable) context.getResources().getDrawable(mBitmapIds[index]);
		return d.getBitmap();
	}

	public static Bitmap loadReverseBitmap(Context context, int index, int[] mBitmapIds) {
		BitmapDrawable d = (BitmapDrawable) context.getResources().getDrawable(mBitmapIds[index]);
		Bitmap orgBitmap = d.getBitmap();
		Matrix matrix = new Matrix();
		matrix.preScale(-1.0f, 1.0f);
		Bitmap revBitmap = Bitmap.createBitmap(orgBitmap, 0, 0, orgBitmap.getWidth(), orgBitmap.getHeight(), matrix, false);
		orgBitmap.recycle();
		return revBitmap;
	}

	public static Bitmap loadBitmap(Context context, int index, int orient, int[] mBitmapIds) {
		BitmapDrawable d = (BitmapDrawable) context.getResources().getDrawable(mBitmapIds[index]);
		return cropHalf(d.getBitmap(), orient);
	}

	public static Bitmap loadReverseBitmap(Bitmap orgBitmap) {
		Matrix matrix = new Matrix();
		matrix.preScale(-1.0f, 1.0f);
		Bitmap revBitmap = Bitmap.createBitmap(orgBitmap, 0, 0, orgBitmap.getWidth(), orgBitmap.getHeight(), matrix, false);
		return revBitmap;
	}

	/**
	 * 원본을 좌우로 나눠 비트맵을 반환한다.
	 *
	 * @param srcBitmap
	 * @return
	 */
	public static Bitmap[] loadBitmap(Bitmap srcBitmap) {
		int orgW = srcBitmap.getWidth();
		int orgH = srcBitmap.getHeight();
		int picW = orgW / 2;

		Bitmap[] leftRightBitmap = new Bitmap[2];
		leftRightBitmap[0] = Bitmap.createBitmap(srcBitmap, 0, 0, picW, orgH);
		leftRightBitmap[1] = Bitmap.createBitmap(srcBitmap, picW, 0, picW, orgH);
		// srcBitmap.recycle();

		return leftRightBitmap;
	}

	/**
	 * 이미지의 반을 좌우 기준으로 자른다.
	 *
	 * @param srcBitmap
	 * @param orient
	 * @return
	 */
	public static Bitmap cropHalf(Bitmap srcBitmap, int orient) {
		int orgW = srcBitmap.getWidth();
		int orgH = srcBitmap.getHeight();
		int picW = orgW / 2;
		if (orient == PageBitmap.ORIENT_LEFT) {// 좌우반전
			Matrix matrix = new Matrix();
			matrix.preScale(-1.0f, 1.0f);
			return Bitmap.createBitmap(srcBitmap, picW * orient, 0, picW, orgH, matrix, false);
		} else {
			return Bitmap.createBitmap(srcBitmap, picW * orient, 0, picW, orgH);
		}
	}

	/**
	 * 한쪽방향만 그리고 반대쪽은 비운다.
	 *
	 * @param bm
	 * @return
	 */
	public static Bitmap drawHalf(Bitmap bm, int orient) {
		try {
			Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth() * 2, bm.getHeight(), bm.getConfig());
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawColor(Color.TRANSPARENT);
			canvas.drawBitmap(bm, orient == PageBitmap.ORIENT_LEFT ? 0 : bm.getWidth(), 0, null);
			return newBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return null;
	}

	/**
	 * 좌우 bitmap을 합친다.
	 *
	 * @param bmLeft
	 * @param bmRight
	 * @return
	 */
	public static Bitmap drawAll(Bitmap bmLeft, Bitmap bmRight) {
		try {
			int minWidth = bmLeft.getWidth();
			int minHeight = bmLeft.getHeight();
			if (bmLeft.getWidth() > bmRight.getWidth()) {
				minWidth = bmRight.getWidth();
				minHeight = bmRight.getHeight();
			}

			Bitmap newBitmap = Bitmap.createBitmap(minWidth * 2, minHeight, bmLeft.getConfig());
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bmLeft, 0, 0, null);
			canvas.drawBitmap(bmRight, minWidth, 0, null);
			return newBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return null;
	}

	/**
	 * 좌우 bitmap을 합치는데 좌우 우선순위를 둔다.
	 *
	 * @param bmLeft
	 * @param bmRight
	 * @param orient  지정한 방향의 bitmap을 기준으로 합친다.
	 * @return
	 */
	public static Bitmap drawAll(Bitmap bmLeft, Bitmap bmRight, int orient) {
		try {
			// 한쪽 이미지가 작으면 큰 이미지에 사이즈를 맞춤.
			if (orient == PageBitmap.ORIENT_LEFT) {
				Bitmap tmp = Bitmap.createScaledBitmap(bmRight, bmLeft.getWidth(), bmLeft.getHeight(), true);
				if (tmp != bmRight) {
					bmRight.recycle();
					bmRight = null;
				}
				bmRight = tmp;
			} else {
				Bitmap tmp = Bitmap.createScaledBitmap(bmLeft, bmRight.getWidth(), bmRight.getHeight(), true);
				if (tmp != bmLeft) {
					bmLeft.recycle();
					bmLeft = null;
				}
				bmLeft = tmp;
			}

			Bitmap newBitmap = Bitmap.createBitmap(bmLeft.getWidth() * 2, bmLeft.getHeight(), bmLeft.getConfig());
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bmLeft, 0, 0, null);
			canvas.drawBitmap(bmRight, bmLeft.getWidth(), 0, null);
			return newBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return null;
	}

	/***
	 * 비트맵을 자를는 함수
	 *
	 * @param srcBitmap
	 * @param leftRatio
	 * @param topRatio
	 * @param rightRatio
	 * @param bottomRatio
	 * @return
	 */
	public static Bitmap cropBitmap(Bitmap srcBitmap, float leftRatio, float topRatio, float rightRatio, float bottomRatio) {

		int left = (int) (Math.ceil(srcBitmap.getWidth() * leftRatio));
		int top = (int) (Math.ceil(srcBitmap.getHeight() * topRatio));

		int orgW = srcBitmap.getWidth() - (int) (Math.ceil(srcBitmap.getWidth() * leftRatio + srcBitmap.getWidth() * rightRatio));
		int orgH = srcBitmap.getHeight() - (int) (Math.ceil(srcBitmap.getHeight() * topRatio + srcBitmap.getHeight() * bottomRatio));

		return Bitmap.createBitmap(srcBitmap, left, top, orgW, orgH);

	}

	/**
	 * bitmap을 파일로 저장.
	 *
	 * @param bmp
	 * @return
	 */
	public static boolean saveImgFile(String savePath, Bitmap bmp) {
		FileOutputStream stream = null;
		try {
			File file = new File(savePath);
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
			stream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 95, stream);
			bmp.recycle();
			bmp = null;
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		}
	}

	public static Rect getLocalImageRect(String imageUri) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageUri, options);
			return new Rect(0, 0, options.outWidth, options.outHeight);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isValidThumbnailImage(String imageUri) {
		Rect imageRect = getLocalImageRect(imageUri);
		return imageRect != null && imageRect.width() > 99 && imageRect.height() > 99;
	}

	/***
	 * 비트맵 릴리즈 함수.
	 *
	 * @param bitmap
	 */
	public static void bitmapRecycle(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/***
	 * 비트맵 릴리즈 함수.
	 *w
	 */
	public static void drawableRecycle(ImageView imageView) {
		if (imageView == null || imageView.getDrawable() == null) {
			return;
		}
		imageView.getDrawable().setCallback(null);
	}

	//가끔 이미지 비율 이상 현상이 있어서, 보정해 주는 코드
	public static String[] checkImageRatio(MyPhotoSelectImageData imgData, String[] rect, String[] clip) {
		if (imgData == null || rect == null || clip == null) {
			return rect;
		}

		boolean isWrongRatio = false;
		float imgWidth = 0, imgHeight = 0, rectWidth = 0, rectHeight = 0;

		try {
			imgWidth = Float.parseFloat(imgData.F_IMG_WIDTH);
			imgHeight = Float.parseFloat(imgData.F_IMG_HEIGHT);
			rectWidth = Float.parseFloat(rect[2]);
			rectHeight = Float.parseFloat(rect[3]);
		} catch (Exception e) {
			Dlog.e(TAG, e);
			return rect;
		}

		if (rectWidth <= 0 || rectHeight <= 0) {
			isWrongRatio = true;
		} else {
			if (imgWidth > imgHeight
					&& rectWidth < rectHeight) {
				isWrongRatio = true;
			}

			if (imgWidth < imgHeight
					&& rectWidth > rectHeight) {
				isWrongRatio = true;
			}
		}

		if (isWrongRatio) {
			Dlog.w(TAG, "checkImageRatio() isWrongRatio:" + isWrongRatio);
			String[] fixRect = fixImageRatio(imgData, imgWidth, imgHeight, clip);
			return fixRect != null ? fixRect : rect;
		}
		return rect;
	}

	public static String getImagePath(MyPhotoSelectImageData data) {
		if (data == null) {
			return "";
		}

		String baseImgPath = data.PATH;

		if (data.isApplyEffect) {
			String path = data.EFFECT_PATH;
			if ((new File(path).exists())) {
				return path;
			} else {
				if (Config.isSNSPhoto(data.KIND)) {
					baseImgPath = data.getSafetyThumbnailPath();
				} else {
					if (data.getSafetyThumbnailPath() != null && data.getSafetyThumbnailPath().length() > 0) {
						baseImgPath = SnapsAPI.DOMAIN(false) + data.getSafetyThumbnailPath();
					}
				}
			}
		} else {
			boolean isWebUrl = baseImgPath != null && baseImgPath.startsWith("http");

			if (!isWebUrl) {
				if (!new File(baseImgPath).exists()) {
					if (Config.isSNSPhoto(data.KIND)) {
						baseImgPath = data.getSafetyThumbnailPath();
					} else {
						if (data.getSafetyThumbnailPath() != null && data.getSafetyThumbnailPath().length() > 0 && !data.getSafetyThumbnailPath().contains("/snaps/effect")) {
							baseImgPath = SnapsAPI.DOMAIN(false) + data.getSafetyThumbnailPath();
						}
					}
				}
			} else {
				if (Config.isSNSPhoto(data.KIND)) {
					baseImgPath = data.PATH;
					if (Const_PRODUCT.isSNSBook()) {
						String thumb = data.getSafetyThumbnailPath();
						if (thumb != null && thumb.startsWith("http")) {
							baseImgPath = data.getSafetyThumbnailPath();
						}
					}
				} else {
					if (data.getSafetyThumbnailPath() != null && data.getSafetyThumbnailPath().length() > 0 && !data.getSafetyThumbnailPath().contains("/snaps/effect")) {

						if (!data.getSafetyThumbnailPath().startsWith("http")) {
							baseImgPath = SnapsAPI.DOMAIN(false) + data.PATH;
						} else {
							baseImgPath = data.PATH;
						}
					}
				}
			}
		}

		return baseImgPath;
	}

	public static String[] fixImageRatio(MyPhotoSelectImageData imgData, float imgW, float imgH, String[] clip) {

		if (clip == null || clip.length < 4) {
			return null;
		}

		String[] temp = null;
		float clipW = 0, clipH = 0;
		try {
			clipW = Float.parseFloat(clip[2]);
			clipH = Float.parseFloat(clip[3]);
		} catch (Exception e) {
			return temp;
		}

		int totalRotate = imgData.ROTATE_ANGLE == -1 ? 0 : imgData.ROTATE_ANGLE;

		String uri = getImagePath(imgData);

		if (uri.startsWith("http")) {
			// 웹사진이면 path
		} else {
			String imgUrl = uri.replace("file://", "");
			int ro = CropUtil.getExifOrientation(imgUrl);
			totalRotate = (ro + totalRotate) % 360;
		}

		if (totalRotate == 90 || totalRotate == 270) {
			float tempW = imgW;
			imgW = imgH;
			imgH = tempW;
		}

		temp = BitmapUtil.getPageFullPosition(clipW, clipH, imgW, imgH, imgData).replace(" ", "|").split("\\|");

		if (temp != null && temp.length >= 4) {
			if (temp[2] == null || temp[2].equalsIgnoreCase("0")
					|| temp[3] == null || temp[3].equalsIgnoreCase("0")) {
				return null;
			}
		}

		return temp;
	}

	public static Bitmap getScaledBitmapOffsetHeight(Bitmap orgBitmap, Bitmap offsetBitmap, float scale) {
		if (orgBitmap == null || orgBitmap.isRecycled() || offsetBitmap == null || offsetBitmap.isRecycled()) {
			return orgBitmap;
		}

		try {
			float scaledHeight = offsetBitmap.getHeight() * scale;
			float scaledWidth = orgBitmap.getWidth() * (scaledHeight / orgBitmap.getHeight());
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(orgBitmap,
					(int) scaledWidth, (int) scaledHeight, false);
			if (scaledBitmap != orgBitmap) {
				if (orgBitmap != null && !orgBitmap.isRecycled()) {
					orgBitmap.recycle();
					orgBitmap = null;
				}
			}
			orgBitmap = scaledBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return orgBitmap;
	}

	public static Bitmap getScaledBitmapOffsetLength(Bitmap orgBitmap, int offsetLength) {
		if (orgBitmap == null || orgBitmap.isRecycled()) {
			return orgBitmap;
		}

		int bitmapWidth = orgBitmap.getWidth();
		int bitmapHeight = orgBitmap.getHeight();

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
			return orgBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return orgBitmap;
	}

	public static Bitmap getScaledBitmapOffsetWidth(Bitmap orgBitmap, Bitmap offsetBitmap, float scale) {
		if (orgBitmap == null || orgBitmap.isRecycled() || offsetBitmap == null || offsetBitmap.isRecycled()) {
			return orgBitmap;
		}

		try {
			float scaledWidth = offsetBitmap.getWidth() * scale;
			float scaledheight = orgBitmap.getHeight() * (scaledWidth / orgBitmap.getWidth());
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(orgBitmap,
					(int) scaledWidth, (int) scaledheight, false);
			if (scaledBitmap != orgBitmap) {
				if (orgBitmap != null && !orgBitmap.isRecycled()) {
					orgBitmap.recycle();
					orgBitmap = null;
				}
			}
			orgBitmap = scaledBitmap;
		} catch (OutOfMemoryError e1) {
			return orgBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return orgBitmap;
	}

	public static Bitmap getScaledBitmapOffsetWidth(Bitmap orgBitmap, int offsetWidth) {
		if (orgBitmap == null || orgBitmap.isRecycled()) {
			return orgBitmap;
		}
		try {
			float scaledWidth = offsetWidth;
			float scaledheight = orgBitmap.getHeight() * (scaledWidth / orgBitmap.getWidth());
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(orgBitmap,
					(int) scaledWidth, (int) scaledheight, false);
			if (scaledBitmap != orgBitmap) {
				if (orgBitmap != null && !orgBitmap.isRecycled()) {
					orgBitmap.recycle();
					orgBitmap = null;
				}
			}
			orgBitmap = scaledBitmap;
		} catch (OutOfMemoryError e1) {
			return orgBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return orgBitmap;
	}

	public static Bitmap getScaledBitmapOffsetHeight(Bitmap orgBitmap, int offsetHeight) {
		if (orgBitmap == null || orgBitmap.isRecycled()) {
			return orgBitmap;
		}
		try {
			float scaledHeight = offsetHeight;
			float scaledWidth = orgBitmap.getWidth() * (scaledHeight / orgBitmap.getHeight());
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(orgBitmap,
					(int) scaledWidth, (int) scaledHeight, false);
			if (scaledBitmap != orgBitmap) {
				if (orgBitmap != null && !orgBitmap.isRecycled()) {
					orgBitmap.recycle();
					orgBitmap = null;
				}
			}
			orgBitmap = scaledBitmap;
		} catch (OutOfMemoryError e1) {
			return orgBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return orgBitmap;
	}

	public static Bitmap getScaledBitmap(Bitmap orgBitmap, int width, int height) {
		if (orgBitmap == null || orgBitmap.isRecycled()) {
			return orgBitmap;
		}
		try {
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(orgBitmap,
					width, height, false);
			if (scaledBitmap != orgBitmap) {
				if (orgBitmap != null && !orgBitmap.isRecycled()) {
					orgBitmap.recycle();
					orgBitmap = null;
				}
			}
			orgBitmap = scaledBitmap;
		} catch (OutOfMemoryError e1) {
			return orgBitmap;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return orgBitmap;
	}

	public static boolean saveFileFromUrl(String imgUrl, String outputPath) {
		if (imgUrl == null || outputPath == null) {
			return false;
		}

		URL url = null;
		OutputStream output = null;
		FlushedInputStream fis = null;
		try {

			File outputFile = new File(outputPath);
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			url = new URL(imgUrl);
			fis = new FlushedInputStream(url.openConnection().getInputStream());
			output = new FileOutputStream(outputPath);
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = fis.read(buffer, 0, buffer.length)) >= 0) {
				output.write(buffer, 0, bytesRead);
			}

			return outputFile.exists() && outputFile.length() > 0;
		} catch (MalformedURLException e) {
			Dlog.e(TAG, e);
			return false;
		} catch (IOException e) {
			Dlog.e(TAG, e);
			return false;
		} finally {
			try {
				if (output != null) {
					output.close();
				}

				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		}
	}

	public static boolean isUseAbleBitmap(Bitmap bitmap) {
		return bitmap != null && !bitmap.isRecycled();
	}

	public static Bitmap getInSampledBitmap(int width, int height, int samplingRatio) {
		Bitmap imgBitmap;

		try {
			if (width < 1 || height < 1) return null;

			imgBitmap = Bitmap.createBitmap(width, height, DEFAULT_BITMAP_CONFIG);

		} catch (OutOfMemoryError e) {
			samplingRatio *= 2;
			if (samplingRatio <= MAX_DOWN_SAMPLE_RATIO) {
				return getInSampledBitmap(width / samplingRatio, height / samplingRatio, samplingRatio);
			} else {
				return null;
			}
		}
		return imgBitmap;
	}
}
