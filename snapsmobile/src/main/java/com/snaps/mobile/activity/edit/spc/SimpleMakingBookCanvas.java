package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.snaps.common.structure.SnapsTemplateInfo.COVER_TYPE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;

public class SimpleMakingBookCanvas extends ThemeBookCanvas {
	private static final String TAG = SimpleMakingBookCanvas.class.getSimpleName();
	protected int pageCover_w = 0;
	protected int pageCover_h = 0;

	public SimpleMakingBookCanvas(Context context) {
		super(context);
	}

	public SimpleMakingBookCanvas(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	protected void loadPageLayer() {
		try {
			// TODO Auto-generated method stub
			if (getSnapsPage().type.equalsIgnoreCase("page") || getSnapsPage().type.equalsIgnoreCase("title")) {
				if (!getSnapsPage().info.F_PAPER_CODE.equals("160008"))
					pageLayer.setBackgroundResource(R.drawable.skin_a4);// 내지
				else
					pageLayer.setBackgroundResource(R.drawable.skin_a4_rayflat);// 내지
			} else if (getSnapsPage().type.equalsIgnoreCase("cover")) {
				if (getSnapsPage().info.getCoverType() == COVER_TYPE.HARD_COVER)
					;// 커버
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}
	
	@Override
	public  void onDestroyCanvas() {
		if(pageLayer != null) {
			Drawable d = pageLayer.getBackground();
			if (d != null) {
				try {
					d.setCallback(null);
				} catch (Exception ignore) {
				}
			}
		}
		super.onDestroyCanvas();
	}
}
