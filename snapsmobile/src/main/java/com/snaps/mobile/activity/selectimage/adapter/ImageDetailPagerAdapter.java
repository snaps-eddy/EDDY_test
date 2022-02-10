package com.snaps.mobile.activity.selectimage.adapter;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ImageDirectLoader;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.selectimage.ImageDetailEditActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ImageDetailPagerAdapter extends PagerAdapter {
	private static final String TAG = ImageDetailPagerAdapter.class.getSimpleName();
	static final int ROTATE_ANGLE = 90;

	Map<Integer, ImageView> mapImgDetail = new HashMap<Integer, ImageView>();
	Map<Integer, Bitmap> mapImgBitmap = new HashMap<Integer, Bitmap>();
	Map<Integer, AsyncTask<Void, Void, Bitmap>> mapTask = new HashMap<Integer, AsyncTask<Void, Void, Bitmap>>();

	ImageDetailEditActivity detailAct;

	int srcImgThumbSize;

	public ImageDetailPagerAdapter(ImageDetailEditActivity detailAct) {
		this.detailAct = detailAct;
		srcImgThumbSize = UIUtil.getScreenWidth(detailAct);// getCalcSrcImgThumbSize(detailAct);
	}

	

	@Override
	public Object instantiateItem(View pager, final int position) {// 뷰페이저에서 사용할 뷰객체 생성/등록
		// 페이징화면 생성
		LayoutInflater inflater = LayoutInflater.from(detailAct);
		View view = inflater.inflate(R.layout.activity_imagedetailedit_item, null);

		final MyPhotoSelectImageData imgData = detailAct.getImgData(position);
		final ImageView imgDetailEdit = (ImageView) view.findViewById(R.id.imgDetailEdit);
		mapImgDetail.put(position, imgDetailEdit);
		final ProgressBar progressImg = (ProgressBar) view.findViewById(R.id.progressImg);

		AsyncTask<Void, Void, Bitmap> task = ATask.executeVoid(new ATask.OnTaskBitmap() {
			
			Bitmap resBitmap = null;
			
			@Override
			public void onPre() {
				progressImg.setVisibility(View.VISIBLE);
			}

			@Override
			public Bitmap onBG() {
				
				String diskCachePath = Const_VALUE.PATH_IMAGESELECT_DETAIL(detailAct) + imgData.F_IMG_NAME;

				if (new File(diskCachePath).exists())// disk cache 파일
					resBitmap = ImageDirectLoader.getLocalPathBitmap(detailAct, diskCachePath, srcImgThumbSize);
				else {
					if (imgData.PATH.startsWith("http://") || imgData.PATH.startsWith("https://"))// url 파일
					{
						resBitmap = ImageDirectLoader.getUrlBitmap(imgData.PATH, srcImgThumbSize, diskCachePath);
					}

					else// local 파일
					{
						resBitmap = ImageDirectLoader.getLocalPathBitmap(detailAct, imgData.PATH, srcImgThumbSize, diskCachePath);

					}

				}
				return resBitmap;
			}

			@Override
			public void onPost(Bitmap resBitmap) {
				progressImg.setVisibility(View.GONE);
				if (resBitmap != null) {
					if (imgData.ROTATE_ANGLE > 0) {
						Bitmap rotateBitmap = rotate(position, resBitmap, imgData.ROTATE_ANGLE);
						showImage(position, imgDetailEdit, rotateBitmap);
					} else {
						showImage(position, imgDetailEdit, resBitmap);
					}

				}
			}
		});

		AsyncTask<Void, Void, Bitmap> oldTask = mapTask.remove(position);
		if (oldTask != null)
			oldTask.cancel(true);
		mapTask.put(position, task);

		((ViewPager) pager).addView(view, 0); // 뷰 페이저에 추가
		return view;
	}

	void showImage(int position, ImageView imgDetailEdit, Bitmap resBitmap) {
		mapImgBitmap.put(position, resBitmap);

		// SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(resBitmap);
		WeakReference<Bitmap> softBitmap = new WeakReference<Bitmap>(resBitmap);
		imgDetailEdit.setAnimation(AnimationUtils.loadAnimation(detailAct, android.R.anim.fade_in));
		imgDetailEdit.setImageBitmap(null);
		imgDetailEdit.setImageBitmap(softBitmap.get());
	}

	public void crop(int position) {
		Bitmap cropBitmap = crop(position, mapImgBitmap.get(position), mapImgBitmap.get(position).getWidth(), mapImgBitmap.get(position).getHeight());
		// Bitmap cropBitmap = crop(position, mapImgBitmap.get(position), 500 , 500);

		showImage(position, mapImgDetail.get(position), cropBitmap);
	}

	Bitmap crop(int position, Bitmap cropBitmap, int w, int h) {
		if (cropBitmap == null)
			return null;

		int width = cropBitmap.getWidth();
		int height = cropBitmap.getHeight();

		if (width < w && height < h)
			return cropBitmap;

		int x = 0;
		int y = 0;

		if (width > w)
			x = (width - w) / 2;

		if (height > h)
			y = (height - h) / 2;

		int cw = w; // crop width
		int ch = h; // crop height

		if (w > width)
			cw = width;

		if (h > height)
			ch = height;

		Bitmap converted = null;
		try {
			converted = Bitmap.createBitmap(cropBitmap, x, y, cw, ch);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}

		return converted;

	}

	public void rotate(int position) {
		Bitmap rotateBitmap = rotate(position, mapImgBitmap.get(position), -1);
		showImage(position, mapImgDetail.get(position), rotateBitmap);
	}

	Bitmap rotate(int position, Bitmap curBitmap, int setRotate) {
		if (curBitmap == null)
			return null;

		MyPhotoSelectImageData imgData = detailAct.getImgData(position);

		Matrix matrix = new Matrix();

		if (setRotate == -1) {// + 90도 더 돌아감.
			matrix.setRotate(ROTATE_ANGLE);

			imgData.ROTATE_ANGLE += ROTATE_ANGLE;

			// setRotate 값의 -1 thumb rotation 값이 360에 도달 하지 못하여 Rotation max 값으로 350으로 지
			if (imgData.ROTATE_ANGLE >= 350)
				imgData.ROTATE_ANGLE = 0;
			imgData.ROTATE_ANGLE_THUMB += ROTATE_ANGLE;

			if (imgData.ROTATE_ANGLE_THUMB >= 350)
				imgData.ROTATE_ANGLE_THUMB = 0;
		} else {
			matrix.setRotate(setRotate);// 첫 이미지 로딩 시 회전된 이미지라면 회전함.
			Dlog.d("rotate() setRotate:" + setRotate);
		}

		Bitmap converted = null;
		try {
			converted = Bitmap.createBitmap(curBitmap, 0, 0, curBitmap.getWidth(), curBitmap.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
		return converted;
	}

	@Override
	public int getCount() {
		return detailAct.selectImgKeyList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public void destroyItem(View pager, int position, Object view) {// 뷰 객체 삭제.
		((ViewPager) pager).removeView((View) view);
		ImageView iv = (ImageView) pager.findViewById(R.id.imgDetailEdit);

		ImageUtil.recycleBitmap(iv);

		ViewUnbindHelper.unbindReferences((View) view);
		view = null;

		AsyncTask<Void, Void, Bitmap> oldTask = mapTask.remove(position);
		if (oldTask != null)
			oldTask.cancel(true);
	}
}
