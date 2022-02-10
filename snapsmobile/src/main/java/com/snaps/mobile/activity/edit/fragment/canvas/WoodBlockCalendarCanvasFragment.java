package com.snaps.mobile.activity.edit.fragment.canvas;

import android.view.ViewGroup.LayoutParams;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.spc.WoodBlockCalendarCanvas;

import errorhandle.logger.Logg;

public class WoodBlockCalendarCanvasFragment extends ThemeBookCanvasFragment
{
	private static final String TAG = WoodBlockCalendarCanvasFragment.class.getSimpleName();
	float scale_X = 1.f;
	float scale_Y = 1.f;
	boolean pageEnd = false;


	public float getScale_X() {
		return scale_X;
	}

	public void setScale_X(float scale_X) {
		this.scale_X = scale_X;
	}

	public float getScale_Y() {
		return scale_Y;
	}

	public void setScale_Y(float scale_Y) {
		this.scale_Y = scale_Y;
	}

	public WoodBlockCalendarCanvasFragment(){
	}

	@Override
	public void makeSnapsCanvas() {
		int index = getArguments().getInt("index");
		try {
			if (canvas == null) {
				canvas = new WoodBlockCalendarCanvas(getActivity().getApplicationContext());
				canvas.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				if(isPreview) {
					canvas.setZoomable(false);
					canvas.setIsPreview(true);
				}
			}

			pageLoad = getArguments().getBoolean("pageLoad");

			boolean isPageSaving = getArguments().getBoolean("pageSave", false);
			canvas.setIsPageSaving(isPageSaving);

			if (pageLoad) {
				handleIncreaseCanvasLoadCompleteCount();
			}

			SnapsPage spcPage = getPageList().get(index);

			canvas.setCallBack(this);

			imageRange(spcPage, index);

			int size = getPageList().size();
			{
				canvas.setSnapsPage(spcPage, index, true, null);
				if(index == (size-1))
				{
					pageEnd = true;
					Config.setComplete(true,getActivity().getApplicationContext());
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, "이미지 저장 실패" + " : " + index, e);
			setPageThumbnailFail(index);
		}
	}

	@Override
	protected void imageRange(SnapsPage page, int index) {
		try {

			SnapsLayoutControl layout;
			
			for (int i = 0; i < page.getLayoutList().size(); i++) {
				layout = (SnapsLayoutControl) page.getLayoutList().get(i);
				MyPhotoSelectImageData imgData = layout.imgData;
				if (imgData != null)
					layout.angle = imgData.ROTATE_ANGLE + "";
			}

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
}
