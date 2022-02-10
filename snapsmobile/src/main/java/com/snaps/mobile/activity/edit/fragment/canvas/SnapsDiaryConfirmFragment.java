package com.snaps.mobile.activity.edit.fragment.canvas;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.interfacies.SnapsDiaryEditActToFragmentBridgeActivity;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryConfirmBaseActivity;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryCanvasDimensChangeListener;
import com.snaps.mobile.activity.edit.spc.SnapsDiaryConfirmCanvas;
import com.snaps.mobile.activity.edit.thumbnail_skin.SnapsThumbNailUtil;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.order.order_v2.exceptions.SnapsIOException;

import java.io.File;

import errorhandle.logger.Logg;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;

public class SnapsDiaryConfirmFragment extends SimplePhotoBookCanvasFragment implements ISnapsDiaryCanvasDimensChangeListener {
	private static final String TAG = SnapsDiaryConfirmFragment.class.getSimpleName();

	private FrameLayout parentView = null;

	public void setParentView(FrameLayout parentView) {
		this.parentView = parentView;
	}

	@Override
	public void onCanvasDimensChanged(int fixHeight) {
		if (parentView == null) return;

		ViewGroup.LayoutParams layoutParams = parentView.getLayoutParams();
		layoutParams.height = fixHeight;
		parentView.setLayoutParams(layoutParams);
		parentView.invalidate();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pagecanvas2, container, false);

		setOnViewpagerListener((SnapsDiaryConfirmBaseActivity)getActivity());

		canvas = new SnapsDiaryConfirmCanvas(getActivity());
		((SnapsDiaryConfirmCanvas)canvas).setOnCanvasDimensChangedListener(this);

		if (canvas != null) {
			canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			canvas.setGravity(Gravity.CENTER);
			canvas.setId(R.id.fragment_root_view_id);

			((SnapsDiaryConfirmCanvas) canvas).setDiaryTextControlListener((((SnapsDiaryEditActToFragmentBridgeActivity)getEditActBridge()).getDiaryTextControlListener()));

			rootView.addView(canvas);

			boolean isVisibleButton = getArguments().getBoolean("visibleButton", true);
			canvas.setEnableButton(isVisibleButton);

			boolean isPageSaving = getArguments().getBoolean("pageSave", false);
			canvas.setIsPageSaving(isPageSaving);

			makeSnapsCanvas();
			if(viewPager != null) {
				canvas.setViewPager(viewPager);
				viewPager.addCanvas(canvas);
			}
			canvas.setLandscapeMode(isLandscapeMode);
		}

		canvas.setZoomable(false);
		canvas.setScaleX(canvas.getFitScaleX());
		canvas.setScaleY(canvas.getFitScaleY());

		return rootView;
	}

	/**
	 * 이미지들만 리로드
	 */
	public void reLoadImageView() {
		if (canvas != null)
			canvas.changeLayoutLayer();
	}

	public void makeSnapsCanvas(boolean isMakeThumbnail) {
		try {
			if (canvas == null) {
				canvas = new SnapsDiaryConfirmCanvas(getActivity());
				canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				canvas.setZoomable(false);
				canvas.setScaleX(canvas.getFitScaleX());
				canvas.setScaleY(canvas.getFitScaleY());
			}

			canvas.setId(R.id.fragment_root_view_id);

			((SnapsDiaryConfirmCanvas) canvas).setDiaryTextControlListener((((SnapsDiaryEditActToFragmentBridgeActivity)getEditActBridge()).getDiaryTextControlListener()));
			canvas.setScaledThumbnailMakeMode(isMakeThumbnail);

			int index = getArguments().getInt("index");
			pageLoad = getArguments().getBoolean("pageLoad");

			if (pageLoad) {
                handleIncreaseCanvasLoadCompleteCount();
			}

			if(getPageList() != null && getPageList().size() > index) {
				SnapsPage spcPage = getPageList().get(index);
				canvas.setCallBack(this);
				imageRange(spcPage, index);
				canvas.setSnapsPage(spcPage, index);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	public void makeSnapsCanvas() {
		makeSnapsCanvas(false);
	}

	@Override
	protected void saveLoadImageTask(final int page) {
		String [] productData = SnapsThumbNailUtil.getThumbNailData(Config.getPROD_CODE(),canvas);
		if(productData == null) {
			loadThumbNail(true,page, null, 1);
		} else {
			String product = productData[0];
			final float scale = Float.parseFloat(productData[1]);
			SnapsThumbNailUtil.downSkinImage(getContext(), product, new SnapsThumbNailUtil.SnapsSkinLoadListener() {
				@Override
				public void onSkinLoaded(Bitmap bitmap) {
					loadThumbNail(false,page, bitmap, scale);
				}
			});
		}


	}
	private void loadThumbNail(final boolean preThumbNail, final int page, final Bitmap skinBitmap, final float scale) {
		ATask.executeVoid(new ATask.OnTaskBitmap() {

			@Override
			public void onPre() {}

			@Override
			public Bitmap onBG() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Dlog.e(TAG, e);
				}
				return null;
			}

			@Override
			public void onPost(Bitmap bitmap) {
				// 앱이 pause되면 작업을 중지하고, resume 시 재작업하도록 상태표시함.
				if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
					SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_IMGSAVE);
					return;
				}

				boolean isResult = false;
				String filePath = "";
				try {
					Config.checkThumbnailFileDir();
					File file = Config.getTHUMB_PATH("thumb.jpg");
					if (file == null) throw new SnapsIOException("failed make thumbnail dir");
					filePath = file.getAbsolutePath();
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
				Bitmap pageBitmap = null;
				if(preThumbNail) {
					pageBitmap = getViewBitmap(page, false);
				} else {
					pageBitmap = getViewBitmapThumbNail(page, scale,0,0);
					if(skinBitmap != null) {
						Bitmap tempBitmap = CropUtil.getInSampledBitmapCopy(pageBitmap, DEFAULT_BITMAP_CONFIG,0);
						Canvas margeCanvas = new Canvas(tempBitmap);
						margeCanvas.drawBitmap(skinBitmap, new Matrix(), null);
						pageBitmap = tempBitmap;
						//BitmapUtil.bitmapRecycle(tempBitmap);
					}
				}
				if(pageBitmap != null && !pageBitmap.isRecycled())
					isResult = saveLocalThumbnail(getActivity(), pageBitmap);

				if (isResult) {
					// 페이지에 썸네일을 넣는다.
					canvas.getSnapsPage().thumbnailPath = filePath;
					setPageThumbnail(page, filePath);
				} else {
					setPageThumbnailFail(page);
				}

				Dlog.d("loadThumbNail() 이미지 저장 결과:" + isResult + ", page:" + page);

				setPageFileOutput(page + 1);
			}
		});
	}
}
