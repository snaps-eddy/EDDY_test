package com.snaps.common.spc.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.interfaces.ISnapsControl;

public class ImageLoadView extends androidx.appcompat.widget.AppCompatImageView implements ISnapsControl {
	SnapsControl snapsControl = null;
	public ImageLoadView(Context context, SnapsLayoutControl layout) {
		super(context);
		ViewGroup.MarginLayoutParams params = new FrameLayout.LayoutParams(new LayoutParams((int) Float.parseFloat(layout.width)+2, (int) Float.parseFloat(layout.height)+2));

		//땜방!!!!!!
		//Ben
		//+2는 도대체 왜 하는지.. 알수가 없네
		if (layout._controlType == SnapsControl.CONTROLTYPE_STICKER) {
			//대재앙이 발생할 우려가 있어서 일단 이야기 나온 폰케이스만
			if (Const_PRODUCT.isLegacyPhoneCaseProduct() || Const_PRODUCT.isUvPhoneCaseProduct() || Const_PRODUCT.isPrintPhoneCaseProduct()) {
				params = new FrameLayout.LayoutParams(new LayoutParams((int) Float.parseFloat(layout.width), (int) Float.parseFloat(layout.height)));
			}
		}

		params.leftMargin = layout.getX();
		params.topMargin = (int) Float.parseFloat(layout.y);
		this.setLayoutParams(new FrameLayout.LayoutParams(params));

		if ((layout.regName.equals("background") || layout.regName.equals("line")) && layout.bgColor != null && !"".equals(layout.bgColor))
			setBackgroundColor(Color.parseColor("#" + layout.bgColor));
	}

	public ImageLoadView(Context context, AttributeSet attribute) {
		super(context, attribute);
	}

	@Override
	public SnapsControl getSnapsControl() {
		return snapsControl;
	}

	@Override
	public void setSnapsControl(SnapsControl layoutControl) {
		this.snapsControl = layoutControl;
	}

	@Override
	public View getView() {
		return this;
	}
}
